package a.gautham.statussaver.Utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.OutputStream;

import a.gautham.statussaver.Models.Status;

public class SingleMediaScanner {

    private static final String TAG = "Media Scanner Client: ";

    public SingleMediaScanner(Context context, @Nullable File f, @Nullable Status status, @Nullable String fileName, @Nullable Bitmap bitmap) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            if(status == null || fileName == null) {
                Log.e(TAG, "No Filename/Bitmap Provided");
                return;
            }

            // To Notify Media Scanner
            Uri url = null;

            ContentResolver cr = context.getContentResolver();

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.DESCRIPTION, fileName);

            if (status.isVideo()) {
                values.put(MediaStore.Images.Media.MIME_TYPE, "video/mp4");
            } else {
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            }
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

            try{

                if(status.isVideo()) {
                    url = cr.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                }

                if (bitmap != null) {
                    try (OutputStream imageOut = cr.openOutputStream(url)) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                    }

                } else {
                    cr.delete(url, null, null);
                }

                cr.delete(url, null, null);

            }catch (Exception e) {
                if (url != null) {
                    cr.delete(url, null, null);
                }

                e.printStackTrace();
            }

            return;
        }

        if(f == null) {
            Log.e(TAG, "No File Provided");
            return;
        }

        MediaScannerConnection.scanFile(context, new String[]{f.getAbsolutePath()},
                new String[]{"image/jpeg", "videos/mp4"}, (path, uri) -> {
                    if (uri == null) {
                        throw new IllegalStateException("media scan failed...");
                    } else {
                        Log.i(TAG, "Success");
                    }
                });
    }

}