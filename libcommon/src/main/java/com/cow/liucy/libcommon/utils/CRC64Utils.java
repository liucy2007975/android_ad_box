package com.cow.liucy.libcommon.utils;

import java.util.Arrays;

/**
 * <p>
 * CRC64校验 2字节
 * 采用CRC-16/CCITT算法 CRC-16/CCITT-FALSE
 * 验证地址：http://www.ip33.com/crc.html
 * </p>
 */
public class CRC64Utils {
    /**
     * 给需要校验的byte数组添加两位CRC校验码
     *
     * @param data 需要校验的byte数组
     * @return
     */
    public static byte[] setParamCRC(byte[] data) {
        int checkCode = 0;
        checkCode = crc_16_CCITT(data);//获取校验码
        //添加校验码
        byte[] crcByte = new byte[2];
        crcByte[0] = (byte) ((checkCode >> 8) & 0xff);
        crcByte[1] = (byte) (checkCode & 0xff);
        //将生成的校验码添加到原数据结尾
        return concatAll(data, crcByte);
    }

    /**
     * 合并byte数组
     *
     * @param first
     * @param rest
     * @return
     */
    private static byte[] concatAll(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        //计算偏移量
        int offset = first.length;
        for (byte[] array :rest)
        {
            System.arraycopy(array,0,result,offset,array.length);
            offset+=array.length;
        }
        return result;
    }

    /**
     * CRC-16/CCITT x16+x12+x5+1 算法
     * <p>
     * info
     * Name:CRC-16/CCITT-FAI
     * Width:16
     * Poly:0x1021
     * Init:0xFFFF
     * RefIn:False
     * RefOut:False
     * XorOut:0x0000
     *
     * @param bytes
     * @return
     */
    private static int crc_16_CCITT(byte[] bytes) {
        int crc = 0xffff;
        int polynomial = 0x1021;
        for (int index = 0; index < bytes.length; index++) {
            byte b = bytes[index];
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit)
                    crc ^= polynomial;
            }
        }
        crc &= 0xffff;
        String strCrc = Integer.toHexString(crc).toUpperCase();
        System.out.println(strCrc);
        return crc;
    }

    /**
     * CRC校验
     * @param srcByte 添加了校验码的数据
     * @param len 校验码字节长度
     * @return false:校验失败 true:校验成功
     */
    public static boolean isPassCRC(byte[] srcByte,int len){
        //取出除校验位的其他值
        int calcCRC=calcCRC(srcByte,0,srcByte.length-len);
        byte[] bytes=new byte[2];
        bytes[0] = (byte) ((calcCRC >> 8) & 0xff);
        bytes[1] = (byte) (calcCRC & 0xff);
        //取出校验位
        int i=srcByte.length;
        byte[] b={srcByte[i-2],srcByte[i-1]};
        //比较并返回结果
        return bytes[0]==b[0]&&bytes[1] == b[1];

    }
    /**
     * 对data中offset前crcLen长度的字节作crc校验，返回校验结果
     * @param  buf
     * @param crcLen
     */
    private static int calcCRC(byte[] buf, int offset, int crcLen) {
        int start = offset;
        int end = offset + crcLen;
        int crc = 0xffff; // initial value
        int polynomial = 0x1021;
        for (int index = start; index < end; index++) {
            byte b = buf[index];
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit)
                    crc ^= polynomial;
            }
        }
        crc &= 0xffff;
        return crc;
    }

    public static void main(String[] args){
        byte [] bytes = new byte []{0x31,0x32,0x33,0x34};
        int re = crc_16_CCITT(bytes);
        System.out.println(Integer.toHexString(re).toUpperCase());
    }
}
