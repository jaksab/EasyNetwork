package pro.oncreate.easynet.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import pro.oncreate.easynet.models.NKeyValueModel;


/**
 * Created by andrej on 15.11.15.
 */
public class NDataBuilder {

    public static String getQuery(List<NKeyValueModel> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NKeyValueModel pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
