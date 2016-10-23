package pro.oncreate.easynet.models;

import android.app.Dialog;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */
public class NRequestModel {

    // Request general
    private String url;
    private String method;
    private String requestType;

    // Request data
    private ArrayList<NKeyValueModel> headers;
    private ArrayList<NKeyValueModel> params;
    private ArrayList<NKeyValueModel> queryParams;
    private ArrayList<NKeyValueFileModel> paramsFile;
    private ArrayList<NKeyValueModel> paramsText;
    private String body;

    // States
    private long connectTimeout;
    private long readTimeout;
    private long startTime;

    // Parse
    private boolean needParse;

    // Listeners
    private boolean enableDefaultListeners;

    // Progress
    private Dialog progressDialog;
    private ProgressBar progressBar;
    private View progressView;
    private View hideView;

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
        if (headers == null)
            headers = new ArrayList<>();
        return headers;
    }

    public void setHeaders(ArrayList<NKeyValueModel> headers) {
        this.headers = headers;
    }

    public ArrayList<NKeyValueModel> getParams() {
        if (params == null)
            params = new ArrayList<>();
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
        if (paramsFile == null)
            paramsFile = new ArrayList<>();
        return paramsFile;
    }

    public void setParamsFile(ArrayList<NKeyValueFileModel> paramsFile) {
        this.paramsFile = paramsFile;
    }

    public ArrayList<NKeyValueModel> getParamsText() {
        if (paramsText == null)
            paramsText = new ArrayList<>();
        return paramsText;
    }

    public void setParamsText(ArrayList<NKeyValueModel> paramsText) {
        this.paramsText = paramsText;
    }

    public ArrayList<NKeyValueModel> getQueryParams() {
        if (queryParams == null)
            queryParams = new ArrayList<>();
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

    public Dialog getProgressDialog() {
        return progressDialog;
    }

    public void setProgressDialog(Dialog progressDialog) {
        this.progressDialog = progressDialog;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public View getProgressView() {
        return progressView;
    }

    public void setProgressView(View progressView) {
        this.progressView = progressView;
    }

    public View getHideView() {
        return hideView;
    }

    public void setHideView(View hideView) {
        this.hideView = hideView;
    }
}
