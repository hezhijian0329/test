package com.example.snow.bgvideorecord.util;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.widget.Toast;

import com.example.snow.bgvideorecord.MyBackService;

/**
 * Created by Administrator on 2015/4/8.
 */
public class VolumeChangedObserver extends ContentObserver {

    private AudioManager _audioManager;
    private int _cachedVolume;
    private int _maxVolume;
    private static final int VOLUME_MIN_VALUE = 0;
    private MyBackService _iCaptureTakenEvent;
    public static final int AUDIO_TYPE = AudioManager.STREAM_MUSIC;
    private Context context;


    public VolumeChangedObserver(Handler handler, MyBackService captureTakenEvent) {
        super(handler);
        _iCaptureTakenEvent = captureTakenEvent;
        context=captureTakenEvent.getContext();
        _audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        _maxVolume = _audioManager.getStreamMaxVolume(AUDIO_TYPE);
        _cachedVolume = _audioManager.getStreamVolume(AUDIO_TYPE);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        int currentVolume = _audioManager.getStreamVolume(AUDIO_TYPE);

        if (_cachedVolume == currentVolume)
            return;
  //      if(currentVolume-_cachedVolume<0){
            _iCaptureTakenEvent.startRecorderService();
            Toast.makeText(context, "音量变小", Toast.LENGTH_SHORT);
 //       }


        if (currentVolume == VOLUME_MIN_VALUE || currentVolume == _maxVolume)
            currentVolume = _cachedVolume;

        _cachedVolume = currentVolume;
        _audioManager.setStreamVolume(AUDIO_TYPE, currentVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }


}
