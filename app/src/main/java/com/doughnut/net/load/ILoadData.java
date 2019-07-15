package com.doughnut.net.load;


import com.doughnut.net.apirequest.ApiRequest;
import com.doughnut.net.listener.LoadDataListener;

/**
 */
public interface ILoadData {

    void loadData(ApiRequest request, boolean shouldCache, LoadDataListener listener);

}
