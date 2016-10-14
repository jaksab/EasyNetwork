package pro.oncreate.easynet.tasks;

import pro.oncreate.easynet.NConfig;
import pro.oncreate.easynet.data.NErrors;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

class NBaseCallback {

    protected NRequestModel requestModel;

    public void onStart(NRequestModel requestModel) {
    }

    protected void preError(NResponseModel responseModel) {
        if (NConfig.getInstance().getOnErrorDefaultListener() != null) {
            NConfig.getInstance().getOnErrorDefaultListener().onError(responseModel);
        } else {
            for (int i = 0; i < NConfig.getInstance().getOnErrorDefaultListenersCollection().size(); i++) {
                if (NConfig.getInstance().getOnErrorDefaultListenersCollection().get(i).getCode() == responseModel.getStatusCode()) {
                    NConfig.getInstance().getOnErrorDefaultListenersCollection().get(i).onError(responseModel);
                    break;
                }
            }
            onError(responseModel);
        }
    }

    public void onError(NResponseModel responseModel) {
    }

    void preFailed(NRequestModel nRequestModel, NErrors error) {
        if ((NConfig.getInstance().getOnFailedDefaultListener() != null && NConfig.getInstance().getOnFailedDefaultListener().onFailed(nRequestModel, error))
                || (NConfig.getInstance().getOnFailedDefaultListener() == null))
            onFailed(nRequestModel, error);
    }

    public void onFailed(NRequestModel nRequestModel, NErrors error) {
    }
}
