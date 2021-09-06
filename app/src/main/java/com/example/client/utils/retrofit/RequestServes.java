package com.example.client.utils.retrofit;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/7/25.
 */

public interface RequestServes {
    @Headers({"Content-Type: application/json", "Accept: application/json"}) //
    @POST("/verify")
    Call<String> verifyToken(@Body RequestBody verifyParam); //{"type":"verification","token":"","clienttype":""}

    @POST("/token")
    Call<String> getAcessToken(@Query("grant_type") String grant_type, @Query("username") String username, @Query("password") String password);

    @POST("/lbs")
    Call<String> getCluster();

//    "/token?grant_type=password&username="+username+"&password="+password
}
