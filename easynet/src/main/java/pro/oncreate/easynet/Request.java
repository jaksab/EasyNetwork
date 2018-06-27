package pro.oncreate.easynet;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.annotation.RawRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import pro.oncreate.easynet.data.NConst;
import pro.oncreate.easynet.methods.Method;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.models.subsidiary.BindParams;
import pro.oncreate.easynet.models.subsidiary.BindView;
import pro.oncreate.easynet.models.subsidiary.NKeyValueFileModel;
import pro.oncreate.easynet.models.subsidiary.NKeyValueModel;
import pro.oncreate.easynet.processing.BaseTask;
import pro.oncreate.easynet.processing.JSONTask;
import pro.oncreate.easynet.processing.MultipartTask;
import pro.oncreate.easynet.processing.NBaseCallback;
import pro.oncreate.easynet.processing.NCallback;
import pro.oncreate.easynet.processing.NCallbackGson;
import pro.oncreate.easynet.processing.NCallbackParse;
import pro.oncreate.easynet.processing.RawTask;
import pro.oncreate.easynet.processing.TestTask;
import pro.oncreate.easynet.processing.UrlencodedTask;

import static pro.oncreate.easynet.methods.Method.GET;
import static pro.oncreate.easynet.methods.Method.POST;
import static pro.oncreate.easynet.processing.BaseTask.DEFAULT_TIMEOUT_CONNECT;
import static pro.oncreate.easynet.processing.BaseTask.DEFAULT_TIMEOUT_READ;


/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 * <p>
 * This class provides methods to create and customize http request
 */

@SuppressWarnings("unused,WeakerAccess")
public class Request {


    //
    // Constants
    //


    public static final String PORT_HTTP = "http://";
    public static final String PORT_HTTPS = "https://";
    public static final String PORT_FTP = "ftp://";
    public static final String PORT_FTP_SSL = "ftps://";
    public static final String PORT_FILE = "file://";
    public static final String PORT_DARA = "data:";
    private static final String DEFAULT_PORT = PORT_HTTP;


    //
    // States and data
    //


    private NRequestModel requestModel;
    private String contentType;
    private boolean parallelExecution = false;
    private BaseTask.NTaskListener taskListener;


    //
    // Constructor and creator
    //


    private Request() {
        requestModel = new NRequestModel();
        requestModel.setMethod(GET);
        requestModel.setNeedParse(false);
        requestModel.setEnablePagination(false);
        requestModel.setEnableDefaultListeners(true);
        requestModel.setConnectTimeout(DEFAULT_TIMEOUT_CONNECT);
        requestModel.setReadTimeout(DEFAULT_TIMEOUT_READ);
        setContentType(NConst.MIME_TYPE_X_WWW_FORM_URLENCODED);
    }

    public static Request newInstance() {
        return new Request();
    }


    /**
     * For multipart request default method - POST
     *
     * @param method the method of the request
     */
    public Request setMethod(String method) {
        for (int i = 0; i < EasyNet.getSupportedMethods().length; i++)
            if (method.equals(EasyNet.getSupportedMethods()[i])) {
                requestModel.setMethod(Method.getMethodByName(method));
                return this;
            }
        requestModel.setMethod(GET);
        return this;
    }

    private Request setMethod(Method method) {
        requestModel.setMethod(method);
        return this;
    }

    /**
     * You can use default constants (MIME types) in {@link NConst} class or other.
     *
     * @param contentType content type of request
     */
    public Request setContentType(String contentType) {
        this.contentType = contentType;
        this.requestModel.setRequestType(contentType);
        return this;
    }

    /**
     * Get current Builder instance
     */
    public Request instance() {
        return this;
    }

    //
    // Set URL components
    //


    /**
     * Example: http://example.com/path or example.com/path
     *
     * @param url correct url address
     */
    public Request setUrl(String url) {
        if (url.contains("://"))
            requestModel.setUrl(url);
        else requestModel.setUrl(DEFAULT_PORT + url);
        return this;
    }

