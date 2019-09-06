package pro.oncreate.easynetwork;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pro.oncreate.easynet.EasyNet;
import pro.oncreate.easynet.data.NConst;
import pro.oncreate.easynet.data.NError;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.models.subsidiary.NKeyValueModel;
import pro.oncreate.easynet.processing.NBaseCallback;
import pro.oncreate.easynet.processing.NCallbackParse;
import pro.oncreate.easynetwork.adapters.ExpandableListAdapter;
import pro.oncreate.easynetwork.models.CountryModel;

public class DemoActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private EditText edtToolbar;
    private RecyclerView recyclerview;

    private ExpandableListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        edtToolbar = (EditText) findViewById(R.id.toolbar_edittext);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ImageButton ibtnGo = (ImageButton) findViewById(R.id.toolbar_go);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);

        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ExpandableListAdapter(new ArrayList<ExpandableListAdapter.Item>());
        recyclerview.setAdapter(adapter);

        ibtnGo.setOnClickListener(this);
        ibtnGo.performClick();
    }

    @Override
    public void onClick(View view) {
        EasyNet.get()
                .setPath("countries", 100)
                .addParam("expand", "name")
                .addHeader(NConst.ACCEPT_TYPE, NConst.MIME_TYPE_JSON)
                .bind(progressBar, recyclerview)
                .enableDefaultListeners(true) // default
                .setContentType(NConst.MIME_TYPE_X_WWW_FORM_URLENCODED) // default
                .waitHeader(new NBaseCallback.WaitHeaderCallback("Date") {
                    @Override
                    public void takeHeader(List<String> values) {
                        Log.d("EasyNetDemo", values.toString());
                    }
                })
                .waitHeader(new NBaseCallback.WaitHeaderCallback("Server") {
                    @Override
                    public void takeHeader(List<String> values) {
                        Log.d("EasyNetDemo", values.toString());
                    }
                })
                .start(new NCallbackParse<CountryModel>(CountryModel.class) {
                    @Override
                    public void onStart(NRequestModel requestModel) {
                        edtToolbar.setText(requestModel.getMethod() + " " + requestModel.getUrl());

                        adapter.clear();
                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, String.format(Locale.getDefault(), "Headers (%d)", requestModel.getHeaders().size())));
                        for (NKeyValueModel header : requestModel.getHeaders())
                            adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, header.getKey() + ": " + header.getValue()));

                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, String.format(Locale.getDefault(), "Params (%d)", requestModel.getParams().size())));
                        for (NKeyValueModel header : requestModel.getParams())
                            adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, header.getKey() + ": " + header.getValue()));
                    }

                    @Override
                    public void onSuccess(CountryModel model, NResponseModel responseModel) {
                        Snackbar.make(getCurrentFocus(), String.format(Locale.getDefault(), "%s [status code: %d] [response time: %d ms]", "Success", responseModel.getStatusCode(), responseModel.getResponseTime()), Snackbar.LENGTH_LONG).show();

                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, String.format(Locale.getDefault(), "Headers (%d)", responseModel.getHeaders().size())));
                        for (Map.Entry<String, List<String>> entry : responseModel.getHeaders().entrySet())
                            adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, entry.getKey() + ": " + entry.getValue().toString()));

                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Body"));
                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, responseModel.getBody()));
                    }

                    @Override
                    public void onError(NResponseModel responseModel) {
                        Snackbar.make(getCurrentFocus(), String.format(Locale.US, "%s [status code: %d] [response time: %d ms]", "Error", responseModel.getStatusCode(), responseModel.getResponseTime()), Snackbar.LENGTH_LONG).show();

                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Headers"));
                        for (Map.Entry<String, List<String>> entry : responseModel.getHeaders().entrySet())
                            adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, entry.getKey() + ": " + entry.getValue().toString()));

                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Response"));
                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, responseModel.getBody()));
                    }

                    @Override
                    public void onFailed(NRequestModel nRequestModel, NError error) {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(getCurrentFocus(), String.format(Locale.getDefault(), "Failed %s ", error.exception.toString()), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onTaskCancelled(NRequestModel requestModel, String tag) {
                        Toast.makeText(DemoActivity.this, "Cancelled " + tag, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (EasyNet.getInstance().isCurrentTasks())
            EasyNet.getInstance().cancelAllTasks();
        else super.onBackPressed();
    }
}
