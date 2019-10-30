package com.doughnut.web;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.android.jtblk.client.bean.Marker;
import com.doughnut.wallet.ICallBack;
import com.doughnut.wallet.WalletManager;
import com.jccdex.rpc.base.JCallback;
import com.just.agentweb.AgentWeb;

/**
 * JS调用原生接口类
 */
public class JsNativeBridge {
    private AgentWeb agent;
    private Context context;
    private WalletManager mWalletManager;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public JsNativeBridge(AgentWeb agent, Context context) {
        this.agent = agent;
        this.context = context;
        this.mWalletManager = WalletManager.getInstance(context);
    }

    @JavascriptInterface
    public void createWallet(String password, String name, ICallBack callBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWalletManager.createWallet(password, name, callBack);
            }
        });
    }

    @JavascriptInterface
    public void deleteWallet(String address) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWalletManager.deleteWallet(address);
            }
        });
    }

    //TODO 后续需要定义如何传递Bitmap给js
    @JavascriptInterface
    public Bitmap exportWalletWithQR(String address, int widthAndHeight, int color) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWalletManager.exportWalletWithQR(address, widthAndHeight, color);
            }
        });
        return null;
    }

    @JavascriptInterface
    public void importWalletWithKey(String password, String privateKey, String name, ICallBack callBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWalletManager.importWalletWithKey(password, privateKey, name, callBack);
            }
        });
    }

    @JavascriptInterface
    public void importKeysStore(String keyStore, String password, String name, ICallBack callBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWalletManager.importKeysStore(keyStore, password, name, callBack);
            }
        });
    }

    @JavascriptInterface
    public void getPrivateKey(String password, String keyStore, ICallBack callBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWalletManager.getPrivateKey(password, keyStore, callBack);
            }
        });
    }

    @JavascriptInterface
    public void transfer(String privateKey, String from, String to, String token, String issuer, String value, String fee, String memo, ICallBack callBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWalletManager.transfer(privateKey, from, to, token, issuer, value, fee, memo, callBack);
            }
        });
    }

    @JavascriptInterface
    public void getTransferHistory(String address, Integer limit, Marker marker, ICallBack callBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWalletManager.getTransferHistory(address, limit, marker, callBack);
            }
        });
    }

    @JavascriptInterface
    public void getBalance(String address, boolean isDisposable, ICallBack callBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWalletManager.getBalance(address, isDisposable, callBack);
            }
        });
    }

    @JavascriptInterface
    public void getSWTBalance(String address, ICallBack callBack) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWalletManager.getSWTBalance(address, callBack);
            }
        });
    }

    @JavascriptInterface
    public void getTokenPrice(String base, JCallback jCallback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWalletManager.getTokenPrice(base, jCallback);
            }
        });
    }

    @JavascriptInterface
    public void getAllTokenPrice(JCallback jCallback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWalletManager.getAllTokenPrice(jCallback);
            }
        });
    }

    @JavascriptInterface
    public void getAllTokens() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWalletManager.getAllTokens();
            }
        });
    }
}
