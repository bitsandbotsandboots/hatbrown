package uri.egr.biosensing.anearbeta.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import java.util.ArrayList;

import uri.egr.biosensing.anearbeta.DbHelpers.BlackoutSettingsDbHelper;
import uri.egr.biosensing.anearbeta.R;
import uri.egr.biosensing.anearbeta.ViewBlackoutSettingActivity;
import uri.egr.biosensing.anearbeta.models.BlackoutSettingModel;

/**
 * Created by np on 2/5/2017.
 *
 * The BlackoutSettingDetailFragment class displays the details of a specific Blackout Setting
 * and allows the User to edit the display name, start time, end time, or whether or not it is
 * used for the weekends or weekdays. The User should leave this fragment only by clicking either
 * the save or delete button. This is because if the User accidentally taps the Add Blackout Setting
 * to bring up this fragment, the Setting is already put into the database and must be deleted rather
 * than the User just hitting the back button. Also, if the User makes a change to the Blackout
 * Setting, it does not save automatically, the save button must be pressed for it to take effect.
 */
public class BlackoutSettingDetailFragment extends Fragment {

    private EditText mDisplayNameEditText;
    private Button mStartTimeButton, mEndTimeButton;
    private Switch mWeekendSwitch;
    private int mStartHour, mEndHour, mStartMinute, mEndMinute;
    private String mOldDisplayName;
    private boolean isStartTime;

    public static Fragment newInstance() {
        return new BlackoutSettingDetailFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blackout_setting_detail_view, container, false);

        mDisplayNameEditText = (EditText) view.findViewById(R.id.display_name_edittext);
        mStartTimeButton = (Button) view.findViewById(R.id.start_time_button);
        mEndTimeButton = (Button) view.findViewById(R.id.end_time_button);
        Button saveButton = (Button) view.findViewById(R.id.save_button);
        Button deleteButton = (Button) view.findViewById(R.id.delete_button);
        mWeekendSwitch = (Switch) view.findViewById(R.id.weekend_switch);

        final Bundle bundle = getArguments();
        if (bundle == null) {
            getActivity().finish();
            return null;
        } else {
            mOldDisplayName = bundle.getString(ViewBlackoutSettingActivity.EXTRA_DISPLAY_NAME);
            mDisplayNameEditText.setText(mOldDisplayName);
            int rawStartTime = bundle.getInt(ViewBlackoutSettingActivity.EXTRA_START_TIME);
            int rawEndTime = bundle.getInt(ViewBlackoutSettingActivity.EXTRA_END_TIME);
            mStartTimeButton.setText(BlackoutSettingModel.generateTimeString(rawStartTime));
            mEndTimeButton.setText(BlackoutSettingModel.generateTimeString(rawEndTime));
            mWeekendSwitch.setChecked(bundle.getBoolean(ViewBlackoutSettingActivity.EXTRA_WEEKEND));
            mStartHour = BlackoutSettingModel.getHour(rawStartTime);
            mEndHour = BlackoutSettingModel.getHour(rawEndTime);
            mStartMinute = BlackoutSettingModel.getMinute(rawStartTime);
            mEndMinute = BlackoutSettingModel.getMinute(rawEndTime);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlackoutSettingsDbHelper dbHelper = new BlackoutSettingsDbHelper(getActivity());
                dbHelper.deleteSetting(mOldDisplayName);
                dbHelper.close();
                (getActivity()).finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<BlackoutSettingModel> models = new BlackoutSettingsDbHelper(getActivity()).getAllSettings();
                int count = 0;
                int maxCount;
                if (mOldDisplayName.equals(mDisplayNameEditText.getText().toString())) {
                    maxCount = 1;
                } else {
                    maxCount = 0;
                }
                for (BlackoutSettingModel model : models) {
                    if (model.getDisplayName().equals(mDisplayNameEditText.getText().toString())) {
                        if (++count > maxCount) {
                            ((ViewBlackoutSettingActivity) getActivity()).notify("Display Name must be unique");
                            return;
                        }
                    }
                }
                BlackoutSettingModel model = new BlackoutSettingModel(mDisplayNameEditText.getText().toString(), BlackoutSettingModel.generateTime(mStartHour, mStartMinute), BlackoutSettingModel.generateTime(mEndHour, mEndMinute), mWeekendSwitch.isChecked());
                BlackoutSettingsDbHelper dbHelper = new BlackoutSettingsDbHelper(getActivity());
                dbHelper.deleteSetting(mOldDisplayName);
                dbHelper.putSetting(model);
                dbHelper.close();
                getActivity().finish();
            }
        });

        mStartTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartTime = true;
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        mEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartTime = false;
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });

        return view;
    }

    public void setTime(int hour, int minute) {
        String timeString = BlackoutSettingModel.generateTimeString(hour, minute);
        if (isStartTime) {
            mStartHour = hour;
            mStartMinute = minute;
            mStartTimeButton.setText(timeString);
        } else {
            mEndHour = hour;
            mEndMinute = minute;
            mEndTimeButton.setText(timeString);
        }
    }
}