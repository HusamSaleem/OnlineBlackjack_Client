package onlineblackjack.client;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

public class SendData implements Runnable {
    String data;

    public SendData(String data) {
        this.data = data;
    }

    @Override
    public void run() {
        OutputStream dataOutputStream = null;
        try {
            dataOutputStream = ServerConnectionManager.getClientSocket().getOutputStream();
            data = data + "~";
            dataOutputStream.write(data.getBytes());
            dataOutputStream.flush();
        } catch (IOException e) {
            Log.e("Error", "Error when sending data");
        }
    }
}
