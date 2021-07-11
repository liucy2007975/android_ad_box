package com.cow.liucy.libcommon.api.http.interceptor;


import android.util.ArrayMap;


import com.cow.liucy.libcommon.api.http.model.BaseResponse;
import com.cow.liucy.libcommon.logger.AppLogger;
import com.cow.liucy.libcommon.utils.DateTimeUtils;
import com.alibaba.fastjson.JSON;


import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * 拦截器
 * 向请求头里添加公共参数
 */

public class TokenInterceptor implements Interceptor {

    public final static String AUTH_HEADER_KEY = "Authorization";
    public final static String BEARER_HEADER_VALUE = "Bearer ";

    public final static String HEADER_PARKING_ID = "parkId";
    public final static String HEADER_API_VERSION = "apiVersion";

    private Map<String, String> mHeaderParamsMap = new ArrayMap<>();



    public TokenInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request oldRequest = null;
//        if (Valid.valid(AppPrefs.getInstance().getParkingId())) {
//            oldRequest = request.newBuilder()
//                    .removeHeader(HEADER_PARKING_ID)
//                    .removeHeader(HEADER_API_VERSION)
//                    .addHeader(HEADER_PARKING_ID, AppPrefs.getInstance().getParkingId())
////                    .addHeader(HEADER_API_VERSION, BuildConfig.API_VERSION)
//                    .build();
//        } else {
//            if (request.url().encodedPath().contains("upload")){
//                oldRequest.headers().
//            }
            oldRequest = request;
//        }

