package courses.pluralsight.com.tabianconsulting.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by User on 4/16/2018.
 */

@IgnoreExtraProperties
public class Project implements Parcelable {

    private String name;
    private String description;
    private String creator;
    private @ServerTimestamp Date time_created;
    private String avatar;
    private String project_id;

    public Project(String name, String description, String creator, Date time_created, String avatar, String project_id) {
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.time_created = time_created;
        this.avatar = avatar;
        this.project_id = project_id;
    }

    public Project() {

    }

    protected Project(Parcel in) {
        name = in.readString();
        description = in.readString();
        creator = in.readString();
        avatar = in.readString();
        project_id = in.readString();
        time_created = (Date) in.readSerializable();
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public String getProject_id() {
        return project_id;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getTime_created() {
        return time_created;
    }

    public void setTime_created(Date time_created) {
        this.time_created = time_created;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(creator);
        parcel.writeString(avatar);
        parcel.writeString(project_id);
        parcel.writeSerializable(time_created);
    }
}
