package com.simplytapp.sample;

import com.simplytapp.sample.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit interface for a simple GET query
 *
 * Created by Chad Schultz on 2/4/2016.
 */
public interface WeatherService {
    @GET("data/2.5/weather")
    Call<WeatherResponse> weatherResponse(@Query("lat") String lat, @Query("lon") String lon, @Query("appid") String appId);
}
