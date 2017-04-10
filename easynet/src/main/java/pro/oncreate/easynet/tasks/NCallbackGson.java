package pro.oncreate.easynet.tasks;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import pro.oncreate.easynet.EasyNet;
import pro.oncreate.easynet.data.NErrors;
import pro.oncreate.easynet.models.NResponseModel;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused,WeakerAccess")
public class NCallbackGson<T extends Object> extends NBaseCallback {

    private T model;
    protected List<T> models;
    private Class<T> tClass;

    public NCallbackGson(Class<T> tClass) {
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
                        Class[] fromJsonParams = new Class[2];
                        fromJsonParams[0] = String.class;
                        fromJsonParams[1] = Class.class;

                        Class<? extends Object> aClass = Class.forName("com.google.gson.Gson");
                        Object gson = aClass.newInstance();
                        Method method = aClass.getDeclaredMethod("fromJson", fromJsonParams);

                        model = (T) method.invoke(gson, responseModel.getBody(), tClass);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                else if (responseModel.getBody().startsWith("[") && responseModel.getBody().endsWith("]")) {
                    try {
                        Class[] fromJsonParams = new Class[2];
                        fromJsonParams[0] = String.class;
                        fromJsonParams[1] = Class.class;

                        Class<? extends Object> aClass = Class.forName("com.google.gson.Gson");
                        Object gson = aClass.newInstance();
                        Method method = aClass.getDeclaredMethod("fromJson", fromJsonParams);

                        Class t = java.lang.reflect.Array.newInstance(tClass, 0).getClass();

                        T[] array = (T[]) method.invoke(gson, responseModel.getBody(), t);
                        models = Arrays.asList(array);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private void preSuccess(T model, NResponseModel responseModel) {
        if ((EasyNet.getInstance().getOnSuccessDefaultListener() == null)
                || EasyNet.getInstance().getOnSuccessDefaultListener().onSuccess(responseModel))
            onSuccess(model, responseModel);
    }

    private void preSuccess(List<T> models, NResponseModel responseModel) {
        if ((EasyNet.getInstance().getOnSuccessDefaultListener() == null)
                || EasyNet.getInstance().getOnSuccessDefaultListener().onSuccess(responseModel))
            onSuccess(models, responseModel);
    }

    public void onSuccess(T model, NResponseModel responseModel) {
    }

    public void onSuccess(List<T> models, NResponseModel responseModel) {
    }

}
