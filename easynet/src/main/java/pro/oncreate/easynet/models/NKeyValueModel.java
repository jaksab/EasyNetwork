package pro.oncreate.easynet.models;

/**
 * Created by andrej on 15.11.15.
 */
public class NKeyValueModel extends NKeyModel {

    private String value;

    public NKeyValueModel(String key, String value) {
        super(key);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
