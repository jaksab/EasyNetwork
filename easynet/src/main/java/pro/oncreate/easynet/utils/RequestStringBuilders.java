package pro.oncreate.easynet.utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import java.util.List;
import java.util.Map;

import pro.oncreate.easynet.http.Net;

/**
 * Created by Konovalenko A., onCreate team on 03.08.2015.
 */
@Deprecated
public class RequestStringBuilders {

    public static final String EMPTY_STRING = "";
    public static final String DEFAULT_DIALOG_LOADING_MESSAGE = "Loading";
    public static final String DEFAULT_USER_AGENT = "oncreate.com.ua user agent";

    // Build headers in request

    public static HttpPost addAllHeaders(HttpPost post, Map<String, String> headers, List<String> headersKey) {
        for (int i = 0; i < headers.size(); i++)
            post.addHeader(headersKey.get(i),
                    headers.get(headersKey.get(i)));

        return post;
    }

    public static HttpGet addAllHeaders(HttpGet get, Map<String, String> headers, List<String> headersKey) {
        for (int i = 0; i < headers.size(); i++)
            get.addHeader(headersKey.get(i),
                    headers.get(headersKey.get(i)));

        return get;
    }

    public static HttpPut addAllHeaders(HttpPut put, Map<String, String> headers, List<String> headersKey) {
        for (int i = 0; i < headers.size(); i++)
            put.addHeader(headersKey.get(i),
                    headers.get(headersKey.get(i)));

        return put;
    }

    public static HttpDelete addAllHeaders(HttpDelete delete, Map<String, String> headers, List<String> headersKey) {
        for (int i = 0; i < headers.size(); i++)
            delete.addHeader(headersKey.get(i),
                    headers.get(headersKey.get(i)));

        return delete;
    }

    // Build params in request string

    public static HttpGet addRequestParams(Net net, HttpGet get, List<NameValuePair> entityValues) {
        String add_params = EMPTY_STRING;

        for (int i = 0; i < entityValues.size(); i++) {
            if (i == 0)
                add_params = "?";
            else
                add_params += "&";

            add_params += entityValues.get(i).getName() + "=";
            add_params += entityValues.get(i).getValue();
        }

        if (!add_params.equals(EMPTY_STRING)) {
            net.setURL(net.getURL() + add_params);
            get = new HttpGet(net.getURL());
        }

        return get;
    }

    public static HttpDelete addRequestParams(Net net, HttpDelete delete, List<NameValuePair> entityValues) {
        String add_params = EMPTY_STRING;

        for (int i = 0; i < entityValues.size(); i++) {
            if (i == 0)
                add_params = "?";
            else
                add_params += "&";

            add_params += entityValues.get(i).getName() + "=";
            add_params += entityValues.get(i).getValue();
        }

        if (!add_params.equals(EMPTY_STRING)) {
            net.setURL(net.getURL() + add_params);
            delete = new HttpDelete(net.getURL());
        }

        return delete;
    }
}
