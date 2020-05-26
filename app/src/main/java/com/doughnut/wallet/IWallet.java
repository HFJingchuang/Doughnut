package com.doughnut.wallet;

import android.graphics.Bitmap;

import com.android.jtblk.BIP44.AddressIndex;
import com.android.jtblk.client.bean.Marker;
import com.jccdex.rpc.base.JCallback;

public interface IWallet {

    void createWallet(String password, String name, boolean isED25519, ICallBack callBack);

    void deleteWallet(String address);

    Bitmap exportWalletWithQR(String address, int widthAndHeight, int color);

    void importWalletWithKey(String password, String privateKey, String name, boolean isED25519, ICallBack callBack);

    void importKeysStore(String keyStore, String password, String name, ICallBack callBack);

    void importMnemonics(String password, String privateKey, String name, boolean isED25519, ICallBack callBack);

    void importMnemonicsWithPath(String mnemonics, String password, String name, AddressIndex addressIndex, boolean isED25519, ICallBack callBack);

    void getPrivateKey(String password, String keyStore, ICallBack callBack);

    void transfer(String privateKey, String from, String to, String token, String issuer, String value, String fee, String memo, ICallBack callBack);

    void transferForHash(String privateKey, String from, String to, String token, String issuer, String value, String fee, String memo, ICallBack callBack);

    void getTransferHistory(String address, Integer limit, Marker marker, ICallBack callBack);

    void getBalance(String address, boolean isDisposable, ICallBack callBack);

    void getSWTBalance(String address, ICallBack callBack);

    void getTokenPrice(String base, JCallback jCallback);

    void getAllTokenPrice(JCallback jCallback);

    void getAllTokens();
}
