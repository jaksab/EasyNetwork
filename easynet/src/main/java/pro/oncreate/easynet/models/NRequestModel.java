package pro.oncreate.easynet.models;

import java.util.ArrayList;

/**
 * Created by andrej on 15.11.15.
 */
public class NRequestModel {

    private String url;
    private String method;
    private String requestType;
    private ArrayList<NKeyValueModel> headers = new ArrayList<>();
    private ArrayList<NKeyValueModel> params = new ArrayList<>();
    private long connectTimeout;
    private long readTimeout;

    private boolean writeLogs;
    private boolean needParse;
    private boolean enableDefaultListeners;

    private long startTime;

    public NRequestModel() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public ArrayList<NKeyValueModel> getHeaders() {
        return headers;
    }

    public void setHeaders(ArrayList<NKeyValueModel> headers) {
        this.headers = headers;
    }

    public ArrayList<NKeyValueModel> getParams() {
        return params;
    }

    public void setParams(ArrayList<NKeyValueModel> params) {
        this.params = params;
    }

    public boolean isWriteLogs() {
        return writeLogs;
    }

    public void setWriteLogs(boolean writeLogs) {
        this.writeLogs = writeLogs;
    }

    public boolean isNeedParse() {
        return needParse;
    }

    public void setNeedParse(boolean needParse) {
        this.needParse = needParse;
    }

    public boolean isEnableDefaultListeners() {
        return enableDefaultListeners;
    }

    public void setEnableDefaultListeners(boolean enableDefaultListeners) {
        this.enableDefaultListeners = enableDefaultListeners;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }
}
