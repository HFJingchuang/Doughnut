package com.doughnut.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PWDUtils {

    /**
     * 验证密码格式，大小字母，数字组合
     *
     * @param password
     * @return
     */
    public static boolean verifyPasswordFormat(String password) {
        String regex = "^(?![0-9A-Z]+$)(?![0-9a-z]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,64}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        return m.matches();
    }
}
