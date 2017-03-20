package pro.oncreate.easynet.utils;

import java.util.List;

/**
 * Created by Andrii Konovalenko, 2014-2017 years.
 * Copyright © 2017 [Andrii Konovalenko]. All Rights Reserved.
 */

public class HttpUtlis {

    public static int getIntValue(List<String> values) {
        if (values != null && !values.isEmpty() && isNumeric(values.get(0)))
            return Integer.parseInt(values.get(0));
        else return -1;
    }

    private static boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }
}
