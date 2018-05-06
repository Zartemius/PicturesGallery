package com.example.artem.picturesgallery.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Picture {

    @SerializedName("totalHits")
    private String totalHits;

    @SerializedName("hits")
    private List<InfoAboutPicture> image;

    public String getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(String totalHits) {
        this.totalHits = totalHits;
    }

    public List<InfoAboutPicture> getImage() {
        return image;
    }

    public void setImage(List<InfoAboutPicture> image) {
        this.image = image;
    }
}
