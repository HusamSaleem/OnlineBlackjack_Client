package onlineblackjack.client.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import onlineblackjack.client.Client;
import onlineblackjack.client.R;
import onlineblackjack.client.ServerConnectionManager;

public class MainActivity extends AppCompatActivity {

    private TextView usernameInput;
    private Button registerUsernameBtn;

    private TextView log;
    private TextView serverStatusTxt;
    private TextView versionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityManager.mainActivity = this;
        setUpUI();

        // Starts a connection to the server
        if (!ServerConnectionManager.connected()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startConnection();
                }
            }).start();
        }
    }

    protected void onStart() {
        super.onStart();
        ActivityManager.setCurrentActivity(this);
        ActivityManager.setInGame(false);
        ActivityManager.setInLobby(false);
    }

    private void setUpUI() {
        usernameInput = findViewById(R.id.username_input);
        registerUsernameBtn = findViewById(R.id.register_username_btn);
        log = findViewById(R.id.main_activity_log);
        serverStatusTxt = findViewById(R.id.server_status_txt);
        versionId = findViewById(R.id.version_id_txt);

        setUpButtonListeners();
    }

    private void setUpButtonListeners() {
        registerUsernameBtn.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();

            if (username.length() >= 3 && username.length() <= 16) {
                if (ServerConnectionManager.connected()) {
                    Client.submitUsernameRequest(username);
                }
            } else {
                setLogText("Username is too short or too long.", "#FF0000");
            }
        });
    }

    public void setServerStatusOnline() {
        serverStatusTxt.setTextColor(Color.GREEN);
        serverStatusTxt.setText("Server Status: Online");
    }

    public void setServerStatusOffline() {
        serverStatusTxt.setTextColor(Color.RED);
        serverStatusTxt.setText("Server Status: Offline");
    }

    public void startConnection() {
        try {
            ServerConnectionManager.connectToServer();
        } catch (IOException e) {
            Log.e("Error", "Error when connecting to server");
        }
    }

    public void setLogText(String msg, String colorHex) {
        log.setVisibility(View.VISIBLE);
        log.setText(msg);
        log.setTextColor(Color.parseColor(colorHex));
    }

    public void setVersionId(String id) {
        versionId.setText("Version: " + id);
    }
}