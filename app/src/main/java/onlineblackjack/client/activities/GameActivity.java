package onlineblackjack.client.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import onlineblackjack.client.Client;
import onlineblackjack.client.game.GameManager;
import onlineblackjack.client.game.Player;
import onlineblackjack.client.R;

public class GameActivity extends AppCompatActivity {

    private TextView currentMoneyTxt;
    private TextView currentBetTxt;
    private TextView playerLogTxt;
    private TextView betInputField;

    private TextView dealerCardsTxt;
    private TextView dealerScoreTxt;

    private TextView playerCardsTxt; // This is for the person playing the game on their screen
    private TextView playerScoreTxt;

    // Other player info
    private TextView player2CardsTxt;
    private TextView player2ScoreTxt;
    private TextView player2MoneyInfoTxt;

    private TextView player3CardsTxt;
    private TextView player3ScoreTxt;
    private TextView player3MoneyInfoTxt;

    private TextView player4CardsTxt;
    private TextView player4ScoreTxt;
    private TextView player4MoneyInfoTxt;

    // Buttons
    private Button leaveBtn;
    private Button hitBtn;
    private Button standBtn;
    private Button submitBetBtn;

    // Other
    private TextView timerTxt;
    private int secondsElapsed = 0;
    private int totalResponseTime = 0;
    Timer timer;
    TimerTask timerTask;
    private String timerMsg;

    static boolean isTimerOn = false;

