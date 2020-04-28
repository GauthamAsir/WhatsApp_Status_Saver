package a.gautham.library.models;

public class Update {

    private String downloadUrl, asset_name, release_title, release_description, latest_version;
    private long download_count, asset_size;
    private double asset_sizeKb, asses_sizeMb, asset_Gb;

    public Update(String downloadUrl, String asset_name, String release_title, String release_description, String latest_version, long download_count, long asset_size, double asset_sizeKb, double asses_sizeMb, double asset_Gb) {
        this.downloadUrl = downloadUrl;
        this.asset_name = asset_name;
        this.release_title = release_title;
        this.release_description = release_description;
        this.latest_version = latest_version;
        this.download_count = download_count;
        this.asset_size = asset_size;
        this.asset_sizeKb = asset_sizeKb;
        this.asses_sizeMb = asses_sizeMb;
        this.asset_Gb = asset_Gb;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getAsset_name() {
        return asset_name;
    }

    public void setAsset_name(String asset_name) {
        this.asset_name = asset_name;
    }

    public String getRelease_title() {
        return release_title;
    }

    public void setRelease_title(String release_title) {
        this.release_title = release_title;
    }

    public String getRelease_description() {
        return release_description;
    }

    public void setRelease_description(String release_description) {
        this.release_description = release_description;
    }

    public String getLatest_version() {
        return latest_version;
    }

    public void setLatest_version(String latest_version) {
        this.latest_version = latest_version;
    }

    public long getDownload_count() {
        return download_count;
    }

    public void setDownload_count(long download_count) {
        this.download_count = download_count;
    }

    public long getAsset_size() {
        return asset_size;
    }

    public void setAsset_size(long asset_size) {
        this.asset_size = asset_size;
    }

    public double getAsset_sizeKb() {
        return asset_sizeKb;
    }

    public void setAsset_sizeKb(double asset_sizeKb) {
        this.asset_sizeKb = asset_sizeKb;
    }

    public double getAsses_sizeMb() {
        return asses_sizeMb;
    }

    public void setAsses_sizeMb(double asses_sizeMb) {
        this.asses_sizeMb = asses_sizeMb;
    }

    public double getAsset_Gb() {
        return asset_Gb;
    }

    public void setAsset_Gb(double asset_Gb) {
        this.asset_Gb = asset_Gb;
    }
}
