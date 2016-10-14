package pro.oncreate.easynet.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import pro.oncreate.easynet.models.NKeyValueModel;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */
public class NDataBuilder {

    public static String getQuery(List<NKeyValueModel> params, String charset) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NKeyValueModel pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getKey(), charset));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), charset));
        }

        return result.toString();
    }
}
