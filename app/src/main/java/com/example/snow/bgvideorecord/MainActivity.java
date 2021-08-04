package com.example.snow.bgvideorecord;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
//import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.snow.bgvideorecord.util.CrashHandler;
import com.example.snow.bgvideorecord.util.MyLog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener {


    //fragment 控件
    private Fragment messageFragment;
    private Fragment contactFragment;
    private Fragment newsFragment;
    private Fragment settingFragment;

    //layout控件
    private LinearLayout layout_message;
    private LinearLayout layout_contact;
    private LinearLayout layout_news;
    private LinearLayout layout_setting;
    //图片控件
    private ImageView messageImg;
    private ImageView contactImg;
    private ImageView newsImg;
    private ImageView settingImg;
    //文本控件
    private TextView messageTV;
    private TextView contactTV;
    private TextView newsTV;
    private TextView settingTV;
    private TextView texttop;

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> fragmentList;

    private FragmentManager fragmentManager;
    private static final int REQUEST_PERMISSION = 2;
    private boolean mPermited = false;
    public static boolean servicerunning=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //       MyLog.d("d","log.d");
 //       MyLog.e("e","log.e");
  //      MyLog.v("v","log.v");
  //      MyLog.i("i","log.i");
  //      MyLog.w("w","log.w");
        CrashHandler.getInstance().init(this);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        selected(0);

        initPermission();

 //       if (!mPermited){
     //       checkPermission();
//        }

 //       startbackser();
    }
    @Override
    protected void onResume(){
        super.onResume();
    }
    @Override
    protected void onDestroy() {
//        stopbackser();
        super.onDestroy();
    }

    private void initView() {

        //初始化控件
        messageImg = (ImageView) findViewById(R.id.iv_message);
        contactImg = (ImageView) findViewById(R.id.iv_contact);
        newsImg = (ImageView) findViewById(R.id.iv_news);
        settingImg = (ImageView) findViewById(R.id.iv_setting);

        messageTV = (TextView) findViewById(R.id.tv_message);
        contactTV = (TextView) findViewById(R.id.tv_contact);
        newsTV = (TextView) findViewById(R.id.tv_news);
        settingTV = (TextView) findViewById(R.id.tv_setting);
        texttop=findViewById(R.id.tvtop);

        layout_contact = (LinearLayout) findViewById(R.id.layout_contact);
        layout_message = (LinearLayout) findViewById(R.id.layout_message);
        layout_news = (LinearLayout) findViewById(R.id.layout_news);
        layout_setting = (LinearLayout) findViewById(R.id.layout_setting);

        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);


        messageFragment = new MessageFragment();
        contactFragment = new ContactFragment();
        newsFragment = new NewsFragment();
        settingFragment = new SettingFragment();

        fragmentList = new ArrayList<>();

        fragmentList.add(messageFragment);
        fragmentList.add(contactFragment);
        fragmentList.add(newsFragment);
        fragmentList.add(settingFragment);

        mAdapter = new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public android.app.Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                int currentItem = mViewPager.getCurrentItem();
                setTab(currentItem);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initEvent() {
        layout_message.setOnClickListener(this);
        layout_contact.setOnClickListener(this);
        layout_news.setOnClickListener(this);
        layout_setting.setOnClickListener(this);


    }

    //点击事件的监听
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.layout_message:
                selected(0);
     //           startser();
                break;
            case R.id.layout_contact:
                selected(1);
                break;
            case R.id.layout_news:
                selected(2);
                break;
            case R.id.layout_setting:
                selected(3);
                break;

        }

    }

    private void selected(int i) {
        //选中的时候讲图片设置为亮色
        //改变fragment 的内容
        setTab(i);
        //设置当前的viewpager 和选择的相同
        mViewPager.setCurrentItem(i);

    }

    private void setTab(int i) {
        restImg();
        switch (i) {
            case 0:
                messageImg.setImageResource(R.drawable.message_selected);
                messageTV.setTextColor(Color.WHITE);
                texttop.setText("录像 Record");
                break;
            case 1:
                contactImg.setImageResource(R.drawable.contacts_selected);
                contactTV.setTextColor(Color.WHITE);
                texttop.setText("文件 File");
                break;
            case 2:
                newsImg.setImageResource(R.drawable.news_selected);
                newsTV.setTextColor(Color.WHITE);
                texttop.setText("设置 Setting");
                break;
            case 3:
                settingImg.setImageResource(R.drawable.setting_selected);
                settingTV.setTextColor(Color.WHITE);
                texttop.setText("关于 About");
                break;

        }
    }

    //重置图片颜色 暗色
    private void restImg() {
        messageTV.setTextColor(Color.BLACK);
        messageImg.setImageResource(R.drawable.message_unselected);

        contactTV.setTextColor(Color.BLACK);
        contactImg.setImageResource(R.drawable.contacts_unselected);

        newsTV.setTextColor(Color.BLACK);
        newsImg.setImageResource(R.drawable.news_unselected);

        settingTV.setTextColor(Color.BLACK);
        settingImg.setImageResource(R.drawable.setting_unselected);

    }
