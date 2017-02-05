package uri.egr.biosensing.anearbeta.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import uri.egr.biosensing.anearbeta.SettingsActivity;

/**
 * Created by np on 2/5/2016.
 */

public class SDCardInsertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SettingsActivity.putBoolean(context, SettingsActivity.PREF_SD_CARD, true);
    }
}
