package pro.oncreate.easynetwork;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pro.oncreate.easynet.NBuilder;
import pro.oncreate.easynet.NConst;
import pro.oncreate.easynet.models.NBaseModel;
import pro.oncreate.easynet.models.NKeyValueModel;
import pro.oncreate.easynet.models.NRequestModel;
import pro.oncreate.easynet.models.NResponseModel;
import pro.oncreate.easynet.tasks.NTaskCallback;
import pro.oncreate.easynetwork.adapters.ExpandableListAdapter;
import pro.oncreate.easynetwork.models.CountryModel;

public class DemoActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar bar1;
    private EditText edtToolbar;
    private ImageButton ibtnGo;
    private RecyclerView recyclerview;

    private ExpandableListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        edtToolbar = (EditText) findViewById(R.id.toolbar_edittext);
        ibtnGo = (ImageButton) findViewById(R.id.toolbar_go);
        bar1 = (ProgressBar) findViewById(R.id.progressBar);

        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ExpandableListAdapter(new ArrayList<ExpandableListAdapter.Item>());
        recyclerview.setAdapter(adapter);

        ibtnGo.setOnClickListener(this);
        ibtnGo.performClick();
    }

    @Override
    public void onClick(View view) {
        //urlencode();

        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    2);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, 1);
                } else {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String imgPath = getFilePathFromUri(this, data.getData());
            Log.d("asdfasdfasd", imgPath);
            if (imgPath != null) {
                multipart(new File(imgPath));
            } else {
                Snackbar.make(getCurrentFocus(), "Error gallery", Snackbar.LENGTH_LONG).show();
            }

        }
    }

    public void urlencode() {
        NBuilder.create()
                //.setPath("geocode/json")
                //.addParam("address", "USA")
                .setPath("countries")
                .setNeedParse(true) // default
                .setListener(new NTaskCallback<CountryModel>(CountryModel.class) {
                    @Override
                    public void onStart(NRequestModel requestModel) {
                        edtToolbar.setText(requestModel.getMethod() + " " + requestModel.getUrl());
                        bar1.setVisibility(View.VISIBLE);

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
                        bar1.setVisibility(View.GONE);
                        Snackbar.make(getCurrentFocus(), String.format(Locale.getDefault(), "%s [status code: %d] [response time: %d ms]", "Success", responseModel.getStatusCode(), responseModel.getResponseTime()), Snackbar.LENGTH_LONG).show();

                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, String.format(Locale.getDefault(), "Headers (%d)", responseModel.getHeaders().size())));
                        for (Map.Entry<String, List<String>> entry : responseModel.getHeaders().entrySet())
                            adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, entry.getKey() + ": " + entry.getValue().toString()));

                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Body"));
                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, responseModel.getBody()));
                    }

                    @Override
                    public void onSuccess(ArrayList<CountryModel> models, NResponseModel responseModel) {
                        bar1.setVisibility(View.GONE);
                        Snackbar.make(getCurrentFocus(), String.format(Locale.getDefault(), "%s [status code: %d] [response time: %d ms]", "Success", responseModel.getStatusCode(), responseModel.getResponseTime()), Snackbar.LENGTH_SHORT).show();

                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, String.format(Locale.getDefault(), "Headers (%d)", responseModel.getHeaders().size())));
                        for (Map.Entry<String, List<String>> entry : responseModel.getHeaders().entrySet())
                            adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, entry.getKey() + ": " + entry.getValue().toString()));

                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Body"));
                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, responseModel.getBody()));

                        Toast.makeText(DemoActivity.this, "" + models.size(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(NResponseModel responseModel) {
                        bar1.setVisibility(View.GONE);
                        Snackbar.make(getCurrentFocus(), String.format(Locale.US, "%s [status code: %d] [response time: %d ms]", "Error", responseModel.getStatusCode(), responseModel.getResponseTime()), Snackbar.LENGTH_LONG).show();

                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Headers"));
                        for (Map.Entry<String, List<String>> entry : responseModel.getHeaders().entrySet())
                            adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, entry.getKey() + ": " + entry.getValue().toString()));

                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Response"));
                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, responseModel.getBody()));
                    }

                    @Override
                    public void onFailed(NRequestModel nRequestModel, Errors error) {
                        bar1.setVisibility(View.GONE);
                        Snackbar.make(getCurrentFocus(), String.format(Locale.getDefault(), "Failed %s ", error.name()), Snackbar.LENGTH_LONG).show();
                    }
                }).start();
    }

    public void multipart(File file) {
        NBuilder.create()
                .setUrl("https://api.infitting.com/v1.1/users/4/update-avatar")
                .addHeader(NConst.ACCEPT_TYPE, NConst.MIME_TYPE_JSON)
                .addHeader("Authorization", "Bearer N_L0GHjMfDzfT4BNRgBeUiIyx3BnT3Qc")
                .addHeader(NConst.USER_AGENT, "InFitting/1 (iPhone; iOS 10.0.1; Scale/3.00)")
                .setContentType(NConst.MIME_TYPE_MULTIPART_FORM_DATA)
                .setNeedParse(false)
                .addFileParam("avatar_image", file)
                .setListener(new NTaskCallback<NBaseModel>(NBaseModel.class) {
                    @Override
                    public void onStart(NRequestModel requestModel) {
                        edtToolbar.setText(requestModel.getMethod() + " " + requestModel.getUrl());
                        bar1.setVisibility(View.VISIBLE);

                        adapter.clear();
                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, String.format(Locale.getDefault(), "Headers (%d)", requestModel.getHeaders().size())));
                        for (NKeyValueModel header : requestModel.getHeaders())
                            adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, header.getKey() + ": " + header.getValue()));

                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, String.format(Locale.getDefault(), "Params (%d)", requestModel.getParams().size())));
                        for (NKeyValueModel header : requestModel.getParams())
                            adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, header.getKey() + ": " + header.getValue()));

                    }

                    @Override
                    public void onSuccess(NResponseModel responseModel) {
                        bar1.setVisibility(View.GONE);
                        Snackbar.make(getCurrentFocus(), String.format(Locale.getDefault(), "%s [status code: %d] [response time: %d ms]", "Success", responseModel.getStatusCode(), responseModel.getResponseTime()), Snackbar.LENGTH_LONG).show();

                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, String.format(Locale.getDefault(), "Headers (%d)", responseModel.getHeaders().size())));
                        for (Map.Entry<String, List<String>> entry : responseModel.getHeaders().entrySet())
                            adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, entry.getKey() + ": " + entry.getValue().toString()));

                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Body"));
                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, responseModel.getBody()));

                    }

                    @Override
                    public void onFailed(NRequestModel nRequestModel, Errors error) {
                        bar1.setVisibility(View.GONE);
                        Snackbar.make(getCurrentFocus(), String.format(Locale.getDefault(), "Failed %s ", error.name()), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(NResponseModel responseModel) {
                        bar1.setVisibility(View.GONE);
                        Snackbar.make(getCurrentFocus(), String.format(Locale.US, "%s [status code: %d] [response time: %d ms]", "Error", responseModel.getStatusCode(), responseModel.getResponseTime()), Snackbar.LENGTH_LONG).show();

                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Headers"));
                        for (Map.Entry<String, List<String>> entry : responseModel.getHeaders().entrySet())
                            adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, entry.getKey() + ": " + entry.getValue().toString()));

                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Response"));
                        adapter.addItem(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, responseModel.getBody()));

                    }
                })
                .start();
    }

    public static String getFilePathFromUri(Context context, Uri uri) {
        try {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = context.getContentResolver().query(uri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgPath = cursor.getString(columnIndex);
            cursor.close();
            return imgPath;
        } catch (Exception e) {
            return null;
        }
    }

}
