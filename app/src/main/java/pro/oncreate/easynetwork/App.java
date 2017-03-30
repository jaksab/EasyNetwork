package pro.oncreate.easynetwork;

import android.app.Application;
import android.widget.Toast;

import java.util.Locale;

import pro.oncreate.easynet.NBuilder;
import pro.oncreate.easynet.NConfig;
import pro.oncreate.easynet.data.NErrors;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NConfig.getInstance()
                .setWriteLogs(true)
                .setDefaultNBuilderListener(new NConfig.NBuilderDefaultListener() {
                    @Override
                    public NBuilder defaultConfig(NBuilder nBuilder) {
                        return nBuilder
                                .setHost("http://oncreate.com.ua")
                                .addHeader("Accept-Language", Locale.getDefault().toString().replace("_", "-"));
                    }
                })
                .setDefaultOnSuccessListener(new NConfig.OnSuccessDefaultListener() {
                    @Override
                    public boolean onSuccess(NResponseModel responseModel) {
                        Toast.makeText(App.this, "Pre success", Toast.LENGTH_LONG).show();
                        return true;
                    }
                })
                .setDefaultOnFailedListener(new NConfig.OnFailedDefaultListener() {
                    @Override
                    public boolean onFailed(NRequestModel nRequestModel, NErrors error) {
                        Toast.makeText(App.this, "Pre failed", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                })
                .addOnErrorDefaultListener(new NConfig.OnErrorDefaultListenerWithCode(404) {
                    @Override
                    public void onError(NResponseModel responseModel) {
                        Toast.makeText(App.this, "Intercepted error: 404", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
