package com.doughnut.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.doughnut.BuildConfig;
import com.doughnut.utils.FileUtil;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * 下载结果处理
 */
public class DownloadCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            String fileName = context.getPackageName() + "_update";
            SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
            long id = sharedPreferences.getLong("downloadId", -1);
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            if (id == downloadId && DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                try {
                    String apkName = sharedPreferences.getString("apkName", "");
                    File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath(), apkName);
                    // 验证APK MD5
                    String md5 = sharedPreferences.getString("md5", "");
                    if (!(FileUtil.checkFileMD5(file, md5))) {
                        downloadManager.remove(downloadId);
                        return;
                    }

                    // 安装APK
                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                        installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } else {
                        uri = Uri.fromFile(file);
                    }
                    installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                    context.startActivity(installIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
