package pro.oncreate.easynetwork.models;

import org.json.JSONException;
import org.json.JSONObject;

import pro.oncreate.easynet.models.NBaseModel;
import pro.oncreate.easynet.models.NResponseModel;

/**
 * Created by andrej on 05.10.16.
 */

public class ExampleModel extends NBaseModel {

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public NBaseModel create(NResponseModel responseModel) {
        try {
            JSONObject jsonObject = new JSONObject(responseModel.getBody());
            this.status = jsonObject.optString("status");
            return this;

        } catch (JSONException e) {
            return super.create(responseModel);
        }
    }
}
