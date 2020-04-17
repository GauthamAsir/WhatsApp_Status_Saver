package a.gautham.statusdownloader.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Random;

import a.gautham.statusdownloader.BuildConfig;
import a.gautham.statusdownloader.Models.Status;
import a.gautham.statusdownloader.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class Common {

    static final int MINI_KIND = 1;
    static final int MICRO_KIND = 3;

    private static final String CHANNEL_NAME = "GAUTHAM";

    public static final int ITEM_VIEW_FLAG = 0;     //Default

    public static final File STATUS_DIRECTORY = new File(Environment.getExternalStorageDirectory() +
            File.separator + "WhatsApp/Media/.Statuses");

    public static String APP_DIR;

    public static void copyFile(Status status, Context context, RelativeLayout container){

        File file = new File(Common.APP_DIR);
        if (!file.exists()){
            file.mkdirs();
        }

        File destFile = new File(file+File.separator + status.getTitle());

        if (destFile.exists()){
            destFile.delete();
        }

        if (!destFile.getParentFile().exists()){
            destFile.getParentFile().mkdirs();
        }

        try {

            InputStream source = new FileInputStream(status.getFile());
            OutputStream destination = new FileOutputStream(destFile);

            byte[] buff = new byte[1024];
            int len;

            while ((len = source.read(buff)) > 0){
                destination.write(buff,0,len);
            }

            showNotification(context, container, destFile, status);
            source.close();
            destination.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void showNotification(Context context, RelativeLayout container, File destFile, Status status){

        // make the channel. The method has been discussed before.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel(CHANNEL_NAME, "Saved", NotificationManager.IMPORTANCE_DEFAULT, context);
        }

        Uri data = FileProvider.getUriForFile(context, "a.gautham.statusdownloader" + ".provider" ,new File(destFile.getAbsolutePath()));
        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (status.isVideo()){
            intent.setDataAndType(data, "video/*");
        }else {
            intent.setDataAndType(data, "image/*");
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(context, CHANNEL_NAME);

        notification.setSmallIcon(R.drawable.ic_file_download_black)
                .setContentTitle(destFile.getName())
                .setContentText("File Saved to"+ APP_DIR)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(new Random().nextInt(), notification.build());

        Snackbar.make(container,"Saved to "+Common.APP_DIR,Snackbar.LENGTH_LONG).show();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void makeNotificationChannel(String id, String name, int importance, Context context) {

        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setShowBadge(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

}
