package com.cow.liucy.libcommon.api.http;


import com.cow.liucy.hdxm.libcommon.BuildConfig;
import com.cow.liucy.libcommon.api.http.interceptor.TokenInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by anjubao on 2017/8/1.
 */
//OkHttp 工厂方法，设置SSLSocket
public class CowOkHttpClientFacotry {
    private static final int DEFAULT_TIME_OUT = 5;//连接超时时间 10s
    private static final int DEFAULT_READ_TIME_OUT = 5;
    private static OkHttpClient okHttpClient = null;

    public synchronized static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {

            //开启Log
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
            if (BuildConfig.DEBUG) {
                //显示日志
                logInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            } else {
                logInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            }

            // 创建 OKHttpClient
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
            builder.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS);

            TokenInterceptor.Builder builderHead = new TokenInterceptor.Builder();
            // 添加公共参数拦截器
            TokenInterceptor commonInterceptor = builderHead.build();
            builder.addInterceptor(commonInterceptor);
            builder.addInterceptor(logInterceptor);
            //添加日志
//            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//                @Override
//                public void log(String message) {
//                    //打印retrofit日志
//                    Log.e("RetrofitLog","retrofitBack = "+message);
//                }
//            });
//            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            builder.sslSocketFactory(CowSSLSocketFactory.getSSLSocketFactory(Utils.getContext()));
//            builder.hostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            });
//            builder.
//            builder.addInterceptor(loggingInterceptor);

            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxRequests(15);
            builder.dispatcher(dispatcher);

            okHttpClient = builder
                    .build();
        }
        return okHttpClient;
    }


}
