package com.efunhub.ticktok.FirbaseNotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.efunhub.ticktok.R;
import com.efunhub.ticktok.activity.MainActivity;
import com.efunhub.ticktok.fragments.NotificationsFragment;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.v(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

            Log.v(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.v(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {


        Log.d(TAG, "handleNotification: " + "Inside handleNotification");

        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {


            Intent notificationIntent = new Intent(getApplicationContext(), NotificationsFragment.class);

            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            //Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Uri notificationSound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.beep);

            NotificationManager notificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);


            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence nameChannel = getApplicationContext().getString(R.string.app_name);
                String descChannel = getApplicationContext().getString(R.string.app_name);

                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();

                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(getApplicationContext().getString(R.string.app_name), nameChannel, importance);
                channel.setDescription(descChannel);
                channel.setSound(notificationSound, attributes);
                //  channel.setGroup(GROUP_KEY_WORK_EMAIL);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity((getApplicationContext()), 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT|
                            PendingIntent.FLAG_IMMUTABLE);

            // Create Notification
            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(),
                    getApplicationContext().getString(R.string.app_name))
                    .setChannelId(getApplicationContext().getString(R.string.app_name))
                    //.setContentTitle(TextUtils.isEmpty(title) ? getString(R.string.app_name) : title)
                    .setContentText(message)
                    .setTicker(getApplicationContext().getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLights(Color.RED, 3000, 3000)
                    .setVibrate(new long[]{500, 500})
                    .setWhen(System.currentTimeMillis())
                    //.setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                notification.setSound(notificationSound);
            }

            assert notificationManager != null;
            notificationManager.notify(7, notification.build());
        } else {


            Intent notificationIntent = new Intent(getApplicationContext(), NotificationsFragment.class);

            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            Uri notificationSound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.beep);

            // Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationManager notificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);


            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence nameChannel = getApplicationContext().getString(R.string.app_name);
                String descChannel = getApplicationContext().getString(R.string.app_name);
                int importance = NotificationManager.IMPORTANCE_DEFAULT;

                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();

                NotificationChannel channel = new NotificationChannel(getApplicationContext().getString(R.string.app_name), nameChannel, importance);
                channel.setDescription(descChannel);
                //  channel.setGroup(GROUP_KEY_WORK_EMAIL);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this

                channel.setSound(notificationSound, attributes);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);
            }

            PendingIntent pendingIntent = PendingIntent.getActivity((getApplicationContext()), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT|
                    PendingIntent.FLAG_IMMUTABLE);

            // Create Notification
            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(),
                    getApplicationContext().getString(R.string.app_name))
                    .setChannelId(getApplicationContext().getString(R.string.app_name))
                    //.setContentTitle(TextUtils.isEmpty(title) ? getString(R.string.app_name) : title)
                    .setContentText(message)
                    .setTicker(getApplicationContext().getString(R.string.app_name))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    //.setSound(notificationSound)
                    .setLights(Color.RED, 3000, 3000)
                    .setVibrate(new long[]{500, 500})
                    .setWhen(System.currentTimeMillis())

                    //.setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                notification.setSound(notificationSound);
            }

            assert notificationManager != null;
            notificationManager.notify(100, notification.build());

        }
    }

    private void handleDataMessage(JSONObject json) {


        Log.e(TAG, "push json: " + json.toString());

        try {
            //JSONObject data = json.getJSONObject("notification");

            String title = json.getString("title");
            String message = json.getString("body");
System.out.println("Notification title"+title);
System.out.println("Notification body"+message);

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
              /*  Intent pushNotification = new Intent(String.valueOf(PUSH_NOTIFICATION));
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);


                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();*/

                Intent notificationIntent = new Intent(getApplicationContext(), NotificationsFragment.class);

                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                //Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                Uri notificationSound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.beep);

                NotificationManager notificationManager =
                        (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                // Create the NotificationChannel, but only on API 26+ because
                // the NotificationChannel class is new and not in the support library
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence nameChannel = getApplicationContext().getString(R.string.app_name);
                    String descChannel = getApplicationContext().getString(R.string.app_name);
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel(getApplicationContext().getString(R.string.app_name), nameChannel, importance);
                    channel.setDescription(descChannel);

                    //  channel.setGroup(GROUP_KEY_WORK_EMAIL);
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    assert notificationManager != null;
                    notificationManager.createNotificationChannel(channel);
                }

                PendingIntent pendingIntent = PendingIntent.getActivity((getApplicationContext()), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT|
                        PendingIntent.FLAG_IMMUTABLE);

                // Create Notification
                NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(),
                        getApplicationContext().getString(R.string.app_name))
                        .setChannelId(getApplicationContext().getString(R.string.app_name))
                        .setContentTitle(TextUtils.isEmpty(title) ? getString(R.string.app_name) : title)
                        .setContentText(message)
                        .setTicker(getApplicationContext().getString(R.string.app_name))
                        .setSmallIcon(R.mipmap.ic_launcher)                        //.setSound(notificationSound)
                        .setLights(Color.RED, 3000, 3000)
                        .setVibrate(new long[]{500, 500})
                        .setWhen(System.currentTimeMillis())
                        //.setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    notification.setSound(notificationSound);
                }

                assert notificationManager != null;
                notificationManager.notify(100, notification.build());


            } else {
                // app is in background, show the notification in notification tray
               /* Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);

                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();


                showNotificationMessage(getApplicationContext(), title, message, ts, resultIntent);*/


                Intent notificationIntent = new Intent(getApplicationContext(), NotificationsFragment.class);

                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                //Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                Uri notificationSound = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.beep);

                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                // Create the NotificationChannel, but only on API 26+ because
                // the NotificationChannel class is new and not in the support library
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence nameChannel = getApplicationContext().getString(R.string.app_name);
                    String descChannel = getApplicationContext().getString(R.string.app_name);
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel(getApplicationContext().getString(R.string.app_name), nameChannel, importance);
                    channel.setDescription(descChannel);
                    //  channel.setGroup(GROUP_KEY_WORK_EMAIL);

                    AudioAttributes attributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build();

                    channel.setSound(notificationSound, attributes);
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    assert notificationManager != null;
                    notificationManager.createNotificationChannel(channel);
                }

                PendingIntent pendingIntent = PendingIntent.getActivity((getApplicationContext()), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT|
                        PendingIntent.FLAG_IMMUTABLE);

                // Create Notification
                NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(),
                        getApplicationContext().getString(R.string.app_name))
                        .setChannelId(getApplicationContext().getString(R.string.app_name))
                        .setContentTitle(TextUtils.isEmpty(title) ? getString(R.string.app_name) : title)
                        .setContentText(message)
                        .setTicker(getApplicationContext().getString(R.string.app_name))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        //.setSound(notificationSound)
                        .setLights(Color.RED, 3000, 3000)
                        .setVibrate(new long[]{500, 500})
                        .setWhen(System.currentTimeMillis())
                        //.setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    notification.setSound(notificationSound);
                }


                assert notificationManager != null;
                notificationManager.notify(100, notification.build());


                // check for image attachment
                /*if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }*/
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }


}
