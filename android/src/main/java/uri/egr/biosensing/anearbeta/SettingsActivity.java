package uri.egr.biosensing.anearbeta;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.HeartRateConsentListener;

import java.util.Set;

import uri.egr.biosensing.anearbeta.fragments.SettingsFragment;

/**
 * Created by np on 2/5/2016.
 */

public class SettingsActivity extends AppCompatActivity {
    public static final String PREF_AUDIO_ENABLE = "pref_audio_enable";
    public static final String PREF_AUDIO_DURATION = "pref_audio_duration";
    public static final String PREF_AUDIO_DELAY = "pref_audio_delay";
    public static final String PREF_SENSOR_ENABLE = "pref_sensor_enable";
    public static final String PREF_SENSOR_DURATION = "pref_sensor_duration";
    public static final String PREF_SENSOR_DELAY = "pref_sensor_delay";
    public static final String PREF_SENSOR_SELECT = "pref_sensor_select";
    public static final String PREF_SENSOR_TRIGGER = "pref_sensor_trigger";
    public static final String PREF_DYNAMIC_BLACKOUT_ENABLE = "pref_dynamic_blackout_enable";
    public static final String PREF_IDENTIFIER = "pref_identifier";
    public static final String PREF_SAVE_LOCATION = "pref_save_location";
    public static final String PREF_BAND_WORN = "pref_band_worn";
    public static final String PREF_SD_CARD = "pref_sd_card";
    public static final String PREF_HR_CONSENT = "pref_hr_consent";
    public static final String PREF_ENABLE_GSR = "pref_enable_gsr";
    public static final String PREF_ENABLE_HR = "pref_enable_hr";
    public static final String PREF_ENABLE_ACC = "pref_enable_acc";
    public static final String PREF_ENABLE_TEMP = "pref_enable_temp";
    public static final String PREF_ENABLE_CONTACT = "pref_enable_contact";
    public static final String PREF_ENABLE_RR = "pref_enable_rr";
    public static final String PREF_ENABLE_LIGHT = "pref_enable_light";
    public static final String ERASING = "erasing_preference";

    public static final int PERMISSIONS_REQUEST_CODE = 4310;

    public static String[] get() {
        return new String[] {PREF_AUDIO_ENABLE, PREF_AUDIO_DURATION, PREF_AUDIO_DELAY, PREF_SENSOR_ENABLE,
                PREF_SENSOR_DURATION, PREF_SENSOR_DELAY, PREF_SENSOR_SELECT, PREF_SENSOR_TRIGGER,
                PREF_DYNAMIC_BLACKOUT_ENABLE, PREF_IDENTIFIER, PREF_SAVE_LOCATION, ERASING};
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static Set<String> getMultiSelectPreference(Context context, String key, Set<String> defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getStringSet(key, defaultValue);
    }

    private HeartRateConsentListener mHeartRateConsentListener = new HeartRateConsentListener() {
        @Override
        public void userAccepted(boolean b) {

        }
    };

    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Activity activity = this;
        BandInfo[] bandInfo = BandClientManager.getInstance().getPairedBands();
        final BandClient bandClient = BandClientManager.getInstance().create(this, bandInfo[0]);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ConnectionState connectionState = bandClient.connect().await();
                    if (connectionState == ConnectionState.CONNECTED) {
                        UserConsent userConsent = bandClient.getSensorManager().getCurrentHeartRateConsent();
                        if (userConsent != UserConsent.GRANTED) {
                            bandClient.getSensorManager().requestHeartRateConsent(activity, mHeartRateConsentListener);
                        }
                    }
                    if (bandClient.isConnected()) {
                        bandClient.disconnect().await();
                    }
                } catch (BandException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFragment = new SettingsFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mFragment, "settings_fragment")
                .commit();
    }

    public void requestPermission(String permission) {
        String[] permissions = new String[] {permission};
        requestPermission(permissions);
    }

    public void requestPermission(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
        }
    }

    public boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
}
