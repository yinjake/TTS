package com.freelycar.demo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.freelycar.demo.R;
import com.freelycar.demo.giftrain.BoxInfo;
import com.freelycar.demo.giftrain.BoxPrizeBean;
import com.freelycar.demo.giftrain.RedPacketViewHelper;
import com.freelycar.demo.service.FreeDemoService;
import com.freelycar.demo.util.MyLogUtils;

import java.util.ArrayList;
import java.util.List;

public class TrainActivity extends Activity {
    private static final String TAG = TrainActivity.class.getSimpleName();
    RedPacketViewHelper mRedPacketViewHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_train);
        mRedPacketViewHelper = new RedPacketViewHelper(this);
        rain();
        MyLogUtils.file(TAG,"onCreate");
    }

    public void rain() {
        //view.setEnabled(false);
        mRedPacketViewHelper.endGiftRain();
        getWindow().getDecorView().postDelayed(() -> {
            List<BoxInfo> boxInfos = new ArrayList<>();
            for (int i = 0; i < 64; i++) {
                BoxInfo boxInfo = new BoxInfo();
                boxInfo.setAwardId(i);
                boxInfo.setVoucher("ice " + i);
                boxInfos.add(boxInfo);
            }
            mRedPacketViewHelper.launchGiftRainRocket(0, boxInfos, new RedPacketViewHelper.GiftRainListener() {
                @Override
                public void startLaunch() {
                    MyLogUtils.file(TAG,"startLaunch");
                }

                @Override
                public void startRain() {
                    MyLogUtils.file(TAG,"startRain");
                }

                @Override
                public void openGift(BoxPrizeBean boxPrizeBean) {
                    MyLogUtils.file(TAG,"openGift");
                }

                @Override
                public void endRain() {
                    //view.setEnabled(true);
                    MyLogUtils.file(TAG,"endRain");
                    finish();
                }
            });
        }, 500);
    }

    @Override
    protected void onDestroy() {
        MyLogUtils.file(TAG,"onDestroy");
        super.onDestroy();
        mRedPacketViewHelper.endGiftRain();
    }
}
