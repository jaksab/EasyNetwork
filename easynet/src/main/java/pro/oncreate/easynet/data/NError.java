package pro.oncreate.easynet.data;

import android.support.annotation.NonNull;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused,WeakerAccess")
public class NError {

    /**
     * Error during connection process in {@link pro.oncreate.easynet.processing.BaseTask#doInBackground}
     */
    public static final int TYPE_CONNECTION_ERROR = 1;

    /**
     * Error in parse model algorithm
     */
    public static final int TYPE_PARSE_ERROR = 2;

    /**
     * Redirect was interrupted
     */
    public static final int TYPE_REDIRECT_INTERRUPT = 3;

    /**
     * Redirect was interrupted
     */
    public static final int TYPE_UNKNOWN_ERROR = 0;

    public int type;
    public Exception exception;

    public NError(int type, Exception exception) {
        this.type = type;
        this.exception = exception;
    }

    NError() {
        type = TYPE_UNKNOWN_ERROR;
    }

    @NonNull
    @Override
    public String toString() {
        if (exception == null) {
            switch (type) {
                case TYPE_CONNECTION_ERROR:
                    return "Connection failed";
                case TYPE_PARSE_ERROR:
                    return "Parsing error";
                case TYPE_REDIRECT_INTERRUPT:
                    return "Redirect interrupted";
                default:
                    return "Unknown error";
            }
        } else {
            return exception.toString();
        }
    }
}
