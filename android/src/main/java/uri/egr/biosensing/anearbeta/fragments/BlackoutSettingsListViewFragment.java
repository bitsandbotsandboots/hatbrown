package uri.egr.biosensing.anearbeta.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import uri.egr.biosensing.anearbeta.DbHelpers.BlackoutSettingsDbHelper;
import uri.egr.biosensing.anearbeta.R;
import uri.egr.biosensing.anearbeta.ViewBlackoutSettingActivity;
import uri.egr.biosensing.anearbeta.adapters.BlackoutSettingAdapter;
import uri.egr.biosensing.anearbeta.models.BlackoutSettingModel;

/**
 * Created by np on 2/5/2017.
 *
 * The BlackoutSettingsListViewFragment class using the BlackoutSettingAdapter to display all of the
 * currently saved Blackout Settings in a ListView using a BlackoutSettingView to display each
 * Blackout Setting. The ListView is updated during the onResume portion of the Fragment's lifecycle.
 */
public class BlackoutSettingsListViewFragment extends Fragment {
    private ListView mListView;

    public static Fragment newInstance() {
        return new BlackoutSettingsListViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blackout_settings_listview, container, false);

        Button addButton = (Button) view.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewBlackoutSettingActivity.start(getActivity());
            }
        });

        mListView = (ListView) view.findViewById(R.id.listview);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BlackoutSettingModel settingModel = (BlackoutSettingModel) mListView.getAdapter().getItem(position);
                ViewBlackoutSettingActivity.start(getActivity(), settingModel);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<BlackoutSettingModel> blackoutSettingsList = new BlackoutSettingsDbHelper(getActivity()).getAllSettings();
        if (blackoutSettingsList != null) {
            mListView.setAdapter(new BlackoutSettingAdapter(getActivity(), blackoutSettingsList));
        }
    }
}