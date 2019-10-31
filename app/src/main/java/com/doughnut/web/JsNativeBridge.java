package com.doughnut.web;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import com.android.jtblk.client.bean.Marker;
import com.doughnut.utils.GsonUtil;
import com.doughnut.wallet.ICallBack;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;
import com.jccdex.rpc.base.JCallback;
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
    private static String mCallBackId;

    public JsNativeBridge(AgentWeb agent, Context context) {
        this.agent = agent;
        this.context = context;
        this.mWalletManager = WalletManager.getInstance(context);
    }


    @JavascriptInterface
    public void callHandler(String methodName, String params, String callbackId) {
        GsonUtil result = new GsonUtil("{}");
        this.mCallBackId = callbackId;
        switch (methodName) {
            case "getAppInfo":
                // todo
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
                // todo
                break;
            case "fullScreen":
                // todo
                break;
            case "close":
                // todo
                break;
            default:
        }

    }

    public static String getCallBackId() {
        return mCallBackId;
    }
}
