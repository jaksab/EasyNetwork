package pro.oncreate.easynet.tasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import pro.oncreate.easynet.NConfig;
import pro.oncreate.easynet.data.NConst;
import pro.oncreate.easynet.data.NErrors;
import pro.oncreate.easynet.methods.EntityMethod;
import pro.oncreate.easynet.methods.QueryMethod;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.models.subsidiary.NKeyValueFileModel;
import pro.oncreate.easynet.models.subsidiary.NKeyValueModel;
import pro.oncreate.easynet.utils.NDataBuilder;
import pro.oncreate.easynet.utils.NLog;


/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused,WeakerAccess")
public class NTask extends AsyncTask<String, Integer, NResponseModel> {

    public final static int DEFAULT_TIMEOUT_READ = 10000;
    public final static int DEFAULT_TIMEOUT_CONNECT = 7500;

    private static final int BUFFER_SIZE = 8192;
    private static final String LINE_END = "\r\n";
    private final String boundary = "===" + System.currentTimeMillis() + "===";
    private static final String charset = "UTF-8";

    private String tag;

    private OutputStream outputStream;
    private PrintWriter writer;

    private NTask.NTaskListener listener;
    private NRequestModel requestModel;

    public NTask(NTask.NTaskListener listener, NRequestModel requestModel) {
        this.listener = listener;
        this.requestModel = requestModel;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (listener != null)
            listener.start(requestModel);
        requestModel.setStartTime(System.currentTimeMillis());
        setTag("task#" + requestModel.getStartTime());
        NConfig.getInstance().addTask(this.tag, this);
    }

    @Override
    protected NResponseModel doInBackground(String... params) {
        NResponseModel responseModel;
        HttpURLConnection connection = null;
        String body;
        InputStream inputStream;

        try {
            // Request
            NLog.logD("======== START REQUEST ========");

            if (NConfig.getInstance().isWriteLogs())
                NLog.logD("[" + requestModel.getMethod() + "] " + requestModel.getUrl());

            while (true) {
                addUrlParams();

                switch (requestModel.getRequestType()) {
                    case NConst.MIME_TYPE_X_WWW_FORM_URLENCODED:
                        connection = setUpConnection();
                        addHeaders(connection);
                        addEntityParams(connection);
                        break;
                    case NConst.MIME_TYPE_MULTIPART_FORM_DATA:
                        if (requestModel.getChunk() == null) {
                            connection = setUpMultipartConnection();
                            addHeaders(connection);
                            addMultipartParams(connection);
                        } else {
                            connection = setUpMultipartConnection();
                            connection.setChunkedStreamingMode(1024);
                            addHeaders(connection);
                            chunk(connection, requestModel.getChunk());
                        }
                        break;
                    case NConst.MIME_TYPE_JSON:
                        connection = setUpConnection();
                        addHeaders(connection);
                        setJSONBody(connection);
                        break;
                    default:
                        connection = setUpConnection();
                        addHeaders(connection);
                        setRawBody(connection);
                }


                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                        || responseCode == HttpURLConnection.HTTP_MOVED_PERM
                        || responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
                    String newUrl = connection.getHeaderField("Location");

                    boolean next = true;
                    if (listener != null)
                        next = listener.redirect(newUrl);

                    if (!next) {
                        NLog.logD("[The redirect is forbidden]: " + newUrl);
                        responseModel = new NResponseModel(requestModel.getUrl(), responseCode,
                                null, null);
                        responseModel.setRedirectInterrupted(true);
                        responseModel.setRedirectLocation(newUrl);
                        break;
                    } else {
                        if (NConfig.getInstance().isWriteLogs())
                            NLog.logD("[Redirect]: " + newUrl);

                        requestModel.setUrl(newUrl);
                        requestModel.clearParams();
                        requestModel.setRequestType(NConst.MIME_TYPE_X_WWW_FORM_URLENCODED);
                        connection.disconnect();
                    }
                } else {
                    if (NConfig.getInstance().isWriteLogs())
                        NLog.logD("[Status code]: " + responseCode);

                    inputStream = getInputStreamFromConnection(connection);
                    body = readData(inputStream, BUFFER_SIZE);
                    Map<String, List<String>> headers = getResponseHeaders(connection, body);

                    responseModel = new NResponseModel(requestModel.getUrl(), responseCode, body, headers);
                    responseModel.setEndTime(System.currentTimeMillis());
                    responseModel.setResponseTime((int) (responseModel.getEndTime() - requestModel.getStartTime()));

                    if (NConfig.getInstance().isWriteLogs())
                        NLog.logD("[Response time]: " + responseModel.getResponseTime() + " ms");

                    if (listener != null)
                        listener.finish(responseModel);
                    break;
                }
            }
        } catch (Exception e) {
            responseModel = null;
            if (NConfig.getInstance().isWriteLogs())
                NLog.logD("[Error]: " + e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            NConfig.getInstance().removeTask(getTag());
        }
        return responseModel;
    }

    @Override
    protected void onProgressUpdate(Integer... errorCode) {
    }

