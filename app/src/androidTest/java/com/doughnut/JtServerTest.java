package com.doughnut;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.android.jtblk.client.bean.AccountRelations;
import com.doughnut.wallet.ICallBack;
import com.doughnut.wallet.JtServer;
import com.doughnut.wallet.WalletManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

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
        final CountDownLatch mutex = new CountDownLatch(1);
        // 导入钱包
        String privateKey = "ssWiEpky7Bgj5GFrexxpKexYkeuUv";
        String address = "j3UcBBbes7HFgmTLmGkEQQShM2jdHbdGAe";
        final AccountRelations[] accountRelations1 = new AccountRelations[1];
        JtServer.getInstance(appContext).changeServer("wss://s.jingtum.com:5020", true);
        WalletManager.getInstance(appContext).importWalletWithKey("123456", privateKey, "test", new ICallBack() {
            @Override
            public void onResponse(Object response) {
                mutex.countDown();
            }
        });
        mutex.await();

        final CountDownLatch mutex1 = new CountDownLatch(1);
        WalletManager.getInstance(appContext).getBalance(address, new ICallBack() {
            @Override
            public void onResponse(Object response) {
                accountRelations1[0] = (AccountRelations) response;
                mutex1.countDown();
            }
        });
        mutex1.await();

        Assert.assertEquals(true, JtServer.getInstance(appContext).getRemote().getLocalSign());
        final CountDownLatch mutex2 = new CountDownLatch(1);
        final AccountRelations[] accountRelations2 = new AccountRelations[1];
        JtServer.getInstance(appContext).changeServer("ws://ts5.jingtum.com:5020", false);
        WalletManager.getInstance(appContext).getBalance(address, new ICallBack() {
            @Override
            public void onResponse(Object response) {
                accountRelations2[0] = (AccountRelations) response;
                mutex2.countDown();
            }
        });
        mutex2.await();
        Assert.assertEquals(false, JtServer.getInstance(appContext).getRemote().getLocalSign());
        Assert.assertNotEquals(accountRelations1[0].getLines().size(), accountRelations2[0].getLines().size());
    }

}