    /**
     * Make URL from two part [http://example.com]/[path]
     *
     * @param host example: http://example.com
     * @param path example: path
     */
    public Request setUrl(String host, String path) {
        if (host.contains("://"))
            this.setUrl(host + "/" + path);
        else this.setUrl(DEFAULT_PORT + host + "/" + path);
        return this;
    }

    /**
     * Make URL from thee part [http]://[example.com]/[path]
     *
     * @param port example: http
     * @param host example: example.com
     * @param path example: path
     */
    public Request setUrl(String port, String host, String path) {
        if (port != null && !port.isEmpty())
            this.setUrl(port + "://" + host + "/" + path);
        else throw new NullPointerException("Port can not be empty");
        return this;
    }

    /**
     * Set the host of request URL.
     *
     * @param host example: http://example.com or example.com
     */
    public Request setHost(String host) {
        if (host == null || host.isEmpty())
            throw new NullPointerException("Host can not be empty");

        if (!host.endsWith("/"))
            host += "/";
        if (host.contains("://"))
            requestModel.setUrl(host);
        else requestModel.setUrl(DEFAULT_PORT + host);
        return this;
    }

    /**
     * You must call {@link Request#setHost(String)} before use this method.
     * Set the path of request URL.
     *
     * @param path example: path
     */
    public Request setPath(String path) {
        if (requestModel.getUrl() == null || requestModel.getUrl().isEmpty())
            throw new NullPointerException("Host is empty");
        if (path == null || path.isEmpty())
            throw new NullPointerException("Path can not be empty");

        if (requestModel.getUrl().endsWith("/"))
            requestModel.setUrl(requestModel.getUrl() + path);
        return this;
    }

    /**
     * You must call {@link Request#setHost(String)} before use this method.
     * Set the path of request URL with id.
     * Example: path/{id}
     *
     * @param path url path
     * @param id   essence id
     */
    public Request setPath(String path, long id) {
        if (requestModel.getUrl() == null || requestModel.getUrl().isEmpty())
            throw new NullPointerException("Host is empty");
        if (path == null || path.isEmpty())
            throw new NullPointerException("Path can not be empty");

        if (requestModel.getUrl().endsWith("/"))
            requestModel.setUrl(requestModel.getUrl() + path + "/" + id);
        return this;
    }

    /**
     * You must call {@link Request#setHost(String)} before use this method.
     * Example: path/{id}/{param}
     *
     * @param path  url path
     * @param id    essence id
     * @param param essence param
     */
    public Request setPath(String path, long id, String param) {
        if (requestModel.getUrl() == null || requestModel.getUrl().isEmpty())
            throw new NullPointerException("Host is empty");
        if (path == null || path.isEmpty())
            throw new NullPointerException("Path can not be empty");

        if (requestModel.getUrl().endsWith("/"))
            requestModel.setUrl(requestModel.getUrl() + path + "/" + id + "/" + param);
        return this;
    }

    /**
     * You must call {@link Request#setHost(String)} before use this method.
     * Example: path/{param1}/{param2}/{param3}
     *
     * @param path   url path
     * @param params params array
     */
    public Request setPath(String path, String... params) {
        if (requestModel.getUrl() == null || requestModel.getUrl().isEmpty())
            throw new NullPointerException("Host is empty");
        if (path == null || path.isEmpty())
            throw new NullPointerException("Path can not be empty");
        if (params == null)
            throw new NullPointerException("Params can not be null");

        if (requestModel.getUrl().endsWith("/")) {
            StringBuilder paramsString = new StringBuilder();
            paramsString.append(requestModel.getUrl()).append(path).append("/");
            for (String param : params) paramsString.append(param).append("/");
            paramsString.deleteCharAt(paramsString.length() - 1);
            requestModel.setUrl(paramsString.toString());
        }
        return this;
    }