    private final HashMap<Integer,Player> otherPlayers = new HashMap<Integer,Player>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ActivityManager.gameActivity = this;
        setUpUI();
    }

    protected void onStart() {
        super.onStart();
        ActivityManager.setCurrentActivity(this);
        ActivityManager.setInGame(true);
    }

    public void setUpUI() {
        // Current player UI
        playerLogTxt = findViewById(R.id.player_log_txt);
        currentBetTxt = findViewById(R.id.current_bet_txt);
        currentMoneyTxt = findViewById(R.id.total_money_txt);
        playerCardsTxt = findViewById(R.id.player_cards__txt);
        playerScoreTxt = findViewById(R.id.total_score_txt);
        betInputField = findViewById(R.id.bet_input_field);
        submitBetBtn = findViewById(R.id.submit_bet_btn);
        hitBtn = findViewById(R.id.hit_btn);
        standBtn = findViewById(R.id.stand_btn);
        leaveBtn = findViewById(R.id.leave_game_btn);

        // Dealer UI
        dealerCardsTxt = findViewById(R.id.dealer_cards_txt);
        dealerScoreTxt = findViewById(R.id.dealer_total_score_txt);

        // Other player info
        player2CardsTxt = findViewById(R.id.player2_cards_txt);
        player2ScoreTxt = findViewById(R.id.player2_total_score_txt);
        player2MoneyInfoTxt = findViewById(R.id.player2_money_info_txt);

        player3CardsTxt = findViewById(R.id.player3_cards_txt);
        player3ScoreTxt = findViewById(R.id.player3_total_score_txt);
        player3MoneyInfoTxt = findViewById(R.id.player3_money_info_txt);

        player4CardsTxt = findViewById(R.id.player4_cards_txt);
        player4ScoreTxt = findViewById(R.id.player4_total_score_txt);
        player4MoneyInfoTxt = findViewById(R.id.player4_money_info_txt);

        timerTxt = findViewById(R.id.timer_txt);

        //checkUIType();
        startTimer(0);
        updatePlayerInfo();
        setUpButtonListeners();
    }

    private void setUpButtonListeners() {
        leaveBtn.setOnClickListener(v -> {
            timer.cancel();
            timer.purge();
            timerTask.cancel();
            playerCardsTxt.setText("Your cards:");
            playerScoreTxt.setText("Total Score: 0");
            otherPlayers.clear();
            GameManager.setDealer(null);
            Client.leaveGame();
            ActivityManager.goToLobby();
        });

        hitBtn.setOnClickListener(v -> {
            if (!GameManager.getPlayer().isBust()) {
                setLogText("You chose to get hit!");
                Client.hit();
            }
        });

        // When you stand, you basically end your turn.
        standBtn.setOnClickListener(v -> {
            if (!GameManager.getPlayer().isBust()) {
                setLogText("You chose to stand, please wait until the round finishes");
                Client.stand();
                showGameOptionsUI(false);
            }
        });

        submitBetBtn.setOnClickListener(v -> {
            int betAmt;
            try {
                betAmt = Integer.parseInt(String.valueOf(betInputField.getText())); // Since the field is numbers only, this is safe to do
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }

            // Check bet amount
            if (betAmt <= GameManager.getPlayer().getMoney() && betAmt > 0) {
                Client.submitBet(betAmt);
                showBetUI(false);
            } else {
                setLogText("Invalid bet");
            }
        });
    }

    private void startTimer(int totalTime) {
        totalResponseTime = totalTime;
        timerTxt.setVisibility(View.VISIBLE);
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                setTimerVisibility(isTimerOn);

                if (isTimerOn) {
                    int timeLeft = totalResponseTime - secondsElapsed;
                    updateTimerTxt(timeLeft);
                    secondsElapsed++;

                    if (timeLeft == 0) {
                        isTimerOn = false;
                    }
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    // BUG: Slight 1s delay because of the fixed rate from the timer
    public void startTimer() {
        isTimerOn = true;
    }

    public void setTotalTime(int totalTime) {
        totalResponseTime = totalTime;
        secondsElapsed = 0;
    }

    private void setTimerVisibility(boolean show) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    timerTxt.setVisibility(View.VISIBLE);
                } else {
                    timerTxt.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void updateTimerTxt(int time) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                timerTxt.setText(time + getTimerMsg());
            }
        });
    }

    public void updateOtherPlayerUI() throws JSONException {
        TextView[] otherPlayerTxts = {player2CardsTxt, player2MoneyInfoTxt, player2ScoreTxt, player3CardsTxt, player3MoneyInfoTxt, player3ScoreTxt, player4CardsTxt, player4MoneyInfoTxt, player4ScoreTxt};

        int j = 0;
        for (Map.Entry<Integer, Player> entry : otherPlayers.entrySet()) {
            String playerCards = "";
            for (int x = 0; x < entry.getValue().getCurrentHand().size(); x++) {
                playerCards += entry.getValue().getCurrentHand().get(x).getCardId() + ", ";
            }

            if (entry.getValue().isSpectateMode()) {
                otherPlayerTxts[j++].setText(String.format("%s is spectating", entry.getValue().getUsername()));
                otherPlayerTxts[j++].setText("");
                otherPlayerTxts[j++].setText("");
            } else {
                otherPlayerTxts[j++].setText(String.format("%s cards: %s", entry.getValue().getUsername(), playerCards));
                otherPlayerTxts[j++].setText(String.format("Money: $%d, Current Bet: $%d", entry.getValue().getMoney(), entry.getValue().getCurrentBet()));
                otherPlayerTxts[j++].setText(String.format("Total Score: %d", entry.getValue().getTotalScore()));
            }
        }

        setVisibilityForOtherPlayerUi(otherPlayerTxts, otherPlayers.size());
    }

    private void setVisibilityForOtherPlayerUi(TextView[] otherPlayerUi, int amt) {
        int j = 0;
        for (int i = 0; i < otherPlayerUi.length / 3; i++) {
            if (i < amt) {
                otherPlayerUi[j++].setVisibility(View.VISIBLE);
                otherPlayerUi[j++].setVisibility(View.VISIBLE);
                otherPlayerUi[j++].setVisibility(View.VISIBLE);
            } else {
                otherPlayerUi[j++].setVisibility(View.INVISIBLE);
                otherPlayerUi[j++].setVisibility(View.INVISIBLE);
                otherPlayerUi[j++].setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setLogText(String msg) {
        playerLogTxt = findViewById(R.id.player_log_txt);
        playerLogTxt.setText(msg);
    }

    public void showBetUI(boolean show) {
        if (show) {
            submitBetBtn.setVisibility(View.VISIBLE);
            betInputField.setVisibility(View.VISIBLE);
        } else {
            submitBetBtn.setVisibility(View.INVISIBLE);
            betInputField.setVisibility(View.INVISIBLE);
        }
    }

    public void updateDealerInfo() {
        dealerCardsTxt.setVisibility(View.VISIBLE);
        dealerScoreTxt.setVisibility(View.VISIBLE);

        dealerScoreTxt.setText(String.format("Dealer Score: %d", GameManager.getDealer().getTotalScore()));

        String dealerCards = "Dealer Cards: ";
        for (int i = 0; i < GameManager.getDealer().getCurrentHand().size(); i++) {
            // Don't add the hidden cards to UI
            if (!GameManager.getDealer().getCurrentHand().get(i).isHidden()) { // INDEX OUT OF EXCEPTION HERE?
                dealerCards += GameManager.getDealer().getCurrentHand().get(i).getCardId() + ", ";
            } else {
                dealerCards += "Hidden Card, ";
            }
        }
        dealerCardsTxt.setText(dealerCards);
    }

    public void removePlayerFromMap(int id) {
        otherPlayers.remove(id);
    }

    public void addToOtherPlayers(Player player) {
        otherPlayers.put(player.getPlayerId(), player);
    }

    public void updatePlayerInfo() {
        currentMoneyTxt.setText(String.format("Money: $%d", GameManager.getPlayer().getMoney()));
        currentBetTxt.setText(String.format("Current Bet: $%d", GameManager.getPlayer().getCurrentBet()));

        playerScoreTxt.setVisibility(View.VISIBLE);
        playerCardsTxt.setVisibility(View.VISIBLE);
        playerScoreTxt.setText(String.format("Total Score: %d", GameManager.getPlayer().getTotalScore()));

        String playerCards = "Your Cards: ";
        for (int i = 0; i < GameManager.getPlayer().getCurrentHand().size(); i++) {
            playerCards += GameManager.getPlayer().getCurrentHand().get(i).getCardId() + ", ";
        }
        playerCardsTxt.setText(playerCards);
    }

    public void showGameOptionsUI(boolean show) {
        if (show) {
            hitBtn.setVisibility(View.VISIBLE);
            standBtn.setVisibility(View.VISIBLE);
        } else {
            hitBtn.setVisibility(View.INVISIBLE);
            standBtn.setVisibility(View.INVISIBLE);
        }
    }
    public String getTimerMsg() {
        return timerMsg;
    }

    public void setTimerMsg(String timerMsg) {
        this.timerMsg = timerMsg;
    }
}
