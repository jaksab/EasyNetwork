package pro.oncreate.easynet.tasks;

import pro.oncreate.easynet.NConfig;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

public class NCallback extends NBaseCallback implements NTask.NTaskListener {

    @Override
    public void start(NRequestModel requestModel) {
        this.requestModel = requestModel;
        onStart(requestModel);
    }

    @Override
    public void finishUI(NResponseModel responseModel) {
        if (responseModel.statusType() == NResponseModel.STATUS_TYPE_SUCCESS) {
            if (requestModel.isEnableDefaultListeners())
                preSuccess(responseModel);
            else onSuccess(responseModel);

        } else if (responseModel.statusType() == NResponseModel.STATUS_TYPE_ERROR) {
            if (requestModel.isEnableDefaultListeners())
                preError(responseModel);
            else onError(responseModel);
        }
    }

    @Override
    public void finish(NResponseModel responseModel) {
    }

    private void preSuccess(NResponseModel responseModel) {
        if ((NConfig.getInstance().getOnSuccessDefaultListener() == null) || NConfig.getInstance().getOnSuccessDefaultListener().onSuccess(responseModel))
            onSuccess(responseModel);
    }

    public void onSuccess(NResponseModel responseModel) {
    }

}
