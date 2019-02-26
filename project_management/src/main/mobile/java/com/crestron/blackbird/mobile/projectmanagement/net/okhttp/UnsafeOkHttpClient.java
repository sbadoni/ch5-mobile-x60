package com.crestron.blackbird.mobile.projectmanagement.net.okhttp;

import android.content.Context;
import android.util.Log;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.crestron.blackbird.mobile.projectmanagement.util.ProjectUtil.CACHE_SIZE;
import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

public class UnsafeOkHttpClient {
    private static final String TAG = UnsafeOkHttpClient.class.getSimpleName();

    public static OkHttpClient getUnsafeOkHttpClient(Context context) {
        try {
            // Install the all-trusting trust manager
            TrustAllSSLSocketFactory trustAllSSLSocketFactory = new TrustAllSSLSocketFactory();

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory factory = trustAllSSLSocketFactory.sslcontext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            X509TrustManager x509TrustManager = (X509TrustManager) trustAllSSLSocketFactory.trustManagers[0];
            builder.sslSocketFactory(factory, x509TrustManager);

            // Authenticate
            builder.hostnameVerifier((hostname, session) -> true);

            // Cache etags
            Cache cache = new Cache(context.getCacheDir(), CACHE_SIZE);
            builder.cache(cache);

            // Log intercepted request/response
            builder.addNetworkInterceptor(getLoggingInterceptor());

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * An OkHttp {@link Interceptor} which logs request/response body.
     *
     * @return logging interceptor
     */
    private static Interceptor getLoggingInterceptor() {
        return loggingInterceptor;
    }

    private static final HttpLoggingInterceptor loggingInterceptor =
            new HttpLoggingInterceptor((msg) -> {
                Log.i(TAG, msg);
            }).setLevel(BODY);

}
