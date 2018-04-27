package courses.pluralsight.com.tabianconsulting.models;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;


@IgnoreExtraProperties
public class Attachment {

    private String name;
    private String url;
    private String user_id;
    private @ServerTimestamp Date timestamp;

    public Attachment(String name, String url, String user_id, Date timestamp) {
        this.name = name;
        this.url = url;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public Attachment() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
