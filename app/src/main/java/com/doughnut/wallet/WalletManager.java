package com.doughnut.wallet;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.android.jtblk.BIP39.WordCount;
import com.android.jtblk.BIP39.wordlists.Chinese_simplified;
import com.android.jtblk.BIP44.AddressIndex;
import com.android.jtblk.client.Transaction;
import com.android.jtblk.client.Wallet;
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
import com.doughnut.net.api.GetAllTokenList;
import com.doughnut.net.load.RequestPresenter;
import com.doughnut.utils.AESUtil;
import com.doughnut.utils.CaclUtil;
import com.doughnut.utils.Currency;
import com.doughnut.utils.GsonUtil;
import com.jccdex.rpc.api.JccConfig;
import com.jccdex.rpc.api.JccdexInfo;
import com.jccdex.rpc.base.JCallback;
import com.jccdex.rpc.url.JccdexUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 钱包管理类
 * <p>
 * 创建钱包、删除钱包、导出二维码图片、导入私钥、导入KeyStore、导入助记词、获取私钥、转账、获取交易记录、获取余额
 */
public class WalletManager implements IWallet {

    private final static String CONFIG_HOST = "weidex.vip";
    private final static String COUNTER = "CNT";

    private static WalletManager walletManager = null;
    private static Context mContext;
    private static CompositeDisposable compositeDisposable;

    private WalletManager() {
    }

