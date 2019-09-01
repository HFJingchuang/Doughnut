package com.doughnut.view;

public class SubCharSequence implements CharSequence {
    private CharSequence mSource;

    public SubCharSequence(CharSequence source) {
        mSource = source;
    }

    public char charAt(int index) {
        return '*';
    }

    public int length() {
        return mSource.length();
    }

    public CharSequence subSequence(int start, int end) {
        return mSource.subSequence(start, end);
    }
}
