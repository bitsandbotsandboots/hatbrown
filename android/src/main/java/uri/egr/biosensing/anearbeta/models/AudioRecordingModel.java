package uri.egr.biosensing.anearbeta.models;

/**
 * Created by np on 2/5/2017.
 */

public class AudioRecordingModel {
    private String mStartTime, mEndTime, mStartDate, mEndDate;
    private boolean mTriggered;
    private long mFileLength;

    public AudioRecordingModel() {

    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String startTime) {
        mStartTime = startTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(String endTime) {
        mEndTime = endTime;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public void setStartDate(String startDate) {
        mStartDate = startDate;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public void setEndDate(String endDate) {
        mEndDate = endDate;
    }

    public boolean isTriggered() {
        return mTriggered;
    }

    public void setTriggered(boolean triggered) {
        mTriggered = triggered;
    }

    public long getFileLength() {
        return mFileLength;
    }

    public void setFileLength(long fileLength) {
        mFileLength = fileLength;
    }

    public String create() {
        return mStartDate + "," + mStartTime + "," + mEndDate + "," + mEndTime + "," + String.valueOf(mTriggered) + "," + String.valueOf(mFileLength);
    }
}
