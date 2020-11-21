package me.kareluo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import me.kareluo.imaging.core.util.TUIUtils;

public abstract class TFullScreenActivity extends AppCompatActivity {
    BroadcastReceiver mBatInfoReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final IntentFilter filter = new IntentFilter();
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT);

        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_USER_PRESENT.equals(action) || Intent.ACTION_SCREEN_ON.equals(action)) {
                    // 解锁并且当前页面为显示状态，则重新采集
                    TUIUtils.setFullScreen(TFullScreenActivity.this);
                }
            }
        };
        registerReceiver(mBatInfoReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TUIUtils.setFullScreen(TFullScreenActivity.this);
    }
}
