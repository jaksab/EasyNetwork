package pro.oncreate.easynet.processing;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pro.oncreate.easynet.EasyNet;
import pro.oncreate.easynet.data.NError;
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
    private Exception parseException;

    public NCallbackParse(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public void finishUI(NResponseModel responseModel) {
        super.finishUI(responseModel);
        if (responseModel.isFromCache()) {
            if (model != null)
                onCacheLoaded(model, responseModel);
            else if (models != null)
                onCacheLoaded(models, responseModel);
            else onCacheMissing(requestModel);
        } else if (responseModel.statusType() == NResponseModel.STATUS_TYPE_SUCCESS) {
            if (requestModel.isEnableDefaultListeners()) {
                if (model != null)
                    preSuccess(model, responseModel);
                else if (models != null)
                    preSuccess(models, responseModel);
                else preFailed(requestModel, new NError(NError.TYPE_PARSE_ERROR, parseException));
            } else {
                if (model != null)
                    onSuccess(model, responseModel);
                else if (models != null)
                    onSuccess(models, responseModel);
                else onFailed(requestModel, new NError(NError.TYPE_PARSE_ERROR, parseException));
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
    public boolean finish(NResponseModel responseModel) {
        if (responseModel.statusType() == NResponseModel.STATUS_TYPE_SUCCESS || responseModel.isFromCache()) {
            if (requestModel.isNeedParse()) {
                if (responseModel.getBody().startsWith("{") && responseModel.getBody().endsWith("}"))
                    try {
                        model = tClass.cast(tClass.newInstance()
                                .parse(responseModel, new JSONObject(responseModel.getBody())));
                        return true;
                    } catch (Exception e) {
                        parseException = e;
                        Log.d(NLog.LOG_NAME_DEFAULT, e.toString());
                        return false;
                    }
                else if (responseModel.getBody().startsWith("[") && responseModel.getBody().endsWith("]")) {
                    try {
                        JSONArray jsonArray = new JSONArray(responseModel.getBody());
                        models = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++)
                            models.add(tClass.cast(tClass.newInstance()
                                    .parse(responseModel, jsonArray.getJSONObject(i))));
                        return true;
                    } catch (Exception e) {
                        parseException = e;
                        Log.d(NLog.LOG_NAME_DEFAULT, e.toString());
                        return false;
                    }
                }
            }
        }
        return false;
    }

    private void preSuccess(T model, NResponseModel responseModel) {
        if ((EasyNet.getInstance().getOnSuccessDefaultListener() == null)
                || EasyNet.getInstance().getOnSuccessDefaultListener().onSuccess(responseModel))
            onSuccess(model, responseModel);
    }

    private void preSuccess(ArrayList<T> models, NResponseModel responseModel) {
        if ((EasyNet.getInstance().getOnSuccessDefaultListener() == null)
                || EasyNet.getInstance().getOnSuccessDefaultListener().onSuccess(responseModel))
            onSuccess(models, responseModel);
    }

    public void onSuccess(T model, NResponseModel responseModel) {
    }

    public void onSuccess(ArrayList<T> models, NResponseModel responseModel) {
    }

    public void onCacheLoaded(T model, NResponseModel responseModel) {
    }

    public void onCacheLoaded(List<T> models, NResponseModel responseModel) {
    }

}
