package a.gautham.library.models;

import com.google.gson.annotations.SerializedName;

public class AssestsModel {

    @SerializedName("browser_download_url")
    private String download_url;

    @SerializedName("download_count")
    private long download_count;

    @SerializedName("size")
    private long size;

    @SerializedName("name")
    private String assest_name;

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public long getDownload_count() {
        return download_count;
    }

    public void setDownload_count(long download_count) {
        this.download_count = download_count;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getAssest_name() {
        return assest_name;
    }

    public void setAssest_name(String assest_name) {
        this.assest_name = assest_name;
    }
}
