package courses.pluralsight.com.tabianconsulting.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.List;



public class Chatroom implements Parcelable{

    private String chatroom_name;
    private String creator_id;
    private String security_level;
    private String chatroom_id;
    private List<ChatMessage> chatroom_messages;
    private List<String> users;

    public Chatroom(String chatroom_name, String creator_id, String security_level,
                    String chatroom_id, List<ChatMessage> chatroom_messages, List<String> users) {
        this.chatroom_name = chatroom_name;
        this.creator_id = creator_id;
        this.security_level = security_level;
        this.chatroom_id = chatroom_id;
        this.chatroom_messages = chatroom_messages;
        this.users = users;
    }

    public Chatroom() {

    }

    protected Chatroom(Parcel in) {
        chatroom_name = in.readString();
        creator_id = in.readString();
        security_level = in.readString();
        chatroom_id = in.readString();
    }

    public static final Creator<Chatroom> CREATOR = new Creator<Chatroom>() {
        @Override
        public Chatroom createFromParcel(Parcel in) {
            return new Chatroom(in);
        }

        @Override
        public Chatroom[] newArray(int size) {
            return new Chatroom[size];
        }
    };

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public static Creator<Chatroom> getCREATOR() {
        return CREATOR;
    }

    public List<ChatMessage> getChatroom_messages() {
        return chatroom_messages;
    }

    public void setChatroom_messages(List<ChatMessage> chatroom_messages) {
        this.chatroom_messages = chatroom_messages;
    }

    public String getChatroom_id() {
        return chatroom_id;
    }

    public void setChatroom_id(String chatroom_id) {
        this.chatroom_id = chatroom_id;
    }

    public String getChatroom_name() {
        return chatroom_name;
    }

    public void setChatroom_name(String chatroom_name) {
        this.chatroom_name = chatroom_name;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getSecurity_level() {
        return security_level;
    }

    public void setSecurity_level(String security_level) {
        this.security_level = security_level;
    }

    @Override
    public String toString() {
        return "Chatroom{" +
                "chatroom_name='" + chatroom_name + '\'' +
                ", creator_id='" + creator_id + '\'' +
                ", security_level='" + security_level + '\'' +
                ", chatroom_id='" + chatroom_id + '\'' +
                ", chatroom_messages=" + chatroom_messages +
                ", users=" + users +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(chatroom_name);
        parcel.writeString(creator_id);
        parcel.writeString(security_level);
        parcel.writeString(chatroom_id);
    }
}
