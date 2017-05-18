package com.xu.toggleview;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.xu.toggleview.ui.ToggleView;

/*
* 控件在界面打开的时候绘制，由生命周期onresume之后绘制的，之后就可以点击了
*
* */

public class MainActivity extends Activity {

    private ToggleView toggleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleView = (ToggleView) findViewById(R.id.toggleView);
        //设置开关的一些参数
//        toggleView.setSwitchBackgroundResource(R.drawable.switch_background);
//        toggleView.setSlideButtonResource(R.drawable.slide_button);
//        toggleView.setSwitchState(true);


        // 设置开关更新监听
        toggleView.setOnSwitchStateUpdateListener(new ToggleView.OnSwitchStateUpdateListener(){
            @Override
            public void onStateUpdate(boolean state) {
                Toast.makeText(getApplicationContext(), "state: " + state, Toast.LENGTH_SHORT).show();
            }

        });
    }

//	@Override
//	protected void onResume() {
//		super.onResume();
//	}
//
}
