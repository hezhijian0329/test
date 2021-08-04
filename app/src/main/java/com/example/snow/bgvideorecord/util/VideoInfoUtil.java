package com.example.snow.bgvideorecord.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.widget.ImageView;

import java.util.ArrayList;

public class VideoInfoUtil {
 //   private static final String[] VIDEO_PROJECT = { MediaStore.Video.Media._ID, MediaStore.Video.Media.DATE_MODIFIED, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATA,MediaStore.Video.Media.DATE_TAKEN };



    public static ArrayList<VideoInfo> getVideoinfos(Context context,String path){
        String[] VIDEO_PROJECT = { MediaStore.Video.Media._ID, MediaStore.Video.Media.DATE_MODIFIED, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATA,MediaStore.Video.Media.DATE_TAKEN };
        ArrayList<VideoInfo> mVideoInfos=new ArrayList<>();
   //     String selection = MediaStore.Video.Media.DATA+" like ?";
        String selection = MediaStore.Video.Media.DATA+" like ?";

//        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECT, selection, new String[]{"/storage/emulated/0/DCIM/Camera/Vc"+"%"}, MediaStore.MediaColumns.DATE_MODIFIED + " DESC");
 //       Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECT, selection, new String[]{"/storage/emulated/0/video/camera/vi"+"%"}, MediaStore.MediaColumns.DATE_MODIFIED + " DESC");
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECT, selection, new String[]{path}, MediaStore.MediaColumns.DATE_MODIFIED + " DESC");
 //       Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECT, null,null,null);
        if (cursor != null)

        {
            int idindex = cursor.getColumnIndex(BaseColumns._ID);
            int modifiedindex = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED);
            int durationindex = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
            int dataindex = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
            int takenindex = cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN);

            while (cursor.moveToNext()) {
                VideoInfo info = new VideoInfo();
                info.setId(cursor.getInt(idindex));
                info.setPath(cursor.getString(dataindex));
                info.setDateModified(cursor.getLong(modifiedindex));
                info.setDateTaken(cursor.getLong(takenindex));
                info.setDuration(cursor.getInt(durationindex));
           //     info.setImage(createVideoThumbnail(cursor.getString(dataindex), Thumbnails.MINI_KIND));


                Cursor thumbCursor=context.getContentResolver().query(Thumbnails.EXTERNAL_CONTENT_URI,null, Thumbnails.VIDEO_ID+"="+cursor.getInt(idindex),null,null);
                int thumbnailindex=thumbCursor.getColumnIndex(Thumbnails.DATA);
                if(thumbCursor.moveToFirst()){
                    info.setImage(BitmapFactory.decodeFile(thumbCursor.getString(thumbnailindex)));
                }
                mVideoInfos.add(info);
           //     Log.d("videoinfo", info.toString());
            }
        }
        return mVideoInfos;
    }

    class MyBitmapAsynctack extends AsyncTask<String, Void, Bitmap> {
        private ImageView imgView;
        private String path;

        public MyBitmapAsynctack(ImageView imageView,String path) {
            this.imgView = imageView;
            this.path = path;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            //这里的创建缩略图方法是调用VideoUtil类的方法，也是通过 android中提供的 ThumbnailUtils.createVideoThumbnail(vidioPath, kind);
            //          Bitmap bitmap = VideoUtil.createVideoThumbnail(params[0], 70, 50, MediaStore.Video.Thumbnails.MICRO_KIND);
            MediaMetadataRetriever media =new MediaMetadataRetriever();
            media.setDataSource(params[0]);
            Bitmap bitmap = media.getFrameAtTime();
            Matrix matrix = new Matrix();
            matrix.postScale(0.4f, 0.4f);
            bitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

            //加入缓存中
      //      if(getVideoThumbToCache(params[0]) == null){
        //        addVideoThumbToCache(path, bitmap);
      //      }
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
