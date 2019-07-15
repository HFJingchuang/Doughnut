package com.doughnut;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.widget.TextView;

import com.blink.jtblc.client.bean.AccountTx;
import com.doughnut.wallet.WalletManager;
import com.doughnut.wallet.WalletSp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

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
        WalletManager.getInstance(appContext).createWallet("123456");
        String address = WalletSp.getInstance(appContext).getAddress();
        String keyStore = WalletSp.getInstance(appContext).getKeyStore();
        Assert.assertNotNull(address);
        Assert.assertNotNull(keyStore);
        Log.v(TAG, address);
        Log.v(TAG, keyStore);
    }

    @Test
    public void importWalletWithKeyTest() throws Exception {
        String address = "j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe";
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey);
        String keyStore = WalletSp.getInstance(appContext).getKeyStore();
        Assert.assertEquals("j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe", address);
        Assert.assertNotNull(keyStore);
        Log.v(TAG, keyStore);
    }

    @Test
    public void exportWalletWithQRTest() throws Exception {
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey);
        Bitmap qrBitmap = WalletManager.getInstance(appContext).exportWalletWithQR(500, Color.BLACK);
        Assert.assertNotNull(qrBitmap);
    }

    @Test
    public void importQRImageTest() throws Exception {
        // 生成二维码
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey);
        Bitmap qrBitmap = WalletManager.getInstance(appContext).exportWalletWithQR(500, Color.BLACK);

        // 清楚本地钱包地址和keyStore
        String fileName = appContext.getPackageName() + "_" + "wallet";
        SharedPreferences sharedPreferences = appContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.remove("address");
        edit.remove("keyStore");
        edit.apply();
        Assert.assertNull(WalletSp.getInstance(appContext).getAddress());
        Assert.assertNull(WalletSp.getInstance(appContext).getKeyStore());

        // 导入二维码
        WalletManager.getInstance(appContext).importQRImage(qrBitmap);
        String address = WalletSp.getInstance(appContext).getAddress();
        String keyStore = WalletSp.getInstance(appContext).getKeyStore();
        Assert.assertEquals("j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe", address);
        Assert.assertNotNull(keyStore);
        Log.v(TAG, keyStore);
    }

    @Test
    public void getPrivateKeyTest() throws Exception {
        // 导入钱包
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey);
        String privateKey1 = WalletManager.getInstance(appContext).getPrivateKey("123456");
        Assert.assertEquals(privateKey1, privateKey);
    }

    @Test
    public void transferTest() throws Exception {
        // 导入钱包
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey);
        WalletManager.getInstance(appContext).transfer("123456", "jNn89aY84G23onFXupUd7bkMode6aKYMt8", new BigDecimal("0.01"), "钱包工具类：转账单元测试");
    }

    @Test
    public void getTansferHishoryTest() throws Exception {
        // 导入钱包
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey);
        AccountTx bean = WalletManager.getInstance(appContext).getTansferHishory(new Integer("20"));
        Log.v(TAG, bean.getTransactions().get(0).getHash());
    }

}
