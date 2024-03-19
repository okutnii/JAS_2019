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
import com.example.multitool.data.MyDatabaseHelper;
import com.example.multitool.main.MainActivity;

public class PlanAlarmBroadcastReceiver extends BroadcastReceiver {

    MyDatabaseHelper databaseHelper;
    private Bundle extras;
    private String name,descr, title, content;
    private long id, timestamp;
    private int sum, count;




    @Override
    public void onReceive(Context context, Intent intent) {
        databaseHelper = new MyDatabaseHelper(context);

        extras = intent.getExtras();

        assert extras != null;
        name = extras.getString("name");    id = extras.getInt("id");
        descr = extras.getString("descr");  sum = extras.getInt("sum");
        timestamp = extras.getLong("timestamp");

        count = databaseHelper.getPlanCount(name, descr);
        if (count  < 0) return;

        title = name + ": " + descr;

        if(count != sum && sum != 1)
            content = count + "/" + sum + " часу плану \"" + name + "\" вичерпано!";
        else
            content = "Час плану \""  + name + "\" вичерпано!";

        displayNotification(context, title, content);

        setNextNotification(context);
    }

    private void setNextNotification(Context context){
        if (count != sum){

            databaseHelper.setPlanCount(name, descr, ++count);

            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent1 = new Intent(context, PlanAlarmBroadcastReceiver.class);

            intent1.putExtra("name", name).putExtra("descr", descr).putExtra("id", id)
                    .putExtra("sum", sum)
                    .putExtra("timestamp", timestamp);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, (int)(id), intent1,0);

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + timestamp,  pendingIntent);

        }else {
            databaseHelper.setPlanCompleted(name, descr, true);
        }
    }

    private void displayNotification(Context context, String title, String content){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Drawable drawable = context.getDrawable(R.drawable.ic_time_left);



        Resources resources = context.getResources(),
                systemResources = Resources.getSystem();


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "channel_02";
            CharSequence name = "plan_channel";
            String description = "Channel for Plans";
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
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.drawable.ic_important_time)
                    .setAutoCancel(true)
//                    .setLargeIcon(bitmap)
                    .setTicker("MultiTool")

                    .setLights(
                            ContextCompat.getColor(context, systemResources
                                    .getIdentifier("config_defaultNotificationColor", "color", "android")),
                            resources.getInteger(systemResources
                                    .getIdentifier("config_defaultNotificationLedOn", "integer", "android")),
                            resources.getInteger(systemResources
                                    .getIdentifier("config_defaultNotificationLedOff", "integer", "android")))

                    .setVibrate(new long[]{1000,1000,1000,1000,1000})
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

            Intent resultIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(resultPendingIntent);

            notificationManager.notify(666, builder.build());

            return;

        }

        assert drawable != null;
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        Notification note = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_important_time)
                .setAutoCancel(true)
                .setLargeIcon(bitmap)
                .setTicker("Gofer-app")

                .setLights(
                        ContextCompat.getColor(context, systemResources
                                .getIdentifier("config_defaultNotificationColor", "color", "android")),
                        resources.getInteger(systemResources
                                .getIdentifier("config_defaultNotificationLedOn", "integer", "android")),
                        resources.getInteger(systemResources
                                .getIdentifier("config_defaultNotificationLedOff", "integer", "android")))

                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .build();

        Log.d("Triggered", name + ":  " + descr);
        notificationManager.notify(666, note);
    }

}
