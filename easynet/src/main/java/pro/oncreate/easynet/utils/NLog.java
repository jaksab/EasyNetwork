package pro.oncreate.easynet.utils;

import android.util.Log;

/**
 * Created by andrej on 16.11.15.
 */
public class NLog {

    public static final String LOG_NAME_DEFAULT = "EasyNet";

    public static final String ERROR_REQUEST_NULL = "Request model is null";
    public static final String ERROR_EXCEPTION = "[Error]: ";
    public static final String ERROR_URL_EMPTY = "Url is empty";
    public static final String ERROR_URL_INVALID = "Url incorrect";

    public static final String DEBUG_START_CONNECTION = "======== START REQUEST ========";
    public static final String DEBUG_OPEN_CONNECTION = "[URL]: ";
    public static final String DEBUG_METHOD = "[Method]: ";
    public static final String DEBUG_RESPONSE_TIME = "[Response time]: ";
    public static final String DEBUG_HEADERS = "[Headers]: ";
    public static final String DEBUG_NO_BODY_PARAMS = "[No-body params]: ";
    public static final String DEBUG_BODY_PARAMS = "[Body params]: ";

    public static final String DEBUG_RESPONSE_CODE = "[Response code]: ";
    public static final String DEBUG_RESPONSE_BODY = "[Body]: ";
    public static final String DEBUG_RESPONSE_HEADERS_COUNT = "[Getting x headers]";
    public static final String DEBUG_RESPONSE_NO_BODY = "[Body=null]";
    public static final String DEBUG_RESPONSE_NO_HEADERS = "[Headers=null]";

    public static final String DEBUG_OPEN_CONNECTION_MULTIPART = "Multipart [URL]: ";

    public static void logD(String text) {
        Log.i(LOG_NAME_DEFAULT, text);
    }

    public static void logE(String error) {
        Log.e(LOG_NAME_DEFAULT, error);
    }
}
