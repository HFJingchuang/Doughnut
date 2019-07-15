package com.doughnut.net.api.jtrequest;

import com.android.volley.VolleyError;
import com.doughnut.config.Constant;
import com.doughnut.net.apirequest.BaseGetApiRequest;


public class JTBalanceRequest extends BaseGetApiRequest {
    private String mJtWalletAddress;

    public JTBalanceRequest(String address) {
        this.mJtWalletAddress = address;
    }
    @Override
    public String initUrl() {
        return Constant.jt_base_url +"/v2/accounts/" + this.mJtWalletAddress + "/balances";
    }

    @Override
    public void handleMessage(String response) {

    }

    @Override
    public void handleError(int code, VolleyError error) {

    }
}
