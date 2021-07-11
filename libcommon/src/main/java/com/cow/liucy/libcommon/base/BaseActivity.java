package com.cow.liucy.libcommon.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.cow.liucy.libcommon.utils.DialogUtil;
import com.cow.liucy.libcommon.utils.ToastUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;




/**
 * Created by fanyufeng on 2017-7-28.
 */

public class BaseActivity extends RxAppCompatActivity {

//    protected TextView btn_left;
//    protected TextView tv_title;
//    protected TextView btn_right;

    private LinearLayout common_topbar;
    private ProgressDialog progressDialog;
    //屏幕宽高
    public int windowWidth;
    public int windowHeight;

//    protected DaoSession daoSession=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        daoSession= DaoManager.getInstance().getmDaoSession();

        //隐藏顶部，全屏显示
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //保持屏幕常量
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //使该Activity在锁屏界面上面显示
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//        hideBottomUIMenu();

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        wm.getDefaultDisplay().getWidth();
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        windowWidth = (int) (width / density);  // 屏幕宽度(dp)
        windowHeight = (int) (height / density);// 屏幕高度(dp)
//        BaseApplication.getInstance().addActivity(this);
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

//    /**
//     * @param titleId      标题名称
//     * @param left_str_id  左侧image 资源id 为0 则不显示 没有右侧image
//     * @param right_str_id 右侧image 资源id 为0 则不显示 没有右侧image
//     */
//    public void initTopButton(int titleId, int left_str_id, int right_str_id, @Nullable View.OnClickListener listener) {
//        tv_title = (TextView) findViewById(R.id.common_topbar_title);
//        tv_title.setText(getResources().getString(titleId));
//        btn_left = (TextView) findViewById(R.id.common_leftBtn);
//        btn_right = (TextView) findViewById(R.id.common_rightBtn);
//        setCommon_topbar((LinearLayout)findViewById(R.id.common_topbar));
//        if (left_str_id == 0) {
//            btn_left.setVisibility(View.GONE);
//        } else {
//            btn_left.setVisibility(View.VISIBLE);
//            btn_left.setText(getResources().getString(left_str_id));
//            btn_left.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    if (ItemClickUtils.isFastDoubleClick()) {
//                        return;
//                    }
//                    finish();
//                }
//            });
//        }
//        if (right_str_id == 0) {
//            btn_right.setVisibility(View.GONE);
//        } else {
//            btn_right.setVisibility(View.VISIBLE);
//            btn_right.setText(getResources().getString(right_str_id));
//            if(listener != null) {
//                btn_right.setOnClickListener(listener);
//            }
//        }
//    }

//    /**
//     * 动态修改标题名称
//     * @param titleStr      标题名称
//     * @param left_str_id  左侧image 资源id 为0 则不显示 没有右侧image
//     * @param right_str_id 右侧image 资源id 为0 则不显示 没有右侧image
//     */
//    public void initTopButton(String titleStr, int left_str_id, int right_str_id, @Nullable View.OnClickListener listener) {
//        tv_title = (TextView) findViewById(R.id.common_topbar_title);
//        tv_title.setText(titleStr);
//        btn_left = (TextView) findViewById(R.id.common_leftBtn);
//        btn_right = (TextView) findViewById(R.id.common_rightBtn);
//        setCommon_topbar((LinearLayout)findViewById(R.id.common_topbar));
//        if (left_str_id == 0) {
//            btn_left.setVisibility(View.GONE);
//        } else {
//            btn_left.setVisibility(View.VISIBLE);
//            btn_left.setText(getResources().getString(left_str_id));
//            btn_left.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    if (ItemClickUtils.isFastDoubleClick()) {
//                        return;
//                    }
//                    finish();
//                }
//            });
//        }
//        if (right_str_id == 0) {
//            btn_right.setVisibility(View.GONE);
//        } else {
//            btn_right.setVisibility(View.VISIBLE);
//            btn_right.setText(getResources().getString(right_str_id));
//            if(listener != null) {
//                btn_right.setOnClickListener(listener);
//            }
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void showToast(Context mContext,String msg) {
        ToastUtils.getShortToastByString(mContext,msg);
    }

//    protected  void showNavigationBar() {
//        Intent hideIntent = new Intent();
//        hideIntent.setAction(Constants.SHOW_NAVIGATION_BAR);
//        sendBroadcast(hideIntent);
//    }
//
//    protected  void hideNavigationBar() {
//        Intent hideIntent = new Intent();
//        hideIntent.setAction(Constants.HIDE_NAVIGATION_BAR);
//        sendBroadcast(hideIntent);
//    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public void showProgressDialog(Activity activity , String msg){
        showProgressDialog(activity, msg,true,null);
    }
    public void showProgressDialog(Activity activity, String msg,boolean cancelable,DialogInterface.OnCancelListener onCancelListener) {
        dismissProgressDialog();
        progressDialog = DialogUtil.createProgressDialog(activity, msg,cancelable);
        if(onCancelListener!=null) {
            progressDialog.setOnCancelListener(onCancelListener);
        }
        progressDialog.show();
    }

    public void showHProgressDialog(Activity activity,String msg,int count) {
//        dismissProgressDialog();
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }
        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(msg);
        progressDialog.setMax(count);
        progressDialog.show();
    }

    public void setHProgressPos(Activity activity,String msg,int count,int pos) {
        if (null != progressDialog&& progressDialog.isShowing()) {
            progressDialog.setProgress(pos);
        }else {
            showHProgressDialog(activity,msg,count);
            progressDialog.setProgress(pos);
        }
    }

    public void dismissProgressDialog() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    public LinearLayout getCommon_topbar() {
        return common_topbar;
    }

    public void setCommon_topbar(LinearLayout common_topbar) {
        this.common_topbar = common_topbar;
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


}
