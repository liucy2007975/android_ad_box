package com.cow.liucy.libcommon.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import androidx.annotation.CallSuper;
import android.util.Log;

import com.cow.liucy.libcommon.logger.AppLogger;

import java.util.ArrayList;
import java.util.List;


//import android.support.multidex.MultiDex;
//import android.support.multidex.MultiDexApplication;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2017/03/30
 *     desc  : 基类App
 * </pre>
 */
public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";

    private static BaseApplication instance;
    private List<Activity> activityList = new ArrayList();
    private Activity lastAddActivity = null;
    private Activity previousActivity = null;

    @CallSuper
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        refWatcher= LeakCanary.install(this);
        registerActivityLifecycleCallbacks(mCallbacks);

    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }
    public static BaseApplication getInstance() {
        return instance;
    }

    public void addActivity(Activity activity) {
        this.activityList.add(activity);
        this.previousActivity = this.lastAddActivity;
        this.lastAddActivity = activity;
    }

    public void removeActivity(Activity activity) {
        synchronized (this.activityList) {
            AppLogger.d("removeActivity");
            this.activityList.remove(activity);
        }
    }

    public void clearAllActivities() {
        synchronized (this.activityList) {
            for (Activity activity : this.activityList) {
                if (activity != null) {
                    activity.finish();
                }
            }
            this.activityList.clear();
        }
    }

    private ActivityLifecycleCallbacks mCallbacks = new ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Log.d(TAG, "onActivityCreated() called with: activity = [" + activity + "], savedInstanceState = [" + savedInstanceState + "]");
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.d(TAG, "onActivityStarted() called with: activity = [" + activity + "]");
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.d(TAG, "onActivityResumed() called with: activity = [" + activity + "]");
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.d(TAG, "onActivityPaused() called with: activity = [" + activity + "]");
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.d(TAG, "onActivityStopped() called with: activity = [" + activity + "]");
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Log.d(TAG, "onActivitySaveInstanceState() called with: activity = [" + activity + "], outState = [" + outState + "]");
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.d(TAG, "onActivityDestroyed() called with: activity = [" + activity + "]");
        }
    };
}
