package com.doughnut.wallet;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.android.jtblk.client.Transaction;
import com.android.jtblk.client.Wallet;
import com.android.jtblk.client.bean.Account;
import com.android.jtblk.client.bean.AccountInfo;
import com.android.jtblk.client.bean.AccountOffers;
import com.android.jtblk.client.bean.AccountRelations;
import com.android.jtblk.client.bean.AccountTx;
import com.android.jtblk.client.bean.AmountInfo;
import com.android.jtblk.client.bean.Line;
import com.android.jtblk.client.bean.Marker;
import com.android.jtblk.client.bean.Offer;
import com.android.jtblk.client.bean.TransactionInfo;
import com.android.jtblk.keyStore.KeyStore;
import com.android.jtblk.keyStore.KeyStoreFile;
import com.android.jtblk.qrCode.QRGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 钱包管理类
 * <p>
 * 创建钱包、删除钱包、导出二维码图片、导入私钥、导入二维码图片、导入KeyStore、获取私钥、转账、获取交易记录、获取余额
 */
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
     * 判断app当前是否有钱包
     *
     * @return
     */
    public boolean hasWallet() {
        try {
            return !TextUtils.isEmpty(WalletSp.getInstance(mContext, "").getCurrentWallet());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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

    /**
     * 删除钱包
     */
    @Override
    public void deleteWallet(String address) {
        WalletSp.getInstance(mContext, address).delete();
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
            amount.setCurrency(WConstant.CURRENCY_SWT);
            amount.setValue(value.toString());
            Transaction tx = JtServer.getInstance().getRemote().buildPaymentTx(from, to, amount);
            tx.setSecret(getPrivateKey(password, from));
            List<String> memos = new ArrayList<String>();
            memos.add(memo);
            tx.addMemo(memos);
            TransactionInfo bean = tx.submit();
            if (WConstant.RESULT_OK.equals(bean.getEngineResultCode())) {
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
    public AccountTx getTansferHishory(String address, Integer limit, Marker marker) {
        try {
            AccountTx bean = JtServer.getInstance().getRemote().requestAccountTx(address, limit, marker);
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
    public AccountRelations getBalance(String address) {
        try {
            // 获取账户信息
            AccountInfo info = JtServer.getInstance().getRemote().requestAccountInfo(address, null, null);
            // 获取账户挂单信息
            AccountOffers accountOffers = JtServer.getInstance().getRemote().requestAccountOffers(address, null);
            // 获取账户其它token信息
            AccountRelations relationsTrust = JtServer.getInstance().getRemote().requestAccountRelations(address, null, WConstant.RELATION_TRUST);
            AccountRelations relationsFreeze = JtServer.getInstance().getRemote().requestAccountRelations(address, null, WConstant.RELATION_FREEZE);

            // 计算其它token冻结数量
            List<Offer> offers = accountOffers.getOffers();
            List<Line> linesT = relationsTrust.getLines();

            // 计算swt冻结数量
            Integer freezed = (relationsTrust.getLines().size() + accountOffers.getOffers().size()) * WConstant.FREEZED + WConstant.RESERVED;
            Line swtLine = new Line();
            BigDecimal valid = new BigDecimal(info.getAccountData().getBalance()).subtract(BigDecimal.valueOf(freezed));
            swtLine.setBalance(valid.stripTrailingZeros().toPlainString());
            swtLine.setCurrency(WConstant.CURRENCY_SWT);
            swtLine.setLimit(String.valueOf(freezed));
            relationsTrust.getLines().add(swtLine);

            // trust limit 置零
            for (int j = 0; j < linesT.size(); j++) {
                String currency = linesT.get(j).getCurrency();
                if (!TextUtils.equals(currency, WConstant.CURRENCY_SWT)) {
                    linesT.get(j).setLimit("0");
                }
            }

            // 通过挂单计算冻结数量
            for (int i = 0; i < offers.size(); i++) {
                String getsCurrency = offers.get(i).getTakerGets().getCurrency();
                for (int j = 0; j < linesT.size(); j++) {
                    Line line = linesT.get(j);
                    String currency = line.getCurrency();
                    if (TextUtils.equals(getsCurrency, currency)) {
                        BigDecimal tokenFreeze = new BigDecimal(line.getLimit()).add(new BigDecimal(offers.get(i).getTakerGets().getValue()));
                        BigDecimal balance = new BigDecimal(line.getBalance()).subtract(tokenFreeze);
                        line.setBalance(balance.stripTrailingZeros().toPlainString());
                        line.setLimit(tokenFreeze.stripTrailingZeros().toPlainString());
                    }
                }
            }

            // 通过冻结关系类型计算冻结数量
            List<Line> linesF = relationsFreeze.getLines();
            for (int i = 0; i < linesF.size(); i++) {
                String FCurrency = linesF.get(i).getCurrency();
                for (int j = 0; j < linesT.size(); j++) {
                    Line line = linesT.get(j);
                    String currency = line.getCurrency();
                    if (TextUtils.equals(FCurrency, currency)) {
                        BigDecimal tokenFreeze = new BigDecimal(line.getLimit()).add(new BigDecimal(linesF.get(i).getLimit()));
                        BigDecimal balance = new BigDecimal(line.getBalance()).subtract(tokenFreeze);
                        line.setBalance(balance.stripTrailingZeros().toPlainString());
                        line.setLimit(tokenFreeze.stripTrailingZeros().toPlainString());
                    }
                }
            }

            return relationsTrust;
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTrans(String hash) {
        try {
            Account bean = JtServer.getInstance().getRemote().requestTx(hash);
            return bean.getHash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
