package com.doughnut.wallet;

import android.content.Context;
import android.graphics.Bitmap;


import com.android.jtblk.client.Remote;
import com.android.jtblk.client.Transaction;
import com.android.jtblk.client.Wallet;
import com.android.jtblk.client.bean.AccountInfo;
import com.android.jtblk.client.bean.AccountTx;
import com.android.jtblk.client.bean.AmountInfo;
import com.android.jtblk.client.bean.TransactionInfo;
import com.android.jtblk.keyStore.KeyStore;
import com.android.jtblk.keyStore.KeyStoreFile;
import com.android.jtblk.qrCode.QRGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class WalletManager implements IWallet {

    private static WalletManager walletManager = null;
    private static Context mContext;
    private static Remote remote = new JtServer().getRemote();

    private WalletManager() {
    }

    public static WalletManager getInstance(Context context) {
        mContext = context;
        if (walletManager == null) {
            synchronized (WalletManager.class) {
                if (walletManager == null) {
                    walletManager = new WalletManager();
                }
            }
        }
        return walletManager;
    }

    /**
     * 创建钱包
     *
     * @param password
     * @return
     */
    @Override
    public boolean createWallet(String password) {
        try {
            Wallet wallet = Wallet.generate();
            KeyStoreFile keyStoreFile = KeyStore.createLight(password, wallet);
            WalletSp.getInstance(mContext).setAddress(keyStoreFile.getAddress());
            WalletSp.getInstance(mContext).setKeyStore(keyStoreFile.toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 导出二维码图片
     *
     * @param widthAndHeight
     * @param color
     * @return
     */
    @Override
    public Bitmap exportWalletWithQR(int widthAndHeight, int color) {
        try {
            String keyStore = WalletSp.getInstance(mContext).getKeyStore();
            Bitmap bitmap = QRGenerator.getQrCodeImage(keyStore, widthAndHeight, color);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 导入私钥
     *
     * @param password
     * @param privateKey
     * @return
     */
    @Override
    public boolean importWalletWithKey(String password, String privateKey) {
        try {
            if (Wallet.isValidSecret(privateKey)) {
                Wallet wallet = Wallet.fromSecret(privateKey);
                KeyStoreFile keyStoreFile = KeyStore.createLight(password, wallet);
                WalletSp.getInstance(mContext).setAddress(keyStoreFile.getAddress());
                WalletSp.getInstance(mContext).setKeyStore(keyStoreFile.toString());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 导入二维码图片
     *
     * @param qrImage
     * @return
     */
    @Override
    public boolean importQRImage(Bitmap qrImage) {
        try {
            String keyStore = QRGenerator.decodeQrImage(qrImage);
            return importKeysStore(keyStore);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 导入KeyStore
     *
     * @param keyStore
     * @return
     */
    @Override
    public boolean importKeysStore(String keyStore) {
        try {
            KeyStoreFile keyStoreFile = KeyStoreFile.parse(keyStore);
            String address = keyStoreFile.getAddress();
            if (Wallet.isValidAddress(address)) {
                WalletSp.getInstance(mContext).setAddress(keyStoreFile.getAddress());
                WalletSp.getInstance(mContext).setKeyStore(keyStoreFile.toString());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getPrivateKey(String password) {
        try {
            KeyStoreFile keyStoreFile = KeyStoreFile.parse(WalletSp.getInstance(mContext).getKeyStore());
            Wallet jtKeyPair = KeyStore.decrypt(password, keyStoreFile);
            return jtKeyPair.getSecret();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转账
     *
     * @param password
     * @param to
     * @param value
     * @param memo
     * @return
     */
    public String transfer(String password, String to, BigDecimal value, String memo) {
        try {
            AmountInfo amount;
            amount = new AmountInfo();
            amount.setCurrency("SWT");
            amount.setValue(value.toString());
            String from = WalletSp.getInstance(mContext).getAddress();
            Transaction tx = remote.buildPaymentTx(from, to, amount);
            tx.setSecret(getPrivateKey(password));
            List<String> memos = new ArrayList<String>();
            memos.add(memo);
            tx.addMemo(memos);
            TransactionInfo bean = tx.submit();
            if ("0".equals(bean.getEngineResultCode())) {
                return bean.getTxJson().getHash();
            } else {
                return bean.getEngineResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取交易记录
     *
     * @param limit
     * @return
     */
    public AccountTx getTansferHishory(Integer limit) {
        try {
            String address = WalletSp.getInstance(mContext).getAddress();
            AccountTx bean = remote.requestAccountTx(address, limit, null);
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取余额
     *
     * @return
     */
    public String getBalance() {
        try {
            String address = WalletSp.getInstance(mContext).getAddress();
            AccountInfo bean = remote.requestAccountInfo(address, null, null);
            return bean.getAccountData().getBalance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
