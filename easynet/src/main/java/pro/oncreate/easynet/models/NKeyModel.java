package pro.oncreate.easynet.models;

/**
 * Created by andrej on 15.11.15.
 */
public class NKeyModel {

    protected String key;

    public NKeyModel(String key) {
        setKey(key);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