/*
    public void checkPermission() {
        boolean permitted = true;
        if(Build.VERSION.SDK_INT >= 23) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permitted = false;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            }
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permitted = false;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
            }
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permitted = false;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION);
            }
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)) {
                permitted = false;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, REQUEST_PERMISSION);
            }
            if (!Settings.canDrawOverlays(this)) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1010);
            }


        }
        mPermited = permitted;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION){
            boolean granted = true;
            for(int i : grantResults) {
                if(i != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                }
            }
            if(!granted) {
                mPermited = false;
                Toast.makeText(this, "您需要开启权限才能使用", Toast.LENGTH_SHORT).show();
            } else {
                mPermited = true;
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1010) {
            if (Build.VERSION.SDK_INT >= 23) { // Android6.0及以后需要动态申请权限
                if (Settings.canDrawOverlays(this)) {
                    // 弹出悬浮窗
                    mPermited = true;
                } else {
                    Toast.makeText(this, "您需要开启弹出悬浮窗权限才能使用", Toast.LENGTH_SHORT);
                    mPermited = false;
                }
            }
        }
    }

 */


    private void startbackser(){
        Intent intent=new Intent(this,MyBackService.class);
  //      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(intent);
    }
    /*
    private void stopbackser(){
        Intent intent=new Intent(this,MyBackService.class);
        stopService(intent);
    }
    private void startser(){
        Button starts=messageFragment.getView().findViewById(R.id.startservice);
        Button stops=messageFragment.getView().findViewById(R.id.stopservice);

        final Intent intent = new Intent(this, RecorderService.class);

        starts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPermited){
                    checkPermission();
                }
                intent.putExtra(RecorderService.INTENT_VIDEO_PATH, "/video/camera/"); //eg: "/video/camera/"
                startService(intent);
                //           MyLog.d("activity button","finish"+"/n");
            }

        });
        stops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(intent);
            }

        });
    }

     */
    //申请多个权限，录音和文件读写
    //1、首先声明一个数组permissions，将需要的权限都放在里面
    String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,Manifest.permission.INTERNET};
    //2、创建一个mPermissionList，逐个判断哪些权限未授予，未授予的权限存储到mPerrrmissionList中
    List<String> mPermissionList = new ArrayList<>();

    private final int mRequestCode = 100;//权限请求码


    //权限判断和申请
    private void initPermission() {

        mPermissionList.clear();//清空没有通过的权限

        //逐个判断你要的权限是否已经通过
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
            }
        }

        //申请权限
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, mRequestCode);
        }else{
            //说明权限都已经通过，可以做你想做的事情去
            startbackser();
        }
    }


    //请求权限后回调的方法
    //参数： requestCode  是我们自己定义的权限请求码
    //参数： permissions  是我们请求的权限名称数组
    //参数： grantResults 是我们在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限名称数组的长度，数组的数据0表示允许权限，-1表示我们点击了禁止权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;//有权限没有通过
        if (mRequestCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                }
            }
            //如果有权限没有被允许
            if (hasPermissionDismiss) {
     //           showPermissionDialog();//跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
            }else{
                //全部权限通过，可以进行下一步操作。。。
                startbackser();
            }
        }

    }


    /**
     * 不再提示权限时的展示对话框
     */
    /*
    AlertDialog mPermissionDialog;
    String mPackName = "com.example.snow.viewpagerfragment";

    private void showPermissionDialog() {
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(this)
                    .setMessage("已禁用权限，请手动授予")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();

                            Uri packageURI = Uri.parse("package:" + mPackName);
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭页面或者做其他操作
                            cancelPermissionDialog();

                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    //关闭对话框
    private void cancelPermissionDialog() {
        mPermissionDialog.cancel();
    }
*/
}
