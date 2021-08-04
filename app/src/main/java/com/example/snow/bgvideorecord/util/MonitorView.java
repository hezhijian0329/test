package com.example.snow.bgvideorecord.util;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.snow.bgvideorecord.MyBackService;

public class MonitorView extends View {
    WindowManager mWindowManager;
    Context mContext;
    MonitorView mMonitorView;
    MyBackService myBackService;

    public MonitorView(Context context) {
        super(context);
        mContext=context;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
   //     Log.d("suhuazhi", "keyCode " + event.getKeyCode());
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_MENU:
                // 处理自己的逻辑break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
       /*         Log.d("suhuazhi", "KEYCODE_VOLUME_DOWN");
                if (mAudioUtil != null && isMediaVolumePowerSaveSettings) {
                    int currAudioVolume = mAudioUtil.getMediaVolume();

        //            Log.d(TAG, "currMediaVolume = " + currAudioVolume);

                    if (AUDIO_ADJ == currAudioVolume) {
          //              Log.d(TAG, "Down volume key resume");
                        mAudioUtil.setMediaVolume(AUDIO_ADJ);
                        isMediaVolumePowerSaveSettings = false;
                    }
                }

        */
                myBackService.startRecorderService();
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                Toast.makeText(mContext, "音量变小", Toast.LENGTH_SHORT);
                myBackService.startRecorderService();
   //             Log.d("suhuazhi", "KEYCODE_VOLUME_UP");
                break;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }
    /*
    public void showWindow() {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
            mMonitorView = new MonitorView(mContext);
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                1, 1, //Must be at least 1x1
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                //Don't know if this is a safe default
                PixelFormat.TRANSLUCENT);

        //Don't set the preview visibility to GONE or INVISIBLE
        mWindowManager.addView(mMonitorView, params);
    }

    public void hideWindow() {
        if(null != mWindowManager) {
            mWindowManager.removeView(mMonitorView);
        }
    }

     */
}
