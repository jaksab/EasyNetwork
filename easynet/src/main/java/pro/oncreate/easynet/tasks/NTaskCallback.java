package pro.oncreate.easynet.tasks;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import pro.oncreate.easynet.NConfig;
import pro.oncreate.easynet.models.NBaseModel;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;

/**
 * Created by andrej on 04.10.16.
 */

public class NTaskCallback<T extends NBaseModel> implements NTask.NTaskListener {

    protected NRequestModel requestModel;
    protected T model;
    protected ArrayList<T> models;
    protected Class<T> tClass;

    public NTaskCallback(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public void start(NRequestModel requestModel) {
        this.requestModel = requestModel;
        onStart(requestModel);
    }

    @Override
    public void finishUI(NResponseModel responseModel) {
        if (responseModel.statusType() == NResponseModel.STATUS_TYPE_SUCCESS) {
            if (requestModel.isEnableDefaultListeners()) {
                if (model != null)
                    preSuccess(model, responseModel);
                else if (models != null)
                    preSuccess(models, responseModel);
                else preFailed(requestModel, Errors.PARSE_ERROR);
            } else {
                if (model != null)
                    onSuccess(model, responseModel);
                else if (models != null)
                    onSuccess(models, responseModel);
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
                if (responseModel.getBody().startsWith("{") && responseModel.getBody().endsWith("}"))
                    try {
                        model = tClass.cast(tClass.newInstance().parse(responseModel, new JSONObject(responseModel.getBody())));
                    } catch (Exception e) {
                    }
                else if (responseModel.getBody().startsWith("[") && responseModel.getBody().endsWith("]")) {
                    try {
                        JSONArray jsonArray = new JSONArray(responseModel.getBody());
                        models = new ArrayList<T>();
                        for (int i = 0; i < jsonArray.length(); i++)
                            models.add(tClass.cast(tClass.newInstance().parse(responseModel, jsonArray.getJSONObject(i))));
                    } catch (Exception e) {
                    }
                }
            }
        } else {
            // TODO: parse error model
        }

    }

    public void onStart(NRequestModel requestModel) {
    }

    protected void preSuccess(T model, NResponseModel responseModel) {
        if ((NConfig.getInstance().getOnSuccessDefaultListener() == null) || NConfig.getInstance().getOnSuccessDefaultListener().onSuccess(responseModel))
            onSuccess(model, responseModel);
    }

    protected void preSuccess(ArrayList<T> models, NResponseModel responseModel) {
        if ((NConfig.getInstance().getOnSuccessDefaultListener() == null) || NConfig.getInstance().getOnSuccessDefaultListener().onSuccess(responseModel))
            onSuccess(models, responseModel);
    }

    public void onSuccess(T model, NResponseModel responseModel) {
    }

    public void onSuccess(ArrayList<T> models, NResponseModel responseModel) {
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
