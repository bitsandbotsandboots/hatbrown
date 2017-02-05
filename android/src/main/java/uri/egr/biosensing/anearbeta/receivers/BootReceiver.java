package uri.egr.biosensing.anearbeta.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import uri.egr.biosensing.anearbeta.SettingsActivity;
import uri.egr.biosensing.anearbeta.services.RecordManagerService;

/**
 * Created by np on 2/5/2016.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (SettingsActivity.getBoolean(context, SettingsActivity.PREF_SENSOR_ENABLE, false)) {
            RecordManagerService.start(context, RecordManagerService.ACTION_SENSOR_CREATE);
        }
        if (SettingsActivity.getBoolean(context, SettingsActivity.PREF_AUDIO_ENABLE, false)) {
            RecordManagerService.start(context, RecordManagerService.ACTION_AUDIO_CREATE);
        }
    }
}
