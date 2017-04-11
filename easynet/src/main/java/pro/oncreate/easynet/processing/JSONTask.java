package pro.oncreate.easynet.processing;

import org.json.JSONException;
import org.json.JSONObject;

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
public class JSONTask extends BaseTask {

    public JSONTask(NTaskListener listener, NRequestModel requestModel) {
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

    private void setRawBody(HttpURLConnection connection) throws IOException {
        setRawBody(connection, requestModel.getBody());
    }
}