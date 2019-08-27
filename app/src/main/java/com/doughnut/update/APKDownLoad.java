package com.doughnut.update;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.webkit.URLUtil;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

public class APKDownLoad {

    final private static String MIMETYPE = "application/vnd.android.package-archive";

    public static void downLoad(Context context, String url) {
        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 允许在计费流量下下载
        request.setAllowedOverMetered(true);
        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(true);
        // 允许漫游时下载
        request.setAllowedOverRoaming(true);
        // 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType(MIMETYPE);
//        request.addRequestHeader("User-Agent", userAgent);

        // 设置下载文件保存的路径和文件名
        String apkName = URLUtil.guessFileName(url, "", MIMETYPE);
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, apkName);
        String filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + apkName;
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            file.delete();
        }

        // 添加一个下载任务
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);

        // 保存文件名
        String fileName = context.getPackageName() + "_update";
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("apkName", apkName);
        editor.apply();
    }
}
