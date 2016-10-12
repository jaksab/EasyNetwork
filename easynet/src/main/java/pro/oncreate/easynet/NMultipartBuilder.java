package pro.oncreate.easynet;

import java.io.File;

import pro.oncreate.easynet.models.NKeyValueFileModel;
import pro.oncreate.easynet.models.NKeyValueModel;
import pro.oncreate.easynet.models.NRequestMultipartModel;
import pro.oncreate.easynet.tasks.NTask;
import pro.oncreate.easynet.tasks.NTaskMultipart;


/**
 * Created by andrej on 15.11.15.
 */
public class NMultipartBuilder extends NBuilder {

    private NRequestMultipartModel requestModel;
    private NTask.NTaskListener taskListener;

    protected NMultipartBuilder() {
        super();
        requestModel = new NRequestMultipartModel();
        requestModel.setRequestType(NConst.MIME_TYPE_MULTIPART_FORM_DATA);
        requestModel.setMethod(POST);
        requestModel.setWriteLogs(true);
        requestModel.setNeedParse(true);
        requestModel.setEnableDefaultListeners(true);
        requestModel.setConnectTimeout(NTask.DEFAULT_TIMEOUT_CONNECT);
        requestModel.setReadTimeout(NTask.DEFAULT_TIMEOUT_READ);
    }

    public static NMultipartBuilder create() {
        return new NMultipartBuilder();
    }

    public NMultipartBuilder addTextParam(String key, String value) {
        requestModel.getParamsText().add(new NKeyValueModel(key, value));
        return this;
    }

    public NMultipartBuilder addFileParam(String key, File file) {
        requestModel.getParamsFile().add(new NKeyValueFileModel(key, file));
        return this;
    }

    public NMultipartBuilder setListener(NTask.NTaskListener listener) {
        this.taskListener = listener;
        return this;
    }

    @Override
    public void start() {
        if (validateRequest()) {
            NTaskMultipart task = new NTaskMultipart(taskListener, requestModel);
            task.execute();
        }
    }
}
