
package com.doughnut.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doughnut.R;
import com.doughnut.utils.NetUtil;
import com.doughnut.utils.ToastUtil;
import com.doughnut.utils.ViewUtil;


public class DappFragment extends BaseFragment implements View.OnClickListener {

    public static DappFragment newInstance() {
        Bundle args = new Bundle();
        DappFragment fragment = new DappFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return ViewUtil.inflatView(inflater, container, R.layout.fragment_main_wallet_new, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView(view);
    }

    private void initView(View view) {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (!NetUtil.isNetworkAvailable(getActivity())) {
            ToastUtil.toast(getContext(), getString(R.string.toast_no_network));
            return;
        }
        switch (view.getId()) {
        }
    }

}