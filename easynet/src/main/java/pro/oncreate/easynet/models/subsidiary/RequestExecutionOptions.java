package pro.oncreate.easynet.models.subsidiary;

/**
 * Created by Andrii Konovalenko, 2014-2017 years.
 * Copyright © 2017 [Andrii Konovalenko]. All Rights Reserved.
 */

public class RequestExecutionOptions {

    public static final int NETWORK_ONLY = 1;
    public static final int CACHE_ONLY = 2;
    public static final int CACHE_AND_NETWORK = 3;

    private int requestExecutionType;

    public RequestExecutionOptions(int requestExecutionType) {
        this.requestExecutionType = requestExecutionType;
    }

    public int getRequestExecutionType() {
        return requestExecutionType;
    }

    public void setRequestExecutionType(int requestExecutionType) {
        this.requestExecutionType = requestExecutionType;
    }
}
