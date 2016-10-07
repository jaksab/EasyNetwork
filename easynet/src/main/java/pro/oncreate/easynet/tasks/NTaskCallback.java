package pro.oncreate.easynet.tasks;

import pro.oncreate.easynet.NConfig;
import pro.oncreate.easynet.models.NBaseModel;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;

/**
 * Created by andrej on 04.10.16.
 */

public class NTaskCallback<T extends NBaseModel> implements NTask.NTaskListener {

    private NRequestModel requestModel;
    private T model;

    public NTaskCallback(T model) {
        this.model = model;
    }

    @Override
    public void start(NRequestModel requestModel) {
        this.requestModel = requestModel;
        onStart(requestModel);
    }

    @Override
    public void finishUI(NResponseModel responseModel) {
        if (responseModel.statusType() == NResponseModel.STATUS_TYPE_SUCCESS) {
            if (model != null) {
                if (requestModel.isEnableDefaultListeners())
                    preSuccess(model, responseModel);
                else onSuccess(model, responseModel);
            } else {
                if (requestModel.isEnableDefaultListeners())
                    preFailed(requestModel, Errors.PARSE_ERROR);
                else onFailed(requestModel, Errors.PARSE_ERROR);
            }
        } else if (responseModel.statusType() == NResponseModel.STATUS_TYPE_ERROR) {
            if (requestModel.isEnableDefaultListeners())
                preError(responseModel);
            else onError(responseModel);
        }
    }

    @Override
    public void finish(NResponseModel responseModel) {
        if (responseModel.statusType() == NResponseModel.STATUS_TYPE_SUCCESS) {
            if (requestModel.isNeedParse()) {
                model.create(responseModel);
            }
        } else {
            // TODO: parse error model
        }

    }

    public void onStart(NRequestModel requestModel) {

    }

    private void preSuccess(T model, NResponseModel responseModel) {
        if ((NConfig.getInstance().getOnSuccessDefaultListener() != null && NConfig.getInstance().getOnSuccessDefaultListener().onSuccess(model, responseModel))
                || (NConfig.getInstance().getOnSuccessDefaultListener() == null))
            onSuccess(model, responseModel);
    }

    public void onSuccess(T model, NResponseModel responseModel) {
    }

    private void preError(NResponseModel responseModel) {
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

    void preFailed(NRequestModel nRequestModel, Errors error) {
        if ((NConfig.getInstance().getOnFailedDefaultListener() != null && NConfig.getInstance().getOnFailedDefaultListener().onFailed(nRequestModel, error))
                || (NConfig.getInstance().getOnFailedDefaultListener() == null))
            onFailed(nRequestModel, error);
    }

    public void onFailed(NRequestModel nRequestModel, Errors error) {
    }

    public enum Errors {
        CONNECTION_ERROR, PARSE_ERROR;
    }
}
