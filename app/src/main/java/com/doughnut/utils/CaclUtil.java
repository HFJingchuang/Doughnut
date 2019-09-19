package com.doughnut.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 精确计算工具类
 */
public class CaclUtil {

    /**
     * 提供精确的加法运算
     *
     * @param v1
     * @param v2
     * @param scale
     * @return
     */
    public static String add(String v1, String v2, int scale) {
        try {
            if (!TextUtils.isEmpty(v1) && !TextUtils.isEmpty(v2)) {
                BigDecimal b1 = new BigDecimal(v1);
                BigDecimal b2 = new BigDecimal(v2);
                return b1.add(b2).setScale(scale, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
            } else if (!TextUtils.isEmpty(v1)) {
                return new BigDecimal(v1).setScale(scale, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
            } else if (!TextUtils.isEmpty(v2)) {
                return new BigDecimal(v2).setScale(scale, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.00";
    }

    public static String add(String v1, String v2) {
        try {
            if (!TextUtils.isEmpty(v1) && !TextUtils.isEmpty(v2)) {
                BigDecimal b1 = new BigDecimal(v1);
                BigDecimal b2 = new BigDecimal(v2);
                return b1.add(b2).stripTrailingZeros().toPlainString();
            } else if (!TextUtils.isEmpty(v1)) {
                return new BigDecimal(v1).stripTrailingZeros().toPlainString();
            } else if (!TextUtils.isEmpty(v2)) {
                return new BigDecimal(v2).stripTrailingZeros().toPlainString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.00";
    }

    /**
     * 提供精确的减法运算
     *
     * @param v1
     * @param v2
     * @param scale
     * @return
     */
    public static String sub(String v1, String v2, int scale) {
        try {
            if (!TextUtils.isEmpty(v1) && !TextUtils.isEmpty(v2)) {
                BigDecimal b1 = new BigDecimal(v1);
                BigDecimal b2 = new BigDecimal(v2);
                return b1.subtract(b2).setScale(scale, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
            } else if (!TextUtils.isEmpty(v1)) {
                return new BigDecimal(v1).setScale(scale, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
            } else if (!TextUtils.isEmpty(v2)) {
                return new BigDecimal(v2).setScale(scale, RoundingMode.DOWN).negate().stripTrailingZeros().toPlainString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.00";
    }

    /**
     * 注意数值过大返回0
     *
     * @param v1
     * @param v2
     * @return
     */
    public static String sub(String v1, String v2) {
        try {
            if (!TextUtils.isEmpty(v1) && !TextUtils.isEmpty(v2)) {
                BigDecimal b1 = new BigDecimal(v1);
                BigDecimal b2 = new BigDecimal(v2);
                return b1.subtract(b2).stripTrailingZeros().toPlainString();
            } else if (!TextUtils.isEmpty(v1)) {
                return new BigDecimal(v1).stripTrailingZeros().toPlainString();
            } else if (!TextUtils.isEmpty(v2)) {
                return new BigDecimal(v2).negate().stripTrailingZeros().toPlainString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.00";
    }

    /**
     * 提供精确的乘法运算
     *
     * @param v1
     * @param v2
     * @param scale
     * @return
     */
    public static String mul(String v1, String v2, int scale) {
        try {
            if (!TextUtils.isEmpty(v1) && !TextUtils.isEmpty(v2)) {
                BigDecimal b1 = new BigDecimal(v1);
                BigDecimal b2 = new BigDecimal(v2);
                return b1.multiply(b2).setScale(scale, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.00";
    }

    public static String mul(String v1, String v2) {
        try {
            if (!TextUtils.isEmpty(v1) && !TextUtils.isEmpty(v2)) {
                BigDecimal b1 = new BigDecimal(v1);
                BigDecimal b2 = new BigDecimal(v2);
                return b1.multiply(b2).stripTrailingZeros().toPlainString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.00";
    }

    /**
     * 提供精确的除法运算。
     *
     * @param v1
     * @param v2
     * @param scale
     * @return
     */
    public static String div(String v1, String v2, int scale) {
        try {
            if (!TextUtils.isEmpty(v1) && !TextUtils.isEmpty(v2)) {
                BigDecimal b1 = new BigDecimal(v1);
                BigDecimal b2 = new BigDecimal(v2);
                return b1.divide(b2, scale, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.00";
    }

    /**
     * @param v1
     * @param v2
     * @return
     */
    public static String div(String v1, String v2) {
        try {
            if (!TextUtils.isEmpty(v1) && !TextUtils.isEmpty(v2)) {
                BigDecimal b1 = new BigDecimal(v1);
                BigDecimal b2 = new BigDecimal(v2);
                return b1.divide(b2, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.00";
    }

    /**
     * 格式化数字，截取小数位，以便于画面显示(不为零的值截取后为零时默认重新截取4位小数并返回)
     *
     * @param amountStr
     * @param scale
     * @return
     */
    public static String formatAmount(String amountStr, int scale) {
        try {
            if (!TextUtils.isEmpty(amountStr)) {
                BigDecimal amount = new BigDecimal(amountStr);
                BigDecimal amountF = amount.setScale(scale, RoundingMode.DOWN);
                if (amountF.compareTo(BigDecimal.ZERO) == 0) {
                    if (amount.compareTo(BigDecimal.ZERO) != 0) {
                        return amount.setScale(4, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
                    } else {
                        return "0.00";
                    }
                } else {
                    return amountF.stripTrailingZeros().toPlainString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return amountStr;
    }

    /**
     * 比较大小
     *
     * @param v1 被比较数
     * @param v2 比较数
     * @return 如果v1 大于v2 则 返回true 否则false
     */
    public static int compare(String v1, String v2) {
        try {
            if (TextUtils.isEmpty(v1) && TextUtils.isEmpty(v2)) {
                return 0;
            } else if (TextUtils.isEmpty(v1) && !TextUtils.isEmpty(v2)) {
                return -1;
            } else if (!TextUtils.isEmpty(v1) && TextUtils.isEmpty(v2)) {
                return 1;
            } else {
                BigDecimal b1 = new BigDecimal(v1);
                BigDecimal b2 = new BigDecimal(v2);
                return b1.compareTo(b2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
