package com.heycode.devassignment;

import retrofit2.Call;
import retrofit2.http.GET;

interface JsonPlaceHolderApi {

    @GET("1")
    Call<Opt1> getTasks();
}
