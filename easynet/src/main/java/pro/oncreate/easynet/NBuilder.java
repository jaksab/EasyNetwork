package pro.oncreate.easynet;

import android.util.Patterns;

import java.io.File;
import java.util.regex.Pattern;

import pro.oncreate.easynet.models.NKeyValueFileModel;
import pro.oncreate.easynet.models.NKeyValueModel;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.tasks.NTask;
import pro.oncreate.easynet.utils.NLog;

/**
 * Created by andrej on 15.11.15.
 */
public class NBuilder {

    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    private static final String PORT_HTTP = "http://";
    private static final String PORT_HTTPS = "https://";
    private static final String DEFAULT_PORT = PORT_HTTP;

    private NRequestModel requestModel;
    private String contentType;
    private NTask.NTaskListener taskListener;

    private NBuilder() {
        requestModel = new NRequestModel();
        requestModel.setMethod(GET);
        requestModel.setNeedParse(true);
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

    public NBuilder setUrl(String url) {
        if (url.contains("://"))
            requestModel.setUrl(url);
        else requestModel.setUrl(DEFAULT_PORT + url);
        return this;
    }

    public NBuilder setUrl(String host, String path) {
        if (host.contains("://"))
            this.setUrl(host + "/" + path);
        else this.setUrl(DEFAULT_PORT + host + "/" + path);
        return this;
    }

    public NBuilder setUrl(String port, String host, String path) {
        if (port.equals(PORT_HTTP) || port.equals(PORT_HTTPS))
            this.setUrl(port + host + "/" + path);
        else this.setUrl(DEFAULT_PORT + host + "/" + path);
        return this;
    }

    public NBuilder setHost(String host) {
        if (!host.endsWith("/"))
            host += "/";

        if (host.contains("://"))
            requestModel.setUrl(host);
        else requestModel.setUrl(DEFAULT_PORT + host);

        return this;
    }

    public NBuilder setPath(String path) {
        if (path != null && !path.isEmpty() && requestModel.getUrl() != null && !requestModel.getUrl().isEmpty() && requestModel.getUrl().endsWith("/"))
            requestModel.setUrl(requestModel.getUrl() + path);
        return this;
    }

    public NBuilder setMethod(String method) {
        if (method.equals(POST) || method.equals(GET) || method.equals(PUT) || method.equals(DELETE))
            requestModel.setMethod(method);
        else requestModel.setMethod(GET);
        return this;
    }

    public NBuilder setContentType(String contentType) {
        this.contentType = contentType;
        this.requestModel.setRequestType(contentType);
        return this;
    }

    public NBuilder setConnectTimeout(long mills) {
        if (mills > 0)
            requestModel.setConnectTimeout(mills);
        return this;
    }

    public NBuilder setReadTimeout(long mills) {
        if (mills > 0)
            requestModel.setReadTimeout(mills);
        return this;
    }

    public NBuilder setBody(String body) {
        requestModel.setBody(body);
        return this;
    }

    public NBuilder addHeader(String key, String value) {
        requestModel.getHeaders().add(new NKeyValueModel(key, value));
        return this;
    }

    public NBuilder addParam(String key, String value) {
        requestModel.getParams().add(new NKeyValueModel(key, value));
        return this;
    }

    public NBuilder addTextParam(String key, String value) {
        requestModel.getParamsText().add(new NKeyValueModel(key, value));
        return this;
    }

    public NBuilder addFileParam(String key, File file) {
        requestModel.getParamsFile().add(new NKeyValueFileModel(key, file));
        return this;
    }

    public NBuilder clearParams() {
        requestModel.getParams().clear();
        return this;
    }

    public NBuilder clearHeaders() {
        requestModel.getHeaders().clear();
        return this;
    }

    public NBuilder setListener(NTask.NTaskListener listener) {
        this.taskListener = listener;
        return this;
    }

    public NBuilder setNeedParse(boolean isNeed) {
        requestModel.setNeedParse(isNeed);
        return this;
    }

    public NBuilder enableDefaultListeners(boolean enabled) {
        requestModel.setEnableDefaultListeners(enabled);
        return this;
    }

    public void start() {
        if (validateRequest()) {
            addHeader(NConst.CONTENT_TYPE, contentType == null ? NConst.MIME_TYPE_X_WWW_FORM_URLENCODED : contentType);

            if (contentType == null) {
                this.requestModel.setRequestType(NConst.MIME_TYPE_X_WWW_FORM_URLENCODED);
            }

            switch (contentType) {
                case NConst.MIME_TYPE_X_WWW_FORM_URLENCODED: {
                    NTask task = new NTask(taskListener, requestModel);
                    task.execute();
                    break;
                }
                case NConst.MIME_TYPE_MULTIPART_FORM_DATA: {
                    requestModel.setMethod(POST);
                    NTask task = new NTask(taskListener, requestModel);
                    task.execute();
                    break;
                }
                default:
                    // TODO: make request with raw body
                    break;
            }
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
        return valid;
    }
}