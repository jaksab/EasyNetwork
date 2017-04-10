package pro.oncreate.easynetwork;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import pro.oncreate.easynet.EasyNet;
import pro.oncreate.easynet.tasks.NCallback;

public class DevActivity extends AppCompatActivity {

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
        EasyNet.get()
                .bind(progressBar, view, view2, view3)
                .start(new NCallback() {

                });
    }
}
