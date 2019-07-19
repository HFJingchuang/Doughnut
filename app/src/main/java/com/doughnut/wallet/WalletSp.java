package com.doughnut.wallet;

import android.content.Context;
import android.content.SharedPreferences;

public class WalletSp {

    private static WalletSp instance;
    private static final String WALLET = "wallet";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private WalletSp() {
    }

    public static WalletSp getInstance(Context context) {
        if (instance == null) {
            String fileName = context.getPackageName() + "_" + WALLET;
            sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            instance = new WalletSp();
        }
        return instance;
    }

    public void setAddress(String address) {
        editor.putString("address", address);
        editor.apply();
    }

    public String getAddress() {
        return sharedPreferences.getString("address", null);
    }

    public void setKeyStore(String keyStore) {
        editor.putString("keyStore", keyStore);
        editor.apply();
    }

    public String getKeyStore() {
        return sharedPreferences.getString("keyStore", null);
    }
}
