package com.doughnut.net.api;

import com.android.volley.VolleyError;
import com.doughnut.config.Constant;
import com.doughnut.net.apirequest.BaseGetApiRequest;
import com.doughnut.utils.DeviceUtil;


public class GetAllTokenList extends BaseGetApiRequest {

    public GetAllTokenList() {
    }

    @Override
    public String initUrl() {
        return Constant.JC_SCAN_SERVER + "/sum/all/" + DeviceUtil.generateDeviceUniqueId();
    }

    @Override
    public void handleMessage(String response) {

    }

    @Override
    public void handleError(int code, VolleyError error) {

    }

}
