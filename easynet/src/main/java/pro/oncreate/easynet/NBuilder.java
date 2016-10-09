package pro.oncreate.easynet;

import android.util.Patterns;

import java.util.regex.Pattern;

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

    public static final String PORT_HTTP = "http://";
    public static final String PORT_HTTPS = "https://";
    public static final String DEFAULT_PORT = PORT_HTTP;

    public static final String REQUEST_TYPE_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded;charset=UTF-8";

    private NRequestModel requestModel;
    private NTask.NTaskListener taskListener;

    private NBuilder() {
        requestModel = new NRequestModel();
        requestModel.setRequestType(REQUEST_TYPE_X_WWW_FORM_URLENCODED);
        requestModel.setMethod(GET);
        requestModel.setWriteLogs(true);
        requestModel.setNeedParse(true);
        requestModel.setEnableDefaultListeners(true);
        requestModel.setConnectTimeout(NTask.DEFAULT_TIMEOUT_CONNECT);
        requestModel.setReadTimeout(NTask.DEFAULT_TIMEOUT_READ);
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

    public NBuilder addHeader(String key, String value) {
        requestModel.getHeaders().add(new NKeyValueModel(key, value));
        return this;
    }

    public NBuilder addParam(String key, String value) {
        requestModel.getParams().add(new NKeyValueModel(key, value));
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

    public NBuilder writeLogs(boolean isWrite) {
        requestModel.setWriteLogs(isWrite);
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
        return valid;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
