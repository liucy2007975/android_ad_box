package com.cow.liucy.libcommon.utils;

import android.util.Log;

import com.cow.liucy.libcommon.api.http.model.BaseResponse;
import com.cow.liucy.libcommon.logger.AppLogger;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by anjubao on 2018-01-10.
 */

public class HttpUtls {
    private static final int CONNECT_TIMEOUT = 5;

    private static final int READ_TIMEOUT = 10;

    private static final MediaType MEDIA_JSON = MediaType.parse("application/json;charset=utf-8");

    private static OkHttpClient httpClient = null;
    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIMEOUT,TimeUnit.SECONDS);

        //添加日志
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //打印retrofit日志
                Log.e("RetrofitLog","retrofitBack = "+message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(15);
        builder.dispatcher(dispatcher);

        httpClient =builder.build();
    }

//    /**
//     * 上传文件
//     * @param serverUrl
//     * @param data
//     * @param fileName
//     * @return
//     */
//    public static String postFile(String serverUrl,byte[] data,String fileName){
//        try {
//            FileUploadReq fileUploadReq=new FileUploadReq();
//            fileUploadReq.setFileName(fileName);
//            fileUploadReq.setData(new String(Base64.encode(data,Base64.DEFAULT), Charset.forName("UTF-8")));
//            String json = JSON.toJSONString(fileUploadReq);
//            AppLogger.e(">>>>>postBody json: " + json);
//            RequestBody body = RequestBody.create(MEDIA_JSON, json);
//            Request request = new Request.Builder()
//                    .url(serverUrl)
//                    .post(body)
//                    .build();
//            Response response = httpClient.newCall(request).execute();
//            if (response.isSuccessful()) {
//                FileUploadResp fileUploadResp = JSON.parseObject(response.body().string(), FileUploadResp.class);
//                if (fileUploadResp!=null){
//                    return fileUploadResp.getId();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static <T> BaseResponse<String> postBody(String serverUrl, T t) {
        BaseResponse<String> br = null;
        try {
            String json = JSON.toJSONString(t);
            AppLogger.e(">>>>>postBody json: " + json);
            RequestBody body = RequestBody.create(MEDIA_JSON, json);
            Request request = new Request.Builder()
                    .url(serverUrl)
                    .post(body)
                    .build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                br = JSON.parseObject(response.body().string(), new TypeReference<BaseResponse<String>>(){}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppLogger.e(">>>>>POST BODY ERROR: " + e.getMessage());
        }
        return br;
    }

    public static <T> BaseResponse<T> get(String serverUrl, Class<T> clz) {
        BaseResponse<T> br = null;
        try {
            Request request = new Request.Builder()
                    .url(serverUrl)
                    .get()
                    .build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                BaseResponse<String> brTmp = JSON.parseObject(response.body().string(), new TypeReference<BaseResponse<String>>(){}.getType());
                br = new BaseResponse<>();
                br.code = brTmp.code;
                br.message = brTmp.message;
                br.total = brTmp.total;
                br.data = JSON.parseObject(brTmp.data, clz);
            }
        } catch (Exception e) {
            AppLogger.e(">>>>>GET ERROR: " + e.getMessage());
        }
        return br;
    }

    /**
     * 获取枪的信息
     * @param strIP  传入的枪的IP地址
     * @return
     */
    public static String getDeviceParam(String strIP){
        String serverUrl = "http://" + strIP + ":8000/cgi-bin/get.cgi"+"?random=&key=device_param";
        String strDeviceVersion = null;
        try {
            Request request = new Request.Builder()
                    .url(serverUrl)
                    .get()
                    .build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String data = response.body().string();
                if(data != null)
                {
                    JSONObject jsonObj = JSON.parseObject(data);
                    if(jsonObj.get("device_param") != null) {
                        JSONObject jsonSubObj = JSON.parseObject(jsonObj.get("device_param").toString());
                        if(jsonSubObj.get("device_version") != null) {
                            strDeviceVersion = jsonSubObj.getString("device_version");
                        }
                    }
                }
            }
        }catch (Exception e) {
            AppLogger.e(">>>>>GET ERROR: " + e.getMessage());
        }
        return strDeviceVersion;

    }

    public static <T> BaseResponse<List<T>> getList(String serverUrl, Class<T> clz) {
        BaseResponse<List<T>> br = null;
        try {
            Request request = new Request.Builder()
                    .url(serverUrl)
                    .get()
                    .build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                BaseResponse<String> brTmp = JSON.parseObject(response.body().string(), new TypeReference<BaseResponse<String>>(){}.getType());
                br = new BaseResponse<>();
                br.code = brTmp.code;
                br.message = brTmp.message;
                br.total = brTmp.total;
                br.data = JSON.parseArray(brTmp.data, clz);
            }
        } catch (Exception e) {
            AppLogger.e(">>>>>GET ERROR: " + e.getMessage());
        }
        return br;
    }

    public static String getCommandLoadCertificateReqDataList(String serverUrl) {
        try {
            Request request = new Request.Builder()
                    .url(serverUrl)
                    .get()
                    .build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载文件
     * @param fileUrl 文件地址
     * @param destFileDir 存储目录
     * @return
     */
    public static boolean downloadFile(String fileUrl, String destFileDir){
        InputStream is = null;
        FileOutputStream outStream = null;
        try {
            //http下载文件
            Request request = new Request.Builder()
                    .url(fileUrl)
                    .get()
                    .build();
            Response response = httpClient.newCall(request).execute();
            //保存文件到本地
            byte[] buf = new byte[2048];
            int len = 0;
            if (response.isSuccessful()) {
                File file = new File(destFileDir);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                } else {
                    file.delete();
                    file.createNewFile();
                }
                outStream = new FileOutputStream(file);
                is=response.body().byteStream();
                while ((len = is.read(buf)) != -1) {
                    outStream.write(buf, 0, len);
                }
                outStream.flush();
                AppLogger.e(">>>>>>下载固件成功");
                return true;
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            AppLogger.e(">>>>>>下载固件失败："+e.getMessage());
            return false;
        }finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                AppLogger.e(">>>>>>下载固件失败："+e.getMessage());
            }
        }
    }

}
