package pro.oncreate.easynet.processing;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;

import pro.oncreate.easynet.data.NConst;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.subsidiary.NKeyValueFileModel;
import pro.oncreate.easynet.models.subsidiary.NKeyValueModel;
import pro.oncreate.easynet.utils.NLog;


/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused,WeakerAccess")
public class MultipartTask extends BaseTask {

    protected static final int FILE_BUFFER_SIZE = 4096;
    protected static final String LINE_END = "\r\n";
    protected final String boundary = "---" + System.currentTimeMillis() + "---";

    public MultipartTask(NTaskListener listener, NRequestModel requestModel) {
        super(listener, requestModel);
    }

    @Override
    protected HttpURLConnection setupConnection() throws Exception {
        HttpURLConnection connection = openConnection();
        connection.setRequestProperty(NConst.CONNECTION, "Keep-Alive");
        connection.setRequestProperty(NConst.CACHE_CONTROL, "no-cache");
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(!requestModel.isEnableManualRedirect());
        return connection;
    }

    @Override
    protected void addHeaders(HttpURLConnection connection) throws UnsupportedEncodingException {
        StringBuilder logHeaders = new StringBuilder();
        for (NKeyValueModel header : requestModel.getHeaders()) {
            logHeaders.append(String.format("%s=%s; ", header.getKey(), header.getValue()));
            if (header.getKey().equals(NConst.CONTENT_TYPE)
                    && requestModel.getRequestType().contains(NConst.MIME_TYPE_MULTIPART_FORM_DATA))
                connection.setRequestProperty(header.getKey(), header.getValue() + "; boundary=" + boundary);
            else connection.setRequestProperty(header.getKey(), header.getValue());
        }
        NLog.logD("[Headers]: " + logHeaders.toString());
    }

    @Override
    protected void makeRequestBody(HttpURLConnection connection) throws IOException {
        outputStream = connection.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);

        StringBuilder debug = new StringBuilder("[Request text params]: ");
        for (int i = 0; i < requestModel.getParams().size(); i++) {
            addFormField(requestModel.getParams().get(i));
            debug.append(requestModel.getParams().get(i).getKey()).append("=")
                    .append(requestModel.getParams().get(i).getValue()).append("; ");
        }
        NLog.logD(debug.toString());

        debug = new StringBuilder("[Request file params]: ");
        for (int i = 0; i < requestModel.getParamsFile().size(); i++) {
            addFilePart(requestModel.getParamsFile().get(i));
            debug.append(requestModel.getParamsFile().get(i).getKey()).append(" > ")
                    .append(requestModel.getParamsFile().get(i).getValue()).append("; ");
        }
        NLog.logD(debug.toString());

        writer.append(LINE_END).flush();
        writer.append("--").append(boundary).append("--").append(LINE_END);
        writer.close();
    }

    protected void addFormField(NKeyValueModel model) {
        writer.append("--").append(boundary).append(LINE_END);
        writer.append("Content-Disposition: form-data; name=\"").append(model.getKey()).append("\"")
                .append(LINE_END);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_END);
        writer.append(LINE_END);
        writer.append(model.getValue()).append(LINE_END);
        writer.flush();
    }

    protected void addFilePart(NKeyValueFileModel model)
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
        byte[] buffer = new byte[FILE_BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_END);
        writer.flush();
    }
}
