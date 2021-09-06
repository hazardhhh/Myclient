package com.example.client.utils.retrofit;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RequestConferenceServes {

	@Headers({ "Content-Type: application/json", "Accept: application/json" }) // 需要添加头
	@POST("/rest/meeting/createLjMeeting")
	Call<String> createLjMeeting(@Body RequestBody route); // 创建立即会议

	@Headers({ "Content-Type: application/json", "Accept: application/json" }) // 需要添加头
	@POST("/rest/meeting/meetingNotice")
	Call<String> meetingnotice(@Body RequestBody route); // 创建单人会议

	@GET("/rest/meeting/getDevices/{from}")
	Call<String> getDevices(@Path("from") String from); // 获取设备列表

	@Headers({ "Content-Type: application/json", "Accept: application/json" }) // 需要添加头
	@POST("/rest/meeting/inviteUsersMeeting")
	Call<String> inviteUsersMeeting(@Body RequestBody route); // 邀请人员入会

	@GET("/rest/meeting/getCurrMeetingList/{from}")
	Call<String> getCurrMeetingList(@Path("from") String from); // 获取当前会议列表

	@GET("/rest/meeting/getHistoryMeetingList/{from}")
	Call<String> searchHistoryList(@Path("from") String from); // 获取历史会议列表

	//http://localhost:9090/rest/meeting/getOneHistoryMeeting/{from}/{golobal}
	@GET("/rest/meeting/getOneHistoryMeeting/{from}/{globalConferenceID}")
	Call<String> searchHistoryDetail(@Path("from") String from, @Path("globalConferenceID") String gid); // 获取历史会议详情

	@GET("/rest/meeting/getMeetingDetail/{from}/{roomId}")
	Call<String> getMeetingDetail(@Path("from") String from, @Path("roomId") String roomId); // 查询会议详情

	@GET("/rest/meeting/getToken/{from}")
	Call<String> getToken(@Path("from") String from); // 获取token

	@GET("/rest/meeting/getOwnerRoom/{from}")
	Call<String> getSingleRoom(@Path("from") String from); // 获取个人会议室

	@GET("/rest/meeting/setpwd/{userid}")
	Call<String> setRoomPwd(@Path("userid") String userid, @Query("roomId") String roomid,
							@Query("roomPIN") String pin); // 设置会议室密码

	@GET("/rest/meeting/lockmeeting/{userid}")
	Call<String> lockmeeting(@Path("userid") String userid, @Query("roomId") String roomid,
							@Query("isLock") boolean islock); // 锁定会议室

	@GET("/rest/meeting/getShareRoomUrl/{golobalConferenceID}/{from}")
	Call<String> getShareRoomUrl(@Path("golobalConferenceID") String id, @Path("from") String from); // 分享会议链接
}
