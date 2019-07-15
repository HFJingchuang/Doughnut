package com.doughnut.wallet;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.jtwallet.keyStore.JtKeyPair;
import com.android.jtwallet.keyStore.KeyStore;
import com.android.jtwallet.keyStore.KeyStoreFile;
import com.android.jtwallet.qrCode.QrCodeGenerator;
import com.blink.jtblc.client.Remote;
import com.blink.jtblc.client.Transaction;
import com.blink.jtblc.client.Wallet;
import com.blink.jtblc.client.bean.AccountTx;
import com.blink.jtblc.client.bean.AmountInfo;
import com.blink.jtblc.client.bean.TransactionInfo;

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
            JtKeyPair jtKeyPair = new JtKeyPair(wallet.getAddress(), wallet.getSecret());
            KeyStoreFile keyStoreFile = KeyStore.createLight(password, jtKeyPair);
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
            Bitmap bitmap = QrCodeGenerator.getQrCodeImage(keyStore, widthAndHeight, color);
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
                JtKeyPair jtKeyPair = new JtKeyPair(wallet.getAddress(), privateKey);
                KeyStoreFile keyStoreFile = KeyStore.createLight(password, jtKeyPair);
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
            String keyStore = QrCodeGenerator.decodeQrImage(qrImage);
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
            JtKeyPair jtKeyPair = KeyStore.decrypt(password, keyStoreFile);
            return jtKeyPair.getPrivateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //todo 转账
    public void transfer(String password, String to, BigDecimal value, String memo) {
        AmountInfo amount = new AmountInfo();
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
            System.out.println("数据上链成功 Hash：" + bean.getTxJson().getHash());
        } else {
            System.out.println("数据上链失败 Message：" + bean.getEngineResult());
        }
    }

    //todo 交易记录
    public AccountTx getTansferHishory(Integer limit) {
        String address = WalletSp.getInstance(mContext).getAddress();
        AccountTx bean = remote.requestAccountTx(address, limit, null);
        return bean;
    }
}
