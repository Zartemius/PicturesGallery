package com.example.artem.picturesgallery.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "infoAboutPictureRepository")
public class InfoAboutPicture {

    @PrimaryKey(autoGenerate = true)
    private int dbItemId;
    private int id;
    private String webformatURL;

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getWebformatURL() {
        return webformatURL;
    }

    public void setWebformatURL(String webformatURL) {
        this.webformatURL = webformatURL;
    }

    public int getDbItemId() {
        return dbItemId;
    }

    public void setDbItemId(int dbItemId) {
        this.dbItemId = dbItemId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InfoAboutPicture that = (InfoAboutPicture) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
