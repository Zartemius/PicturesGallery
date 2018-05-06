package com.example.artem.picturesgallery.activitymainscreen;

import android.app.Application;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.Collections;
import okhttp3.Cache;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Picasso.setSingletonInstance(getCustomPicasso());
    }

    public Picasso getCustomPicasso() {

        // В данном методе настраивается возможность кэширования через Picasso

        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)

                .build();

       File httpCacheDirectory = new File(getCacheDir(), "picasso-cache");
       Cache cache = new Cache(httpCacheDirectory,100*1024*1024);

       OkHttpClient.Builder client = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(spec))
                .cache(cache);

       Picasso customPicasso = new Picasso.Builder(this)
               .downloader(new OkHttp3Downloader(client.build())).build();

       return customPicasso;
    }
}
