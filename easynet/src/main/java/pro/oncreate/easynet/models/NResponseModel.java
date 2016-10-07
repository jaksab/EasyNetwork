package pro.oncreate.easynet.models;

import java.util.List;
import java.util.Map;

/**
 * Created by andrej on 15.11.15.
 */
public class NResponseModel {

    public static final int STATUS_TYPE_SUCCESS = 1, STATUS_TYPE_ERROR = 2;

    private String url;
    private int statusCode;
    private String body;
    private Map<String, List<String>> headers;
    private long endTime;
    private int responseTime;

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
}
