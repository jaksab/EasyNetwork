package pro.oncreate.easynet.models.subsidiary;

/**
 * Created by Andrii Konovalenko, 2014-2017 years.
 * Copyright © 2017 [Andrii Konovalenko]. All Rights Reserved.
 */

public class RequestExecutionOptions {

    public static final int NETWORK_ONLY = 1;
    public static final int CACHE_ONLY = 2;
    public static final int CACHE_AND_NETWORK = 3;

    public static final RequestExecutionOptions NETWORK_ONLY_OPTIONS
            = new RequestExecutionOptions(NETWORK_ONLY);
    public static final RequestExecutionOptions CACHE_ONLY_OPTIONS
            = new RequestExecutionOptions(CACHE_ONLY);
    public static final RequestExecutionOptions CACHE_AND_NETWORK_OPTIONS
            = new RequestExecutionOptions(CACHE_AND_NETWORK);

    private int requestExecutionType;

    private RequestExecutionOptions(int requestExecutionType) {
        this.requestExecutionType = requestExecutionType;
    }

    public int getRequestExecutionType() {
        return requestExecutionType;
    }
}