    @Override
    protected void onPostExecute(NResponseModel responseModel) {
        super.onPostExecute(responseModel);
        if (listener != null) {
            if (responseModel != null) {
                if (!responseModel.isRedirectInterrupted())
                    listener.finishUI(responseModel);
                else if (listener instanceof NBaseCallback) {
                    ((NBaseCallback) listener).finishUIFailed();
                    ((NBaseCallback) listener).onRedirectInterrupted(responseModel.getRedirectLocation(), responseModel);
                }
            } else if (listener instanceof NBaseCallback) {
                ((NBaseCallback) listener).finishUIFailed();
                if (requestModel.isEnableDefaultListeners())
                    ((NBaseCallback) listener).preFailed(requestModel, NErrors.CONNECTION_ERROR);
                else
                    ((NBaseCallback) listener).onFailed(requestModel, NErrors.CONNECTION_ERROR);
            }
        }
    }

    // Base
    //

    private HttpURLConnection openConnection() throws Exception {
        URL url = new URL(requestModel.getUrl());
        return (HttpURLConnection) url.openConnection();
    }

    private void addUrlParams() throws UnsupportedEncodingException {
        String urlParams;

        if ((requestModel.getMethod() instanceof QueryMethod)
                && requestModel.getQueryParams().isEmpty() && !requestModel.getParams().isEmpty()) {
            urlParams = NDataBuilder.getQuery(requestModel.getParams(), charset);
            if (!urlParams.isEmpty()) {
                requestModel.setUrl(requestModel.getUrl() + "?" + urlParams);
                if (NConfig.getInstance().isWriteLogs())
                    NLog.logD("[Query params]: " + urlParams.replace("&", "; "));
            }
        } else if (!requestModel.getQueryParams().isEmpty()) {
            urlParams = NDataBuilder.getQuery(requestModel.getQueryParams(), charset);
            if (!urlParams.isEmpty()) {
                requestModel.setUrl(requestModel.getUrl() + "?" + urlParams);
                if (NConfig.getInstance().isWriteLogs())
                    NLog.logD("[Query params]: " + urlParams.replace("&", "; "));
            }
        }
    }

    private void addHeaders(HttpURLConnection connection) throws UnsupportedEncodingException {
        String logHeaders = "";
        for (NKeyValueModel header : requestModel.getHeaders()) {
            logHeaders += String.format("%s=%s; ", header.getKey(), header.getValue());
            if (header.getKey().equals(NConst.CONTENT_TYPE)
                    && requestModel.getRequestType().equals(NConst.MIME_TYPE_MULTIPART_FORM_DATA))
                connection.setRequestProperty(header.getKey(), header.getValue() + "; boundary=" + boundary);
            else connection.setRequestProperty(header.getKey(), header.getValue());
        }
        if (NConfig.getInstance().isWriteLogs())
            NLog.logD("[Headers]: " + logHeaders);
    }

    private Map<String, List<String>> getResponseHeaders(HttpURLConnection connection, String body) {
        Map<String, List<String>> headers = connection.getHeaderFields();
        if (NConfig.getInstance().isWriteLogs()) {
            if (headers != null) {
                String headersLog = "";
                for (Map.Entry<String, List<String>> entry : headers.entrySet())
                    headersLog += entry.getKey() + "=" + entry.getValue().toString() + "; ";
                NLog.logD("[Getting x headers]: ".replace("x", "" + headers.size()) + headersLog);
            } else NLog.logD("[Headers empty]");

            if (!body.isEmpty())
                NLog.logD("[Body]: " + body);
            else NLog.logD("[Body=null]");
        }
        return headers;
    }

    private InputStream getInputStreamFromConnection(HttpURLConnection connection) throws IOException {
        if (connection.getResponseCode() / 100 == 2)
            return connection.getInputStream();
        else return connection.getErrorStream();
    }

    @Deprecated
    private String readData(InputStream inputStream) throws IOException {
        String body = "", line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while ((line = reader.readLine()) != null) {
            body += line;
        }
        reader.close();
        return body;
    }

    private String readData(InputStream inputStream, int bufferSize) throws Exception {
        byte[] buf = new byte[bufferSize];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int bytesRead;
        int bytesBuffered = 0;
        while ((bytesRead = inputStream.read(buf)) > -1) {
            if (isCancelled()) {
                throw new Exception("Task was cancelled");
            }
            outputStream.write(buf, 0, bytesRead);
            bytesBuffered += bytesRead;
            if (bytesBuffered > 1024 * 1024) {
                bytesBuffered = 0;
                outputStream.flush();
            }
            // TODO: calculate progress speed
        }
        outputStream.flush();
        outputStream.close();
        return outputStream.toString();
    }

    @Override
    protected void onCancelled() {
        if (listener != null && listener instanceof NBaseCallback)
            ((NBaseCallback) listener).preTaskCancelled(requestModel, tag);
    }

    // URL Encode
    //

