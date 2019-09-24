package com.doughnut;

import android.support.test.runner.AndroidJUnit4;

import com.doughnut.utils.PWDUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PWDUtilTest {

    @Test
    public void normalTest() throws Exception {
        boolean res = PWDUtils.verifyPasswordFormat(" ");
        Assert.assertEquals(false, res);

        res = PWDUtils.verifyPasswordFormat("We11111");
        Assert.assertEquals(false, res);

        res = PWDUtils.verifyPasswordFormat("11111111");
        Assert.assertEquals(false, res);

        res = PWDUtils.verifyPasswordFormat("eeeeeeee");
        Assert.assertEquals(false, res);

        res = PWDUtils.verifyPasswordFormat("EEEEEEEE");
        Assert.assertEquals(false, res);

        res = PWDUtils.verifyPasswordFormat("WEEEEEEE");
        Assert.assertEquals(false, res);

        res = PWDUtils.verifyPasswordFormat("123WEwww");
        Assert.assertEquals(true, res);

        res = PWDUtils.verifyPasswordFormat("12345678123456781234567812345678123456781234567812345678123456We");
        Assert.assertEquals(true, res);

        res = PWDUtils.verifyPasswordFormat("12345678123456781234567812345678123456781234567812345678123456We1");
        Assert.assertEquals(false, res);
    }

}
