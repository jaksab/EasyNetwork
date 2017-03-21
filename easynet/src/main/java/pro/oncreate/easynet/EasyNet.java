package pro.oncreate.easynet;

import pro.oncreate.easynet.data.NConst;

import static pro.oncreate.easynet.data.NConst.DELETE;
import static pro.oncreate.easynet.data.NConst.GET;
import static pro.oncreate.easynet.data.NConst.HEAD;
import static pro.oncreate.easynet.data.NConst.OPTIONS;
import static pro.oncreate.easynet.data.NConst.POST;
import static pro.oncreate.easynet.data.NConst.PUT;

/**
 * Created by Andrii Konovalenko, 2014-2017 years.
 * Copyright © 2017 [Andrii Konovalenko]. All Rights Reserved.
 */

@SuppressWarnings("unused,WeakerAccess")
public class EasyNet {

    public static NBuilder get() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(GET);
    }

    public static NBuilder post() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(POST);
    }

    public static NBuilder put() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(PUT);
    }

    public static NBuilder delete() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(DELETE);
    }

    public static NBuilder opt() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(OPTIONS);
    }

    public static NBuilder head() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setMethod(HEAD);
    }

    public static NBuilder multipart() {
        return NConfig.getInstance()
                .getDefaultNBuilder()
                .setContentType(NConst.MIME_TYPE_MULTIPART_FORM_DATA);
    }

    public static NBuilder get(String path, Object... params) {
        return get().setPath(path, params);
    }

    public static NBuilder post(String path, Object... params) {
        return post().setPath(path, params);
    }

    public static NBuilder put(String path, Object... params) {
        return put().setPath(path, params);
    }

    public static NBuilder delete(String path, Object... params) {
        return delete().setPath(path, params);
    }

    public static NBuilder opt(String path, Object... params) {
        return opt().setPath(path, params);
    }

    public static NBuilder head(String path, Object... params) {
        return head().setPath(path, params);
    }

    public static NBuilder multipart(String path, Object... params) {
        return multipart().setPath(path, params);
    }
}
