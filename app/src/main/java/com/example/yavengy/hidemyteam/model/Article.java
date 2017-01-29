package com.example.yavengy.hidemyteam.model;

import android.graphics.Bitmap;

import static com.example.yavengy.hidemyteam.Util.DbBitmapUtility.getDecImage;

/**
 * Created by yavengy on 12/17/16.
 */

public class Article {

    private String title;
    private Bitmap image;
    private byte[] imageArr;

    public Article(){
    }

    public Article(String title, Bitmap image){
        this.title = title;
        this.image = image;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public Bitmap getImage(){
        return getDecImage(imageArr);
    }

    public void setByteArray(byte[] incoming){
        this.imageArr = incoming;
    }

    public void setImage(Bitmap image){
        this.image = image;
    }

}
