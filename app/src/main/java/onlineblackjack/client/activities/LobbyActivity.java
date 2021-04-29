package onlineblackjack.client.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

import onlineblackjack.client.Client;
import onlineblackjack.client.game.GameManager;
import onlineblackjack.client.Packet;
import onlineblackjack.client.R;

import static onlineblackjack.client.Client.stopMatchmaking;

public class LobbyActivity extends AppCompatActivity {

    private TextView usernameTxt;
    private TextView playersOnlineTxt;

    private TextView queueInfoTxt;
    private TextView queueElapsedTimeTxt;

    private Button leaveQueueBtn;
    private Button joinSoloBtn; // Solo session
    private Button joinDuoBtn; // 2 player queue
    private Button joinTrioBtn; // 3 player queue
    private Button joinQuadBtn; // 4 player queue

    private boolean inQueue = false;

    Timer timer;
    int secondsElapsed = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        ActivityManager.lobbyActivity = this;
        setUpUI();
        Client.requestLobbyInfo();
    }

    protected void onStart() {
        super.onStart();
        ActivityManager.setCurrentActivity(this);
        ActivityManager.setInGame(false);
        ActivityManager.setInLobby(true);
    }

    private void setUpUI() {
        usernameTxt = findViewById(R.id.lobby_username_txt);
        playersOnlineTxt = findViewById(R.id.players_online_txt);
        queueElapsedTimeTxt = findViewById(R.id.queue_timer_txt);
        queueInfoTxt = findViewById(R.id.queue_info_txt);

        leaveQueueBtn = findViewById(R.id.leave_queue_btn);
        joinSoloBtn = findViewById(R.id.join_solo_game_btn);
        joinDuoBtn = findViewById(R.id.join_two_game_btn2);
        joinTrioBtn = findViewById(R.id.join_three_game_btn3);
        joinQuadBtn = findViewById(R.id.join_four_game_btn4);

        setUpListeners();
    }

    private void setUpListeners() {
        leaveQueueBtn.setOnClickListener(v -> {
            if (inQueue) {
                stopMatchmaking();
                stopQueueTimer();

                inQueue = false;
                leaveQueueBtn.setVisibility(View.INVISIBLE);
                queueInfoTxt.setVisibility(View.INVISIBLE);
                queueElapsedTimeTxt.setVisibility(View.INVISIBLE);
            }
        });

        joinSoloBtn.setOnClickListener(v -> {
            if (!inQueue) {
                Client.joinMatchmaking(Packet.JOIN_SOLO_SESSION);
            }
        });

        joinDuoBtn.setOnClickListener(v -> {
            if (!inQueue) {
                Client.joinMatchmaking(Packet.JOIN_TWO_PLAYER_QUEUE);
                inQueue = true;
                leaveQueueBtn.setVisibility(View.VISIBLE);
                startQueueTimer("two players");
            }
        });

        joinTrioBtn.setOnClickListener(v -> {
            if (!inQueue) {
                Client.joinMatchmaking(Packet.JOIN_THREE_PLAYER_QUEUE);
                inQueue = true;
                leaveQueueBtn.setVisibility(View.VISIBLE);
                startQueueTimer("three Players");
            }
        });

        joinQuadBtn.setOnClickListener(v -> {
            if (!inQueue) {
                Client.joinMatchmaking(Packet.JOIN_FOUR_PLAYER_QUEUE);
                inQueue = true;
                leaveQueueBtn.setVisibility(View.VISIBLE);
                startQueueTimer("four Players");
            }
        });
    }

    private void stopQueueTimer() {
        timer.cancel();
        secondsElapsed = 0;
        queueInfoTxt.setVisibility(View.INVISIBLE);
        queueElapsedTimeTxt.setVisibility(View.INVISIBLE);
    }

    private void startQueueTimer(String queueType) {
        timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                updateTimer();
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 1000);
        queueInfoTxt.setVisibility(View.VISIBLE);
        queueElapsedTimeTxt.setVisibility(View.VISIBLE);

        queueInfoTxt.setText("Currently in queue for " + queueType);
    }

    private void updateTimer() {
        Handler handler = new Handler(getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                queueElapsedTimeTxt.setText(secondsElapsed + " seconds have elapsed");
                secondsElapsed++;
            }
        });
    }

    public void setPlayerName() {
        usernameTxt.setText(String.format("You: %s", GameManager.getPlayer().getUsername()));
    }

    public void setPlayersOnlineTxt(int amt) {
        playersOnlineTxt.setText(String.format("Players currently online: %d", amt));
    }
}
