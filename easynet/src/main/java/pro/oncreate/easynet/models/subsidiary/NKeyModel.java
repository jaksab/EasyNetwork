package pro.oncreate.easynet.models.subsidiary;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
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
