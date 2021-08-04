package com.example.snow.bgvideorecord;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.snow.bgvideorecord.util.AppPermissionUtil;
import com.example.snow.bgvideorecord.util.MyLog;

/**
 * Created by snow on 2016/10/27.
 */

public class MessageFragment extends Fragment {
//    private String whichcamera;
 //   private boolean preview;
    private boolean mPermited=false;
    private View view;
    private boolean isPlay = true;
    private static ImageButton button1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.message_fragment, container, false);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
  //      SharedPreferences sp = getActivity().getSharedPreferences("mysetting", Context.MODE_PRIVATE);
  //      preview=sp.getBoolean("preview",false);
  //      whichcamera=sp.getString("whichcamera","b");
        if(view!=null){
            startser();
        }

    }
    private void startser(){

//        Button starts=view.findViewById(R.id.startservice);
 //       Button stops=view.findViewById(R.id.stopservice);
        button1 = view.findViewById(R.id.btn);
        if(MainActivity.servicerunning){
            button1.setImageResource(R.drawable.stop120);
        }else{
            button1.setImageResource(R.drawable.rec120);
        }
        final Intent intent = new Intent(getActivity(), RecorderService.class);

        button1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(isPlay){
                    ((ImageButton)v).setImageResource(R.drawable.stop120);
                    if (!mPermited){
                        checkOverlays();
                        //             getActivity().checkPermissiona();
                    }
                    //       checkPermission();
   //                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if(mPermited){
                        intent.putExtra(RecorderService.INTENT_VIDEO_PATH, "/video/camera/"); //eg: "/video/camera/"
                        getActivity().startService(intent);
            //            MyLog.d("button","start ser");
                    }

                    //           MyLog.d("activity button","finish"+"/n");
                }
                else{
                    ((ImageButton)v).setImageResource(R.drawable.rec120);
                    getActivity().stopService(intent);
                    ContactFragment.filecreated=true;
                }
                isPlay = !isPlay;


            }

        });
        /*
        stops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().stopService(intent);
            }

        });

         */
    }

    private void checkPermission(){
        AppPermissionUtil.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE}, new AppPermissionUtil.OnPermissionListener() {
            @Override
            public void onPermissionGranted() {
                //授权
            }

            @Override
            public void onPermissionDenied() {
                //没有授权，或者有一个权限没有授权
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkOverlays(){
        if (!Settings.canDrawOverlays(getActivity())) {
            //启动Activity让用户授权
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+ getActivity().getPackageName()));
            startActivityForResult(intent, 1010);
        }else{
            mPermited=true;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1010) {
            if (Build.VERSION.SDK_INT >= 23) { // Android6.0及以后需要动态申请权限
                if (Settings.canDrawOverlays(getActivity())) {
                    // 弹出悬浮窗
                    mPermited = true;
                } else {
                    Toast.makeText(getActivity(), "您需要开启弹出悬浮窗权限才能使用", Toast.LENGTH_SHORT);
                    mPermited = false;
                }
            }
        }
    }
    public static void setimagebutton(){
        button1.setImageResource(R.drawable.rec120);
    }
}
