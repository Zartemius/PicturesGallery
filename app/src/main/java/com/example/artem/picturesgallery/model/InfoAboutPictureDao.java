package com.example.artem.picturesgallery.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import java.util.List;

@Dao
public interface InfoAboutPictureDao {

    @Query("SELECT * FROM infoAboutPictureRepository")
    List<InfoAboutPicture> getAll();

    @Query("DELETE FROM infoAboutPictureRepository")
    void clearDataBase();

    @Insert
    void addItem(InfoAboutPicture item);

    @Delete
    void deleteItem(InfoAboutPicture item);

    @Query("SELECT * FROM infoAboutPictureRepository where dbItemId LIKE :id")
    InfoAboutPicture findById(int id);
}
