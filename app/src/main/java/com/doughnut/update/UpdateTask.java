package com.doughnut.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.dialog.MsgDialog;
import com.doughnut.dialog.UpgradeDialog;
import com.doughnut.utils.DeviceUtil;
import com.doughnut.utils.GsonUtil;
import com.doughnut.utils.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * 检查更新AsyncTask
 */
public class UpdateTask extends AsyncTask<Context, Integer, Integer> {
    private final int RES_NOR = 0;//获取更新信息成功
    private final int RES_ERR = 1;//获取更新信息失败
    private int RE_CONN = 0;
    private static Context mContext;
    private GsonUtil versionInfo = null;

    @Override
    protected void onPreExecute() {
        RE_CONN = 0;
    }

    @Override
    protected Integer doInBackground(Context... params) {
        mContext = params[0];
        InputStream inputStream = connect(mContext);
        try {
            if (inputStream != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int len;
                byte[] buffer = new byte[1024];
                while ((len = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                inputStream.close();
                out.close();
                String result = out.toString();
                versionInfo = new GsonUtil(result);
                return RES_NOR;
            } else {
                return RES_ERR;
            }
        } catch (Exception e) {
            return RES_ERR;
        }
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    @Override
    protected void onPostExecute(Integer result) {
        switch (result) {
            case RES_NOR:
                int version_Code = versionInfo.getInt("version_Code", 0);
                String url = versionInfo.getString("url", "");
                if (version_Code > DeviceUtil.getVersionCode(mContext)) {
                    new UpgradeDialog(mContext, url).show();

                    // 保存md5
                    String md5 = versionInfo.getString("md5", "");
                    String fileName = mContext.getPackageName() + "_update";
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("md5", md5);
                    editor.apply();
                } else {
                    new MsgDialog(mContext, mContext.getResources().getString(R.string.toast_latest_version)).show();
                }
                break;
            case RES_ERR:
                new MsgDialog(mContext, mContext.getResources().getString(R.string.dialog_update_fail)).show();
                break;
        }
    }

    /**
     * 网络请求失败重试3次
     *
     * @param context
     * @return
     */
    private InputStream connect(Context context) {
        if (RE_CONN == 3) {
            return null;
        }
        HttpsURLConnection conn = null;
        String URL = Constant.UPATE_URL + "?" + System.currentTimeMillis();
        InputStream inputStream;
        try {
            RE_CONN++;
            java.net.URL url = new URL(URL);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setDoOutput(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            inputStream = conn.getInputStream();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                conn.disconnect();
                inputStream = connect(context);
            }
        } catch (Exception e) {
            inputStream = connect(context);
        }
        return inputStream;
    }
}