    /**
     * You must call {@link Request#setHost(String)} before use this method.
     * Example: path/{param1}/{param2}/{param3}
     *
     * @param path   url path
     * @param params params array
     */
    public Request setPath(String path, Object... params) {
        if (requestModel.getUrl() == null || requestModel.getUrl().isEmpty())
            throw new NullPointerException("Host is empty");
        if (path == null || path.isEmpty())
            throw new NullPointerException("Path can not be empty");
        if (params == null)
            throw new NullPointerException("Params can not be null");

        if (requestModel.getUrl().endsWith("/")) {
            StringBuilder paramsString = new StringBuilder();
            paramsString.append(requestModel.getUrl()).append(path).append("/");
            for (Object param : params) paramsString.append(String.valueOf(param)).append("/");
            paramsString.deleteCharAt(paramsString.length() - 1);
            requestModel.setUrl(paramsString.toString());
        }
        return this;
    }


    //
    // Connection settings
    //


    /**
     * @param mills connection timeout in milliseconds
     */
    public Request setConnectTimeout(long mills) {
        if (mills > 0)
            requestModel.setConnectTimeout(mills);
        return this;
    }

    /**
     * @param mills read timeout in milliseconds
     */
    public Request setReadTimeout(long mills) {
        if (mills > 0)
            requestModel.setReadTimeout(mills);
        return this;
    }


    //
    // Methods to work with headers, body and query params for any kind of request
    //


    /**
     * Add header (property) to the request
     *
     * @param key   header name
     * @param value header value
     */
    public Request addHeader(String key, String value) {
        requestModel.getHeaders().add(new NKeyValueModel(key, value));
        return this;
    }

    public Request addParams(String arrayName, List<?> params) {
        if (params != null && params.size() > 0)
            for (int i = 0; i < params.size(); i++)
                if (params.get(i) instanceof File)
                    addParam(arrayName + String.format(Locale.getDefault(), "[%d]", i), (File) params.get(i));
                else
                    addParam(arrayName + String.format(Locale.getDefault(), "[%d]", i), params.get(i));

        return this;
    }

    public Request addParams(String arrayName, Object... params) {
        if (params != null && params.length > 0)
            for (int i = 0; i < params.length; i++)
                if (params[i] instanceof File)
                    addParam(arrayName + String.format(Locale.getDefault(), "[%d]", i), (File) params[i]);
                else addParam(arrayName + String.format(Locale.getDefault(), "[%d]", i), params[i]);
        return this;
    }

    public Request addParams(Map<?, ?> params) {
        if (params != null && !params.isEmpty())
            for (Map.Entry<?, ?> entry : params.entrySet()) {
                if (entry.getValue() instanceof File)
                    addParam(String.valueOf(entry.getKey()), (File) entry.getValue());
                else addParam(String.valueOf(entry.getKey()), entry.getValue());
            }
        return this;
    }

    /**
     * Use only with {@link NConst#MIME_TYPE_X_WWW_FORM_URLENCODED} content type.
     * If the request method is supported body (POST, PUT), this collection will be added to the request body params.
     * If the request method is not supported body (GET, DELETE), this collection will be added to the query params in URL ({@link Request#addQueryParam} should be empty).
     *
     * @param key   param name
     * @param value param value
     */
    public Request addParam(String key, String value) {
        requestModel.getParams().add(new NKeyValueModel(key, value));
        return this;
    }

    /**
     * Use only with {@link NConst#MIME_TYPE_X_WWW_FORM_URLENCODED} content type.
     * If the request method is supported body (POST, PUT), this collection will be added to the request body params.
     * If the request method is not supported body (GET, DELETE), this collection will be added to the query params in URL ({@link Request#addQueryParam} should be empty).
     *
     * @param key   param name
     * @param value param value
     */
    public Request addParam(String key, Object value) {
        requestModel.getParams().add(new NKeyValueModel(key, String.valueOf(value)));
        return this;
    }


