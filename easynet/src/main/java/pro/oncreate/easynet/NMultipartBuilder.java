package pro.oncreate.easynet;

import android.util.Patterns;

import java.io.File;
import java.util.regex.Pattern;

import pro.oncreate.easynet.models.NKeyValueFileModel;
import pro.oncreate.easynet.models.NKeyValueModel;
import pro.oncreate.easynet.models.NRequestMultipartModel;
import pro.oncreate.easynet.tasks.NTaskMultipart;
import pro.oncreate.easynet.utils.NLog;


/**
 * Created by andrej on 15.11.15.
 */
public class NMultipartBuilder {

    public static final String REQUEST_TYPE_MULTIPART_FORM_DATA = "multipart/form-data";

    private NRequestMultipartModel requestModel;
    private NTaskMultipart.NTaskListener taskListener;

    private NMultipartBuilder() {
        requestModel = new NRequestMultipartModel();
        requestModel.setRequestType(REQUEST_TYPE_MULTIPART_FORM_DATA);
        requestModel.setWriteLogs(true);
    }

    public static NMultipartBuilder create() {
        return new NMultipartBuilder();
    }

    public NMultipartBuilder setUrl(String url) {
        if (url.contains("://"))
            requestModel.setUrl(url);
        else requestModel.setUrl(NBuilder.DEFAULT_PORT + url);
        return this;
    }

    public NMultipartBuilder setUrl(String host, String path) {
        if (host.contains("://"))
            this.setUrl(host + "/" + path);
        else this.setUrl(NBuilder.DEFAULT_PORT + host + "/" + path);
        return this;
    }

    public NMultipartBuilder setUrl(String port, String host, String path) {
        if (port.equals(NBuilder.PORT_HTTP) || port.equals(NBuilder.PORT_HTTPS))
            this.setUrl(port + host + "/" + path);
        else this.setUrl(NBuilder.DEFAULT_PORT + host + "/" + path);
        return this;
    }

    public NMultipartBuilder addHeader(String key, String value) {
        requestModel.getHeaders().add(new NKeyValueModel(key, value));
        return this;
    }

    public NMultipartBuilder addTextParam(String key, String value) {
        requestModel.getParamsText().add(new NKeyValueModel(key, value));
        return this;
    }

    public NMultipartBuilder addFileParam(String key, File file) {
        requestModel.getParamsFile().add(new NKeyValueFileModel(key, file));
        return this;
    }

    public NMultipartBuilder clearParams() {
        requestModel.getParamsText().clear();
        requestModel.getParamsFile().clear();
        return this;
    }

    public NMultipartBuilder clearHeaders() {
        requestModel.getHeaders().clear();
        return this;
    }

    public NMultipartBuilder setListener(NTaskMultipart.NTaskListener listener) {
        this.taskListener = listener;
        return this;
    }

    public NMultipartBuilder writeLogs(boolean isWrite) {
        requestModel.setWriteLogs(isWrite);
        return this;
    }

    public void start() {
        if (validateRequest()) {
            NTaskMultipart task = new NTaskMultipart(taskListener, requestModel);
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

}
