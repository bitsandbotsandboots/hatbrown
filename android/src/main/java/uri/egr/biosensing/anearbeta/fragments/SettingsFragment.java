package uri.egr.biosensing.anearbeta.fragments;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

import uri.egr.biosensing.anearbeta.R;
import uri.egr.biosensing.anearbeta.SettingsActivity;
import uri.egr.biosensing.anearbeta.services.RecordManagerService;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by np on 2/4/2017.
 *
 */

public class SettingsFragment extends PreferenceFragment {
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            setPreferenceSummary(key);
            switch (key) {
                case SettingsActivity.PREF_SENSOR_ENABLE:
                    if (sharedPreferences.getBoolean(key, false)) {
                        /**
                         * In order to record the sensors, anEAR must have permission to use
                         * Bluetooth (in order to access the Band) as well as writing to external
                         * storage (to save the sensor data)
                         */
                        String[] requiredPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.BLUETOOTH};
                        ((SettingsActivity) getActivity()).requestPermission(requiredPermissions);
                        RecordManagerService.start(getActivity(), RecordManagerService.ACTION_SENSOR_CREATE);
                    } else {
                        RecordManagerService.start(getActivity(), RecordManagerService.ACTION_SENSOR_CANCEL);
                    }
                    break;
                case SettingsActivity.PREF_AUDIO_ENABLE:
                    if (sharedPreferences.getBoolean(key, false)) {
                        String[] requiredPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
                        ((SettingsActivity) getActivity()).requestPermission(requiredPermissions);
                        Log.d(this.getClass().getSimpleName(), "Start Audio");
                        RecordManagerService.start(getActivity(), RecordManagerService.ACTION_AUDIO_CREATE);
                    } else {
                        Log.d(this.getClass().getSimpleName(), "Cancel Audio");
                        RecordManagerService.start(getActivity(), RecordManagerService.ACTION_AUDIO_CANCEL);
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        mSharedPreferences.registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            setPreferenceSummaries();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
    }

    private void setPreferenceSummaries() {
        String[] preferences = SettingsActivity.get();
        for (String key : preferences) {
            setPreferenceSummary(key);
        }
    }

    public void setPreferenceSummary(String key) {
        Preference preference = findPreference(key);
        switch (key) {
            case SettingsActivity.PREF_AUDIO_ENABLE:
                String summary;
                if (mSharedPreferences.getBoolean(key, false)) {
                    summary = "Periodic Audio Recordings are enabled";
                } else {
                    summary = "Periodic Audio Recordings are disabled";
                }
                preference.setSummary(summary);
                break;
            case SettingsActivity.PREF_AUDIO_DURATION:
                int duration = Integer.parseInt(mSharedPreferences.getString(key, null));
                preference.setSummary("Audio Recordings will last " + duration + " seconds");
                break;
            case SettingsActivity.PREF_AUDIO_DELAY:
                int delay = Integer.parseInt(mSharedPreferences.getString(key, null));
                preference.setSummary("Audio Recordings will begin every " + delay + " minutes");
                break;
            case SettingsActivity.PREF_SENSOR_ENABLE:
                String sensorSummary;
                if (mSharedPreferences.getBoolean(key, false)) {
                    sensorSummary = "Canvast is Enabled";
                    Toast.makeText(getApplicationContext(), "Connecting...",
                            Toast.LENGTH_LONG).show();
                } else {
                    sensorSummary = "Canvast is Disabled";
                }
                preference.setSummary(sensorSummary);
                break;
            case SettingsActivity.PREF_SENSOR_DURATION:
                int sensorDuration = Integer.parseInt(mSharedPreferences.getString(key, null));
                preference.setSummary("Sensor Recordings will last " + sensorDuration + " seconds");
                break;
            case SettingsActivity.PREF_SENSOR_DELAY:
                int sensorDelay = Integer.parseInt(mSharedPreferences.getString(key, null));
                if (sensorDelay == 0) {
                    preference.setSummary("Sensors will record continuously");
                } else {
                    preference.setSummary("Sensor Recordings will begin every " + sensorDelay + " minutes");
                }
                break;
            case SettingsActivity.PREF_SENSOR_SELECT:
                Set<String> enabledSensors = mSharedPreferences.getStringSet(key, null);
                int numEnabledSensors;
                if (enabledSensors == null) {
                    numEnabledSensors = 0;
                } else {
                    numEnabledSensors = enabledSensors.size();
                }
                preference.setSummary(numEnabledSensors + " sensors are set to record");
                break;
            case SettingsActivity.PREF_SENSOR_TRIGGER:
                Set<String> triggerSensors = mSharedPreferences.getStringSet(key, null);
                int numTriggerSensors;
                if (triggerSensors == null) {
                    numTriggerSensors = 0;
                } else {
                    numTriggerSensors = triggerSensors.size() - 1;
                }
                preference.setSummary(numTriggerSensors + " sensors are set to trigger additional audio recordings");
                break;
            case SettingsActivity.PREF_IDENTIFIER:
                preference.setSummary("Enter username. (X by default)");
                break;
            case SettingsActivity.PREF_DYNAMIC_BLACKOUT_ENABLE:
                if (mSharedPreferences.getBoolean(key, false)) {
                    preference.setSummary("Dynamic Blackout Settings are Enabled. (Sensor Recordings will not occur if Band is not being worn)");
                } else {
                    preference.setSummary("Dynamic Blackout Settings are Disabled. (Regular Blackout Settings will be used)");
                }
                break;
            case SettingsActivity.PREF_SAVE_LOCATION:
                String saveLocation = mSharedPreferences.getString(key, null);
                if (saveLocation == null) {
                    return;
                }
                switch (saveLocation) {
                    case "location_sd_card":
                        preference.setSummary("Files will be saved to the SD Card");
                        break;
                    case "location_documents":
                        preference.setSummary("Files will be saved to the Documents folder");
                        break;
                }
        }
    }
}
