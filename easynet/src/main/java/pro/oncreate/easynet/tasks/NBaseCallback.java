package pro.oncreate.easynet.tasks;

import android.util.Log;
import android.view.View;

import pro.oncreate.easynet.NConfig;
import pro.oncreate.easynet.data.NErrors;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.utils.NLog;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

abstract class NBaseCallback implements NTask.NTaskListener {

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
        } catch (Exception e) {
            Log.d(NLog.LOG_NAME_DEFAULT, e.toString());
        }
    }

    public void onStart(NRequestModel requestModel) {
    }

    @Override
    public void finishUI(NResponseModel responseModel) {
        stopProgressDialog();
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

    public void onError(NResponseModel responseModel) {
    }

    void preFailed(NRequestModel nRequestModel, NErrors error) {
        if ((NConfig.getInstance().getOnFailedDefaultListener() != null && NConfig.getInstance().getOnFailedDefaultListener().onFailed(nRequestModel, error))
                || (NConfig.getInstance().getOnFailedDefaultListener() == null))
            onFailed(nRequestModel, error);
    }

    public void onFailed(NRequestModel nRequestModel, NErrors error) {
    }
}
