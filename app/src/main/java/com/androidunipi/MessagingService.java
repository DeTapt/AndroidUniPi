package com.androidunipi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.androidunipi.data.NotificationObj;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Tasos on 24/10/2016.
 */

public class MessagingService extends FirebaseMessagingService {

    protected CommonData common;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        common = CommonData.getInstance();
        if (remoteMessage.getData().size() > 0) {
        }
        if (remoteMessage.getNotification() != null) {
        }
        //retrieve notification list , if exists
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = sharedPrefs.getString("Notifications", null);
        ArrayList<NotificationObj> arrayList;
        if(json == null) {
            arrayList = new ArrayList<>();
        }
        else{
            Type type = new TypeToken<ArrayList<NotificationObj>>() {}.getType();
            arrayList = gson.fromJson(json, type);
        }

        //make the notification
        NotificationObj currNotification = new NotificationObj();
        currNotification.setId(arrayList.size() == 0 ? 0 : arrayList.size()+1);
        currNotification.setUserId(0);
        currNotification.setEmail(remoteMessage.getData().get("email"));
        currNotification.setDate(remoteMessage.getData().get("date"));
        currNotification.setTime(remoteMessage.getData().get("time"));
        currNotification.setUserName(remoteMessage.getData().get("name"));
        currNotification.setSurName(remoteMessage.getData().get("surname"));
        currNotification.setUserPhoto(remoteMessage.getData().get("img"));
        currNotification.setMessage(remoteMessage.getNotification().getBody());
        currNotification.setRead(false);
        if(remoteMessage.getData().get("chat_token") != null){
            currNotification.setChat_token(remoteMessage.getData().get("chat_token"));
            currNotification.setChat(true);
        }
        else{
            currNotification.setChat(false);
        }

        arrayList.add(0, currNotification);

        common.tvBadge.setText(""+arrayList.size());

        //save notification list
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gsonSave = new Gson();
        String jsonSave = gsonSave.toJson(arrayList);
        editor.putString("Notifications", jsonSave);
        editor.apply();

        if(!common.isChatVisible) {
            Intent intent = new Intent(this, Home.class);
            intent.putExtra("ID", currNotification);
            PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            Notification notification = builder.setContentTitle("BattBook")
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSmallIcon(R.drawable.app_bat_icon)
                    .setContentIntent(pIntent).build();

            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
        }
    }
}
