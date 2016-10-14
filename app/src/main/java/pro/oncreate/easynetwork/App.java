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

        NConfig netConfig = NConfig.getInstance();
        netConfig.setWriteLogs(true); // Default
        netConfig.setDefaultNBuilderListener(new NConfig.NBuilderDefaultListener() {
            @Override
            public NBuilder defaultConfig(NBuilder nBuilder) {
                nBuilder.setHost("https://api.infitting.com/v1.1");
                nBuilder.addHeader("Accept-Language", Locale.getDefault().toString().replace("_", "-"));
                return nBuilder;
            }
        });
        netConfig.setDefaultOnSuccessListener(new NConfig.OnSuccessDefaultListener() {
            @Override
            public boolean onSuccess(NResponseModel responseModel) {
                Toast.makeText(App.this, "PreSuccess", Toast.LENGTH_LONG).show();
                return true;
            }
        });
        netConfig.setDefaultOnFailedListener(new NConfig.OnFailedDefaultListener() {
            @Override
            public boolean onFailed(NRequestModel nRequestModel, NErrors error) {
                Toast.makeText(App.this, "PreFailed", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        netConfig.addOnErrorDefaultListener(new NConfig.OnErrorDefaultListenerWithCode(422) {
            @Override
            public void onError(NResponseModel responseModel) {
                Toast.makeText(App.this, "Intercepted error: 422", Toast.LENGTH_SHORT).show();
            }
        });
        netConfig.addOnErrorDefaultListener(new NConfig.OnErrorDefaultListenerWithCode(404) {
            @Override
            public void onError(NResponseModel responseModel) {
                Toast.makeText(App.this, "Intercepted error: 404", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
