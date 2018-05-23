package pro.oncreate.easynet.processing;

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
    private Object typeAdapter;

    public NCallbackGson(Class<T> tClass) {
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

    public NCallbackGson registerTypeAdapter(Object typeAdapter) {
        this.typeAdapter = typeAdapter;
        return this;
    }

    @Override
    public boolean finish(NResponseModel responseModel) {
        if (responseModel.statusType() == NResponseModel.STATUS_TYPE_SUCCESS || responseModel.isFromCache()) {
            if (requestModel.isNeedParse()) {
                final Class[] fromJsonParams = new Class[2];
                fromJsonParams[0] = String.class;
                fromJsonParams[1] = Class.class;

                final Class[] registerTypeAdapterParams = new Class[2];
                registerTypeAdapterParams[0] = String.class;
                registerTypeAdapterParams[1] = Object.class;

                if (responseModel.getBody().startsWith("{") && responseModel.getBody().endsWith("}"))
                    try {
                        Class<? extends Object> aClass = Class.forName("com.google.gson.Gson");
                        Object gson;
                        if (typeAdapter == null) {
                            gson = aClass.newInstance();
                        } else {
                            Class<? extends Object> bClass = Class.forName("com.google.gson.GsonBuilder");
                            Object gsonBuilder = bClass.newInstance();

                            Method registerTypeAdapter = bClass.getDeclaredMethod("registerTypeAdapter", registerTypeAdapterParams);
                            registerTypeAdapter.invoke(gsonBuilder, typeAdapter);

                            Method create = bClass.getDeclaredMethod("create");
                            gson = create.invoke(gsonBuilder);
                        }
                        Method method = aClass.getDeclaredMethod("fromJson", fromJsonParams);
                        model = (T) method.invoke(gson, responseModel.getBody(), tClass);

                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                else if (responseModel.getBody().startsWith("[") && responseModel.getBody().endsWith("]")) {
                    try {
                        Class<? extends Object> aClass = Class.forName("com.google.gson.Gson");
                        Object gson;
                        if (typeAdapter == null) {
                            gson = aClass.newInstance();
                        } else {
                            Class<? extends Object> bClass = Class.forName("com.google.gson.GsonBuilder");
                            Object gsonBuilder = bClass.newInstance();

                            Method registerTypeAdapter = bClass.getDeclaredMethod("registerTypeAdapter", registerTypeAdapterParams);
                            registerTypeAdapter.invoke(gsonBuilder, typeAdapter);

                            Method create = bClass.getDeclaredMethod("create");
                            gson = create.invoke(gsonBuilder);
                        }
                        Method method = aClass.getDeclaredMethod("fromJson", fromJsonParams);
                        Class t = java.lang.reflect.Array.newInstance(tClass, 0).getClass();

                        T[] array = (T[]) method.invoke(gson, responseModel.getBody(), t);
                        models = Arrays.asList(array);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
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

    private void preSuccess(List<T> models, NResponseModel responseModel) {
        if ((EasyNet.getInstance().getOnSuccessDefaultListener() == null)
                || EasyNet.getInstance().getOnSuccessDefaultListener().onSuccess(responseModel))
            onSuccess(models, responseModel);
    }

    public void onSuccess(T model, NResponseModel responseModel) {
    }

    public void onSuccess(List<T> models, NResponseModel responseModel) {
    }

    public void onCacheLoaded(T model, NResponseModel responseModel) {
    }

    public void onCacheLoaded(List<T> models, NResponseModel responseModel) {
    }
}