        long t1 = System.nanoTime();//请求发起的时间
        AppLogger.e(String.format("发送请求 %s on %s%n%s",
                request.url().uri().getHost()+request.url().uri().getPath(), chain.connection(), request.headers()));
        try {
        Response response = chain.proceed(oldRequest);

        long t2 = System.nanoTime();//收到响应的时间
        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
            //如果是ZIP文件流，下载文件，则直接返回
            if (response.headers().get("Content-Type").equalsIgnoreCase("application/zip")){
                AppLogger.e(String.format("接收响应:[%s] %n返回json:【】 %.1fms%n",
                        response.request().url().uri().getHost()+ response.request().url().uri().getPath(),
                        (t2 - t1) / 1e6d));
                return response;
            }
        ResponseBody responseBody = response.peekBody(1024 * 1024);

            String bodyString = responseBody.string();

            AppLogger.e(String.format("接收响应:[%s] %n返回json:【%s】 %.1fms%n",
                    response.request().url().uri().getHost()+ response.request().url().uri().getPath(),
                    bodyString,
                    (t2 - t1) / 1e6d));
//            if (TokenUtils.hasToken()) {//Token存在时，校验Token是否失效，
//                if (isTokenExpired(bodyString)) {//
//                    //同步请求方式，获取最新的Token
//                    String newToken = getNewToken();
//                    mHeaderParamsMap.put(AUTH_HEADER_KEY, BEARER_HEADER_VALUE + newToken);
//                    //使用新的Token，创建新的请求
//                    Request newRequest = chain.request()
//                            .newBuilder()
//                            .header(AUTH_HEADER_KEY, BEARER_HEADER_VALUE + newToken)
//                            .build();
//                    response.body().close();
//                    //重新请求
//                    return chain.proceed(newRequest);
//                } else {
//                    if (response.code() == 200) {
//                        BaseResponse baseResponse =JSON.parseObject(bodyString,BaseResponse.class);
//                        if (baseResponse.data == null) {
//                            baseResponse.data = "";
//                        }
//                        if (baseResponse.result == null) {
//                            baseResponse.result = "";
//                        }
//                        if (baseResponse.msg == null) {
//                            baseResponse.msg = "";
//                        }
//                        if (baseResponse.message == null) {
//                            baseResponse.message = "";
//                        }
//                        if (baseResponse.datetime == null) {
//                            baseResponse.datetime = DateTimeUtils.getFormatedDataString();
//                        }
//                        MediaType contentType = response.body().contentType();
//                        ResponseBody body = ResponseBody.create(contentType, JSON.toJSONString(baseResponse));
//
//                        response = response.newBuilder().body(body).build();
//
////                            ResponseBody responseBody2 = response.peekBody(1024 * 1024);
////                            String bodyString2 = responseBody2.string();
////                            Timber.e(String.format("接收响应2: [%s] %n返回json:【%s】 %.1fms%n",
////                                    response.request().url(),
////                                    bodyString2,
////                                    (t2 - t1) / 1e6d));
//                        return response;
//
//                    }
//                    return response;
//                }
//            } else {
                if (response.code() == 200) {
                    BaseResponse baseResponse =JSON.parseObject(bodyString,BaseResponse.class);
                    if (baseResponse.data == null) {
                        baseResponse.data = "";
                    }
//                    if (baseResponse.code == null) {
//                        baseResponse.code = "";
//                    }
                    if (baseResponse.msg == null) {
                        baseResponse.msg = "";
                    }
                    if (baseResponse.message == null) {
                        baseResponse.message = "";
                    }
                    if (baseResponse.datetime == null) {
                        baseResponse.datetime = DateTimeUtils.getFormatedDataString();
                    }
                    MediaType contentType = response.body().contentType();
                    ResponseBody body = ResponseBody.create(contentType, JSON.toJSONString(baseResponse));
                    response = response.newBuilder().body(body).build();
                    return response;

                }
                return response;
//            }
        } catch (Exception e) {
            AppLogger.e(">>>Exception e:" + e.getMessage());
        }
        return  chain.proceed(request);
    }

    /**
     * 根据接口返回值中的status，判断Token是否失效
     *
     * @param response
     * @return
     */
    private boolean isTokenExpired(String response) {
        try {
            if (response != null && response != "") {
                BaseResponse baseResponse =JSON.parseObject(response,BaseResponse.class);
                if (baseResponse != null) {
                    if (baseResponse.code==500) {
                        Timber.e(">>>>Token失效！");
                        return true;
                    }
                } else {
                    Timber.e(">>>>baseResponse==null");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Timber.e(">>>>网络请求错误 e:" + e.getMessage());
        }
        return false;
    }

    /**
     * 同步请求方式，获取最新的Token
     *
     * @return
     */
    private String getNewToken() throws IOException {
        String token = "";
//        // 此处要用到同步的retrofit请求
//        AuthRequst authRequst=new AuthRequst();
//        authRequst.setUsername(AppPrefs.getInstance().getMacCode());
//        authRequst.setPassword(AppPrefs.getInstance().getMacCode());//ymhAvm111
//
//        Call<BaseResponse> baseResponseCall= DataLoader.getInstance().getDataService().authCall(authRequst);
//         retrofit2.Response<BaseResponse> baseResponse= baseResponseCall.execute();
//        if (baseResponse!=null &&  baseResponse.body()!=null && valid.valid(baseResponse.body().data)){
//            token=baseResponse.body().data.toString();
//            AppPrefs.getInstance().updateToken(token);
//            AppPrefs.getInstance().updateTokenExpiredTime(DateTimeUtils.getDateByAddDays(1));
//        }
        return token;
    }

    public static class Builder {
        TokenInterceptor mHttpCommonInterceptor;

        public Builder() {
            mHttpCommonInterceptor = new TokenInterceptor();
        }

        public Builder addHeaderParams(String key, String value) {
            mHttpCommonInterceptor.mHeaderParamsMap.put(key, value);
            return this;
        }

        public Builder addHeaderParams(String key, int value) {
            return addHeaderParams(key, String.valueOf(value));
        }

        public Builder addHeaderParams(String key, float value) {
            return addHeaderParams(key, String.valueOf(value));
        }

        public Builder addHeaderParams(String key, long value) {
            return addHeaderParams(key, String.valueOf(value));
        }

        public Builder addHeaderParams(String key, double value) {
            return addHeaderParams(key, String.valueOf(value));
        }

        public TokenInterceptor build() {
            return mHttpCommonInterceptor;
        }
    }
}
