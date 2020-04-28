package a.gautham.library.service;

import a.gautham.library.models.GitRelease;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GithubService {
    @GET("/repos/{username}/{repo}/releases/latest")
    public Call<GitRelease> getReleases(@Path("username") String username, @Path("repo") String repo);

}
