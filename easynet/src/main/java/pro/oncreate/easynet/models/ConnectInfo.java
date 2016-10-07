package pro.oncreate.easynet.models;

import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by Konovalenko A., onCreate team on 03.08.2015.
 */
@Deprecated
public class ConnectInfo {

    private DefaultHttpClient defaultHttpClient;
    private String connectUrl;
    private int connectTime;
    private boolean isConnectSuccessful;

    public ConnectInfo(String connectUrl, int connectTime, boolean isConnectSuccessful, DefaultHttpClient defaultHttpClient) {
        this.defaultHttpClient = defaultHttpClient;
        this.connectUrl = connectUrl;
        this.connectTime = connectTime;
        this.isConnectSuccessful = isConnectSuccessful;
    }

    public boolean isConnectSuccessful() {
        return isConnectSuccessful;
    }

    public void setIsConnectSuccessful(boolean isConnectSuccessful) {
        this.isConnectSuccessful = isConnectSuccessful;
    }

    public String getUrl() {
        return connectUrl;
    }

    public void setUrl(String url) {
        this.connectUrl = url;
    }

    public int getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(int connectTime) {
        this.connectTime = connectTime;
    }

    public DefaultHttpClient getDefaultHttpClient() {
        return defaultHttpClient;
    }

    public void setDefaultHttpClient(DefaultHttpClient defaultHttpClient) {
        this.defaultHttpClient = defaultHttpClient;
    }
}
