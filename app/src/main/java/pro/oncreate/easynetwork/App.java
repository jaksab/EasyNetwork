package pro.oncreate.easynetwork;

import android.app.Application;
import android.widget.Toast;

import java.util.Locale;

import pro.oncreate.easynet.Request;
import pro.oncreate.easynet.EasyNet;
import pro.oncreate.easynet.data.NErrors;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynetwork.api.API;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EasyNet.getInstance()
                .setWriteLogs(true)
                .setDefaultNBuilderListener(new EasyNet.NBuilderDefaultListener() {
                    @Override
                    public Request defaultConfig(Request request) {
                        return request
                                .setHost("http://oncreate.com.ua")
                                .addHeader("Accept-Language", Locale.getDefault().toString().replace("_", "-"))
                                .configurePagination(API.PAGINATION_LIMIT, API.PAGINATION_OFFSET);
                    }
                })
                .setDefaultOnSuccessListener(new EasyNet.OnSuccessDefaultListener() {
                    @Override
                    public boolean onSuccess(NResponseModel responseModel) {
                        Toast.makeText(App.this, "Pre success", Toast.LENGTH_LONG).show();
                        return true;
                    }
                })
                .setDefaultOnFailedListener(new EasyNet.OnFailedDefaultListener() {
                    @Override
                    public boolean onFailed(NRequestModel nRequestModel, NErrors error) {
                        Toast.makeText(App.this, "Pre failed", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                })
                .addOnErrorDefaultListener(new EasyNet.OnErrorDefaultListenerWithCode(404) {
                    @Override
                    public void onError(NResponseModel responseModel) {
                        Toast.makeText(App.this, "Intercepted error: 404", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
