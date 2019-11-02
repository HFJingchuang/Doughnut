package com.doughnut.web;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.android.jtblk.client.Transaction;
import com.android.jtblk.client.bean.AmountInfo;
import com.doughnut.R;
import com.doughnut.config.AppConfig;
import com.doughnut.dialog.LoadDialog;
import com.doughnut.dialog.TransferDetailDialog;
import com.doughnut.dialog.TransferDialog;
import com.doughnut.utils.AESUtil;
import com.doughnut.utils.DeviceUtil;
import com.doughnut.utils.GsonUtil;
import com.doughnut.wallet.ICallBack;
import com.doughnut.wallet.JtServer;
import com.doughnut.wallet.WConstant;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;
import com.just.agentweb.AgentWeb;
import com.zxing.activity.CaptureActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * JS调用原生接口类
 */
public class JsNativeBridge {

    private final static String MSG_SUCCESS = "success";
    private final static long FIFTEEN = 15 * 60 * 1000L;

    private AgentWeb mAgentWeb;
    private Context mContext;
    private WalletManager mWalletManager;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private String mFrom, mTo, mValue, mToken, mIssuer, mGas, mMemo;
    private IWebCallBack mWebCallBack;
    private String mCurrentWallet;

    public JsNativeBridge(AgentWeb agent, Context context, IWebCallBack callback) {
        this.mAgentWeb = agent;
        this.mContext = context;
        this.mWebCallBack = callback;
        this.mWalletManager = WalletManager.getInstance(context);
    }

