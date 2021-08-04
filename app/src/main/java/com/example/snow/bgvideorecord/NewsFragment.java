package com.example.snow.bgvideorecord;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.view.View;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by snow on 2016/10/27.
 */

public class NewsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    ListPreference listPreference=null;
    ListPreference VQlistPreference=null;
    SwitchPreference switchPreference=null;
    SwitchPreference VCswitchPreference=null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   //     getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
        getPreferenceManager().setSharedPreferencesMode(MODE_PRIVATE);
        addPreferencesFromResource(R.xml.pref_setting);
        getPreferenceManager().setSharedPreferencesName("mysetting"); //修改SharedPreferences文件名
    }
    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sp = getActivity().getSharedPreferences("mysetting", Context.MODE_PRIVATE);
        final boolean preview=sp.getBoolean("preview",false);
        final boolean volumecontrol=sp.getBoolean("volumecontrol",false);
        final String whichcamera=sp.getString("whichcamera","b");
        final String videoquality=sp.getString("videoquality","mid");

        Preference folder = findPreference("folder");
        folder.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
     //           Intent intent = new Intent();
     //           intent.setAction(android.content.Intent.ACTION_GET_CONTENT);
    //            File file = new File("/storage/emulated/0/video/camera/");
    //            Uri fileuri= FileProvider.getUriForFile(getActivity(),"com.example.snow.viewpagerfragment.fileprovider",file);
     //           Uri fileuri=Uri.parse("Content://com.example.snow.viewpagerfragment.fileprovider/my_video/video/camera");
          //      intent.setDataAndType(fileuri, "*/*");
    //            intent.addCategory(Intent.CATEGORY_OPENABLE);
      //          startActivity(intent);
                return true;
            }
        });

/*
        findPreference("two").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getActivity(), "two", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        findPreference("three").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getActivity(), "再点一下试试", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        findPreference("four").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getActivity(), "试试就试试", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

 */
        listPreference= (ListPreference) findPreference("whichcamera");
        VQlistPreference= (ListPreference) findPreference("videoquality");
        switchPreference= (SwitchPreference) findPreference("preview");
        VCswitchPreference= (SwitchPreference) findPreference("volumecontrol");
        if(whichcamera.equals("f")){
            listPreference.setValue("f");
            listPreference.setSummary(getResources().getStringArray(R.array.camera)[0]);
        }else{
            listPreference.setValue("b");
            listPreference.setSummary(getResources().getStringArray(R.array.camera)[1]);
        }
        switch (videoquality){
            case "high":
                VQlistPreference.setValue("high");
                VQlistPreference.setSummary(getResources().getStringArray(R.array.videoquality)[0]);
                break;
            case "mid":
                VQlistPreference.setValue("mid");
                VQlistPreference.setSummary(getResources().getStringArray(R.array.videoquality)[1]);
                break;
            case "720p":
                VQlistPreference.setValue("720p");
                VQlistPreference.setSummary(getResources().getStringArray(R.array.videoquality)[2]);
                break;
            case "low":
                VQlistPreference.setValue("low");
                VQlistPreference.setSummary(getResources().getStringArray(R.array.videoquality)[3]);
                break;
        }
        if(preview){
            switchPreference.setChecked(true);
        }else{
            switchPreference.setChecked(false);
        }
        if(volumecontrol){
            VCswitchPreference.setChecked(true);
        }else{
            VCswitchPreference.setChecked(false);
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals("whichcamera")){
            if(listPreference.getValue().equals("f")){
                listPreference.setSummary(getResources().getStringArray(R.array.camera)[0]);
            }else{
                listPreference.setSummary(getResources().getStringArray(R.array.camera)[1]);
            }

        }
        if(s.equals("videoquality")){
            switch (VQlistPreference.getValue()){
                case "high":
                    VQlistPreference.setSummary(getResources().getStringArray(R.array.videoquality)[0]);
                    break;
                case "mid":
                    VQlistPreference.setSummary(getResources().getStringArray(R.array.videoquality)[1]);
                    break;
                case "720p":
                    VQlistPreference.setSummary(getResources().getStringArray(R.array.videoquality)[2]);
                    break;
                case "low":
                    VQlistPreference.setSummary(getResources().getStringArray(R.array.videoquality)[3]);
                    break;
            }
        }

        if(s.equals("preview")){

        }

        if(s.equals("volumecontrol")){

                Intent intent=new Intent(getActivity(),MyBackService.class);
                intent.putExtra("volume",VCswitchPreference.isChecked());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startService(intent);

        }


    }
    public void onResume() {
        super.onResume();

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
}
