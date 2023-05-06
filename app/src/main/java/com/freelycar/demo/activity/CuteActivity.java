package com.freelycar.demo.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.freelycar.demo.R;
import com.freelycar.demo.view.cutecandleview.CandlesAnimView;

public class CuteActivity extends AppCompatActivity {

    private CandlesAnimView mCandlesAnimView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cute);

        mCandlesAnimView = (CandlesAnimView) findViewById(R.id.candles_view);
        mCandlesAnimView.setStopAnimListener(new CandlesAnimView.StopAnimListener() {
            @Override
            public void OnAnimStop() {
                Toast.makeText(CuteActivity.this,"End Anim.",Toast.LENGTH_SHORT).show();
            }
        });
        mCandlesAnimView.postDelayed(new Runnable() {
            @Override
            public void run() {
//                mCandlesAnimView.stopAnim();
            }
        },5000);
    }
}
