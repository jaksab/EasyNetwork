package pro.oncreate.easynet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pro.oncreate.easynet.data.NConst;
import pro.oncreate.easynet.data.NErrors;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.tasks.NTask;

import static pro.oncreate.easynet.data.NConst.DELETE;
import static pro.oncreate.easynet.data.NConst.GET;
import static pro.oncreate.easynet.data.NConst.HEAD;
import static pro.oncreate.easynet.data.NConst.OPTIONS;
import static pro.oncreate.easynet.data.NConst.POST;
import static pro.oncreate.easynet.data.NConst.PUT;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused,WeakerAccess")
public class EasyNet {


    //
    // Fields
    //


    volatile private static EasyNet config;
    private boolean writeLogs = true;
    private Request nBuilder; // Default nBuilder instance
    private NBuilderDefaultListener defaultNBuilderListener;

    private OnSuccessDefaultListener onSuccessDefaultListener;
    private OnFailedDefaultListener onFailedDefaultListener;
    private ArrayList<OnErrorDefaultListenerWithCode> onErrorDefaultListenersCollection = new ArrayList<>();
    private OnErrorDefaultListener onErrorDefaultListener;

    private Map<String, NTask> taskQueue;

    private static final String[] SUPPORTED_METHODS = {
            GET, POST, PUT, DELETE, OPTIONS, HEAD
    };

    //
    // Init
    //


    private EasyNet() {
    }

    synchronized public static EasyNet getInstance() {
        EasyNet localInstance = config;
        if (localInstance == null) {
            synchronized (EasyNet.class) {
                localInstance = config;
                if (localInstance == null) {
                    config = localInstance = new EasyNet();
                }
            }
        }
        return localInstance;
    }


    //
    // Default builders
    //


    /**
     * Use this callback to set the basic properties for all queries.
     */
    public EasyNet setDefaultNBuilderListener(NBuilderDefaultListener defaultNBuilderListener) {
        this.defaultNBuilderListener = defaultNBuilderListener;
        return this;
    }

    public Request getDefaultNBuilder() {
        if (defaultNBuilderListener != null)
            this.nBuilder = defaultNBuilderListener.defaultConfig(Request.newInstance());
        return this.nBuilder;
    }

    public interface NBuilderDefaultListener {
        /**
         * Receives a empty Request instance that must a result return
         */
        Request defaultConfig(Request nBuilder);
    }


    //
    // Default behaviour
    //

    // --> Success

    /**
     * Define the basic logic of successful requests.
     *
     * @param onSuccessDefaultListener OnSuccessDefaultListener instance
     */
    public EasyNet setDefaultOnSuccessListener(OnSuccessDefaultListener onSuccessDefaultListener) {
        this.onSuccessDefaultListener = onSuccessDefaultListener;
        return this;
    }

    public OnSuccessDefaultListener getOnSuccessDefaultListener() {
        return onSuccessDefaultListener;
    }

    public interface OnSuccessDefaultListener {
        boolean onSuccess(NResponseModel responseModel);
    }

    // --> Failed


    /**
     * Define the basic logic of failed requests.
     *
     * @param onFailedDefaultListener OnFailedDefaultListener instance
     */
    public EasyNet setDefaultOnFailedListener(OnFailedDefaultListener onFailedDefaultListener) {
        this.onFailedDefaultListener = onFailedDefaultListener;
        return this;
    }

    public OnFailedDefaultListener getOnFailedDefaultListener() {
        return onFailedDefaultListener;
    }

    public interface OnFailedDefaultListener {
        boolean onFailed(NRequestModel nRequestModel, NErrors error);
    }

    // --> Errors


    public ArrayList<OnErrorDefaultListenerWithCode> getOnErrorDefaultListenersCollection() {
        return onErrorDefaultListenersCollection;
    }

    /**
     * Define the basic logic of error for status code.
     *
     * @param onErrorDefaultListener OnErrorDefaultListenerWithCode instance
     */
    public EasyNet addOnErrorDefaultListener(OnErrorDefaultListenerWithCode onErrorDefaultListener) {
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
    public EasyNet setOnErrorDefaultListener(OnErrorDefaultListener onErrorDefaultListener) {
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


    //
    // Tasks queue
    //

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


    //
    // Other
    //


    public static String[] getSupportedMethods() {
        return SUPPORTED_METHODS;
    }

    public boolean isWriteLogs() {
        return writeLogs;
    }

    /**
     * Set true, if you will want to show logs when library is working. True by default.
     */
    public EasyNet setWriteLogs(boolean writeLogs) {
        this.writeLogs = writeLogs;
        return this;
    }


    //
    // Request Maker
    //


    public static Request get() {
        return EasyNet.getInstance()
                .getDefaultNBuilder()
                .setMethod(GET);
    }

    public static Request post() {
        return EasyNet.getInstance()
                .getDefaultNBuilder()
                .setMethod(POST);
    }

    public static Request put() {
        return EasyNet.getInstance()
                .getDefaultNBuilder()
                .setMethod(PUT);
    }

    public static Request delete() {
        return EasyNet.getInstance()
                .getDefaultNBuilder()
                .setMethod(DELETE);
    }

    public static Request opt() {
        return EasyNet.getInstance()
                .getDefaultNBuilder()
                .setMethod(OPTIONS);
    }

    public static Request head() {
        return EasyNet.getInstance()
                .getDefaultNBuilder()
                .setMethod(HEAD);
    }

    public static Request multipart() {
        return EasyNet.getInstance()
                .getDefaultNBuilder()
                .setContentType(NConst.MIME_TYPE_MULTIPART_FORM_DATA);
    }

    public static Request get(String path, Object... params) {
        return get().setPath(path, params);
    }

    public static Request post(String path, Object... params) {
        return post().setPath(path, params);
    }

    public static Request put(String path, Object... params) {
        return put().setPath(path, params);
    }

    public static Request delete(String path, Object... params) {
        return delete().setPath(path, params);
    }

    public static Request opt(String path, Object... params) {
        return opt().setPath(path, params);
    }

    public static Request head(String path, Object... params) {
        return head().setPath(path, params);
    }

    public static Request multipart(String path, Object... params) {
        return multipart().setPath(path, params);
    }
}

