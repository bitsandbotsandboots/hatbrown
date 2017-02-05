package uri.egr.biosensing.anearbeta.services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

import uri.egr.biosensing.anearbeta.DbHelpers.BlackoutSettingsDbHelper;
import uri.egr.biosensing.anearbeta.SettingsActivity;
import uri.egr.biosensing.anearbeta.models.BlackoutSettingModel;
import uri.egr.biosensing.anearbeta.receivers.AlarmReceiver;

/**
 * Created by np on 2/4/2016.
 */

public class RecordManagerService extends IntentService {
    public static final String INTENT_ACTION = "intent_action";
    public static final int ACTION_AUDIO_START = 0;
    public static final int ACTION_AUDIO_TRIGGER = 1;
    public static final int ACTION_AUDIO_CREATE = 2;
    public static final int ACTION_AUDIO_CANCEL = 3;
    public static final int ACTION_SENSOR_START = 4;
    public static final int ACTION_SENSOR_CREATE = 5;
    public static final int ACTION_SENSOR_CANCEL = 6;
    private boolean first;

    public static void start(Context context, int action) {
        Intent intent = new Intent(context, RecordManagerService.class);
        intent.putExtra(INTENT_ACTION, action);
        context.startService(intent);
    }

    public RecordManagerService() {
        super("Record Manager Thread");
        first = false;
    }

