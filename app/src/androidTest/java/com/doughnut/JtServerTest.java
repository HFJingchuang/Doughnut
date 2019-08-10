package com.doughnut;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.android.jtblk.client.bean.AccountRelations;
import com.doughnut.wallet.JtServer;
import com.doughnut.wallet.WalletManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class JtServerTest {

    private static final String TAG = "WalletTest";
    private static Context appContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void changeServerTest() throws Exception {
        // 导入钱包
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        String addr = WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test");
        AccountRelations balance = WalletManager.getInstance(appContext).getBalance(addr);
        Assert.assertEquals(true, JtServer.getInstance().getRemote().getLocalSign());

        JtServer.getInstance().changeServer("wss://s.jingtum.com:5020", false);
        AccountRelations balance1 = WalletManager.getInstance(appContext).getBalance(addr);
        Assert.assertEquals(false, JtServer.getInstance().getRemote().getLocalSign());
        Assert.assertNotEquals(balance.getLines().size(), balance1.getLines().size());
    }

}
