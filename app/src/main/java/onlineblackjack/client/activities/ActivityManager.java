package onlineblackjack.client.activities;

import android.content.Context;
import android.content.Intent;

public class ActivityManager {
    private static Context currentActivity;
    private static boolean inGame = false;
    private static boolean inLobby = false;

    public static GameActivity gameActivity = null;
    public static LobbyActivity lobbyActivity = null;
    public static MainActivity mainActivity = null;

    public static void goToLobby() {
        Intent intent = new Intent(getCurrentActivity(), LobbyActivity.class);
        currentActivity.startActivity(intent);
    }

    public static void goToGame() {
        Intent intent = new Intent(getCurrentActivity(), GameActivity.class);
        currentActivity.startActivity(intent);
    }

    public static void goToMain() {
        Intent intent = new Intent(getCurrentActivity(), MainActivity.class);
        currentActivity.startActivity(intent);
    }

    public static boolean isInGame() {
        return inGame;
    }

    public static void setInGame(boolean inGame) {
        ActivityManager.inGame = inGame;
    }

    public static void setCurrentActivity(Context activity) {
        currentActivity = activity;
    }

    public static Context getCurrentActivity() {
        return currentActivity;
    }

    public static boolean isInLobby() {
        return inLobby;
    }

    public static void setInLobby(boolean inLobby) {
        ActivityManager.inLobby = inLobby;
    }
}
