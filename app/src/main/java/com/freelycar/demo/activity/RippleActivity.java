package com.freelycar.demo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.freelycar.demo.R;
import com.freelycar.demo.service.FreeDemoService;
import com.freelycar.demo.util.MyLogUtils;
import com.freelycar.demo.view.RippleAnimationView;

public class RippleActivity extends Activity {
    private static final String TAG = RippleActivity.class.getSimpleName();

    private ImageView imageView;
    private RippleAnimationView rippleAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ripple);

        //imageView = (ImageView) findViewById(R.id.ImageView);
        rippleAnimationView = (RippleAnimationView) findViewById(R.id.layout_RippleAnimation);

        if (rippleAnimationView.isRippleRunning()) {
            rippleAnimationView.stopRippleAnimation();
        } else {
            rippleAnimationView.startRippleAnimation();
        }
        MyLogUtils.file(TAG,"onCreate");

//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (rippleAnimationView.isRippleRunning()) {
//                    rippleAnimationView.stopRippleAnimation();
//                } else {
//                    rippleAnimationView.startRippleAnimation();
//                }
//            }
//        });
    }
}
