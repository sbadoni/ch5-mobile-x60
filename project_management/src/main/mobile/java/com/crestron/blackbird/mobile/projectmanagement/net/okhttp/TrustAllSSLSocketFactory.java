package com.crestron.blackbird.mobile.projectmanagement.net.okhttp;

import android.util.Log;

import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Custom socket factory for working with NTLM and older cipher suites.
 */
public final class TrustAllSSLSocketFactory implements LayeredSocketFactory {
    final TrustManager[] trustManagers;
    SSLContext sslcontext = null;

    private static final String TAG = TrustAllSSLSocketFactory.class.getSimpleName();

    private javax.net.ssl.SSLSocketFactory socketfactory = null;

    TrustAllSSLSocketFactory() {
        // Create a trust manager that does not validate certificate chains
        trustManagers = new TrustManager[]{new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                Log.i(TAG, "checkClientTrusted() " + authType);
                // do nothing
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateExpiredException, CertificateNotYetValidException {
                for (X509Certificate x509Certificate : chain) {
                    Calendar curCalendar = Calendar.getInstance();

                    if (x509Certificate.getNotAfter() != null) {
                        //System.out.println("Cert not after: " + x509Certificate.getNotAfter().toString());
                        Calendar notAfterCal = Calendar.getInstance();
                        notAfterCal.setTime(x509Certificate.getNotAfter());

                        if (!curCalendar.before(notAfterCal)) {
                            Log.d(TAG, "The certificate expired on " + x509Certificate.getNotAfter());
                            throw new CertificateExpiredException("The certificate expired on " + x509Certificate.getNotAfter());
                        }
                        // otherwise approve
                    } else {
                        Log.d(TAG, "No certificate expiration date was found");
                        throw new CertificateExpiredException("No certificate expiration date was found");
                    }

                    if (x509Certificate.getNotBefore() != null) {
                        Log.d(TAG, "Cert not before: " + x509Certificate.getNotBefore().toString());
                        Calendar notBeforeCal = Calendar.getInstance();
                        notBeforeCal.setTime(x509Certificate.getNotBefore());

                        if (!curCalendar.after(notBeforeCal)) {
                            Log.d(TAG, "The certificate is not valid yet. The valid start date is " + x509Certificate.getNotBefore());
                            throw new CertificateNotYetValidException("The certificate is not valid yet. The valid start date is " + x509Certificate.getNotBefore());
                        }
                        // otherwise approve
                    } else {
                        Log.d(TAG, "No certificate validity start date was found");
                        throw new CertificateNotYetValidException("No certificate validity start date was found");
                    }

                }


                // do nothing
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

        }};

        // Init the ssl context
        try {
            sslcontext = SSLContext.getInstance(SSLSocketFactory.TLS);
            sslcontext.init(null, trustManagers, new SecureRandom());
            this.socketfactory = sslcontext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            Log.e(TAG, "Failed to instantiate TrustAllSSLSocketFactory!", e);
        }
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return this.socketfactory.createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket connectSocket(Socket sock, String host, int port, InetAddress localAddress,
                                int localPort, HttpParams params) throws IOException, java.net.SocketException {
        if (host == null) {
            throw new IllegalArgumentException("Target host may not be null.");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null.");
        }

        SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());
        // Let's query the currently enabled cipher suites and add the one common cipher suite that hasn't been deprecated.
        // The hope is that newer control systems won't be as limited...
        // See the following Redmine link for cipher suite details:
        // https://sp-redmine.willturn.org/projects/crestron-app-for-android/wiki/Wiki#Cipher-Suites-on-the-3-series-processors-Web-Server
        List<String> cipherSuitesToEnable = new ArrayList<>(Arrays.asList(sslsock.getEnabledCipherSuites()));
        cipherSuitesToEnable.add("SSL_RSA_WITH_3DES_EDE_CBC_SHA");
        sslsock.setEnabledCipherSuites(cipherSuitesToEnable.toArray(new String[cipherSuitesToEnable.size()]));

        if ((localAddress != null) || (localPort > 0)) {

            // we need to bind explicitly
            if (localPort < 0) {
                localPort = 0; // indicates "any"
            }

            InetSocketAddress isa = new InetSocketAddress(localAddress, localPort);
            sslsock.bind(isa);
        }

        int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
        int soTimeout = HttpConnectionParams.getSoTimeout(params);

        InetSocketAddress remoteAddress;
        remoteAddress = new InetSocketAddress(host, port);

        sslsock.connect(remoteAddress, connTimeout);

        sslsock.setSoTimeout(soTimeout);

        return sslsock;
    }

    public Socket createSocket(HttpParams params) throws IOException {
        // the cast makes sure that the factory is working as expected
        return this.socketfactory.createSocket();
    }

    @Override
    public Socket createSocket() throws IOException {
        // the cast makes sure that the factory is working as expected
        return this.socketfactory.createSocket();
    }

    /**
     * @param sock
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public boolean isSecure(Socket sock) {
        return true;
    }

}
