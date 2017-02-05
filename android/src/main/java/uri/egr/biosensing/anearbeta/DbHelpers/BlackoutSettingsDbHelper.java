package uri.egr.biosensing.anearbeta.DbHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import uri.egr.biosensing.anearbeta.contracts.BlackoutSettingsContract;
import uri.egr.biosensing.anearbeta.models.BlackoutSettingModel;

/**
 * Created by np on 2/4/2017.
 *
 * The BlackoutSettingsDbHelper class is responsible for all database related tasks. It provides
 * methods for creating the blackout_settings_table, upgrading and downgrading the database,
 * adding a Blackout Setting to the table, removing a Blackout Setting from the table, and returning
 * an ArrayList of all the Blackout Settings currently stored in the database.
 */
public class BlackoutSettingsDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "BlackoutSettingsTable.db";

    private static final String TEXT_TYPE = " TEXT(50)";
    private static final String INT_TYPE = " INT(2047)";
    private static final String BOOLEAN_TYPE = " INT(1)";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + BlackoutSettingsContract.Setting.TABLE_NAME + " (" +
                    BlackoutSettingsContract.Setting._ID + " INTEGER PRIMARY KEY," +
                    BlackoutSettingsContract.Setting.COLUMN_NAME_DISPLAY_NAME + TEXT_TYPE + COMMA_SEP +
                    BlackoutSettingsContract.Setting.COLUMN_NAME_START_TIME + INT_TYPE + COMMA_SEP +
                    BlackoutSettingsContract.Setting.COLUMN_NAME_END_TIME + INT_TYPE + COMMA_SEP +
                    BlackoutSettingsContract.Setting.COLUMN_NAME_WEEKEND + BOOLEAN_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + BlackoutSettingsContract.Setting.TABLE_NAME;

    public BlackoutSettingsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        //Default Settings:
        //  Weekdays:
        //      11:00pm - 11:59pm
        //      12:00am -  9:00am
        //  Weekends:
        //      12:00am -  9:00am
        List<BlackoutSettingModel> models = new ArrayList<>();
        models.add(new BlackoutSettingModel("Weekday Blackout Setting 1", BlackoutSettingModel.generateTime(23, 0), BlackoutSettingModel.generateTime(23, 59), false));
        models.add(new BlackoutSettingModel("Weekday Blackout Setting 2", BlackoutSettingModel.generateTime(0,0), BlackoutSettingModel.generateTime(9,0), false));
        models.add(new BlackoutSettingModel("Weekend Blackout Setting", BlackoutSettingModel.generateTime(0,0), BlackoutSettingModel.generateTime(9,0), true));

        //Add default Blackout Settings to table
        for (BlackoutSettingModel model : models) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BlackoutSettingsContract.Setting.COLUMN_NAME_DISPLAY_NAME, model.getDisplayName());
            contentValues.put(BlackoutSettingsContract.Setting.COLUMN_NAME_START_TIME, model.getRawStartTime());
            contentValues.put(BlackoutSettingsContract.Setting.COLUMN_NAME_END_TIME, model.getRawEndTime());
            contentValues.put(BlackoutSettingsContract.Setting.COLUMN_NAME_WEEKEND, model.isWeekends());
            db.insert(BlackoutSettingsContract.Setting.TABLE_NAME, null, contentValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void putSetting(String displayName, int startTime, int endTime, boolean weekend) {
        int weekendInt = weekend ? 1 : 0;

        SQLiteDatabase dbWrite = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BlackoutSettingsContract.Setting.COLUMN_NAME_DISPLAY_NAME, displayName);
        contentValues.put(BlackoutSettingsContract.Setting.COLUMN_NAME_START_TIME, startTime);
        contentValues.put(BlackoutSettingsContract.Setting.COLUMN_NAME_END_TIME, endTime);
        contentValues.put(BlackoutSettingsContract.Setting.COLUMN_NAME_WEEKEND, weekendInt);
        dbWrite.insert(BlackoutSettingsContract.Setting.TABLE_NAME, null, contentValues);
        dbWrite.close();
    }

    public void putSetting(BlackoutSettingModel setting) {
        putSetting(setting.getDisplayName(), setting.getRawStartTime(), setting.getRawEndTime(), setting.isWeekends());
    }

    public void deleteSetting(String displayName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BlackoutSettingsContract.Setting.TABLE_NAME, BlackoutSettingsContract.Setting.COLUMN_NAME_DISPLAY_NAME + " = ? ", new String[] {displayName});
        db.close();
    }

    public ArrayList<BlackoutSettingModel> getAllSettings() {
        SQLiteDatabase dbRead = getReadableDatabase();
        Cursor res =  dbRead.rawQuery( "SELECT * FROM " + BlackoutSettingsContract.Setting.TABLE_NAME, null);
        ArrayList<BlackoutSettingModel> list = new ArrayList<>();
        if (res == null || res.getCount() == 0) {
            return null;
        }
        res.moveToFirst();
        do {
            BlackoutSettingModel setting = new BlackoutSettingModel(res.getString(1), res.getInt(2), res.getInt(3), res.getInt(4));
            list.add(setting);
        } while(res.moveToNext());
        res.close();
        dbRead.close();
        return list;
    }
}