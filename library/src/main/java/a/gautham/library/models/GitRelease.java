package a.gautham.library.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GitRelease {

    @SerializedName("name")
    private String release_title;

    @SerializedName("body")
    private String release_description;

    @SerializedName("draft")
    private boolean draft;

    @SerializedName("prerelease")
    private boolean prerelease;

    @SerializedName("tag_name")
    private String release_tag_name;

    @SerializedName("assets_url")
    private String assets_url;

    @SerializedName("assets")
    private List<AssestsModel> assestsModels;

    public List<AssestsModel> getAssestsModels() {
        return assestsModels;
    }

    public void setAssestsModels(List<AssestsModel> assestsModels) {
        this.assestsModels = assestsModels;
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

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public boolean isPrerelease() {
        return prerelease;
    }

    public void setPrerelease(boolean prerelease) {
        this.prerelease = prerelease;
    }

    public String getRelease_tag_name() {
        return release_tag_name;
    }

    public void setRelease_tag_name(String release_tag_name) {
        this.release_tag_name = release_tag_name;
    }

    public String getAssets_url() {
        return assets_url;
    }

    public void setAssets_url(String assets_url) {
        this.assets_url = assets_url;
    }
}
