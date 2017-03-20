package pro.oncreate.easynet.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import pro.oncreate.easynet.models.subsidiary.NKeyValueModel;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused,WeakerAccess")
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

    public static String getQuery(List<NKeyValueModel> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NKeyValueModel pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(pair.getKey());
            result.append("=");
            result.append(pair.getValue());
        }

        return result.toString();
    }
}
