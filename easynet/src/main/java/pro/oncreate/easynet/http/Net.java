package pro.oncreate.easynet.http;

import android.content.Context;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.oncreate.easynet.interfaces.ConnectionListener;
import pro.oncreate.easynet.tasks.NetMultipartTask;
import pro.oncreate.easynet.tasks.NetTask;
import pro.oncreate.easynet.utils.RequestStringBuilders;

@Deprecated
public class Net {

    // Global data
    public static final String GET = "get", POST = "post", PUT = "put", DELETE = "delete";

    @Deprecated
    public static final String METHOD_GET = "GET", METHOD_POST = "POST",
            METHOD_PUT = "PUT", METHOD_DELETE = "DELETE";

    // Private data
    private Context context;

    // Request data
    private String method;
    private String url;
    private Map<String, String> headers;
    private List<String> headersKey;
    private List<NameValuePair> entityValues;

    // View data
    private boolean showDialog = false;

    // Other data
    private String dialogMessage = RequestStringBuilders.EMPTY_STRING;
    private String userAgent = RequestStringBuilders.DEFAULT_USER_AGENT;

    //
    // Setting request

    public Net(Context context) {
        this(context, GET, RequestStringBuilders.EMPTY_STRING, false);
    }

    public Net(Context context, String method, String url, boolean showDialog) {
        this.context = context;
        this.method = method;
        this.url = url;
        this.showDialog = showDialog;

        headers = new HashMap<String, String>();
        headersKey = new ArrayList<String>();
        entityValues = new ArrayList<NameValuePair>();
    }

    public boolean setMethod(String method) {
        if (method.equals(METHOD_GET) || method.equals(METHOD_POST)
                || method.equals(METHOD_PUT) || method.equals(METHOD_DELETE)) {
            this.method = method;
            return true;
        } else if (method.equals(GET) || method.equals(POST)
                || method.equals(PUT) || method.equals(DELETE)) {
            this.method = method;
            return true;
        } else
            throw new IllegalArgumentException();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getURL() {
        return url;
    }

    public void setProgressDialogEnable(boolean showDialog) {
        this.showDialog = showDialog;
    }

    public boolean isProgressDialogEnable() {
        return this.showDialog;
    }

    public boolean addHeader(String name, String value) {
        try {
            headers.put(name, value);
            headersKey.add(name);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void clearHeaders() {
        headers.clear();
        headersKey.clear();
    }

    public void addEntityValue(String name, String value) {
        this.entityValues.add(new BasicNameValuePair(name, value));
    }

    public void clearEntity() {
        this.entityValues.clear();
    }

    public boolean connect(ConnectionListener listener) {
        if (NetSettings.isInternetEnabled(context)) {
            if (this.method == METHOD_POST || this.method.equals(POST)) {
                new NetTask(this, createPostRequest(), listener).execute();

            } else if (this.method == METHOD_GET || this.method.equals(GET)) {
                new NetTask(this, createGetRequest(), listener).execute();

            } else if (this.method == METHOD_PUT || this.method.equals(PUT)) {
                new NetTask(this, createPutRequest(), listener).execute();

            } else if (this.method == METHOD_DELETE || this.method.equals(DELETE)) {
                new NetTask(this, createDeleteRequest(), listener).execute();
            }
            return true;
        }
        return false;
    }

    public HttpPost createPostRequest() {
        HttpPost post = new HttpPost(url);
        try {
            post.setEntity(new UrlEncodedFormEntity(entityValues, HTTP.UTF_8));
            post = RequestStringBuilders.addAllHeaders(post, headers, headersKey);
            return post;
        } catch (Exception e) {
            return null;
        }
    }

    public HttpGet createGetRequest() {
        HttpGet get = new HttpGet(url);
        try {
            get = RequestStringBuilders.addRequestParams(this, get, entityValues);
            get = RequestStringBuilders.addAllHeaders(get, headers, headersKey);
            return get;
        } catch (Exception e) {
            return get;
        }
    }

    public HttpPut createPutRequest() {
        HttpPut put = new HttpPut(url);
        try {
            put.setEntity(new UrlEncodedFormEntity(entityValues, HTTP.UTF_8));
            put = RequestStringBuilders.addAllHeaders(put, headers, headersKey);
            return put;
        } catch (Exception e) {
            return null;
        }
    }

    public HttpDelete createDeleteRequest() {
        HttpDelete delete = new HttpDelete(url);
        try {
            delete = RequestStringBuilders.addRequestParams(this, delete, entityValues);
            delete = RequestStringBuilders.addAllHeaders(delete, headers, headersKey);
            return delete;
        } catch (Exception e) {
            return null;
        }
    }

    //
    // Manage data

    public String getDialogMessage() {
        return dialogMessage;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    //
    // Multipart request

    private ArrayList<BasicNameValuePair> multiPartsStrings = new ArrayList<BasicNameValuePair>();
    private ArrayList<BasicNameValuePair> multiPartsFiles = new ArrayList<BasicNameValuePair>();

    public void addStringPart(String name, String value) {
        multiPartsStrings.add(new BasicNameValuePair(name, value));
    }

    public void addFilePart(String name, String filePath) {
        multiPartsFiles.add(new BasicNameValuePair(name, filePath));
    }

    public void clearMultiParts() {
        multiPartsStrings.clear();
        multiPartsFiles.clear();
    }

    public void connectMultipart(ConnectionListener listener) {
        try {
            HttpPost postMulti = new HttpPost(url);

            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            for (int i = 0; i < multiPartsStrings.size(); i++)
                multipartEntity.addPart(multiPartsStrings.get(i).getName(), new StringBody(multiPartsStrings.get(i).getValue(), Charset.forName("UTF-8")));

            for (int i = 0; i < multiPartsFiles.size(); i++)
                multipartEntity.addPart(multiPartsFiles.get(i).getName(), new FileBody(new File(multiPartsFiles.get(i).getValue())));

            postMulti = RequestStringBuilders.addAllHeaders(postMulti, headers, headersKey);

            postMulti.setEntity(multipartEntity);

            new NetMultipartTask(this, postMulti, listener).execute();


            // New method

//            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//
//            for (int i = 0; i < multiPartsStrings.size(); i++)
//                builder.addTextBody(multiPartsStrings.get(i).getName(), multiPartsStrings.get(i).getValue());
//
//            for (int i = 0; i < multiPartsFiles.size(); i++)
//                builder.addPart(multiPartsFiles.get(i).getName(), new FileBody(new File(multiPartsFiles.get(i).getValue())));
//
//            postMulti.setEntity(builder.build());

            // new NetMultipartTask(this, postMulti, listener).execute();

        } catch (Exception e) {
        }
    }
}