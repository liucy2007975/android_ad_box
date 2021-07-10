package com.cow.liucy.hdxm.libcommon.utils;

import java.io.ByteArrayOutputStream;
import java.util.BitSet;

/**
 * 常用类型转换
 */
public final class ConvertUtil {

    private final static String hexStr = "0123456789ABCDEF";

    //java 合并两个byte数组
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    //java 分隔byte数组，从第几个字节开始截取,向后截取
    public static byte[] byteSplit(byte[] byte_1, int indexStart) {
        byte[] byte_3 = new byte[byte_1.length - indexStart];
        System.arraycopy(byte_1, indexStart, byte_3, 0, byte_3.length);
        return byte_3;
    }

    //java 分隔byte数组，从第几个字节开始截取,向前截取多少长度的字符串
    public static byte[] byteLengthSplit(byte[] byte_1, int indexStart, int length) {
        byte[] byte_3 = new byte[length];
        System.arraycopy(byte_1, indexStart, byte_3, 0, byte_3.length);
        return byte_3;
    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序.
     */
    public static byte[] intTo2Bytes(int value) {
        byte[] targets = new byte[2];
        targets[0] = (byte) (value >> 8 & 0xFF);
        targets[1] = (byte) (value & 0xFF);
        return targets;
    }

    /**
     * 短整型与字节的转换
     */
    public final static byte[] shortToByte(short number) {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            // 将最低位保存在最低位
            b[i] = new Integer(temp & 0xff).byteValue();
            // 向右移8位
            temp = temp >> 8;
        }
        return b;
    }

