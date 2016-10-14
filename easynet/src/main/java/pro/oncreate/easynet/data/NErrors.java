package pro.oncreate.easynet.data;

import pro.oncreate.easynet.tasks.NTask;

/**
 * Copyright (c) $today.year. Konovalenko Andrii [jaksab2@mail.ru]
 */

public enum NErrors {
    /**
     * Error during connection process in {@link NTask#doInBackground}
     */
    CONNECTION_ERROR,
    /**
     * Error in parse model algorithm
     */
    PARSE_ERROR
}
