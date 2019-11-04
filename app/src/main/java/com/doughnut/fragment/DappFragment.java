package com.doughnut.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doughnut.R;
import com.doughnut.config.Constant;
import com.doughnut.web.WebActivity;

public class DappFragment extends BaseFragment {

    private EditText mEt_url;
    private TextView mTv_search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dapp, container, false);
        mEt_url = view.findViewById(R.id.et_url);
        mTv_search = view.findViewById(R.id.tv_search);
        mTv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchUrl = mEt_url.getText().toString().trim();
                //以下未测试数据
                //searchUrl = "https://www.baidu.com/";
                if (TextUtils.isEmpty(searchUrl)) {
                    Toast.makeText(getActivity(), "输入地址为空", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(getActivity(), WebActivity.class)
                            .putExtra(Constant.LOAD_URL, searchUrl));
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static DappFragment newInstance() {
        DappFragment dappFragment = new DappFragment();
        return dappFragment;
    }
}
