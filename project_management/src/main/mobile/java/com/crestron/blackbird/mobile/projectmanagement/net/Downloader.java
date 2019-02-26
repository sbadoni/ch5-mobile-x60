package com.crestron.blackbird.mobile.projectmanagement.net;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.crestron.blackbird.mobile.projectmanagement.net.okhttp.UnsafeOkHttpClient;
import com.crestron.blackbird.mobile.projectmanagement.util.ProjectConstants;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * The logic for downloading projects over the net.
 */
public class Downloader {
    private static final String TAG = Downloader.class.getSimpleName();

    private ApiInterface projectDownloadApi;
    private final Context context;

    public Downloader(Context context) {
        this.context = context;
    }

    /**
     * Perform the network logic of downloading a project from a control system.
     */
    public final Call<ResponseBody> download(Map<String, Object> paramMap) {
        String hostName = String.valueOf(paramMap.get(ProjectConstants.DATA_HOSTNAME));
        String port = String.valueOf(paramMap.get(ProjectConstants.DATA_PORT));
        String projectName = String.valueOf(paramMap.get(ProjectConstants.DATA_PROJECTNAME)).concat(".zip");
        String userName = String.valueOf(paramMap.get(ProjectConstants.DATA_USERNAME));
        String password = String.valueOf(paramMap.get(ProjectConstants.DATA_PASSWORD));
        boolean isUseSSL = (Boolean) paramMap.get(ProjectConstants.DATA_USESSL);

        projectDownloadApi = createRetrofitApi(isUseSSL, hostName, port);

        // This is a secure connection
        if (isUseSSL) {
            String credential = Credentials.basic(userName, password);
            return projectDownloadApi.downloadSecureProjectZip(credential, projectName);

            // This is a non-secure connection
        } else {
            return projectDownloadApi.downloadNonSecureProjectZip(projectName);
        }
    }

    /**
     * Create the endpoint configuration for retrofit api.
     *
     * @param isUseSSL boolean - whether ssl is turned on for this project.
     * @param hostName String.
     * @param port     String.
     * @return {@link ApiInterface} - rest interface for project download api.
     */
    private ApiInterface createRetrofitApi(boolean isUseSSL, String hostName, String port) {
        OkHttpClient httpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient(context);

        // Build the base url with certificate validation (https or http)
        HttpUrl.Builder url = new HttpUrl.Builder();
        if (isUseSSL) {
            url.scheme("https");
        } else {
            url.scheme("http");
        }

        url.host(hostName)
                .port(Integer.parseInt(port));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url.build())
                .client(httpClient)
                .build();
        projectDownloadApi = retrofit.create(ApiInterface.class);

        return projectDownloadApi;
    }

}
