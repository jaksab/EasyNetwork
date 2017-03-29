package pro.oncreate.easynet.tasks;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import pro.oncreate.easynet.NConfig;
import pro.oncreate.easynet.data.NErrors;
import pro.oncreate.easynet.models.NBaseModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.utils.NLog;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused,WeakerAccess")
public class NCallbackParse<T extends NBaseModel> extends NBaseCallback {

    private T model;
    protected ArrayList<T> models;
    private Class<T> tClass;

    public NCallbackParse(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public void finishUI(NResponseModel responseModel) {
        super.finishUI(responseModel);
        if (responseModel.statusType() == NResponseModel.STATUS_TYPE_SUCCESS) {
            if (requestModel.isEnableDefaultListeners()) {
                if (model != null)
                    preSuccess(model, responseModel);
                else if (models != null)
                    preSuccess(models, responseModel);
                else preFailed(requestModel, NErrors.PARSE_ERROR);
            } else {
                if (model != null)
                    onSuccess(model, responseModel);
                else if (models != null)
                    onSuccess(models, responseModel);
                else onFailed(requestModel, NErrors.PARSE_ERROR);
            }
        } else if (responseModel.statusType() == NResponseModel.STATUS_TYPE_ERROR) {
            if (requestModel.isEnableDefaultListeners())
                preError(responseModel);
            else onError(responseModel);
        }
    }

    @Override
    public void onRedirectInterrupted(String location, NResponseModel responseModel) {

    }

    @Override
    public void finish(NResponseModel responseModel) {
        if (responseModel.statusType() == NResponseModel.STATUS_TYPE_SUCCESS) {
            if (requestModel.isNeedParse()) {
                if (responseModel.getBody().startsWith("{") && responseModel.getBody().endsWith("}"))
                    try {
                        model = tClass.cast(tClass.newInstance().parse(responseModel, new JSONObject(responseModel.getBody())));
                    } catch (Exception e) {
                        Log.d(NLog.LOG_NAME_DEFAULT, e.toString());
                    }
                else if (responseModel.getBody().startsWith("[") && responseModel.getBody().endsWith("]")) {
                    try {
                        JSONArray jsonArray = new JSONArray(responseModel.getBody());
                        models = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++)
                            models.add(tClass.cast(tClass.newInstance().parse(responseModel, jsonArray.getJSONObject(i))));
                    } catch (Exception e) {
                        Log.d(NLog.LOG_NAME_DEFAULT, e.toString());
                    }
                }
            }
        }
    }

    private void preSuccess(T model, NResponseModel responseModel) {
        if ((NConfig.getInstance().getOnSuccessDefaultListener() == null) || NConfig.getInstance().getOnSuccessDefaultListener().onSuccess(responseModel))
            onSuccess(model, responseModel);
    }

    private void preSuccess(ArrayList<T> models, NResponseModel responseModel) {
        if ((NConfig.getInstance().getOnSuccessDefaultListener() == null) || NConfig.getInstance().getOnSuccessDefaultListener().onSuccess(responseModel))
            onSuccess(models, responseModel);
    }

    public void onSuccess(T model, NResponseModel responseModel) {
    }

    public void onSuccess(ArrayList<T> models, NResponseModel responseModel) {
    }

}
