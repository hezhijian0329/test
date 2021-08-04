package com.example.snow.bgvideorecord;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

//import androidx.core.app.NotificationCompat;

import com.example.snow.bgvideorecord.util.MyLog;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecorderService extends Service implements SurfaceHolder.Callback {

    private static final String tag_ = RecorderService.class.getSimpleName();

    public static final String INTENT_VIDEO_PATH = "video_path";
    public static String fcam="f1";   //用前置还是后置摄像头,f为前置,其它为后置
    /**
     * It is used to check recording status
     */
    public static boolean mRecordingStatus;

    private WindowManager windowManager;
    private SurfaceView surfaceView;
    private Camera mServiceCamera = null;
    private MediaRecorder mMediaRecorder = null;
    private Context context;
    private File outFile;
    private String videpPath;
    private PowerManager.WakeLock wakeLock;
    private String videoquality;

    private ImageButton button;

//    public static boolean bothCamera=false;

    /**
     * This override method is called when first instance of RecordServices is made and use to create layout for camera video recording.
     */
    @Override
    public void onCreate() {
    //    MyLog.d("service on create","start");

        context = this;
        SharedPreferences sp = getSharedPreferences("mysetting", Context.MODE_PRIVATE);
        final boolean preview=sp.getBoolean("preview",false);
        String whichcamera=sp.getString("whichcamera","b");
        videoquality=sp.getString("videoquality","720p");
        fcam=whichcamera;
  //      MyLog.d("service","oncreate");
            try {
                /** Create new SurfaceView, set its size to 1x1, move it to the top
                 * left corner and set this service as a callback  */
                windowManager = (WindowManager) this
                        .getSystemService(Context.WINDOW_SERVICE);
                surfaceView = new SurfaceView(this);
                button = new ImageButton(this);
                button.setImageResource(R.drawable.stop50b);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stopSelf();
                        MessageFragment.setimagebutton();
                        ContactFragment.filecreated=true;
                    }
                });
     /*       WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(360, 640,
                    WindowManager.LayoutParams.TYPE_TOAST,      //TYPE_SYSTEM_OVERLAY
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT);

      */
                //         layoutParams.type=WindowManager.LayoutParams.TYPE_TOAST;
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                WindowManager.LayoutParams layoutParamsbutton = new WindowManager.LayoutParams();
                layoutParamsbutton.width = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParamsbutton.height = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParamsbutton.gravity = Gravity.LEFT | Gravity.TOP;
                //       layoutParamsbutton.x =10;
                //      layoutParamsbutton.y=5;

                // 设置window type
        /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }

         */

                //8.0系统加强后台管理，禁止在其他应用和窗口弹提醒弹窗，如果要弹，必须使用TYPE_APPLICATION_OVERLAY
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                    layoutParamsbutton.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                    layoutParamsbutton.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                }


                /*
                 * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE; 那么优先级会降低一些,
                 * 即拉下通知栏不可见
                 */
                //          layoutParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

                // 设置Window flag
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                layoutParamsbutton.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

                // 设置悬浮窗的长宽
                if (preview) {
                    layoutParams.width = 360;
                    layoutParams.height = 640;
                    //         windowManager.addView(button, layoutParamsbutton);
                } else {
                    layoutParams.width = 1;
                    layoutParams.height = 1;
                }

                layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
                windowManager.addView(surfaceView, layoutParams);
                surfaceView.getHolder().addCallback(this);
        //        MyLog.d("surfaceView",surfaceView);
         //       MyLog.d("    surfaceView.getHolder()",surfaceView.getHolder());
                if (preview) {
                    windowManager.addView(button, layoutParamsbutton);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }



 //       MyLog.d("service on create","finish"+"/n");

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("ID", "NAME", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);

                // 设置通知出现时的震动（如果 android 设备支持的话）
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{0,500});
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = null;
   //     long[] pattern = {0, 100};
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, "ID")
                    .setContentTitle("HEllo title")
                    .setContentText("Hello content text")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
            //        .setVibrate(pattern)
                    .setContentIntent(pendingIntent)
                    .build();
        } else {

            notification = new NotificationCompat.Builder(this)
                    .setContentTitle("HEllo title")
                    .setContentText("Hello content text")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setVibrate(new long[]{0,500})
                    .setContentIntent(pendingIntent)
                    .build();
        }


        startForeground(1, notification);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, RecorderService.class.getName());
        wakeLock.acquire();

        MainActivity.servicerunning=true;

    }

    public Context getContext(){
        return context;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null)
            videpPath = intent.getStringExtra(INTENT_VIDEO_PATH);
        if (videpPath == null)
            videpPath = "/Video/camera/";
  //      MyLog.d("service on start","finish"+"/n");
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * Callback method which gets called when sufaceholder is create.
     * As surfaceHolder is created it initializes MediaRecorder and starts recording front camera video.
     *
     * @param surfaceHolder
     */
    @Override
    public void surfaceCreated(final SurfaceHolder surfaceHolder) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                setupSurface(surfaceHolder);
            }
        }).start();
 //       MyLog.d("surfaceCreated","finish"+"/n");

    }

    /**
     * Initialize MediaRecorder to open and start recording front camera video
     *
     * @param surfaceHolder
     */
    private void setupSurface(SurfaceHolder surfaceHolder) {
        try {
     /*       if (Build.VERSION.SDK_INT >= 18) {
                mServiceCamera = openFrontCameraNew();
            } else {
                mServiceCamera = openFrontFacingCamera();
            }

      */
            if("f".equals(fcam)){
                mServiceCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }else{
                mServiceCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            }

            if (mServiceCamera != null) {


                Camera.Parameters params = mServiceCamera.getParameters();

                if (Integer.parseInt(Build.VERSION.SDK) >= 8) {

                    Display display = ((WindowManager) context
                            .getSystemService(Context.WINDOW_SERVICE))
                            .getDefaultDisplay();

                    if (display.getRotation() == Surface.ROTATION_0) {
                        mServiceCamera.setDisplayOrientation(90);
                    } else if (display.getRotation() == Surface.ROTATION_270) {
                        mServiceCamera.setDisplayOrientation(0);
                    }
                } else {

                    if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        params.set("orientation", "portrait");
                        params.set("rotation", 90);
                    }
                    if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        params.set("orientation", "landscape");
                        params.set("rotation", 0);
                    }
                }

                mServiceCamera.setParameters(params);

                mServiceCamera.unlock();

                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {

                    @Override
                    public void onInfo(MediaRecorder mr, int what, int extra) {
                        // TODO Auto-generated method stub

                    }
                });

                mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {

                    @Override
                    public void onError(MediaRecorder mr, int what, int extra) {
                        // TODO Auto-generated method stub

                        mMediaRecorder.reset();
                        mMediaRecorder.release();
                        mMediaRecorder = null;

                        mServiceCamera.lock();
                        mServiceCamera.release();
                        mServiceCamera = null;

                        stopSelf();
                        return;

                    }
                });
                File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + videpPath);
                if (!directory.exists())
                    directory.mkdirs();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                String fileNameString = sdf.format(new Date());
                fileNameString ="video"+ fileNameString+".mp4";
                //        long currentTime = System.currentTimeMillis();
                //        String fileNameString = "videooutput" + currentTime + ".mp4";
                String uniqueOutFile = Environment.getExternalStorageDirectory().getAbsolutePath() + videpPath
                        + fileNameString;
                outFile = new File(directory, uniqueOutFile);
                if (outFile.exists()) {
                    outFile.delete();
                }

                mMediaRecorder.setCamera(mServiceCamera);
                mMediaRecorder
                        .setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder
                        .setVideoSource(MediaRecorder.VideoSource.DEFAULT);

                CamcorderProfile cProfile=CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
                if(videoquality.equals("720p")) {

                    mMediaRecorder
                            .setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    mMediaRecorder.setAudioSamplingRate(44100);
                    mMediaRecorder.setAudioChannels(2);
                    mMediaRecorder.setAudioEncodingBitRate(128000);
                    mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                    mMediaRecorder.setVideoEncodingBitRate(3000000);
                    mMediaRecorder.setVideoFrameRate(25);
                    mMediaRecorder.setOutputFile(uniqueOutFile);
                    mMediaRecorder.setVideoSize(1280, 720);
                }else {

                    switch (videoquality){
                        //直接采用QUALITY_HIGH,这样可以提高视频的录制质量，但是不能设置编码格式和帧率等参数。
                        case "high":
                            cProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                            break;
                        case "mid":
                            cProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
                            break;
                        case "low":
                            cProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
                            break;
                    }
                    mMediaRecorder.setProfile(cProfile);
                    //设置录制的视频帧率,注意文档的说明:
                    mMediaRecorder.setVideoFrameRate(25);
                    //设置输出路径
                    mMediaRecorder.setOutputFile(uniqueOutFile);
                }


                if("f".equals(fcam)){
                    mMediaRecorder.setOrientationHint(270);
                }
                mMediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

                mMediaRecorder.prepare();
                mRecordingStatus = true;
                try {
                    mMediaRecorder.start();
                } catch (Exception e) {
                    // TODO: handle exception
                    if (outFile.exists())
                        outFile.delete();

                }
            } else {
                Log.v(tag_,
                        "Camera is not available (in use or does not exist)");
                try {
                    if (outFile.exists())
                        outFile.delete();
                } catch (Exception e) {
                    return;
                }

                return;
            }

        } catch (IllegalStateException e) {

            Log.d(tag_, e.getMessage());
            e.printStackTrace();

        } catch (IOException e) {

            Log.d(tag_, e.getMessage());
            e.printStackTrace();
            if (outFile.exists())
                outFile.delete();

        }
        /*
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        if(Build.VERSION.SDK_INT>=24){
            Uri fileuri= FileProvider.getUriForFile(this,"com.example.snow.viewpagerfragment.fileprovider",outFile);
            scanIntent.setData(fileuri);
            scanIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        }else{
            scanIntent.setData(Uri.fromFile(outFile));
        }
        sendBroadcast(scanIntent);
        */
        // Tell the media scanner about the new file so that it is
        // immediately available to the user.


    }


    // Stop recording and remove SurfaceView
    @Override
    public void onDestroy() {
   /*     MediaScannerConnection.scanFile(this,
                new String[] { outFile.getAbsolutePath() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                        Toast.makeText(RecorderService.this, "MediaScannerConnection.scanFile", Toast.LENGTH_SHORT);
                    }
                });

    */
  //      ContactFragment.filecreated=true;

        try {

            mRecordingStatus = false;
            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
            }

            if (mServiceCamera != null) {
                mServiceCamera.lock();
                mServiceCamera.release();
                windowManager.removeView(surfaceView);
                windowManager.removeView(button);
            }

        } catch (Exception e) {
        }

        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        MainActivity.servicerunning=false;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        stopSelf();

    }

    @TargetApi(14)
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        stopSelf();

    }


    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
                               int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Open the camera.  First attempt to find and open the front-facing camera.
     *
     * @return a Camera object
     */
    private Camera openFrontFacingCamera() {


        // Look for front-facing mServiceCamera, using the Gingerbread API.
        // Java reflection is used for backwards compatibility with pre-Gingerbread APIs.
        try {
            Class<?> cameraClass = Class.forName("android.hardware.Camera");
            Object cameraInfo = null;
            Field field = null;
            int cameraCount = 0;
            Method getNumberOfCamerasMethod = cameraClass.getMethod("getNumberOfCameras");
            if (getNumberOfCamerasMethod != null) {
                cameraCount = (Integer) getNumberOfCamerasMethod.invoke(null, (Object[]) null);
            }
            Class<?> cameraInfoClass = Class.forName("android.hardware.Camera$CameraInfo");
            if (cameraInfoClass != null) {
                cameraInfo = cameraInfoClass.newInstance();
            }
            if (cameraInfo != null) {
                field = cameraInfo.getClass().getField("facing");
            }
            Method getCameraInfoMethod = cameraClass.getMethod("getCameraInfo", Integer.TYPE, cameraInfoClass);
            if (getCameraInfoMethod != null && cameraInfoClass != null && field != null) {
                for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                    getCameraInfoMethod.invoke(null, camIdx, cameraInfo);
                    int facing = field.getInt(cameraInfo);
                    if (facing == 1) { // Camera.CameraInfo.CAMERA_FACING_FRONT
                        try {
                            Method cameraOpenMethod = cameraClass.getMethod("open", Integer.TYPE);
                            if (cameraOpenMethod != null) {
                                mServiceCamera = (Camera) cameraOpenMethod.invoke(null, camIdx);
                            }
                        } catch (RuntimeException e) {
                            Log.e(tag_, "Camera failed to open: " + e.getLocalizedMessage());
                        }
                    }
                }
            }
        }
        // Ignore the bevy of checked exceptions the Java Reflection API throws - if it fails, who cares.
        catch (ClassNotFoundException e) {
            Log.e(tag_, "ClassNotFoundException" + e.getLocalizedMessage());
        } catch (NoSuchMethodException e) {
            Log.e(tag_, "NoSuchMethodException" + e.getLocalizedMessage());
        } catch (NoSuchFieldException e) {
            Log.e(tag_, "NoSuchFieldException" + e.getLocalizedMessage());
        } catch (IllegalAccessException e) {
            Log.e(tag_, "IllegalAccessException" + e.getLocalizedMessage());
        } catch (InvocationTargetException e) {
            Log.e(tag_, "InvocationTargetException" + e.getLocalizedMessage());
        } catch (InstantiationException e) {
            Log.e(tag_, "InstantiationException" + e.getLocalizedMessage());
        } catch (SecurityException e) {
            Log.e(tag_, "SecurityException" + e.getLocalizedMessage());
        }


        return mServiceCamera;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private Camera openFrontCameraNew() {
        Camera camera = null;
        boolean found = false;
        int i;
        for (i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo newInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, newInfo);
            if (newInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                found = true;
                break;
            }
        }
        if (found) {
            camera = Camera.open(i);
        }
        return camera;
    }
    public static final String ACTION_MEDIA_SCANNER_SCAN_DIR = "android.intent.action.MEDIA_SCANNER_SCAN_DIR";
    public void scanDirAsync(Context ctx, String dir) {
        Intent scanIntent = new Intent(ACTION_MEDIA_SCANNER_SCAN_DIR);
        scanIntent.setData(Uri.fromFile(new File(dir)));
        ctx.sendBroadcast(scanIntent);
    }
    public void scanFileAsync(Context ctx, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        ctx.sendBroadcast(scanIntent);
    }
}