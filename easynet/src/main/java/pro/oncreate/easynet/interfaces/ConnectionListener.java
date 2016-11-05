package pro.oncreate.easynet.interfaces;

import org.apache.http.HttpResponse;

import pro.oncreate.easynet.models.subsidiary.ConnectInfo;


@Deprecated
public interface ConnectionListener {

    public void onStartConnection(String requestUrl);

    public void onFinishConnection(ConnectInfo info, HttpResponse response,
                                   String entity);
}