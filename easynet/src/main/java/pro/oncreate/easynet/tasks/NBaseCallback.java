package pro.oncreate.easynet.tasks;

import android.util.Log;
import android.view.View;

import java.util.List;

import pro.oncreate.easynet.NConfig;
import pro.oncreate.easynet.data.NErrors;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.utils.NLog;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

public abstract class NBaseCallback implements NTask.NTaskListener {

    protected NRequestModel requestModel;

    @Override
    public void start(NRequestModel requestModel) {
        this.requestModel = requestModel;
        startProgressDialog();
        onStart(requestModel);
    }

    protected void startProgressDialog() {
        try {
            if (requestModel.getProgressDialog() != null)
                requestModel.getProgressDialog().show();
            if (requestModel.getProgressBar() != null)
                requestModel.getProgressBar().setVisibility(View.VISIBLE);
            if (requestModel.getProgressView() != null)
                requestModel.getProgressView().setVisibility(View.VISIBLE);
            if (requestModel.getHideView() != null)
                requestModel.getHideView().setVisibility(View.INVISIBLE);
            if (requestModel.getRefreshLayout() != null && !requestModel.getRefreshLayout().isRefreshing())
                requestModel.getRefreshLayout().setRefreshing(true);
        } catch (Exception e) {
            Log.d(NLog.LOG_NAME_DEFAULT, e.toString());
        }
    }

    protected void stopProgressDialog() {
        try {
            if (requestModel.getProgressDialog() != null)
                requestModel.getProgressDialog().dismiss();
            if (requestModel.getProgressBar() != null)
                requestModel.getProgressBar().setVisibility(View.GONE);
            if (requestModel.getProgressView() != null)
                requestModel.getProgressView().setVisibility(View.GONE);
            if (requestModel.getHideView() != null)
                requestModel.getHideView().setVisibility(View.VISIBLE);
            if (requestModel.getRefreshLayout() != null && requestModel.getRefreshLayout().isRefreshing())
                requestModel.getRefreshLayout().setRefreshing(false);
        } catch (Exception e) {
            Log.d(NLog.LOG_NAME_DEFAULT, e.toString());
        }
    }

    public void onStart(NRequestModel requestModel) {
    }

    @Override
    public void finishUI(NResponseModel responseModel) {
        stopProgressDialog();
        callWaitHeadersCallbacks(responseModel);
    }

    protected void preError(NResponseModel responseModel) {
        if (NConfig.getInstance().getOnErrorDefaultListener() != null) {
            NConfig.getInstance().getOnErrorDefaultListener().onError(responseModel);
        } else {
            for (int i = 0; i < NConfig.getInstance().getOnErrorDefaultListenersCollection().size(); i++) {
                if (NConfig.getInstance().getOnErrorDefaultListenersCollection().get(i).getCode() == responseModel.getStatusCode()) {
                    NConfig.getInstance().getOnErrorDefaultListenersCollection().get(i).onError(responseModel);
                    break;
                }
            }
            onError(responseModel);
        }
    }

    private void callWaitHeadersCallbacks(NResponseModel responseModel) {
        if (requestModel.getWaitHeaderCallbacks() != null && responseModel != null && responseModel.getHeaders() != null) {
            for (WaitHeaderCallback callback : requestModel.getWaitHeaderCallbacks()) {
                List<String> headerValues = responseModel.getHeaders().get(callback.getHeader());
                if (headerValues != null && !headerValues.isEmpty()) {
                    callback.takeHeader(headerValues);
                }
            }
        }
    }

    public void onError(NResponseModel responseModel) {
    }

    void preFailed(NRequestModel nRequestModel, NErrors error) {
        if ((NConfig.getInstance().getOnFailedDefaultListener() != null && NConfig.getInstance().getOnFailedDefaultListener().onFailed(nRequestModel, error))
                || (NConfig.getInstance().getOnFailedDefaultListener() == null))
            onFailed(nRequestModel, error);
    }

    public void onFailed(NRequestModel nRequestModel, NErrors error) {
    }

    void preTaskCancelled(NRequestModel requestModel, String tag) {
        stopProgressDialog();
        onTaskCancelled(requestModel, tag);
    }

    public void onTaskCancelled(NRequestModel requestModel, String tag) {
    }

    public static abstract class WaitHeaderCallback {

        private String header;

        public WaitHeaderCallback(String header) {
            this.header = header;
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        abstract public void takeHeader(List<String> values);
    }
}
