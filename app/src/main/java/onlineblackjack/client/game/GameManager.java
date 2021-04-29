package onlineblackjack.client.game;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class GameManager {
    private static Player player;
    private static Dealer dealer;

    public static Player getPlayer() {
        return player;
    }

    public static void setPlayer(Player newPlayer) {
        GameManager.player = newPlayer;
    }

    public static void updatePlayer(JSONObject json) {
        try {
            player.updateFromJson(json);
        } catch (JSONException e) {
            Log.e("Error", "Error when updating player with new json data");
        }
    }

    public static Dealer getDealer() {
        return dealer;
    }

    public static void setDealer(Dealer dealer) {
        GameManager.dealer = dealer;
    }
}
