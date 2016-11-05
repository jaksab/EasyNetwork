package pro.oncreate.easynet.models.subsidiary;

import java.io.File;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */
public class NKeyValueFileModel extends NKeyModel {

    private File file;

    public NKeyValueFileModel(String key, File value) {
        super(key);
        this.file = value;
    }

    public File getValue() {
        return file;
    }

    public void setValue(File value) {
        this.file = value;
    }
}
