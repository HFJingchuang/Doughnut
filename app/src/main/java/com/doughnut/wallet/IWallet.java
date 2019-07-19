package com.doughnut.wallet;

import android.graphics.Bitmap;

public interface IWallet {

    boolean createWallet(String password);

    Bitmap exportWalletWithQR(int widthAndHeight, int color);

    boolean importWalletWithKey(String password, String privateKey);

    boolean importQRImage(Bitmap qrImage);

    boolean importKeysStore(String keyStore);

    String getPrivateKey(String password);

}
