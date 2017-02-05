package uri.egr.biosensing.anearbeta.models;

import java.util.Calendar;

/**
 * Created by np on 2/4/2017.
 *
 */
public class BlackoutSettingModel {
    private String mDisplayName;
    private int mStartTime, mEndTime;
    private int mWeekends;

    public static int generateTime(int hour, int minutes) {
        return ((hour&31)<<6) + minutes;
    }

    public static int generateTime(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        return generateTime(hour, minutes);
    }

    public static String generateTimeString(int hour, int minutes) {
        String am;
        String minutesString;
        am = (hour > 12) ? "PM" : "AM";
        hour %= 12;
        if (hour == 0) {
            hour = 12;
        }
        if (minutes < 10) {
            minutesString = "0"+minutes;
        } else {
            minutesString = "" + minutes;
        }
        return hour + ":" + minutesString + " " + am;
    }

    public static String generateTimeString(int time) {
        return generateTimeString(getHour(time), getMinute(time));
    }

    public static int getHour(int time) {
        return ((time&1984)>>>6);
    }

    public static int getMinute(int time) {
        return (time&63);
    }

    public BlackoutSettingModel(String displayName, int startTime, int endTime, boolean weekends) {
        mDisplayName = displayName;
        mStartTime = startTime;
        mEndTime = endTime;
        mWeekends = weekends ? 1 : 0;
    }

    public BlackoutSettingModel(String displayName, int startTime, int endTime, int weekends) {
        mDisplayName = displayName;
        mStartTime = startTime;
        mEndTime = endTime;
        mWeekends = weekends;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getStartTime() {
        return generateTimeString(mStartTime);
    }

    public String getEndTime() {
        return generateTimeString(mEndTime);
    }

    public int getRawStartTime() {
        return mStartTime;
    }

    public int getRawEndTime() {
        return mEndTime;
    }

    public boolean isWeekends() {
        return mWeekends == 1;
    }

    /**
     * The boolean variable runOver tracks whether the end time runs over to the next day. If that
     * is the case, the start and end times are switched and the result is inverted. Since the
     * variable runOver will always be opposite depending on if the blackout runs over into the
     * next day or not, we can use that as a return value
     * @return
     */
    public boolean inBlackoutTime() {
        Calendar calendar   = Calendar.getInstance();
        int currentDay      = calendar.get(Calendar.DAY_OF_WEEK);
        int currentHour     = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute   = calendar.get(Calendar.MINUTE);
        int startHour       = getHour(mStartTime);
        int startMinute     = getMinute(mStartTime);
        int endHour         = getHour(mEndTime);
        int endMinute       = getMinute(mEndTime);
        boolean runOver = false;

        //First check if days match
        if ((isWeekends() && (currentDay != Calendar.SATURDAY && currentDay != Calendar.SUNDAY)) || (!isWeekends() && (currentDay == Calendar.SATURDAY || currentDay == Calendar.SUNDAY))) {
            return false;
        }

        if (endHour < startHour) {
            //Blackout runs into next day
            runOver = true;
            //Switch start and end times
            int tempHour, tempMinute;
            tempHour = startHour;
            tempMinute = startMinute;
            startHour = endHour;
            startMinute = endMinute;
            endHour = tempHour;
            endMinute = tempMinute;
        }

        //Check if before start time
        if (currentHour < startHour) {
            //Time before start time
            return runOver;
        } else if (currentHour == startHour) {
            if (currentMinute < startMinute) {
                //Time before start time
                return runOver;
            }
        }

        //Time after start, check if after end
        if (currentHour > endHour) {
            //Time after end time
            return runOver;
        } else if (currentHour == endHour) {
            if (currentMinute > endMinute) {
                //Time after end time
                return runOver;
            }
        }

        //Time is either equal to either start or end time, or between start and end time
        return !runOver;
    }
}