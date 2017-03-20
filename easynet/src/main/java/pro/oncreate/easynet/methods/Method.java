package pro.oncreate.easynet.methods;

import pro.oncreate.easynet.data.NConst;

/**
 * Created by Andrii Konovalenko, 2014-2017 years.
 * Copyright © 2017 [Andrii Konovalenko]. All Rights Reserved.
 */

@SuppressWarnings("unused,WeakerAccess")
public abstract class Method {

    public abstract String name();

    public abstract boolean withBody();

    public static Method getMethodByName(String name) {
        Method method = GET;
        switch (name) {
            case NConst.GET:
                method = GET;
                break;
            case NConst.POST:
                method = POST;
                break;
            case NConst.PUT:
                method = PUT;
                break;
            case NConst.DELETE:
                method = DELETE;
                break;
            case NConst.HEAD:
                method = HEAD;
                break;
            case NConst.OPTIONS:
                method = OPTIONS;
                break;
        }
        return method;
    }

    public static final QueryMethod GET = new QueryMethod() {
        @Override
        public String name() {
            return "GET";
        }
    };

    public static final EntityMethod POST = new EntityMethod() {
        @Override
        public String name() {
            return "POST";
        }
    };

    public static final EntityMethod PUT = new EntityMethod() {
        @Override
        public String name() {
            return "PUT";
        }
    };

    public static final QueryMethod DELETE = new QueryMethod() {
        @Override
        public String name() {
            return "DELETE";
        }
    };

    public static final EntityMethod OPTIONS = new EntityMethod() {
        @Override
        public String name() {
            return "OPTIONS";
        }
    };

    public static final QueryMethod HEAD = new QueryMethod() {
        @Override
        public String name() {
            return "HEAD";
        }
    };

    @Override
    public String toString() {
        return name();
    }
}
