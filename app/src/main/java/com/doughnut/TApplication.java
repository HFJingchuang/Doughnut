package com.doughnut;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.doughnut.activity.BaseActivity;
import com.doughnut.base.BlockChainData;
import com.doughnut.base.JSUtil;
import com.doughnut.base.WalletInfoManager;
import com.doughnut.base.TBController;
import com.doughnut.config.AppConfig;
import com.doughnut.utils.LanguageUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.external.ExternalAdaptInfo;


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
        AutoSize.initCompatMultiProcess(this);
        AutoSizeConfig.getInstance().setExcludeFontScale(true);
        AutoSizeConfig.getInstance().getExternalAdaptManager()
                .addExternalAdaptInfoOfActivity(SmartRefreshLayout.class, new ExternalAdaptInfo(true, 410));
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
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