    public static WalletManager getInstance(Context context) {
        mContext = context.getApplicationContext();
        if (walletManager == null) {
            synchronized (WalletManager.class) {
                if (walletManager == null) {
                    compositeDisposable = new CompositeDisposable();
                    walletManager = new WalletManager();
                }
            }
        }
        if (compositeDisposable != null) {
            compositeDisposable.clear();
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

    private List<String> createWallet(String password, String name, boolean isED25519) {
        List<String> list = new ArrayList<>();
        try {
            Wallet wallet = Wallet.generate(Chinese_simplified.INSTANCE, WordCount.TWELVE, isED25519);
            KeyStoreFile keyStoreFile = KeyStore.createLight(password, wallet);
            String address = keyStoreFile.getAddress();
            String mnemonics = wallet.getMnemonics();
            String enMnemonics = AESUtil.encrypt(password, mnemonics);
            WalletSp.getInstance(mContext, address).createWallet(name, enMnemonics, keyStoreFile.toString());
            list.add(address);
            list.add(mnemonics);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 创建钱包
     *
     * @param password
     * @param name
     * @param isED25519
     * @param callBack
     */
    @Override
    public void createWallet(String password, String name, boolean isED25519, ICallBack callBack) {
        Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(ObservableEmitter<List<String>> emitter) throws Exception {
                List<String> list = createWallet(password, name, isED25519);
                emitter.onNext(list);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<String>>() {
            @Override
            public void accept(List<String> list) throws Exception {
                callBack.onResponse(list);
            }
        });
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

    private boolean importWalletWithKey(String password, String privateKey, String name, boolean isED25519) {
        try {
            if (Wallet.isValidSecret(privateKey)) {
                Wallet wallet = Wallet.fromSecret(privateKey, isED25519);
                KeyStoreFile keyStoreFile = KeyStore.createLight(password, wallet);
                String address = keyStoreFile.getAddress();
                if (Wallet.isValidAddress(address)) {
                    String enMnemonics = AESUtil.encrypt(password, wallet.getMnemonics());
                    WalletSp.getInstance(mContext, address).createWallet(name, enMnemonics, keyStoreFile.toString());
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 导入私钥
     *
     * @param password
     * @param privateKey
     * @param name
     * @param callBack
     */
    @Override
    public void importWalletWithKey(String password, String privateKey, String name, boolean isED25519, ICallBack callBack) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean isSuccess = importWalletWithKey(password, privateKey, name, isED25519);
                emitter.onNext(isSuccess);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean isSuccess) throws Exception {
                callBack.onResponse(isSuccess);
            }
        });
    }

    private boolean importKeysStore(String keyStore, String password, String name) {
        try {
            KeyStoreFile keyStoreFile = KeyStoreFile.parse(keyStore);
            String address = keyStoreFile.getAddress();
            if (Wallet.isValidAddress(address)) {
                String privateKey = getPrivateKey(password, keyStore);
                if (Wallet.isValidSecret(privateKey)) {
                    WalletSp.getInstance(mContext, address).createWallet(name, null, keyStore);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 导入KeyStore
     *
     * @param keyStore
     * @param password
     * @param name
     * @param callBack
     */
    @Override
    public void importKeysStore(String keyStore, String password, String name, ICallBack callBack) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean isSuccess = importKeysStore(keyStore, password, name);
                emitter.onNext(isSuccess);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean isSuccess) throws Exception {
                callBack.onResponse(isSuccess);
            }
        });
    }

    private boolean importMnemonics(String mnemonics, String password, String name, boolean isED25519) {
        try {
            Wallet wallet = Wallet.fromMnemonics(mnemonics, isED25519);
            KeyStoreFile keyStoreFile = KeyStore.createLight(password, wallet);
            String address = keyStoreFile.getAddress();
            if (Wallet.isValidAddress(address)) {
                String enMnemonics = AESUtil.encrypt(password, wallet.getMnemonics());
                WalletSp.getInstance(mContext, address).createWallet(name, enMnemonics, keyStoreFile.toString());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 导入助记词
     *
     * @param mnemonics
     * @param password
     * @param name
     * @param isED25519
     * @param callBack
     */
    @Override
    public void importMnemonics(String mnemonics, String password, String name, boolean isED25519, ICallBack callBack) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean isSuccess = importMnemonics(mnemonics, password, name, isED25519);
                emitter.onNext(isSuccess);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean isSuccess) throws Exception {
                callBack.onResponse(isSuccess);
            }
        });
    }

    private boolean importMnemonicsWithPath(String mnemonics, String password, String name, AddressIndex addressIndex, boolean isED25519) {
        try {
            Wallet wallet = Wallet.fromMnemonicWithPath(mnemonics, addressIndex, isED25519);
            KeyStoreFile keyStoreFile = KeyStore.createLight(password, wallet);
            String address = keyStoreFile.getAddress();
            if (Wallet.isValidAddress(address)) {
                String enMnemonics = AESUtil.encrypt(password, wallet.getMnemonics());
                WalletSp.getInstance(mContext, address).createWallet(name, enMnemonics, keyStoreFile.toString());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 导入助记词
     *
     * @param mnemonics
     * @param password
     * @param name
     * @param addressIndex
     * @param isED25519
     * @param callBack
     */
    @Override
    public void importMnemonicsWithPath(String mnemonics, String password, String name, AddressIndex addressIndex, boolean isED25519, ICallBack callBack) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {

            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean isSuccess = importMnemonicsWithPath(mnemonics, password, name, addressIndex, isED25519);
                emitter.onNext(isSuccess);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean isSuccess) throws Exception {
                callBack.onResponse(isSuccess);
            }
        });
    }

    private String getPrivateKey(String password, String keyStore) {
        try {
            KeyStoreFile keyStoreFile = KeyStoreFile.parse(keyStore);
            Wallet wallet = KeyStore.decrypt(password, keyStoreFile);
            return wallet.getSecret();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取私钥
     *
     * @param password
     * @param keyStore
     * @return
     */
    @Override
    public void getPrivateKey(String password, String keyStore, ICallBack callBack) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String privateKey = getPrivateKey(password, keyStore);
                emitter.onNext(privateKey);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String address) throws Exception {
                callBack.onResponse(address);
            }
        });
    }

    private GsonUtil transferForHash(String privateKey, String from, String to, String token, String issuer, String value, String fee, String memo) {
        GsonUtil res = new GsonUtil("{}");
        try {
            AmountInfo amount;
            amount = new AmountInfo();
            if (TextUtils.equals(WConstant.CURRENCY_SWTC, token)) {
                amount.setCurrency(WConstant.CURRENCY_SWT);
                amount.setIssuer("");
            } else if (TextUtils.equals(WConstant.CURRENCY_CNT, token) && TextUtils.equals(WConstant.CURRENCY_ISSUE, issuer)) {
                amount.setCurrency(WConstant.CURRENCY_CNY);
                amount.setIssuer(issuer);
            } else {
                amount.setCurrency(token);
                amount.setIssuer(issuer);
            }
            amount.setValue(value);
            Transaction tx = JtServer.getInstance(mContext).getRemote().buildPaymentTx(from, to, amount);
            tx.setSecret(privateKey);
            List<String> memos = new ArrayList<String>();
            memos.add(memo);
            tx.addMemo(memos);
            if (!TextUtils.isEmpty(fee)) {
                tx.setFee(fee);
            }
            TransactionInfo bean = tx.submit();
            if (WConstant.RESULT_OK.equals(bean.getEngineResultCode())) {
                res.putBoolean("result", true);
                res.putString("txHash", bean.getTxJson().getHash());
                res.putString("msg", "success");
            } else {
                res.putBoolean("result", false);
                res.putString("msg", bean.getEngineResult());
                return res;
            }
        } catch (Exception e) {
            res.putBoolean("result", false);
            res.putString("msg", e.getMessage());
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 转账，返回交易HASH
     *
     * @param privateKey
     * @param from
     * @param to
     * @param token
     * @param issuer
     * @param value
     * @param fee
     * @param memo
     * @param callBack
     * @return
     */
    @Override
    public void transferForHash(String privateKey, String from, String to, String token, String issuer, String value, String fee, String memo, ICallBack callBack) {
        Observable.create(new ObservableOnSubscribe<GsonUtil>() {
            @Override
            public void subscribe(ObservableEmitter<GsonUtil> emitter) throws Exception {
                emitter.onNext(transferForHash(privateKey, from, to, token, issuer, value, fee, memo));
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<GsonUtil>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(GsonUtil hash) {
                callBack.onResponse(hash);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private boolean transfer(String privateKey, String from, String to, String token, String issuer, String value, String fee, String memo) {
        try {
            AmountInfo amount;
            amount = new AmountInfo();
            if (TextUtils.equals(WConstant.CURRENCY_SWTC, token)) {
                amount.setCurrency(WConstant.CURRENCY_SWT);
                amount.setIssuer("");
            } else if (TextUtils.equals(WConstant.CURRENCY_CNT, token) && TextUtils.equals(WConstant.CURRENCY_ISSUE, issuer)) {
                amount.setCurrency(WConstant.CURRENCY_CNY);
                amount.setIssuer(issuer);
            } else {
                amount.setCurrency(token);
                amount.setIssuer(issuer);
            }
            amount.setValue(value);
            Transaction tx = JtServer.getInstance(mContext).getRemote().buildPaymentTx(from, to, amount);
            tx.setSecret(privateKey);
            List<String> memos = new ArrayList<String>();
            memos.add(memo);
            tx.addMemo(memos);
            if (!TextUtils.isEmpty(fee)) {
                tx.setFee(fee);
            }
            TransactionInfo bean = tx.submit();
            if (WConstant.RESULT_OK.equals(bean.getEngineResultCode())) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 转账
     *
     * @param privateKey
     * @param from
     * @param to
     * @param token
     * @param issuer
     * @param value
     * @param fee
     * @param memo
     * @param callBack
     */
    @Override
    public void transfer(String privateKey, String from, String to, String token, String issuer, String value, String fee, String memo, ICallBack callBack) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                emitter.onNext(transfer(privateKey, from, to, token, issuer, value, fee, memo));
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(Boolean aBoolean) {
                callBack.onResponse(aBoolean);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private AccountTx getTransferHistory(String address, Integer limit, Marker marker) {
        try {
            AccountTx bean = JtServer.getInstance(mContext).getRemote().requestAccountTx(address, limit, marker);
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取交易记录
     *
     * @param address
     * @param limit
     * @param marker
     * @param callBack
     */
    @Override
    public void getTransferHistory(String address, Integer limit, Marker marker, ICallBack callBack) {
        Observable.create(new ObservableOnSubscribe<AccountTx>() {
            @Override
            public void subscribe(ObservableEmitter<AccountTx> emitter) throws Exception {
                AccountTx accountTx = getTransferHistory(address, limit, marker);
                if (accountTx == null) {
                    accountTx = new AccountTx();
                }
                emitter.onNext(accountTx);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<AccountTx>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(AccountTx accountTx) {
                callBack.onResponse(accountTx);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private String getSWTBalance(String address) {
        try {
            // 获取账户信息
            AccountInfo info = JtServer.getInstance(mContext).getRemote().requestAccountInfo(address, null, null);
            if (info != null && info.getAccountData() != null) {
                return info.getAccountData().getBalance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.00";
    }

    /**
     * 获取SWT余额
     *
     * @param address
     * @param iCallBack
     */
    @Override
    public void getSWTBalance(String address, ICallBack iCallBack) {
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String balance = getSWTBalance(address);
                if (TextUtils.isEmpty(balance)) {
                    balance = "0.00";
                }
                emitter.onNext(balance);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(String balance) {
                iCallBack.onResponse(balance);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private AccountRelations getBalance(String address) {
        try {
            // 获取账户信息
            AccountInfo info = JtServer.getInstance(mContext).getRemote().requestAccountInfo(address, null, null);
            // 获取账户挂单信息
            AccountOffers accountOffers = JtServer.getInstance(mContext).getRemote().requestAccountOffers(address, null);
            // 获取账户其它token信息
            AccountRelations relationsTrust = JtServer.getInstance(mContext).getRemote().requestAccountRelations(address, null, WConstant.RELATION_TRUST);
            AccountRelations relationsFreeze = JtServer.getInstance(mContext).getRemote().requestAccountRelations(address, null, WConstant.RELATION_FREEZE);

            // 计算其它token冻结数量
            List<Offer> offers = accountOffers.getOffers();
            List<Line> linesT = relationsTrust.getLines();

            // 计算swt冻结数量
            Integer freezed = (relationsTrust.getLines().size() + accountOffers.getOffers().size()) * WConstant.FREEZED + WConstant.RESERVED;
            Line swtLine = new Line();
            String valid = CaclUtil.sub(info.getAccountData().getBalance(), String.valueOf(freezed));
            swtLine.setBalance(valid);
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

            // 根据挂单计算冻结数量
            for (int i = 0; i < offers.size(); i++) {
                String getsCurrency = offers.get(i).getTakerGets().getCurrency();
                for (int j = 0; j < linesT.size(); j++) {
                    Line line = linesT.get(j);
                    String currency = line.getCurrency();
                    if (TextUtils.equals(getsCurrency, currency)) {
                        String offerValue = offers.get(i).getTakerGets().getValue();
                        String tokenFreeze = CaclUtil.add(line.getLimit(), offerValue);
                        String balance = CaclUtil.sub(line.getBalance(), offerValue);
                        line.setBalance(balance);
                        line.setLimit(tokenFreeze);
                        break;
                    }
                }
            }

            // 根据冻结关系类型计算冻结数量
            List<Line> linesF = relationsFreeze.getLines();
            for (int i = 0; i < linesF.size(); i++) {
                String FCurrency = linesF.get(i).getCurrency();
                for (int j = 0; j < linesT.size(); j++) {
                    Line line = linesT.get(j);
                    String currency = line.getCurrency();
                    if (TextUtils.equals(FCurrency, currency)) {
                        String freeze = linesF.get(i).getLimit();
                        String tokenFreeze = CaclUtil.add(line.getLimit(), freeze);
                        String balance = CaclUtil.sub(line.getBalance(), freeze);
                        line.setBalance(balance);
                        line.setLimit(tokenFreeze);
                    }
                }
            }
            return relationsTrust;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取余额
     *
     * @param address
     * @param isDisposable //是否添加Disposable事件，添加后连续下一次WalletManager的调用会切断本次事件
     * @param callBack
     */
    @Override
    public void getBalance(String address, boolean isDisposable, ICallBack callBack) {
        Observable.create(new ObservableOnSubscribe<AccountRelations>() {
            @Override
            public void subscribe(ObservableEmitter<AccountRelations> emitter) throws Exception {
                AccountRelations accountRelations = getBalance(address);
                if (accountRelations == null) {
                    accountRelations = new AccountRelations();
                }
                emitter.onNext(accountRelations);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<AccountRelations>() {
            @Override
            public void onSubscribe(Disposable d) {
                if (isDisposable) {
                    compositeDisposable.add(d);
                }
            }

            @Override
            public void onNext(AccountRelations accountRelations) {
                callBack.onResponse(accountRelations);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * 获取token时价
     *
     * @param base
     * @param callback
     */
    @Override
    public void getTokenPrice(String base, JCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取infoHosts
                JccConfig jccConfig = JccConfig.getInstance();
                JccdexUrl jccdexUrl = new JccdexUrl(CONFIG_HOST, true);
                jccConfig.setmBaseUrl(jccdexUrl);
                jccConfig.requestConfig(new JCallback() {
                    @Override
                    public void onResponse(String code, String response) {
                        if (!TextUtils.isEmpty(response)) {
                            GsonUtil res = new GsonUtil(response);
                            GsonUtil infoHosts = res.getArray("infoHosts");
                            int index = (int) (Math.random() * infoHosts.getLength());
                            final JccdexUrl jccUrl = new JccdexUrl(infoHosts.getString(index, ""), true);
                            JccdexInfo jccdexInfo = JccdexInfo.getInstance();
                            jccdexInfo.setmBaseUrl(jccUrl);
                            // 获取时价
                            if (TextUtils.equals(WConstant.CURRENCY_SWTC, base)) {
                                jccdexInfo.requestTicker(WConstant.CURRENCY_SWT, COUNTER, callback);
                            } else {
                                jccdexInfo.requestTicker(base, COUNTER, callback);
                            }
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }).start();
    }

    /**
     * 获取所有的token时价
     *
     * @param jCallback
     */
    @Override
    public void getAllTokenPrice(JCallback jCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取infoHosts
                JccConfig jccConfig = JccConfig.getInstance();
                JccdexUrl jccdexUrl = new JccdexUrl(CONFIG_HOST, true);
                jccConfig.setmBaseUrl(jccdexUrl);
                jccConfig.requestConfig(new JCallback() {
                    @Override
                    public void onResponse(String code, String response) {
                        if (!TextUtils.isEmpty(response)) {
                            GsonUtil res = new GsonUtil(response);
                            GsonUtil infoHosts = res.getArray("infoHosts");
                            int index = (int) (Math.random() * infoHosts.getLength());
                            final JccdexUrl jccUrl = new JccdexUrl(infoHosts.getString(index, ""), true);
                            JccdexInfo jccdexInfo = JccdexInfo.getInstance();
                            jccdexInfo.setmBaseUrl(jccUrl);
                            // 获取时价
                            jccdexInfo.requestAllTickers(jCallback);
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }).start();
    }

    /**
     * 获取所有tokens
     */
    @Override
    public void getAllTokens() {
        GetAllTokenList request = new GetAllTokenList();
        new RequestPresenter().loadData(request, false, new RequestPresenter.RequestCallback() {
            @Override
            public void onRequesResult(int ret, GsonUtil json) {
                String code = json.getString("code", "");
                if (TextUtils.equals(code, "0")) {
                    GsonUtil data = json.getArray("data");
                    Map<String, Currency> tokenMap = new TreeMap<>();
                    for (int i = 0; i < data.getLength(); i++) {
                        GsonUtil tokenPair = data.getObject(i);
                        List<String> keys = tokenPair.getKey();
                        GsonUtil tokens = tokenPair.getArray(keys.get(0));
                        for (int j = 0; j < tokens.getLength(); j++) {
                            Currency currency = new Currency();
                            String token = tokens.getString(j, "");
                            String[] tokenArr = token.split("_");
                            if (tokenArr.length == 2) {
                                currency.setName(tokenArr[0]);
                                currency.setIssue(tokenArr[1]);
                                tokenMap.put(token, currency);
                            } else if (tokenArr.length == 1) {
                                currency.setName(tokenArr[0]);
                                currency.setIssue("");
                                tokenMap.put(token, currency);
                            }
                        }
                    }
                    String JStr = JSON.toJSONString(tokenMap);
                    // 本地保存tokens
                    String fileName = mContext.getPackageName() + "_tokens";
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("tokens", JStr);
                    editor.apply();
                }
            }
        });
    }
}
