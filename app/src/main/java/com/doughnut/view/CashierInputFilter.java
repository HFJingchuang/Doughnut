package com.doughnut.view;


import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

public class CashierInputFilter implements InputFilter {

    private static final String POINTER = ".";

    private static final String ZERO = "0";

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        String sourceText = source.toString();
        String destText = dest.toString();

        //首位不能是小数点
        if (TextUtils.isEmpty(dest) && TextUtils.equals(source, POINTER)) {
            return "";
        }

        //首位是0，后位只能是小数点
        if (TextUtils.equals(destText, ZERO) && !TextUtils.equals(sourceText, POINTER)) {
            return "";
        }

        //小数点后只能是数字,且只能又一个小数点
        if (destText.contains(POINTER) && TextUtils.equals(sourceText, POINTER)) {
            return "";
        }

        return null;
    }
}