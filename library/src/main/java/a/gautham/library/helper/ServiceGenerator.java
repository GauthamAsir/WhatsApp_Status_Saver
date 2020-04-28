package a.gautham.library.helper;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static final String GITHUB_BASE_URL = "https://api.github.com/";

    public static Retrofit build(){
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(GITHUB_BASE_URL);
        builder.addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        return retrofit;
    }

}
