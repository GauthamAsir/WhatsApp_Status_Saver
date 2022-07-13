package a.gautham.statusdownloader.Models;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.util.Objects;

public class Status {

    private File file;
    private String title;
    private String path;
    private boolean isVideo, isApi30;
    private DocumentFile documentFile;

    public Status(File file, String title, String path) {
        this.file = file;
        this.title = title;
        this.path = path;
        String MP4 = ".mp4";
        this.isApi30 = false;
        this.isVideo = file.getName().endsWith(MP4);
    }

    public Status(DocumentFile documentFile) {
        this.isApi30 = true;
        this.documentFile = documentFile;
        String MP4 = ".mp4";
        this.isVideo = Objects.requireNonNull(documentFile.getName()).endsWith(MP4);
    }

    public DocumentFile getDocumentFile() {
        return documentFile;
    }

    public void setDocumentFile(DocumentFile docFile) {
        this.documentFile = docFile;
    }

    public boolean isApi30() {
        return isApi30;
    }

    public void setApi30(boolean api30) {
        this.isApi30 = api30;
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
}
