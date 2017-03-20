package pro.oncreate.easynet.models.subsidiary;

/**
 * Created by Andrii Konovalenko, 2014-2017 years.
 * Copyright © 2017 [Andrii Konovalenko]. All Rights Reserved.
 */

public class BindParams {

    public enum Type {
        HIDE_AND_SHOW_AFTER,
        SHOW_AND_HIDE_AFTER,
        DISABLE_AND_ENABLE_AFTER,
        ENABLE_AND_DISABLE_AFTER,
    }

    public enum Extra {
        IF_SUCCESS,
        IF_ERROR,
        ALWAYS
    }

    private Type type;
    private Extra extra;
    private Object[] data;

    public BindParams(Type type) {
        this.type = type;
        this.extra = Extra.ALWAYS;
    }

    public BindParams(Type type, Extra extra) {
        this.type = type;
        this.extra = extra;
    }

    public BindParams(Type type, Object... data) {
        this(type);
        this.data = data;
    }

    public BindParams(Type type, Extra extra, Object... data) {
        this.type = type;
        this.extra = extra;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public Extra getExtra() {
        return extra;
    }

    public Object[] getData() {
        return data;
    }
}
