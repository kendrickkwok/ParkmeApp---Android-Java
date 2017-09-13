package com.example.joseph.parkmeapplication;

import com.example.joseph.parkmeapplication.POJO.Example;

import static com.android.volley.Request.Method.GET;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
/**
 * Created by Joseph on 4/19/2017.
 */

public interface RetrofitMaps {
    @GET("api/directions/json?key=AIzaSyC22GfkHu9FdgT9SwdCWMwKX1a4aohGifM")
    Call<Example> getDistanceDuration(@Query("units") String units, @Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode);
}

