package pro.oncreate.easynet.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.util.EntityUtils;

import java.net.URI;

import pro.oncreate.easynet.http.Net;
import pro.oncreate.easynet.http.NetSettings;
import pro.oncreate.easynet.interfaces.ConnectionListener;
import pro.oncreate.easynet.models.subsidiary.ConnectInfo;
import pro.oncreate.easynet.models.ResponseModel;
import pro.oncreate.easynet.utils.RequestStringBuilders;

@Deprecated
public class NetTask extends AsyncTask<String, Void, ResponseModel> {

    // General
    private Net net;
    private Context context;
    private ConnectionListener taskListener;

    private HttpGet httpGet;
    private HttpPost httpPost;
    private HttpPut httpPut;
    private HttpDelete httpDelete;

    // Data
    private String currentUrl;
    private boolean showDialog;
    private ProgressDialog progressDialog;

    private long timeStartRequest;


    public NetTask(Net net, ConnectionListener taskListener, URI url) {
        this.net = net;
        this.context = net.getContext();
        this.taskListener = taskListener;
        this.showDialog = net.isProgressDialogEnable();

        try {
            currentUrl = url.toString();
        } catch (Exception e) {
        }
    }

    public NetTask(Net net, HttpGet get,
                   ConnectionListener taskListener) {
        this(net, taskListener, get.getURI());
        this.httpGet = get;
    }


    public NetTask(Net net, HttpDelete delete,
                   ConnectionListener taskListener) {
        this(net, taskListener, delete.getURI());
        this.httpDelete = delete;
    }


    public NetTask(Net net, HttpPost post,
                   ConnectionListener taskListener) {
        this(net, taskListener, post.getURI());
        this.httpPost = post;
    }


    public NetTask(Net net, HttpPut put,
                   ConnectionListener taskListener) {
        this(net, taskListener, put.getURI());
        this.httpPut = put;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (taskListener != null) {
            taskListener.onStartConnection(currentUrl);
        }

        if (showDialog) {
            try {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage(net.getDialogMessage().equals(RequestStringBuilders.EMPTY_STRING) ? RequestStringBuilders.DEFAULT_DIALOG_LOADING_MESSAGE : net.getDialogMessage());
                progressDialog.show();
            } catch (Exception e) {

            }
        }
    }

    @Override
    protected ResponseModel doInBackground(String... params) {
        try {
            timeStartRequest = System.currentTimeMillis();

            NetSettings netSettings = NetSettings.getInstance(net);
            HttpResponse response = null;
            if (httpPost != null)
                response = netSettings.getResponse(httpPost);
            else if (httpGet != null)
                response = netSettings.getResponse(httpGet);
            else if (httpPut != null)
                response = netSettings.getResponse(httpPut);
            else if (httpDelete != null)
                response = netSettings.getResponse(httpDelete);

            String entityString = null;
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null)
                    entityString = EntityUtils.toString(entity);
            } catch (Exception e) {
            }

            return new ResponseModel(response, netSettings.getHttpClient(), entityString, currentUrl);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(ResponseModel responseModel) {
        super.onPostExecute(responseModel);

        try {
            if (progressDialog != null) {
                progressDialog.cancel();
            }
        } catch (Exception e) {
        }

        if (taskListener != null) {
            ConnectInfo info = new ConnectInfo(currentUrl, (int) ((System.currentTimeMillis() - timeStartRequest) / 1000), responseModel != null, responseModel.getDefaultHttpClient());
            taskListener.onFinishConnection(info,
                    responseModel.getReponse(), responseModel.getEntity());
        }
    }
}