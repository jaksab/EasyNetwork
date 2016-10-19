package pro.oncreate.easynet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pro.oncreate.easynet.data.NErrors;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.tasks.NTask;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

public class NConfig {

    // Init
    //

    volatile private static NConfig config;

    private NConfig() {
    }

    synchronized public static NConfig getInstance() {
//        if (config == null)
//            config = new NConfig();
//        return config;
        NConfig localInstance = config;
        if (localInstance == null) {
            synchronized (NConfig.class) {
                localInstance = config;
                if (localInstance == null) {
                    config = localInstance = new NConfig();
                }
            }
        }
        return localInstance;
    }

    // Data
    //

    private boolean writeLogs = true;

    public boolean isWriteLogs() {
        return writeLogs;
    }

    /**
     * Set true, if you will want to show logs when library is working
     *
     * @param writeLogs
     */
    public void setWriteLogs(boolean writeLogs) {
        this.writeLogs = writeLogs;
    }


    // Default builders
    //

    private NBuilder nBuilder;

    private NBuilderDefaultListener defaultNBuilderListener;

    public void setDefaultNBuilderListener(NBuilderDefaultListener defaultNBuilderListener) {
        this.defaultNBuilderListener = defaultNBuilderListener;
    }

    public NBuilder getDefaultNBuilder() {
        if (defaultNBuilderListener != null) {
            this.nBuilder = defaultNBuilderListener.defaultConfig(NBuilder.newInstance());
        }
        return this.nBuilder;
    }

    public interface NBuilderDefaultListener {
        NBuilder defaultConfig(NBuilder nBuilder);
    }

    // Default behaviour
    //

    // Success

    private OnSuccessDefaultListener onSuccessDefaultListener;

    public void setDefaultOnSuccessListener(OnSuccessDefaultListener onSuccessDefaultListener) {
        this.onSuccessDefaultListener = onSuccessDefaultListener;
    }

    public OnSuccessDefaultListener getOnSuccessDefaultListener() {
        return onSuccessDefaultListener;
    }

    public interface OnSuccessDefaultListener {
        boolean onSuccess(NResponseModel responseModel);
    }

    // Failed

    private OnFailedDefaultListener onFailedDefaultListener;

    public void setDefaultOnFailedListener(OnFailedDefaultListener onFailedDefaultListener) {
        this.onFailedDefaultListener = onFailedDefaultListener;
    }

    public OnFailedDefaultListener getOnFailedDefaultListener() {
        return onFailedDefaultListener;
    }

    public interface OnFailedDefaultListener {
        boolean onFailed(NRequestModel nRequestModel, NErrors error);
    }

    // Errors

    private ArrayList<OnErrorDefaultListenerWithCode> onErrorDefaultListenersCollection = new ArrayList<>();
    private OnErrorDefaultListener onErrorDefaultListener;

    public ArrayList<OnErrorDefaultListenerWithCode> getOnErrorDefaultListenersCollection() {
        return onErrorDefaultListenersCollection;
    }

    public void addOnErrorDefaultListener(OnErrorDefaultListenerWithCode onErrorDefaultListener) {
        onErrorDefaultListenersCollection.add(onErrorDefaultListener);
    }

    public OnErrorDefaultListener getOnErrorDefaultListener() {
        return onErrorDefaultListener;
    }

    public void setOnErrorDefaultListener(OnErrorDefaultListener onErrorDefaultListener) {
        this.onErrorDefaultListener = onErrorDefaultListener;
    }

    public static abstract class OnErrorDefaultListenerWithCode extends OnErrorDefaultListener {

        private int code;

        public OnErrorDefaultListenerWithCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    public static abstract class OnErrorDefaultListener {

        public abstract void onError(NResponseModel responseModel);
    }

    // Tasks queue

    private Map<String, NTask> taskQueue;

    public void addTask(String tag, NTask task) {
        if (taskQueue == null)
            taskQueue = new HashMap<>();
        taskQueue.put(tag, task);
    }

    public void removeTask(String tag) {
        taskQueue.remove(tag);
    }

    public void cancelAllTasks() {
        for (NTask task : taskQueue.values()) {
            task.cancel(false);
        }
    }

}

