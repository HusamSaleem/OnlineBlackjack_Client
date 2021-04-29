package onlineblackjack.client.game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Player {
    private String username;
    private ArrayList<Card> currentHand;
    private boolean isMyTurn;
    private boolean endTurn;
    private boolean isBust;
    private int currentBet;
    private int money;
    private int totalScore;
    private boolean blackjack;
    private boolean betPlaced;
    private boolean spectateMode;
    private int playerId;

    public void updateFromJson(JSONObject json) throws JSONException {
        username = json.getString("username");
        isMyTurn = json.getBoolean("isMyTurn");
        endTurn = json.getBoolean("endTurn");
        isBust = json.getBoolean("isBust");
        currentBet = json.getInt("currentBet");
        money = json.getInt("money");
        blackjack = json.getBoolean("blackjack");
        betPlaced = json.getBoolean("betPlaced");
        totalScore = json.getInt("totalScore");
        spectateMode = json.getBoolean("spectateMode");
        playerId = json.getInt("playerId");

        JSONArray jsonArray = json.getJSONArray("currentHand");
        currentHand = new ArrayList<Card>();

        for (int i = 0; i < jsonArray.length(); i++) {
            currentHand.add(new Card(jsonArray.getJSONObject(i)));
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<Card> getCurrentHand() {
        return currentHand;
    }

    public void setCurrentHand(ArrayList<Card> currentHand) {
        this.currentHand = currentHand;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public void setMyTurn(boolean myTurn) {
        isMyTurn = myTurn;
    }

    public boolean isEndTurn() {
        return endTurn;
    }

    public void setEndTurn(boolean endTurn) {
        this.endTurn = endTurn;
    }

    public boolean isBust() {
        return isBust;
    }

    public void setBust(boolean bust) {
        isBust = bust;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public void setCurrentBet(int currentBet) {
        this.currentBet = currentBet;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public boolean isBlackjack() {
        return blackjack;
    }

    public void setBlackjack(boolean blackjack) {
        this.blackjack = blackjack;
    }

    public boolean isBetPlaced() {
        return betPlaced;
    }

    public void setBetPlaced(boolean betPlaced) {
        this.betPlaced = betPlaced;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public boolean isSpectateMode() {
        return spectateMode;
    }

    public void setSpectateMode(boolean spectateMode) {
        this.spectateMode = spectateMode;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
