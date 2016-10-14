package pro.oncreate.easynet;

import android.util.Patterns;

import java.io.File;
import java.util.regex.Pattern;

import pro.oncreate.easynet.data.NConst;
import pro.oncreate.easynet.models.NKeyValueFileModel;
import pro.oncreate.easynet.models.NKeyValueModel;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.tasks.NCallback;
import pro.oncreate.easynet.tasks.NCallbackParse;
import pro.oncreate.easynet.tasks.NTask;
import pro.oncreate.easynet.utils.NLog;

import static pro.oncreate.easynet.utils.NLog.ERROR_LISTENER_NULL;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */
public class NBuilder {

    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    public static final String PORT_HTTP = "http://";
    public static final String PORT_HTTPS = "https://";
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
        return NConfig.getInstance().getDefaultNBuilder();
    }

    /**
     * Example: <b>http://example.com/path</b> or <b>example.com/path</b>
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
     * Make URL from two part <b>[http://example.com]/[path]</b>
     *
     * @param host example: </b>http://example.com</b>
     * @param path example: </b>path</b>
     */
    public NBuilder setUrl(String host, String path) {
        if (host.contains("://"))
            this.setUrl(host + "/" + path);
        else this.setUrl(DEFAULT_PORT + host + "/" + path);
        return this;
    }

    /**
     * Make URL from thee part <b>[http]://[example.com]/[path]</b>
     *
     * @param port example: <b>http</b>
     * @param host example: <b>example.com</b>
     * @param path example: <b>path</b>
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
     * @param host example: <b>http://example.com</b> or <b>example.com</b>
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
     * @param path example: <b>path</b>
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
     * Use only next constants: <b>NBuilder.GET</b>, <b>NBuilder.POST</b>, <b>NBuilder.PUT</b>, <b>NBuilder.DELETE</b>
     * For multipart request default method - <b>POST</b>
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