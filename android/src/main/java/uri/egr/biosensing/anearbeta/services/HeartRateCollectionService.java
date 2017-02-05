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
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.HeartRateQuality;

import java.io.File;
import java.util.Set;

import uri.egr.biosensing.anearbeta.SettingsActivity;

/**
 * Created by np on 2/5/2016.
 */

public class HeartRateCollectionService extends Service {
    public static final String HEADER = "date,time,heart rate,quality";
    public static final String INTENT_FILE = "intent_file";
    public static final int HR_THRESHOLD = 100;

    public static void start(Context context, File file) {
        Intent intent = new Intent(context, HeartRateCollectionService.class);
        intent.putExtra(INTENT_FILE, file.getAbsolutePath());
        context.startService(intent);
    }

    private Context mContext;
    private BandClient mBandClient;
    private File mFile;
    private boolean mTrigger;
    private CountDownTimer mCountDownTimer;

    private BandHeartRateEventListener mBandHeartRateEventListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(BandHeartRateEvent bandHeartRateEvent) {
            String contents = bandHeartRateEvent.getHeartRate() + "," + bandHeartRateEvent.getQuality();
            if (mTrigger && bandHeartRateEvent.getHeartRate() >= HR_THRESHOLD && bandHeartRateEvent.getQuality() == HeartRateQuality.LOCKED) {
                //Trigger Audio
                RecordManagerService.start(mContext, RecordManagerService.ACTION_AUDIO_TRIGGER);
            }
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

        Set<String> triggerSensors = SettingsActivity.getMultiSelectPreference(this, SettingsActivity.PREF_SENSOR_TRIGGER, null);
        mTrigger = triggerSensors.contains(SettingsActivity.PREF_ENABLE_HR);

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
                    if (mBandClient.getSensorManager().getCurrentHeartRateConsent() != UserConsent.GRANTED) {
                        //Heart Rate Consent not Granted
                        return;
                    }
                    mBandClient.getSensorManager().registerHeartRateEventListener(mBandHeartRateEventListener);
                } catch (BandException | InterruptedException e) {
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
                mBandClient.getSensorManager().unregisterHeartRateEventListener(mBandHeartRateEventListener);
                mBandClient.disconnect().await();
            }
        } catch (BandException | InterruptedException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        Log.d(this.getClass().getSimpleName(), "Service Destroyed");
    }
}
