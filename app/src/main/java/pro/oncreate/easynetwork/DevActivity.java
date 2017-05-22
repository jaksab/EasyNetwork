package pro.oncreate.easynetwork;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import pro.oncreate.easynet.EasyNet;
import pro.oncreate.easynet.PaginationModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.models.subsidiary.RequestExecutionOptions;
import pro.oncreate.easynet.processing.NCallback;

public class DevActivity extends AppCompatActivity implements PaginationModel.PaginationInterface {

    private ProgressBar progressBar;
    private FrameLayout view, view2, view3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);
        progressBar = (ProgressBar) findViewById(R.id.pb);
        view = (FrameLayout) findViewById(R.id.view);
        view2 = (FrameLayout) findViewById(R.id.view2);
        view3 = (FrameLayout) findViewById(R.id.view3);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
    }

    private void start() {
        EasyNet.get().setUrl("https://api.letshindig.com", "v1/users/privacy-policy")
                .cacheResponse()
                .startOptions(new RequestExecutionOptions(RequestExecutionOptions.CACHE_AND_NETWORK))
                .start(new NCallback() {
                    @Override
                    public void onSuccess(NResponseModel responseModel) {
                        super.onSuccess(responseModel);
                    }
                });
    }

    @Override
    public Integer getPaginationValue(String key) {
        int value = 0;
        switch (key) {
            case "limit":
                value = 10;
                break;
            case "offset":
                value = (int) (System.currentTimeMillis() / 1000000000);
                break;
        }
        return value;
    }
}
