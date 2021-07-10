package com.cow.liucy.hdxm.libcommon.utils;

import android.content.Context;
import android.net.LinkAddress;
import android.net.ProxyInfo;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;


import com.cow.liucy.hdxm.libcommon.logger.AppLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.List;

/**
 * @author fanyufeng
 * @date 2018-1-29
 */

public class NetUtil {

    public static final String TAG = "NetUtil";

    private static Object ipConfigurationInstance;

    public static void setIp(String ip, String dns1, String gateWay) throws Exception {

        //获取ETHERNET_SERVICE参数
        String ETHERNET_SERVICE = (String) Context.class.getField("ETHERNET_SERVICE").get(null);

        Class<?> ethernetManagerClass = Class.forName("android.net.EthernetManager");

        Class<?> ipConfigurationClass = Class.forName("android.net.IpConfiguration");

        //获取ethernetManager服务对象
        Object ethernetManager = Utils.getContext().getSystemService(ETHERNET_SERVICE);

        Object getConfiguration = ethernetManagerClass.getDeclaredMethod("getConfiguration").invoke(ethernetManager);

        Log.e(TAG, "ETHERNET_SERVICE : " + ETHERNET_SERVICE);

        //获取在EthernetManager中的抽象类mService成员变量
        Field mService = ethernetManagerClass.getDeclaredField("mService");

        //修改private权限
        mService.setAccessible(true);

        //获取抽象类的实例化对象
        Object mServiceObject = mService.get(ethernetManager);

        Class<?> iEthernetManagerClass = Class.forName("android.net.IEthernetManager");

        Method[] methods = iEthernetManagerClass.getDeclaredMethods();

        for (Method ms : methods) {
            AppLogger.e("IEthernetManager方法=" + ms.getName());

            if (ms.getName().equals("setEthernetEnabled")) {

                ms.invoke(mServiceObject, true);

                Log.e(TAG, "mServiceObject : " + mServiceObject);

            }

        }
        Class<?> staticIpConfig = Class.forName("android.net.StaticIpConfiguration");

        Constructor<?> staticIpConfigConstructor = staticIpConfig.getDeclaredConstructor(staticIpConfig);

        Object staticIpConfigInstance = staticIpConfig.newInstance();

        //获取LinkAddress里面只有一个String类型的构造方法
        Constructor<?> linkAddressConstructor = LinkAddress.class.getDeclaredConstructor(String.class);

        //实例化带String类型的构造方法
        //192.168.1.1/24--子网掩码长度,24相当于255.255.255.0
        LinkAddress linkAddress = (LinkAddress) linkAddressConstructor.newInstance(ip);

        //获取staticIpConfig中所有的成员变量
        Field[] declaredFields = staticIpConfigInstance.getClass().getDeclaredFields();

        for (Field f : declaredFields) {
            AppLogger.e("staticIpConfig中所有的成员变量=" + f.getName());

            //设置成员变量的值
            if (f.getName().equals("ipAddress")) {

                //设置IP地址和子网掩码
                f.set(staticIpConfigInstance, linkAddress);

            } else if (f.getName().equals("gateway")) {

                //设置默认网关
                f.set(staticIpConfigInstance, InetAddress.getByName(gateWay));

            } else if (f.getName().equals("domains")) {

                f.set(staticIpConfigInstance, "");

            } else if (f.getName().equals("dnsServers")) {

                //设置DNS
                List<InetAddress> dnsServers = (List<InetAddress>) staticIpConfigInstance.getClass().getField("dnsServers").get(staticIpConfigInstance);
                dnsServers.clear();
                dnsServers.add(InetAddress.getByName(dns1));
                // Google DNS as DNS2 for safety
                dnsServers.add(InetAddress.getByName("8.8.8.8"));

            }

        }
        Object staticInstance = staticIpConfigConstructor.newInstance(staticIpConfigInstance);

        // Set up IpAssignment to STATIC.
        Object ipAssignment = getEnumValue("android.net.IpConfiguration$IpAssignment", "STATIC");
        // Set up proxySettings to NONE.
        Object proxySettings = getEnumValue("android.net.IpConfiguration$ProxySettings", "NONE");

        //获取ipConfiguration类的构造方法
        Constructor<?>[] ipConfigConstructors = ipConfigurationClass.getDeclaredConstructors();

        for (Constructor constru : ipConfigConstructors) {

            //获取ipConfiguration类的4个参数的构造方法
            //设置以上四种类型
            if (constru.getParameterTypes().length == 4) {

                //初始化ipConfiguration对象,设置参数
                ipConfigurationInstance = constru.newInstance(ipAssignment, proxySettings, staticInstance, ProxyInfo.buildDirectProxy(null, 0));

            }

        }

        Log.e(TAG, "ipCon : " + ipConfigurationInstance);

        //获取ipConfiguration类中带有StaticIpConfiguration参数类型的名叫setStaticIpConfiguration的方法
        Method setStaticIpConfiguration = ipConfigurationClass.getDeclaredMethod("setStaticIpConfiguration", staticIpConfig);

        //修改private方法权限
        setStaticIpConfiguration.setAccessible(true);

        //在ipConfiguration对象中使用setStaticIpConfiguration方法,并传入参数
        setStaticIpConfiguration.invoke(ipConfigurationInstance, staticInstance);

        Object ethernetManagerInstance = ethernetManagerClass.getDeclaredConstructor(Context.class, iEthernetManagerClass).newInstance(Utils.getContext(), mServiceObject);

        ethernetManagerClass.getDeclaredMethod("setConfiguration", ipConfigurationClass).invoke(ethernetManagerInstance, ipConfigurationInstance);

        Log.e(TAG, "getConfiguration : " + getConfiguration.toString());

    }


