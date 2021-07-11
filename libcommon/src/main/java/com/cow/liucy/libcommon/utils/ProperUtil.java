package com.cow.liucy.libcommon.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Created by anjubao on 2018-01-08.
 */

public class ProperUtil {
    private static Properties props = new Properties();


    public static void put(String name, String value) {
        props.put(name,value);
    }

    public static void remove(String name){
        props.remove(name);
    }

    public static void loadProperties(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        InputStream is = null;
        try {
            is = new FileInputStream(file);
            props.load(new InputStreamReader(is, Charset.forName("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getProperty(String name) {
        return props.getProperty(name);
    }

}
