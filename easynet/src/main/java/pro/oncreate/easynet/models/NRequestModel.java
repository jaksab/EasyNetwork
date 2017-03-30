package pro.oncreate.easynet.models;

import android.app.Dialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pro.oncreate.easynet.NPaginationModel;
import pro.oncreate.easynet.methods.Method;
import pro.oncreate.easynet.models.subsidiary.BindView;
import pro.oncreate.easynet.models.subsidiary.NKeyValueFileModel;
import pro.oncreate.easynet.models.subsidiary.NKeyValueModel;
import pro.oncreate.easynet.tasks.NBaseCallback;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused")
public class NRequestModel {

    // Request general
    private String url;
    private Method method;
    private String requestType;

    // Request data
    private ArrayList<NKeyValueModel> headers;
    private ArrayList<NKeyValueModel> params;
    private ArrayList<NKeyValueModel> queryParams;
    private ArrayList<NKeyValueFileModel> paramsFile;
    private String body;
    private File chunk;
    private NPaginationModel paginationModel;

    // Etc
    private long connectTimeout;
    private long readTimeout;
    private long startTime;
    private boolean needParse;
    private boolean enablePagination;
    private boolean enableManualRedirect;
    private boolean enableDefaultListeners;
    private ArrayList<NBaseCallback.WaitHeaderCallback> waitHeaderCallbacks;

    // Progress
    private List<BindView> bindViews;
    private Dialog progressDialog;
    private ProgressBar progressBar;
    private View progressView;
    private SwipeRefreshLayout refreshLayout;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
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

    public NPaginationModel getPaginationModel() {
        return paginationModel;
    }

    public void setPaginationModel(NPaginationModel paginationModel) {
        this.paginationModel = paginationModel;
    }

    public boolean isEnablePagination() {
        return enablePagination;
    }

    public void setEnablePagination(boolean enablePagination) {
        this.enablePagination = enablePagination;
    }

    public File getChunk() {
        return chunk;
    }

    public void setChunk(File chunk) {
        this.chunk = chunk;
    }

    public ArrayList<NBaseCallback.WaitHeaderCallback> getWaitHeaderCallbacks() {
        return waitHeaderCallbacks;
    }

    public void addWaitHeaderCallbacks(NBaseCallback.WaitHeaderCallback waitHeaderCallback) {
        if (this.waitHeaderCallbacks == null)
            this.waitHeaderCallbacks = new ArrayList<>();
        this.waitHeaderCallbacks.add(waitHeaderCallback);
    }

    public boolean isEnableManualRedirect() {
        return enableManualRedirect;
    }

    public void setEnableManualRedirect(boolean enableManuadlRedirect) {
        this.enableManualRedirect = enableManuadlRedirect;
    }

    //
    //
    //


    public List<BindView> getBindViews() {
        return bindViews;
    }

    public void setBindViews(List<BindView> bindViews) {
        this.bindViews = bindViews;
    }

    public void addBindView(BindView bindView) {
        if (this.bindViews == null)
            this.bindViews = new ArrayList<>();
        bindViews.add(bindView);
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

    public SwipeRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    public void setRefreshLayout(SwipeRefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
    }

    public void clearParams() {
        if (this.queryParams != null)
            this.queryParams.clear();
        if (this.params != null)
            this.params.clear();
        if (this.paramsFile != null)
            this.paramsFile.clear();
        this.body = null;
    }


}
