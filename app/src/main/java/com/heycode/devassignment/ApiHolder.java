package com.heycode.devassignment;

import retrofit2.Call;
import retrofit2.http.GET;

interface ApiHolder {

    @GET("1")
    Call<Opt1> getTasks1();

    @GET("2")
    Call<Opt1> getTasks2();

    @GET("3")
    Call<Opt1> getTasks3();
}
