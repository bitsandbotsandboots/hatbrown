package uri.egr.biosensing.anearbeta.services;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.auth.FirebaseAuth;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

/**
 * Created by np on 2/5/2016.
 */

public class CSVLoggingService extends IntentService {
    private Socket mSocket;
    private FirebaseAuth mFirebaseAuth;
    WebSocketClient mWebSocketClient;
    public static final String INTENT_FILE = "intent_file";
    public static final String INTENT_HEADER = "intent_header";
    public static final String INTENT_CONTENTS = "intent_contents";

    public CSVLoggingService() {
        super("CSV Logging Thread");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        if (intent == null || !intent.hasExtra(INTENT_FILE) || !intent.hasExtra(INTENT_HEADER) || !intent.hasExtra(INTENT_CONTENTS)) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        boolean newFile = false;
        File file = new File(intent.getStringExtra(INTENT_FILE));
        if (!file.exists()) {
            try {
                newFile = true;
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int milliSecond = calendar.get(Calendar.MILLISECOND);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);

        String date = formatDate(month, day, year);
        String time = formatTime(hour, minute, second, milliSecond);

//        // Initialize FirebaseAuth
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        mFirebaseAuth.createUserWithEmailAndPassword("pellyprograms@gmail.com", "password")
//                .addOnCompleteListener((Executor) CSVLoggingService.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//
//
//                    }
//                });

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            if (newFile) {
                fileOutputStream.write((intent.getStringExtra(INTENT_HEADER)).getBytes());
                fileOutputStream.write("\n".getBytes());
            }
            fileOutputStream.write((date + ",").getBytes());
            fileOutputStream.write((time + ",").getBytes());
            fileOutputStream.write((intent.getStringExtra(INTENT_CONTENTS)).getBytes());
            fileOutputStream.write("\n".getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatDate(int month, int day, int year) {
        String monthString, dayString;
        if (month < 10) {
            monthString = "0" + month;
        } else {
            monthString = String.valueOf(month);
        }

        if (day < 10) {
            dayString = "0" + day;
        } else {
            dayString = String.valueOf(day);
        }

        return monthString + "/" + dayString + "/" + String.valueOf(year);
    }

    private String formatTime(int hour, int minute, int second, int milliSecond) {
        String hourString, minuteString, secondString, milliSecondString;
        if (hour < 10) {
            hourString = "0" + hour;
        } else {
            hourString = String.valueOf(hour);
        }

        if (minute < 10) {
            minuteString = "0" + minute;
        } else {
            minuteString = String.valueOf(minute);
        }

        if (second < 10) {
            secondString = "0" + second;
        } else {
            secondString = String.valueOf(second);
        }

        if (milliSecond < 10) {
            milliSecondString = "00" + milliSecond;
        } else if (milliSecond < 100) {
            milliSecondString = "0" + milliSecond;
        } else {
            milliSecondString = String.valueOf(milliSecond);
        }

        return hourString + ":" + minuteString + ":" + secondString + "." + milliSecondString;
    }



    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("bitsandbotsandboots.azurewebsites.net:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }
}