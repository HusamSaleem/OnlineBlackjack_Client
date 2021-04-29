package onlineblackjack.client;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import onlineblackjack.client.activities.ActivityManager;
import onlineblackjack.client.game.Dealer;
import onlineblackjack.client.game.GameManager;
import onlineblackjack.client.game.Player;

public class ReceiveData implements Runnable {
    @Override
    public void run() {
        while (true) {
            String msg = "";
            try {
                byte[] buffer = new byte[8192];
                InputStream stream = ServerConnectionManager.getClientSocket().getInputStream();
                stream.read(buffer);
                msg = new String(buffer);
                String[] allData = msg.split("~");

                for (int i = 0; i < allData.length - 1; i++) {
                    if (allData[i].length() > 0) {
                        processData(allData[i]);
                        Log.i("Data", allData[i]);
                    }
                }
            } catch (IOException | StringIndexOutOfBoundsException | JSONException e) {
                e.printStackTrace();
                Client.requestLastPacketSent();
                Log.e("ERROR", msg);
            }
        }
    }

    private void processData(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);

        int packetType = (int) jsonObject.get("Packet");

        if (packetType == Packet.PING.ordinal()) {
            Client.ping();
            setServerStatusOnline();
        } else if (packetType == Packet.USERNAME_REGISTERED.ordinal()) {
            ActivityManager.goToLobby();
        } else if (packetType == Packet.VERSION_INFO.ordinal()) {
            String versionId = String.valueOf(jsonObject.get("version"));
            updateVersionInfo(versionId);
        } else if (packetType == Packet.PLAYER_INFO.ordinal()) {
            if (GameManager.getPlayer() == null) {
                GameManager.setPlayer(new Player());
            }
            GameManager.updatePlayer(jsonObject);

            if (GameManager.getPlayer().isSpectateMode() && ActivityManager.isInGame()) {
                showBetUI(false);
                showGameOptions(false);
            }

            if (!ActivityManager.isInGame()) {
                updatePlayerName(); // Update various lobby things
            } else {
                updateGameInfo();
            }

        } else if (packetType == Packet.LOBBY_INFO.ordinal()) {
            if (ActivityManager.isInLobby()) {
                updateLobbyInfo(jsonObject.getInt("playersConnected"));
            }
        } else if (packetType == Packet.SESSION_STARTED_SOLO.ordinal() || packetType == Packet.SESSION_STARTED_TWO.ordinal() || packetType == Packet.SESSION_STARTED_THREE.ordinal() || packetType == Packet.SESSION_STARTED_FOUR.ordinal()) {
            ActivityManager.goToGame();
        } else if (packetType == Packet.REQUEST_BET.ordinal()) {
            if (!GameManager.getPlayer().isSpectateMode()) {
                showBetUI(true);
                setGameLog("Place your bet!");
            } else {
                setGameLog("People are placing their bets!");
            }
            startGameTimer(jsonObject.getInt("timeLeft"), "s left to respond");
        } else if (packetType == Packet.VALID_BET.ordinal()) {
            setGameLog("Your bet has been placed!");
            showBetUI(false);
        } else if (packetType == Packet.INVALID_BET.ordinal()) {
            setGameLog("Your bet has not been placed. (Invalid bet)");
        } else if (packetType == Packet.REMOVE_FROM_GAME_SESSION.ordinal()) {
            setGameLog("You have been removed from the game session (Ran out of money or didn't place a bet). You can continue to spectate");
        } else if (packetType == Packet.DEALER_INFO.ordinal()) {
            if (GameManager.getDealer() == null) {
                GameManager.setDealer(new Dealer());
            }
            updateDealerInfo(jsonObject);
        } else if (packetType == Packet.REQUEST_RESPONSE.ordinal()) {
            if (!GameManager.getPlayer().isSpectateMode()) {
                setGameLog("You can now choose to Hit (Multiple times), or stand to end your turn");
                showGameOptions(true);
            }
            startGameTimer(jsonObject.getInt("timeLeft"), "s left to respond");
        } else if (packetType == Packet.FINISH_TAKING_RESPONSES.ordinal()) {
            setGameLog("Time for the dealer to make his move!");
            showGameOptions(false);
        } else if (packetType == Packet.NO_MORE_RESPONSES.ordinal()) {
            setGameLog("You can no longer hit or stand at this time");
            showGameOptions(false);
        } else if (packetType == Packet.BEAT_THE_DEALER.ordinal()) {
            setGameLog("You beat the dealer! Money has been given to you now");
        } else if (packetType == Packet.LOST_AGAINST_DEALER.ordinal()) {
            setGameLog("The dealer won. You have lost your bet");
        } else if (packetType == Packet.TIE_WITH_DEALER.ordinal()) {
            setGameLog("You tied with the dealer. You get your money back");
        }  else if (packetType == Packet.BLACKJACK.ordinal()) {
            setGameLog("You have a BlackJack! No need to hit");
            showGameOptions(false);
        } else if (packetType == Packet.BUST.ordinal()) {
            setGameLog("BUST! Wait until next round to play again");
            showGameOptions(false);
        } else if (packetType == Packet.DELAY_TIME_BEFORE_GAME.ordinal()) {
            setGameLog("Please wait until the game starts");
            startGameTimer(jsonObject.getInt("timeLeft"), "s left before game starts");
        } else if (packetType == Packet.DELAY_TIME_AFTER_GAME.ordinal()) {
            startGameTimer(jsonObject.getInt("timeLeft"), "s before game starts again");
        } else if (packetType == Packet.DELAY_TIME_FOR_DEALER.ordinal()) {
            startGameTimer(jsonObject.getInt("timeLeft"), "s before dealer makes their move");
        } else if (packetType == Packet.OTHER_PLAYER_INFO.ordinal()) {
            if (ActivityManager.isInGame()) {
                updateOtherPlayerUI(jsonObject);
            }
        } else if (packetType == Packet.REMOVE_PLAYER_FROM_CLIENT.ordinal()) {
            ActivityManager.gameActivity.removePlayerFromMap(jsonObject.getInt("playerId"));
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                try {
                    ActivityManager.gameActivity.updateOtherPlayerUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void setServerStatusOnline() {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(() -> {
            ActivityManager.mainActivity.setServerStatusOnline();
        });
    }

    private void updateOtherPlayerUI(JSONObject jsonObject) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(() -> {
            try {
                Player player = new Player();
                player.updateFromJson(jsonObject);
                ActivityManager.gameActivity.addToOtherPlayers(player);
                ActivityManager.gameActivity.updateOtherPlayerUI();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void startGameTimer(int time, String msg) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(() -> {
            ActivityManager.gameActivity.setTimerMsg(msg);
            ActivityManager.gameActivity.setTotalTime(time - 1); // Temp fix for delay
            ActivityManager.gameActivity.startTimer();
        });
    }

    // Shows or unshows the hit, stand or end turn buttons
    private void showGameOptions(boolean show) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(() -> ActivityManager.gameActivity.showGameOptionsUI(show));
    }

    private void setGameLog(String msg) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(() -> ActivityManager.gameActivity.setLogText(msg));
    }

    private void updateDealerInfo(JSONObject jsonObject) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(() -> {
            try {
                GameManager.getDealer().updateFromJson(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ActivityManager.gameActivity.updateDealerInfo();
        });
    }

    private void showBetUI(boolean show) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(() -> ActivityManager.gameActivity.showBetUI(show));
    }

    private void updateGameInfo() {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(() -> ActivityManager.gameActivity.updatePlayerInfo());
    }

    private void updatePlayerName() {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(() -> ActivityManager.lobbyActivity.setPlayerName());
    }

    private void updateLobbyInfo(int playersConnected) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(() -> ActivityManager.lobbyActivity.setPlayersOnlineTxt(playersConnected));
    }

    private void updateVersionInfo(String versionId) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(() -> ActivityManager.mainActivity.setVersionId(versionId));
    }
}
