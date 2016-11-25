package pro.oncreate.easynet;

import android.app.Dialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.util.regex.Pattern;

import pro.oncreate.easynet.data.NConst;
import pro.oncreate.easynet.models.subsidiary.NKeyValueFileModel;
import pro.oncreate.easynet.models.subsidiary.NKeyValueModel;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.tasks.NBaseCallback;
import pro.oncreate.easynet.tasks.NCallback;
import pro.oncreate.easynet.tasks.NCallbackParse;
import pro.oncreate.easynet.tasks.NTask;
import pro.oncreate.easynet.utils.NLog;

import static pro.oncreate.easynet.utils.NLog.ERROR_LISTENER_NULL;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 * <p>
 * This class provides methods to create and customize http request
 */
public class NBuilder {

    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    public static final String PORT_HTTP = "http://";
    public static final String PORT_HTTPS = "https://";
    public static final String PORT_FTP = "ftp://";
    public static final String PORT_FTP_SSL = "ftps://";
    public static final String PORT_FILE = "file://";
    public static final String PORT_DARA = "data:";
    private static final String DEFAULT_PORT = PORT_HTTP;

    private NRequestModel requestModel;
    private String contentType;
    private NTask.NTaskListener taskListener;

    private NBuilder() {
        requestModel = new NRequestModel();
        requestModel.setMethod(GET);
        requestModel.setNeedParse(false);
        requestModel.setEnableDefaultListeners(true);
        requestModel.setConnectTimeout(NTask.DEFAULT_TIMEOUT_CONNECT);
        requestModel.setReadTimeout(NTask.DEFAULT_TIMEOUT_READ);
        setContentType(NConst.MIME_TYPE_X_WWW_FORM_URLENCODED);
    }

    static NBuilder newInstance() {
        return new NBuilder();
    }

    public static NBuilder create() {
        return NConfig.getInstance()
                .getDefaultNBuilder();
    }

    public static NBuilder create(String method) {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(method);
    }

