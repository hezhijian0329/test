package com.example.snow.bgvideorecord.util;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyVideoThumbLoader {
     private ImageView imgView;
     private String path;
     //创建cache
     private LruCache<String, Bitmap> lruCache;

     @SuppressLint("NewApi")
    public MyVideoThumbLoader(){
         int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取最大的运行内存
         int maxSize = maxMemory /4;//拿到缓存的内存大小
                  lruCache = new LruCache<String, Bitmap>(maxSize){
                     @Override
                     protected int sizeOf(String key, Bitmap value) {
                             //这个方法会在每次存入缓存的时候调用
                             return value.getByteCount();
                         }
                 };
      }

              public void addVideoThumbToCache(String path,Bitmap bitmap){
        //         if(getVideoThumbToCache(path) == null){
                     //当前地址没有缓存时，就添加
                  if(path!=null&bitmap!=null) {
                      lruCache.put(path, bitmap);
                  }
         //        }
             }
             public Bitmap getVideoThumbToCache(String path){

                 return lruCache.get(path);

             }
             public void showThumbByAsynctack(String path,ImageView imgview){

                 if(getVideoThumbToCache(path) == null){
                     //异步加载
         //            new MyBobAsynctack(imgview, path).execute(path);
          //           new MyBobAsynctack(imgview, path).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
                     ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
                     new MyBobAsynctack(imgview, path).executeOnExecutor(newCachedThreadPool, path);
                 }else{
                     imgview.setImageBitmap(getVideoThumbToCache(path));
                 }

             }

             class MyBobAsynctack extends AsyncTask<String, Void, Bitmap> {
          private ImageView imgView;
          private String path;

                  public MyBobAsynctack(ImageView imageView,String path) {
                     this.imgView = imageView;
                     this.path = path;
                 }

                  @Override
          protected Bitmap doInBackground(String... params) {
        //这里的创建缩略图方法是调用VideoUtil类的方法，也是通过 android中提供的 ThumbnailUtils.createVideoThumbnail(vidioPath, kind);
           //          Bitmap bitmap = VideoUtil.createVideoThumbnail(params[0], 70, 50, MediaStore.Video.Thumbnails.MICRO_KIND);
           //           MediaMetadataRetriever media =new MediaMetadataRetriever();
             //         media.setDataSource(params[0]);
               //       Bitmap bitmap = media.getFrameAtTime();
                 //     Matrix matrix = new Matrix();
                   //   matrix.postScale(0.4f, 0.4f);
                 //     bitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
                      Bitmap bitmap=ThumbnailUtils.createVideoThumbnail(params[0], MediaStore.Video.Thumbnails.MINI_KIND);
              //        ThumbnailUtils.extractThumbnail(bitmap,380,380, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                      bitmap=ThumbnailUtils.extractThumbnail(bitmap,384,384, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

          //加入缓存中
       //              if(getVideoThumbToCache(params[0]) == null){
                             addVideoThumbToCache(params[0], bitmap);
        //                 }
                     return bitmap;
                 }
          @Override
          protected void onPostExecute(Bitmap bitmap) {
                     if(imgView.getTag().equals(path)){//通过 Tag可以绑定 图片地址和 imageView，这是解决Listview加载图片错位的解决办法之一
                             imgView.setImageBitmap(bitmap);
                         }
                 }
      }
 }