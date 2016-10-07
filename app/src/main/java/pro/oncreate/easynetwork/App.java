package pro.oncreate.easynetwork;

import android.app.Application;
import android.widget.Toast;

import pro.oncreate.easynet.NBuilder;
import pro.oncreate.easynet.NConfig;
import pro.oncreate.easynet.models.NBaseModel;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.tasks.NTaskCallback;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        NConfig netConfig = NConfig.getInstance();
        netConfig.setDefaultNBuilderListener(new NConfig.NBuilderDefaultListener() {
            @Override
            public NBuilder defaultConfig(NBuilder nBuilder) {
                nBuilder.setHost("http://maps.google.com/maps/api");
                nBuilder.addHeader("Accept-Language", "ru-Ru");
                nBuilder.writeLogs(true);
                return nBuilder;
            }
        });
        netConfig.setDefaultOnSuccessListener(new NConfig.OnSuccessDefaultListener() {
            @Override
            public boolean onSuccess(NBaseModel model, NResponseModel responseModel) {
                Toast.makeText(App.this, "PreSuccess", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        netConfig.setDefaultOnFailedListener(new NConfig.OnFailedDefaultListener() {
            @Override
            public boolean onFailed(NRequestModel nRequestModel, NTaskCallback.Errors error) {
                Toast.makeText(App.this, "PreFailed", Toast.LENGTH_SHORT).show();
                return true;
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
