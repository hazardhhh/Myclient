package com.example.client.utils.retrofit;


import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.ucstar.android.SDKGlobal;
import com.ucstar.android.SDKSharedPreferences;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
//import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Administrator on 2017/7/25.
 */

public class RetrofitClient {
    public static Retrofit retrofit = null;
    private static String paString = "\\src\\main\\java\\test\\ucstar\\com\\websocketclient\\ca.crt";

    public static Retrofit retrofit(String url) {
        //if (retrofit == null) {
//            CustomTrust ct = new CustomTrust();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        /**
         *设置缓存，代码略
         */

        /**
         *  公共参数，代码略
         */

        /**
         * 设置头，代码略
         */

        /**
         * Log信息拦截器，代码略
         */

        /**
         * 设置cookie，代码略
         */

        /**
         * 设置超时和重连，代码略
         */
        //设置超时
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                LogUtils.d("token", SDKSharedPreferences.getInstance().getAccessToken());
                LogUtils.d("clientType", String.valueOf(SDKGlobal.getSDKOption().clientType));
                LogUtils.d("loginServer", SDKGlobal.getLoginInfo() == null ? "" : SDKGlobal.getLoginInfo().getServer());
                Request request;
                if (!TextUtils.isEmpty(SDKSharedPreferences.getInstance().getAccessToken())) {
                    request = chain.request().newBuilder()
                            .addHeader("token", SDKSharedPreferences.getInstance().getAccessToken())
                            .addHeader("clientType", String.valueOf(SDKGlobal.getSDKOption().clientType))
                            .addHeader("loginServer", SDKGlobal.getLoginInfo() == null ? "" : SDKGlobal.getLoginInfo().getServer())
                            .build();
                    return chain.proceed(request);
                } else {
                    request = chain.request();
                    return chain.proceed(request);
                }
            }
        });

        //以上设置结束，才能build(),不然设置白搭
        /**
         * 设置SSLSocketFactory
         */
        // builder.sslSocketFactory(getSSLSocketFactory(),getTrustManager());
        /**
         * 降低了https的安全性，没办法，不加就报错Hostname  not verified
         */
//            builder.hostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            });

        OkHttpClient okHttpClient = builder.build();
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                //增加返回值为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
                //gson
                .addConverterFactory(GsonConverterFactory.create())
                //增加返回值为Oservable<T>的支持
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        //}
        return retrofit;
    }

    public static Retrofit retrofitNoBase() {
        //if (retrofit == null) {
//            CustomTrust ct = new CustomTrust();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        /**
         *设置缓存，代码略
         */

        /**
         *  公共参数，代码略
         */

        /**
         * 设置头，代码略
         */

        /**
         * Log信息拦截器，代码略
         */

        /**
         * 设置cookie，代码略
         */

        /**
         * 设置超时和重连，代码略
         */
        //设置超时
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);

        //以上设置结束，才能build(),不然设置白搭
        /**
         * 设置SSLSocketFactory
         */
        // builder.sslSocketFactory(getSSLSocketFactory(),getTrustManager());
        /**
         * 降低了https的安全性，没办法，不加就报错Hostname  not verified
         */
//            builder.hostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            });

        OkHttpClient okHttpClient = builder.build();
        retrofit = new Retrofit.Builder()
                //增加返回值为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
                //gson
//                    .addConverterFactory(GsonConverterFactory.create())
                //增加返回值为Oservable<T>的支持
                //.addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        //}
        return retrofit;
    }

    protected static SSLSocketFactory getSSLSocketFactory() {
        SSLContext sslContext = null;
        try {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // randomCA.crt should be in the Assets directory
            InputStream caInput = null;
//            caInput = new BufferedInputStream(FileUtils.getStream(paString));
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            //Create an SSLContext that uses our TrustManager
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslContext.getSocketFactory();
    }


    protected static X509TrustManager getTrustManager() {
        TrustManagerFactory tmf = null;
        try {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // randomCA.crt should be in the Assets directory
            InputStream caInput = null;
//            caInput = new BufferedInputStream(FileUtils.getStream(paString));
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        TrustManager[] trustManagers = tmf.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }


}
