package com.doughnut.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import com.doughnut.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Util {

    private final static String TAG = "Util";

    public static void clipboard(Context context, CharSequence label, CharSequence text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText(label, text));
    }

    public static String formatTime(long time) {
        if (time <= 0) {
            return "";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date date = new Date(time * 1000);
            String str = sdf.format(date);
            return str;
        }
    }

    //TODO 要转成科学计数法
    public static double getValueByWeight(String valueinWeight) {
        try {
            double value = Double.parseDouble(valueinWeight) / 1000000000000000000.0f;
            BigDecimal bg = new BigDecimal(value);
            double result = bg.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
            return result;
        } catch (Exception e) {
            return 0.0f;
        }
    }

    //将wei转成token value, 保存len位小数， 返回double
    public static double formatDouble(long blockChain, int len, double wei) {
        try {
            double value = fromWei(blockChain, wei);
            BigDecimal bg = new BigDecimal(value);
            double result = bg.setScale(len, BigDecimal.ROUND_HALF_UP).doubleValue();
            return result;
        } catch (Exception e) {
            return 0.0f;
        }
    }

    public static double formatDouble(int len, double value) {
        try {
            BigDecimal bg = new BigDecimal(value);
            double result = bg.setScale(len, BigDecimal.ROUND_HALF_UP).doubleValue();
            return result;
        } catch (Exception e) {
            return 0.0f;
        }
    }

    //将wei转成token value, 保存len位小数， 返回string
    public static String formatDoubleToStr(int len, double value) {
        return String.format("%." + len + "f", value).toString();
    }

    public static double strToDouble(String str) {
        try {
            double result = Double.parseDouble(str);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }


    //单位为gwei
    public static double getMinGweiGas(long blockChain, boolean defaultToken) {
        if (blockChain == 1) {
            if (defaultToken) {

            }
            return 32000;
        }
        return 0;
    }

    //单位为gwei
    public static double getMaxGweiGas(long blockChain, boolean defaultToken) {
        if (blockChain == 1) {
            if (defaultToken) {
                return 30000;
            } else {
                return 60000;
            }
        }
        return 0;
    }

    //单位为gwei
    public static double getRecommendGweiGas(long blockChain, boolean defaultToken) {
        if (blockChain == 1) {
            if (defaultToken) {
                return 25200;
            } else {
                return 60000;
            }
        }
        return 0;
    }

    public static String getSymbolByBlockChain(long blockChain) {
        if (blockChain == 1) {
            return "ETH";
        } else if (blockChain == 2) {
            return "SWT";
        }
        return "";
    }

    //wei to token value
    public static double fromWei(long blockChain, double wei) {
        if (blockChain == 1) {
            if (wei <= 0) {
                return 0;
            }
            return wei / 1000000000000000000.0f;
        }
        return 0.0f;
    }

    //wei string to tokenvalue
    public static double fromWei(long blockChain, String wei) {
        try {
            double dwei = Double.parseDouble(wei);
            return fromWei(blockChain, dwei);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    public static double translateValue(int decimal, double value) {
        double divider = 1.0f;
        for (int i = 0; i < decimal; i++) {
            divider *= 10;
        }
        return value / divider;
    }

    //tokenvalue to wei
    public static double toWei(long blockChain, double tokenvalue) {
        if (blockChain == 1) {
            if (tokenvalue <= 0) {
                return 0;
            }
            return tokenvalue * 1000000000000000000.0f;
        }
        return 0.0f;
    }

    public static double tokenToWei(long blockChain, double tokenValue, int dec) {
        if (blockChain == 1) {
            String decimal = "1";
            for (int i = 0; i < dec; i++) {
                decimal = decimal + "0";
            }
            TLog.e(TAG, "decimal:" + decimal);
            return tokenValue * parseDouble(decimal);
        }
        return 0.0f;
    }

    public static double fromGweToWei(long blockChain, double gwei) {
        if (blockChain == 1) {
            if (gwei <= 0) {
                return 0.0f;
            }
            return gwei * 1000000000.0f;
        }
        return 0.0f;
    }

    public static double fromWeiToGwei(long blockChain, double wei) {
        if (blockChain == 1) {
            if (wei <= 0) {
                return 0.0f;
            }
            return wei / 1000000000.0f;
        }
        return 0.0f;
    }


    public static double parseDouble(String doubStr) {
        if (TextUtils.isEmpty(doubStr)) {
            return 0.0f;
        }
        try {
            return Double.parseDouble(doubStr);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    /**
     * 数字每隔三位加个逗号
     *
     * @param amunot
     * @return
     */
    public static String formatWithComma(long amunot) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(amunot);
    }

    /**
     * 验证输入数目格式
     */
    public static boolean verifyAmount(String amount) {
        String trim = amount.replaceAll("0|\\.", "");
        if (!TextUtils.isEmpty(trim)) {
            String regex = "^([1-9][\\d]{0,7}|0)(\\.[\\d]{1,2})?$";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(amount);
            return m.matches();
        }
        return false;
    }

    /**
     * 格式化数字，截取小数位，以便于画面显示
     *
     * @param amountStr
     * @param scale
     * @return
     */
    public static String formatAmount(String amountStr, int scale) {
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            BigDecimal amountF = amount.setScale(scale, BigDecimal.ROUND_DOWN);
            if (amountF.compareTo(BigDecimal.ZERO) != 0) {
                return amountF.stripTrailingZeros().toPlainString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return amountStr;
    }

    /**
     * 判断字符串是不是以数字开头
     */
    public static boolean isStartWithNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str.charAt(0) + "");
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 匹配币种图标
     *
     * @param token
     * @return
     */
    public static int getTokenIcon(String token) {
        int icon;
        switch (token) {
            case "BGT":
                icon = R.drawable.bgt;
                break;
            case "BIC":
                icon = R.drawable.bic;
                break;
            case "JBIZ":
                icon = R.drawable.biz;
                break;
            case "JBTC":
                icon = R.drawable.btc;
                break;
            case "JCALL":
                icon = R.drawable.call;
                break;
            case "JCKM":
                icon = R.drawable.ckm;
                break;
            case "CNY":
                icon = R.drawable.cnt;
                break;
            case "CSP":
                icon = R.drawable.cspc;
                break;
            case "JDABT":
                icon = R.drawable.dabt;
                break;
            case "ECP":
                icon = R.drawable.ecp;
                break;
            case "JEKT":
                icon = R.drawable.ekt;
                break;
            case "JEOS":
                icon = R.drawable.eos;
            case "JETH":
                icon = R.drawable.eth;
                break;
            case "JFST":
                icon = R.drawable.fst;
                break;
            case "KGALAXY":
                icon = R.drawable.galaxy;
                break;
            case "GDC":
                icon = R.drawable.gdc;
                break;
            case "JGSGC":
                icon = R.drawable.gsgc;
                break;
            case "HJT":
                icon = R.drawable.hjt;
                break;
            case "HNT":
                icon = R.drawable.hnt;
                break;
            case "HPT":
                icon = R.drawable.hpt;
                break;
            case "JHT":
                icon = R.drawable.ht;
                break;
            case "JJCC":
                icon = R.drawable.jcc;
                break;
            case "JMOAC":
                icon = R.drawable.moac;
                break;
            case "JMONA":
                icon = R.drawable.mona;
                break;
            case "MYT":
                icon = R.drawable.myt;
                break;
            case "SEAA":
                icon = R.drawable.seaa;
                break;
            case "JSLASH":
                icon = R.drawable.slash;
                break;
            case "JSNRC":
                icon = R.drawable.snrc;
                break;
            case "JSTM":
                icon = R.drawable.stm;
                break;
            case "SWT":
            case "SWTC":
                icon = R.drawable.swtc;
                break;
            case "JUSDT":
                icon = R.drawable.usdt;
                break;
            case "UST":
                icon = R.drawable.ust;
                break;
            case "VCC":
                icon = R.drawable.vcc;
                break;
            case "JXRP":
                icon = R.drawable.xrp;
                break;
            case "YUT":
                icon = R.drawable.yut;
                break;
            default:
                icon = R.drawable.icon_default;
        }
        return icon;
    }
}
