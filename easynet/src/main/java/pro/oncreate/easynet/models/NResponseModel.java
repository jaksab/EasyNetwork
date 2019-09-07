package pro.oncreate.easynet.models;

import java.util.List;
import java.util.Map;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused,WeakerAccess")
public class NResponseModel {

    public static final int STATUS_TYPE_SUCCESS = 1, STATUS_TYPE_ERROR = 2;

    private String url;
    private int statusCode;
    private String body;
    private Map<String, List<String>> headers;
    private long endTime;
    private int responseTime;
    private boolean redirectInterrupted;
    private String redirectLocation;
    private boolean fromCache;
    private NRequestModel requestModel;

    public NResponseModel(String url, int statusCode, String body, Map<String, List<String>> headers) {
        this.url = url;
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long time) {
        this.endTime = time;
    }

    public int statusType() {
        return statusCode / 100 == 2 ? STATUS_TYPE_SUCCESS : STATUS_TYPE_ERROR;
    }

    public boolean isRedirectInterrupted() {
        return redirectInterrupted;
    }

    public void setRedirectInterrupted(boolean redirectInterrupted) {
        this.redirectInterrupted = redirectInterrupted;
    }

    public String getRedirectLocation() {
        return redirectLocation;
    }

    public void setRedirectLocation(String redirectLocation) {
        this.redirectLocation = redirectLocation;
    }

    public boolean isFromCache() {
        return fromCache;
    }

    public void setFromCache(boolean fromCache) {
        this.fromCache = fromCache;
    }

    public NRequestModel getRequestModel() {
        return requestModel;
    }

    public void setRequestModel(NRequestModel requestModel) {
        this.requestModel = requestModel;
    }
}
