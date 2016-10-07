package pro.oncreate.easynetwork;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import pro.oncreate.easynet.NBuilder;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.tasks.NTaskCallback;
import pro.oncreate.easynetwork.models.ExampleModel;

public class DemoActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvContent;
    private ProgressBar bar1;
    private EditText edtToolbar;
    private ImageButton ibtnGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        tvContent = (TextView) findViewById(R.id.content);
        edtToolbar = (EditText) findViewById(R.id.toolbar_edittext);
        ibtnGo = (ImageButton) findViewById(R.id.toolbar_go);
        bar1 = (ProgressBar) findViewById(R.id.progressBar);

        ibtnGo.setOnClickListener(this);
        ibtnGo.performClick();
    }

    @Override
    public void onClick(View view) {
        NBuilder.create()
                .setPath("geocode/json?address=Ukraine")
                .setListener(new NTaskCallback<ExampleModel>(new ExampleModel()) {
                    @Override
                    public void onStart(NRequestModel requestModel) {
                        edtToolbar.setText(requestModel.getUrl());
                        tvContent.setText("");
                        bar1.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onSuccess(ExampleModel model, NResponseModel responseModel) {
                        tvContent.setText(model.getStatus());
                        bar1.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(NResponseModel responseModel) {
                        tvContent.setText("Error: " + responseModel.getStatusCode());
                        bar1.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailed(NRequestModel nRequestModel, Errors error) {
                        tvContent.setText("Failed: " + error.name());
                        bar1.setVisibility(View.GONE);
                    }
                }).start();
    }


}
