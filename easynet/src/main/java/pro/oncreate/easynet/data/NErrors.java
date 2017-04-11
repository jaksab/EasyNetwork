package pro.oncreate.easynet.data;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

@SuppressWarnings("unused,WeakerAccess")
public enum NErrors {
    /**
     * Error during connection process in {@link pro.oncreate.easynet.processing.BaseTask#doInBackground}
     */
    CONNECTION_ERROR,
    /**
     * Error in parse model algorithm
     */
    PARSE_ERROR,
    /**
     * Redirect was interrupted
     */
    REDIRECT_INTERRUPT
}
