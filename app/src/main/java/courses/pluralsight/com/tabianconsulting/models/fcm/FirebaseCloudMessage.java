package courses.pluralsight.com.tabianconsulting.models.fcm;

/**
 * Created by User on 10/26/2017.
 */

public class FirebaseCloudMessage {

    private String to;
    private Data data;

    public FirebaseCloudMessage(String to, Data data) {
        this.to = to;
        this.data = data;
    }

    public FirebaseCloudMessage() {

    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }


    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FirebaseCloudMessage{" +
                "to='" + to + '\'' +
                ", data=" + data +
                '}';
    }
}
