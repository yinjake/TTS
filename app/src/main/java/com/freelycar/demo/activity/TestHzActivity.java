package com.freelycar.demo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import com.freelycar.demo.R;
import java.util.Locale;

public class TestHzActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = TestHzActivity.class.getSimpleName();


    private boolean isStart = false;
    private boolean isPause = false;
    private static final String[] STYLE_DATA = new String[]{"STYLE_ALL", "STYLE_NOTHING", "STYLE_WAVE", "STYLE_HOLLOW_LUMP"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_hz);
        //ButterKnife.bind(this);
        initPermission();
        initAudioView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initRecord();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initAudioView() {
       // audioView.setStyle(AudioView.ShowStyle.STYLE_ALL, AudioView.ShowStyle.STYLE_ALL);
       // tvState.setVisibility(View.GONE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, STYLE_DATA);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       // spUpStyle.setAdapter(adapter);
       // spDownStyle.setAdapter(adapter);
       // spUpStyle.setOnItemSelectedListener(this);
        //spDownStyle.setOnItemSelectedListener(this);
    }


    private void initPermission() {
//        AndPermission.with(this)
//                .runtime()
//                .permission(new String[]{Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE,
//                        Permission.RECORD_AUDIO})
//                .start();
    }

    private void initRecord() {
        String recordDir = String.format(Locale.getDefault(), "%s/Record/com.freelycar.demo/",
                Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spUpStyle:

                break;
            case R.id.spDownStyle:

                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
