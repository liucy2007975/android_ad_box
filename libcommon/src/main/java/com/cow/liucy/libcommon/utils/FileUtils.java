package com.cow.liucy.libcommon.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.cow.liucy.libcommon.logger.AppLogger;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import timber.log.Timber;

import static com.cow.liucy.libcommon.utils.Constants.APP_DEF_PATH_IMG;

/**
 * Created by fanyufeng on 2017-7-17.
 */

public final class FileUtils {


    public static String[] getFolderList(String path) throws Exception {
        File dir = new File(path);
        String[] files = null;
        try {
            files = dir.list();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;
    }

    // 获取指定文件夹内所有文件大小的和
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }


    // 格式化单位
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    //根据文件后缀获取判断文件类型
    public static String getFileType(String path) {
//        Timber.e("getFileType = "+path);
        String fileType;
        fileType = path.substring(path.lastIndexOf(".") + 1);
        fileType = fileType.toLowerCase();
        switch (fileType) {
            case "mp4":
                fileType = "video";
                break;
            case "png":
            case "jpg":
            case "jpeg":
                fileType = "img";
                break;
            case "apk":
                fileType = "apk";
                break;
            default:
                break;
        }
        return fileType;
    }

    /**
     * 根据文件路径，类型获取文件列表
     *
     * @param rootPath 文件路径
     * @param type     文件类型 video:视频文件 img:图片文件 apk:应用文件
     * @return 文件
     */
    public static List<String> getFileList(String rootPath, String type) {
        List<String> tempList = new ArrayList<>();
        List<String> resultList = new ArrayList<>();
        try {
            tempList = Arrays.asList(FileUtils.getFolderList(rootPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String path : tempList) {
            if (FileUtils.getFileType(path).equals(type)) {
                resultList.add(rootPath + path);
            }
        }

        return resultList;

    }

    /**
     * 根据文件路径，名称关键字获取文件列表
     *
     * @param rootPath 文件路径
     * @param keyword  文件名称中包含的关键字
     * @return 文件
     */
    public static List<String> getFileListByKeyword(String rootPath, String keyword) {
        List<String> tempList = new ArrayList<>();
        List<String> resultList = new ArrayList<>();
        try {
            tempList = Arrays.asList(FileUtils.getFolderList(rootPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String path : tempList) {
            if (path.contains(keyword)) {
                resultList.add(rootPath + path);
            }
        }

        return resultList;

    }

    public static void isExist(String path) {
        File file = new File(path);

        try {
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Timber.e(e);
        }
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断目录是否存在
     *
     * @param file 文件
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isExistsDir(final File file) {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && (file.exists());
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(final File file) {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 复制目录
     *
     * @param srcDirPath  源目录路径
     * @param destDirPath 目标目录路径
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public static boolean copyDir(final String srcDirPath, final String destDirPath) {
        return copyDir(getFileByPath(srcDirPath), getFileByPath(destDirPath));
    }

    /**
     * 复制目录
     *
     * @param srcDir  源文件
     * @param destDir 目标文件
     * @return {@code true}: 复制成功<br>{@code false}: 复制失败
     */
    public static boolean copyDir(final File srcDir, final File destDir) {
        if (srcDir == null || destDir == null) return false;
        // 如果目标目录在源目录中则返回false，看不懂的话好好想想递归怎么结束
        // srcPath : F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res
        // destPath: F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res1
        // 为防止以上这种情况出现出现误判，须分别在后面加个路径分隔符
        String srcPath = srcDir.getPath() + File.separator;
        String destPath = destDir.getPath() + File.separator;
        Timber.e("srcPath=" + srcPath);
        Timber.e("destPath=" + destPath);
        if (destPath.contains(srcPath)) return false;
        // 源文件不存在或者不是目录则返回false
        if (!srcDir.exists() || !srcDir.isDirectory()) return false;
        // 目标目录不存在返回false
        if (!createOrExistsDir(destDir)) return false;
        File[] files = srcDir.listFiles();
        for (File file : files) {
            File oneDestFile = new File(destPath + file.getName());
            if (file.isFile()) {
                // 如果操作失败返回false
                try {
                    org.greenrobot.essentials.io.FileUtils.copyFile(file, oneDestFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (file.isDirectory()) {
                // 如果操作失败返回false
                copyDir(file, oneDestFile);
            }
        }
        return true;
    }

    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 输出信息到txt文件
     */
    public static void writeMsgToFile(String path, String msg) {
        StringBuffer sb = new StringBuffer();

        String logTime = DateTimeUtils.getDatetime(new Date());
        sb.append(logTime);
        sb.append("    ");
        sb.append(msg);
        sb.append("    ");
        sb.append("\n");

        try {
            String fileName = "TemperatureLog.txt";
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                File dir = new File(path, fileName);
                if (!dir.exists()) {
//                    dir.mkdirs();
                    dir.createNewFile();
                    FileOutputStream fos = new FileOutputStream(path + fileName);
                    fos.write(sb.toString().getBytes());
                    fos.flush();
                    fos.close();
                } else {
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(path + fileName, true)));
                    out.write(sb.toString());
                    out.flush();
                    out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Bitmap saveBitmap(byte[] picData, String filePath) {
        FileOutputStream fos = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            Bitmap bmp = BitmapFactory.decodeByteArray(picData, 0, picData.length);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            fos = null;
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static Bitmap saveBitmap(Bitmap bmp, String filePath) {
        FileOutputStream fos = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            fos = null;
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }



    //flie：要删除的文件夹的所在位置
    public static void deleteAllFile(File file) {
        AppLogger.e("deleteFileName="+file.getAbsolutePath());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteAllFile(f);
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 获取图片byte[]
     *
     * @param path
     * @return
     */
    public static byte[] image2byte(String path) {
        byte[] data = null;
        FileInputStream input = null;
        try {
            input = new FileInputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            input.close();
        } catch (FileNotFoundException ex1) {
            ex1.printStackTrace();
        } catch (IOException ex1) {
            ex1.printStackTrace();
        }
        return data;
    }

    /**
     * 保存图片到本地
     *
     * @param data
     * @return
     */
    public static Boolean saveImage(byte[] data, String name) {
        try {
            File file = new File(APP_DEF_PATH_IMG + name);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            FileOutputStream outStream = null;
            outStream = new FileOutputStream(file);
            outStream.write(data);
            outStream.flush();
            outStream.close();
            AppLogger.e(">>>>>>保存图片文件到本地成功：");
            return true;
        } catch (Exception e) {
            AppLogger.e(">>>>>>保存图片文件到本地失败：" + e.getMessage());
        }
        return false;
    }

    /* 删除7天前的日记 */

    public static void deleteOldLog() {

        Context mContext = Utils.getContext();
        String FILE_SEP = System.getProperty("file.separator");

        String logDirectorySD = "";
        String logDirectoryPV = "";

        List<String> LogNameSD = null;
        List<String> LogNamePV = null;

        File file = null;

        String strNowDate = DateTimeUtils.getDate(new Date());
        Date nowDate = DateTimeUtils.parse2(strNowDate);
        long currentTime = nowDate.getTime();

        //SD卡目录下的Log
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && mContext.getExternalCacheDir() != null) {

            logDirectorySD = mContext.getExternalCacheDir() + FILE_SEP + "logs" + FILE_SEP;

            File logDirectoryFileSD = new File(logDirectorySD);
            if (logDirectoryFileSD.exists()) {
                LogNameSD = FileUtils.getFileListByKeyword(logDirectorySD, "my-log");
            }


        }
        //私有目录下的Log
        if (mContext.getCacheDir() != null) {

            logDirectoryPV = mContext.getCacheDir() + FILE_SEP + "logs" + FILE_SEP;

            File logDirectoryFilePV = new File(logDirectoryPV);
            if (logDirectoryFilePV.exists()) {
                LogNamePV = FileUtils.getFileListByKeyword(logDirectoryPV, "my-log");
            }

        }


        if (Valid.valid(logDirectorySD) && Valid.valid(LogNameSD)) {

            for (int i = 0; i < LogNameSD.size(); i++) {

                file = new File(LogNameSD.get(i));
                String fileName = file.getName();

                if (fileName.equals("my-log-latest.html")) {
                    continue;
                }

                int start = fileName.indexOf(".");
                int end = fileName.lastIndexOf(".");

                if (start != -1 && end != -1) {

                    String subName = fileName.substring(start + 1, end - 2);

                    if (subName.length() == 10 && Valid.valid(subName)) {

                        Date date = DateTimeUtils.parse2(subName);
                        long oldTime = date.getTime();

                        if (currentTime - oldTime > 7 * DateTimeUtils.MILSECONDS_PER_DAY) {

                            FileUtils.deleteFile(LogNameSD.get(i));
                        }
                    }
                } else {
                    continue;
                }

            }
            AppLogger.e("SD卡目录下前7天的Log文件删除完毕");

        }

        if (Valid.valid(logDirectoryPV) && Valid.valid(LogNamePV)) {

            for (int i = 0; i < LogNamePV.size(); i++) {

                file = new File(LogNamePV.get(i));
                String fileName = file.getName();

                if (fileName.equals("my-log-latest.html")) {
                    continue;
                }

                int start = fileName.indexOf(".");
                int end = fileName.lastIndexOf(".");

                if (start != -1 && end != -1) {

                    String subName = fileName.substring(start + 1, end - 2);

                    if (subName.length() == 10 && Valid.valid(subName)) {

                        Date date = DateTimeUtils.parse2(subName);
                        long oldTime = date.getTime();

                        if (currentTime - oldTime > 7 * DateTimeUtils.MILSECONDS_PER_DAY) {

                            FileUtils.deleteFile(LogNamePV.get(i));
                        }
                    }
                } else {
                    continue;
                }

            }
        }
        AppLogger.e("私有目录下前7天的Log文件删除完毕");
    }

    public static boolean copySdcardFile(File fromFile, String toFile, boolean deleteDif) throws IOException {
        File targetFile = new File(toFile);
        String md5 = getFileMD5(fromFile);
        if (md5 == null) return false;
        while (targetFile.exists()) {
            if (deleteDif) {
                targetFile.delete();
            } else {
                if (md5.equals(getFileMD5(targetFile))) {
                    return true;
                } else {
                    toFile = getCopyNameFromOriginal(toFile);
                    targetFile = new File(toFile);
                }
            }
        }
        targetFile.createNewFile();

        InputStream fosfrom = new FileInputStream(fromFile);
        OutputStream fosto = new FileOutputStream(targetFile);
        byte bt[] = new byte[1024];
        int c;
        while ((c = fosfrom.read(bt)) > 0) {
            fosto.write(bt, 0, c);
        }
        fosfrom.close();
        fosto.close();
        return true;
    }

    /**
     * 获取单个文件的MD5值！
     *
     * @param file 文件
     * @return 文件的md5值
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    final static String regex = "-";

    /**
     * @param originalName 原本的名字，XXX.xx 或者完整路径 xx/xx/XXX.xx ， 也可以没有后缀.xx
     * @return 副本名称
     * @Description 得到文件副本名称，可供粘贴及多选重命名方法使用
     * 命名规则为：普通文件后加“ 1”，若文件末尾已有“ 数字”，则数字递增。
     * 比如，有个文件叫“我.jpg”，使用本方法后得到了“我 1.jpg”，再次使用本方法后得到“我 2.jpg”
     */
    public static String getCopyNameFromOriginal(final String originalName) {
        //1.判断阈值
        if (originalName == null || originalName.isEmpty()) {
            return null;
        }
        String copyName = null;
        //2.得到文件名和后缀名
        String[] nameAndExt = getNameAndExtFromOriginal(originalName);
        if (nameAndExt == null) {
            return null;
        }
        String fileName = nameAndExt[0];
        String fileExt = nameAndExt[1];
        //3.判断文件名是否包含我们定义副本规范的标记字符（空格）
        if (fileName.contains(regex)) { //如果文件名包涵空格，进行判断是否已经为副本名称
            //4-1.得到end
            String[] array = fileName.split(regex);
            String end = array[array.length - 1]; //得到标记字符后面的值
            //4-2.确保end得到的是最后面的值（防止出现类似路径中的目录也有标记字符的情况，如："mnt/sda/wo de/zhao pian/我的 照片 1.png"）
            while (end.contains(regex)) {
                array = fileName.split(regex);
                end = array[array.length - 1];
            }
            //5.判断标记字符后的字符串是否复合规范（是否是数字）
            boolean isDigit = end.matches("[0-9]+"); //用正则表达式判断是否是正整数
            if (isDigit) {
                try {
                    int index = Integer.parseInt(end) + 1; //递增副本记数
                    int position = fileName.lastIndexOf(regex); //得到最后的空格的位置，用于截取前面的字符串
                    if (position != -1) {
                        //6-1.构造新的副本名（数字递增）
                        copyName = fileName.substring(0, position + 1) + String.valueOf(index);
                    }
                } catch (Exception e) { //转化成整形错误
                    e.printStackTrace();
                    return null;
                }
            } else { //如果空格后不是纯数字，即不为我们定义副本的规范
                //6-2.构造新的副本名（数字初始为1）
                copyName = fileName + regex + "1";
            }
        } else { //如果没有，则变为副本名称格式
            //6-3.构造新的副本名（数字初始为1）
            copyName = fileName + regex + "1";
        }
        String path = getPathFromFilepath(originalName);
//        ILog.i("new copy name is " + path + File.separator + copyName +"."+fileExt);
        //6.返回副本名+后缀名

        return path + File.separator + copyName + "." + fileExt;
    }

    private static String[] getNameAndExtFromOriginal(String originalName) {
        int pos = originalName.lastIndexOf('/');
        String fileName = originalName.substring(pos + 1, originalName.length());
//        ILog.i("fileName = "+fileName);
        String[] temps = fileName.split("\\.");
//        ILog.i("getNameAndExtFromOriginal temps.length = "+temps.length);
        return temps;
    }

    /**
     * @param filepath 文件全路径名称，like mnt/sda/XX.xx
     * @return 根路径，like mnt/sda
     * @Description 得到文件所在路径（即全路径去掉完整文件名）
     */
    public static String getPathFromFilepath(final String filepath) {
        int pos = filepath.lastIndexOf('/');
        if (pos != -1) {
            return filepath.substring(0, pos);
        }
        return "";
    }

    public static void unZipFile(String zipPath, String outputDirectory) throws IOException {
        /**
         * 解压assets的zip压缩文件到指定目录
         * @param context上下文对象
         * @param assetName压缩文件名
         * @param outputDirectory输出目录
         * @param isReWrite是否覆盖
         * @throws IOException
         */

        Log.i("ZIP", "开始解压的文件： " + zipPath + "\n" + "解压的目标路径：" + outputDirectory);
        // 创建解压目标目录
        File file = new File(outputDirectory);
        // 如果目标目录不存在，则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        // 打开压缩文件
        InputStream inputStream = new FileInputStream(zipPath);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        // 读取一个进入点
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        // 使用1Mbuffer
        byte[] buffer = new byte[1024 * 1024];
        // 解压时字节计数
        int count = 0;
        // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
        while (zipEntry != null) {
            Log.i("ZIP", "解压文件 入口 1： " + zipEntry);
            if (!zipEntry.isDirectory()) {  //如果是一个文件
                // 如果是文件
                String fileName = zipEntry.getName();
                Log.i("ZIP", "解压文件 原来 文件的位置： " + fileName);
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);  //截取文件的名字 去掉原文件夹名字
                Log.i("ZIP", "解压文件 的名字： " + fileName);
                file = new File(outputDirectory + File.separator + fileName);  //放到新的解压的文件路径

                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                while ((count = zipInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, count);
                }
                fileOutputStream.close();

            }

            // 定位到下一个文件入口
            zipEntry = zipInputStream.getNextEntry();
            Log.i("ZIP", "解压文件 入口 2： " + zipEntry);
        }
        zipInputStream.close();
        Log.i("ZIP", "解压完成");

    }

    /**
     * 读取文件内容
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String readFile(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder("");
        //打开文件输入流
        FileInputStream inputStream = new FileInputStream(filePath);

        byte[] buffer = new byte[1024];
        int len = inputStream.read(buffer);
        //读取文件内容
        while (len > 0) {
            sb.append(new String(buffer, 0, len,"UTF-8"));

            //继续将数据放到buffer中
            len = inputStream.read(buffer);
        }
        //关闭输入流
        inputStream.close();
        return sb.toString();
    }
}
