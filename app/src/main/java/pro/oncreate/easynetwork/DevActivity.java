package pro.oncreate.easynetwork;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import pro.oncreate.easynet.EasyNet;
import pro.oncreate.easynet.data.NErrors;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.processing.BaseTask;
import pro.oncreate.easynet.processing.NCallbackParse;
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

        EasyNet.get().setUrl("https://api.infitting.com/v1.1/countries/231/cities")
                .addParam("name", "д")
                .addParam("q", (String) null)
                .bind(progressBar, v)
                .cacheResponse()
                .start(BaseTask.CacheOptions.CACHE_AND_NETWORK, new NCallbackParse<TestModel>(TestModel.class) {
                    @Override
                    public void onSuccess(TestModel model, NResponseModel responseModel) {
                        super.onSuccess(model, responseModel);
                        Toast.makeText(DevActivity.this, responseModel.getBody(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(NResponseModel responseModel) {
                        super.onError(responseModel);
                    }

                    @Override
                    public void onFailed(NRequestModel nRequestModel, NErrors error) {
                        super.onFailed(nRequestModel, error);
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
