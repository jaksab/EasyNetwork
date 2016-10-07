package pro.oncreate.easynet.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import pro.oncreate.easynet.http.Net;
import pro.oncreate.easynet.http.NetSettings;
import pro.oncreate.easynet.interfaces.ConnectionListener;
import pro.oncreate.easynet.models.ConnectInfo;
import pro.oncreate.easynet.models.ResponseModel;
import pro.oncreate.easynet.utils.RequestStringBuilders;

@Deprecated
public class NetMultipartTask extends AsyncTask<String, Void, ResponseModel> {

    // General
    private Net net;
    private Context context;
    private HttpPost httpPost;
    private ConnectionListener taskListener;

    // Data
    private boolean showDialog;
    private ProgressDialog progressDialog;

    private long timeStartRequest;


    public NetMultipartTask(Net net, HttpPost httpPost, ConnectionListener connectionListener) {
        this.net = net;
        this.httpPost = httpPost;
        this.context = net.getContext();
        this.showDialog = net.isProgressDialogEnable();
        this.taskListener = connectionListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (taskListener != null) {
            taskListener.onStartConnection(net.getURL());
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

            if (httpPost != null)
                netSettings.getResponse(httpPost, new PhotoUploadResponseHandler());

            return new ResponseModel(null, netSettings.getHttpClient(), null, net.getURL());
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

//        if (taskListener != null) {
//            ConnectInfo info = new ConnectInfo(net.getURL(), (int) ((System.currentTimeMillis() - timeStartRequest) / 1000), responseModel != null, responseModel.getDefaultHttpClient());
//            taskListener.onFinishConnection(info,
//                    responseModel.getReponse(), responseModel.getEntity());
//        }
    }

    public class PhotoUploadResponseHandler implements ResponseHandler<Object> {

        @Override
        public Object handleResponse(HttpResponse response) {

            if (response == null) {
                taskListener.onFinishConnection(new ConnectInfo(net.getURL(), (int) ((System.currentTimeMillis() - timeStartRequest) / 1000), false, null), null, null);
            } else {

                try {
                    HttpEntity r_entity = response.getEntity();
                    String responseString = EntityUtils.toString(r_entity);

                    if (taskListener != null) {
                        ConnectInfo info = new ConnectInfo(net.getURL(), (int) ((System.currentTimeMillis() - timeStartRequest) / 1000), response != null, null);

                        taskListener.onFinishConnection(info,
                                response, responseString);
                    }

                } catch (Exception e) {

                }

            }

            return null;
        }

    }
}

