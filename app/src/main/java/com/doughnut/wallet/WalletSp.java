package com.doughnut.wallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.doughnut.utils.GsonUtil;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.doughnut.config.Constant.words;

public class WalletSp {

    private static WalletSp instance;
    private static Context mContext;
    private static final String WALLET = "wallet";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static String mAddress;

    private WalletSp() {
    }

    public static WalletSp getInstance(Context context, String address) {
        if (sharedPreferences == null || !sharedPreferences.contains(address)) {
            mContext = context;
            mAddress = address;
            String fileName = context.getPackageName() + "_" + WALLET + "_" + address;
            sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            instance = new WalletSp();
        }
        return instance;
    }

    public void setName(String name) {
        editor.putString("name", name);
        editor.apply();
    }

    public String getName() {
        return sharedPreferences.getString("name", null);
    }

    private void setAddress(String address) {
        editor.putString("address", address);
        editor.apply();
    }

    public String getAddress() {
        return sharedPreferences.getString("address", null);
    }

    private void setKeyStore(String keyStore) {
        editor.putString("keyStore", keyStore);
        editor.apply();
    }

    public String getKeyStore() {
        return sharedPreferences.getString("keyStore", null);
    }

    private void setCreateTime() {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(currentTime);
        String sim = formatter.format(date);
        editor.putString("createTime", sim);
        editor.apply();
    }

    public String getCreateTime() {
        return sharedPreferences.getString("createTime", null);
    }

    public void createWallet(String name, String keyStore) {
        String fileName = mContext.getPackageName() + "_wallets";
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String wallets = sharedPreferences.getString("wallets", "");
        List<String> walletList;
        if (TextUtils.isEmpty(wallets)) {
            walletList = new ArrayList();
        } else {
            String str = wallets.replace("[", "").replace("]", "");
            List<String> arrList = Arrays.asList(str.split(","));
            walletList = new ArrayList(arrList);
        }
        if (!walletList.contains(mAddress)) {
            setName(name);
            setAddress(mAddress);
            setKeyStore(keyStore);
            setCreateTime();
            walletList.add(mAddress);
            editor.putString("wallets", walletList.toString());
            editor.apply();
        }

    }

    public void delete() {
        editor.clear();
        editor.apply();
    }
}