    public Request addParam(String key, Object value, boolean saveIfNull) {
        if (saveIfNull || value != null)
            addParam(key, value);
        return this;
    }

    public Request addParam(String key, File value, boolean saveIfNull) {
        if (saveIfNull || value != null)
            addParam(key, value);
        return this;
    }

    /**
     * Use this method with all content types. The method add the query param to the request URL.
     *
     * @param key   query param name
     * @param value query param value
     * @see Request#addParam(String, String)
     */
    public Request addQueryParam(String key, String value) {
        requestModel.getQueryParams().add(new NKeyValueModel(key, value));
        return this;
    }

    public Request addQueryParam(String key, String value, boolean saveIfNull) {
        if (saveIfNull || value != null)
            requestModel.getQueryParams().add(new NKeyValueModel(key, value));
        return this;
    }

    /**
     * Use this method with all content types. The method add the query param to the request URL.
     *
     * @param key   query param name
     * @param value query param value
     * @see Request#addParam(String, String)
     */
    public Request addQueryParam(String key, Object value) {
        requestModel.getQueryParams().add(new NKeyValueModel(key, String.valueOf(value)));
        return this;
    }

    public Request addQueryParam(String key, Object value, boolean saveIfNull) {
        if (saveIfNull || value != null)
            requestModel.getQueryParams().add(new NKeyValueModel(key, String.valueOf(value)));
        return this;
    }

    /**
     * Use only with {@link NConst#MIME_TYPE_MULTIPART_FORM_DATA} content type.
     * Method add new file part to the multipart request.
     *
     * @param key  file name
     * @param file file param
     */
    public Request addParam(String key, File file) {
        requestModel.getParamsFile().add(new NKeyValueFileModel(key, file));
        return this;
    }

    /**
     * Use only with {@link NConst#MIME_TYPE_MULTIPART_FORM_DATA} content type.
     * Method add chunk binary file to upload task.
     *
     * @param chunk binary file
     */
    public Request addParam(File chunk) {
        requestModel.setChunk(chunk);
        return this;
    }

    /**
     * Use this method when the content type is not {@link NConst#MIME_TYPE_MULTIPART_FORM_DATA} and not {@link NConst#MIME_TYPE_X_WWW_FORM_URLENCODED}
     *
     * @param body the String representation of request body
     */
    public Request setBody(String body) {
        requestModel.setBody(body);
        return this;
    }

    /**
     * This method clear all collections params (body, query, multipart params) from request builder.
     */
    public Request clearParams() {
        requestModel.getParams().clear();
        requestModel.getQueryParams().clear();
        requestModel.getParamsFile().clear();
        return this;
    }

    /**
     * This method remove all headers from request builder.
     */
    public Request clearHeaders() {
        requestModel.getHeaders().clear();
        return this;
    }

    /**
     * Use test requests with some data from raw resource file. Use delay for emulate server response.
     */
    public Request test(@RawRes int rawResId, Resources resources, long delayMills) {
        setContentType(NConst.MIME_TYPE_TEST);
        requestModel.setTestRaw(rawResId);
        requestModel.setResources(resources);
        requestModel.setTestDelayMills(delayMills);
        return this;
    }


    //
    // Setting the default request listeners
    //


    /**
     * You can set default handlers by means of {@link EasyNet} class. Default enabled = true;
     *
     * @param enabled enabled default listeners for this request
     */
    public Request enableDefaultListeners(boolean enabled) {
        requestModel.setEnableDefaultListeners(enabled);
        return this;
    }

    /**
     * Redirect from http to https and from https to http always true.
     */
    public Request manualRedirect(boolean enabled) {
        requestModel.setEnableManualRedirect(enabled);
        return this;
    }


    //
    // Methods for processing progress views during request
    //


    public Request bind(View view, BindParams params) {
        requestModel.addBindView(new BindView(view, params));
        return this;
    }


