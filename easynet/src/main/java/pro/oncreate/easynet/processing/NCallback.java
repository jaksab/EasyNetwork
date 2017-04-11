package pro.oncreate.easynet.processing;

import pro.oncreate.easynet.EasyNet;
import pro.oncreate.easynet.models.NResponseModel;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused,WeakerAccess")
public class NCallback extends NBaseCallback {

    @Override
    public void finishUI(NResponseModel responseModel) {
        super.finishUI(responseModel);
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
    public void onRedirectInterrupted(String location, NResponseModel responseModel) {

    }

    @Override
    public void finish(NResponseModel responseModel) {
    }

    private void preSuccess(NResponseModel responseModel) {
        if ((EasyNet.getInstance().getOnSuccessDefaultListener() == null)
                || EasyNet.getInstance().getOnSuccessDefaultListener().onSuccess(responseModel))
            onSuccess(responseModel);
    }

    public void onSuccess(NResponseModel responseModel) {
    }

}
