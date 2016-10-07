package pro.oncreate.easynet.models;

import java.util.ArrayList;

/**
 * Created by andrej on 15.11.15.
 */
public class NRequestMultipartModel {

    private String url;
    private String method;
    private String requestType;
    private ArrayList<NKeyValueModel> headers = new ArrayList<>();
    private ArrayList<NKeyValueFileModel> paramsFile = new ArrayList<>();
    private ArrayList<NKeyValueModel> paramsText = new ArrayList<>();
    private boolean writeLogs;

    public NRequestMultipartModel() {
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

    public boolean isWriteLogs() {
        return writeLogs;
    }

    public void setWriteLogs(boolean writeLogs) {
        this.writeLogs = writeLogs;
    }
}
