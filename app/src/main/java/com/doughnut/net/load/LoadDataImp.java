package com.doughnut.net.load;


import com.doughnut.net.apirequest.ApiRequest;
import com.doughnut.net.listener.LoadDataListener;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 *
 */
public class LoadDataImp implements ILoadData {

    @Override
    public void loadData(final ApiRequest request, boolean shouldCache, final LoadDataListener listener) {
        Observer<String> subscriber = new Observer<String>() {
            @Override
            public void onError(Throwable throwable) {
                listener.loadFailed(throwable, request.getReqId());
            }

            @Override
            public void onComplete() {
                listener.loadFinish();
            }

            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String response) {
                listener.loadSuccess(response);
            }
        };

        request.getObservableObj(shouldCache)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
