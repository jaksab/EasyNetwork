package pro.oncreate.easynet.models;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;

@Deprecated
public class ResponseModel {

    private HttpResponse reponse;
    private DefaultHttpClient defaultHttpClient;
    private String entity;
    private String url;

    public ResponseModel() {
    }

    public ResponseModel(HttpResponse reponse, DefaultHttpClient defaultHttpClient, String entity, String url) {
        this.reponse = reponse;
        this.defaultHttpClient = defaultHttpClient;
        this.entity = entity;
        this.url = url;
    }

    public HttpResponse getReponse() {
        return reponse;
    }

    public void setReponse(HttpResponse reponse) {
        this.reponse = reponse;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DefaultHttpClient getDefaultHttpClient() {
        return defaultHttpClient;
    }

    public void setDefaultHttpClient(DefaultHttpClient defaultHttpClient) {
        this.defaultHttpClient = defaultHttpClient;
    }
}