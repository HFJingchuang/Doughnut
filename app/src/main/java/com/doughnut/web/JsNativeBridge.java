package com.doughnut.web;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.doughnut.utils.GsonUtil;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;
import com.just.agentweb.AgentWeb;
import com.zxing.activity.CaptureActivity;

/**
 * JS调用原生接口类
 */
public class JsNativeBridge {

    private final static String MSG_SUCCESS = "success";

    private AgentWeb agent;
    private Context context;
    private WalletManager mWalletManager;
    private WalletSp mWalletSp;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private String mCallBackId;
    private IWebCallBack mWebCallBack;

    public JsNativeBridge(AgentWeb agent, Context context, IWebCallBack callback) {
        this.agent = agent;
        this.context = context;
        this.mWebCallBack = callback;
        this.mWalletManager = WalletManager.getInstance(context);
    }


    @JavascriptInterface
    public void callHandler(String methodName, String params, String callbackId) {
        GsonUtil result = new GsonUtil("{}");
        this.mCallBackId = callbackId;
        switch (methodName) {
            case "getAppInfo":
                String version = "";
                String name = "";
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo = null;
                try {
                    packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                    if (packageInfo != null) {
                        version = packageInfo.versionName;
                        name = context.getResources().getString(packageInfo.applicationInfo.labelRes);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                GsonUtil infoData = new GsonUtil("{}");
                infoData.putString("name", name);
                infoData.putString("system", "android");
                infoData.putString("version", version);
                infoData.putString("sys_version", Build.VERSION.SDK_INT + "");

                result.putBoolean("result", true);
                result.put("data", infoData);
                result.putString("msg", MSG_SUCCESS);
                this.agent.getJsAccessEntrace().callJs("javascript:" + callbackId + "('" + result.toString() + "')");
                break;
            case "getDeviceId":
                // todo
                break;
            case "getWallets":
                // todo
                break;
            case "getCurrentWallet":
                String currentWallet = WalletSp.getInstance(context, "").getCurrentWallet();
                String walletName = WalletSp.getInstance(context, currentWallet).getName();
                GsonUtil data = new GsonUtil("{}");
                data.putString("address", currentWallet);
                data.putString("name", walletName);
                result.putBoolean("result", true);
                result.put("data", data);
                result.putString("msg", MSG_SUCCESS);
                this.agent.getJsAccessEntrace().callJs("javascript:" + callbackId + "('" + result.toString() + "')");
                break;
            case "sign":
                // todo
                break;
            case "invokeQRScanner":
                CaptureActivity.startCaptureActivity(context, true);
                break;
            case "back":
                if (mWebCallBack != null) {
                    mWebCallBack.onBack();
                }
                break;
            case "fullScreen":
                if (mWebCallBack != null) {
                    mWebCallBack.switchFullScreen(params);
                }
                break;
            case "close":
                if (mWebCallBack != null) {
                    mWebCallBack.onClose();
                }
                break;
            default:
        }
    }

    public String getCallBackId() {
        return mCallBackId;
    }
}
