package com.doughnut;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.android.jtblk.client.bean.AccountTx;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
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
        String addr = WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test");
        Assert.assertEquals("j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe", addr);
    }

    @Test
    public void exportWalletWithQRTest() throws Exception {
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        String addr = WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test");
        Bitmap qrBitmap = WalletManager.getInstance(appContext).exportWalletWithQR(addr, 500, Color.BLACK);
        Assert.assertNotNull(qrBitmap);
    }

    @Test
    public void importQRImageTest() throws Exception {
        // 生成二维码
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        String addr = WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test");
        Bitmap qrBitmap = WalletManager.getInstance(appContext).exportWalletWithQR(addr, 500, Color.BLACK);

        // 清楚本地钱包地址和keyStore
        WalletSp.getInstance(appContext, addr).delete();
        Assert.assertNull(WalletSp.getInstance(appContext, addr).getAddress());
        Assert.assertNull(WalletSp.getInstance(appContext, addr).getKeyStore());

        // 导入二维码
        String addr1 = WalletManager.getInstance(appContext).importQRImage(qrBitmap, "test");
        Assert.assertEquals("j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe", addr);
    }

    @Test
    public void getPrivateKeyTest() throws Exception {
        // 导入钱包
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        String addr = WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test");
        String privateKey1 = WalletManager.getInstance(appContext).getPrivateKey("123456", addr);
        Assert.assertEquals(privateKey1, privateKey);
    }

    @Test
    public void transferTest() throws Exception {
        // 导入钱包
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        String addr = WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test");
        String res = WalletManager.getInstance(appContext).transfer("123456", addr, "jNn89aY84G23onFXupUd7bkMode6aKYMt8", new BigDecimal("0.01"), "钱包工具类：转账单元测试");
        Assert.assertNotNull(res);
    }

    @Test
    public void getTansferHishoryTest() throws Exception {
        // 导入钱包
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        String addr = WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test");
        AccountTx bean = WalletManager.getInstance(appContext).getTansferHishory(addr, new Integer("20"));
        Assert.assertEquals(bean.getTransactions().size(), 20);
    }

    @Test
    public void getBalanceTest() throws Exception {
        // 导入钱包
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        String addr = WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test");
        String balance = WalletManager.getInstance(appContext).getBalance(addr);
        Log.v(TAG, balance);
        Assert.assertNotNull(balance);
    }
}
