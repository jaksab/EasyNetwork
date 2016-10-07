package pro.oncreate.easynet.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import pro.oncreate.easynet.tasks.NetMultipartTask;


@Deprecated
public class NetSettings {

    static private NetSettings networkService;

    private DefaultHttpClient defaultHttpClient;
    private Net net;


    public DefaultHttpClient getDefaultHttpClient() {
        return defaultHttpClient;
    }

    public Net getNet() {
        return net;
    }

    public void setNet(Net net) {
        this.net = net;
    }

    private NetSettings(Net net) {
        this.setNet(net);
        defaultHttpClient = getHttpClient();
    }

    static public NetSettings getInstance(Net net) {
        networkService = new NetSettings(net);
        return networkService;
    }

    static public boolean isInternetEnabled(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            if (nInfo == null || !nInfo.isConnected())
                return false;
            else
                return true;
        } catch (Exception e) {
            return false;
        }
    }

    public DefaultHttpClient getHttpClient() {
        if (defaultHttpClient != null)
            return defaultHttpClient;
        else {
            try {
                HttpParams httpParams = new BasicHttpParams();

                HttpConnectionParams.setConnectionTimeout(httpParams, 20000);
                HttpConnectionParams.setSoTimeout(httpParams, 20000);
                HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
                HttpProtocolParams.setUserAgent(httpParams, net.getUserAgent());

//                SchemeRegistry registry = new SchemeRegistry();
//                registry.register(new Scheme("http", PlainSocketFactory
//                        .getSocketFactory(), 80));
//
//				ClientConnectionManager clientConnectionManager = new ThreadSafeClientConnManager(
//						httpParams, registry);

                defaultHttpClient = new DefaultHttpClient(httpParams);
                defaultHttpClient.setCookieStore(new BasicCookieStore());
            } catch (Exception e) {
                defaultHttpClient = new DefaultHttpClient();
                defaultHttpClient.setCookieStore(new BasicCookieStore());
            }
        }
        return defaultHttpClient;
    }


    public HttpResponse getResponse(HttpPost httpPost) {
        HttpResponse response = null;
        try {
            response = getHttpClient().execute(httpPost);
        } catch (Exception e) {
        }
        return response;
    }


    public HttpResponse getResponse(HttpPut httpPut) {
        HttpResponse response = null;
        try {
            response = getHttpClient().execute(httpPut);
        } catch (Exception e) {
        }
        return response;
    }


    public HttpResponse getResponse(HttpGet httpGet) {
        HttpResponse response = null;
        try {
            response = getHttpClient().execute(httpGet);
        } catch (Exception e) {
        }
        return response;
    }


    public HttpResponse getResponse(HttpDelete httpDelete) {
        HttpResponse response = null;
        try {
            response = getHttpClient().execute(httpDelete);
        } catch (Exception e) {
        }
        return response;
    }

    public void getResponse(HttpPost httpPost, NetMultipartTask.PhotoUploadResponseHandler photoUploadResponseHandler) {
        try {
            getHttpClient().execute(httpPost, photoUploadResponseHandler);
        } catch (Exception e) {
        }
    }
}
