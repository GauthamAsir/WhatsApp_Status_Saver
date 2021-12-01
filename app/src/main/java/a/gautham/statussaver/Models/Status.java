package a.gautham.statussaver.Models;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;

public class Status {

    private File file;
    private String title;
    private String path;
    private boolean isVideo;
//    private Bitmap bitmap;
    private Uri fileUri;

    public Status(File file, String title, String path, Uri uri) {
        this.file = file;
        this.title = title;
        this.path = path;
//        this.bitmap = bitmap;
        this.fileUri = uri;
        String MP4 = ".mp4";
        this.isVideo = file.getName().endsWith(MP4);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }
}
