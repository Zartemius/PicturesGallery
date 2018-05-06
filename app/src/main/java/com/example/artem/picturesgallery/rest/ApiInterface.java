package com.example.artem.picturesgallery.rest;

import com.example.artem.picturesgallery.model.Picture;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("api/")
    Observable <Picture> getListOfPictures(@Query("key") String key,
                                           @Query("q") String query,
                                           @Query("per_page") int quantityOfReceivedResults);
}