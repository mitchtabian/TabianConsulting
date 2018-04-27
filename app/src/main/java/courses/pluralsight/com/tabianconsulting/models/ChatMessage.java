package courses.pluralsight.com.tabianconsulting.models;


public class ChatMessage {

    private String message;
    private String user_id;
    private String timestamp;
    private String profile_image;
    private String name;

    public ChatMessage(String message, String user_id, String timestamp, String profile_image, String name) {
        this.message = message;
        this.user_id = user_id;
        this.timestamp = timestamp;
        this.profile_image = profile_image;
        this.name = name;
    }

    public ChatMessage() {

    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "message='" + message + '\'' +
                ", user_id='" + user_id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", profile_image='" + profile_image + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