    @Override
    public void onHandleIntent(Intent intent) {
        if (intent == null || !intent.hasExtra(INTENT_ACTION)) {
            log("Service not Started Properly");
            return;
        }

        switch (intent.getIntExtra(INTENT_ACTION, -1)) {
            case ACTION_AUDIO_START:
                log("ACTION_AUDIO_START");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        log("anEAR must have Audio Record permission to record audio");
                        return;
                    }
                }
                //Start Recording
                startAudio();
                setAudioAlarm();
                break;
            case ACTION_AUDIO_TRIGGER:
                log("ACTION_AUDIO_TRIGGER");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        log("anEAR must have Audio Record permission to record audio");
                        return;
                    }
                }
                //Start Recording
                startAudio();
                break;
            case ACTION_AUDIO_CREATE:
                log("ACTION_AUDIO_CREATE");
                setAudioAlarm();
                break;
            case ACTION_AUDIO_CANCEL:
                log("ACTION_AUDIO_CANCEL");
                cancelAlarm(ACTION_AUDIO_CANCEL);
                break;
            case ACTION_SENSOR_START:
                log("ACTION_SENSOR_START");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        log("anEAR must have bluetooth permissions to record sensors");
                        return;
                    }
                }
                startSensors(getEnabledSensors());
                setSensorAlarm();
                break;
            case ACTION_SENSOR_CREATE:
                log("ACTION_SENSOR_CREATE");
                setSensorAlarm();
                break;
            case ACTION_SENSOR_CANCEL:
                log("ACTION_SENSOR_CANCEL");
                cancelAlarm(ACTION_SENSOR_CANCEL);
                cancelSensors();
                break;
        }
    }

    private String[] getEnabledSensors() {
        String[] temp = new String[7];
        int count = 0;
        Set<String> enabledSensors = SettingsActivity.getMultiSelectPreference(this, SettingsActivity.PREF_SENSOR_SELECT, null);
        if (enabledSensors != null) {
            Iterator<String> iterator = enabledSensors.iterator();
            while (iterator.hasNext()) {
                temp[count++] = iterator.next();
            }
            String[] sensors = new String[count];
            while (count > 0) {
                sensors[--count] = temp[count];
            }
            return sensors;
        } else {
            return null;
        }
    }

    private void setAudioAlarm() {
        int audioDuration = Integer.parseInt(SettingsActivity.getString(this, SettingsActivity.PREF_AUDIO_DURATION, "30"));
        int audioDelay = Integer.parseInt(SettingsActivity.getString(this, SettingsActivity.PREF_AUDIO_DELAY, "12")) * 60;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, audioDelay + audioDuration);

        ((AlarmManager) getSystemService(ALARM_SERVICE)).setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), getPendingIntent(AlarmReceiver.AUDIO_ID, ACTION_AUDIO_START));
    }

    private void setSensorAlarm() {
        int sensorDuration = Integer.parseInt(SettingsActivity.getString(this, SettingsActivity.PREF_SENSOR_DURATION, "60"));
        int sensorDelay = Integer.parseInt(SettingsActivity.getString(this, SettingsActivity.PREF_SENSOR_DELAY, "0")) * 60;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, sensorDelay + sensorDuration);

        ((AlarmManager) getSystemService(ALARM_SERVICE)).setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), getPendingIntent(AlarmReceiver.SENSOR_ID, ACTION_SENSOR_START));
    }

    private void cancelAlarm(int action) {
        PendingIntent pendingIntent;
        switch (action) {
            case ACTION_AUDIO_CANCEL:
                pendingIntent = getPendingIntent(AlarmReceiver.AUDIO_ID, ACTION_AUDIO_START);
                stopService(new Intent(this, AudioCollectionService.class));
                break;
            case ACTION_SENSOR_CANCEL:
                pendingIntent = getPendingIntent(AlarmReceiver.SENSOR_ID, ACTION_SENSOR_START);
                break;
            default:
                return;
        }
        ((AlarmManager) getSystemService(ALARM_SERVICE)).cancel(pendingIntent);
    }

    private void cancelSensors() {
        stopService(new Intent(this, HeartRateCollectionService.class));
        stopService(new Intent(this, AccelerometerCollectionService.class));
        stopService(new Intent(this, AmbientLightCollectionService.class));
        stopService(new Intent(this, BandContactCollectionService.class));
        stopService(new Intent(this, GSRCollectionService.class));
        stopService(new Intent(this, RRIntervalCollectionService.class));
        stopService(new Intent(this, SkinTemperatureCollectionService.class));
    }

    private PendingIntent getPendingIntent(int id, int action) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.KEY_ALARM_ID, id);
        intent.putExtra(AlarmReceiver.KEY_ACTION, action);

        return PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void startAudio() {
        BlackoutSettingsDbHelper dbHelper = new BlackoutSettingsDbHelper(this);
        ArrayList<BlackoutSettingModel> blackoutSettingModels = dbHelper.getAllSettings();
        for (BlackoutSettingModel blackoutSettingModel : blackoutSettingModels) {
            if (blackoutSettingModel.inBlackoutTime()) {
                return;
            }
        }

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        File directory = new File(getRootDirectory(), formatDate(month, day, year));
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, formatTime(hour, minute, second) + ".wav");
        File temp = new File(directory, "raw_audio.tmp");

        Intent intent = new Intent(this, AudioCollectionService.class);
        intent.putExtra(AudioCollectionService.INTENT_WAV_FILE, file.getAbsolutePath());
        intent.putExtra(AudioCollectionService.INTENT_TEMP_FILE, temp.getAbsolutePath());
        startService(intent);
    }

    private void startSensors(String[] sensors) {
        boolean isBandWorn = SettingsActivity.getBoolean(this, SettingsActivity.PREF_BAND_WORN, false);
        boolean isDynamicBlackout = SettingsActivity.getBoolean(this, SettingsActivity.PREF_DYNAMIC_BLACKOUT_ENABLE, true);

        if (!isDynamicBlackout) {
            BlackoutSettingsDbHelper dbHelper = new BlackoutSettingsDbHelper(this);
            ArrayList<BlackoutSettingModel> blackoutSettingModels = dbHelper.getAllSettings();
            for (BlackoutSettingModel blackoutSettingModel : blackoutSettingModels) {
                if (blackoutSettingModel.inBlackoutTime()) {
                    return;
                }
            }
        }
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH)+1;
        int year = calendar.get(Calendar.YEAR);
        File directory = new File(getRootDirectory(), formatDate(month, day, year));
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file;
        for (String sensorKey : sensors) {
            switch (sensorKey) {
                case SettingsActivity.PREF_ENABLE_ACC:
                    if (!isDynamicBlackout || isBandWorn) {
                        file = new File(directory, "acc.csv");
                        AccelerometerCollectionService.start(this, file);
                    }
                    break;
                case SettingsActivity.PREF_ENABLE_CONTACT:
                    file = new File(directory, "contact.csv");
                    BandContactCollectionService.start(this, file);
                    break;
                case SettingsActivity.PREF_ENABLE_GSR:
                    if (!isDynamicBlackout || isBandWorn) {
                        file = new File(directory, "gsr.csv");
                        GSRCollectionService.start(this, file);
                    }
                    break;
                case SettingsActivity.PREF_ENABLE_HR:
                    if (!isDynamicBlackout || isBandWorn) {
                        file = new File(directory, "hr.csv");
                        HeartRateCollectionService.start(this, file);
                    }
                    break;
                case SettingsActivity.PREF_ENABLE_LIGHT:
                    if (!isDynamicBlackout || isBandWorn) {
                        file = new File(directory, "light.csv");
                        AmbientLightCollectionService.start(this, file);
                    }
                    break;
                case SettingsActivity.PREF_ENABLE_RR:
                    if (!isDynamicBlackout || isBandWorn) {
                        file = new File(directory, "rr.csv");
                        RRIntervalCollectionService.start(this, file);
                    }
                    break;
                case SettingsActivity.PREF_ENABLE_TEMP:
                    if (!isDynamicBlackout || isBandWorn) {
                        file = new File(directory, "temp.csv");
                        SkinTemperatureCollectionService.start(this, file);
                    }
                    break;
            }
        }
    }

    private File getRootDirectory() {
        String saveLocation = SettingsActivity.getString(this, SettingsActivity.PREF_SAVE_LOCATION, null);
        File root;
        switch (saveLocation) {
            case "location_sd_card":
                if (SettingsActivity.getBoolean(this, SettingsActivity.PREF_SD_CARD, false)) {
                    root = new File("/storage/sdcard1");
                    if (!root.exists()) {
                        root = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOCUMENTS);
                    }
                } else {
                    root = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOCUMENTS);
                }
                break;
            case "location_documents":
                root = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOCUMENTS);
                break;
            default:
                if (SettingsActivity.getBoolean(this, SettingsActivity.PREF_SD_CARD, false)) {
                    root = new File("/storage/sdcard1");
                    if (!root.exists()) {
                        root = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOCUMENTS);
                    }
                } else {
                    root = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOCUMENTS);
                }
                break;
        }
        File directory;
        String id = SettingsActivity.getString(this, SettingsActivity.PREF_IDENTIFIER, null);
        if (id == null || id.equals("")) {
            directory = new File(root, "Canvast");
        } else {
            directory = new File(root, "Canvast/" + id);
        }
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
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

        return monthString + "_" + dayString + "_" + String.valueOf(year);
    }

    private String formatTime(int hour, int minute, int second) {
        String hourString, minuteString, secondString;
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

        return hourString + "_" + minuteString + "_" + secondString;
    }

    private void log(String message) {
        String tag = this.getClass().getSimpleName();
        Log.d(tag, message);
    }
}
