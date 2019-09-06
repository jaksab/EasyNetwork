package pro.oncreate.easynetwork;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import pro.oncreate.easynet.EasyNet;
import pro.oncreate.easynet.Request;
import pro.oncreate.easynet.data.NError;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EasyNet.getInstance()
                .setWriteLogs(false)
                .enableCache(getCacheDir(), 30)
                .setDefaultRequestListener(new EasyNet.RequestDefaultListener() {
                    @Override
                    public Request defaultConfig(Request request) {
                        return request
                                .setHost("http://oncreate.com.ua")
                                .addHeader("Accept-Language", Locale.getDefault().toString().replace("_", "-"))
                                .parallelExecution();
                    }
                })
                .setDefaultOnSuccessListener(new EasyNet.OnSuccessDefaultListener() {
                    @Override
                    public boolean onSuccess(NResponseModel responseModel) {
                        Toast.makeText(App.this, "Global success", Toast.LENGTH_LONG).show();
                        return true;
                    }
                })
                .setDefaultOnFailedListener(new EasyNet.OnFailedDefaultListener() {
                    @Override
                    public boolean onFailed(NRequestModel nRequestModel, NError error) {
                        Toast.makeText(App.this, "Global Failed" + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("EasyNetLog", "Global Failed " + error.type + " " + error.toString());
                        return true;
                    }
                })
                .setOnErrorDefaultListener(new EasyNet.OnErrorDefaultListener() {
                    @Override
                    public void onError(NResponseModel responseModel) {
                        Toast.makeText(App.this, "Global Error " + responseModel.getStatusCode() + " " + responseModel.getBody(), Toast.LENGTH_SHORT).show();
                        Log.d("EasyNetLog", "Global Error " + responseModel.getStatusCode() + " " + responseModel.getBody());
                    }
                })
                .addOnErrorDefaultListener(new EasyNet.OnErrorDefaultListenerWithCode(404) {
                    @Override
                    public void onError(NResponseModel responseModel) {
                        Toast.makeText(App.this, "Intercepted error: 404", Toast.LENGTH_SHORT).show();
                        Log.d("EasyNetLog", "Intercepted error: 404 " + responseModel.getStatusCode());
                    }
                });
    }
}
