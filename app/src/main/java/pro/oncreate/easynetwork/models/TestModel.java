package pro.oncreate.easynetwork.models;

import org.json.JSONObject;

import pro.oncreate.easynet.models.NBaseModel;
import pro.oncreate.easynet.models.NResponseModel;

/**
 * Created by andrej on 10.10.16.
 */

public class TestModel extends NBaseModel {

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public TestModel parse(NResponseModel responseModel, JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("status");
            return this;
        } catch (Exception e) {
            return null;
        }
    }
}
