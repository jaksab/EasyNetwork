package pro.oncreate.easynet.models;

import java.util.ArrayList;

/**
 * Created by andrej on 15.11.15.
 */
public class NRequestMultipartModel extends NRequestModel {

    private ArrayList<NKeyValueFileModel> paramsFile = new ArrayList<>();
    private ArrayList<NKeyValueModel> paramsText = new ArrayList<>();

    public NRequestMultipartModel() {
    }

    public ArrayList<NKeyValueFileModel> getParamsFile() {
        return paramsFile;
    }

    public void setParamsFile(ArrayList<NKeyValueFileModel> paramsFile) {
        this.paramsFile = paramsFile;
    }

    public ArrayList<NKeyValueModel> getParamsText() {
        return paramsText;
    }

    public void setParamsText(ArrayList<NKeyValueModel> paramsText) {
        this.paramsText = paramsText;
    }
}
