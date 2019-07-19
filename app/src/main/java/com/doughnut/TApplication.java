package com.doughnut;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.doughnut.activity.BaseActivity;
import com.doughnut.base.BlockChainData;
import com.doughnut.base.JSUtil;
import com.doughnut.base.WalletInfoManager;
import com.doughnut.base.TBController;
import com.doughnut.config.AppConfig;
import com.doughnut.utils.LanguageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class TApplication extends Application {

    private final static String TAG = "TApplication";
    private List<BaseActivity> mActivities = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        AppConfig.init(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        BlockChainData.getInstance().init();
        TBController.getInstance().init();
        WalletInfoManager.getInstance().init();
        JSUtil.getInstance().init();
    }

    public void addActivity(BaseActivity activity) {
        mActivities.add(activity);
        AppConfig.setCurActivity(activity);
    }

    public void popActivity(BaseActivity activity) {
        mActivities.remove(activity);
        if (!activity.isFinishing()) {
            activity.finish();
        }
    }

    public void clearActivity() {
        for (BaseActivity activity : mActivities
        ) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Locale locale = LanguageUtil.getUserLocale(base);
            super.attachBaseContext(LanguageUtil.updateLocale(base, locale));
        } else {
            super.attachBaseContext(base);
        }
        MultiDex.install(base);
    }
}
