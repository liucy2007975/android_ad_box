package com.cow.liucy.hdxm.libcommon.utils;



import com.cow.liucy.hdxm.libcommon.logger.AppLogger;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *  AES密钥规则
 *  密钥长度: 128位
 *  加密模式采用: ECB
 *  补码方式采用: PKCS5Padding
 *  偏移量:无
 *
 *  算法验证地址:http://tool.chacuo.net/cryptaes
 */
public class AESUtils {

    /**
     * AES KEY
     */
    public static String AES_KEY=null;
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * AES加密字符串
     * @param data  待加密的字符串
     * @param key  加密秘钥
     * @return  加密后的字节数组
     */
    public static byte[] encryptAES(String data, String key){
        if (key==null){
            AppLogger.e(">>>>encryptAES key==null");
            return null;
        }
//        AppLogger.e(">>>>key:"+key);
        // 注意，为了能与其他平台统一
        // 这里的 key 不可以使用 KeyGenerator、SecureRandom、SecretKey 生成
        byte[] byteContent=data.getBytes(Charset.forName("UTF-8"));
        byte[] enCodeFormat = key.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, KEY_ALGORITHM);
        // 指定加密的算法、工作模式和填充方式
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(byteContent);
            return encryptedBytes;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES解密字节数组
     * @param byteContent  待解密的字节数组
     * @param key  解密秘钥
     * @return  解密后的字符串
     */
    public static String decryptAES(byte[] byteContent, String key) {
        if (key==null){
            AppLogger.e(">>>>decryptAES key==null");
            return null;
        }
//        AppLogger.e(">>>>key:"+key);
        // 这里的 key 不可以使用 KeyGenerator、SecureRandom、SecretKey 生成
        byte[] enCodeFormat = key.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, KEY_ALGORITHM);
        try {
            // 指定加密的算法、工作模式和填充方式
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(byteContent);
            return new String(encryptedBytes,"UTF-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
       return null;
    }

    /**
     * AES加密字符串
     * @param data
     * @return
     */
    public static byte[] encryptAES(String data){
        return encryptAES(data,AES_KEY);
    }

    /**
     * AES解密字节数组
     * @param byteContent
     * @return
     */
    public static String decryptAES(byte[] byteContent){
        return decryptAES(byteContent,AES_KEY);
    }

    /**
     * 示例
     * @param args
     */
    public static void main(String[] args){
        try {
            encryptAES("沣突然好453瑞特人特瑞特热特特414","1234567891234567");
            decryptAES( encryptAES("沣突然好453瑞特人特瑞特热特特414","1234567891234567"),"1234567891234567");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