    private static Object newInstance(String className)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        return newInstance(className, new Class<?>[0], new Object[0]);
    }

    private static Object newInstance(String className, Class<?>[] parameterClasses, Object[] parameterValues)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        Class<?> clz = Class.forName(className);
        Constructor<?> constructor = clz.getConstructor(parameterClasses);
        return constructor.newInstance(parameterValues);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object getEnumValue(String enumClassName, String enumValue)
            throws ClassNotFoundException {
        Class<Enum> enumClz = (Class<Enum>) Class.forName(enumClassName);
        return Enum.valueOf(enumClz, enumValue);
    }

    private static void setField(Object object, String fieldName, Object value)
            throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.set(object, value);
    }

    private static <T> T getField(Object object, String fieldName, Class<T> type)
            throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(fieldName);
        return type.cast(field.get(object));
    }

    private static void callMethod(Object object, String methodName, String[] parameterTypes, Object[] parameterValues)
            throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        Class<?>[] parameterClasses = new Class<?>[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterClasses[i] = Class.forName(parameterTypes[i]);
        }
        Method method = object.getClass().getDeclaredMethod(methodName, parameterClasses);
        method.invoke(object, parameterValues);
    }


    public static String toIpString(byte[] a) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.valueOf(a[0] & 255));
        stringBuilder.append('.');
        stringBuilder.append(a[1] & 255);
        stringBuilder.append('.');
        stringBuilder.append(a[2] & 255);
        stringBuilder.append('.');
        stringBuilder.append(a[3] & 255);
        return stringBuilder.toString();
    }

    public static String toIpString(byte b1, byte b2, byte b3, byte b4) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.valueOf(b1 & 255));
        stringBuilder.append('.');
        stringBuilder.append(b2 & 255);
        stringBuilder.append('.');
        stringBuilder.append(b3 & 255);
        stringBuilder.append('.');
        stringBuilder.append(b4 & 255);
        return stringBuilder.toString();
    }

    public static String toIpString(int i) {
        return toIpString(toIpBytes(i));
    }

    public static int toIpInteger(byte[] a) {
        return (((a[0] & 255) | ((a[1] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK)) | ((a[2] << 16) & 16711680)) | ((a[3] << 24) & ViewCompat.MEASURED_STATE_MASK);
    }

    public static int toIpInteger(byte b1, byte b2, byte b3, byte b4) {
        return (((b1 & 255) | ((b2 << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK)) | ((b3 << 16) & 16711680)) | ((b4 << 24) & ViewCompat.MEASURED_STATE_MASK);
    }

    public static int toIpInteger(String s) {
        byte[] a = toIpBytes(s);
        if (a != null) {
            return toIpInteger(a);
        }
        return 0;
    }

    public static byte[] toIpBytes(int i) {
        return new byte[]{(byte) (i & 255), (byte) ((i >>> 8) & 255), (byte) ((i >>> 16) & 255), (byte) ((i >>> 24) & 255)};
    }

    public static byte[] toIpBytes(String s) {
        try {
            return InetAddress.getByName(s).getAddress();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据掩码位数获取子网掩码
     *
     * @param maskBit
     * @return
     */
    public static String getMaskByBit(int maskBit) {
        String maskIp = "";

        if (maskBit > 32 || maskBit < 1)
            return maskIp;

        String maskBinary = "";
        for (int i = 0; i < 32; i++) {
            if (i < maskBit)
                maskBinary += "1";
            else {
                maskBinary += "0";
            }
        }

        for (int i = 0; i < 4; i++) {
            maskIp += "." + (Integer.parseInt(maskBinary.substring(8 * i, 8 * (i + 1)), 2));
        }
        return maskIp.replaceFirst(".", "");

    }

    public static int maskStr2InetMask(String maskStr) {
        StringBuffer sb;
        String str;
        int inetmask = 0;
        int count = 0;
        String[] ipSegment = maskStr.split("\\.");

        for (int n = 0; n < ipSegment.length; n++) {
            sb = toBin(Integer.parseInt(ipSegment[n]));
            str = sb.reverse().toString();
            count = 0;
            for (int i = 0; i < str.length(); i++) {
                i = str.indexOf("1", i);
                if (i == -1) {
                    break;
                }
                count++;
            }
            inetmask += count;

        }
        return inetmask;
    }

    static StringBuffer toBin(int x) {
        StringBuffer result = new StringBuffer();
        result.append(x % 2);
        x /= 2;
        while (x > 0) {
            result.append(x % 2);
            x /= 2;
        }
        return result;
    }
}
