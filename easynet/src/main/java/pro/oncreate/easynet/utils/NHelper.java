package pro.oncreate.easynet.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

import pro.oncreate.easynet.models.NResponseModel;


/**
 * Created by andrej on 17.11.15.
 */
public class NHelper {

    static public boolean isActiveInternet(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            if (nInfo == null || !nInfo.isConnected())
                return false;
            else
                return true;
        } catch (Exception e) {
            return false;
        }
    }

    static public String getFirstHeader(NResponseModel responseModel, String header) {
        if (responseModel != null && responseModel.getHeaders() != null) {
            List<String> headers = responseModel.getHeaders().get(header);
            if (headers != null && !headers.isEmpty())
                return headers.get(0);
            else return null;
        } else {
            return null;
        }
    }

    static public int getIntHeader(NResponseModel responseModel, String header) {
        String value = getFirstHeader(responseModel, header);
        if (value != null) {
            return Integer.valueOf(value);
        } else return 0;
    }
}
