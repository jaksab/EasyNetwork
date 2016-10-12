package pro.oncreate.easynet.tasks;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import pro.oncreate.easynet.models.NKeyValueFileModel;
import pro.oncreate.easynet.models.NKeyValueModel;
import pro.oncreate.easynet.models.NRequestMultipartModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.utils.NLog;

/**
 * Created by andrej on 15.11.15.
 */
public class NTaskMultipart extends AsyncTask<String, Void, NResponseModel> {

    // Data
    private static final String LINE_FEED = "\r\n";
    private final String boundary = "===" + System.currentTimeMillis() + "===";
    private static final String charset = "UTF-8";

    private HttpURLConnection connection;
    private OutputStream outputStream;
    private PrintWriter writer;

    private NTask.NTaskListener listener;
    private NRequestMultipartModel requestModel;

    public NTaskMultipart(NTask.NTaskListener listener, NRequestMultipartModel requestModel) {
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
        NResponseModel responseModel = null;
        try {
            // Request
            if (requestModel.isWriteLogs())
                NLog.logD(NLog.DEBUG_OPEN_CONNECTION_MULTIPART + requestModel.getUrl());

            URL url = new URL(requestModel.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);

            String debug = "Request headers: ";
            for (int i = 0; i < requestModel.getHeaders().size(); i++) {
                connection.setRequestProperty(requestModel.getHeaders().get(i).getKey(), requestModel.getHeaders().get(i).getValue());
                debug += requestModel.getHeaders().get(i).getKey() + ">" +
                        requestModel.getHeaders().get(i).getValue() + "; ";
            }

            outputStream = connection.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                    true);


            if (requestModel.isWriteLogs())
                NLog.logD(debug);

            debug = "Request text params: ";
            for (int i = 0; i < requestModel.getParamsText().size(); i++) {
                addFormField(requestModel.getParamsText().get(i));
                debug += requestModel.getParamsText().get(i).getKey() + ">" +
                        requestModel.getParamsText().get(i).getValue() + "; ";
            }
            if (requestModel.isWriteLogs())
                NLog.logD(debug);


            debug = "Request file params: ";
            for (int i = 0; i < requestModel.getParamsFile().size(); i++) {
                addFilePart(requestModel.getParamsFile().get(i));
                debug += requestModel.getParamsFile().get(i).getKey() + ">" +
                        requestModel.getParamsFile().get(i).getValue() + "; ";
            }
            if (requestModel.isWriteLogs())
                NLog.logD(debug);


            // Response
            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();

            String body = "", line;
            InputStream is;

            int responseCode = connection.getResponseCode();
            if (requestModel.isWriteLogs())
                NLog.logD(NLog.DEBUG_RESPONSE_CODE + responseCode);

            if (responseCode / 100 == 2) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while ((line = reader.readLine()) != null) {
                body += line;
            }
            reader.close();

            Map<String, List<String>> headers = connection.getHeaderFields();
            if (requestModel.isWriteLogs()) {
                if (headers != null)
                    NLog.logD(NLog.DEBUG_RESPONSE_HEADERS_COUNT.replace("x", "" + headers.size()));
                else NLog.logD(NLog.DEBUG_RESPONSE_NO_HEADERS);

                if (body != null)
                    NLog.logD(NLog.DEBUG_RESPONSE_BODY + body);
                else NLog.logD(NLog.DEBUG_RESPONSE_NO_BODY);
            }

            responseModel = new NResponseModel(requestModel.getUrl(), responseCode, body, headers);

            if (listener != null)
                listener.finish(responseModel);

        } catch (Exception e) {
            responseModel = null;
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
        if (listener != null)
            listener.finishUI(responseModel);

    }

    public void addFormField(NKeyValueModel model) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + model.getKey() + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(model.getValue()).append(LINE_FEED);
        writer.flush();
    }

    public void addFilePart(NKeyValueFileModel model)
            throws IOException {
        String fileName = model.getValue().getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + model.getKey()
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(model.getValue().getPath());
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED);
        writer.flush();
    }

    public void addHeaderField(NKeyValueModel header) {
        writer.append(header.getKey() + ": " + header.getValue()).append(LINE_FEED);
        writer.flush();
    }

//    // Callback
//    public interface NTaskListener {
//        void start(NRequestMultipartModel requestMModel);
//
//        void finishUI(NResponseModel responseModel);
//
//        void finish(NResponseModel responseModel);
//    }
}