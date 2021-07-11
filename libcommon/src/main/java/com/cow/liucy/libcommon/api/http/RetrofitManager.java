package com.cow.liucy.libcommon.api.http;


import com.cow.liucy.libcommon.api.CommonConfig;
import com.cow.liucy.libcommon.api.http.model.BaseResponse;
import com.cow.liucy.libcommon.api.http.model.DeviceLoginRes;
import com.cow.liucy.libcommon.api.http.model.EntryReq;
import com.cow.liucy.libcommon.api.http.model.ExitReq;
import com.cow.liucy.libcommon.api.http.model.FileUploadReq;
import com.cow.liucy.libcommon.api.http.model.HeartBeatReq;
import com.cow.liucy.libcommon.api.http.model.HeartBeatResp;
import com.cow.liucy.libcommon.utils.AppPrefs;
import com.cow.liucy.libcommon.utils.CommonUtils;
import com.cow.liucy.libcommon.utils.Valid;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by cow on 2017-7-6.
 */

public class RetrofitManager extends BaseRxScheduler {
    private Retrofit mRetrofit;
    private ApiService apiService;
    private volatile static RetrofitManager instance;

    //--profile --offline
    private RetrofitManager() {
        String baseUrl = AppPrefs.getInstance().getServer();
        if (!Valid.valid(baseUrl)){
            baseUrl = CommonConfig.BASE_URL;
            AppPrefs.getInstance().setServer(CommonConfig.BASE_URL);
        }
        if (!baseUrl.endsWith("/")){
            baseUrl=baseUrl+"/";
        }
        mRetrofit = new Retrofit.Builder()
                .client(CowOkHttpClientFacotry.getOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build();
        setApiService(mRetrofit.create(ApiService.class));
    }

    public ApiService getApiService() {
        return apiService;
    }

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }


    /**
     * 获取RetrofitServiceManager
     *
     * @return
     */
    public static RetrofitManager getInstance() {
        if (instance == null) {
            synchronized (RetrofitManager.class) {
                if (instance == null) {
                    instance = new RetrofitManager();
                }
            }
        }
        return instance;
    }

    public static void resetRetrofitManager() {
        instance = null;
        getInstance();
    }


    /**
     * 获取对应的Service
     *
     * @param service Service 的 class
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);
    }


//    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//    MultipartBody.Part part = MultipartBody.Part.createFormData("img", file.getName(), requestBody);
//                            RetrofitManager.getInstance().fielUpload(part, entity.getFilePath())
//            .subscribe(new Consumer<String>() {
//        @Override
//        public void accept(String listBaseResponse) throws Exception {
//            AppLogger.e("图片上传成功>>>>>: " + JSON.toJSONString(listBaseResponse));
//            plateInfoImageEntityBox.remove(entity);
//        }
//    }, new Consumer<Throwable>() {
//        @Override
//        public void accept(Throwable throwable) throws Exception {
//            AppLogger.e("图片上传失败>>>>>: " + throwable.getMessage());
//        }
//    });


//    /**
//     * 文件上传
//     *
//     * @return
//     */
//    public Observable<String> fileUpload(MultipartBody.Part file, String path) {
//        return observe(RetrofitManager
//                .getInstance()
//                .getApiService()
//                .fileUpload(file, path))
//                .map(new BaseFunction<String>());
//    }
//
//    /**
//     * 访问记录自动提交
//     *
//     * @param accessRecord
//     * @return
//     */
//    public Call<Object> postAccessRecord(String accessRecord, String mac, String ip) {
//        return RetrofitManager
//                .getInstance()
//                .getApiService()
//                .postAccessRecord(accessRecord, mac, ip);
//    }
//
//    /**
//     * 发送批量同步全部处理完毕结果
//     *
//     * @param deviceMac
//     * @return
//     */
//    public Observable<Object> sendSyncResultAllFinish(String deviceMac) {
//        return observe(RetrofitManager
//                .getInstance()
//                .getApiService()
//                .sendSyncResultAllFinish(deviceMac));
//    }
//
//    /**
//     * 发送批量同步结果
//     *
//     * @param syncResult
//     * @return
//     */
//    public Observable<Object> sendSyncResultAll(String syncResult) {
//        return observe(RetrofitManager
//                .getInstance()
//                .getApiService()
//                .sendSyncResultAll(syncResult));
//    }
//
//    /**
//     * 发送单个同步结果
//     *
//     * @param syncResult
//     * @return
//     */
//    public Observable<Object> sendSyncResult(String syncResult) {
//        return observe(RetrofitManager
//                .getInstance()
//                .getApiService()
//                .sendSyncResult(syncResult));
//    }
//
//    /**
//     * 下载文件
//     *
//     * @param url
//     * @return
//     */
//    public Call<ResponseBody> download(String url) {
//        return RetrofitManager
//                .getInstance()
//                .getApiService()
//                .download(url);
//    }
//
//
//    /**
//     * 获取小区编号
//     *
//     * @param
//     * @return
//     */
//    public Observable<String> getCodeId() {
//        return RetrofitManager
//                .getInstance()
//                .getApiService()
//                .getCodeId()
//                .observeOn(Schedulers.io())
//                .map(new BaseFunction<String>());
//    }
//
////    /**
////     * 上传二维码访客通行记录
////     *
////     * @param
////     * @return
////     */
////    public Observable<Object> sendVisitorRecord(VisitorRecordEntity visitorRecordEntity) {
////        return RetrofitManager
////                .getInstance()
////                .getApiService()
////                .sendVisitorRecord(visitorRecordEntity)
////                .map(new BaseFunction<Object>());
////    }
//
//    /**
//     * 重置应用数据库应答
//     *
//     * @param
//     * @return
//     */
//    public Observable<Object> sendDeviceResetSuccess() {
//        return RetrofitManager
//                .getInstance()
//                .getApiService()
//                .sendDeviceResetSuccess(CommonUtils.getIPAddress());
//    }


