package com.doughnut.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.dialog.MsgDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MnemonicVerFragment extends BaseFragment implements View.OnClickListener {

    private TextView mTvWord1, mTvWord2, mTvWord3, mTvWord4, mTvWord5, mTvWord6, mTvWord7, mTvWord8, mTvWord9, mTvWord10, mTvWord11, mTvWord12;
    private TextView mTvVer1, mTvVer2, mTvVer3, mTvVer4, mTvVer5, mTvVer6, mTvVer7, mTvVer8, mTvVer9, mTvVer10, mTvVer11, mTvVer12;
    private Map<Integer, Integer> selectWords = new HashMap<Integer, Integer>();
    private Integer curSelected = 0;
    private String menmonics;

    public static MnemonicVerFragment newInstance(String privateKey) {
        Bundle args = new Bundle();
        args.putString(Constant.MNEMONICS, privateKey);
        MnemonicVerFragment fragment = new MnemonicVerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mnemonic_ver, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            clearSelect();
        }
    }

    /**
     * 画面初期化
     *
     * @param view
     */
    private void initView(View view) {

        mTvWord1 = view.findViewById(R.id.tv_word1);
        mTvWord2 = view.findViewById(R.id.tv_word2);
        mTvWord3 = view.findViewById(R.id.tv_word3);
        mTvWord4 = view.findViewById(R.id.tv_word4);
        mTvWord5 = view.findViewById(R.id.tv_word5);
        mTvWord6 = view.findViewById(R.id.tv_word6);
        mTvWord7 = view.findViewById(R.id.tv_word7);
        mTvWord8 = view.findViewById(R.id.tv_word8);
        mTvWord9 = view.findViewById(R.id.tv_word9);
        mTvWord10 = view.findViewById(R.id.tv_word10);
        mTvWord11 = view.findViewById(R.id.tv_word11);
        mTvWord12 = view.findViewById(R.id.tv_word12);

        mTvVer1 = view.findViewById(R.id.tv_ver1);
        mTvVer1.setOnClickListener(this);
        mTvVer2 = view.findViewById(R.id.tv_ver2);
        mTvVer2.setOnClickListener(this);
        mTvVer3 = view.findViewById(R.id.tv_ver3);
        mTvVer3.setOnClickListener(this);
        mTvVer4 = view.findViewById(R.id.tv_ver4);
        mTvVer4.setOnClickListener(this);
        mTvVer5 = view.findViewById(R.id.tv_ver5);
        mTvVer5.setOnClickListener(this);
        mTvVer6 = view.findViewById(R.id.tv_ver6);
        mTvVer6.setOnClickListener(this);
        mTvVer7 = view.findViewById(R.id.tv_ver7);
        mTvVer7.setOnClickListener(this);
        mTvVer8 = view.findViewById(R.id.tv_ver8);
        mTvVer8.setOnClickListener(this);
        mTvVer9 = view.findViewById(R.id.tv_ver9);
        mTvVer9.setOnClickListener(this);
        mTvVer10 = view.findViewById(R.id.tv_ver10);
        mTvVer10.setOnClickListener(this);
        mTvVer11 = view.findViewById(R.id.tv_ver11);
        mTvVer11.setOnClickListener(this);
        mTvVer12 = view.findViewById(R.id.tv_ver12);
        mTvVer12.setOnClickListener(this);

        if (getArguments() != null) {
            String mnemonic = getArguments().getString(Constant.MNEMONICS);
            if (!TextUtils.isEmpty(mnemonic)) {
                List<String> mnemonics = Arrays.asList(mnemonic.split(" "));
                menmonics = mnemonics.toString();
                if (mnemonics.size() == 12) {
                    Collections.shuffle(mnemonics);
                    mTvVer1.setText(mnemonics.get(0));
                    mTvVer2.setText(mnemonics.get(1));
                    mTvVer3.setText(mnemonics.get(2));
                    mTvVer4.setText(mnemonics.get(3));
                    mTvVer5.setText(mnemonics.get(4));
                    mTvVer6.setText(mnemonics.get(5));
                    mTvVer7.setText(mnemonics.get(6));
                    mTvVer8.setText(mnemonics.get(7));
                    mTvVer9.setText(mnemonics.get(8));
                    mTvVer10.setText(mnemonics.get(9));
                    mTvVer11.setText(mnemonics.get(10));
                    mTvVer12.setText(mnemonics.get(11));
                }
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        Integer index;
        switch (view.getId()) {
            case R.id.tv_ver1:
                if (mTvVer1.isActivated()) {
                    index = selectWords.get(0);
                    setWord(index, "");
                    selectWords.remove(0);
                    setCurSelectedByDel(index);
                    mTvVer1.setActivated(false);
                } else {
                    setWord(curSelected, mTvVer1.getText().toString());
                    selectWords.put(0, curSelected);
                    setCurSelected(curSelected + 1);
                    mTvVer1.setActivated(true);
                    isCorrect();
                }
                break;
            case R.id.tv_ver2:
                if (mTvVer2.isActivated()) {
                    index = selectWords.get(1);
                    setWord(index, "");
                    selectWords.remove(1);
                    setCurSelectedByDel(index);
                    mTvVer2.setActivated(false);
                } else {
                    setWord(curSelected, mTvVer2.getText().toString());
                    selectWords.put(1, curSelected);
                    setCurSelected(curSelected + 1);
                    mTvVer2.setActivated(true);
                    isCorrect();
                }
                break;
            case R.id.tv_ver3:
                if (mTvVer3.isActivated()) {
                    index = selectWords.get(2);
                    setWord(index, "");
                    selectWords.remove(2);
                    setCurSelectedByDel(index);
                    mTvVer3.setActivated(false);
                } else {
                    setWord(curSelected, mTvVer3.getText().toString());
                    selectWords.put(2, curSelected);
                    setCurSelected(curSelected + 1);
                    mTvVer3.setActivated(true);
                    isCorrect();
                }
                break;
            case R.id.tv_ver4:
                if (mTvVer4.isActivated()) {
                    index = selectWords.get(3);
                    setWord(index, "");
                    selectWords.remove(3);
                    setCurSelectedByDel(index);
                    mTvVer4.setActivated(false);
                } else {
                    setWord(curSelected, mTvVer4.getText().toString());
                    selectWords.put(3, curSelected);
                    setCurSelected(curSelected + 1);
                    mTvVer4.setActivated(true);
                    isCorrect();
                }
                break;
            case R.id.tv_ver5:
                if (mTvVer5.isActivated()) {
                    index = selectWords.get(4);
                    setWord(index, "");
                    selectWords.remove(4);
                    setCurSelectedByDel(index);
                    mTvVer5.setActivated(false);
                } else {
                    setWord(curSelected, mTvVer5.getText().toString());
                    selectWords.put(4, curSelected);
                    setCurSelected(curSelected + 1);
                    mTvVer5.setActivated(true);
                    isCorrect();
                }
                break;
            case R.id.tv_ver6:
                if (mTvVer6.isActivated()) {
                    index = selectWords.get(5);
                    setWord(index, "");
                    selectWords.remove(5);
                    setCurSelectedByDel(index);
                    mTvVer6.setActivated(false);
                } else {
                    setWord(curSelected, mTvVer6.getText().toString());
                    selectWords.put(5, curSelected);
                    setCurSelected(curSelected + 1);
                    mTvVer6.setActivated(true);
                    isCorrect();
                }
                break;
            case R.id.tv_ver7:
                if (mTvVer7.isActivated()) {
                    index = selectWords.get(6);
                    setWord(index, "");
                    selectWords.remove(6);
                    setCurSelectedByDel(index);
                    mTvVer7.setActivated(false);
                } else {
                    setWord(curSelected, mTvVer7.getText().toString());
                    selectWords.put(6, curSelected);
                    setCurSelected(curSelected + 1);
                    mTvVer7.setActivated(true);
                    isCorrect();
                }
                break;
            case R.id.tv_ver8:
                if (mTvVer8.isActivated()) {
                    index = selectWords.get(7);
                    setWord(index, "");
                    selectWords.remove(7);
                    setCurSelectedByDel(index);
                    mTvVer8.setActivated(false);
                } else {
                    setWord(curSelected, mTvVer8.getText().toString());
                    selectWords.put(7, curSelected);
                    setCurSelected(curSelected + 1);
                    mTvVer8.setActivated(true);
                    isCorrect();
                }
                break;
            case R.id.tv_ver9:
                if (mTvVer9.isActivated()) {
                    index = selectWords.get(8);
                    setWord(index, "");
                    selectWords.remove(8);
                    setCurSelectedByDel(index);
                    mTvVer9.setActivated(false);
                } else {
                    setWord(curSelected, mTvVer9.getText().toString());
                    selectWords.put(8, curSelected);
                    setCurSelected(curSelected + 1);
                    mTvVer9.setActivated(true);
                    isCorrect();
                }
                break;
            case R.id.tv_ver10:
                if (mTvVer10.isActivated()) {
                    index = selectWords.get(9);
                    setWord(index, "");
                    selectWords.remove(9);
                    setCurSelectedByDel(index);
                    mTvVer10.setActivated(false);
                } else {
                    setWord(curSelected, mTvVer10.getText().toString());
                    selectWords.put(9, curSelected);
                    setCurSelected(curSelected + 1);
                    mTvVer10.setActivated(true);
                    isCorrect();
                }
                break;
            case R.id.tv_ver11:
                if (mTvVer11.isActivated()) {
                    index = selectWords.get(10);
                    setWord(index, "");
                    selectWords.remove(10);
                    setCurSelectedByDel(index);
                    mTvVer11.setActivated(false);
                } else {
                    setWord(curSelected, mTvVer11.getText().toString());
                    selectWords.put(10, curSelected);
                    setCurSelected(curSelected + 1);
                    mTvVer11.setActivated(true);
                    isCorrect();
                }
                break;
            case R.id.tv_ver12:
                if (mTvVer12.isActivated()) {
                    index = selectWords.get(11);
                    setWord(index, "");
                    selectWords.remove(11);
                    setCurSelectedByDel(index);
                    mTvVer12.setActivated(false);
                } else {
                    setWord(curSelected, mTvVer12.getText().toString());
                    selectWords.put(11, curSelected);
                    setCurSelected(curSelected + 1);
                    mTvVer12.setActivated(true);
                    isCorrect();
                }
                break;
        }
    }

    private void setWord(Integer index, String word) {
        switch (index) {
            case 0:
                mTvWord1.setText(word);
                break;
            case 1:
                mTvWord2.setText(word);
                break;
            case 2:
                mTvWord3.setText(word);
                break;
            case 3:
                mTvWord4.setText(word);
                break;
            case 4:
                mTvWord5.setText(word);
                break;
            case 5:
                mTvWord6.setText(word);
                break;
            case 6:
                mTvWord7.setText(word);
                break;
            case 7:
                mTvWord8.setText(word);
                break;
            case 8:
                mTvWord9.setText(word);
                break;
            case 9:
                mTvWord10.setText(word);
                break;
            case 10:
                mTvWord11.setText(word);
                break;
            case 11:
                mTvWord12.setText(word);
                break;
        }
    }

    private String getWord(Integer index) {
        String word = "";
        switch (index) {
            case 0:
                word = mTvWord1.getText().toString();
                break;
            case 1:
                word = mTvWord2.getText().toString();
                break;
            case 2:
                word = mTvWord3.getText().toString();
                break;
            case 3:
                word = mTvWord4.getText().toString();
                break;
            case 4:
                word = mTvWord5.getText().toString();
                break;
            case 5:
                word = mTvWord6.getText().toString();
                break;
            case 6:
                word = mTvWord7.getText().toString();
                break;
            case 7:
                word = mTvWord8.getText().toString();
                break;
            case 8:
                word = mTvWord9.getText().toString();
                break;
            case 9:
                word = mTvWord10.getText().toString();
                break;
            case 10:
                word = mTvWord11.getText().toString();
                break;
            case 11:
                word = mTvWord12.getText().toString();
                break;
        }
        return word;
    }

    private void setCurSelected(Integer index) {
        while (!TextUtils.isEmpty(getWord(index)) && index < 12) {
            index++;
        }
        curSelected = index;
    }

    private void setCurSelectedByDel(Integer index) {
        if (index < curSelected) {
            curSelected = index;
        }
    }

    private void isCorrect() {
        if (selectWords.size() == 12) {
            List<String> list = new ArrayList<>();
            list.add(mTvWord1.getText().toString());
            list.add(mTvWord2.getText().toString());
            list.add(mTvWord3.getText().toString());
            list.add(mTvWord4.getText().toString());
            list.add(mTvWord5.getText().toString());
            list.add(mTvWord6.getText().toString());
            list.add(mTvWord7.getText().toString());
            list.add(mTvWord8.getText().toString());
            list.add(mTvWord9.getText().toString());
            list.add(mTvWord10.getText().toString());
            list.add(mTvWord11.getText().toString());
            list.add(mTvWord12.getText().toString());

            if (list.toString().equals(menmonics)) {
                mTvVer1.setClickable(false);
                mTvVer2.setClickable(false);
                mTvVer3.setClickable(false);
                mTvVer4.setClickable(false);
                mTvVer5.setClickable(false);
                mTvVer6.setClickable(false);
                mTvVer7.setClickable(false);
                mTvVer8.setClickable(false);
                mTvVer9.setClickable(false);
                mTvVer10.setClickable(false);
                mTvVer11.setClickable(false);
                mTvVer12.setClickable(false);
                new MsgDialog(getContext(), getString(R.string.toast_mnemonic_backup)).setIsHook(true).show();
            } else {
                clearSelect();
                new MsgDialog(getContext(), getString(R.string.toast_mnemonic_backup_fail)).setIsHook(false).show();
            }
        }
    }

    private void clearSelect() {
        mTvWord1.setText("");
        mTvWord2.setText("");
        mTvWord3.setText("");
        mTvWord4.setText("");
        mTvWord5.setText("");
        mTvWord6.setText("");
        mTvWord7.setText("");
        mTvWord8.setText("");
        mTvWord9.setText("");
        mTvWord10.setText("");
        mTvWord11.setText("");
        mTvWord12.setText("");

        mTvVer1.setActivated(false);
        mTvVer2.setActivated(false);
        mTvVer3.setActivated(false);
        mTvVer4.setActivated(false);
        mTvVer5.setActivated(false);
        mTvVer6.setActivated(false);
        mTvVer7.setActivated(false);
        mTvVer8.setActivated(false);
        mTvVer9.setActivated(false);
        mTvVer10.setActivated(false);
        mTvVer11.setActivated(false);
        mTvVer12.setActivated(false);
        curSelected = 0;
        selectWords = new HashMap<>();
    }
}




