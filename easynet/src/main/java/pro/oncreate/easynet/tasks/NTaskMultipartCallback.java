package pro.oncreate.easynet.tasks;

import pro.oncreate.easynet.models.NBaseModel;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NRequestMultipartModel;

/**
 * Created by andrej on 04.10.16.
 */

public class NTaskMultipartCallback<T extends NBaseModel> extends NTaskCallback<T> {

    public NTaskMultipartCallback(Class<T> tClass) {
        super(tClass);
    }

    @Override
    public void start(NRequestModel requestModel) {
        if (requestModel instanceof NRequestMultipartModel)
            this.requestModel = (NRequestMultipartModel) requestModel;
        else
            throw new IllegalArgumentException("The request model in multipart request must have type NRequestMultipartModel");
        onStart(requestModel);
    }

    public void onStart(NRequestMultipartModel requestModel) {

    }

}
