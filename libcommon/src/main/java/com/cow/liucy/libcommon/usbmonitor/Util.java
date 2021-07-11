package com.cow.liucy.libcommon.usbmonitor;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Util {
    private static String appName = "";

    public static String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }

    public static Drawable getDrawable(Context context, int resId) {
        Resources res = context.getResources();
        Bitmap bitmapTemp = decodeSampledBitmapFromResource(res, resId);
        //Bitmap bitmap = compressImage(bitmapTemp);
        return bitmap2Drawable(context, bitmapTemp);
    }

    private static Drawable bitmap2Drawable(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    private static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 10) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 执行Android命令
     *
     * @param cmd 命令
     */
    public static void execSuCmd(String cmd) {
        Process process = null;
        DataOutputStream os = null;
        DataInputStream is = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            int aa = process.waitFor();
            is = new DataInputStream(process.getInputStream());
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            String out = new String(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
                if (process != null) {
                    process.destroy();
                }

            } catch (Exception e) {
            }
        }
    }

    public static String readGPIO44() {
        String valuePath = "/sys/class/gpio/gpio44/value";
        return readGPIO(valuePath);
    }

    public static String readGPIO(String valuePath) {
        String def_value = "";
        File value_file = new File(valuePath);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(value_file), "UTF-8"));
            def_value = br.readLine();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return def_value;
    }

    public static String getDataFromSharePreferences(Context context, String filename, String elementname) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        String data = sharedPreferences.getString(elementname, "");
        return data;
    }

    public static void putDataBySharePreferences(Context context, String filename, String elementname, String content) {
        //步骤1：创建一个SharedPreferences对象
        SharedPreferences sharedPreferences = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        //步骤2： 实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //步骤3：将获取过来的值放入文件
        editor.putString(elementname, content);
        //步骤4：提交
        editor.commit();
    }

    /**
     * 获取动态控件的宽度
     *
     * @param view
     * @return
     */
    public static int getWidth(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        return view.getMeasuredWidth();
    }

    /**
     * 禁止Edittext弹出软件盘，光标依然正常显示。
     */
    public static void disableShowSoftInput(EditText editText) {
        if (Build.VERSION.SDK_INT <= 10) {
            editText.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
            } catch (Exception e) {
                // TODO: handle exception
            }

            try {
                method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    /**
     * 获取参数string的异或校验
     *
     * @param string
     * @return 返回校验的结果
     */
    public static String getCheck(String string) {
        byte[] bytes = Util.HexString2Bytes(string);
        byte b_res = bytes[0];
        for (int i = 1; i < bytes.length; ++i) {
            b_res ^= bytes[i];
        }
        return toHexString(b_res);
    }

    /**
     * 将两个ASCII字符合成一个字节；
     * 如："EF"--> 0xEF
     *
     * @param src0 byte
     * @param src1 byte
     * @return byte
     */
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式
     * 如："2B44EFD9" –> byte[]{0x2B, 0×44, 0xEF, 0xD9}
     *
     * @param src String
     * @return byte[]
     */
    public static byte[] HexString2Bytes(String src) {
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < tmp.length / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    /**
     * 数组转换成十六进制字符串
     *
     * @param //byte[]
     * @return HexString
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static String toHexString(byte b) {
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1) {
            return "0" + s;
        } else {
            return s;
        }
    }

    /**
     * @param fileName 提供完整路径
     * @return
     */
    public static String _getJsonStringBypath(String fileName) {
        if ((fileName == null) || fileName.isEmpty()) {
            return "";
        }
        String retString = "";
        FileInputStream fis = null;
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(fileName);
            if (file.exists()) {
                try {
                    fis = new FileInputStream(file);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    fis.close();

                    retString = new String(buffer);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {

            }
        }
        return retString;
    }

    /**
     * @param fileName 相对路径，直接是文件名
     * @return
     */
    public static String _getJsonString(String fileName) {
        if ((fileName == null) || fileName.isEmpty()) {
            return "";
        }
        String retString = "";
        FileInputStream fis = null;
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory()
                    + "/" + fileName + ".json");
            if (file.exists()) {
                try {
                    fis = new FileInputStream(file);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    fis.close();

                    retString = new String(buffer);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {

            }
        }
        return retString;
    }

    /**
     * @param fileName 文件名，不需要后缀
     * @param content  要写入的内容
     *                 调用
     *                 Gson gson = new Gson();
     *                 saveAsJson("config", gson.toJson(serverInfo));
     */
    public static boolean saveAsJson(String fileName, String content) {
        boolean b_result = false;
        FileOutputStream fos = null;
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File path = new File(Constant.TARGET_PATH);
            if ((path != null) && !path.exists()) {
                path.mkdirs();
            }
            File file = new File(Environment.getExternalStorageDirectory()
                    + "/" + fileName + ".json");
            try {
                fos = new FileOutputStream(file);
                byte[] buffer = content.getBytes();
                fos.write(buffer);
                fos.close();
                b_result = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return b_result;
    }

    /**
     * 字符串的长度 0.0.0.0 7位 ~ 000.000.000.000 15位
     * 将字符串拆分成四段
     * 检查每段是否都是纯数字
     * 检查每段是否都在0-255之间
     * 以上条件都满足的话返回true
     */
    public boolean isIPAddress(String str) {
        // 如果长度不符合条件 返回false
        if (str.length() < 7 || str.length() > 15) return false;
        String[] arr = str.split("\\.");
        //如果拆分结果不是4个字串 返回false
        if (arr.length != 4) return false;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < arr[i].length(); j++) {
                char temp = arr[i].charAt(j);
                //如果某个字符不是数字就返回false
                if (!(temp > '0' && temp < '9')) return false;
            }
        }
        for (int i = 0; i < 4; i++) {
            int temp = Integer.parseInt(arr[i]);
            //如果某个数字不是0到255之间的数 就返回false
            if (temp < 0 || temp > 255) return false;
        }
        return true;
    }

    /**
     * 用正则表达式进行判断
     */
    public static boolean isIPAddressByRegex(String str) {
        String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
        // 判断ip地址是否与正则表达式匹配
        if (str.matches(regex)) {
            String[] arr = str.split("\\.");
            for (int i = 0; i < 4; i++) {
                int temp = Integer.parseInt(arr[i]);
                //如果某个数字不是0到255之间的数 就返回false
                if (temp < 0 || temp > 255) return false;
            }
            return true;
        } else return false;
    }

    //隐藏虚拟按键：
    public static void hideBottomUIMenu(Context context) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = ((Activity) context).getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = ((Activity) context).getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public static void hideNavigationBar(Context context) {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if (Build.VERSION.SDK_INT >= 19) {
            uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE;//0x00001000; // SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }

        try {
            ((Activity) context).getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static void deleteAllFiles(String path) {
        //LogUtil.d(TAG, "delete file");
        File root = new File(path);
        if (!root.exists()) {
            root.mkdirs();
            return;
        }
        File files[] = root.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f.getAbsolutePath());
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f.getAbsolutePath());
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    public static String getPackageName(String absPath, Context context) {
        ApplicationInfo appInfo = apkInfo(absPath, context);
        return appInfo.packageName;
    }


    /**
     * 获取apk包的信息：版本号，名称，图标等
     *
     * @param //absPath  apk包的绝对路径
     * @param //context 
     */
    public static ApplicationInfo apkInfo(String absPath, Context context) {
        ApplicationInfo appInfo = null;
        String version = "";
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath, PackageManager.GET_ACTIVITIES);
        if (pkgInfo != null) {
            appInfo = pkgInfo.applicationInfo;
            /* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
            appInfo.sourceDir = absPath;
            appInfo.publicSourceDir = absPath;
            String appName = pm.getApplicationLabel(appInfo).toString();// 得到应用名 
            String packageName = appInfo.packageName; // 得到包名
            version = pkgInfo.versionName; // 得到版本信息
            /* icon1和icon2其实是一样的 */
            //获取u盘里面的文件图片会导致文件占用
            //Drawable icon1 = pm.getApplicationIcon(appInfo);// 得到图标信息
            //Drawable icon2 = appInfo.loadIcon(pm);
            String pkgInfoStr = String.format("PackageName:%s, Vesion: %s, AppName: %s", packageName, version, appName);
            //Log.i(MainActivity.TAG, String.format("PkgInfo: %s", pkgInfoStr));
        }
        return appInfo;
    }

    public static boolean doCheckPackageName(Context context, String app) {
        //测试代码
        //String packageName = "";
        String packageName = getPackageName(app, context);
        String cur_package_name = getCurPackageName(context);
        if (cur_package_name.equals(packageName)) {
            return true;
        } else {
            //Log.d(MainActivity.TAG, "包名不一致，不需要更新");
        }
        return false;
    }

    private static String getCurPackageName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }

    public static String doSearchFile(String path, String name) {
        //LogUtil.d(TAG, "doSearchFile");
        File[] fileArray;
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                fileArray = file.listFiles();
                if (fileArray != null) {
                    for (File f : fileArray) {
                        if (f.isDirectory()) {
                            doSearchFile(f.getPath(), name);
                        } else {
                            //Log.d(MainActivity.TAG, "file: " + f.getName());
                            if (f.getName().endsWith(name)) {
                                //LogUtil.d(TAG, "file: "+f.getName());
                                appName = f.getAbsolutePath();
                                return appName;
//								LogUtil.d(TAG, "list: "+ fileTempList.toString());
                            } else {
                                appName = "";
                            }
                        }
                    }
                }
            }
        }
        return appName;
    }

    public static String doSearchApk(Context context, String path) {
        //LogUtil.d(TAG, "doSearchApk");
        File[] fileArray;
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                fileArray = file.listFiles();
                if (fileArray != null) {
                    for (File f : fileArray) {
                        if (f.isDirectory()) {
                            doSearchApk(context, f.getPath());
                        } else {
                            //Log.d(MainActivity.TAG, "file: " + f.getName());
                            if (f.getName().endsWith(".apk")) {
                                //LogUtil.d(TAG, "file: "+f.getName());
                                appName = f.getAbsolutePath();
                                if (doCheckPackageName(context, appName)) {
                                    return appName;
                                } else {
                                    appName = "";
                                }
//								LogUtil.d(TAG, "list: "+ fileTempList.toString());
                            } else {
                                appName = "";
                            }
                        }
                    }
                }
            }
        }
        return appName;
    }

    /**
     * 判断路径是否存在
     *
     * @param path 需要判断的路径
     * @return true 是存在，false 是不存在
     */
    public static boolean isPathExist(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            return true;
        }
        return false;
    }

    /**
     * 判断文件是否存在
     *
     * @param name 需要判断的路径
     * @return true 是存在，false 是不存在
     */
    public static boolean isFileExist(String name) {
        File file = new File(name);
        if (file.exists() && file.isFile()) {
            return true;
        }
        return false;
    }

    public static String getStoragePath(Context context) {
        String storagePath = null;
        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?> storeManagerClazz = Class.forName("android.os.storage.StorageManager");
            Method getVolumesMethod = storeManagerClazz.getMethod("getVolumes");
            List<?> volumeInfos = (List<?>) getVolumesMethod.invoke(mStorageManager);//获取到了VolumeInfo的列表
            Class<?> volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo");
            Method getFsUuidMethod = volumeInfoClazz.getMethod("getFsUuid");
            Field pathField = volumeInfoClazz.getDeclaredField("path");
            if (volumeInfos != null) {
                for (Object volumeInfo : volumeInfos) {
                    String uuid = (String) getFsUuidMethod.invoke(volumeInfo);
                    if (uuid != null) {
                        storagePath = (String) pathField.get(volumeInfo);
                    }
                }
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return storagePath;
    }

    public static boolean copyFileUsingFileChannels(File source, File dest) {
        boolean b_ret = true;
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(dest);
            inputChannel = fileInputStream.getChannel();
            outputChannel = fileOutputStream.getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            //Log.e(MainActivity.TAG, "copyFileUsingFileChannels IOException 错误！！！");
            b_ret = false;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (inputChannel != null) {
                    inputChannel.close();
                }
                if (outputChannel != null) {
                    outputChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                b_ret = false;
                //Log.e(MainActivity.TAG, "copyFileUsingFileChannels IO 错误！！！");
            }
        }
        return b_ret;
    }

    public static void doUpdate(Context context, String app) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(app)), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void doRestartApp(Activity context) {
        Intent intent = context.getBaseContext().getPackageManager().getLaunchIntentForPackage(context.getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static Boolean writeStrToFile2(String str, String name, String path, Boolean supplements) {
        String old_str;
        File file_path = new File(path);
        if (!file_path.exists()) {
            file_path.mkdirs();
        }
        File file = new File(path + name);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
        try {
            FileWriter fileWritter = new FileWriter(file);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            if (supplements) {
                String old_data;
                old_data = getStrFromFile2(name, path);
                if (TextUtils.isEmpty(old_data)) {
                    old_str = old_data;
                } else {
                    old_str = "";
                }
                str = old_str + str;
            }
            bufferWritter.write(str);
            bufferWritter.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String getStrFromFile2(String name, String path) {
        //Log.d(MainActivity.TAG, "=name=>>" + name + "=path=>>" + path);
        String result = "";
        File file = new File(path + name);
        if (!file.exists()) {
            result = "";
        } else {
            try {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String str = "";
                String mimeTypeLine = null;
                while ((mimeTypeLine = br.readLine()) != null) {
                    str = str + mimeTypeLine;
                }
                br.close();
                result = str;
            } catch (Exception e) {
                result = "";
            }
        }
        return result;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
}
