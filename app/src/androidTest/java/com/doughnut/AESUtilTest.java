package com.doughnut;

import android.support.test.runner.AndroidJUnit4;

import com.doughnut.utils.AESUtil;
import com.doughnut.utils.CaclUtil;
import com.doughnut.utils.Util;
import com.doughnut.wallet.ICallBack;

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
public class AESUtilTest {

    private String content = "Test123456";

    @Test
    public void normalTest() throws Exception {
        String key = AESUtil.generateKey();
        final CountDownLatch mutex = new CountDownLatch(1);
        final String[] encrypt = new String[1];
        AESUtil.encrypt(key, content, new ICallBack() {
            @Override
            public void onResponse(Object response) {
                encrypt[0] = (String) response;
                mutex.countDown();
            }
        });
        mutex.await();
        final CountDownLatch mutex1 = new CountDownLatch(1);
        final String[] decrypt = new String[1];
        AESUtil.decrypt(key, encrypt[0], new ICallBack() {
            @Override
            public void onResponse(Object response) {
                decrypt[0] = (String) response;
                mutex1.countDown();
            }
        });
        mutex1.await();
        Assert.assertEquals(decrypt[0], content);
    }

    @Test
    public void normal1Test() throws Exception {
        String key = AESUtil.generateKey();
        final CountDownLatch mutex = new CountDownLatch(1);
        final String[] encrypt = new String[1];
        AESUtil.encrypt(key, " ", new ICallBack() {
            @Override
            public void onResponse(Object response) {
                encrypt[0] = (String) response;
                mutex.countDown();
            }
        });
        mutex.await();
        final CountDownLatch mutex1 = new CountDownLatch(1);
        final String[] decrypt = new String[1];
        AESUtil.decrypt(key, encrypt[0], new ICallBack() {
            @Override
            public void onResponse(Object response) {
                decrypt[0] = (String) response;
                mutex1.countDown();
            }
        });
        mutex1.await();
        Assert.assertEquals(decrypt[0], " ");
    }
}
