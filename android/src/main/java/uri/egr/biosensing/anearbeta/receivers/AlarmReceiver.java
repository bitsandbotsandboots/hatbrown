package uri.egr.biosensing.anearbeta.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import uri.egr.biosensing.anearbeta.services.RecordManagerService;

/**
 * Created by np on 2/4/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    public static final String KEY_ACTION = "alarm_key_action";
    public static final String KEY_ALARM_ID = "key_alarm_id";
    public static final int AUDIO_ID = 430;
    public static final int SENSOR_ID = 431;

    @Override
    public void onReceive(Context context, Intent intent) {
        RecordManagerService.start(context, intent.getIntExtra(KEY_ACTION, -1));
    }
}
