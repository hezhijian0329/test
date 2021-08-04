package com.example.snow.bgvideorecord;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.snow.bgvideorecord.util.MyLog;
import com.example.snow.bgvideorecord.util.TipHelper;

public class MyBackService extends Service {
    private Context context;
    AudioManager audioManager;
    private MediaPlayer _mediaPlayer;
    private boolean VolumeControl;
    private SharedPreferences sp;
    public MyBackService() {
    }

    public Context getContext(){
        return context;

    }
    public void startRecorderService(){
        Intent intent = new Intent(this, RecorderService.class);
        intent.putExtra(RecorderService.INTENT_VIDEO_PATH, "/video/camera/"); //eg: "/video/camera/"
        startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
        @Override
    public void onCreate(){
        context = this;
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationChannel channel = new NotificationChannel("ID", "NAME", NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
            }

            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            Notification notification = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notification = new Notification.Builder(this, "ID")
                        .setContentTitle("HEllo title")
                        .setContentText("Hello content text")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .build();
            } else {
                notification = new NotificationCompat.Builder(this)
                        .setContentTitle("HEllo title")
                        .setContentText("Hello content text")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .build();
            }


            startForeground(1, notification);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
  //      myRegisterReceiver();
        sp = getSharedPreferences("mysetting", Context.MODE_PRIVATE);
 /*       VolumeControl=sp.getBoolean("volumecontrol",false);
        if(VolumeControl){
            myRegisterReceiver();
            _mediaPlayer = MediaPlayer.create(this, R.raw.bg_sound);
            _mediaPlayer.setLooping(true);
        }else{
            if (_mediaPlayer != null) {
                _mediaPlayer.stop();
                _mediaPlayer = null;
                unregisterReceiver(mVolumeReceiver);
            }
        }

  */
    }
    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean newVolumControl=sp.getBoolean("volumecontrol", false);
        MyLog.d("newVolumControl1",newVolumControl);
        if (intent != null)
            newVolumControl = intent.getBooleanExtra("volume",newVolumControl);

        MyLog.d("newVolumControl2",newVolumControl);
   //     MyLog.d("myRegisterReceiver",mVolumeReceiver);

//        if (VolumeControl != newVolumControl) {
            if (newVolumControl) {
                if (_mediaPlayer == null) {
                    _mediaPlayer = MediaPlayer.create(this, R.raw.bg_sound);
                    _mediaPlayer.setLooping(true);
                    myRegisterReceiver();
                }
            }
            if (!newVolumControl) {
                if (_mediaPlayer != null) {
                    _mediaPlayer.stop();
                    _mediaPlayer = null;
                    unregisterReceiver(mVolumeReceiver);
                }
            }
     //       VolumeControl=newVolumControl;
   //     }

        if(_mediaPlayer!=null){
            _mediaPlayer.start();
        }
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy(){
 //       MyVolumeReceiver mVolumeReceiver = new MyVolumeReceiver();
        unregisterReceiver(mVolumeReceiver);
        if(_mediaPlayer!=null){
            _mediaPlayer.stop();
        }
    }
    MyVolumeReceiver mVolumeReceiver;
    private void myRegisterReceiver() {
        mVolumeReceiver = new MyVolumeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(mVolumeReceiver, filter);
    }

    private class MyVolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
     //           int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
                //int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    //            Toast.makeText(getApplicationContext(), currVolume + "", Toast.LENGTH_SHORT).show();
                TipHelper.Vibrate(context, 500);
                startRecorderService();
            }
        }
    }

}
