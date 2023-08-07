package com.viewsonic.vsplayer1;


import android.graphics.Bitmap;

public class MediaFile {
    public String Name;
    public String Path;
    public String ParrentPath;
    public int Filetype;  //0 all 1 folder 2 video 3 audio 4 picture
    public boolean ischose;
    public int ID;
    public int PlayListID;
    public String Timetext;
    public long timeInMillisec;
    Bitmap CoverPicture;
    Bitmap LastPicture;


    public MediaFile()
    {
        Timetext="";
    }

    public void CheckTime()
    {

    }

}