package courses.pluralsight.com.tabianconsulting.models.fcm;

/**
 * Created by User on 10/26/2017.
 */

public class Data {

    private String title;
    private String message;
    private String data_type;


    public Data(String title, String message, String data_type) {
        this.title = title;
        this.message = message;
        this.data_type = data_type;
    }

    public Data() {

    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Data{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