    /**
     * Set the progressDialog instance, which will be automatically start\dismiss in request lifecycle.
     *
     * @param progressDialog - progressDialog request indicator
     */
    public Request bind(Dialog progressDialog) {
        requestModel.setProgressDialog(progressDialog);
        return this;
    }

    /**
     * Set the progressBar instance, which will be automatically show\hide in request lifecycle.
     *
     * @param progressBar - progressBar request indicator
     */
    public Request bind(ProgressBar progressBar) {
        requestModel.setProgressBar(progressBar);
        return this;
    }

    /**
     * Set the other progress view instance, which will be automatically show\hide in request lifecycle.
     *
     * @param progressView progress view request indicator
     */
    public Request bind(View progressView) {
        if (progressView instanceof SwipeRefreshLayout)
            requestModel.setRefreshLayout((SwipeRefreshLayout) progressView);
        else if (progressView instanceof ProgressBar)
            requestModel.setProgressBar((ProgressBar) progressView);
        else requestModel.setProgressView(progressView);
        return this;
    }

    /**
     * Set the SwipeRefreshLayout instance, which will be automatically show\hide in request lifecycle.
     *
     * @param swipeRefreshLayout SwipeRefreshLayout request indicator
     */
    public Request bind(SwipeRefreshLayout swipeRefreshLayout) {
        requestModel.setRefreshLayout(swipeRefreshLayout);
        return this;
    }

    /**
     * Set the SwipeRefreshLayout instance, which will be automatically start\dismiss in request lifecycle with content view.
     *
     * @param swipeRefreshLayout SwipeRefreshLayout request indicator
     * @param viewsToHide        content which must be hidden during the request
     */
    public Request bind(SwipeRefreshLayout swipeRefreshLayout, View... viewsToHide) {
        bind(swipeRefreshLayout);
        setViewsToHide(viewsToHide);
        return this;
    }

    /**
     * Set the progressDialog instance, which will be automatically start\dismiss in request lifecycle with content view.
     *
     * @param progressDialog progressDialog request indicator
     * @param viewsToHide    content which must be hidden during the request
     */
    public Request bind(Dialog progressDialog, View... viewsToHide) {
        bind(progressDialog);
        setViewsToHide(viewsToHide);
        return this;
    }

    /**
     * Set the progressBar instance, which will be automatically show\hide in request lifecycle  with content view.
     *
     * @param progressBar - progressBar request indicator
     * @param viewsToHide - content which must be hidden during the request
     */
    public Request bind(ProgressBar progressBar, View... viewsToHide) {
        bind(progressBar);
        setViewsToHide(viewsToHide);
        return this;
    }

    /**
     * Set the other progress view instance, which will be automatically show\hide in request lifecycle with content view.
     *
     * @param progressView progress view request indicator
     * @param viewsToHide  content which must be hidden during the request
     */
    public Request bind(View progressView, View... viewsToHide) {
        bind(progressView);
        setViewsToHide(viewsToHide);
        return this;
    }

    private void setViewsToHide(View... viewsToHide) {
        if (viewsToHide != null) {
            for (View aViewsToHide : viewsToHide)
                bind(aViewsToHide, new BindParams(BindParams.Type.HIDE_AND_SHOW_AFTER));
        }
    }


    //
    // New Pagination
    //


    /**
     * Configure pagination with custom API keys.
     * Please, attend! You must call this method before using other pagination methods.
     */
    public Request configurePagination(String... keys) {
        requestModel.setPaginationModel(new PaginationModel(keys));
        return this;
    }

    /**
     * Enable pagination keys in this Request instance with NPaginationInterface instance.
     * You must call this method before using other pagination methods.
     */
    public Request enablePagination(PaginationModel.PaginationInterface paginationInterface) {
        if (requestModel.getPaginationModel() == null)
            throw new NullPointerException("You must call configurePagination before");
        requestModel.setEnablePagination(true);
        this.setPaginationInterface(paginationInterface);
        return this;
    }

