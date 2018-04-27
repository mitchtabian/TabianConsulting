package courses.pluralsight.com.tabianconsulting.models;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Comment {

    private String comment;
    private String user_id;
    private @ServerTimestamp Date timestamp;

    public Comment(String comment, String user_id, Date timestamp) {
        this.comment = comment;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public Comment() {

    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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














