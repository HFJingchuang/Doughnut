package com.doughnut.wallet;

import android.content.Context;
import android.graphics.Bitmap;

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
     * @param name
     * @return
     */
    @Override
    public String createWallet(String password, String name) {
        try {
            Wallet wallet = Wallet.generate();
            KeyStoreFile keyStoreFile = KeyStore.createLight(password, wallet);
            String address = keyStoreFile.getAddress();
            WalletSp.getInstance(mContext, address).createWallet(name, keyStoreFile.toString());
            return address;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public boolean deleteWallet(String address) {
        return false;
    }

    /**
     * 导出二维码图片
     *
     * @param address
     * @param widthAndHeight
     * @param color
     * @return
     */
    @Override
    public Bitmap exportWalletWithQR(String address, int widthAndHeight, int color) {
        try {
            String keyStore = WalletSp.getInstance(mContext, address).getKeyStore();
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
     * @param name
     * @return
     */
    @Override
    public String importWalletWithKey(String password, String privateKey, String name) {
        try {
            if (Wallet.isValidSecret(privateKey)) {
                Wallet wallet = Wallet.fromSecret(privateKey);
                KeyStoreFile keyStoreFile = KeyStore.createLight(password, wallet);
                String address = keyStoreFile.getAddress();
                WalletSp.getInstance(mContext, address).createWallet(name, keyStoreFile.toString());
                return address;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 导入二维码图片
     *
     * @param qrImage
     * @param name
     * @return
     */
    @Override
    public String importQRImage(Bitmap qrImage, String name) {
        try {
            String keyStore = QRGenerator.decodeQrImage(qrImage);
            return importKeysStore(keyStore, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 导入KeyStore
     *
     * @param keyStore
     * @param name
     * @return
     */
    @Override
    public String importKeysStore(String keyStore, String name) {
        try {
            KeyStoreFile keyStoreFile = KeyStoreFile.parse(keyStore);
            String address = keyStoreFile.getAddress();
            if (Wallet.isValidAddress(address)) {
                WalletSp.getInstance(mContext, address).createWallet(name, keyStoreFile.toString());
                return address;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取私钥
     *
     * @param password
     * @param address
     * @return
     */
    @Override
    public String getPrivateKey(String password, String address) {
        try {
            KeyStoreFile keyStoreFile = KeyStoreFile.parse(WalletSp.getInstance(mContext, address).getKeyStore());
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
     * @param from
     * @param to
     * @param value
     * @param memo
     * @return
     */
    @Override
    public String transfer(String password, String from, String to, BigDecimal value, String memo) {
        try {
            AmountInfo amount;
            amount = new AmountInfo();
            amount.setCurrency("SWT");
            amount.setValue(value.toString());
            Transaction tx = JtServer.getInstance().getRemote().buildPaymentTx(from, to, amount);
            tx.setSecret(getPrivateKey(password, from));
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
    @Override
    public AccountTx getTansferHishory(String address, Integer limit) {
        try {
            AccountTx bean = JtServer.getInstance().getRemote().requestAccountTx(address, limit, null);
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
    @Override
    public String getBalance(String address) {
        try {
            AccountInfo bean = JtServer.getInstance().getRemote().requestAccountInfo(address, null, null);
            return bean.getAccountData().getBalance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
