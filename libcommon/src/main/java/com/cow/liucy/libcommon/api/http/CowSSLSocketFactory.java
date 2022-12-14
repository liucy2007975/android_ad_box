package com.cow.liucy.libcommon.api.http;

import android.content.Context;

import java.io.InputStream;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by cow on 2017/7/31.
 */

public class CowSSLSocketFactory {

    public static SSLSocketFactory getSSLSocketFactory(Context context) {
        try {
            InputStream trust_input = context.getAssets().open("cow.bks");//服务器授信证书
            InputStream client_input = context.getAssets().open("client.p12");//客户端证书
            HttpsUtils.SSLParams sslParams= HttpsUtils.getSslSocketFactory(new InputStream[]{trust_input},client_input,"cow.com");
           return sslParams.sSLSocketFactory;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {

        }
    }
}