    /**
     * 设备注册接口
     *
     * @param deviceSn
     * @return
     */
    public Call<BaseResponse<DeviceLoginRes>> postDeviceLogin(String deviceSn) {
        return RetrofitManager
                .getInstance()
                .getApiService()
                .postDeviceLogin(deviceSn);
    }

    /**
     * 设备心跳接口
     *
     * @param deviceId
     * @return
     */
    public Observable<BaseResponse<Object>> postDeviceHeartbeat(String deviceId) {
        return observe(RetrofitManager
                .getInstance()
                .getApiService()
                .postDeviceHeartbeat(deviceId));
    }


    /**
     * 设备心跳接口V2
     *
     * @param heartBeatReq
     * @return
     */
    public Observable<BaseResponse<HeartBeatResp>> postDeviceHeartbeatV2(HeartBeatReq heartBeatReq) {
        return observe(RetrofitManager
                .getInstance()
                .getApiService()
                .postDeviceHeartbeatV2(heartBeatReq));
    }

    public Observable<BaseResponse<Object>> postUpload(FileUploadReq fileUploadReq){
        return observe(RetrofitManager
                .getInstance()
                .getApiService()
                .postUpload("cow",CommonUtils.getSN(),fileUploadReq));
    }

    public Observable<BaseResponse<Object>> postUpload(String filePath,RequestBody body){
        return observe(RetrofitManager
                .getInstance()
                .getApiService()
                .postUploadJsonBody("cow",CommonUtils.getSN(),body));
    }

    /**
     * 入场信息上传
     *
     * @param entryReq
     * @return
     */
    public  Call<BaseResponse<Object>> postEntry( EntryReq entryReq){
        return RetrofitManager
                .getInstance()
                .getApiService()
                .postEntry(entryReq);
    }

    /**
     * 出场信息上传
     *
     * @param exitReq
     * @return
     */
    public Call<BaseResponse<Object>> postExit( ExitReq exitReq){
        return RetrofitManager
                .getInstance()
                .getApiService()
                .postExit(exitReq);
    }

    /**
     * Created by cow on 2017/12/22.
     * Api接口定义
     */

    public interface ApiService {


