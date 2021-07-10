package com.cow.liucy.hdxm.libcommon.api.http;


import com.cow.liucy.hdxm.libcommon.api.http.exception.ApiException;
import com.cow.liucy.hdxm.libcommon.api.http.model.BaseResponse;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by anjubao on 2017/12/22.
 * 对返回数据进行变换
 */

public class BaseFunction<T> implements Function<BaseResponse<T>,T> {

    @Override
    public T apply(@NonNull BaseResponse<T> baseResponse) throws Exception {
        if(baseResponse.code!=0){
            throw new ApiException(baseResponse.code+"",baseResponse.message);
        }
        return baseResponse.data;
    }
}
