package pro.oncreate.easynet.models.subsidiary;

import android.view.View;

/**
 * Created by Andrii Konovalenko, 2014-2017 years.
 * Copyright © 2017 [Andrii Konovalenko]. All Rights Reserved.
 */

public class BindView {

    private View view;
    private BindParams params;

    public BindView(View view, BindParams params) {
        this.view = view;
        this.params = params;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public BindParams getParams() {
        return params;
    }

    public void setParams(BindParams params) {
        this.params = params;
    }


    private int getIntegerData() {
        if (params.getData() != null &&
                params.getData().length > 0 && params.getData()[0] instanceof Integer)
            return (int) params.getData()[0];
        else return -1;
    }

    public void onStart() {
        switch (params.getType()) {
            case HIDE_AND_SHOW_AFTER:
                int visibility = getIntegerData();
                if (visibility == View.INVISIBLE || visibility == View.GONE)
                    view.setVisibility(View.GONE);
                else view.setVisibility(View.INVISIBLE);
                break;
            case SHOW_AND_HIDE_AFTER:
                view.setVisibility(View.VISIBLE);
                break;
            case DISABLE_AND_ENABLE_AFTER:
                view.setEnabled(true);
                break;
            case ENABLE_AND_DISABLE_AFTER:
                view.setEnabled(false);
                break;
        }
    }

    public void onSuccess() {
        if (params.getExtra() == BindParams.Extra.IF_ERROR)
            return;

        switch (params.getType()) {
            case HIDE_AND_SHOW_AFTER:
                view.setVisibility(View.VISIBLE);
                break;
            case SHOW_AND_HIDE_AFTER:
                int visibility = getIntegerData();
                if (visibility == View.INVISIBLE || visibility == View.GONE)
                    view.setVisibility(View.GONE);
                else view.setVisibility(View.INVISIBLE);
                break;
            case DISABLE_AND_ENABLE_AFTER:
                view.setEnabled(false);
                break;
            case ENABLE_AND_DISABLE_AFTER:
                view.setEnabled(true);
                break;
        }
    }

    public void onError() {
        if (params.getExtra() == BindParams.Extra.IF_SUCCESS)
            return;

        switch (params.getType()) {
            case HIDE_AND_SHOW_AFTER:
                view.setVisibility(View.VISIBLE);
                break;
            case SHOW_AND_HIDE_AFTER:
                int visibility = getIntegerData();
                if (visibility == View.INVISIBLE || visibility == View.GONE)
                    view.setVisibility(View.GONE);
                else view.setVisibility(View.INVISIBLE);
                break;
            case DISABLE_AND_ENABLE_AFTER:
                view.setEnabled(false);
                break;
            case ENABLE_AND_DISABLE_AFTER:
                view.setEnabled(true);
                break;
        }
    }
}
