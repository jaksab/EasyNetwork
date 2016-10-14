package pro.oncreate.easynet.models;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
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
