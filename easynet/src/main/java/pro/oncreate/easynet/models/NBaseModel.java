package pro.oncreate.easynet.models;

import org.json.JSONObject;

/**
 * Created by andrej on 05.10.16.
 */

public abstract class NBaseModel {

    public abstract NBaseModel parse(NResponseModel responseModel, JSONObject jsonObject);

    public JSONObject toJSONObject() {
        return new JSONObject();
    }
}
