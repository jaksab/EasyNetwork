package pro.oncreate.easynet.models;

import java.io.File;

/**
 * Created by andrej on 15.11.15.
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
