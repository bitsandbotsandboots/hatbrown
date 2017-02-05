package uri.egr.biosensing.anearbeta.contracts;

import android.provider.BaseColumns;

/**
 * Created by np on 2/4/2017.
 *
 * The BlackoutSettingsContract is a Contract used by the Application's database which stores
 * all of the Blackout Settings. There are four columns in the blackout_settings_table:
 *
 * Column Name      Type        Length
 * display_name     TEXT        50
 * start_time       INT         2047
 * end_time         INT         2047
 * weekend          INT         1
 *
 * start_time and end_time are binary strings used to store the hour (in 24-hour format) and minute.
 * The first 5 bits (most significant) are used to represent the hour (only decimal 0-23 are used).
 * The remaining 6 bits are used to represent the minutes (only decimal 0-59 are used).
 *
 * weekend is an INT of length 1 since it is actually boolean value (0=false, 1=true).
 */
public class BlackoutSettingsContract {
    public BlackoutSettingsContract() {

    }

    public static abstract class Setting implements BaseColumns {
        public static final String TABLE_NAME = "blackout_settings_table";
        public static final String COLUMN_NAME_DISPLAY_NAME = "display_name";
        public static final String COLUMN_NAME_START_TIME = "start_time";
        public static final String COLUMN_NAME_END_TIME = "end_time";
        public static final String COLUMN_NAME_WEEKEND = "weekend";
    }
}