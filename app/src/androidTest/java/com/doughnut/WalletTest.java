package com.doughnut;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.android.jtblk.client.bean.AccountRelations;
import com.android.jtblk.client.bean.AccountTx;
import com.doughnut.wallet.ICallBack;
import com.doughnut.wallet.JtServer;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class WalletTest {

    private static final String TAG = "WalletTest";
    private static Context appContext = InstrumentationRegistry.getTargetContext();

    @Before
    public void changeServer() throws Exception {
        JtServer.getInstance(appContext).changeServer("ws://ts5.jingtum.com:5020");
//        JtServer.getInstance(appContext).changeServer("ws://s.jingtum.com:5020");
    }

    @Test
    public void createWalletTest() throws Exception {

        String addr = WalletManager.getInstance(appContext).createWallet("123456", "测试");
        Assert.assertNotNull(addr);
    }

    @Test
    public void deleteWalletTest() throws Exception {
        WalletManager.getInstance(appContext).deleteWallet("j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe");
        List<String> wallets = WalletSp.getInstance(appContext, "").getAllWallet();
        String addr = WalletSp.getInstance(appContext, "").getCurrentWallet();
        boolean isHas = wallets.contains("j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe");
        Assert.assertFalse(isHas);
        Assert.assertNotNull(addr);
        Assert.assertNotEquals("j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe", addr);
    }

    @Test
    public void importWalletWithKeyTest() throws Exception {
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        boolean res = WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test");
        Assert.assertEquals(true, res);
    }

    @Test
    public void exportWalletWithQRTest() throws Exception {
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        String address = "j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe";
        WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test");
        Bitmap qrBitmap = WalletManager.getInstance(appContext).exportWalletWithQR(address, 500, Color.BLACK);
        Assert.assertNotNull(qrBitmap);
    }

    @Test
    public void getPrivateKeyTest() throws Exception {
        // 导入钱包
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        String address = "j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe";
        WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test");
        String privateKey1 = WalletManager.getInstance(appContext).getPrivateKey("123456", address);
        Assert.assertEquals(privateKey1, privateKey);
    }

    @Test
    public void transferTest() throws Exception {
        final CountDownLatch mutex = new CountDownLatch(1);
        // 导入钱包
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        String address = "j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe";
        final boolean[] result = new boolean[1];
        WalletManager.getInstance(appContext).transfer(privateKey, address, "jNn89aY84G23onFXupUd7bkMode6aKYMt8", "SWT", "", "0.01", "", "钱包工具类：转账单元测试", new ICallBack() {
            @Override
            public void onResponse(Object response) {
                result[0] = (boolean) response;
                mutex.countDown();
            }
        });
        mutex.await();
        Assert.assertEquals(true, result[0]);
    }

    @Test
    public void getTansferHishoryTest() throws Exception {
        final CountDownLatch mutex = new CountDownLatch(1);
        // 导入钱包
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        String address = "j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe";
        final AccountTx[] bean = new AccountTx[1];
        WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test");
        WalletManager.getInstance(appContext).getTransferHistory(address, new Integer("10"), null, new ICallBack() {
            @Override
            public void onResponse(Object response) {
                bean[0] = (AccountTx) response;
                mutex.countDown();
            }
        });

        mutex.await();
        Assert.assertEquals(10, bean[0].getTransactions().size());
    }

    @Test
    public void getSWTBalanceTest() throws Exception {
        final CountDownLatch mutex = new CountDownLatch(1);
        // 导入钱包
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        String address = "j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe";
        final String[] balance = new String[1];
        WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test");
        WalletManager.getInstance(appContext).getSWTBalance(address, new ICallBack() {
            @Override
            public void onResponse(Object response) {
                balance[0] = (String) response;
                mutex.countDown();
            }
        });
        mutex.await();
        Assert.assertNotEquals("", balance[0]);
    }

    @Test
    public void getBalanceTest() throws Exception {
        final CountDownLatch mutex = new CountDownLatch(1);
        // 导入钱包
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        String address = "j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe";
        final AccountRelations[] accountRelations = new AccountRelations[1];
        WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test");
        WalletManager.getInstance(appContext).getBalance(address, new ICallBack() {
            @Override
            public void onResponse(Object response) {
                accountRelations[0] = (AccountRelations) response;
                mutex.countDown();
            }
        });
        mutex.await();
        Assert.assertEquals("j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe", accountRelations[0].getAccount());
    }
}
