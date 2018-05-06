package com.example.artem.picturesgallery.activitymainscreen;

/*
*Основное activity в котором показывается основная лента картинок. Через метод initRecyclerView инициализируется
* RecyclerView, а через метод loadData класса Downloader осуществляется загрузка данных туда.
*/

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
import com.example.artem.picturesgallery.R;
import com.example.artem.picturesgallery.activityscreenofpicture.ActivityScreenOfPicture;
import com.example.artem.picturesgallery.model.AppDataBase;
import com.example.artem.picturesgallery.model.InfoAboutPicture;
import com.example.artem.picturesgallery.model.Picture;
import com.example.artem.picturesgallery.rest.Downloader;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import retrofit2.HttpException;

public class ActivityMainScreen extends AppCompatActivity {

    private Downloader mDownloader;
    private RecyclerView mRecyclerView;
    private GalleryAdapter mGalleryAdapter;
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDownloader = new Downloader(this);
        initRecyclerView();
        mDownloader.loadData();
    }

    private void initRecyclerView(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_first);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
    }


    public void handleResponse(Picture response){
        List<InfoAboutPicture> listOfItemsFromNet = response.getImage();
        getDataToFillRecyclerView(listOfItemsFromNet);
    }

    public void handleError(Throwable error){
       Log.i("ERROR", "Error " + error.getLocalizedMessage());
        if(error instanceof HttpException){
            if(((HttpException) error).code() == 429){
                Log.i("ERROR", "Error 429");

                mDownloader.loadData();
            }
        }

        if(error instanceof UnknownHostException){
            Log.i("ERROR", "No internet connection");
            Toast.makeText(this,"Нет интернет соединения",Toast.LENGTH_LONG).show();
        }

       error.printStackTrace();
    }


    public void getDataToFillRecyclerView(List<InfoAboutPicture> listOfFilesFromNet){
        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(Single.just(listOfFilesFromNet)
                .map(this::addDataFromNetInDatabase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setListOfItemFromDbInAdapter));
    }

    public void setListOfItemFromDbInAdapter(List<InfoAboutPicture> listOfItems){
        mGalleryAdapter = new GalleryAdapter(this, listOfItems);
        mRecyclerView.setAdapter(mGalleryAdapter);
        mGalleryAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
               callActivityPictureOfScreen(listOfItems,position);
            }
        });
    }


    public List<InfoAboutPicture> addDataFromNetInDatabase(List<InfoAboutPicture>listOfFilesFromNet){
        /*
           В данном методе проверяются объекты, которые получают на вход из сети и попадают в базу данных.
        */

        AppDataBase appDataBase = AppDataBase.getDataBase(this);
        List<InfoAboutPicture> listOfFilesFromDb = appDataBase.infoAboutPictureDao().getAll();

        if(!listOfFilesFromDb.isEmpty()) {
            if(!isURLReachable(listOfFilesFromDb.get(0).getWebformatURL())) {
                    appDataBase.infoAboutPictureDao().clearDataBase();
            }
        }

        if (listOfFilesFromDb.isEmpty()) {
            for (int i = 0; i < listOfFilesFromNet.size(); i++) {
                appDataBase.infoAboutPictureDao().addItem(listOfFilesFromNet.get(i));
            }
        } else {

            for (int i = 0; i < listOfFilesFromNet.size(); i++) {
                if (!listOfFilesFromDb.contains(listOfFilesFromNet.get(i))) {
                    appDataBase.infoAboutPictureDao().addItem(listOfFilesFromNet.get(i));
                }
            }

            for (int i = 0; i < listOfFilesFromDb.size(); i++) {
                if (!listOfFilesFromNet.contains(listOfFilesFromDb.get(i))) {
                    appDataBase.infoAboutPictureDao().deleteItem(listOfFilesFromDb.get(i));
                }
            }
        }
        listOfFilesFromDb = appDataBase.infoAboutPictureDao().getAll();

        return listOfFilesFromDb;
    }

    public boolean isURLReachable(String urla){

        //В данном методе происходит проверка того, что ссылка на картинку не устарела.

        boolean result = false;

        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)

                .build();

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(spec));

            try {
                URL url = new URL(urla);
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.build().newCall(request).execute();
                Log.i("RESPONSE BODY", "" + response.code());

                if (response.code() == 200) {
                    result = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDownloader.unsubscribe();
        mCompositeDisposable.dispose();
    }

    private void callActivityPictureOfScreen(List<InfoAboutPicture> listOfItems, int position){
        /*
            Данный метод вызывает ActivityScreenOfPicture и передает туда id объекта картинки в базе
            данных на которую кликнул пользователь.
        */
        Intent intent = new Intent(this, ActivityScreenOfPicture.class);
        intent.putExtra("pictureId",listOfItems.get(position).getDbItemId());
        startActivity(intent);
    }
}
