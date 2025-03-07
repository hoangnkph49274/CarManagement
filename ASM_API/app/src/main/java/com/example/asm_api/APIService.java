package com.example.asm_api;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {
    @GET("getAllCar")
    Call<List<Car>> getCars();

    @POST("add")
    Call<Car> addCar(@Body Car car);


    @GET("/delete/{id}")
    Call<Car> deleteCar(@Path("id") String id);

    @POST("/update/{id}")
    Call<Car> updateCar(@Path("id") String id,@Body Car car);
}
