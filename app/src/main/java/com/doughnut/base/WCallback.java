package com.doughnut.base;

import com.doughnut.utils.GsonUtil;



public interface WCallback {

    void onGetWResult(int ret, GsonUtil extra);
}
