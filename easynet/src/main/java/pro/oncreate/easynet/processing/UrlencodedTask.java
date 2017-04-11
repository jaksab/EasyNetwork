package pro.oncreate.easynet.processing;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import pro.oncreate.easynet.methods.EntityMethod;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.utils.NDataBuilder;
import pro.oncreate.easynet.utils.NLog;


/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused,WeakerAccess")
public class UrlencodedTask extends BaseTask {

    public UrlencodedTask(NTaskListener listener, NRequestModel requestModel) {
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
            NLog.logD("[Body params]: " + bodyParams.replace("&", "; "));
        }
    }
}