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
//        String baseUrl = AppPrefs.getInstance().getServer();
//        if (!Valid.valid(baseUrl)){
//            baseUrl = CommonConfig.BASE_URL;
//            AppPrefs.getInstance().setServer(CommonConfig.BASE_URL);
//        }
//        if (!baseUrl.endsWith("/")){
//            baseUrl=baseUrl+"/";
//        }
//        mRetrofit = new Retrofit.Builder()
//                .client(CowOkHttpClientFacotry.getOkHttpClient())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(FastJsonConverterFactory.create())
//                .baseUrl(baseUrl)
//                .build();
//        setApiService(mRetrofit.create(ApiService.class));
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



    /**
     * Created by cow on 2017/12/22.
     * Api接口定义
     */

    public interface ApiService {



    }

}
