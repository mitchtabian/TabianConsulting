package courses.pluralsight.com.tabianconsulting.utility;

import java.util.Map;

import courses.pluralsight.com.tabianconsulting.models.fcm.FirebaseCloudMessage;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * Created by User on 10/26/2017.
 */

public interface FCM {

    @POST("send")
    Call<ResponseBody> send(
      @HeaderMap Map<String, String> headers,
      @Body FirebaseCloudMessage message
    );
}
