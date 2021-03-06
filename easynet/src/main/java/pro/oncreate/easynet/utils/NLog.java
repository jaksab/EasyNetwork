package pro.oncreate.easynet.utils;

import android.util.Log;

import pro.oncreate.easynet.EasyNet;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("WeakerAccess")
public class NLog {

    public static final String LOG_NAME_DEFAULT = "EasyNetwork";

    public static void logD(String text) {
        if (EasyNet.getInstance().isWriteLogs())
            Log.i(LOG_NAME_DEFAULT, text);
    }

    public static void logE(String error) {
        Log.e(LOG_NAME_DEFAULT, error);
    }
}
