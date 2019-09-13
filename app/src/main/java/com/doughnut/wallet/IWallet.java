package com.doughnut.wallet;

import android.graphics.Bitmap;
import android.widget.TextView;

import com.android.jtblk.client.bean.AccountRelations;
import com.android.jtblk.client.bean.AccountTx;
import com.android.jtblk.client.bean.Line;
import com.android.jtblk.client.bean.Marker;

import java.math.BigDecimal;
import java.util.List;

public interface IWallet {

    String createWallet(String password, String name);

    void deleteWallet(String address);

    Bitmap exportWalletWithQR(String address, int widthAndHeight, int color);

    boolean importWalletWithKey(String password, String privateKey, String name);

    boolean importKeysStore(String keyStore, String password, String name);

    String getPrivateKey(String password, String address);

    String transfer(String password, String from, String to, String token, String issuer, String value, String fee, String memo);

    AccountTx getTransferHistory(String address, Integer limit, Marker marker);

    AccountRelations getBalance(String address);

    String getSWTBalance(String address);

    void getTokenPrice(String base, BigDecimal balance, TextView v1, TextView v2);

    void getAllTokenPrice(List<Line> dataList, TextView v1, TextView v2, TextView v3, TextView v4, Boolean isHidden);

    void getAllTokens();
}
