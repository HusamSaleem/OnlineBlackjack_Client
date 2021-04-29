package onlineblackjack.client;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Client {

    public static void ping() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Packet", Packet.PING);

            new Thread(new SendData(jsonObject.toString())).start();
        } catch (JSONException e) {
            Log.e("Error", "Error when pinging server");
        }
    }

    public static void hit() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Packet", Packet.HIT);

            new Thread(new SendData(jsonObject.toString())).start();
        } catch (JSONException e) {
            Log.e("Error", "Error when sending a hit request");
        }
    }

    public static void stand() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Packet", Packet.STAND);

            new Thread(new SendData(jsonObject.toString())).start();
        } catch (JSONException e) {
            Log.e("Error", "Error when sending a stand request");
        }
    }

    public static void endTurn() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Packet", Packet.END_TURN);

            new Thread(new SendData(jsonObject.toString())).start();
        } catch (JSONException e) {
            Log.e("Error", "Error when ending turn");
        }
    }

    public static void submitBet(int betAmt) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Packet", Packet.PLACE_BET);
            jsonObject.put("betAmt", betAmt);

            new Thread(new SendData(jsonObject.toString())).start();
        } catch (JSONException e) {
            Log.e("Error", "Error when submitting a bet");
        }
    }

    public static void leaveGame() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Packet", Packet.LEAVE_SESSION);

            new Thread(new SendData(jsonObject.toString())).start();
        } catch (JSONException e) {
            Log.e("Error", "Error when leaving the game");
        }
    }

    public static void submitUsernameRequest(String username) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Packet", Packet.REGISTER_USERNAME);
            jsonObject.put("username", username);

            new Thread(new SendData(jsonObject.toString())).start();
        } catch (JSONException e) {
            Log.e("Error", "Error when submitting username packet");
        }
    }

    public static void requestLobbyInfo() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Packet", Packet.REQUEST_LOBBY_INFO);
            new Thread(new SendData(jsonObject.toString())).start();
        } catch (JSONException e) {
            Log.e("Error", "Error when requesting lobby info");
        }
    }

    public static void joinMatchmaking(Packet queueType) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Packet", queueType);

            new Thread(new SendData(jsonObject.toString())).start();
        } catch (JSONException e) {
            Log.e("Error", "Error when trying to join Matchmaking");
        }
    }

    public static void stopMatchmaking() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Packet", Packet.LEAVE_MATCHMAKING);

            new Thread(new SendData(jsonObject.toString())).start();
        } catch (JSONException e) {
            Log.e("Error", "Error when trying to join Matchmaking");
        }
    }

    public static void requestLastPacketSent() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Packet", Packet.RESEND_LAST_PACKET);

            new Thread(new SendData(jsonObject.toString())).start();
        } catch (JSONException e) {
            Log.e("Error", "Error when trying to join Matchmaking");
        }
    }
}
