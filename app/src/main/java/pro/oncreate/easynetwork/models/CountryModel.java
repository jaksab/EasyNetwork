package pro.oncreate.easynetwork.models;

import org.json.JSONObject;

import pro.oncreate.easynet.models.NBaseModel;
import pro.oncreate.easynet.models.NResponseModel;

/**
 * Created by andrej on 10.10.16.
 */

public class CountryModel extends NBaseModel {

    long id;
    String code;
    String name;

    public CountryModel() {
    }

    public CountryModel(long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public CountryModel parse(NResponseModel responseModel, JSONObject jsonObject) {
        try {
            this.id = jsonObject.getLong("id");
            this.code = jsonObject.getString("code");
            this.name = jsonObject.getString("name");
            return this;
        } catch (Exception e) {
            return null;
        }
    }
}