    /**
     * Use way to calculate pagination states in your adapters, model etc.
     *
     * @param paginationInterface pagination interface instance
     */
    Request setPaginationInterface(PaginationModel.PaginationInterface paginationInterface) {
        if (requestModel.getPaginationModel() == null)
            throw new NullPointerException("You must call configurePagination before");
        requestModel.getPaginationModel().setPaginationInterface(paginationInterface);
        return this;
    }

    /**
     * Add pagination data to request query params.
     */
    private void setupPagination() {
        if (!requestModel.isEnablePagination() || requestModel.getPaginationModel() == null)
            return;
        for (String key : requestModel.getPaginationModel().getData().keySet()) {
            Integer value = requestModel.getPaginationModel().getPaginationInterface().getPaginationValue(key);
            if (value != null)
                addParam(key, value);
        }
    }


    //
    // Cache
    //

    /**
     * Call this method, if you want to save response data to cache.
     */
    public Request cacheResponse() {
        this.requestModel.setCacheResponse(true);
        return this;
    }

    /**
     * Select suitable cache options from BaseTask#CacheOptions enum.
     * setCacheResponse enabled by default.
     */
    public Request setCacheOptions(BaseTask.CacheOptions cacheOptions) {
        this.requestModel.setCacheOptions(cacheOptions);
        this.requestModel.setCacheResponse(true);
        return this;
    }

    /**
     * @see Request#setCacheOptions(BaseTask.CacheOptions)
     * cacheWithParams affects on cache file name, if for the same query it is important to consider different caches for different parameters, you must set true value.
     * setCacheResponse enabled by default.
     */
    public Request setCacheOptions(BaseTask.CacheOptions cacheOptions, boolean cacheWithParams) {
        this.requestModel.setCacheOptions(cacheOptions);
        this.requestModel.setCacheWithParams(cacheWithParams);
        this.requestModel.setCacheResponse(true);
        return this;
    }

    /**
     * @see Request#setCacheOptions(BaseTask.CacheOptions, boolean)
     * Use cacheResponse param, to influence the need to save a query response.
     */
    public Request setCacheOptions(BaseTask.CacheOptions cacheOptions, boolean cacheWithParams,
                                   boolean cacheResponse) {
        this.requestModel.setCacheOptions(cacheOptions);
        this.requestModel.setCacheWithParams(cacheWithParams);
        this.requestModel.setCacheResponse(cacheResponse);
        return this;
    }

    //
    // Start the request
    //

    /**
     * Start without callback (ignore response).
     *
     * @return String - tag (task id)
     */
    public String start() {
        this.requestModel.setTag("task#" + requestModel.getStartTime());
        this.startTask();
        return this.requestModel.getTag();
    }

    /**
     * Use this method, if you want to take response without representation model.
     *
     * @param taskListener instance of callback
     * @return String - tag (task id)
     */
    public String start(NCallback taskListener) {
        this.taskListener = taskListener;
        this.requestModel.setTag("task#" + requestModel.getStartTime());
        this.startTask();
        return this.requestModel.getTag();
    }

    /**
     * Use this method, if you want to take response together with the representation model.
     * The model must inherit {@link pro.oncreate.easynet.models.NBaseModel}, which organize parse algorithm.
     *
     * @param taskListener request lifecycle callback
     * @return String - tag (task id)
     */
    public String start(NCallbackParse taskListener) {
        this.requestModel.setNeedParse(true);
        this.taskListener = taskListener;
        this.requestModel.setTag("task#" + requestModel.getStartTime());
        this.startTask();
        return this.requestModel.getTag();
    }

    /**
     * Use model auto parsing functional with Gson library.
     *
     * @param taskListener request lifecycle callback
     * @return String - tag (task id)
     */
    public String start(NCallbackGson taskListener) {
        this.requestModel.setNeedParse(true);
        this.taskListener = taskListener;
        this.requestModel.setTag("task#" + requestModel.getStartTime());
        this.startTask();
        return this.requestModel.getTag();
    }


