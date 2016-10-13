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
    private ArrayList<NKeyValueModel> queryParams = new ArrayList<>();
    private ArrayList<NKeyValueFileModel> paramsFile = new ArrayList<>();
    private ArrayList<NKeyValueModel> paramsText = new ArrayList<>();
    private String body;
    private long connectTimeout;
    private long readTimeout;
    private long startTime;

    private boolean needParse;
    private boolean enableDefaultListeners;

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

    public ArrayList<NKeyValueFileModel> getParamsFile() {
        return paramsFile;
    }

    public void setParamsFile(ArrayList<NKeyValueFileModel> paramsFile) {
        this.paramsFile = paramsFile;
    }

    public ArrayList<NKeyValueModel> getParamsText() {
        return paramsText;
    }

    public void setParamsText(ArrayList<NKeyValueModel> paramsText) {
        this.paramsText = paramsText;
    }

    public ArrayList<NKeyValueModel> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(ArrayList<NKeyValueModel> queryParams) {
        this.queryParams = queryParams;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
