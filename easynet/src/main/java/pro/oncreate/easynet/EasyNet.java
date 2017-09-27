package pro.oncreate.easynet;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pro.oncreate.easynet.data.NConst;
import pro.oncreate.easynet.data.NErrors;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.processing.BaseTask;
import pro.oncreate.easynet.utils.NHelper;

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

    public static final String CACHE_DIR_NAME = "easy-network-cache";

    volatile private static EasyNet config;
    private boolean writeLogs = BuildConfig.DEBUG;
    private Request request;
    private RequestDefaultListener defaultRequestListener;

    private OnSuccessDefaultListener onSuccessDefaultListener;
    private OnFailedDefaultListener onFailedDefaultListener;
    private ArrayList<OnErrorDefaultListenerWithCode> onErrorDefaultListenersCollection = new ArrayList<>();
    private OnErrorDefaultListener onErrorDefaultListener;

    private Map<String, BaseTask> taskQueue;

    private static final String[] SUPPORTED_METHODS = {
            GET, POST, PUT, DELETE, OPTIONS, HEAD
    };

    private boolean cacheEnabled = false;
    private File cacheDir;
    private int maxCacheItems = 50;

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
    // Default request
    //


    /**
     * Use this callback to set the basic properties for all queries.
     */
    public EasyNet setDefaultRequestListener(RequestDefaultListener requestDefaultListener) {
        this.defaultRequestListener = requestDefaultListener;
        return this;
    }

    public Request getDefaultRequestInstance() {
        if (defaultRequestListener != null)
            this.request = defaultRequestListener.defaultConfig(Request.newInstance());
        return this.request;
    }

    public interface RequestDefaultListener {
        /**
         * Receives a empty Request instance that must a result return
         */
        Request defaultConfig(Request request);
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
        boolean onFailed(NRequestModel requestModel, NErrors error);
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
    public void addTask(String tag, BaseTask task) {
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
     * @return true - if task is present and has been removed.
     */
    public boolean removeTask(String tag) {
        return taskQueue.remove(tag) != null;
    }

    /**
     * Cancel all current request execution.
     *
     * @return count of removed items
     */
    public int cancelAllTasks() {
        try {
            int countRemoved = 0;
            if (taskQueue != null) {
                for (BaseTask task : taskQueue.values()) {
                    task.cancel(false);
                    countRemoved++;
                }
            }
            return countRemoved;
        } catch (Exception e) {
            return 0;
        }
    }

    //
    // Getters and setters
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
    // Cache
    //

    /**
     * @param cacheDir      recommend Context#getCacheDir()
     * @param maxCacheItems 50 by default
     */
    public EasyNet enableCache(File cacheDir, int maxCacheItems) {
        setCacheDir(cacheDir);
        setMaxCacheItems(maxCacheItems);
        return this;
    }

    /**
     * @param cacheDir recommend Context$getCacheDir()
     */
    public EasyNet enableCache(File cacheDir) {
        setCacheDir(cacheDir);
        return this;
    }

    EasyNet setCacheDir(File cacheDir) {
        this.cacheDir = cacheDir;
        return this;
    }

    EasyNet setMaxCacheItems(int maxCacheItems) {
        this.maxCacheItems = maxCacheItems;
        return this;
    }

    public int getMaxCacheItems() {
        return maxCacheItems;
    }

    public File getCacheDir() {
        return this.cacheDir;
    }

    public File getCacheFile(String name) throws IOException {
        try {
            if (!getCacheDir().exists())
                //noinspection ResultOfMethodCallIgnored
                getCacheDir().mkdirs();
        } catch (Exception ignored) {
        }
        return new File(getCacheDir(), name + ".tmp");
    }

    public void clearCache() {
        try {
            if (cacheDir != null && cacheDir.isDirectory()) deleteDir(cacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            boolean result = true;
            String[] children = dir.list();
            for (String aChildren : children) {
                File child = new File(dir, aChildren);
                if (child.isDirectory())
                    deleteDir(child);
                else result = result && child.delete();
            }
            return result;
        }
        return false;
    }


    //
    // Request Maker
    //


    public static Request get() {
        return EasyNet.getInstance()
                .getDefaultRequestInstance()
                .setMethod(GET);
    }

    public static Request post() {
        return EasyNet.getInstance()
                .getDefaultRequestInstance()
                .setMethod(POST);
    }

    public static Request put() {
        return EasyNet.getInstance()
                .getDefaultRequestInstance()
                .setMethod(PUT);
    }

    public static Request delete() {
        return EasyNet.getInstance()
                .getDefaultRequestInstance()
                .setMethod(DELETE);
    }

    public static Request opt() {
        return EasyNet.getInstance()
                .getDefaultRequestInstance()
                .setMethod(OPTIONS);
    }

    public static Request head() {
        return EasyNet.getInstance()
                .getDefaultRequestInstance()
                .setMethod(HEAD);
    }

    public static Request multipart() {
        return EasyNet.getInstance()
                .getDefaultRequestInstance()
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


    //
    // Bonus
    //


    public static boolean isActiveInternet(Context context) {
        return NHelper.isActiveInternet(context);
    }

}

