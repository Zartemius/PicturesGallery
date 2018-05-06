package com.example.artem.picturesgallery.rest;

/*
*Через объект данного класса осуществляется обращение к API сайта "https://pixabay.com/".
*Для этой цели используется библиотека Retrofit.
*/

import com.example.artem.picturesgallery.activitymainscreen.ActivityMainScreen;
import java.util.Collections;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Downloader {

    private final static String BASE_URL ="https://pixabay.com/";
    private CompositeDisposable mCompositeDisposable;
    private ActivityMainScreen mActivityMainScreen;
    private static final String KEY = "8882826-8261d1930e33564d06a3dca67";
    private static final String SEARCH_TARGET = "baikal";
    private static final int QUANTITY_OF_ITEMS_RECEIVED = 66;

    public Downloader(ActivityMainScreen activityMainScreen){
        mCompositeDisposable = new CompositeDisposable();
        mActivityMainScreen = activityMainScreen;
    }

    public void unsubscribe(){
        mCompositeDisposable.clear();
    }

    public void loadData(){

         /*
         *В connectionSpec добавляется CipherSuites для согласования параметров безопасности между
         *приложением и ресурсом к которому делается запрос.
         */

        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(spec))
                .build();


            ApiInterface apiInterface = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build().create(ApiInterface.class);

            mCompositeDisposable.add(apiInterface.getListOfPictures(KEY,SEARCH_TARGET,
                    QUANTITY_OF_ITEMS_RECEIVED)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(mActivityMainScreen::handleResponse, mActivityMainScreen::handleError));
    }
}
