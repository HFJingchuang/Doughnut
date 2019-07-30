package com.doughnut.wallet;

import android.graphics.Bitmap;

import com.android.jtblk.client.bean.AccountTx;

import java.math.BigDecimal;

public interface IWallet {

    boolean createWallet(String password);

    Bitmap exportWalletWithQR(int widthAndHeight, int color);

    boolean importWalletWithKey(String password, String privateKey);

    boolean importQRImage(Bitmap qrImage);

    boolean importKeysStore(String keyStore);

    String getPrivateKey(String password);

    String transfer(String password, String to, BigDecimal value, String memo);

    AccountTx getTansferHishory(Integer limit);

    String getBalance();
}
