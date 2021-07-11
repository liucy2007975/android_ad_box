package com.cow.liucy.libcommon.utils;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.cow.liucy.libcommon.logger.AppLogger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import timber.log.Timber;

import static com.cow.liucy.libcommon.utils.Constants.CMD_SERIAL_NO;


/**
 * Created by cow on 2017/8/24.
 */

public class CommonUtils {
    /**
     * 获得IP地址，分为两种情况，一是wifi下，二是移动网络下，得到的ip地址是不一样的
     */
    public static String getIPAddress() {
        Context context = Utils.getContext();
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE || info.getType() == ConnectivityManager.TYPE_ETHERNET) {//当前使用2G/3G/4G网络 或者有线
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //调用方法将int转换为地址字符串
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    public static String getMacAddress() {
        /*获取mac地址有一点需要注意的就是android 6.0版本后，以下注释方法不再适用，不管任何手机都会返回"02:00:00:00:00:00"这个默认的mac地址，这是googel官方为了加强权限管理而禁用了getSYstemService(Context.WIFI_SERVICE)方法来获得mac地址。*/
        //        String macAddress= "";
//        WifiManager wifiManager = (WifiManager) MyApp.getContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        macAddress = wifiInfo.getMacAddress();
//        return macAddress;

        String macAddress = null;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "020000000002";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X", b));
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "020000000002";
        }
        return macAddress;
    }

    private static final String FILENAME_ETHMAC = "/sys/class/net/eth0/address";
    private static final String FILENAME_WLANMAC = "/sys/class/net/wlan0/address";

    public static String getLocalMacAddress() {
        //         String mac = "ae:4b:9a:b0:30:aa";//门口
//        String mac = "ae:4b:9a:b0:30:ab";//朱工
//                String mac = "ae:4b:9a:b0:30:ac";//春娇办公室
        //        String mac = "ae:4b:9a:b0:30:ad";//测试办公室
//        String mac = "ae:4b:9a:b0:30:ae";//测试办公室2
//         String mac = "ae:4b:9a:b0:03:00";//我办公室
//         String mac = "ae:4b:9a:01:08:03";//183Ip机子
//         String mac = "ae:4b:9a:01:08:06";//186Ip机子
        String mac = "";
        try {
            String path = "sys/class/net/eth0/address";
            FileInputStream fis_name = new FileInputStream(path);
            byte[] buffer_name = new byte[1024 * 8];
            int byteCount_name = fis_name.read(buffer_name);
            if (byteCount_name > 0) {
                mac = new String(buffer_name, 0, byteCount_name, "utf-8");
//                mac="00:15:15:15:15:16";
//                mac="00:18:99:24:24:01";
//                mac="00:18:99:01:08:7F";
//                mac="9b:09:78:09:90:90";
//                Timber.e(">>>>>>mac1:" + mac);
            }

//            if(mac.length()==0||mac==null){
            path = "sys/class/net/eth0/wlan0";
            FileInputStream fis = new FileInputStream(path);
            byte[] buffer = new byte[1024 * 8];
            int byteCount = fis.read(buffer);
            if (byteCount > 0) {
                mac = new String(buffer, 0, byteCount, "utf-8");
                Timber.e(">>>>>>mac2:" + mac);
            }
//            }

            if (mac.length() == 0 || mac == null) {
                return "";
            }
        } catch (Exception io) {

        }
        return mac.trim();
    }

    public static String getSN() {
        return AppPrefs.getInstance().getSn();
//        String sn = "";
//        try {
//            String ethsn = getLocalMacAddress().toUpperCase().replaceAll(":", "");
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(ethsn);
//            sn = stringBuilder.toString();
//            if (sn.length() < 0 || sn.equals("")) {
//                sn = "6E21A98E3FC2A1FA";
//            }
//        } catch (Exception e) {
//        }
//        return sn;
    }

