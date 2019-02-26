package com.crestron.blackbird.mobile.projectmanagement.net;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * A {@link retrofit2.Retrofit} interface to the project download remote REST API.
 */
public interface ApiInterface {
    @Streaming
    @GET("{project_name}")
    Call<ResponseBody> downloadSecureProjectZip(@Header("Authorization") String authkey, @Path("project_name") String projectName);

    @Streaming
    @GET("{project_name}")
    Call<ResponseBody> downloadNonSecureProjectZip(@Path("project_name") String projectName);
}
