package pro.oncreate.easynetwork.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by andrej on 10.10.16.
 */

public class TestModel /*extends NBaseModel*/ {

    @SerializedName("name")
    String name;

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

//    @Override
//    public TestModel parse(NResponseModel responseModel, JSONObject jsonObject) {
////        try {
//        try {
//            this.name = jsonObject.getInt("name");
//        } catch (JSONException e) {
//            throw new IllegalArgumentException(e.toString());
//        }
//        return this;
////        } catch (Exception e) {
////            return null;
////        }
//    }
}
