package pro.oncreate.easynet.processing;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import pro.oncreate.easynet.methods.EntityMethod;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.utils.NLog;


/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused,WeakerAccess")
public class RawTask extends BaseTask {

    public RawTask(NTaskListener listener, NRequestModel requestModel) {
        super(listener, requestModel);
    }

    @Override
    protected HttpURLConnection setupConnection() throws Exception {
        HttpURLConnection connection = openConnection();
        connection.setRequestMethod(requestModel.getMethod().name());
        connection.setReadTimeout((int) requestModel.getReadTimeout());
        connection.setConnectTimeout((int) requestModel.getConnectTimeout());
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(!requestModel.isEnableManualRedirect());
        return connection;
    }

    @Override
    protected void makeRequestBody(HttpURLConnection connection) throws IOException {
        setRawBody(connection, requestModel.getBody());
    }

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
            NLog.logD("[Request body]: " + body);
        }
    }
}