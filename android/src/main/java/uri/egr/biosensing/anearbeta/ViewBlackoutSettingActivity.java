package uri.egr.biosensing.anearbeta;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

import uri.egr.biosensing.anearbeta.DbHelpers.BlackoutSettingsDbHelper;
import uri.egr.biosensing.anearbeta.fragments.BlackoutSettingDetailFragment;
import uri.egr.biosensing.anearbeta.models.BlackoutSettingModel;

/**
 * Created by np on 2/5/2016.
 *
 */
public class ViewBlackoutSettingActivity extends AppCompatActivity {
    public static final String EXTRA_BUNDLE = "extra_bundle";
    public static final String EXTRA_DISPLAY_NAME = "extra_display_name";
    public static final String EXTRA_START_TIME = "extra_start_time";
    public static final String EXTRA_END_TIME = "extra_end_time";
    public static final String EXTRA_WEEKEND = "extra_weekend";

    private CoordinatorLayout mCoordinatorLayout;

    public static void start(Context context) {
        BlackoutSettingsDbHelper dbHelper = new BlackoutSettingsDbHelper(context);
        ArrayList<BlackoutSettingModel> models = dbHelper.getAllSettings();
        int size;
        if (models == null) {
            size = 0;
        } else {
            size = models.size();
        }
        Calendar currentTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.HOUR_OF_DAY, 1);
        BlackoutSettingModel model = new BlackoutSettingModel("setting" + size, BlackoutSettingModel.generateTime(currentTime), BlackoutSettingModel.generateTime(endTime), false);
        dbHelper.putSetting(model);
        dbHelper.close();
        start(context, model);
    }

    public static void start(Context context, BlackoutSettingModel model) {
        Intent intent = new Intent(context, ViewBlackoutSettingActivity.class);
        Bundle modelBundle = new Bundle();
        modelBundle.putString(EXTRA_DISPLAY_NAME, model.getDisplayName());
        modelBundle.putInt(EXTRA_START_TIME, model.getRawStartTime());
        modelBundle.putInt(EXTRA_END_TIME, model.getRawEndTime());
        modelBundle.putBoolean(EXTRA_WEEKEND, model.isWeekends());
        intent.putExtra(EXTRA_BUNDLE, modelBundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(EXTRA_BUNDLE)) {
            finish();
            return;
        }
        Bundle bundle = intent.getBundleExtra(EXTRA_BUNDLE);
        Fragment fragment = BlackoutSettingDetailFragment.newInstance();
        fragment.setArguments(bundle);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, "create_blackout_settings_fragment")
                .commit();
    }

    public void setTime(int hour, int minute) {
        Fragment fragment = getFragmentManager().findFragmentByTag("create_blackout_settings_fragment");
        if (fragment != null) {
            ((BlackoutSettingDetailFragment) fragment).setTime(hour, minute);
        }
    }

    public void notify(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}