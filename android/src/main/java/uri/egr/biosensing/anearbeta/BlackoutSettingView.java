package uri.egr.biosensing.anearbeta;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import uri.egr.biosensing.anearbeta.models.BlackoutSettingModel;

/**
 * Created by np on 2/5/2016.
 *
 */
public class BlackoutSettingView extends RelativeLayout {
    private TextView mDisplayNameTextview;
    private TextView mTimeTextView;
    private TextView mWeekends;

    public static BlackoutSettingView inflate(ViewGroup parent) {
        return (BlackoutSettingView) LayoutInflater.from(parent.getContext()).inflate(R.layout.view_blackout_setting_model, parent, false);
    }

    public BlackoutSettingView(Context c) {
        this(c, null);
    }

    public BlackoutSettingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlackoutSettingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.fragment_blackout_setting_children, this, true);
        setupChildren();
    }

    private void setupChildren() {
        mDisplayNameTextview = (TextView) findViewById(R.id.displayname_textview);
        mTimeTextView = (TextView) findViewById(R.id.time_textview);
        mWeekends = (TextView) findViewById(R.id.weekend_textview);
    }

    public void setItem(BlackoutSettingModel setting) {
        mDisplayNameTextview.setText(setting.getDisplayName());
        String timeString = "(" + setting.getStartTime() + " - " + setting.getEndTime() + ")";
        mTimeTextView.setText(timeString);
        if (setting.isWeekends()) {
            mWeekends.setText(R.string.b_settings_weekend_string);
        } else {
            mWeekends.setText(R.string.b_settings_weekday_string);
        }
    }
}