        /**
         * 设备注册接口
         *
         * @param deviceSn
         * @return
         */
        @FormUrlEncoded
        @POST("/parking-lot-status/device")
        Call<BaseResponse<DeviceLoginRes>> postDeviceLogin(@Field("deviceSn") String deviceSn);

        /**
         * 设备心跳接口
         *
         * @param deviceId
         * @return
         */
        @FormUrlEncoded
        @POST("/parking-lot-status/device-heartbeat")
        Observable<BaseResponse<Object>> postDeviceHeartbeat(@Field("deviceId") String deviceId);



        /**
         * 设备心跳接口V2
         *
         * @param heartBeatReq
         * @return
         */
        @POST("/parking-lot-status/device-heartbeat-v2")
        Observable<BaseResponse<HeartBeatResp>> postDeviceHeartbeatV2(@Body HeartBeatReq heartBeatReq);

        /**
         * 入场信息上传
         *
         * @param entryReq
         * @return
         */
        @POST("/parkingRecord/entry")
        Call<BaseResponse<Object>> postEntry(@Body EntryReq entryReq);

        /**
         * 出场信息上传
         *
         * @param exitReq
         * @return
         */
        @POST("/parkingRecord/exit")
        Call<BaseResponse<Object>> postExit(@Body ExitReq exitReq);

        /**
         * 图片信息上传
         *
         * @param fileUploadReq
         * @return
         */
        @POST("/parkingRecord/upload")
        Observable<BaseResponse<Object>> postUpload(@Header("sourceHost") String sourceHost,@Header("deviceSN") String deviceSN, @Body FileUploadReq fileUploadReq);

        /**
         * 图片信息上传
         *
         * @param body
         * @return
         */
        @POST("/parkingRecord/upload")
        Observable<BaseResponse<Object>> postUploadJsonBody(@Header("sourceHost") String sourceHost,@Header("deviceSN") String deviceSN,@Body RequestBody body);



//        /**
//         * 文件上传
//         *
//         * @return
//         */
//        @POST(CommonConfig.UPLOAD_FILE)
//        @Multipart
//        Observable<BaseResponse<String>> fileUpload(@Part MultipartBody.Part file, @Query("path") String path);
//
//        /**
//         * 访问记录自动提交
//         *
//         * @param accessRecord
//         * @return
//         */
//        @FormUrlEncoded
//        @POST("api/device/api/accessRecord")
//        Call<Object> postAccessRecord(@Field("accessRecord") String accessRecord,
//                                      @Field("mac") String mac,
//                                      @Field("ip") String ip);
//
//        @Streaming
//        @GET
//        Call<ResponseBody> download(@Url String url);
//
//        /**
//         * 返回批量同步全部完成结果
//         * @return
//         */
//        @GET("api/notify/device/{deviceMac}/finishInit")
//        Observable<Object> sendSyncResultAllFinish(@Path("deviceMac") String deviceMac);
//
//
//        /**
//         * 返回批量同步结果
//         * @param syncResult
//         * @return
//         */
//        @FormUrlEncoded
//        @POST("api/notify/checkResultAll")
//        Observable<Object> sendSyncResultAll(@Field("syncResultData") String syncResult);
//
//        /**
//         * 返回单个同步结果
//         * @param syncResult
//         * @return
//         */
//        @FormUrlEncoded
//        @POST("api/notify/checkResult")
//        Observable<Object> sendSyncResult(@Field("syncResultData") String syncResult);
//
//
//
//
//        /**
//         * 获取小区编号
//         * @return
//         */
//        @GET("api/device/api/getCommunityCode")
//        Observable<BaseResponse<String>> getCodeId();
//
////        /**
////         * 上传二维码访客通行记录
////         * @param visitorRecordEntity
////         * @return
////         */
////        @POST("api/device/api/visitorRecord")
////        Observable<BaseResponse<Object>> sendVisitorRecord(@Body VisitorRecordEntity visitorRecordEntity);
//
//        /**
//         * 重置应用数据库应答
//         * @return
//         */
//        @GET("api/notify/{localIp}/deviceReset")
//        Observable<Object> sendDeviceResetSuccess(@Path("localIp") String localIp);

    }

}
