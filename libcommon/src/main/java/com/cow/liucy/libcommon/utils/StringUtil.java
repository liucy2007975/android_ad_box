package com.cow.liucy.libcommon.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    //分割带数字的字符串
    public static List<String> splitNumText(String s) {
        char[] lists = s.toCharArray();

        boolean isText = false;
        List<String> listResult = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lists.length; i++) {
            if (i == 0) {
                isText = !Character.isDigit(lists[i]);
                sb.append(lists[i]);
            } else {
                //字符的类别没有改变
                if (!Character.isDigit(lists[i]) == isText || lists[i] == '.') {
                    sb.append(lists[i]);
                }
                //字符的类别改变了
                else {
                    isText = !Character.isDigit(lists[i]);
                    listResult.add(sb.toString());
                    sb = new StringBuilder();
                    sb.append(lists[i]);
                }
                if (i == lists.length - 1) {
                    listResult.add(sb.toString());
                }
            }

        }
        return listResult;
    }

    /**
     * 数字转换成汉语读法
     */
    public static String transfer(Long number) {
        if (number == 0) {
            return String.valueOf(number);
        }
        String result = transfer1(number);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result.length(); i++) {
            char c = result.charAt(i);
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 转换亿万千百十
     */
    private static String transfer1(Long number) {
        StringBuilder sb = new StringBuilder();

        for (NumFont nf : NumFont.values()) {
            if (number / nf.getNum() > 0) {
                // 做除法之后整数部分递归继续读
                sb.append(transfer(number / nf.getNum()));

                sb.append(nf.name());
                // 余数继续读
                number = number % nf.getNum();
                // 如果下一位不够，则补0，整除则不补0，比如800，不能读八百零
                if (number < nf.getNum() / 10 && number != 0) {
                    sb.append("0");
                }
            }
        }
        if (number > 0) {
            sb.append(number);
        }
        return sb.toString();
    }


    private enum NumFont {
        亿(100000000L), 万(10000L), 千(1000L), 百(100L), 十(10L);

        private final Long num;

        NumFont(Long num) {
            this.num = num;
        }

        private Long getNum() {
            return num;
        }
    }

    // 判断一个字符串是否含有数字
    public static boolean hasDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        return flag;
    }

    // 判断一个字符串是否都为数字
    public static boolean isDigit(String strNum) {
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher((CharSequence) strNum);
        return matcher.matches();
    }
}
