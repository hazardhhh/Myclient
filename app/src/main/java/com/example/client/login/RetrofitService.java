package com.example.client.login;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2018/3/16/0016.
 */

public interface RetrofitService {
//    @POST("/token")
//    Call<String> getAcessToken(@Query("grant_type") String grant_type, @Query("username") String username, @Query("password") String password, @Query("clienttype") String clienttype);

    @POST("/token")
    Call<String> refreshAcessToken(@Query("grant_type") String grant_type, @Query("refresh_token") String refresh_token, @Query("clienttype") String clienttype);

//    @POST("/lbs")
//    Call<String> getCluster();

    @Multipart
    @POST("")
    Call<String> getUploadRes(@Url String url, @Part("description") RequestBody description, @Part MultipartBody.Part file);

    @GET
    Call<ResponseBody> downloadFile(@Url String url);

    @GET("/")
    Call<ResponseBody> downloadFileBase();

    /**
     * 邮箱未读数
     *
     * @param email
     * @return
     */
    @POST("ucintegrator/tx/getUnreadNumber?")
    Call<ResponseBody> getEmailUnReadCount(@Query("userid") String email);

    /**
     * 获取登录企业邮箱的url
     *
     * @param email
     * @return
     */
    @POST("ucintegrator/tx/getReturnLoginUrl")
    Call<ResponseBody> getEmailOutSideLoginUrl(@Query("userid") String email);

    /**
     * 获取待办事项数量
     *
     * @param loginName
     * @return
     */
    @GET
    Call<ResponseBody> getTodoNotifyCount(@Url String loginName);

    /**
     * 获取登录验证码
     *
     * @param phone
     * @return
     */
    @POST("/api/v1/uccall/smslogin/sendLoginSms")
    Call<ResponseBody> getVerifyCode(@Query("phone") String phone);

    /**
     * 验证码登录
     *
     * @param phone
     * @param code
     * @return
     */
    @POST("/api/v1/uccall/smslogin/login")
    Call<ResponseBody> loginByVerifyCode(@Query("phone") String phone, @Query("code") String code);

    /**
     * 获取注册验证码
     *
     * @param phone
     * @return
     */
    @POST("/ucwebservice/api/v1/uccall/smsRegister/sendRegisterSms")
    Call<ResponseBody> sendRegisterSms(@Query("phone") String phone, @Query("source") String source);

    /**
     * 商会--用户注册
     *
     * @param phone
     * @return
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"}) // 需要添加头
    @POST("/ucloud-chamber-server/register")
    Call<ResponseBody> register(@Body RequestBody body);

    @GET("/ucloud-chamber-server/member/getAuthentication")
    Call<ResponseBody> getAuthentication();


    /**
     * 获取工作状态列表
     *
     * @return
     */
    @POST("/v1/configure/fetchworkstatus")
    Call<ResponseBody> getWorkStatusList(@Body Map<String, String> map);


    /**
     * 获取工作状态详情
     *
     * @return
     */
    @POST("/v1/configure/fetchworkstatusmsg")
    Call<ResponseBody> getWorkStatusDetailInfo(@Body Map<String, String> map);

    /**
     * 设置工作状态
     *
     * @return
     */
    @POST("/v1/configure/selectworkstatus")
    Call<ResponseBody> setWorkStatus(@Body Map<String, String> map);


    /**
     * 工作状态 新增自定义内容
     *
     * @param username
     * @param clientType 默认 1
     * @param msgJson    格式  "msg":{"content":"msg","workstatusid":"","priority":0},
     * @return
     */
    @POST("/v1/configure/createhworkstatusmsg")
    Call<ResponseBody> createhWorkStatusMsg(@Body Map<String, String> map);


    /**
     * 工作状态 删除内容
     *
     * @param username
     * @param clientType 默认 1
     * @return
     */
    @POST("/v1/configure/createhworkstatusmsg")
    Call<ResponseBody> deleteWorkStatusMsg(@Body Map<String, String> map);

    /**
     * DING消息  获取未读数
     *
     * @return
     */
    @POST("/api/v1/uccall/ding/getcount")
    Call<ResponseBody> getDingUnReadCount();

    /**
     * DING消息 标记为已读状态
     *
     * @param ucmsguri
     * @param token
     * @return
     */
    @POST("/api/v1/uccall/ding/mark")
    Call<ResponseBody> markDingRead(@Body Map<String, String> body);

    /**
     * 音视频通话UserSIG
     *
     * @param body
     * @return
     */
    @FormUrlEncoded
    @POST("/api/v1/uccall/tencentTRTC/getUserSig")
    Call<ResponseBody> getUserSig(@Field("token") String token);

}
