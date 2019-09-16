package com.doughnut;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.android.jtblk.client.bean.AccountRelations;
import com.android.jtblk.client.bean.AccountTx;
import com.doughnut.wallet.JtServer;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

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
        // 导入钱包
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        String address = "j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe";
        boolean res = WalletManager.getInstance(appContext).transfer(privateKey, address, "jNn89aY84G23onFXupUd7bkMode6aKYMt8", "SWT", "", "0.01", "", "钱包工具类：转账单元测试");
        Assert.assertEquals(true, res);
    }

    @Test
    public void getTansferHishoryTest() throws Exception {
        // 导入钱包
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        String address = "j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe";
        WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test");
        AccountTx bean = WalletManager.getInstance(appContext).getTransferHistory(address, new Integer("10"), null);
        Assert.assertEquals(10, bean.getTransactions().size());
    }

    @Test
    public void getBalanceTest() throws Exception {
        // 导入钱包
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        String address = "j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe";
        WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test");
        AccountRelations accountRelations = WalletManager.getInstance(appContext).getBalance(address);
        Assert.assertEquals("j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe", accountRelations.getAccount());
    }
}
