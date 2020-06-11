package com.doughnut;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.chain3j.crypto.Credentials;
import org.chain3j.crypto.ECKeyPair;
import org.chain3j.crypto.Keys;
import org.chain3j.crypto.RawTransaction;
import org.chain3j.crypto.TransactionEncoder;
import org.chain3j.protocol.Chain3j;
import org.chain3j.protocol.http.HttpService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class McServerTest {

    private static final String TAG = "WalletTest";
    private static Context appContext = InstrumentationRegistry.getTargetContext();

    //    @Test
//    public void changeServerTest() throws Exception, CipherException {
////        System.out.println(WalletUtils.generateNewWalletFile("123456", new File("")));
////        McServer.getInstance(appContext).getRemote();
////        Log.v("www", Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath());
////        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
////
////        Log.v("www", Numeric.toHexStringWithPrefix(ecKeyPair.getPrivateKey()));
////        Log.v("www", Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey()));
////        WalletFile walletFile = Wallet.createLight("123456", ecKeyPair);
////        Log.v("www", walletFile.getAddress());
////        Log.v("www", walletFile.getCrypto().toString());
////        Log.v("www", walletFile.getId());
////        String wallet = WalletUtils.generateLightNewWalletFile("123", new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath()));
////        Log.v("www", wallet);
//        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigIntNoPrefix("da6f39ef485ce945be669a3bcb3a2863330f8ff0d783d3bed260b6af65911e3a"));
//        Log.v("www", Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey()));
//        RawTransaction rawTransaction = RawTransaction.createTransaction(new BigInteger("91"), new BigInteger("2000000"), Convert.toWei("0.00000002", Convert.Unit.ETHER).toBigInteger(), "0x8aacb5febf12546ca47fc5e9aa1bd1407378ad7a", "haha");
//        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Credentials.create(ecKeyPair));
//        String hexValue = Numeric.toHexString(signedMessage);
//        Log.v("www", hexValue);
//    }
    @Test
    public void changeServerTest() throws Exception {
//        System.out.println(WalletUtils.generateNewWalletFile("123456", new File("")));
//        McServer.getInstance(appContext).getRemote();
//        Log.v("www", Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath());
//        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
//
//        Log.v("www", Numeric.toHexStringWithPrefix(ecKeyPair.getPrivateKey()));
//        Log.v("www", Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey()));
//        WalletFile walletFile = Wallet.createLight("123456", ecKeyPair);
//        Log.v("www", walletFile.getAddress());
//        Log.v("www", walletFile.getCrypto().toString());
//        Log.v("www", walletFile.getId());
//        String wallet = WalletUtils.generateLightNewWalletFile("123", new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath()));
//        Log.v("www", wallet);
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigIntNoPrefix("86736091a441dffeb8656e731474433aaf531c4adab0497fa38d36215f44f18d"));
        Log.v("www", Keys.getAddress(ecKeyPair));
        RawTransaction rawTransaction = RawTransaction.createTransaction(new BigInteger("93"), new BigInteger("20000000000"), new BigInteger("2000000"), "0x8aacb5febf12546ca47fc5e9aa1bd1407378ad7a", new BigInteger("0"), "haha");
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, new Integer("99"), Credentials.create(ecKeyPair));
        String hexValue = Numeric.toHexString(signedMessage);
        Log.v("www", hexValue);
        Log.v("www", Chain3j.build(new HttpService("http://47.52.200.221:8545")).mcGasPrice().send().getGasPrice().toString());

    }

//    @Test
//    public void changeServerTest() throws Exception {
//        final CountDownLatch mutex = new CountDownLatch(1);
//        // 导入钱包
//        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
//        String address = "j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe";
//        final AccountRelations[] accountRelations1 = new AccountRelations[1];
//        JtServer.getInstance(appContext).changeServer("wss://s.jingtum.com:5020", true);
//        WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test", new ICallBack() {
//            @Override
//            public void onResponse(Object response) {
//                mutex.countDown();
//            }
//        });
//        mutex.await();
//
//        final CountDownLatch mutex1 = new CountDownLatch(1);
//        WalletManager.getInstance(appContext).getBalance(address, true, new ICallBack() {
//            @Override
//            public void onResponse(Object response) {
//                accountRelations1[0] = (AccountRelations) response;
//                mutex1.countDown();
//            }
//        });
//        mutex1.await();
//
//        Assert.assertEquals(true, JtServer.getInstance(appContext).getRemote().getLocalSign());
//        final CountDownLatch mutex2 = new CountDownLatch(1);
//        final AccountRelations[] accountRelations2 = new AccountRelations[1];
//        JtServer.getInstance(appContext).changeServer("ws://ts5.jingtum.com:5020", false);
//        WalletManager.getInstance(appContext).getBalance(address, true, new ICallBack() {
//            @Override
//            public void onResponse(Object response) {
//                accountRelations2[0] = (AccountRelations) response;
//                mutex2.countDown();
//            }
//        });
//        mutex2.await();
//        Assert.assertEquals(false, JtServer.getInstance(appContext).getRemote().getLocalSign());
//        Assert.assertNotEquals(accountRelations1[0].getLines().size(), accountRelations2[0].getLines().size());
//    }

}
