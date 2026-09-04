package com.teamlalli61.imdone;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ChoreNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        if (title == null) title = "your chore";
        Intent open = new Intent(context, MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(context, 0, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder builder = Build.VERSION.SDK_INT >= 26
                ? new Notification.Builder(context, "chores")
                : new Notification.Builder(context).setPriority(Notification.PRIORITY_HIGH);
        Notification notification = builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("I'M DONE! ⭐")
                .setContentText("Time to do " + title)
                .setAutoCancel(true)
                .setContentIntent(pending)
                .setVibrate(new long[]{0, 180, 100, 180})
                .build();
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify((title + System.currentTimeMillis() / 300000).hashCode() & 0x7fffffff,
                        notification);
    }
}
