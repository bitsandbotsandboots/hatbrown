package uri.egr.biosensing.anearbeta.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.InvalidBandVersionException;
import com.microsoft.band.sensors.BandGsrEvent;
import com.microsoft.band.sensors.BandGsrEventListener;

import java.io.File;

import uri.egr.biosensing.anearbeta.SettingsActivity;

/**
 * Created by np on 2/5/2016.
 */

public class GSRCollectionService extends Service {
    public static final String HEADER = "date,time,resistance (kohms)";
    public static final String INTENT_FILE = "intent_file";

    public static void start(Context context, File file) {
        Intent intent = new Intent(context, GSRCollectionService.class);
        intent.putExtra(INTENT_FILE, file.getAbsolutePath());
        context.startService(intent);
    }

    private Context mContext;
    private BandClient mBandClient;
    private File mFile;
    private CountDownTimer mCountDownTimer;

    private BandGsrEventListener mBandHeartRateEventListener = new BandGsrEventListener() {
        @Override
        public void onBandGsrChanged(BandGsrEvent bandGsrEvent) {
            String contents = String.valueOf(bandGsrEvent.getResistance());
            Log.d(this.getClass().getSimpleName(), contents);
            Intent intent = new Intent(mContext, CSVLoggingService.class);
            intent.putExtra(CSVLoggingService.INTENT_HEADER, HEADER);
            intent.putExtra(CSVLoggingService.INTENT_FILE, mFile.getAbsolutePath());
            intent.putExtra(CSVLoggingService.INTENT_CONTENTS, contents);
            mContext.startService(intent);
        }
    };

    @Override
    public void onCreate() {
        Log.d(this.getClass().getSimpleName(), "Service Created");
        mContext = this;

        int duration = Integer.parseInt(SettingsActivity.getString(this, SettingsActivity.PREF_SENSOR_DURATION, "60")) * 1000;
        mCountDownTimer = new CountDownTimer(duration, duration) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                stopSelf();
            }
        };
        mCountDownTimer.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        if (intent == null || !intent.hasExtra(INTENT_FILE)) {
            return START_NOT_STICKY;
        }

        mFile = new File(intent.getStringExtra(INTENT_FILE));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BandInfo[] bandInfo = BandClientManager.getInstance().getPairedBands();
                    mBandClient = BandClientManager.getInstance().create(mContext, bandInfo[0]);
                    ConnectionState connectionState = mBandClient.connect().await();
                    if (connectionState != ConnectionState.CONNECTED) {
                        //Could not connect to Band
                        return;
                    }
                    mBandClient.getSensorManager().registerGsrEventListener(mBandHeartRateEventListener);
                } catch (InvalidBandVersionException | BandException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Heart Rate Collection Thread").start();


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            if (mBandClient.isConnected()) {
                mBandClient.getSensorManager().unregisterGsrEventListener(mBandHeartRateEventListener);
                mBandClient.disconnect().await();
            }
        } catch (BandException | InterruptedException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        Log.d(this.getClass().getSimpleName(), "Service Destroyed");
    }
}
