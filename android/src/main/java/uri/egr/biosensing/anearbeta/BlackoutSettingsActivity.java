package uri.egr.biosensing.anearbeta;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import uri.egr.biosensing.anearbeta.fragments.BlackoutSettingsListViewFragment;

/**
 * Created by np on 2/5/2016.
 *
 */
public class BlackoutSettingsActivity extends AppCompatActivity {

    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, BlackoutSettingsListViewFragment.newInstance(), "blackout_settings_fragment")
                .commit();
    }

    public void update() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, BlackoutSettingsListViewFragment.newInstance(), "blackout_settings_fragment")
                .commit();
    }

    public void notify(String message) {
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}