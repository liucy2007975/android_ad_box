package com.cow.liucy.libcommon.api.http;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by cow on 2017/12/22.
 *  封装 Rx 线程相关
 */

public class BaseRxScheduler {
    /**
     * @param observable
     * @param <T>
     * @return
     */
    protected static <T> Observable<T> observe(Observable<T> observable){
        return observable
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