    @JavascriptInterface
    public void callHandler(String methodName, String params, String callbackId) {
        mCurrentWallet = WalletSp.getInstance(mContext, "").getCurrentWallet();
        GsonUtil result = new GsonUtil("{}");
        switch (methodName) {
            case "getAppInfo":
                String version = "";
                String name = "";
                PackageManager packageManager = mContext.getPackageManager();
                PackageInfo packageInfo = null;
                try {
                    packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
                    if (packageInfo != null) {
                        version = packageInfo.versionName;
                        name = mContext.getResources().getString(packageInfo.applicationInfo.labelRes);
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
                this.mAgentWeb.getJsAccessEntrace().callJs("javascript:" + callbackId + "('" + result.toString() + "')");
                break;
            case "getDeviceId":
                String deviceId = DeviceUtil.generateDeviceUniqueId();
                this.mAgentWeb.getJsAccessEntrace().quickCallJs(callbackId, deviceId);
                break;
            case "getWallets":
                List<String> wallets = WalletSp.getInstance(mContext, "").getAllWallet();
                GsonUtil data1 = new GsonUtil("[]");
                for (int i = 0; i < wallets.size(); i++) {
                    GsonUtil wallet = new GsonUtil("{}");
                    String address = wallets.get(i);
                    String name1 = WalletSp.getInstance(mContext, address).getName();
                    wallet.putString("name", name1);
                    wallet.putString("address", address);
                    data1.put(wallet);
                }
                result.putBoolean("result", true);
                result.put("data", data1);
                result.putString("msg", MSG_SUCCESS);
                this.mAgentWeb.getJsAccessEntrace().callJs("javascript:" + callbackId + "('" + result.toString() + "')");
                break;
            case "getCurrentWallet":
                String walletName = WalletSp.getInstance(mContext, mCurrentWallet).getName();
                GsonUtil data = new GsonUtil("{}");
                data.putString("address", mCurrentWallet);
                data.putString("name", walletName);
                result.putBoolean("result", true);
                result.put("data", data);
                result.putString("msg", MSG_SUCCESS);
                this.mAgentWeb.getJsAccessEntrace().callJs("javascript:" + callbackId + "('" + result.toString() + "')");
                break;
            case "sign":
                try {
                    AmountInfo amount;
                    amount = new AmountInfo();
                    GsonUtil tx = new GsonUtil(params);
                    mTo = tx.getString("to", "");
                    mToken = tx.getString("currency", "").toUpperCase();
                    mIssuer = tx.getString("issuer", "");
                    mValue = tx.getString("value", "");
                    mMemo = tx.getString("memo", "");
                    mGas = tx.getString("gas", "");

                    amount.setCurrency(mToken.toUpperCase());
                    amount.setIssuer(mIssuer);
                    amount.setValue(mValue);
                    Transaction transaction = JtServer.getInstance(mContext).getRemote().buildPaymentTx(mCurrentWallet, mTo, amount);
                    List<String> memos = new ArrayList<String>();
                    memos.add(mMemo);
                    transaction.addMemo(memos);
                    String fee = (new BigDecimal(mGas).multiply(new BigDecimal(1000000))).stripTrailingZeros().toPlainString();
                    transaction.setFee(fee);
                    AppConfig.postOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new TransferDetailDialog(mContext, new TransferDetailDialog.OnListener() {
                                @Override
                                public void onBack() {
                                    if (!WithNoPwd(transaction, callbackId)) {
                                        new TransferDialog(mContext, mCurrentWallet)
                                                .setResultListener(new TransferDialog.PwdResultListener() {
                                                    @Override
                                                    public void authPwd(boolean res, String key) {
                                                        if (res) {
                                                            try {
                                                                String signedTx = transaction.sign(key);
                                                                result.putBoolean("result", true);
                                                                result.putString("signedTx", signedTx);
                                                                result.putString("msg", MSG_SUCCESS);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                                result.putBoolean("result", false);
                                                                result.putString("msg", e.getMessage());
                                                            }
                                                            mAgentWeb.getJsAccessEntrace().callJs("javascript:" + callbackId + "('" + result.toString() + "')");
                                                        }
                                                    }
                                                }).show();
                                    }
                                }
                            }).setFrom(mCurrentWallet).setValue(formatHtml()).setTo(mTo).setGas(mGas).setMemo(mMemo).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    result.putBoolean("result", false);
                    result.putString("msg", e.getMessage());
                    mAgentWeb.getJsAccessEntrace().callJs("javascript:" + callbackId + "('" + result.toString() + "')");
                }
                break;
            case "transfer":
                try {
                    GsonUtil tx = new GsonUtil(params);
                    mTo = tx.getString("to", "");
                    mToken = tx.getString("currency", "").toUpperCase();
                    mIssuer = tx.getString("issuer", "");
                    mValue = tx.getString("value", "");
                    mMemo = tx.getString("memo", "");
                    mGas = tx.getString("gas", "");

                    BigDecimal fee = new BigDecimal(mGas).multiply(new BigDecimal(1000000));
                    AppConfig.postOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new TransferDetailDialog(mContext, new TransferDetailDialog.OnListener() {
                                @Override
                                public void onBack() {
                                    if (!WithNoPwd(null, callbackId)) {
                                        new TransferDialog(mContext, mCurrentWallet)
                                                .setResultListener(new TransferDialog.PwdResultListener() {
                                                    @Override
                                                    public void authPwd(boolean res, String key) {
                                                        if (res) {
                                                            mWalletManager.transferForHash(key, mCurrentWallet, mTo, mToken, mIssuer, mValue, fee.stripTrailingZeros().toPlainString(), mMemo, new ICallBack() {
                                                                @Override
                                                                public void onResponse(Object object) {
                                                                    GsonUtil res = (GsonUtil) object;
                                                                    mAgentWeb.getJsAccessEntrace().callJs("javascript:" + callbackId + "('" + res.toString() + "')");
                                                                }
                                                            });
                                                        }
                                                    }
                                                }).show();
                                    }
                                }
                            }).setFrom(mCurrentWallet).setValue(formatHtml()).setTo(mTo).setGas(mGas).setMemo(mMemo).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    result.putBoolean("result", false);
                    result.putString("msg", e.getMessage());
                    mAgentWeb.getJsAccessEntrace().callJs("javascript:" + callbackId + "('" + result.toString() + "')");
                }
                break;
            case "invokeQRScanner":
                CaptureActivity.startCaptureActivity(mContext, callbackId);
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
                break;
        }

    }

    /**
     * 免密支付
     *
     * @param transaction
     * @return
     */
    private boolean WithNoPwd(Transaction transaction, String callBackId) {
        String fileName = mContext.getPackageName() + "_pwd_" + mCurrentWallet;
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        long time = sharedPreferences.getLong("time", 0);
        long now = System.currentTimeMillis();
        long diff = now - time;
        if (diff < FIFTEEN) {
            LoadDialog loadDialog;
            if (transaction == null) {
                loadDialog = new LoadDialog(mContext, mContext.getString(R.string.dialog_tranfer));
            } else {
                loadDialog = new LoadDialog(mContext, mContext.getString(R.string.dialog_sign));
            }
            loadDialog.show();
            String key = sharedPreferences.getString("key", "");
            String encrypt = sharedPreferences.getString("encrypt", "");
            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(encrypt)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                return false;
            }

            AESUtil.decrypt(key, encrypt, new ICallBack() {
                @Override
                public void onResponse(Object response) {
                    String pwd = (String) response;
                    String keyStore = WalletSp.getInstance(mContext, mCurrentWallet).getKeyStore();
                    WalletManager.getInstance(mContext).getPrivateKey(pwd, keyStore, new ICallBack() {
                        @Override
                        public void onResponse(Object response) {
                            String privateKey = (String) response;
                            BigDecimal fee = new BigDecimal(mGas).multiply(new BigDecimal(1000000));
                            if (transaction != null) {
                                GsonUtil result = new GsonUtil("{}");
                                try {
                                    String signedTx = transaction.sign(privateKey);
                                    result.putBoolean("result", true);
                                    result.putString("signedTx", signedTx);
                                    result.putString("msg", MSG_SUCCESS);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    result.putBoolean("result", false);
                                    result.putString("msg", e.getMessage());
                                }
//                                        mAgentWeb.getJsAccessEntrace().callJs("javascript:" + mCallBackId + "('" + result.toString() + "')");
                                mAgentWeb.getUrlLoader().loadUrl("javascript:" + callBackId + "('" + result.toString() + "')");
//                                        mAgentWeb.getJsAccessEntrace().quickCallJs(mCallBackId, "hahahah");
                            } else {
                                mWalletManager.transferForHash(privateKey, mCurrentWallet, mTo, mToken, mIssuer, mValue, fee.stripTrailingZeros().toPlainString(), mMemo, new ICallBack() {
                                    @Override
                                    public void onResponse(Object object) {
                                        GsonUtil res = (GsonUtil) object;
                                        mAgentWeb.getJsAccessEntrace().callJs("javascript:" + callBackId + "('" + res.toString() + "')");
                                    }
                                });
                            }
                            loadDialog.dismiss();
                        }
                    });
                }
            });
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            return false;
        }
        return true;
    }

    private Spanned formatHtml() {
        String token;
        if (TextUtils.equals(WConstant.CURRENCY_SWT, mToken)) {
            token = WConstant.CURRENCY_SWTC;
        } else if (TextUtils.equals(WConstant.CURRENCY_CNY, mToken) && TextUtils.equals(WConstant.CURRENCY_ISSUE, mIssuer)) {
            token = WConstant.CURRENCY_CNT;
        } else {
            token = mToken;
        }
        String paysH = "<font color=\"#3B6CA6\">" + mValue + " </font>";
        String paysCurH = "<font color=\"#021E38\">" + token + " </font>";
        return Html.fromHtml(paysH.concat(paysCurH));
    }
}