    /**
     * 字节的转换与短整型
     */
    public final static short byteToShort(byte[] b) {
        short s;
        // 最低位
        short s0 = (short) (b[0] & 0xff);
        short s1 = (short) (b[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    /**
     * 整型与字节数组的转换
     */
    public final static byte[] intToByte(int i) {
        byte[] bt = new byte[4];
        bt[3] = (byte) (0xff & i);
        bt[2] = (byte) ((0xff00 & i) >> 8);
        bt[1] = (byte) ((0xff0000 & i) >> 16);
        bt[0] = (byte) ((0xff000000 & i) >> 24);
        return bt;
    }

    /**
     * 大端 字节数组和整型的转换
     */
    public final static int bytesToIntBig(byte[] bytes) {
        int num = bytes[3] & 0xFF;
        num |= ((bytes[2] << 8) & 0xFF00);
        num |= ((bytes[1] << 16) & 0xFF0000);
        num |= ((bytes[0] << 24) & 0xFF000000);
        return num;
    }


    /**
     * 大端整型与字节数组的转换
     */
    public final static byte[] intToByteBig(int i) {
        byte[] bt = new byte[4];
        bt[3] = (byte) (0xff & i);
        bt[2] = (byte) ((0xff00 & i) >> 8);
        bt[1] = (byte) ((0xff0000 & i) >> 16);
        bt[0] = (byte) ((0xff000000 & i) >> 24);
        return bt;
    }

    /**
     * 以小端模式将int转成byte[]
     *
     * @param value
     * @return
     */
    public static byte[] intToBytesLittle(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * 小端 字节数组和整型的转换
     */
    public final static int bytesToIntLittle(byte[] bytes) {
        int b = (int) bytes[0];
        int num = bytes[0] & 0xFF;
        num |= ((bytes[1] << 8) & 0xFF00);
        num |= ((bytes[2] << 16) & 0xFF0000);
        num |= ((bytes[3] << 24) & 0xFF000000);
        return num;
    }

    /**
     * 整型数组转换为字节数组的转换
     *
     * @param arr 整型数组
     */
    public final static byte[] intToByte(int[] arr) {
        byte[] bt = new byte[arr.length * 4];
        for (int i = 0; i < arr.length; i++) {
            byte[] t = intToByte(arr[i]);
            System.arraycopy(t, 0, bt, i + 4, 4);
        }
        return bt;
    }

    public final static byte[] encodeBytes(byte[] source, char split) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(source.length);
        for (byte b : source) {
            if (b < 0) {
                b += 256;
            }
            bos.write(split);
            char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
            char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
            bos.write(hex1);
            bos.write(hex2);
        }
        return bos.toByteArray();
    }

    /**
     * bytes数组转char数组
     * bytes to chars
     *
     * @param bytes bytes数组
     */
    public final static char[] bytesToChars(byte[] bytes) {
        char[] chars = new char[]{};
        if (Valid.valid(bytes)) {
            chars = new char[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                chars[i] = (char) bytes[i];
            }
        }
        return chars;
    }

    /**
     * 字节数组和整型的转换
     */
    public final static int bytesToInt(byte[] bytes) {
        int num = bytes[3] & 0xFF;
        num |= ((bytes[2] << 8) & 0xFF00);
        num |= ((bytes[1] << 16) & 0xFF0000);
        num |= ((bytes[0] << 24) & 0xFF000000);
        return num;
    }

    public static int convBytesToInt(byte[] buff, int offset) {
        //4bytes 转为int，要考虑机器的大小端问题
        int len, byteValue;
        len = 0;
        byteValue = (0x000000FF & ((int) buff[offset]));
        len += byteValue << 24;
        byteValue = (0x000000FF & ((int) buff[offset + 1]));
        len += byteValue << 16;
        byteValue = (0x000000FF & ((int) buff[offset + 2]));
        len += byteValue << 8;
        byteValue = (0x000000FF & ((int) buff[offset + 3]));
        len += byteValue;
        return len;
    }

    /**
     * 字节数组和长整型的转换
     */
    public final static byte[] longToByte(long number) {
        long temp = number;
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Long(temp & 0xff).byteValue();
            // 将最低位保存在最低位
            temp = temp >> 8;
            // 向右移8位
        }
        return b;
    }

    /**
     * 字节数组和长整型的转换
     */
    public final static long byteToLong(byte[] b) {
        long s;
        long s0 = b[0] & 0xff;// 最低位
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        long s4 = b[4] & 0xff;// 最低位
        long s5 = b[5] & 0xff;
        long s6 = b[6] & 0xff;
        long s7 = b[7] & 0xff; // s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }

    /**
     * 将byte转换为对应的二进制字符串
     *
     * @param src 要转换成二进制字符串的byte值
     */
    public final static String byteToBinary(byte src) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            result.append(src % 2 == 0 ? '0' : '1');
            src = (byte) (src >>> 1);
        }
        return result.reverse().toString();
    }

    /**
     * 将十六进制字符串转为二进制字符串
     *
     * @param hexStr 十六进制字符串
     */
    public final static String hexStringtoBinarg(String hexStr) {
        hexStr = hexStr.replaceAll("\\s", "").replaceAll("0x", "");
        char[] achar = hexStr.toCharArray();
        String result = "";
        for (char a : achar) {
            result += Integer.toBinaryString(
                    Integer.valueOf(String.valueOf(a), 16)) + " ";
        }
        return result;
    }

    /**
     * 将二进制转换为十六进制字符输出
     *
     * @param bytes bytes数组
     */
    public final static String bytesToHexString(byte[] bytes) {
        String result = "";
        String hex;
        for (byte b : bytes) {
            //字节高4位
            hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
            //字节低4位
            hex += String.valueOf(hexStr.charAt(b & 0x0F));
            result += hex + " ";
        }
        return result;
    }

    /**
     * 把16进制字符串转换成字节数组
     *
     * @param hexString 16进制字符串
     * @return byte[]
     */
    public final static byte[] hexStringToByte(String hexString) {
        String upperCase= hexString.toUpperCase();
        int len = (upperCase.length() / 2);
        byte[] result = new byte[len];
        char[] achar = upperCase.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private final static int toByte(char c) {
        return (byte) hexStr.indexOf(c);
    }

    public static void main(String[] args) {
//        String str = "HDXM00000000000000000000111111111111111111112000000001122";
//        System.out.println(ConvertUtil.bytesToHexString(str.getBytes()));
//        System.out.println(str.getBytes().length);
//
//        System.out.println(ConvertUtil.bytesToHexString(intToByte(568)));
//        System.out.println(ConvertUtil.bytesToHexString(intToByte(1024)));
        String cardNo="B396965B";
//        cardNo = "" + ConvertUtil.byteToLong(BCD.strToBcd(cardNo));
        cardNo = "" + Long.parseLong(cardNo,16);
//        System.out.println(Long.parseUnsignedLong(cardNo, 16));
        System.out.println(">>>cardNo="+cardNo);
    }

    /**
     * 将ByteArray对象转化为BitSet
     *
     * @param bytes
     * @return
     */
    public static BitSet byteArray2BitSet(byte[] bytes) {
        BitSet bitSet = new BitSet(bytes.length * 8);
        int index = 0;
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 7; j >= 0; j--) {
                bitSet.set(index++, (bytes[i] & (1 << j)) >> j == 1 ? true
                        : false);
            }
        }
        return bitSet;
    }

    /**
     * 将BitSet对象转化为ByteArray
     *
     * @param bitSet
     * @return
     */
    public static byte[] bitSet2ByteArray(BitSet bitSet) {
        byte[] bytes = new byte[bitSet.size() / 8];
        for (int i = 0; i < bitSet.size(); i++) {
            int index = i / 8;
            int offset = 7 - i % 8;
            bytes[index] |= (bitSet.get(i) ? 1 : 0) << offset;
        }
        return bytes;
    }

}
