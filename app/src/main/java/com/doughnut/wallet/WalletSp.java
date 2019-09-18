package com.doughnut.wallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 本地钱包信息管理类
 * <p>
 * 钱包名称、地址、KeyStore的JSON字符串、创建时间
 * <p>
 * 当前钱包地址和所有钱包地址
 */
public class WalletSp {

    private static WalletSp instance;
    private static Context mContext;
    private static final String WALLET = "wallet";
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;
    private static String mAddress;

    private WalletSp() {
    }

    public static WalletSp getInstance(Context context, String address) {
        if (mSharedPreferences == null || !mSharedPreferences.contains(address)) {
            mContext = context;
            mAddress = address;
            String fileName = context.getPackageName() + "_" + WALLET + "_" + address;
            mSharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
            mEditor = mSharedPreferences.edit();
            instance = new WalletSp();
        }
        return instance;
    }

    /**
     * 保存钱包名称
     *
     * @param name
     */
    public void setName(String name) {
        mEditor.putString("name", name);
        mEditor.apply();
    }

    /**
     * 获取钱包名称
     *
     * @return
     */
    public String getName() {
        return mSharedPreferences.getString("name", null);
    }

    /**
     * 保存钱包地址
     *
     * @param address
     */
    private void setAddress(String address) {
        mEditor.putString("address", address);
        mEditor.apply();
    }

    /**
     * 获取钱包地址
     *
     * @return
     */
    public String getAddress() {
        return mSharedPreferences.getString("address", null);
    }

    /**
     * 保存钱包KeyStore的JSON字符串
     *
     * @param keyStore
     */
    private void setKeyStore(String keyStore) {
        mEditor.putString("keyStore", keyStore);
        mEditor.apply();
    }

    /**
     * 获取钱包KeyStore的JSON字符串
     *
     * @return
     */
    public String getKeyStore() {
        return mSharedPreferences.getString("keyStore", null);
    }

    /**
     * 保存钱包创建或导入时间，格式：yyyy-MM-dd HH:mm:ss
     */
    private void setCreateTime() {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(currentTime);
        String sim = formatter.format(date);
        mEditor.putString("createTime", sim);
        mEditor.apply();
    }

    /**
     * 获取钱包创建或导入时间，格式：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public String getCreateTime() {
        return mSharedPreferences.getString("createTime", null);
    }

    /**
     * 保存钱包信息,格式：0x...,0x...
     *
     * @param name
     * @param keyStore
     */
    public void createWallet(String name, String keyStore) {
        String fileName = mContext.getPackageName() + "_wallets";
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String wallets = sharedPreferences.getString("wallets", "");
        List<String> walletList;
        if (TextUtils.isEmpty(wallets)) {
            walletList = new ArrayList();
        } else {
            if (wallets.contains(",")) {
                List<String> arrList = Arrays.asList(wallets.split(","));
                walletList = new ArrayList(arrList);
            } else {
                walletList = new ArrayList();
                walletList.add(wallets);
            }
        }
        if (!walletList.contains(mAddress)) {
            setAddress(mAddress);
            walletList.add(mAddress);
            editor.putString("wallets", walletList.toString().replace("[", "").replace("]", "").replace(" ", ""));
            editor.apply();
        }
        if (!TextUtils.isEmpty(name)) {
            setName(name);
        }
        setCreateTime();
        setKeyStore(keyStore);
        setCurrentWallet(mAddress);
    }

    /**
     * 删除钱包信息
     */
    public void delete() {
        mEditor.clear();
        mEditor.apply();
        String fileName = mContext.getPackageName() + "_wallets";
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String wallets = sharedPreferences.getString("wallets", "");
        List<String> walletList;
        if (TextUtils.isEmpty(wallets)) {
            walletList = new ArrayList();
        } else {
            List<String> arrList = Arrays.asList(wallets.split(","));
            walletList = new ArrayList(arrList);
        }
        if (walletList.contains(mAddress)) {
            walletList.remove(mAddress);
            editor.putString("wallets", walletList.toString().replace("[", "").replace("]", "").replace(" ", ""));
            editor.apply();
        }
        String currentWallet = getCurrentWallet();
        // 若删除为当前钱包，删除后须选择一个钱包作为当前钱包
        if (mAddress.equals(currentWallet)) {
            if (walletList.size() > 0) {
                setCurrentWallet(walletList.get(0));
            } else {
                setCurrentWallet("");
            }
        }
    }

    /**
     * 获取app保存的所有钱包
     *
     * @return
     */
    public List<String> getAllWallet() {
        String fileName = mContext.getPackageName() + "_wallets";
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String wallets = sharedPreferences.getString("wallets", "");
        List<String> walletList;
        if (TextUtils.isEmpty(wallets)) {
            walletList = new ArrayList();
        } else {
            List<String> arrList = Arrays.asList(wallets.split(","));
            walletList = new ArrayList(arrList);
        }
        return walletList;
    }

    /**
     * 保存app当前钱包地址
     *
     * @param address
     */
    public void setCurrentWallet(String address) {
        String fileName = mContext.getPackageName() + "_wallets";
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("currentWallet", address);
        editor.apply();
    }

    /**
     * 获取app当前钱包地址
     *
     * @return
     */
    public String getCurrentWallet() {
        String fileName = mContext.getPackageName() + "_wallets";
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sharedPreferences.getString("currentWallet", "");
    }
}
