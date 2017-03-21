package pro.oncreate.easynet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pro.oncreate.easynet.data.NConst;
import pro.oncreate.easynet.data.NErrors;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.tasks.NTask;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused,WeakerAccess")
public class NConfig {

    // Init
    //

    volatile private static NConfig config;

    private NConfig() {
    }

    synchronized public static NConfig getInstance() {
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
    public NConfig setWriteLogs(boolean writeLogs) {
        this.writeLogs = writeLogs;
        return this;
    }


    // Default builders
    //

    private NBuilder nBuilder;

    private NBuilderDefaultListener defaultNBuilderListener;

    /**
     * Use this callback to set the basic properties for all queries.
     *
     * @param defaultNBuilderListener callback default NBuilder
     */
    public NConfig setDefaultNBuilderListener(NBuilderDefaultListener defaultNBuilderListener) {
        this.defaultNBuilderListener = defaultNBuilderListener;
        return this;
    }

    public NBuilder getDefaultNBuilder() {
        if (defaultNBuilderListener != null) {
            this.nBuilder = defaultNBuilderListener.defaultConfig(NBuilder.newInstance());
        }
        return this.nBuilder;
    }

    public interface NBuilderDefaultListener {
        /**
         * Receives a empty NBuilder instance that must a result return
         */
        NBuilder defaultConfig(NBuilder nBuilder);
    }

    // Default behaviour
    //

    // Success

    private OnSuccessDefaultListener onSuccessDefaultListener;

    /**
     * Define the basic logic of successful requests.
     *
     * @param onSuccessDefaultListener OnSuccessDefaultListener instance
     */
    public NConfig setDefaultOnSuccessListener(OnSuccessDefaultListener onSuccessDefaultListener) {
        this.onSuccessDefaultListener = onSuccessDefaultListener;
        return this;
    }

    public OnSuccessDefaultListener getOnSuccessDefaultListener() {
        return onSuccessDefaultListener;
    }

    public interface OnSuccessDefaultListener {
        boolean onSuccess(NResponseModel responseModel);
    }

    // Failed

    private OnFailedDefaultListener onFailedDefaultListener;

    /**
     * Define the basic logic of failed requests.
     *
     * @param onFailedDefaultListener OnFailedDefaultListener instance
     */
    public NConfig setDefaultOnFailedListener(OnFailedDefaultListener onFailedDefaultListener) {
        this.onFailedDefaultListener = onFailedDefaultListener;
        return this;
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

    /**
     * Define the basic logic of error for status code.
     *
     * @param onErrorDefaultListener OnErrorDefaultListenerWithCode instance
     */
    public NConfig addOnErrorDefaultListener(OnErrorDefaultListenerWithCode onErrorDefaultListener) {
        onErrorDefaultListenersCollection.add(onErrorDefaultListener);
        return this;
    }

    public OnErrorDefaultListener getOnErrorDefaultListener() {
        return onErrorDefaultListener;
    }

    /**
     * Define the basic logic of all errors.
     *
     * @param onErrorDefaultListener OnErrorDefaultListener instance
     */
    public NConfig setOnErrorDefaultListener(OnErrorDefaultListener onErrorDefaultListener) {
        this.onErrorDefaultListener = onErrorDefaultListener;
        return this;
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

    /**
     * Resister a new task in queue.
     *
     * @param tag  unique name
     * @param task NTask instance
     */
    public void addTask(String tag, NTask task) {
        if (taskQueue == null)
            taskQueue = new HashMap<>();
        taskQueue.put(tag, task);
    }

    /**
     * Is there any current tasks in execution.
     *
     * @return true - if task queue not empty
     */
    public boolean isCurrentTasks() {
        return taskQueue != null && taskQueue.size() > 0;
    }

    /**
     * Cancel current request execution for tag.
     *
     * @param tag - name of task
     */
    public void removeTask(String tag) {
        taskQueue.remove(tag);
    }

    /**
     * Cancel all current request execution.
     */
    public void cancelAllTasks() {
        for (NTask task : taskQueue.values()) {
            task.cancel(false);
        }
    }

    // Other

    private static final String[] SUPPORTED_METHODS = {
            NConst.GET, NConst.POST, NConst.PUT, NConst.DELETE, NConst.OPTIONS, NConst.HEAD
    };

    public static String[] getSupportedMethods() {
        return SUPPORTED_METHODS;
    }


}

