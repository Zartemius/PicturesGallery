package com.example.artem.picturesgallery.activityscreenofpicture;

/*
*В данном activity происходит отображение картинки, на которую нажал пользователь в ActivityMainScreen.
*Через intent id объекта картинки передается в данную activity и после этого начинается её поиск в базе даннах по id.
*У найденного объекта получаем url изображения и передаем в Picasso для последующей загрузки и отображения в ImageView.
*/

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import com.example.artem.picturesgallery.R;
import com.example.artem.picturesgallery.model.AppDataBase;
import com.example.artem.picturesgallery.model.InfoAboutPicture;
import com.squareup.picasso.Picasso;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ActivityScreenOfPicture extends AppCompatActivity {

    private ImageView mImageView;
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_of_picture);

        mImageView = findViewById(R.id.screen_of_picture_activity__image);

        Intent intent = getIntent();
        int receivedFromMainActivityId = intent.getIntExtra("pictureId",1);

        findItemToDisplay(receivedFromMainActivityId);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void findItemToDisplay(int receivedId){
        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(Single.just(receivedId)
                .map(this::findItemInDb)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::displayItem));
    }

    public InfoAboutPicture findItemInDb(int idOfPicture){
        AppDataBase appDataBase = AppDataBase.getDataBase(this);

        InfoAboutPicture itemChosenByUser = appDataBase.infoAboutPictureDao().findById(idOfPicture);
        return itemChosenByUser;
    }

    public void displayItem(InfoAboutPicture itemChosenByUser){
        Picasso.get()
                .load(itemChosenByUser.getWebformatURL())
                .into(mImageView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }
}
