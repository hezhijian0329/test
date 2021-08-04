package com.example.snow.bgvideorecord.util;

import android.graphics.Bitmap;

public class VideoInfo {
    private Integer Id;
    private String Path;
    private Long DateModified;
    private Long DateTaken;
    private Integer Duration;
    private Bitmap Image;

    public void setId(Integer integer){
        Id=integer;
    }
    public Integer getId(){
        return Id;
    }
    public void setPath(String string){
        Path=string;
    }
    public String getPath(){
        return Path;
    }
    public void setDateModified(Long l){
        DateModified=l;
    }
    public Long getDateModified() {
        return DateModified;
    }
    public void setDateTaken(Long l){
        DateTaken=l;
    }

    public Long getDateTaken() {
        return DateTaken;
    }

    public void setDuration(Integer duration) {
        Duration = duration;
    }

    public Integer getDuration() {
        return Duration;
    }

    public void setImage(Bitmap image){
        Image=image;
    }
    public Bitmap getImage(){
        return Image;
    }
}