    /**
     * @param cacheOptions select data source: network, cache, etc
     * @see Request#start(NCallback)
     */
    public String start(BaseTask.CacheOptions cacheOptions, NCallback taskListener) {
        setCacheOptions(cacheOptions);
        return start(taskListener);
    }

    /**
     * @param cacheOptions select data source: network, cache, etc
     * @see Request#start(NCallbackParse)
     */
    public String start(BaseTask.CacheOptions cacheOptions, NCallbackParse taskListener) {
        setCacheOptions(cacheOptions);
        return start(taskListener);
    }

    /**
     * @param cacheOptions select data source: network, cache, etc
     * @see Request#start(NCallbackGson)
     */
    public String start(BaseTask.CacheOptions cacheOptions, NCallbackGson taskListener) {
        setCacheOptions(cacheOptions);
        return start(taskListener);
    }

    private void startTask() {
        if (validateRequest()) {
            if (parallelExecution)
                makeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                makeTask().execute();
        }
    }

    /**
     * Execute request synchronously (UI thread). Parallel execution always false.
     */
    public NResponseModel getSynchronously() throws ExecutionException, InterruptedException {
        if (validateRequest())
            return makeTask().execute().get();
        else return null;
    }

    /**
     * Use this method for intercepting header values. The callback will be called if the header is present.
     *
     * @param waitHeaderCallback header callback
     */
    public Request waitHeader(NBaseCallback.WaitHeaderCallback waitHeaderCallback) {
        requestModel.addWaitHeaderCallbacks(waitHeaderCallback);
        return this;
    }

    /**
     * This method sets the primary data and selects the appropriate instance of BaseTask class heirs.
     */
    private BaseTask makeTask() {
        if (contentType == null)
            setContentType(NConst.MIME_TYPE_X_WWW_FORM_URLENCODED);
        else if (contentType.equals(NConst.MIME_TYPE_MULTIPART_FORM_DATA))
            requestModel.setMethod(POST);

        addHeader(NConst.CONTENT_TYPE, contentType);
        setupPagination();

        if (requestModel.getCacheOptions() == null)
            requestModel.setCacheOptions(BaseTask.CacheOptions.NETWORK_ONLY);

        BaseTask task;
        switch (requestModel.getRequestType()) {
            case NConst.MIME_TYPE_X_WWW_FORM_URLENCODED:
                task = new UrlencodedTask(taskListener, requestModel);
                break;
            case NConst.MIME_TYPE_MULTIPART_FORM_DATA:
                task = new MultipartTask(taskListener, requestModel);
                break;
            case NConst.MIME_TYPE_JSON:
                task = new JSONTask(taskListener, requestModel);
                break;
            case NConst.MIME_TYPE_TEST:
                task = new TestTask(taskListener, requestModel);
                break;
            default:
                task = new RawTask(taskListener, requestModel);
                break;
        }
        return task;
    }

    public Request parallelExecution() {
        this.parallelExecution = true;
        return this;
    }

    //
    // Validate request data
    //


    private boolean validateRequest() {
        if (requestModel == null)
            throw new NullPointerException("Request model cannot be null");

        if (requestModel.getUrl() == null || requestModel.getUrl().isEmpty()) {
            throw new NullPointerException("URL cannot be empty");
        } else if (!Pattern.matches(Patterns.WEB_URL.pattern(), requestModel.getUrl())) {
            throw new NullPointerException("Invalid URL");
        }
        return true;
    }


    //
    // Deprecated
    //

    /**
     * Now we recommend using {@link NCallbackParse}, which do simplify work with http request lifecycle.
     * {@link BaseTask.NTaskListener} is no longer used for the manual implementation of the client, but you can do if necessary.
     *
     * @see Request#start(NCallback)
     */
    @Deprecated
    public Request setListener(BaseTask.NTaskListener listener) {
        this.taskListener = listener;
        return this;
    }
}