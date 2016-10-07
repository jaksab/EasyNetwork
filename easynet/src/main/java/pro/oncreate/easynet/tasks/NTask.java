package pro.oncreate.easynet.tasks;

import android.os.AsyncTask;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import pro.oncreate.easynet.NBuilder;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.utils.NDataBuilder;
import pro.oncreate.easynet.utils.NLog;


/**
 * Created by andrej on 15.11.15.
 */
public class NTask extends AsyncTask<String, Void, NResponseModel> {

    public final static int DEFAULT_TIMEOUT_READ = 10000;
    public final static int DEFAULT_TIMEOUT_CONNECT = 7500;

    private NTask.NTaskListener listener;
    private NRequestModel requestModel;

    public NTask(NTask.NTaskListener listener, NRequestModel requestModel) {
        this.listener = listener;
        this.requestModel = requestModel;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (listener != null)
            listener.start(requestModel);
    }

    @Override
    protected NResponseModel doInBackground(String... params) {
        NResponseModel responseModel;
        HttpURLConnection connection = null;
        requestModel.setStartTime(System.currentTimeMillis());

        try {
            NLog.logD(NLog.DEBUG_START_CONNECTION);

            // Request
            if (requestModel.isWriteLogs()) {
                NLog.logD("[" + requestModel.getMethod() + "] " + requestModel.getUrl());
            }

            String urlParams = null;
            if (requestModel.getMethod().equals(NBuilder.GET) || requestModel.getMethod().equals(NBuilder.DELETE)) {
                urlParams = NDataBuilder.getQuery(requestModel.getParams());
                if (!urlParams.isEmpty()) {
                    requestModel.setUrl(requestModel.getUrl() + "?" + urlParams);
                    if (requestModel.isWriteLogs())
                        NLog.logD(NLog.DEBUG_NO_BODY_PARAMS + urlParams.replace("&", "; "));
                }
            }

            URL url = new URL(requestModel.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestModel.getMethod());
            connection.setReadTimeout(DEFAULT_TIMEOUT_READ);
            connection.setConnectTimeout(DEFAULT_TIMEOUT_CONNECT);

            connection.setRequestProperty("Content-Type", requestModel.getRequestType());
            //if (requestModel.getRequestType().equals(NBuilder.REQUEST_TYPE_X_WWW_FORM_URLENCODED))
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

            String logHeaders = "";
            for (int i = 0; i < requestModel.getHeaders().size(); i++) {
                String header = requestModel.getHeaders().get(i).getKey();
                String value = requestModel.getHeaders().get(i).getValue();
                logHeaders += String.format("%s=%s; ", header, value);
                connection.setRequestProperty(header, value);
            }
            if (requestModel.isWriteLogs())
                NLog.logD(NLog.DEBUG_HEADERS + logHeaders);

            connection.setUseCaches(false);
            connection.setDoInput(true);
            if (requestModel.getMethod().equals(NBuilder.POST) || requestModel.getMethod().equals(NBuilder.PUT)) {
                connection.setDoOutput(true);
                String bodyParams = NDataBuilder.getQuery(requestModel.getParams());
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(bodyParams);
                writer.flush();
                writer.close();
                os.close();

                if (requestModel.isWriteLogs())
                    NLog.logD(NLog.DEBUG_BODY_PARAMS + bodyParams.replace("&", "; "));
            }

            // Response
            String body = "", line;
            InputStream is;

            int responseCode = connection.getResponseCode();
            if (requestModel.isWriteLogs())
                NLog.logD(NLog.DEBUG_RESPONSE_CODE + responseCode);
            // For only 2xx codes
            if (responseCode / 100 == 2)
                is = connection.getInputStream();
            else is = connection.getErrorStream();

//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//            while ((line = reader.readLine()) != null) {
//                body += line;
//            }
//            reader.close();

            // TODO: calculate progress speed

            byte[] buf = new byte[8192];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int bytesRead = 0;
            int bytesBuffered = 0;
            while ((bytesRead = is.read(buf)) > -1) {
                outputStream.write(buf, 0, bytesRead);
                bytesBuffered += bytesRead;
                if (bytesBuffered > 1024 * 1024) {
                    bytesBuffered = 0;
                    outputStream.flush();
                }
            }
            outputStream.flush();
            body = outputStream.toString();
            outputStream.close();

            Map<String, List<String>> headers = connection.getHeaderFields();
            if (requestModel.isWriteLogs()) {
                if (headers != null) {
                    String headersLog = "";
                    for (Map.Entry<String, List<String>> entry : headers.entrySet())
                        headersLog += entry.getKey() + ": " + entry.getValue().toString() + "; ";
                    NLog.logD(NLog.DEBUG_RESPONSE_HEADERS_COUNT.replace("x", "" + headers.size()) + " " + headersLog);
                } else NLog.logD(NLog.DEBUG_RESPONSE_NO_HEADERS);

                if (!body.isEmpty())
                    NLog.logD(NLog.DEBUG_RESPONSE_BODY + body);
                else NLog.logD(NLog.DEBUG_RESPONSE_NO_BODY);
            }

            responseModel = new NResponseModel(requestModel.getUrl(), responseCode, body, headers);
            responseModel.setEndTime(System.currentTimeMillis());
            responseModel.setResponseTime((int) (responseModel.getEndTime() - requestModel.getStartTime()));

            if (requestModel.isWriteLogs())
                NLog.logD(NLog.DEBUG_RESPONSE_TIME + responseModel.getResponseTime() + " ms");

            if (listener != null)
                listener.finish(responseModel);

        } catch (Exception e) {
            responseModel = null;
            if (requestModel.isWriteLogs())
                NLog.logD(NLog.ERROR_EXCEPTION + e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return responseModel;
    }

    @Override
    protected void onPostExecute(NResponseModel responseModel) {
        super.onPostExecute(responseModel);
        if (listener != null) {
            if (responseModel != null)
                listener.finishUI(responseModel);
            else {
                if (requestModel.isEnableDefaultListeners())
                    ((NTaskCallback) listener).preFailed(requestModel, NTaskCallback.Errors.CONNECTION_ERROR);
                else
                    ((NTaskCallback) listener).onFailed(requestModel, NTaskCallback.Errors.CONNECTION_ERROR);
            }
        }
    }

    // Callback
    public interface NTaskListener {
        void start(NRequestModel requestModel);

        void finishUI(NResponseModel responseModel);

        void finish(NResponseModel responseModel);
    }
}