package com.example.snow.bgvideorecord;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * Created by snow on 2016/10/27.
 */

public class SettingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
   //     View view = inflater.inflate(R.layout.setting_fragment, container, false);

        View aboutPage = new AboutPage(getActivity())
                .isRTL(false)
                .setImage(R.drawable.ic_launcher)//图片
                .setDescription("主要功能：实现后台无预览录制视频，通过音量按钮开启录像占用内存资源，若系统资源不足可能会失效"+"\n"+
                        "Main Functions:This app can record video background,you can start it by press volume button,but it may not work when system  resources low"+"\n"+
                        "Email:68338080@qq.com  fly21cn@gmail.com\n")//介绍
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("与我联系 Contact me")
                .addEmail("fly21cn@gmail.com")//邮箱gvbg
         //       .addWebsite("http://zhaoweihao.me")//网站
         //       .addGitHub("zhaoweihaoChina")//github
         //       .setDescription("Email:68338080@qq.com fly21cn@gmail.com")
                .addPlayStore("com.example.snow.bgvideorecord")//应用商店
                .create();

 //       setContentView(aboutPage);

        return aboutPage;
    }
}
