package com.doughnut.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;


public class FileUtil {
    private static MessageDigest md5 = null;

    public static void putStringToSp(Context context, String spName, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public static String getStringFromSp(Context context, String spName, String key) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void putIntToSp(Context context, String spName, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }

    public static int getIntFromSp(Context context, String spName, String key) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    public static void putLongToSp(Context context, String spName, String key, long value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        sp.edit().putLong(key, value).commit();
    }

    public static long getLongFromSp(Context context, String spName, String key) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getLong(key, 0l);
    }

    public static void putBooleanToSp(Context context, String spName, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getBooleanFromSp(Context context, String spName, String key) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    public static boolean getBooleanFromSp(Context context, String spName, String key, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }


    public static String getStringContent(String originTxt) {
        return originTxt;
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        if (file != null && file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File f : files) {
                        deleteFile(f.getAbsolutePath());
                    }
                }
                file.delete();
            }
        }
    }

    public static String getSharedPrefDir(Context context) {
        return context.getFilesDir().getParent() + "/shared_prefs/";
    }

    public static String getConfigFile(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 验证MD5
     *
     * @param file
     * @param md5
     * @return
     */
    public static boolean checkFileMD5(File file, String md5) {
        if (!file.isFile()) {
            return false;
        }
        boolean md5Flg = false;
        MessageDigest digest;
        FileInputStream in;
        String localMD5;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
            BigInteger bigInt = new BigInteger(1, digest.digest());
            localMD5 = bigInt.toString(16);
            while (localMD5.length() < 32) {
                localMD5 = "0" + localMD5;
            }
            md5Flg = localMD5.equals(md5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5Flg;
    }
}
