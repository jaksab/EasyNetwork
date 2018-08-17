package pro.oncreate.easynet.processing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;

import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.utils.NLog;


/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused,WeakerAccess")
public class TestTask extends BaseTask {

    public TestTask(NTaskListener listener, NRequestModel requestModel) {
        super(listener, requestModel);
    }

    @Override
    protected NResponseModel doInBackground(String... params) {
        InputStream is = null;
        BufferedReader reader = null;
        NResponseModel responseModel;
        String body;

        NLog.logD("======== TEST REQUEST ========");

        try {
            is = requestModel.getResources().openRawResource(requestModel.getTestRaw());
            reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line).append("\n");
            }
            body = data.toString().trim();

            NLog.logD("Read body success");
            NLog.logD("[Body]: " + body);

            if (requestModel.getPreprocessorCallback() != null) {
                body = requestModel.getPreprocessorCallback().onDataReady(body, requestModel);
                NLog.logD("[Processing body]: " + body);
            }

            if (requestModel.getTestDelayMills() > 0)
                Thread.sleep(requestModel.getTestDelayMills());

            responseModel = new NResponseModel(this.url, 200, body, new HashMap<String, List<String>>());
            responseModel.setEndTime(System.currentTimeMillis());
            responseModel.setResponseTime((int) (responseModel.getEndTime() - requestModel.getStartTime()));
            if (listener != null)
                listener.finish(responseModel);
        } catch (Exception e) {
            responseModel = null;
            NLog.logD("[Error]: " + e.toString());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return responseModel;
    }

    @Override
    protected HttpURLConnection setupConnection() throws Exception {
        return null;
    }

    @Override
    protected void makeRequestBody(HttpURLConnection connection) throws IOException {
    }

    public interface PreprocessorCallback {
        /**
         * Implement this instance for organizing custom logic
         *
         * @param body original body from raw file
         * @return body after post processing
         */
        String onDataReady(String body, NRequestModel requestModel);
    }
}