    private void addEntityParams(HttpURLConnection connection) throws IOException {
        if (requestModel.getMethod() instanceof EntityMethod) {
            connection.setDoOutput(true);
            String bodyParams = NDataBuilder.getQuery(requestModel.getParams(), charset);
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, charset));
            writer.write(bodyParams);
            writer.flush();
            writer.close();
            os.close();
            if (NConfig.getInstance().isWriteLogs())
                NLog.logD("[Body params]: " + bodyParams.replace("&", "; "));
        }
    }

    private HttpURLConnection setUpConnection() throws Exception {
        HttpURLConnection connection = openConnection();
        connection.setRequestMethod(requestModel.getMethod().name());
        connection.setReadTimeout((int) requestModel.getReadTimeout());
        connection.setConnectTimeout((int) requestModel.getConnectTimeout());
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(!requestModel.isEnableManualRedirect());
        return connection;
    }

    // Multipart
    //

    private HttpURLConnection setUpMultipartConnection() throws Exception {
        HttpURLConnection connection = openConnection();
        connection.setRequestProperty(NConst.CONNECTION, "Keep-Alive");
        connection.setRequestProperty(NConst.CACHE_CONTROL, "no-cache");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(!requestModel.isEnableManualRedirect());
        return connection;
    }

    private void addMultipartParams(HttpURLConnection connection) throws IOException {
        outputStream = connection.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);

        String debug = "[Request text params]: ";
        for (int i = 0; i < requestModel.getParams().size(); i++) {
            addFormField(requestModel.getParams().get(i));
            debug += requestModel.getParams().get(i).getKey() + "=" +
                    requestModel.getParams().get(i).getValue() + "; ";
        }
        if (NConfig.getInstance().isWriteLogs())
            NLog.logD(debug);

        debug = "[Request file params]: ";
        for (int i = 0; i < requestModel.getParamsFile().size(); i++) {
            addFilePart(requestModel.getParamsFile().get(i));
            debug += requestModel.getParamsFile().get(i).getKey() + " > " +
                    requestModel.getParamsFile().get(i).getValue() + "; ";
        }
        if (NConfig.getInstance().isWriteLogs())
            NLog.logD(debug);

        // Response
        writer.append(LINE_END).flush();
        writer.append("--").append(boundary).append("--").append(LINE_END);
        writer.close();
    }

    private void addFormField(NKeyValueModel model) {
        writer.append("--").append(boundary).append(LINE_END);
        writer.append("Content-Disposition: form-data; name=\"").append(model.getKey()).append("\"")
                .append(LINE_END);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_END);
        writer.append(LINE_END);
        writer.append(model.getValue()).append(LINE_END);
        writer.flush();
    }

    private void addFilePart(NKeyValueFileModel model)
            throws IOException {
        String fileName = model.getValue().getName();
        writer.append("--").append(boundary).append(LINE_END);
        writer.append("Content-Disposition: form-data; name=\"")
                .append(model.getKey()).append("\"; filename=\"").append(fileName).append("\"")
                .append(LINE_END);
        writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_END);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_END);
        writer.append(LINE_END);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(model.getValue());
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_END);
        writer.flush();
    }

    private void chunk(HttpURLConnection connection, File file) throws Exception {
        outputStream = connection.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);

        String fileName = file.getName();
        writer.append("--").append(boundary).append(LINE_END);
        writer.append("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"")
                .append(fileName).append("\"").append(LINE_END);
        writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_END);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_END);
        writer.append(LINE_END);
        writer.flush();

        if (NConfig.getInstance().isWriteLogs())
            NLog.logD("Start upload file: " + fileName);

        FileInputStream inputStream = new FileInputStream(file);

        int maxBufferSize = 1024;
        int bytesAvailable = inputStream.available();
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead;
        bytesRead = inputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            try {
                outputStream.write(buffer, 0, bufferSize);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                throw new Exception("OutOfMemory");
            }
            bytesAvailable = inputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = inputStream.read(buffer, 0, bufferSize);
        }

        outputStream.flush();
        inputStream.close();

        writer.append(LINE_END).flush();
        writer.append("--").append(boundary).append("--").append(LINE_END);
        writer.close();
    }

    // Raw
    //

    private void setRawBody(HttpURLConnection connection, String body) throws IOException {
        if (requestModel.getMethod() instanceof EntityMethod) {
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, charset));
            writer.write(body);
            writer.flush();
            writer.close();
            os.close();
            if (NConfig.getInstance().isWriteLogs())
                NLog.logD("[Request body]: " + body);
        }
    }

    private void setRawBody(HttpURLConnection connection) throws IOException {
        setRawBody(connection, requestModel.getBody());
    }

    // JSON
    //
    private void setJSONBody(HttpURLConnection connection) throws IOException {
        try {
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < requestModel.getParams().size(); i++) {
                jsonObject.put(requestModel.getParams().get(i).getKey(),
                        requestModel.getParams().get(i).getValue());
            }

            setRawBody(connection, jsonObject.toString().replaceAll("\"\\[", "[").replaceAll("]\"", "]"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Callback
    public interface NTaskListener {
        void start(NRequestModel requestModel);

        void finishUI(NResponseModel responseModel);

        void finish(NResponseModel responseModel);

        /**
         * @param location new URL
         * @return true - if you want to continue redirect
         */
        boolean redirect(String location);
    }
}