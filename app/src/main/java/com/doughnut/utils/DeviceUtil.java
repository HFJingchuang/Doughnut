package com.doughnut.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.doughnut.config.AppConfig;
import com.doughnut.config.Constant;

import java.io.File;
import java.util.Locale;
import java.util.UUID;


public final class DeviceUtil {

    //Memeroy
    public static long getAvailableExternalMemorySize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }


    public static String generateDeviceUniqueId() {
        try {
            String deviceUniqueId = FileUtil.getStringFromSp(AppConfig.getContext(), Constant.sys_prefs, Constant.init_keys);
            if (TextUtils.isEmpty(deviceUniqueId)) {
                String id = UUID.randomUUID().toString();
                String deviceId = FileUtil.getStringContent(id);
                FileUtil.putStringToSp(AppConfig.getContext(), Constant.sys_prefs, Constant.init_keys, deviceId);
                return deviceId;
            } else {
                return deviceUniqueId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDeviceUniqueId() {
        String deviceUniqueId = FileUtil.getStringFromSp(AppConfig.getContext(), Constant.sys_prefs, Constant.init_keys);
        return deviceUniqueId;
    }


    public static String getVersionName(Context context) {
        String version = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;
        } catch (Exception e) {
            TLog.d("AndroidConfig", "getVersionName error " + e.getMessage());
        }
        return version;
    }

    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo packInfo = manager.getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return (int) packInfo.getLongVersionCode();
            } else {
                return packInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getVersionName() {
        return getVersionName(AppConfig.getContext());
    }


    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

}
