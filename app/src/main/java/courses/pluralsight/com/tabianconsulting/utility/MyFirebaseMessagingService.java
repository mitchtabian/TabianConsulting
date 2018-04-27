package courses.pluralsight.com.tabianconsulting.utility;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

import courses.pluralsight.com.tabianconsulting.AdminActivity;
import courses.pluralsight.com.tabianconsulting.ChatActivity;
import courses.pluralsight.com.tabianconsulting.ChatroomActivity;
import courses.pluralsight.com.tabianconsulting.LoginActivity;
import courses.pluralsight.com.tabianconsulting.R;
import courses.pluralsight.com.tabianconsulting.RegisterActivity;
import courses.pluralsight.com.tabianconsulting.SettingsActivity;
import courses.pluralsight.com.tabianconsulting.SignedInActivity;
import courses.pluralsight.com.tabianconsulting.models.Chatroom;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
	private static final int BROADCAST_NOTIFICATION_ID = 1;

	private int mNumPendingMessages = 0;

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

	//	String notificationBody = "";
	//	String notificationTitle = "";
	//	String notificationData = "";
	//	try{
	//	   notificationData = remoteMessage.getData().toString();
	//	   notificationTitle = remoteMessage.getNotification().getTitle();
	//	   notificationBody = remoteMessage.getNotification().getBody();
	//	}catch (NullPointerException e){
	//	   Log.e(TAG, "onMessageReceived: NullPointerException: " + e.getMessage() );
	//	}
	//	Log.d(TAG, "onMessageReceived: data: " + notificationData);
	//	Log.d(TAG, "onMessageReceived: notification body: " + notificationBody);
	//	Log.d(TAG, "onMessageReceived: notification title: " + notificationTitle);

		//init image loader since this will be the first code that executes if they click a notification
		initImageLoader();

		String identifyDataType = remoteMessage.getData().get(getString(R.string.data_type));
		//SITUATION: Application is in foreground then only send priority notificaitons such as an admin notification
		if(isApplicationInForeground()){
			if(identifyDataType.equals(getString(R.string.data_type_admin_broadcast))){
				//build admin broadcast notification
				String title = remoteMessage.getData().get(getString(R.string.data_title));
				String message = remoteMessage.getData().get(getString(R.string.data_message));
				sendBroadcastNotification(title, message);
			}
		}

		//SITUATION: Application is in background or closed
		else if(!isApplicationInForeground()){
			if(identifyDataType.equals(getString(R.string.data_type_admin_broadcast))){
				//build admin broadcast notification
				String title = remoteMessage.getData().get(getString(R.string.data_title));
				String message = remoteMessage.getData().get(getString(R.string.data_message));


				sendBroadcastNotification(title, message);
			}
			else if(identifyDataType.equals(getString(R.string.data_type_chat_message))){
				//build chat message notification
				final String title = remoteMessage.getData().get(getString(R.string.data_title));
				final String message = remoteMessage.getData().get(getString(R.string.data_message));
				final String chatroomId = remoteMessage.getData().get(getString(R.string.data_chatroom_id));

				Log.d(TAG, "onMessageReceived: chatroom id: " + chatroomId);
				Query query = FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbnode_chatrooms))
						.orderByKey()
						.equalTo(chatroomId);

				query.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot dataSnapshot) {

						if(dataSnapshot.getChildren().iterator().hasNext()){
							DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();

							Chatroom chatroom = new Chatroom();
							Map<String, Object> objectMap = (HashMap<String, Object>) snapshot.getValue();

							chatroom.setChatroom_id(objectMap.get(getString(R.string.field_chatroom_id)).toString());
							chatroom.setChatroom_name(objectMap.get(getString(R.string.field_chatroom_name)).toString());
							chatroom.setCreator_id(objectMap.get(getString(R.string.field_creator_id)).toString());
							chatroom.setSecurity_level(objectMap.get(getString(R.string.field_security_level)).toString());

							Log.d(TAG, "onDataChange: chatroom: " + chatroom);

							int numMessagesSeen = Integer.parseInt(snapshot
									.child(getString(R.string.field_users))
									.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
									.child(getString(R.string.field_last_message_seen))
									.getValue().toString());

							int numMessages = (int) snapshot
									.child(getString(R.string.field_chatroom_messages)).getChildrenCount();

							mNumPendingMessages = (numMessages - numMessagesSeen);
							Log.d(TAG, "onDataChange: num pending messages: " + mNumPendingMessages);


							sendChatmessageNotification(title, message, chatroom);
						}

					}

					@Override
					public void onCancelled(DatabaseError databaseError) {

					}
				});


			}
		}
    }


	/**
	 * Build a push notification for a chat message
	 * @param title
	 * @param message
	 */
	private void sendChatmessageNotification(String title, String message, Chatroom chatroom){
		Log.d(TAG, "sendChatmessageNotification: building a chatmessage notification");

		//get the notification id
		int notificationId = buildNotificationId(chatroom.getChatroom_id());

		// Instantiate a Builder object.
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
				getString(R.string.default_notification_channel_name));
		// Creates an Intent for the Activity
		Intent pendingIntent = new Intent(this, SignedInActivity.class);
		// Sets the Activity to start in a new, empty task
		pendingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		pendingIntent.putExtra(getString(R.string.intent_chatroom), chatroom);
		// Creates the PendingIntent
		PendingIntent notifyPendingIntent =
				PendingIntent.getActivity(
						this,
						0,
						pendingIntent,
						PendingIntent.FLAG_UPDATE_CURRENT
				);

		//add properties to the builder
		builder.setSmallIcon(R.drawable.tabian_consulting_logo)
				.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
						R.drawable.tabian_consulting_logo))
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setContentTitle(title)
				.setContentText("New messages in " + chatroom.getChatroom_name())
				.setColor(getColor(R.color.blue4))
				.setAutoCancel(true)
				.setSubText(message)
				.setStyle(new NotificationCompat.BigTextStyle()
						.bigText("New messages in " + chatroom.getChatroom_name()).setSummaryText(message))
				.setNumber(mNumPendingMessages)
