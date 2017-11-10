package pro.oncreate.easynet.processing;

import android.os.AsyncTask;
import android.os.Process;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pro.oncreate.easynet.EasyNet;
import pro.oncreate.easynet.data.NConst;
import pro.oncreate.easynet.data.NErrors;
import pro.oncreate.easynet.methods.QueryMethod;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.models.subsidiary.NKeyValueModel;
import pro.oncreate.easynet.utils.NDataBuilder;
import pro.oncreate.easynet.utils.NLog;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;


/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused,WeakerAccess")
public abstract class BaseTask extends AsyncTask<String, Object, NResponseModel> {


    //
    // Data
    //

    public final static int DEFAULT_TIMEOUT_READ = 10000;
    public final static int DEFAULT_TIMEOUT_CONNECT = 7500;

    protected static final int BUFFER_SIZE = 8192;
    protected static final String charset = "UTF-8";

    protected String tag;
    protected String url;
    protected OutputStream outputStream;
    protected PrintWriter writer;
    protected BaseTask.NTaskListener listener;
    protected NRequestModel requestModel;


    //
    // 
    //

    public BaseTask(BaseTask.NTaskListener listener, NRequestModel requestModel) {
        this.listener = listener;
        this.requestModel = requestModel;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (listener != null)
            listener.start(requestModel);
        requestModel.setStartTime(System.currentTimeMillis());
        setTag(requestModel.getTag());
        EasyNet.getInstance().addTask(this.tag, this);
    }

    @Override
    protected NResponseModel doInBackground(String... params) {
        Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);

        NResponseModel responseModel;
        HttpURLConnection connection = null;
        String body;
        InputStream inputStream;

        NLog.logD("======== START REQUEST ========");
        NLog.logD(String.format(Locale.getDefault(), "[%s] %s", requestModel.getMethod(), requestModel.getUrl()));

