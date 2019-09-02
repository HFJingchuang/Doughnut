package com.doughnut.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.activity.StartBakupActivity;
import com.doughnut.base.WalletInfoManager;
import com.doughnut.dialog.EditDialog;
import com.doughnut.dialog.WarnDialog;


public class ViewUtil {

    public static View inflatView(Context context, ViewGroup parent, int id, boolean attach) {
        return LayoutInflater.from(context).inflate(id, parent, attach);
    }

    public static View inflatView(LayoutInflater inflater, ViewGroup parent, int id, boolean attach) {
        return inflater.inflate(id, parent, attach);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void showSysAlertDialog(Context context, String title, String btnTxt) {
        new AlertDialog.Builder(context).setTitle(title).setNegativeButton(btnTxt, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    public static void showSysAlertDialog(Context context, String title, String message, String cancelTxt, DialogInterface.OnClickListener negListener, String positiveTxt, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setNegativeButton(cancelTxt, negListener).setPositiveButton(positiveTxt, listener).show();
    }

    public static void showBakupDialog(final Context context, final WalletInfoManager.WData walletData, boolean canCancel, final boolean needVerifyPwd, final String pwdHash) {
        final WarnDialog warnDialog = new WarnDialog(context, context.getString(R.string.dialog_content_wallet_security), context.getString(R.string.dialog_btn_backup), canCancel,
                new WarnDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(final Dialog dialog, View view) {

                        if (needVerifyPwd) {
//                            EditDialog editDialog = new EditDialog(context, new EditDialog.PwdResultListener() {
//                                @Override
//                                public void authPwd(String tag, boolean result, String key) {
//                                    if (result) {
//                                        if (TextUtils.isEmpty(walletData.words)) {
//                                            StartBakupActivity.startBakupWalletStartActivity(context, walletData.waddress,
//                                                    1);
//                                        } else {
//                                            StartBakupActivity.startBakupWalletStartActivity(context, walletData.waddress,
//                                                    2);
//                                        }
//                                        dialog.dismiss();
//                                    } else {
//                                        ToastUtil.toast(context, context.getString(R.string.toast_password_incorrect));
//                                    }
//                                }
//                            }, pwdHash, "");
//                            editDialog.show();
                        } else {
                            if (TextUtils.isEmpty(walletData.words)) {
                                StartBakupActivity.startBakupWalletStartActivity(context, walletData.waddress,
                                        1);
                            } else {
                                StartBakupActivity.startBakupWalletStartActivity(context, walletData.waddress,
                                        2);
                            }
                            dialog.dismiss();
                        }
                    }
                });
        warnDialog.show();
    }

    public static void showBakupDialog(final Context context, final WalletInfoManager.WData walletData, boolean canCancel) {
        showBakupDialog(context, walletData, canCancel, false, "");
    }


    /**
     * 替换TextView的省略号{...}为星号{***}
     *
     * @param textView
     */
    public static void EllipsisTextView(TextView textView) {
        ViewTreeObserver observer = textView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            boolean isfirstRunning = true;

            @Override
            public void onGlobalLayout() {
                if (isfirstRunning == false) return;
                Layout layout = textView.getLayout();
                if (textView != null && layout != null) {
                    int lines = layout.getLineCount();
                    int ellipsisCount = layout.getEllipsisCount(lines - 1);
                    if (ellipsisCount == 0) return;
                    String showText = textView.getText().toString();
                    String startStr = showText.substring(0, layout.getEllipsisStart(lines - 1) - 1);
                    String endStr = showText.substring(layout.getEllipsisStart(lines - 1) + ellipsisCount + 1);
                    textView.setText(startStr + "***" + endStr);
                    isfirstRunning = false;
                }
            }
        });
    }
}
