package com.unotrack.device.demo.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.unotrack.device.demo.MainActivity;
import com.unotrack.device.demo.R;

public class NotifycationTools {
    private final static int NOTIFICATION_ID = 10;

    public static void createNotifcation(String messageResId, Context context) {

        Intent targetIntent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivities(context, 0, new Intent[]{targetIntent},
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            mBuilder.setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.bluecolse))
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                    .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                    .setOngoing(false).setDefaults(Notification.DEFAULT_SOUND)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setSmallIcon(R.drawable.beac);
        } else {
            mBuilder.setContentTitle(context.getString(R.string.app_name))
                    .setContentText(messageResId)
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                    .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                    .setOngoing(false).setDefaults(Notification.DEFAULT_SOUND)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setSmallIcon(R.drawable.beac);
        }
        mBuilder.setAutoCancel(true);

        //点击的意图ACTION是跳转到Intent
        mBuilder.setContentIntent(pendingIntent);
        Notification notification = mBuilder.build();
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, notification);
    }

}