        CacheOptions cacheOptions = requestModel.getCacheOptions();
        if (cacheOptions == CacheOptions.CACHE_AND_NETWORK
                || cacheOptions == CacheOptions.CACHE_ONLY
                || cacheOptions == CacheOptions.NETWORK_ONCE_CACHE_LATER) {

            try {
                String key = !requestModel.isCacheWithParams() ? requestModel.getUrl() : buildUrlWithQueryParams(false);
                String lastResponse = loadResponse(key);
                NResponseModel cacheResponse = getCacheResponse(requestModel, lastResponse);

                if (lastResponse != null) {
                    NLog.logD("[Load from cache]: " + lastResponse);
                    if (listener != null)
                        listener.finish(cacheResponse);
                    if (cacheOptions == CacheOptions.CACHE_AND_NETWORK)
                        publishProgress(cacheResponse);
                } else {
                    NLog.logD("[Cache missing]");
                    if (cacheOptions != CacheOptions.CACHE_ONLY)
                        publishProgress(requestModel);
                }

                if (cacheOptions == CacheOptions.CACHE_ONLY
                        || (cacheOptions == CacheOptions.NETWORK_ONCE_CACHE_LATER && lastResponse != null))
                    return cacheResponse;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        try {
            while (true) {
                this.url = buildUrlWithQueryParams(true);
                connection = setupConnection();
                addHeaders(connection);
                makeRequestBody(connection);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                        || responseCode == HttpURLConnection.HTTP_MOVED_PERM
                        || responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
                    String newUrl = connection.getHeaderField("Location");

                    boolean next = true;
                    if (listener != null)
                        next = listener.redirect(newUrl);

                    if (!next) {
                        NLog.logE("[The redirect is forbidden]: " + newUrl);
                        responseModel = new NResponseModel(this.url, responseCode,
                                null, null);
                        responseModel.setRedirectInterrupted(true);
                        responseModel.setRedirectLocation(newUrl);
                        break;
                    } else {
                        NLog.logD("[Redirect]: " + newUrl);
                        requestModel.setUrl(newUrl);
                        requestModel.clearParams();
                        requestModel.setRequestType(NConst.MIME_TYPE_X_WWW_FORM_URLENCODED);
                        connection.disconnect();
                    }
                } else {
                    NLog.logD("[Status code]: " + responseCode);

                    inputStream = getInputStreamFromConnection(connection);
                    body = readResponseBody(inputStream);
                    Map<String, List<String>> headers = getResponseHeaders(connection, body);

                    responseModel = new NResponseModel(this.url, responseCode, body, headers);
                    responseModel.setEndTime(System.currentTimeMillis());
                    responseModel.setResponseTime((int) (responseModel.getEndTime() - requestModel.getStartTime()));
                    NLog.logD("[Response time]: " + responseModel.getResponseTime() + " ms");

                    try {
                        inputStream.close();
                        inputStream = null;
                    } catch (Exception ignored) {
                    }

                    if (listener != null && listener.finish(responseModel))
                        saveToCache(responseModel, body);
                    break;
                }
            }
        } catch (Exception e) {
            responseModel = null;
            NLog.logD("[Error]: " + e.toString());
        } finally {
            try {
                if (connection != null) connection.disconnect();
                EasyNet.getInstance().removeTask(getTag());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseModel;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        if (values != null && values.length > 0) {
            if (values.length == 1 && values[0] instanceof NResponseModel && listener instanceof NBaseCallback) {
                listener.finishUI((NResponseModel) values[0]);
            }
            if (values.length == 1 && values[0] instanceof NRequestModel && listener instanceof NBaseCallback) {
                ((NBaseCallback) listener).onCacheMissing((NRequestModel) values[0]);
            }
        }
    }

    @Override
    protected void onPostExecute(NResponseModel responseModel) {
        super.onPostExecute(responseModel);
        if (listener != null) { // Ignore response if no callback
            if (responseModel != null) { // Response is present
                if (!responseModel.isRedirectInterrupted()) // Response OK
                    listener.finishUI(responseModel);
                else if (listener instanceof NBaseCallback) { // Response interrupted
                    ((NBaseCallback) listener).finishUIFailed();
                    ((NBaseCallback) listener).onRedirectInterrupted(responseModel.getRedirectLocation(), responseModel);
                }
            } else if (listener instanceof NBaseCallback) { // Response == null
                ((NBaseCallback) listener).finishUIFailed(); // Finish all request progress views
                if (requestModel.isEnableDefaultListeners()) // Failed processing with default listener
                    ((NBaseCallback) listener).preFailed(requestModel, NErrors.CONNECTION_ERROR);
                else // // Failed processing without default listener
                    ((NBaseCallback) listener).onFailed(requestModel, NErrors.CONNECTION_ERROR);
            }
        }
    }


    //
    // Behavior
    //


    protected HttpURLConnection openConnection() throws Exception {
        URL url = new URL(this.url);
        return (HttpURLConnection) url.openConnection();
    }

    abstract protected HttpURLConnection setupConnection() throws Exception;

    protected void addHeaders(HttpURLConnection connection) throws UnsupportedEncodingException {
        StringBuilder logHeaders = new StringBuilder();
        for (NKeyValueModel header : requestModel.getHeaders()) {
            logHeaders.append(String.format("%s=%s; ", header.getKey(), header.getValue()));
            connection.setRequestProperty(header.getKey(), header.getValue());
        }
        NLog.logD("[Headers]: " + logHeaders.toString());
    }

    @Deprecated
    protected void addUrlParams() throws UnsupportedEncodingException {
        String urlParams;

        if ((requestModel.getMethod() instanceof QueryMethod)
                && requestModel.getQueryParams().isEmpty() && !requestModel.getParams().isEmpty()) {
            urlParams = NDataBuilder.getQuery(requestModel.getParams(), charset);
            if (!urlParams.isEmpty()) {
                requestModel.setUrl(requestModel.getUrl() + "?" + urlParams);
                NLog.logD("[Query params]: " + urlParams.replace("&", "; "));
            }
        } else if (!requestModel.getQueryParams().isEmpty()) {
            urlParams = NDataBuilder.getQuery(requestModel.getQueryParams(), charset);
            if (!urlParams.isEmpty()) {
                requestModel.setUrl(requestModel.getUrl() + "?" + urlParams);
                NLog.logD("[Query params]: " + urlParams.replace("&", "; "));
            }
        }
    }

    protected String buildUrlWithQueryParams(boolean logs) throws UnsupportedEncodingException {
        String urlParams;
        String result = requestModel.getUrl();

        if ((requestModel.getMethod() instanceof QueryMethod)
                && requestModel.getQueryParams().isEmpty() && !requestModel.getParams().isEmpty()) {
            urlParams = NDataBuilder.getQuery(requestModel.getParams(), charset);
            if (!urlParams.isEmpty()) {
                result = requestModel.getUrl() + "?" + urlParams;
                if (logs)
                    NLog.logD("[Query params]: " + urlParams.replace("&", "; "));
            }
        } else if (!requestModel.getQueryParams().isEmpty()) {
            urlParams = NDataBuilder.getQuery(requestModel.getQueryParams(), charset);
            if (!urlParams.isEmpty()) {
                result = requestModel.getUrl() + "?" + urlParams;
                if (logs)
                    NLog.logD("[Query params]: " + urlParams.replace("&", "; "));
            }
        }
        return result;
    }

    abstract protected void makeRequestBody(HttpURLConnection connection) throws IOException;

    protected Map<String, List<String>> getResponseHeaders(HttpURLConnection connection, String body) {
        Map<String, List<String>> headers = connection.getHeaderFields();
        if (EasyNet.getInstance().isWriteLogs()) {
            if (headers != null) {
                StringBuilder headersLog = new StringBuilder();
                for (Map.Entry<String, List<String>> entry : headers.entrySet())
                    headersLog.append(entry.getKey() != null ? (entry.getKey() + "=") : "")
                            .append(entry.getValue().toString()).append("; ");
                NLog.logD("[Getting x headers]: ".replace("x", "" + headers.size()) + headersLog.toString());
            } else NLog.logD("[Headers empty]");

            if (!body.isEmpty())
                NLog.logD("[Body]: " + body);
            else NLog.logD("[Empty body]");
        }
        return headers;
    }

    protected InputStream getInputStreamFromConnection(HttpURLConnection connection) throws IOException {
        if (connection.getResponseCode() / 100 == 2)
            return connection.getInputStream();
        else return connection.getErrorStream();
    }

    protected String readResponseBody(InputStream inputStream) throws Exception {
        byte[] buf = new byte[BUFFER_SIZE];
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

    protected void saveResponse(String key, String body) {
        if (key == null || body == null)
            return;

        FileOutputStream fos = null;
        try {
            String filename = generateCacheFileName(key);
            File file = EasyNet.getInstance().getCacheFile(filename);

            try {
                if (EasyNet.getInstance().getCacheDir().listFiles().length >= EasyNet.getInstance().getMaxCacheItems())
                    EasyNet.getInstance().clearCache();
            } catch (Exception e) {
                e.printStackTrace();
            }

            fos = new FileOutputStream(file, false);
            fos.write(body.getBytes());
            fos.close();
            fos = null;

            NLog.logD("[Saving response to cache success]");
        } catch (IOException e) {
            NLog.logD("[Saving response to cache failed]");
            e.printStackTrace();
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    protected String loadResponse(String key) {
        BufferedReader input = null;
        try {
            String filename = generateCacheFileName(key);
            File file = EasyNet.getInstance().getCacheFile(filename);

            input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            StringBuilder buffer = new StringBuilder();
            while ((line = input.readLine()) != null) {
                buffer.append(line);
            }
            input.close();
            input = null;
            return buffer.toString();
        } catch (IOException e) {
            return null;
        } finally {
            if (input != null)
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    protected NResponseModel getCacheResponse(NRequestModel requestModel, String body) {
        NResponseModel responseModel = new NResponseModel(requestModel.getUrl(), 200, body, null);
        responseModel.setFromCache(true);
        return responseModel;
    }

    protected void saveToCache(NResponseModel responseModel, String body) {
        try {
            if (requestModel.isCacheResponse() && responseModel != null
                    && responseModel.statusType() == NResponseModel.STATUS_TYPE_SUCCESS) {
                String key = !requestModel.isCacheWithParams() ? requestModel.getUrl() : buildUrlWithQueryParams(true);
                saveResponse(key, body);
            }
        } catch (Exception ignored) {
        }
    }

    //
    // Other
    //

    public String generateCacheFileName(String key) {
        return key.replaceAll("[/:.&+?%]", "");
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    protected void onCancelled() {
        if (listener != null && listener instanceof NBaseCallback)
            ((NBaseCallback) listener).preTaskCancelled(requestModel, tag);
    }


    //
    // Callback
    //


    public interface NTaskListener {
        /**
         * Called when request task is started
         *
         * @param requestModel request model data
         */
        void start(NRequestModel requestModel);

        /**
         * Called when request task is finishing and response data ready to use in UI thread
         *
         * @param responseModel response model data
         */
        void finishUI(NResponseModel responseModel);

        /**
         * Called when http request is finishing and response data сan be processed in background thread
         *
         * @param responseModel response model data
         */
        boolean finish(NResponseModel responseModel);

        /**
         * @param location new URL
         * @return true - if you want to continue redirect
         */
        boolean redirect(String location);
    }

    public enum CacheOptions {
        NETWORK_ONLY,
        CACHE_ONLY,
        CACHE_AND_NETWORK,
        NETWORK_ONCE_CACHE_LATER
    }
}