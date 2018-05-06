package com.example.artem.picturesgallery.model;

/*
*База данных используется в данном приложениии в большей степени для сохранения ссылок на изображения
* полученных из ресурса, так как ссылки являются непостоянными, что затрудняет возможность их кэширования.
*/

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {InfoAboutPicture.class}, version =1)
public abstract class AppDataBase extends RoomDatabase {

    private static AppDataBase INSTANCE;
    private static final Object LOCK = new Object();

    public synchronized static AppDataBase getDataBase(Context context) {
        if (INSTANCE == null) {
            synchronized (LOCK) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDataBase.class, "database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    public abstract InfoAboutPictureDao infoAboutPictureDao();
}
