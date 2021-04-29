package onlineblackjack.client.game;

import org.json.JSONException;
import org.json.JSONObject;

public class Card {
    private boolean hidden;
    private int value;
    private String cardId;
    private boolean isAce;

    public Card(JSONObject cardJson) {
        try {
            this.hidden = cardJson.getBoolean("hidden");
            this.value = cardJson.getInt("value");
            this.cardId = cardJson.getString("cardId");
            this.isAce = cardJson.getBoolean("isAce");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public boolean isAce() {
        return isAce;
    }

    public void setAce(boolean ace) {
        isAce = ace;
    }
}
