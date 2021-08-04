package com.example.snow.bgvideorecord;

import android.app.ListFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.AlertDialog;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snow.bgvideorecord.util.MyLog;
import com.example.snow.bgvideorecord.util.MyVideoThumbLoader;
import com.example.snow.bgvideorecord.util.VideoInfo;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by snow on 2016/10/27.
 */

public class ContactFragment extends ListFragment {
    private static final String ROOT_PATH = "/";
    private String vpath= Environment.getExternalStorageDirectory().getAbsolutePath() + "/video/camera/";
    //存储文件名称
    private ArrayList<String> names = new ArrayList<String>();
    //存储文件路径
    private ArrayList<String> paths = new ArrayList<String>();

    private ArrayList<VideoInfo> videoInfos;
    private View view;
    private EditText editText;
    private MyAdapter myAdapter;

    public static volatile boolean filecreated=false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment, container, false);
        return view;
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showFileDir(vpath);
        myAdapter=new MyAdapter(getActivity(),videoInfos,names, paths);
        this.setListAdapter(myAdapter);
    }
    public void onResume() {
  //      MyLog.d("filecreated onresume",filecreated);
        if(filecreated){
            showFileDir(vpath);
            myAdapter.notifyDataSetChanged();
            filecreated=false;
        }
 //       MyLog.d("filecreated onresume after",filecreated);
 //       MyLog.d("d","contactfragment.java onresume");

        super.onResume();
    }
    private void showFileDir(String path){
        names.clear();
        paths.clear();
        File file = new File(path);
        File[] files = file.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return -1;
                else if (diff == 0)
                    return 0;
                else
                    return 1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
            }

            public boolean equals(Object obj) {
                return true;
            }
        });
        //如果当前目录不是根目录
 /*       if (!ROOT_PATH.equals(path)){
            names.add("@1");
            paths.add(ROOT_PATH);
            names.add("@2");
            paths.add(file.getParent());
        }

  */
        //添加所有文件
        for (File f : files){
            names.add(f.getName()+"\n"+getFileLength(f));
            paths.add(f.getPath());
        }
    //    videoInfos=VideoInfoUtil.getVideoinfos(getActivity(),path);
  //      this.setListAdapter(new MyAdapter(getActivity(),videoInfos,names, paths));
    }
    private String getFileLength(File file){
        long size=file.length()/1000;
        String hrSize = "";
        double m = size/1000.0;
        DecimalFormat dec = new DecimalFormat("0.00");

        if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else {
            hrSize = dec.format(size).concat(" KB");
        }
        return hrSize;
    }

    private void updateList(File file){

    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String path = paths.get(position);
   //     String path=videoInfos.get(position).getPath();
        File file = new File(path);
        // 文件存在并可读
        if (file.exists() && file.canRead()){
            if (file.isDirectory()){
                //显示子目录及文件
                showFileDir(path);
            }
            else{
                //处理文件
                fileHandle(file);
            }
        }
        //没有权限
        else{
            Resources res = getResources();
            new AlertDialog.Builder(getActivity()).setTitle("Message")
                    .setMessage(res.getString(R.string.no_permission))
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        }
        super.onListItemClick(l, v, position, id);
    }
    //对文件进行增删改
    private void fileHandle(final File file){
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 打开文件
                if (which == 0){
                    openFile(file);
                }
                //修改文件名
                else if(which == 2){
                    LayoutInflater factory = LayoutInflater.from(getActivity());
                    view = factory.inflate(R.layout.rename_dialog, null);
                    editText = (EditText)view.findViewById(R.id.editText);
                    editText.setText(file.getName());
                    DialogInterface.OnClickListener listener2 = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            String modifyName = editText.getText().toString();
                            final String fpath = file.getParentFile().getPath();
                            final File newFile = new File(fpath + "/" + modifyName);
                            if (newFile.exists()){
                                //排除没有修改情况
                                if (!modifyName.equals(file.getName())){
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle("注意!")
                                            .setMessage("文件名已存在，是否覆盖？")
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (file.renameTo(newFile)){
                                                        showFileDir(fpath);
                                                        displayToast("重命名成功！");
                                                    }
                                                    else{
                                                        displayToast("重命名失败！");
                                                    }
                                                }
                                            })
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            })
                                            .show();
                                }
                            }
                            else{
                                if (file.renameTo(newFile)){
                                    showFileDir(fpath);
                                    displayToast("重命名成功！");
                                }
                                else{
                                    displayToast("重命名失败！");
                                }
                            }
                        }
                    };
       /*             AlertDialog renameDialog = new AlertDialog.Builder(getActivity()).create();
                    renameDialog.setView(view);
                    renameDialog.setButton("确定", listener2);
                    renameDialog.setButton2("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                        }
                    });
                    renameDialog.show();

       */
                }
                //删除文件
                else{
                    new AlertDialog.Builder(getActivity())
                            .setTitle("注意!")
                            .setMessage("确定要删除此文件吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(file.delete()){
                                        //更新文件列表
                           //             showFileDir(file.getParent());
                                        showFileDir(vpath);
                                        myAdapter.notifyDataSetChanged();
                                        displayToast("删除成功！");
                                    }
                                    else{
                                        displayToast("删除失败！");
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
            }
        };
        //选择文件时，弹出增删该操作选项对话框
 //       String[] menu = {"打开文件","重命名","删除文件"};
        String[] menu = {"打开文件","删除文件"};
        new AlertDialog.Builder(getActivity())
                .setTitle("请选择要进行的操作!")
                .setItems(menu, listener)
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }
    //打开文件
    private void openFile(File file){
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String type = getMIMEType(file);
        if(Build.VERSION.SDK_INT>=24){
            Uri fileuri= FileProvider.getUriForFile(getActivity(),"com.example.snow.bgvideorecord.fileprovider",file);
            intent.setDataAndType(fileuri, type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Toast.makeText(getActivity(),fileuri.toString(),Toast.LENGTH_LONG).show();
        }else{
            intent.setDataAndType(Uri.fromFile(file), type);
        }
        startActivity(intent);
    }
    //获取文件mimetype
    private String getMIMEType(File file){
        String type = "";
        String name = file.getName();
        //文件扩展名
        String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
        if (end.equals("m4a") || end.equals("mp3") || end.equals("wav")){
            type = "audio";
        }
        else if(end.equals("mp4") || end.equals("3gp")) {
            type = "video";
        }
        else if (end.equals("jpg") || end.equals("png") || end.equals("jpeg") || end.equals("bmp") || end.equals("gif")){
            type = "image";
        }
        else {
            //如果无法直接打开，跳出列表由用户选择
            type = "*";
        }
        type += "/*";
        return type;
    }
    private void displayToast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
    public class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private Bitmap directory,file;
        //存储文件名称
        private ArrayList<String> names = null;
        //存储文件路径
        private ArrayList<String> paths = null;

        private ArrayList<VideoInfo> mVideoInfos=null;
        private MyVideoThumbLoader mVideoThumbLoader;
        //参数初始化
        public MyAdapter(Context context, ArrayList<VideoInfo> vf,ArrayList<String> na, ArrayList<String> pa){
            names = na;
            paths = pa;
            this.mVideoInfos=vf;
            directory = BitmapFactory.decodeResource(context.getResources(),R.drawable.d);
            file = BitmapFactory.decodeResource(context.getResources(),R.drawable.f);
            //缩小图片
            directory = small(directory,0.16f);
            file = small(file,0.1f);
            inflater = LayoutInflater.from(context);
            mVideoThumbLoader = new MyVideoThumbLoader();// 初始化缩略图载入方法
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return paths.size();
        }
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return paths.get(position);
        }
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            String path=paths.get(position);
   //         if (null == convertView){
                convertView = inflater.inflate(R.layout.file, null);
                holder = new ViewHolder();
                holder.text = (TextView)convertView.findViewById(R.id.textView);
                holder.image = (ImageView)convertView.findViewById(R.id.imageView);
                holder.image.setTag(path);
                convertView.setTag(holder);
   //         }
   //         else {
   //             holder = (ViewHolder)convertView.getTag();
  //          }
    /*        File f = new File(paths.get(position).toString());
            if (names.get(position).equals("@1")){
                holder.text.setText("/");
                holder.image.setImageBitmap(directory);
            }
            else if (names.get(position).equals("@2")){
                holder.text.setText("..");
                holder.image.setImageBitmap(directory);
            }
            else{
                holder.text.setText(f.getName());
                if (f.isDirectory()){
                    holder.image.setImageBitmap(directory);
                }
                else if (f.isFile()){
          //          String path=paths.get(position);
         //           Bitmap bitmap=getVideoThumbnail(paths.get(position));
         //           bitmap=small(bitmap,0.9f);
         //           holder.image.setImageBitmap(bitmap);
         //           holder.image.setTag(path);
         //           mVideoThumbLoader.showThumbByAsynctack(path, holder.image);

                }
                else{
                    System.out.println(f.getName());
                }


            }


     */
      //      videoInfos=VideoInfoUtil.getVideoinfos(getActivity(),path);
     //       VideoInfo videoInfo=new VideoInfo();
     //       if(videoInfos!=null&&videoInfos.size()>0){
    //            videoInfo=mVideoInfos.get(0);
    //        }

        //    long ls=videoInfo.getDuration();
    //        holder.text.setText(names.get(position)+" "+ls/60000+"m"+ls/1000+"s"+" "+videoInfo.getDateTaken()+" "+videoInfo.getPath()+" "+videoInfo.getDateModified());
     //       holder.text.setText(names.get(position)+" "+videoInfo.getDateTaken()+" "+videoInfo.getPath()+" "+videoInfo.getDateModified());
            //      holder.image.setImageBitmap(directory);
      //      holder.image.setImageBitmap(videoInfo.getImage());
            holder.text.setText(names.get(position));
            mVideoThumbLoader.showThumbByAsynctack(path,holder.image);
            return convertView;
        }
        private class ViewHolder{
            private TextView text;
            private ImageView image;
        }
        private Bitmap small(Bitmap map,float num){
            Matrix matrix = new Matrix();
            matrix.postScale(num, num);
            return Bitmap.createBitmap(map,0,0,map.getWidth(),map.getHeight(),matrix,true);
        }
    }
    public Bitmap getVideoThumbnail(String videoPath) {
        MediaMetadataRetriever media =new MediaMetadataRetriever();
        media.setDataSource(videoPath);
        Bitmap bitmap = media.getFrameAtTime();
        return bitmap;
    }
}
