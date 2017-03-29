package pro.oncreate.easynet;

import android.app.Dialog;
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
import pro.oncreate.easynet.tasks.NBaseCallback;
import pro.oncreate.easynet.tasks.NCallback;
import pro.oncreate.easynet.tasks.NCallbackGson;
import pro.oncreate.easynet.tasks.NCallbackParse;
import pro.oncreate.easynet.tasks.NTask;
import pro.oncreate.easynet.utils.NLog;

import static pro.oncreate.easynet.methods.Method.DELETE;
import static pro.oncreate.easynet.methods.Method.GET;
import static pro.oncreate.easynet.methods.Method.HEAD;
import static pro.oncreate.easynet.methods.Method.OPTIONS;
import static pro.oncreate.easynet.methods.Method.POST;
import static pro.oncreate.easynet.methods.Method.PUT;


/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 * <p>
 * This class provides methods to create and customize http request
 */

@SuppressWarnings("unused,WeakerAccess")
public class NBuilder {


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
    private NTask.NTaskListener taskListener;


    //
    // Constructor and creator
    //


    private NBuilder() {
        requestModel = new NRequestModel();
        requestModel.setMethod(GET);
        requestModel.setNeedParse(false);
        requestModel.setEnablePagination(false);
        requestModel.setEnableDefaultListeners(true);
        requestModel.setConnectTimeout(NTask.DEFAULT_TIMEOUT_CONNECT);
        requestModel.setReadTimeout(NTask.DEFAULT_TIMEOUT_READ);
        setContentType(NConst.MIME_TYPE_X_WWW_FORM_URLENCODED);
    }

    static NBuilder newInstance() {
        return new NBuilder();
    }


    //
    // Build the request type and method
    //


    public static NBuilder create() {
        return NConfig.getInstance()
                .getDefaultNBuilder();
    }

    public static NBuilder create(String method) {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(method);
    }

