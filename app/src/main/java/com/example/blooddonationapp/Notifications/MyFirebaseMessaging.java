package com.example.blooddonationapp.Notifications;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.blooddonationapp.Notifications.OreoNotification;
import com.example.blooddonationapp.R;
import com.example.blooddonationapp.ReplyActivity;
import com.example.blooddonationapp.StartActivity;
import com.example.blooddonationapp.Utils.FirebaseDatabaseInstance;
import com.example.blooddonationapp.Utils.SharedPreference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "MedHelp";
    Intent resultIntent;
    SharedPreference pref;
    FirebaseDatabaseInstance rootRef;
    String userType;
    String currentUserId;

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sent = remoteMessage.getData().get("sent");
        String user = remoteMessage.getData().get("user");

        Log.e("check-----", sent);

        pref = SharedPreference.getInstance();
        currentUserId = pref.getData(SharedPreference.currentUserId, getApplicationContext());
        userType = pref.getData(SharedPreference.userType, getApplicationContext());

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        rootRef = FirebaseDatabaseInstance.getInstance();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) { // Build.VERSION_CODES.O
            sendOreoNotification(remoteMessage);
        } else {
            sendNotification(remoteMessage);
        }

    }

    private void sendOreoNotification(RemoteMessage remoteMessage) {

        if (!isAppIsInBackground(getApplicationContext())) {
            //foreground app


            Log.e("remoteMessage", remoteMessage.getData().toString());
            String user = remoteMessage.getData().get("user");
            final String icon = remoteMessage.getData().get("icon");
            final String title = remoteMessage.getData().get("title");
            final String body = remoteMessage.getData().get("body");
            String type = remoteMessage.getData().get("type");
            String sent = remoteMessage.getData().get("sent");
            String topicId = remoteMessage.getData().get("topicId");



            if(type.equals("query")){
                resultIntent = new Intent(getApplicationContext(), ReplyActivity.class);
                if(userType.equals("Doctor")||user.equals(currentUserId)){
                    resultIntent.putExtra("allow_reply", "yes");
                }else {
                    resultIntent.putExtra("allow_reply", "no");
                }
                resultIntent.putExtra("msgKey", topicId);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            Random rand = new Random();
            int i = rand.nextInt(1000);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    i, resultIntent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            OreoNotification oreoNotification = new OreoNotification(this);
            Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                    defaultsound); //String.valueOf(R.drawable.logo)


            oreoNotification.getManager().notify(i, builder.build());

        } else {

            Log.e("remoteMessage", remoteMessage.getData().toString());
            final String title = remoteMessage.getData().get("title");
            final String body = remoteMessage.getData().get("body");
            String user = remoteMessage.getData().get("user");
            final String icon = remoteMessage.getData().get("icon");
            String type = remoteMessage.getData().get("type");
            String topicId = remoteMessage.getData().get("topicId");
            String sent = remoteMessage.getData().get("sent");

            if(type.equals("query")){
                resultIntent = new Intent(getApplicationContext(), ReplyActivity.class);
                if(userType.equals("Doctor")||user.equals(currentUserId)){
                    resultIntent.putExtra("allow_reply", "yes");
                }else {
                    resultIntent.putExtra("allow_reply", "no");
                }
                resultIntent.putExtra("msgKey", topicId);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            Random rand = new Random();
            int i = rand.nextInt(1000);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    /* Request code */i, resultIntent,
                    PendingIntent.FLAG_ONE_SHOT);
            Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            OreoNotification oreoNotification = new OreoNotification(this);
            Notification.Builder builder = oreoNotification.getOreoNotification(title, body,
                    pendingIntent, defaultsound); //String.valueOf(R.drawable.logo)

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            oreoNotification.getManager().notify(i, builder.build());
        }
    }



    private void makeNotiBackOreo(Intent resultIntent, String body, String title) {
        Random rand = new Random();
        int i = rand.nextInt(1000);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                i /* Request code */, resultIntent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body,
                pendingIntent, defaultsound); //String.valueOf(R.drawable.logo)

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        oreoNotification.getManager().notify(i, builder.build());
    }

    private void makeNotiOreoFore(Intent resultIntent, String title, String body) {

        Random rand = new Random();
        int i = rand.nextInt(1000);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                i /* Request code */, resultIntent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                defaultsound); //String.valueOf(R.drawable.logo)

        oreoNotification.getManager().notify(i, builder.build());
    }

    private void sendNotification(final RemoteMessage remoteMessage) {


        if (!isAppIsInBackground(getApplicationContext())) {
            //foreground app
            Log.e("remoteMessageforeground", remoteMessage.getData().toString());
//            String title = remoteMessage.getNotification().getTitle();
//            String body = remoteMessage.getNotification().getBody();
            final String user = remoteMessage.getData().get("user");
            final String icon = remoteMessage.getData().get("icon");
            final String title = remoteMessage.getData().get("title");
            final String body = remoteMessage.getData().get("body");
            String type = remoteMessage.getData().get("type");
            String topicId = remoteMessage.getData().get("topicId");
            String sent = remoteMessage.getData().get("sent");


            if(type.equals("query")){
                resultIntent = new Intent(getApplicationContext(), ReplyActivity.class);
                if(userType.equals("Doctor")||user.equals(currentUserId)){
                    resultIntent.putExtra("allow_reply", "yes");
                }else {
                    resultIntent.putExtra("allow_reply", "no");
                }
                resultIntent.putExtra("msgKey", topicId);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            Log.i("topicNoti---", "generate");
            Random rand = new Random();
            int i = rand.nextInt(1000);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    i /* Request code */, resultIntent,
                    PendingIntent.FLAG_ONE_SHOT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis()) // check params
                    .setSmallIcon(R.drawable.mkr)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    // .setNumber(10)
                    .setTicker("MedHelp")
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentInfo("Info")
//                        .setFullScreenIntent(pendingIntent, true)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.DEFAULT_ALL);

            notificationManager.notify(i, notificationBuilder.build()); // generating


        } else {
            Log.e("remoteMessagebackground", remoteMessage.getData().toString());
            //  Map data = remoteMessage.getData();
//            String title = data.get("title");
//            String body = data.get("body");
            final String user = remoteMessage.getData().get("user");
//            final String icon=remoteMessage.getData().get("icon");
            final String title = remoteMessage.getData().get("title");
            final String body = remoteMessage.getData().get("body");
            String type = remoteMessage.getData().get("type");
            String topicId = remoteMessage.getData().get("topicId");
            String sent = remoteMessage.getData().get("sent");



            if(type.equals("query")){
                resultIntent = new Intent(getApplicationContext(), ReplyActivity.class);
                if(userType.equals("Doctor")||user.equals(currentUserId)){
                    resultIntent.putExtra("allow_reply", "yes");
                }else {
                    resultIntent.putExtra("allow_reply", "no");
                }
                resultIntent.putExtra("msgKey", topicId);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            Log.i("topicNoti---", "generate");

            Random rand = new Random();
            int i = rand.nextInt(1000);

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    i /* Request code */, resultIntent,
                    PendingIntent.FLAG_ONE_SHOT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.mkr)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setTicker("MedHelp")
                    .setContentTitle(title)
                    .setContentText(body)
//                        .setFullScreenIntent(pendingIntent, true)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.DEFAULT_ALL)
                    .setContentInfo("Info");
            notificationManager.notify(i, notificationBuilder.build());

        }
    }

    private void makeOtherBack(Intent resultIntent, String body, String title) {
        Random rand = new Random();
        int i = rand.nextInt(1000);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                i /* Request code */, resultIntent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.mkr)
                .setStyle(new NotificationCompat.BigTextStyle())
                //   .setNumber(10)
                .setTicker("MedHelp")
                .setContentTitle(title)
                .setContentText(body)
                .setFullScreenIntent(pendingIntent, true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.DEFAULT_ALL)
                .setContentInfo("Info");

        notificationManager.notify(i, notificationBuilder.build());
    }

    private void makeOtherFore(Intent resultIntent, String body, String title) {
        Random rand = new Random();
        int i = rand.nextInt(1000);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                i /* Request code */, resultIntent,
                PendingIntent.FLAG_ONE_SHOT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis()) // check params
                .setSmallIcon(R.drawable.mkr)
                .setStyle(new NotificationCompat.BigTextStyle())
                //.setNumber(10)
                .setTicker("MedHelp")
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info")
                .setFullScreenIntent(pendingIntent, true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.DEFAULT_ALL);

        notificationManager.notify(i, notificationBuilder.build()); // generating
    }
}