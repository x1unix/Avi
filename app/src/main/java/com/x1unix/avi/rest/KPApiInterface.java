package com.x1unix.avi.rest;

import com.x1unix.avi.model.KPMovie;
import com.x1unix.avi.model.KPSearchResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface KPApiInterface {
    @GET("getKPSearchInFilms")
    Call<KPSearchResponse> findMovies(@Query("keyword") String keyword);

    @GET("getKPFilmDetailView")
    Call<KPMovie> getMovieById(@Query("filmID") String filmId);

}
