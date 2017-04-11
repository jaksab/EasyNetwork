package pro.oncreate.easynetwork.api;

import pro.oncreate.easynet.EasyNet;
import pro.oncreate.easynet.Request;

/**
 * Created by Andrii Konovalenko, 2014-2017 years.
 * Copyright © 2017 [Andrii Konovalenko]. All Rights Reserved.
 */

public class API {

    public static String PAGINATION_LIMIT = "limit";
    public static String PAGINATION_OFFSET = "offset";

    public static Request get() {
        return EasyNet.get();
    }
}
