package com.doughnut.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Rect;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.doughnut.config.AppConfig;


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

    /**
     * 替换TextView的省略号{...}为星号{***}
     *
     * @param textView
     */
    public static void EllipsisTextView(TextView textView) {
        ViewTreeObserver observer = textView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int cut = 2;
                Layout layout;
                if (textView != null && (layout = textView.getLayout()) != null) {
                    int lines = layout.getLineCount();
                    int ellipsisCount = layout.getEllipsisCount(lines - 1);
                    String showText = textView.getText().toString();
                    if (ellipsisCount == 0) {
                        return true;
                    } else if (ellipsisCount < 3 && (showText.length() - ellipsisCount) / 2 > 4) {
                        cut = 4;
                    }
                    String startStr = showText.substring(0, layout.getEllipsisStart(lines - 1) - cut);
                    String endStr = showText.substring(layout.getEllipsisStart(lines - 1) + ellipsisCount + cut);
                    textView.setText(startStr + "***" + endStr);
                }
                return true;
            }
        });
    }

    /**
     * @param root         最外层布局，需要调整的布局
     * @param scrollToView 被键盘遮挡的scrollToView，滚动root，使scrollToView在root可视区域的底部
     */
    public static void controlKeyboardLayout(final View root, final ScrollView scrollToView, final Activity context) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final Rect rect = new Rect();
                //获取root在窗体的可视区域
                root.getWindowVisibleDisplayFrame(rect);
                //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;
                //若不可视区域高度大于100，则键盘显示
                if (rootInvisibleHeight > 100) {
                    int[] location = new int[2];
                    //获取scrollToView在窗体的坐标
                    scrollToView.getLocationInWindow(location);
                    final Rect rect1 = new Rect();
                    if (context.getWindow().getCurrentFocus() == null) {
                        return;
                    }
                    context.getWindow().getCurrentFocus().getGlobalVisibleRect(rect1);
                    final int px = dip2px(context, 100);
                    if ((rect.bottom < rect1.bottom) || ((rect.bottom - rect1.bottom) < px)) {
                        scrollToView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollToView.scrollBy(0, px - (rect.bottom - rect1.bottom));
                            }
                        });
                    } else if ((rect.bottom - rect1.bottom) > 2 * px) {
                        scrollToView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollToView.scrollBy(0, -((rect.bottom - rect1.bottom) - px));
                            }
                        });
                    }
                } else {
                    //键盘隐藏
                    root.scrollTo(0, 0);
                }
            }
        });
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getWindowWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getWindowHight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 隐藏软键盘
     *
     * @param view
     */
    public static void hideKeyboard(View view) {
        AppConfig.postOnUiThread(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) view.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
    }
}