//				.setOnlyAlertOnce(true)
		;

		builder.setContentIntent(notifyPendingIntent);
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		mNotificationManager.notify(notificationId, builder.build());

	}


	/**
	 * Build a push notification for an Admin Broadcast
	 * @param title
	 * @param message
	 */
	private void sendBroadcastNotification(String title, String message){
		Log.d(TAG, "sendBroadcastNotification: building an admin broadcast notification");

		// Instantiate a Builder object.
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
				getString(R.string.default_notification_channel_name));
		// Creates an Intent for the Activity
		Intent notifyIntent = new Intent(this, SignedInActivity.class);
		// Sets the Activity to start in a new, empty task
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		// Creates the PendingIntent
		PendingIntent notifyPendingIntent =
				PendingIntent.getActivity(
						this,
						0,
						notifyIntent,
						PendingIntent.FLAG_UPDATE_CURRENT
				);

		//add properties to the builder
		builder.setSmallIcon(R.drawable.tabian_consulting_logo)
				.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
						R.drawable.tabian_consulting_logo))
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setContentTitle(title)
				.setContentText(message)
				.setColor(getColor(R.color.blue4))
				.setAutoCancel(true);

		builder.setContentIntent(notifyPendingIntent);
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		mNotificationManager.notify(BROADCAST_NOTIFICATION_ID, builder.build());
	}

	private int buildNotificationId(String id){
		Log.d(TAG, "buildNotificationId: building a notification id.");

		int notificationId = 0;
		for(int i = 0; i < 9; i++){
			notificationId = notificationId + id.charAt(0);
		}
		Log.d(TAG, "buildNotificationId: id: " + id);
		Log.d(TAG, "buildNotificationId: notification id:" + notificationId);
		return notificationId;
	}

	private boolean isApplicationInForeground(){
		//check all the activities to see if any of them are running
		boolean isActivityRunning = SignedInActivity.isActivityRunning
		|| ChatActivity.isActivityRunning || AdminActivity.isActivityRunning
				|| ChatroomActivity.isActivityRunning || LoginActivity.isActivityRunning
				|| RegisterActivity.isActivityRunning || SettingsActivity.isActivityRunning;
		if(isActivityRunning) {
			Log.d(TAG, "isApplicationInForeground: application is in foreground.");
			return true;
		}
		Log.d(TAG, "isApplicationInForeground: application is in background or closed.");    return false;
	}

	/**
	 * init universal image loader
	 */
	private void initImageLoader(){
		UniversalImageLoader imageLoader = new UniversalImageLoader(this);
		ImageLoader.getInstance().init(imageLoader.getConfig());
	}


}