    /**
     * 身份证号码验证
     *
     * @param IDNumber
     * @return
     */
    public static boolean isIDNumber(String IDNumber) {
        if (IDNumber == null || "".equals(IDNumber)) {
            return false;
        }
        // 定义判别用户身份证号的正则表达式（15位或者18位，最后一位可以为字母）
        String regularExpression = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
                "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
        //假设18位身份证号码:41000119910101123X  410001 19910101 123X
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //(18|19|20)                19（现阶段可能取值范围18xx-20xx年）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|12))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十七位奇数代表男，偶数代表女）
        //[0-9Xx] 0123456789Xx其中的一个 X（第十八位为校验值）
        //$结尾

        //假设15位身份证号码:410001910101123  410001 910101 123
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|12))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十五位奇数代表男，偶数代表女），15位身份证不含X
        //$结尾


        boolean matches = IDNumber.matches(regularExpression);

        //判断第18位校验值
        if (matches) {

            if (IDNumber.length() == 18) {
                try {
                    char[] charArray = IDNumber.toCharArray();
                    //前十七位加权因子
                    int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                    //这是除以11后，可能产生的11位余数对应的验证码
                    String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                    int sum = 0;
                    for (int i = 0; i < idCardWi.length; i++) {
                        int current = Integer.parseInt(String.valueOf(charArray[i]));
                        int count = current * idCardWi[i];
                        sum += count;
                    }
                    char idCardLast = charArray[17];
                    int idCardMod = sum % 11;
                    if (idCardY[idCardMod].toUpperCase().equals(String.valueOf(idCardLast).toUpperCase())) {
                        return true;
                    } else {
                        System.out.println("身份证最后一位:" + String.valueOf(idCardLast).toUpperCase() +
                                "错误,正确的应该是:" + idCardY[idCardMod].toUpperCase());
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("异常:" + IDNumber);
                    return false;
                }
            }

        }
        return matches;
    }

    public static boolean isNation(String nation) {
        String str = nation;
        if (TextUtils.isEmpty(nation)) {
            return false;
        }
        String[] nations = new String[]{"汉族", "蒙古族", "回族", "藏族", "维吾尔族", "苗族", "彝族", "壮族", "布依族", "朝鲜族", "满族", "侗族", "瑶族", "白族", "土家族", "哈尼族", "哈萨克族", "傣族", "黎族", "僳僳族", "佤族", "畲族", "高山族", "拉祜族", "水族", "东乡族", "纳西族", "景颇族", "柯尔克孜族", "土族", "达斡尔族", "仫佬族", "羌族", "布朗族", "撒拉族", "毛南族", "仡佬族", "锡伯族", "阿昌族", "普米族", "怒族", "塔吉克族", "乌孜别克族", "俄罗斯族", "鄂温克族", "德昂族", "保安族", "裕固族", "京族", "塔塔尔族", "独龙族", "鄂伦春族", "赫哲族", "门巴族", "珞巴族", "基诺族"};
        int length = nations.length;
        int i = 0;
        while (i < length) {
            String temp = nations[i];
            if (!temp.equals(str)) {
                if (!temp.substring(0, temp.length() - 1).equals(str)) {
                    i++;
                }else {
                    return true;
                }
            }else {
                return true;
            }
        }
        return false;
    }

    public static boolean checkFaceId(String faceId) {
        if (faceId == null || !faceId.matches("[a-zA-Z0-9_\\-]{1,36}")) {
            return false;
        }
        return true;
    }

    /**
     * 获取APP命令发送序列号
     *
     * @return
     */
    public synchronized static Integer getAppSerialNo() {
        if (CMD_SERIAL_NO++ >= Integer.MAX_VALUE) {//超过short最大值则重置值
            CMD_SERIAL_NO = 1;
        }
        return CMD_SERIAL_NO;
    }

    /**
     * list 转带分隔符字符串
     *
     * @return string
     */
    public static String listToString(List list, char separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                sb.append(list.get(i));
            } else {
                sb.append(list.get(i));
                sb.append(separator);
            }
        }
        return sb.toString();
    }


    /**
     * string 转 list
     *
     * @return list
     */
    public static List<Integer> stringToList(String str, String seperator) {
        List<Integer> list = new ArrayList<>();
        Timber.e(">>>str:" + str);
        if (str != null && !str.equals("")) {
            List<String> listStr = Arrays.asList(str.split(seperator));
            for (int count = 0; count < listStr.size(); count++) {
                if (listStr.get(count) != null) {
                    try {
                        Timber.e("stringToList>>>>>:" + listStr.get(count));
                        list.add(Integer.valueOf(listStr.get(count)));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Timber.e(">>>>>>>>>e:" + e.getMessage());
                    }
                }
            }
        } else {
            return list;
        }
        return list;
    }

    public static <T extends Comparable<T>> boolean compare(List<T> a, List<T> b) {
        if (a.size() != b.size()) {
            return false;
        }

        Collections.sort(a);
        Collections.sort(b);
        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).equals(b.get(i))) {
                return false;
            }
        }
        return true;
    }
    /**
     * 获取OS version
     *
     * @return
     */
    public static String getVersion() {
        return android.os.Build.SERIAL;
    }

    /**
     * 获取厂商信息
     *
     * @return
     */
    public static String getManufacturer() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取系统版本号
     *
     * @return
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取子掩码
     *
     * @return
     */
    public static String getNetMask() {
        Context context = Utils.getContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Service.CONNECTIVITY_SERVICE);
        //判断网络连接的是以太网
        if (connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_ETHERNET) {
            //获取网络信息
            List<LinkAddress> linkAddressList = connectivityManager.getLinkProperties(connectivityManager.getAllNetworks()[0]).getLinkAddresses();
            if (Valid.valid(linkAddressList)) {
                String maskAddress = calcMaskByPrefixLength(linkAddressList.get(0).getPrefixLength());
                return maskAddress;
            }
        }
        return null;

    }

    private static String calcMaskByPrefixLength(int length) {
        int mask = 0xffffffff << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int maskParts[] = new int[partsNum];
        int selector = 0x000000ff;

        for (int i = 0; i < maskParts.length; i++) {
            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }

        String result = "";
        result = result + maskParts[0];
        for (int i = 1; i < maskParts.length; i++) {
            result = result + "." + maskParts[i];
        }
        return result;
    }


    //获取网关地址
    public static String getGateWay() {
        return calcSubnetAddress(getIPAddress(), getNetMask());
    }

    public static String calcSubnetAddress(String ip, String mask) {
        AppLogger.e(">>>获取网关:ip="+ip+",mask="+mask);
        String result = "";
        try {
            // calc sub-net IP
            InetAddress ipAddress = InetAddress.getByName(ip);
            InetAddress maskAddress = InetAddress.getByName(mask);

            byte[] ipRaw = ipAddress.getAddress();
            byte[] maskRaw = maskAddress.getAddress();

            int unsignedByteFilter = 0x000000ff;
            int[] resultRaw = new int[ipRaw.length];
            for (int i = 0; i < resultRaw.length; i++) {
                resultRaw[i] = (ipRaw[i] & maskRaw[i] & unsignedByteFilter);
            }

            // make result string
            result = result + resultRaw[0];
            for (int i = 1; i < resultRaw.length; i++) {
                result = result + "." + resultRaw[i];
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 根据adb shell命令获取网关信息（static下）
     * @return
     */
    public static String getGatewayForStatic() {
        BufferedReader bufferedReader = null;
        String result="";
        String str2 = "";
        String str3 = "ip route list table 0";
        Process exec;
        BufferedReader bufferedReader2 = null;
        try {
            exec = Runtime.getRuntime().exec(str3);
            try {
                bufferedReader2 = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            } catch (Throwable th3) {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (exec != null) {
                    exec.exitValue();
                }
            }
            try {
                str2 = bufferedReader2.readLine();
                if (str2 != null) {
                    str2= str2.trim();
                    String[] strings=str2.split("\\s+");
                    if (strings.length>3){
                        result= strings[2];
                    }
                }
                try {
                    bufferedReader2.close();
                } catch (IOException iOException222) {
                    iOException222.printStackTrace();
                }
                if (exec != null) {
                    try {
                        exec.exitValue();
                    } catch (Exception e5) {
                    }
                }
            } catch (IOException e6) {
                if (bufferedReader2 != null) {
                    bufferedReader2.close();
                }
                if (exec != null) {
                    exec.exitValue();
                }
                return result;
            }
        } catch (IOException e62) {
            bufferedReader2 = null;
            exec = null;
            if (bufferedReader2 != null) {
                try {
                    bufferedReader2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (exec != null) {
                exec.exitValue();
            }
            return result;
        } catch (Throwable th4) {
            exec = null;
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (exec != null) {
                exec.exitValue();
            }
        }
        return result;
    }

}
