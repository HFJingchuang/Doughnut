package com.doughnut.activity;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.utils.ViewUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class IntentParseActivity extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();
        if (appLinkData != null) {
            String host = appLinkData.getHost();
            String path = appLinkData.getPath();
            Set<String> queryParameterNames = appLinkData.getQueryParameterNames();
            HashMap<String, String> map = null;
            if (!queryParameterNames.isEmpty()) {
                map = new HashMap<>();
                for (String name : queryParameterNames) {
                    map.put(name, appLinkData.getQueryParameter(name));
                }
            }
            Intent linkIntent = parseSchemes(host, path, map);
            if (linkIntent == null) {
                finish();
                return;
            }
            linkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (ViewUtil.isLaunchedActivity(this, MainActivity.class)) {
                startActivity(linkIntent);
            } else {
                TaskStackBuilder.create(this)
                        .addParentStack(linkIntent.getComponent())
                        .addNextIntent(linkIntent).startActivities();
            }
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private Intent parseSchemes(String host, String path, Map<String, String> queryies) {
        String[] paths = path.split("/");
        Intent intent;
        switch (host) {
            case "transfer":
                intent = new Intent(this, TokenTransferActivity.class);
                String address = queryies.get(Constant.RECEIVE_ADDRESS_KEY);
                String amount = queryies.get(Constant.TOEKN_AMOUNT);
                String tokenName = queryies.get(Constant.TOEKN_NAME);
                // 本地保存tokens
                String fileName = getPackageName() + "_transfer_token";
                SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", tokenName);
                editor.apply();
                intent.putExtra(Constant.RECEIVE_ADDRESS_KEY, address);
                intent.putExtra(Constant.TOEKN_AMOUNT, amount);
                return intent;
            case "receive":
                intent = new Intent(this, TokenReceiveActivity.class);
                return intent;
        }
        intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