    public static NBuilder get() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(GET);
    }

    public static NBuilder post() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(POST);
    }

    public static NBuilder put() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(PUT);
    }

    public static NBuilder delete() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(DELETE);
    }

    public static NBuilder multipart() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setContentType(NConst.MIME_TYPE_MULTIPART_FORM_DATA);
    }

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
     * Set the path of request URL. You must call {@link NBuilder#setHost(String)} before use this method.
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
     * Set the path of request URL with id. You must call {@link NBuilder#setHost(String)} before use this method.
     * Example: path/{id}
     *
     * @param path url path
     * @param id   essence id
     */
    public NBuilder setPath(String path, int id) {
        if (requestModel.getUrl() == null || requestModel.getUrl().isEmpty())
            throw new NullPointerException("Host is empty");
        if (path == null || path.isEmpty())
            throw new NullPointerException("Path can not be empty");

        if (requestModel.getUrl().endsWith("/"))
            requestModel.setUrl(requestModel.getUrl() + path + "/" + id);
        return this;
    }

    /**
     * Set the path of request URL with id. You must call {@link NBuilder#setHost(String)} before use this method.
     * Example: path/{id}
     *
     * @param path  url path
     * @param id    essence id
     * @param param essence param
     */
    public NBuilder setPath(String path, int id, String param) {
        if (requestModel.getUrl() == null || requestModel.getUrl().isEmpty())
            throw new NullPointerException("Host is empty");
        if (path == null || path.isEmpty())
            throw new NullPointerException("Path can not be empty");

        if (requestModel.getUrl().endsWith("/"))
            requestModel.setUrl(requestModel.getUrl() + path + "/" + id + "/" + param);
        return this;
    }

    /**
     * Use only next constants: NBuilder.GET, NBuilder.POST, NBuilder.PUT, NBuilder.DELETE
     * For multipart request default method - POST
     *
     * @param method the method of the request
     */
    public NBuilder setMethod(String method) {
        if (method.equals(POST) || method.equals(GET) || method.equals(PUT) || method.equals(DELETE))
            requestModel.setMethod(method);
        else requestModel.setMethod(GET);
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
     * Add header (property) to the request
     *
     * @param key   header name
     * @param value header value
     */
    public NBuilder addHeader(String key, String value) {
        requestModel.getHeaders().add(new NKeyValueModel(key, value));
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
     * Use only with {@link NConst#MIME_TYPE_MULTIPART_FORM_DATA} content type.
     * Method add new text part to the multipart request.
     *
     * @param key   text param name
     * @param value text param value
     */
    public NBuilder addTextParam(String key, String value) {
        requestModel.getParamsText().add(new NKeyValueModel(key, value));
        return this;
    }

    /**
     * Use only with {@link NConst#MIME_TYPE_MULTIPART_FORM_DATA} content type.
     * Method add new file part to the multipart request.
     *
     * @param key  file name
     * @param file file param
     */
    public NBuilder addFileParam(String key, File file) {
        requestModel.getParamsFile().add(new NKeyValueFileModel(key, file));
        return this;
    }

    /**
     * This method clear all collections params (body, query, multipart params) from request builder.
     */
    public NBuilder clearParams() {
        requestModel.getParams().clear();
        requestModel.getQueryParams().clear();
        requestModel.getParamsFile().clear();
        requestModel.getParamsText().clear();
        return this;
    }

    /**
     * This method remove all headers from request builder.
     */
    public NBuilder clearHeaders() {
        requestModel.getHeaders().clear();
        return this;
    }

    /**
     * You can set default handlers by means of {@link NConfig} class. Default enabled = true;
     *
     * @param enabled enabled default listeners for this request
     */
    public NBuilder enableDefaultListeners(boolean enabled) {
        requestModel.setEnableDefaultListeners(enabled);
        return this;
    }

    /**
     * Set the progressDialog instance, which will be automatically start\dismiss in request lifecycle.
     *
     * @param progressDialog - progressDialog request indicator
     */
    public NBuilder bindProgress(Dialog progressDialog) {
        requestModel.setProgressDialog(progressDialog);
        return this;
    }

    /**
     * Set the progressBar instance, which will be automatically show\hide in request lifecycle.
     *
     * @param progressBar - progressBar request indicator
     */
    public NBuilder bindProgress(ProgressBar progressBar) {
        requestModel.setProgressBar(progressBar);
        return this;
    }

    /**
     * Set the other progress view instance, which will be automatically show\hide in request lifecycle.
     *
     * @param progressView progress view request indicator
     */
    public NBuilder bindProgress(View progressView) {
        requestModel.setProgressView(progressView);
        return this;
    }

    /**
     * Set the SwipeRefreshLayout instance, which will be automatically show\hide in request lifecycle.
     *
     * @param swipeRefreshLayout SwipeRefreshLayout request indicator
     */
    public NBuilder bindProgress(SwipeRefreshLayout swipeRefreshLayout) {
        requestModel.setRefreshLayout(swipeRefreshLayout);
        return this;
    }

    /**
     * Set the SwipeRefreshLayout instance, which will be automatically start\dismiss in request lifecycle with content view.
     *
     * @param swipeRefreshLayout SwipeRefreshLayout request indicator
     * @param hideView           content which must be hidden during the request
     */
    public NBuilder bindProgress(SwipeRefreshLayout swipeRefreshLayout, View hideView) {
        bindProgress(swipeRefreshLayout);
        requestModel.setHideView(hideView);
        return this;
    }

    /**
     * Set the progressDialog instance, which will be automatically start\dismiss in request lifecycle with content view.
     *
     * @param progressDialog progressDialog request indicator
     * @param hideView       content which must be hidden during the request
     */
    public NBuilder bindProgress(Dialog progressDialog, View hideView) {
        bindProgress(progressDialog);
        requestModel.setHideView(hideView);
        return this;
    }

    /**
     * Set the progressBar instance, which will be automatically show\hide in request lifecycle  with content view.
     *
     * @param progressBar - progressBar request indicator
     * @param hideView    - content which must be hidden during the request
     */
    public NBuilder bindProgress(ProgressBar progressBar, View hideView) {
        bindProgress(progressBar);
        requestModel.setHideView(hideView);
        return this;
    }

    /**
     * Set the other progress view instance, which will be automatically show\hide in request lifecycle with content view.
     *
     * @param progressView progress view request indicator
     * @param hideView     content which must be hidden during the request
     */
    public NBuilder bindProgress(View progressView, View hideView) {
        bindProgress(progressView);
        requestModel.setHideView(hideView);
        return this;
    }

    /**
     * Enable pagination with custom API keys. You must call this method before using other pagination methods.
     *
     * @param pageNumberKey page number API key
     * @param itemsCountKey page count API key
     */
    public NBuilder enablePagination(String pageNumberKey, String itemsCountKey) {
        requestModel.setPaginationModel(new NPaginationModel(pageNumberKey, itemsCountKey));
        return this;
    }

    /**
     * Set items count on one page.
     *
     * @param count items count
     */
    public NBuilder setPaginationCount(int count) {
        if (requestModel.getPaginationModel() == null)
            throw new NullPointerException("You must call enablePagination before");
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
            throw new NullPointerException("You must call enablePagination before");
        requestModel.getPaginationModel().pageNumber = pageNumber;
        return this;
    }

    /**
     * Setting the next page with automatic calculation based on the current items number
     *
     * @param itemsCountNow current items count
     */
    public NBuilder setPaginationNextPage(int itemsCountNow) {
        if (requestModel.getPaginationModel() == null)
            throw new NullPointerException("You must call enablePagination before");
        requestModel.getPaginationModel().pageNumber = NPaginationModel.calculateNextPage(itemsCountNow,
                requestModel.getPaginationModel().itemsCount);
        return this;
    }

    /**
     * Add pagination data to request params.
     */
    private void setUpPagination() {
        if (requestModel.getPaginationModel() != null) {
            addParam(requestModel.getPaginationModel().pageNumberKey,
                    String.valueOf(requestModel.getPaginationModel().pageNumber));
            addParam(requestModel.getPaginationModel().countItemsKey,
                    String.valueOf(requestModel.getPaginationModel().itemsCount));
        }
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

    /**
     * Use this method, if you want to take response together with the representation model.
     * The model must inherit {@link pro.oncreate.easynet.models.NBaseModel}, which organize parse algorithm.
     *
     * @param taskListener request lifecycle callback
     */
    public void startWithParse(NCallbackParse taskListener) {
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

    @Deprecated
    public void start() {
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
            NTask task = new NTask(taskListener, requestModel);
            task.execute();
        }
    }

    private boolean validateRequest() {
        boolean valid = true;

        if (requestModel == null) {
            NLog.logE(NLog.ERROR_REQUEST_NULL);
            valid = false;
        }

        if (requestModel.getUrl() == null || requestModel.getUrl().isEmpty()) {
            NLog.logE(NLog.ERROR_URL_EMPTY);
            valid = false;
        } else if (!Pattern.matches(Patterns.WEB_URL.pattern(), requestModel.getUrl())) {
            NLog.logE(NLog.ERROR_URL_INVALID);
            valid = false;
        }

        if (taskListener == null) {
            NLog.logE(NLog.ERROR_LISTENER_NULL);
            throw new NullPointerException(ERROR_LISTENER_NULL);
        }

        return valid;
    }
}