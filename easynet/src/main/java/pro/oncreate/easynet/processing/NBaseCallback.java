package pro.oncreate.easynet.processing;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import java.util.List;

import pro.oncreate.easynet.EasyNet;
import pro.oncreate.easynet.data.NErrors;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.models.subsidiary.BindView;
import pro.oncreate.easynet.utils.NLog;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused,WeakerAccess")
public abstract class NBaseCallback implements BaseTask.NTaskListener {

    private static final int MESSAGE_SHOW_PROGRESS = 100;
    private static final int DEFAULT_DELAY = 399;

    NRequestModel requestModel;

    @Override
    public void start(NRequestModel requestModel) {
        this.requestModel = requestModel;
        hideContent();
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_SHOW_PROGRESS, 1), DEFAULT_DELAY);
        onStart(requestModel);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            startProgress();
        }
    };

    private void startProgress() {
        try {
            if (requestModel.getProgressDialog() != null)
                requestModel.getProgressDialog().show();
            if (requestModel.getProgressBar() != null)
                requestModel.getProgressBar().setVisibility(View.VISIBLE);
            if (requestModel.getProgressView() != null)
                requestModel.getProgressView().setVisibility(View.VISIBLE);
            if (requestModel.getRefreshLayout() != null && !requestModel.getRefreshLayout().isRefreshing())
                requestModel.getRefreshLayout().setRefreshing(true);
        } catch (Exception e) {
            Log.d(NLog.LOG_NAME_DEFAULT, e.toString());
        }
    }

    @SuppressWarnings("WrongConstant")
    private void hideContent() {
        try {
            if (requestModel.getBindViews() != null) {
                for (BindView bindView : requestModel.getBindViews()) {
                    bindView.onStart();
                }
            }
        } catch (Exception e) {
            Log.d(NLog.LOG_NAME_DEFAULT, e.toString());
        }
    }

    private void stopProgress(boolean isSuccess) {
        try {
            if (requestModel.getBindViews() != null) {
                for (int i = 0; i < requestModel.getBindViews().size(); i++) {
                    if (isSuccess)
                        requestModel.getBindViews().get(i).onSuccess();
                    else requestModel.getBindViews().get(i).onError();
                }
            }

            if (requestModel.getProgressDialog() != null && requestModel.getProgressDialog().isShowing())
                requestModel.getProgressDialog().dismiss();
            if (requestModel.getProgressBar() != null && requestModel.getProgressBar().getVisibility() == View.VISIBLE)
                requestModel.getProgressBar().setVisibility(View.GONE);
            if (requestModel.getProgressView() != null && requestModel.getProgressView().getVisibility() == View.VISIBLE)
                requestModel.getProgressView().setVisibility(View.GONE);
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
        mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
        stopProgress(true);
        callWaitHeadersCallbacks(responseModel);
    }

    void finishUIFailed() {
        mHandler.removeMessages(MESSAGE_SHOW_PROGRESS);
        stopProgress(false);
    }

    protected void preError(NResponseModel responseModel) {
        if (EasyNet.getInstance().getOnErrorDefaultListener() != null) {
            EasyNet.getInstance().getOnErrorDefaultListener().onError(responseModel);
        } else {
            for (int i = 0; i < EasyNet.getInstance().getOnErrorDefaultListenersCollection().size(); i++) {
                if (EasyNet.getInstance().getOnErrorDefaultListenersCollection().get(i).getCode() == responseModel.getStatusCode()) {
                    EasyNet.getInstance().getOnErrorDefaultListenersCollection().get(i).onError(responseModel);
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
        if ((EasyNet.getInstance().getOnFailedDefaultListener() != null
                && EasyNet.getInstance().getOnFailedDefaultListener().onFailed(nRequestModel, error))
                || (EasyNet.getInstance().getOnFailedDefaultListener() == null))
            onFailed(nRequestModel, error);
    }

    public void onFailed(NRequestModel nRequestModel, NErrors error) {
    }

    void preTaskCancelled(NRequestModel requestModel, String tag) {
        stopProgress(false);
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

    @Override
    public boolean redirect(String location) {
        return onRedirect(location);
    }

    public boolean onRedirect(String location) {
        return true;
    }

    abstract public void onRedirectInterrupted(String location, NResponseModel responseModel);

    public void onCacheMissing(NRequestModel requestModel) {
    }
}
