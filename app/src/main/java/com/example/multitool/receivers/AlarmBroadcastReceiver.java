package com.example.multitool.receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.multitool.R;
import com.example.multitool.main.MainActivity;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Resources resources = context.getResources(),
                systemResources = Resources.getSystem();

        Bundle extras = intent.getExtras();
        String str = extras.getString("description");
        int id = extras.getInt("id");
        boolean voice = extras.getBoolean("voice");



        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intentTo = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra("description", str)
                .putExtra("id", id).putExtra("voice", voice);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, id, intentTo,
                0);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +  7*AlarmManager.INTERVAL_DAY,  pendingIntent);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "channel_01";
            CharSequence name = "scheduler_channel";
            String description = "Channel for Day Scheduler";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Зараз час: " + str)
                    .setSmallIcon(R.drawable.ic_important_time)
//                    .setLargeIcon(bitmap)
                    .setAutoCancel(true)

                    .setLights(
                            ContextCompat.getColor(context, systemResources
                                    .getIdentifier("config_defaultNotificationColor", "color", "android")),
                            resources.getInteger(systemResources
                                    .getIdentifier("config_defaultNotificationLedOn", "integer", "android")),
                            resources.getInteger(systemResources
                                    .getIdentifier("config_defaultNotificationLedOff", "integer", "android")))

                    .setTicker("Gofer-app")
                    .setVibrate(new long[]{1000,1000,1000,1000,1000});


            if(voice)
                builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);


            Intent resultIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(resultPendingIntent);

            notificationManager.notify(555, builder.build());

            return;
        }


        Drawable drawable = context.getDrawable(R.mipmap.ic_launcher);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        if(voice){

            Notification note = new NotificationCompat.Builder(context)
                    .setContentTitle("Зараз час: " + str)
                    .setSmallIcon(R.drawable.ic_important_time)
                    .setLargeIcon(bitmap)
                    .setAutoCancel(true)

                    .setLights(
                            ContextCompat.getColor(context, systemResources
                                    .getIdentifier("config_defaultNotificationColor", "color", "android")),
                            resources.getInteger(systemResources
                                    .getIdentifier("config_defaultNotificationLedOn", "integer", "android")),
                            resources.getInteger(systemResources
                                    .getIdentifier("config_defaultNotificationLedOff", "integer", "android")))

                    .setTicker("Gofer-app")
                    .setVibrate(new long[]{1000,1000,1000,1000,1000})
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .build();

            Log.d("Triggered", str);
            notificationManager.notify(555, note);
        } else{
            Notification note = new NotificationCompat.Builder(context)
                    .setContentTitle("Зараз час: " + str)
                    .setSmallIcon(R.drawable.ic_important_time)
                    .setLargeIcon(bitmap)
                    .setAutoCancel(true)
                    .build();

            Log.d("Triggered", str);
            notificationManager.notify(555, note);
        }


    }
}
