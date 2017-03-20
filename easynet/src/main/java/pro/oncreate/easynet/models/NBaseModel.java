package pro.oncreate.easynet.models;

import org.json.JSONObject;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused,WeakerAccess")
public abstract class NBaseModel {

    public abstract NBaseModel parse(NResponseModel responseModel, JSONObject jsonObject);
}
