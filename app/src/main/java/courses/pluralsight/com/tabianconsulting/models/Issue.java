package courses.pluralsight.com.tabianconsulting.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;


@IgnoreExtraProperties
public class Issue implements Parcelable{

    public static final String IN_PROGRESS = "In Progress";
    public static final String DONE = "Done";
    public static final String IDLE = "Idle";
    public static final String HIGH = "High";
    public static final String MEDIUM = "Medium";
    public static final String LOW = "Low";
    public static final String TASK = "Task";
    public static final String BUG = "Bug";

    private String summary;
    private String status; // "In Progress", "Done", "Idle"
    private String description;
    private int priority; // "High" = 3, "Medium" = 2, "Low" = 1
    private String issue_type; // Task, Bug (Will have different icons so they're easily identified in list)
    private @ServerTimestamp Date time_reported;
    private String reporter;
    private String assignee;
    private String issue_id;
    private String project_id;
    //attachments (list of string urls)


    public Issue(String summary, String status, String description, int priority, String issue_type,
                 Date time_reported, String reporter, String assignee, String issue_id, String project_id) {
        this.summary = summary;
        this.status = status;
        this.description = description;
        this.priority = priority;
        this.issue_type = issue_type;
        this.time_reported = time_reported;
        this.reporter = reporter;
        this.assignee = assignee;
        this.issue_id = issue_id;
        this.project_id = project_id;
    }


    public Issue() {
    }

    public String getPriorityString(){
        if(priority == 1){
            return LOW;
        }
        else if(priority == 2){
            return MEDIUM;
        }
        else{
            return HIGH;
        }
    }

    public static int getPriorityInteger(String priority){
        if(priority.equals(LOW)){
            return 1;
        }
        else if(priority.equals(MEDIUM)){
            return 2;
        }
        else{
            return 3;
        }
    }

    protected Issue(Parcel in) {
        summary = in.readString();
        status = in.readString();
        description = in.readString();
        priority = in.readInt();
        issue_type = in.readString();
        reporter = in.readString();
        assignee = in.readString();
        issue_id = in.readString();
        project_id = in.readString();
        time_reported = (Date) in.readSerializable();
    }

    public static final Creator<Issue> CREATOR = new Creator<Issue>() {
        @Override
        public Issue createFromParcel(Parcel in) {
            return new Issue(in);
        }

        @Override
        public Issue[] newArray(int size) {
            return new Issue[size];
        }
    };

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getIssue_id() {
        return issue_id;
    }

    public void setIssue_id(String issue_id) {
        this.issue_id = issue_id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getIssue_type() {
        return issue_type;
    }

    public void setIssue_type(String issue_type) {
        this.issue_type = issue_type;
    }

    public void setTime_reported(Date time_reported) {
        this.time_reported = time_reported;
    }

    public Date getTime_reported() {
        return time_reported;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(summary);
        parcel.writeString(status);
        parcel.writeString(description);
        parcel.writeInt(priority);
        parcel.writeString(issue_type);
        parcel.writeString(reporter);
        parcel.writeString(assignee);
        parcel.writeString(issue_id);
        parcel.writeString(project_id);
        parcel.writeSerializable(time_reported);
    }
}