    @Deprecated
    public static NBuilder get() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(GET);
    }

    @Deprecated
    public static NBuilder post() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(POST);
    }

    @Deprecated
    public static NBuilder put() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(PUT);
    }

    @Deprecated
    public static NBuilder delete() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(DELETE);
    }

    @Deprecated
    public static NBuilder opt() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(OPTIONS);
    }

    @Deprecated
    public static NBuilder head() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(HEAD);
    }

    @Deprecated
    public static NBuilder multipart() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setContentType(NConst.MIME_TYPE_MULTIPART_FORM_DATA);
    }

    /**
     * For multipart request default method - POST
     *
     * @param method the method of the request
     */
    public NBuilder setMethod(String method) {
        for (int i = 0; i < NConfig.getSupportedMethods().length; i++)
            if (method.equals(NConfig.getSupportedMethods()[i])) {
                requestModel.setMethod(Method.getMethodByName(method));
                return this;
            }
        requestModel.setMethod(GET);
        return this;
    }

    private NBuilder setMethod(Method method) {
        requestModel.setMethod(method);
        return this;
    }

    /**
     * You can use default constants (MIME types) in {@link NConst} class or other.
     *
     * @param contentType content type of request
     */
    public NBuilder setContentType(String contentType) {
        this.contentType = contentType;
        this.requestModel.setRequestType(contentType);
        return this;
    }

    /**
     * Get current Builder instance
     */
    public NBuilder instance() {
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
    public NBuilder setUrl(String url) {
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
    public NBuilder setUrl(String host, String path) {
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
    public NBuilder setUrl(String port, String host, String path) {
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
    public NBuilder setHost(String host) {
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
     * You must call {@link NBuilder#setHost(String)} before use this method.
     * Set the path of request URL.
     *
     * @param path example: path
     */
    public NBuilder setPath(String path) {
        if (requestModel.getUrl() == null || requestModel.getUrl().isEmpty())
            throw new NullPointerException("Host is empty");
        if (path == null || path.isEmpty())
            throw new NullPointerException("Path can not be empty");

        if (requestModel.getUrl().endsWith("/"))
            requestModel.setUrl(requestModel.getUrl() + path);
        return this;
    }

    /**
     * You must call {@link NBuilder#setHost(String)} before use this method.
     * Set the path of request URL with id.
     * Example: path/{id}
     *
     * @param path url path
     * @param id   essence id
     */
    public NBuilder setPath(String path, long id) {
        if (requestModel.getUrl() == null || requestModel.getUrl().isEmpty())
            throw new NullPointerException("Host is empty");
        if (path == null || path.isEmpty())
            throw new NullPointerException("Path can not be empty");

        if (requestModel.getUrl().endsWith("/"))
            requestModel.setUrl(requestModel.getUrl() + path + "/" + id);
        return this;
    }

    /**
     * You must call {@link NBuilder#setHost(String)} before use this method.
     * Example: path/{id}/{param}
     *
     * @param path  url path
     * @param id    essence id
     * @param param essence param
     */
    public NBuilder setPath(String path, long id, String param) {
        if (requestModel.getUrl() == null || requestModel.getUrl().isEmpty())
            throw new NullPointerException("Host is empty");
        if (path == null || path.isEmpty())
            throw new NullPointerException("Path can not be empty");

        if (requestModel.getUrl().endsWith("/"))
            requestModel.setUrl(requestModel.getUrl() + path + "/" + id + "/" + param);
        return this;
    }

    /**
     * You must call {@link NBuilder#setHost(String)} before use this method.
     * Example: path/{param1}/{param2}/{param3}
     *
     * @param path   url path
     * @param params params array
     */
    public NBuilder setPath(String path, String... params) {
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
     * You must call {@link NBuilder#setHost(String)} before use this method.
     * Example: path/{param1}/{param2}/{param3}
     *
     * @param path   url path
     * @param params params array
     */
    public NBuilder setPath(String path, Object... params) {
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
    public NBuilder setConnectTimeout(long mills) {
        if (mills > 0)
            requestModel.setConnectTimeout(mills);
        return this;
    }

    /**
     * @param mills read timeout in milliseconds
     */
    public NBuilder setReadTimeout(long mills) {
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
    public NBuilder addHeader(String key, String value) {
        requestModel.getHeaders().add(new NKeyValueModel(key, value));
        return this;
    }

    public NBuilder addParams(String arrayName, List<?> params) {
        if (params != null && params.size() > 0)
            for (int i = 0; i < params.size(); i++)
                if (params.get(i) instanceof File)
                    addParam(arrayName + String.format(Locale.getDefault(), "[%d]", i), (File) params.get(i));
                else
                    addParam(arrayName + String.format(Locale.getDefault(), "[%d]", i), params.get(i));

        return this;
    }

    public NBuilder addParams(String arrayName, Object... params) {
        if (params != null && params.length > 0)
            for (int i = 0; i < params.length; i++)
                if (params[i] instanceof File)
                    addParam(arrayName + String.format(Locale.getDefault(), "[%d]", i), (File) params[i]);
                else addParam(arrayName + String.format(Locale.getDefault(), "[%d]", i), params[i]);
        return this;
    }

    public NBuilder addParams(Map<?, ?> params) {
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
     * If the request method is not supported body (GET, DELETE), this collection will be added to the query params in URL ({@link NBuilder#addQueryParam} should be empty).
     *
     * @param key   param name
     * @param value param value
     */
    public NBuilder addParam(String key, String value) {
        requestModel.getParams().add(new NKeyValueModel(key, value));
        return this;
    }

    /**
     * Use only with {@link NConst#MIME_TYPE_X_WWW_FORM_URLENCODED} content type.
     * If the request method is supported body (POST, PUT), this collection will be added to the request body params.
     * If the request method is not supported body (GET, DELETE), this collection will be added to the query params in URL ({@link NBuilder#addQueryParam} should be empty).
     *
     * @param key   param name
     * @param value param value
     */
    public NBuilder addParam(String key, Object value) {
        requestModel.getParams().add(new NKeyValueModel(key, String.valueOf(value)));
        return this;
    }


    public NBuilder addParam(String key, Object value, boolean saveIfNull) {
        if (saveIfNull || value != null)
            requestModel.getParams().add(new NKeyValueModel(key, String.valueOf(value)));
        return this;
    }

    /**
     * Use this method with all content types. The method add the query param to the request URL.
     *
     * @param key   query param name
     * @param value query param value
     * @see NBuilder#addParam(String, String)
     */
    public NBuilder addQueryParam(String key, String value) {
        requestModel.getQueryParams().add(new NKeyValueModel(key, value));
        return this;
    }

    /**
     * Use this method with all content types. The method add the query param to the request URL.
     *
     * @param key   query param name
     * @param value query param value
     * @see NBuilder#addParam(String, String)
     */
    public NBuilder addQueryParam(String key, Object value) {
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
    public NBuilder addParam(String key, File file) {
        requestModel.getParamsFile().add(new NKeyValueFileModel(key, file));
        return this;
    }

    /**
     * Use only with {@link NConst#MIME_TYPE_MULTIPART_FORM_DATA} content type.
     * Method add chunk binary file to upload task.
     *
     * @param chunk binary file
     */
    public NBuilder addParam(File chunk) {
        requestModel.setChunk(chunk);
        return this;
    }

    /**
     * Use this method when the content type is not {@link NConst#MIME_TYPE_MULTIPART_FORM_DATA} and not {@link NConst#MIME_TYPE_X_WWW_FORM_URLENCODED}
     *
     * @param body the String representation of request body
     */
    public NBuilder setBody(String body) {
        requestModel.setBody(body);
        return this;
    }

    /**
     * This method clear all collections params (body, query, multipart params) from request builder.
     */
    public NBuilder clearParams() {
        requestModel.getParams().clear();
        requestModel.getQueryParams().clear();
        requestModel.getParamsFile().clear();
        return this;
    }

    /**
     * This method remove all headers from request builder.
     */
    public NBuilder clearHeaders() {
        requestModel.getHeaders().clear();
        return this;
    }


    //
    // Setting the default request listeners
    //


    /**
     * You can set default handlers by means of {@link NConfig} class. Default enabled = true;
     *
     * @param enabled enabled default listeners for this request
     */
    public NBuilder enableDefaultListeners(boolean enabled) {
        requestModel.setEnableDefaultListeners(enabled);
        return this;
    }


    //
    // Methods for processing progress views during request
    //


    public NBuilder bind(View view, BindParams params) {
        requestModel.addBindView(new BindView(view, params));
        return this;
    }


    /**
     * Set the progressDialog instance, which will be automatically start\dismiss in request lifecycle.
     *
     * @param progressDialog - progressDialog request indicator
     */
    public NBuilder bind(Dialog progressDialog) {
        requestModel.setProgressDialog(progressDialog);
        return this;
    }

    /**
     * Set the progressBar instance, which will be automatically show\hide in request lifecycle.
     *
     * @param progressBar - progressBar request indicator
     */
    public NBuilder bind(ProgressBar progressBar) {
        requestModel.setProgressBar(progressBar);
        return this;
    }

    /**
     * Set the other progress view instance, which will be automatically show\hide in request lifecycle.
     *
     * @param progressView progress view request indicator
     */
    public NBuilder bind(View progressView) {
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
    public NBuilder bind(SwipeRefreshLayout swipeRefreshLayout) {
        requestModel.setRefreshLayout(swipeRefreshLayout);
        return this;
    }

    /**
     * Set the SwipeRefreshLayout instance, which will be automatically start\dismiss in request lifecycle with content view.
     *
     * @param swipeRefreshLayout SwipeRefreshLayout request indicator
     * @param viewsToHide        content which must be hidden during the request
     */
    public NBuilder bind(SwipeRefreshLayout swipeRefreshLayout, View... viewsToHide) {
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
    public NBuilder bind(Dialog progressDialog, View... viewsToHide) {
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
    public NBuilder bind(ProgressBar progressBar, View... viewsToHide) {
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
    public NBuilder bind(View progressView, View... viewsToHide) {
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
    // Pagination
    //


    /**
     * Configure pagination with custom API keys.
     * Please, attend! You must call this method before using other pagination methods.
     *
     * @param pageNumberKey page number API key
     * @param itemsCountKey page count API key
     */
    public NBuilder configurePagination(String pageNumberKey, String itemsCountKey, String pageFromPrimaryKey) {
        requestModel.setPaginationModel(new NPaginationModel(pageNumberKey, itemsCountKey, pageFromPrimaryKey));
        return this;
    }

    /**
     * Enable pagination keys in this NBuilder instance.
     * You must call this method before using other pagination methods (setters).
     */
    public NBuilder enablePagination() {
        if (requestModel.getPaginationModel() == null)
            throw new NullPointerException("You must call configurePagination before");
        requestModel.setEnablePagination(true);
        return this;
    }

    /**
     * Enable pagination keys in this NBuilder instance with NPaginationInterface instance.
     * You must call this method before using other pagination methods.
     */
    public NBuilder enablePagination(NPaginationModel.NPaginationInterface paginationInterface) {
        if (requestModel.getPaginationModel() == null)
            throw new NullPointerException("You must call configurePagination before");
        requestModel.setEnablePagination(true);
        this.setPaginationInterface(paginationInterface);
        return this;
    }

    /**
     * Set items count on one page.
     *
     * @param count items count
     */
    public NBuilder setPaginationCount(int count) {
        if (requestModel.getPaginationModel() == null)
            throw new NullPointerException("You must call configurePagination before");
        requestModel.getPaginationModel().itemsCount = count;
        return this;
    }

    /**
     * Set page number for current request.
     *
     * @param pageNumber page number
     */
    public NBuilder setPaginationPage(int pageNumber) {
        if (requestModel.getPaginationModel() == null)
            throw new NullPointerException("You must call configurePagination before");
        requestModel.getPaginationModel().pageNumber = pageNumber;
        return this;
    }

    /**
     * Setting the next page with automatic calculation based on the current items number.
     *
     * @param itemsCountNow current items count
     */
    public NBuilder setPaginationNextPage(int itemsCountNow) {
        if (requestModel.getPaginationModel() == null)
            throw new NullPointerException("You must call configurePagination before");
        requestModel.getPaginationModel().pageNumber = NPaginationModel.calculateNextPage(itemsCountNow,
                requestModel.getPaginationModel().itemsCount);
        return this;
    }

    /**
     * The maximum number of primary key from which the page count.
     * Use this key in dynamic lists.
     *
     * @param pageFromPrimaryKey last primary key id
     */
    public NBuilder setPaginationPrimaryKey(long pageFromPrimaryKey) {
        if (requestModel.getPaginationModel() == null)
            throw new NullPointerException("You must call configurePagination before");
        requestModel.getPaginationModel().pageFromPK = pageFromPrimaryKey;
        return this;
    }

    /**
     * Use way to calculate pagination states in your adapters, model etc.
     * If you use this methods, you cannot use manual setters: {@link NBuilder#setPaginationPage}, {@link NBuilder#setPaginationPage},
     * {@link NBuilder#setPaginationNextPage}, {@link NBuilder#setPaginationPrimaryKey}
     *
     * @param paginationInterface pagination interface instance
     */
    public NBuilder setPaginationInterface(NPaginationModel.NPaginationInterface paginationInterface) {
        if (requestModel.getPaginationModel() == null)
            throw new NullPointerException("You must call configurePagination before");
        requestModel.getPaginationModel().setPaginationInterface(paginationInterface);
        return this;
    }

    /**
     * Add pagination data to request params.
     */
    private void setUpPagination() {
        if (!requestModel.isEnablePagination() || requestModel.getPaginationModel() == null)
            return;

        NPaginationModel paginationModel = requestModel.getPaginationModel();
        NPaginationModel.NPaginationInterface paginationInterface = requestModel
                .getPaginationModel().getPaginationInterface();

        if (paginationInterface == null) {
            if (paginationModel.itemsCount < 0)
                throw new NullPointerException("Items count cannot be less than zero");
            else addParam(paginationModel.countItemsKey,
                    String.valueOf(paginationModel.itemsCount));
            if (paginationModel.pageNumber >= 0)
                addParam(paginationModel.pageNumberKey,
                        String.valueOf(paginationModel.pageNumber));
            if (paginationModel.pageFromPK >= 0)
                addParam(paginationModel.pageFromPrimaryKey,
                        String.valueOf(paginationModel.pageFromPK));
        } else {
            if (paginationInterface.getPaginationPageCount() < 0)
                throw new NullPointerException("Items count cannot be less than zero");
            else addParam(paginationModel.countItemsKey,
                    String.valueOf(paginationInterface.getPaginationPageCount()));
            if (paginationInterface.getPaginationPageNumber() > NPaginationModel.NPaginationInterface.PAGE_NUMBER_NONE)
                addParam(paginationModel.pageNumberKey,
                        String.valueOf(paginationInterface.getPaginationPageNumber()));
            if (paginationInterface.getPaginationLastPrimaryKey() > NPaginationModel.NPaginationInterface.LAST_PRIMARY_KEY_NONE)
                addParam(paginationModel.pageFromPrimaryKey,
                        String.valueOf(paginationInterface.getPaginationLastPrimaryKey()));
        }
    }


    //
    // Start the request
    //


    /**
     * Use this method, if you want to take response together with the representation model.
     * The model must inherit {@link pro.oncreate.easynet.models.NBaseModel}, which organize parse algorithm.
     *
     * @param taskListener request lifecycle callback
     */
    @Deprecated
    public void startWithParse(NCallbackParse taskListener) {
        this.taskListener = taskListener;
        this.requestModel.setNeedParse(true);
        this.startTask();
    }

    /**
     * @param taskListener request lifecycle callback
     */
    @Deprecated
    public void startWithParse(NCallbackGson taskListener) {
        this.taskListener = taskListener;
        this.requestModel.setNeedParse(true);
        this.startTask();
    }

    /**
     * Use this method, if you want to take response without representation model.
     *
     * @param taskListener instance of callback
     */
    public void start(NCallback taskListener) {
        this.taskListener = taskListener;
        this.startTask();
    }

    /**
     * Use this method, if you want to take response without representation model.
     *
     * @param taskListener instance of callback
     */
    public void start(NBaseCallback taskListener) {
        if (taskListener instanceof NCallbackGson || taskListener instanceof NCallbackParse)
            this.requestModel.setNeedParse(true);
        this.taskListener = taskListener;
        this.startTask();
    }

    private void startTask() {
        if (validateRequest()) {
            addHeader(NConst.CONTENT_TYPE, contentType == null ? NConst.MIME_TYPE_X_WWW_FORM_URLENCODED : contentType);
            if (contentType == null)
                this.requestModel.setRequestType(NConst.MIME_TYPE_X_WWW_FORM_URLENCODED);
            setUpPagination();

            switch (contentType) {
                case NConst.MIME_TYPE_MULTIPART_FORM_DATA: {
                    requestModel.setMethod(POST);
                    break;
                }
            }
            new NTask(taskListener, requestModel).execute();
        }
    }

    /**
     * Execute request synchronously
     */
    public NResponseModel getSynchronously() throws ExecutionException, InterruptedException {
        if (validateRequest()) {
            addHeader(NConst.CONTENT_TYPE, contentType == null ? NConst.MIME_TYPE_X_WWW_FORM_URLENCODED : contentType);
            if (contentType == null)
                this.requestModel.setRequestType(NConst.MIME_TYPE_X_WWW_FORM_URLENCODED);
            setUpPagination();

            switch (contentType) {
                case NConst.MIME_TYPE_MULTIPART_FORM_DATA: {
                    requestModel.setMethod(POST);
                    break;
                }
            }
            return new NTask(taskListener, requestModel).execute().get();
        }
        return null;
    }

    /**
     * Use this method for intercepting header values. The callback will be called if the header is present.
     *
     * @param waitHeaderCallback header callback
     */
    public NBuilder waitHeader(NBaseCallback.WaitHeaderCallback waitHeaderCallback) {
        requestModel.addWaitHeaderCallbacks(waitHeaderCallback);
        return this;
    }


    //
    // Validate request data
    //


    private boolean validateRequest() {
        boolean valid = true;

        if (requestModel == null) {
            NLog.logE("Request model cannot be null");
            valid = false;
        }

        if (requestModel.getUrl() == null || requestModel.getUrl().isEmpty()) {
            NLog.logE("URL cannot be empty");
            valid = false;
        } else if (!Pattern.matches(Patterns.WEB_URL.pattern(), requestModel.getUrl())) {
            NLog.logE("Invalid URL");
            valid = false;
        }
        return valid;
    }


    //
    // Deprecated
    //

    /**
     * Now we recommend using {@link NCallbackParse}, which do simplify work with http request lifecycle.
     * {@link NTask.NTaskListener} is no longer used for the manual implementation of the client, but you can do if necessary.
     *
     * @see NBuilder#start(NCallback)
     * @see NBuilder#startWithParse(NCallbackParse)
     */
    @Deprecated
    public NBuilder setListener(NTask.NTaskListener listener) {
        this.taskListener = listener;
        return this;
    }
}