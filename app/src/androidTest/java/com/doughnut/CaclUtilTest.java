package com.doughnut;

import android.support.test.runner.AndroidJUnit4;

import com.doughnut.utils.CaclUtil;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CaclUtilTest {

    private String normal = "1234567890.1234567890";
    private String normal1 = "1.23400000000000";
    private String space = "";
    private String err1 = "#@";
    private String err2 = "test";
    private String err3 = "12d";
    private String err4 = "c5";
    private String zero = "0.00";

    @Test
    public void normalTest() throws Exception {
        String add = CaclUtil.add(normal, normal);
        Assert.assertEquals("2469135780.246913578", add);
        String add1 = CaclUtil.add(normal, normal, 3);
        Assert.assertEquals("2469135780.246", add1);
        String sub = CaclUtil.sub(normal, normal1);
        Assert.assertEquals("1234567888.889456789", sub);
        String sub1 = CaclUtil.sub(normal, normal1, 3);
        Assert.assertEquals("1234567888.889", sub1);
        String mul = CaclUtil.mul(normal, normal);
        Assert.assertEquals("1524157875323883675.019051998750190521", mul);
        String mul1 = CaclUtil.mul(normal, normal, 3);
        Assert.assertEquals("1524157875323883675.019", mul1);
        String div = CaclUtil.div(normal, normal1);
        Assert.assertEquals("1000460202.6932388889", div);
        String div1 = CaclUtil.div(normal, normal1, 3);
        Assert.assertEquals("1000460202.693", div1);
    }

    @Test
    public void errAddTest() throws Exception {
        String add = CaclUtil.add(normal, err1);
        Assert.assertEquals(zero, add);
        String add1 = CaclUtil.add(normal, err2);
        Assert.assertEquals(zero, add1);
        String add2 = CaclUtil.add(normal, err3);
        Assert.assertEquals(zero, add2);
        String add3 = CaclUtil.add(normal, err4);
        Assert.assertEquals(zero, add3);
    }

    @Test
    public void errSubTest() throws Exception {
        String add = CaclUtil.sub(normal, err1);
        Assert.assertEquals(zero, add);
        String add1 = CaclUtil.sub(normal, err2);
        Assert.assertEquals(zero, add1);
        String add2 = CaclUtil.sub(normal, err3);
        Assert.assertEquals(zero, add2);
        String add3 = CaclUtil.sub(normal, err4);
        Assert.assertEquals(zero, add3);
    }

    @Test
    public void errMulTest() throws Exception {
        String add = CaclUtil.mul(normal, err1);
        Assert.assertEquals(zero, add);
        String add1 = CaclUtil.mul(normal, err2);
        Assert.assertEquals(zero, add1);
        String add2 = CaclUtil.mul(normal, err3);
        Assert.assertEquals(zero, add2);
        String add3 = CaclUtil.mul(normal, err4);
        Assert.assertEquals(zero, add3);
    }

    @Test
    public void errDivTest() throws Exception {
        String add = CaclUtil.div(normal, err1);
        Assert.assertEquals(zero, add);
        String add1 = CaclUtil.div(normal, err2);
        Assert.assertEquals(zero, add1);
        String add2 = CaclUtil.div(normal, err3);
        Assert.assertEquals(zero, add2);
        String add3 = CaclUtil.div(normal, err4);
        Assert.assertEquals(zero, add3);
    }

    @Test
    public void SpaceTest() throws Exception {
        String add = CaclUtil.add(normal, space);
        Assert.assertEquals("1234567890.123456789", add);
        String add1 = CaclUtil.add(space, normal);
        Assert.assertEquals("1234567890.123456789", add1);

        String sub = CaclUtil.sub(normal, space);
        Assert.assertEquals("1234567890.123456789", sub);
        String sub1 = CaclUtil.sub(space, normal);
        Assert.assertEquals("-1234567890.123456789", sub1);

        String mul = CaclUtil.mul(normal, space);
        Assert.assertEquals(zero, mul);
        String mul1 = CaclUtil.mul(space, normal);
        Assert.assertEquals(zero, mul1);

        String div = CaclUtil.div(normal, space);
        Assert.assertEquals(zero, div);
        String div1 = CaclUtil.div(space, normal);
        Assert.assertEquals(zero, div1);
    }

    @Test
    public void formatTest() throws Exception {
        String res = CaclUtil.formatAmount(normal, 2);
        Assert.assertEquals("1234567890.12", res);

        res = CaclUtil.formatAmount(normal, 20);
        Assert.assertEquals("1234567890.123456789", res);

        res = CaclUtil.formatAmount(space, 2);
        Assert.assertEquals(space, res);

        res = CaclUtil.formatAmount(err1, 2);
        Assert.assertEquals(res, err1);

        res = CaclUtil.formatAmount(err2, 2);
        Assert.assertEquals(res, err2);

        res = CaclUtil.formatAmount(err3, 2);
        Assert.assertEquals(res, err3);

        res = CaclUtil.formatAmount(err4, 2);
        Assert.assertEquals(res, err4);

        res = CaclUtil.formatAmount("0.00466000000000001", 2);
        Assert.assertEquals("0.0046", res);

        res = CaclUtil.formatAmount("0.0000000000000", 5);
        Assert.assertEquals("0.00", res);

        res = CaclUtil.formatAmount("", 2);
        Assert.assertEquals("", res);
    }

}
