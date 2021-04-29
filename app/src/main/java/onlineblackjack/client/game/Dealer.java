package onlineblackjack.client.game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Dealer {
    private ArrayList<Card> currentHand;
    private boolean isBust;
    private int totalScore;

    public void updateFromJson(JSONObject json) throws JSONException {
        isBust = json.getBoolean("isBust");
        totalScore = json.getInt("totalScore");

        JSONArray jsonArray = json.getJSONArray("currentHand");
        currentHand = new ArrayList<Card>();

        for (int i = 0; i < jsonArray.length(); i++) {
            currentHand.add(new Card(jsonArray.getJSONObject(i)));
        }
    }

    public ArrayList<Card> getCurrentHand() {
        return currentHand;
    }

    public void setCurrentHand(ArrayList<Card> currentHand) {
        this.currentHand = currentHand;
    }

    public boolean isBust() {
        return isBust;
    }

    public void setBust(boolean bust) {
        isBust = bust;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}
