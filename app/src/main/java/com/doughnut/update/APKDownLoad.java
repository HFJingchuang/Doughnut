package com.doughnut.update;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.URLUtil;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

public class APKDownLoad {

    private static String MIMETYPE = "application/vnd.android.package-archive";

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
        String fileName = URLUtil.guessFileName(url, "", MIMETYPE);
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName);
        String filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + fileName;
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            file.delete();
        }

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        // 添加一个下载任务
        downloadManager.enqueue(request);
    }
}
