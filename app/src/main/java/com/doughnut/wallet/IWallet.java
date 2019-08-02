package com.doughnut.wallet;

import android.graphics.Bitmap;

import com.android.jtblk.client.bean.AccountTx;

import java.math.BigDecimal;

public interface IWallet {

    String createWallet(String password, String name);

    boolean deleteWallet(String address);

    Bitmap exportWalletWithQR(String address, int widthAndHeight, int color);

    String importWalletWithKey(String password, String privateKey, String name);

    String importQRImage(Bitmap qrImage, String name);

    String importKeysStore(String keyStore, String name);

    String getPrivateKey(String password, String address);

    String transfer(String password, String from, String to, BigDecimal value, String memo);

    AccountTx getTansferHishory(String address, Integer limit);

    String getBalance(String address);
}
