package uri.egr.biosensing.anearbeta.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import uri.egr.biosensing.anearbeta.BlackoutSettingView;
import uri.egr.biosensing.anearbeta.models.BlackoutSettingModel;

/**
 * Created by np on 2/4/2017.
 */

public class BlackoutSettingAdapter extends ArrayAdapter {
    public BlackoutSettingAdapter(Context c, List<BlackoutSettingModel> settings) {
        super(c, 0, settings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BlackoutSettingView settingView = (BlackoutSettingView) convertView;
        if (settingView == null) {
            settingView = BlackoutSettingView.inflate(parent);
        }
        settingView.setItem((BlackoutSettingModel) getItem(position));
        return settingView;
    }
}