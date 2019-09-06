package pro.oncreate.easynetwork;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import pro.oncreate.easynet.EasyNet;
import pro.oncreate.easynet.data.NError;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.processing.NCallbackGson;
import pro.oncreate.easynetwork.models.TestModel;

public class DevActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);
        progressBar = findViewById(R.id.pb);
        findViewById(R.id.view).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
//        EasyNet.test(R.raw.test, getResources(), 3000)
//                .bind(progressBar, v)
//                .start(new NCallback() {
//                    @Override
//                    public void onSuccess(NResponseModel responseModel) {
//                        Toast.makeText(DevActivity.this, responseModel.getBody(), Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onFailed(NRequestModel nRequestModel, NErrors error) {
//                        Toast.makeText(DevActivity.this, "onFailed", Toast.LENGTH_LONG).show();
//                    }
//                });

        EasyNet.get()
                //.setUrl("https://oncreate.pro/1241") 404
                .setUrl("https://httpstat.us/502")
                .addParam("name", "д")
                .addParam("q", (String) null)
                .bind(progressBar, v)
                .start(new NCallbackGson<TestModel>(TestModel.class) {
                    @Override
                    public void onSuccess(TestModel model, NResponseModel responseModel) {
                        Toast.makeText(DevActivity.this, responseModel.getBody(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(NResponseModel responseModel) {
                        Toast.makeText(DevActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        Log.d("EasyNetLog", "Error" + responseModel.getStatusCode() + responseModel.getBody());
                    }

                    @Override
                    public void onFailed(NRequestModel nRequestModel, NError error) {
                        Toast.makeText(DevActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        Log.d("EasyNetLog", "Failed " + error.toString());
                        error.exception.printStackTrace();
                    }

                    @Override
                    public void onCacheLoaded(TestModel model, NResponseModel responseModel) {
                        super.onCacheLoaded(model, responseModel);
                    }

                    @Override
                    public void onCacheMissing(NRequestModel requestModel) {
                        super.onCacheMissing(requestModel);
                    }
                });
    }
}
