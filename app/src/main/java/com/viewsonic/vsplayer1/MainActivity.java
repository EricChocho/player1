package com.viewsonic.vsplayer1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.RoundedCorner;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Boolean CanAdd;
    Bitmap   lastFrameBMP;
    IntentFilter intentFilter, intentFilter2;
    USBBroadcastReceiver usbBroadcastReceiver;
    SDBroadcastReceiver sdBroadcastReceiver;

    public String[] devices = {"/storage/emulated/0", "", "", ""};
    LinearLayout deviceicon1, deviceicon2, deviceicon3, deviceicon4;
    ImageView device1, device2, device3, device4,SpeedI,SpeedI_A;
    TextView Path, time1, time2, time1_A, time2_A, AudioName, AudioMan;

    private static final int UPDATE_TIME = 2, HANDLE_LASTPICTURE = 3, MOVE = 4, MOVE_SECOND = 5;
    int LastSelectedfileitem;
    int length;
    ListView listview, playlist, smallplaylist, smallplaylist_A;

    int currentdevice;  //0 local 1 usb1 2 usb2 3 Sdcard
    int currentrow;     //0 deviceslist 1 filelist 2 playlist
    int filelistposition;
    String filelistposition_Path;

    int CurrenMediaType;
    int PlayListDevices;
    int CurrentPictureW=0,CurrentPictureH=0;
    int CurrentDeviceW=0,CurrentDeviceH=0;

    int RepeatMode;  //0 no,1,all 2,one
    int  RandomMode; //0 no ,1 enable
    Boolean HasData=false,PlayFromOutSide=false;

    final int  REQUEST_WRITE_EXTERNAL_STORAGE=4;
    final int  REQUEST_READ_EXTERNAL_STORAGE=3;

    ArrayList<MediaFile> FinalList,FinalList1,FinalList2,FinalList3,FinalList4;





    ArrayList<MediaFile> FinalShortList; //for singal filetype
    ArrayList<MediaFile> FolderList;
    ArrayList<MediaFile> FileList;
    ArrayList<MediaFile> PlayList,SmallPlayList,OPlayList,PlayList1,PlayList2,PlayList3,PlayList4;
    ArrayList<MediaFile> FolderFileList;

    FlieListBaseAdapter adapter;
    PlayListBaseAdapter adapter2,adapter3;



    LayoutInflater inflater, inflater2, inflater3, inflater4;
    LinearLayout llContent, PlayListUI, PlayControlUI, MiniListUI,MiniListUI_A, AudioUI, PlayControlUI_A
            ;
    FrameLayout videoUI;

    TextureView textureView;

    int CurrentFileListLevel, CurrenPlayListIndex, PictureSec;
    ArrayList<String> ParentPath;
    Boolean ReflashFileList, InVideoViewUI,InAudioViewUI, InImageViewUI, isPlaying, SeekBarStart, SeekBarFocus;
    Button PlayButton, SeamButton, FastBackwardButton, LastButton, PlayPauseButton, NextButton, FastForwardButton, ListButton;
    Button FastBackwardButton_A, LastButton_A, PlayPauseButton_A, NextButton_A, FastForwardButton_A, ListButton_A;

    Button RandonButton, RAllButton, ROneButton;
    Button RandonButton_A, RAllButton_A, ROneButton_A;

    VideoView videoView;
    ImageView imageView, audiopic,imageView2;
    SeekBar timeBar, timeBar_A;

    Boolean  Leave=false,VideoCover,NextPause;
    MainActivity mActivity;

    MediaPlayer RealMediaPlayer;
    int CurrentPlaySpeed=1 ;
    int CurrentDirection=0; // 0=x 1=FF 2=FB

    MediaFile CurrentLongClickFolder=null;

    ArrayList<MediaFile> CurrentLongClickFolderList=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_3);

        mActivity=this;
        PlayFromOutSide=false;

        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, REQUEST_READ_EXTERNAL_STORAGE);
        }



        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        Log.i("VS Player", "Android API Version: " + Build.VERSION.SDK_INT);
        Log.i("VS Player", "Android OS Version " + Build.VERSION.RELEASE);
        Log.i("VS Player", "MODEL " + Build.MODEL);  //m2e :2x
     //   Log.i("VS Player", "Board " + Build.BOARD);
     //   Log.i("VS Player", "Brand  " + Build.BRAND);
     //   Log.i("VS Player", "Device " + Build.DEVICE);

        CurrentDeviceW=metrics.widthPixels;
        CurrentDeviceH=metrics.heightPixels;
        Log.i("VS Player", "UI " + CurrentDeviceW + " X " +CurrentDeviceH);

        PlayListUI = (LinearLayout) findViewById(R.id.Layout1);
        PlayControlUI = (LinearLayout) findViewById(R.id.Layout2);
        PlayControlUI_A = (LinearLayout) findViewById(R.id.Layout2A);
        MiniListUI = (LinearLayout) findViewById(R.id.ListLayout);
        MiniListUI_A = (LinearLayout) findViewById(R.id.ListLayout_A);


        AudioUI = (LinearLayout) findViewById(R.id.layout3);
        videoUI = (FrameLayout) findViewById(R.id.layout4);

        deviceicon1 = (LinearLayout) findViewById(R.id.iconlayout1);
        deviceicon2 = (LinearLayout) findViewById(R.id.iconlayout2);
        deviceicon3 = (LinearLayout) findViewById(R.id.iconlayout3);
        deviceicon4 = (LinearLayout) findViewById(R.id.iconlayout4);
        device1 = (ImageView) findViewById(R.id.storageIcon);
        device2 = (ImageView) findViewById(R.id.usbicon1);
        device3 = (ImageView) findViewById(R.id.usbicon2);
        device4 = (ImageView) findViewById(R.id.sdicon);
        Path = (TextView) findViewById(R.id.textView8);
        time1 = (TextView) findViewById(R.id.time1);

        time1_A = (TextView) findViewById(R.id.time1_A);
        time2 = (TextView) findViewById(R.id.time2);
        time2_A = (TextView) findViewById(R.id.time2_A);
         AudioMan=(TextView)findViewById(R.id.AudioMan);
         AudioName=(TextView)findViewById(R.id.AudioName);
        timeBar = (SeekBar) findViewById(R.id.timeBar);
        timeBar_A = (SeekBar) findViewById(R.id.timeBar_A);

        SpeedI= (ImageView) findViewById(R.id.SpeedImage);
        SpeedI_A= (ImageView) findViewById(R.id.SpeedImage_A);
        Path.setSelected(true);

        isUsbdevices=false;

        // isExistCard();
      //  //Log.i("Eric","SdPath:"+ getSDcardPath());
        getSDcardPath();
        for(String e:devices)
        {
         //   //Log.i("Eric",e);
        }
        hadleDeviceIcon();
        setIconfocus();

        currentdevice=0;  //0 local 1 usb1 2 usb2 3 Sdcard
        currentrow=0;     //0 deviceslist 1 filelist 2 playlist
        filelistposition=0;





        LastSelectedfileitem = -1;

        PlayButton = (Button) findViewById(R.id.PlayButton);
        SeamButton = (Button) findViewById(R.id.SeamlessButton);

        LastButton = (Button) findViewById(R.id.lastbt);
        PlayPauseButton = (Button) findViewById(R.id.playbt);
        NextButton = (Button) findViewById(R.id.nextbt);

        FastBackwardButton = (Button) findViewById(R.id.fbbt);
        FastForwardButton = (Button) findViewById(R.id.ffbt);
        ListButton = (Button) findViewById(R.id.minilistbt);
        ListButton_A = (Button) findViewById(R.id.minilistbt_A);

        RandonButton = (Button) findViewById(R.id.Playlistb1);
        RAllButton = (Button) findViewById(R.id.Playlistb2);
        ROneButton = (Button) findViewById(R.id.Playlistb3);

        RandonButton_A = (Button) findViewById(R.id.Playlistb1_A);
        RAllButton_A = (Button) findViewById(R.id.Playlistb2_A);
        ROneButton_A = (Button) findViewById(R.id.Playlistb3_A);



        LastButton_A = (Button) findViewById(R.id.lastbt_A);
        PlayPauseButton_A = (Button) findViewById(R.id.playbt_A);
        NextButton_A = (Button) findViewById(R.id.nextbt_A);

        FastBackwardButton_A = (Button) findViewById(R.id.fbbt_A);
        FastForwardButton_A = (Button) findViewById(R.id.ffbt_A);
        ListButton_A = (Button) findViewById(R.id.minilistbt_A);


        getFileList("/storage/emulated/0",0);
        listview = (ListView) findViewById(R.id.dd);
        playlist=(ListView)findViewById(R.id.playlist);
        smallplaylist=(ListView)findViewById(R.id.smallplaylistview);
        smallplaylist_A=(ListView)findViewById(R.id.smallplaylistview_A);



        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater3 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater4 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);




        adapter = new FlieListBaseAdapter(FinalList,inflater);
        adapter2 = new PlayListBaseAdapter(FinalList,inflater2);


        if(FinalList!=null)
        listview.setAdapter(adapter);


        setMediaButton();
        setMediaButton2();
/*

        timeBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SeekBarStart) {
                //    Toast.makeText(getApplicationContext(), "Scroll Mode", Toast.LENGTH_SHORT).show();
                    SeekBarStart = true;

                }
                else {
                //    Toast.makeText(getApplicationContext(), "Normal Mode", Toast.LENGTH_SHORT).show();
                    SeekBarStart = false;

                }
            }
        });

        */

/*
        timeBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                   // //Log.i("Eric","2.23 !!!!!  Has Focus");
                    if(!SeekBarStart)
                    {
                        SeekBarFocus=true;
                    }
                    // return true;
                }
            }
        });
        */
/*
        timeBar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction()==KeyEvent.ACTION_UP) {
                 //   //Log.i("Eric","!!!!!+"+keyEvent.getKeyCode());
                    if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT)
                    {
                     //   //Log.i("Eric","2.22 !!!!!  A LEFT+"+keyEvent.getKeyCode());
                        if(!SeekBarStart) {
                            if(!SeekBarFocus) {
                       //         //Log.i("Eric","2.23 !!!!!  B LEFT+"+keyEvent.getKeyCode());
                                NextButton.requestFocus();
                            }
                            else
                            {
                        //        //Log.i("Eric","2.23 !!!!!  C LEFT+"+keyEvent.getKeyCode());
                                SeekBarFocus=false;
                            }
                            return true;
                        }
                    }
                    else if(keyEvent.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT)
                    {
                       // //Log.i("Eric","2.22 !!!!!  A RIGHT+"+keyEvent.getKeyCode());
                        if(!SeekBarStart) {
                            if(!SeekBarFocus) {
                        //        //Log.i("Eric","2.23 !!!!!  B RIGHT+"+keyEvent.getKeyCode());
                                ListButton.requestFocus();
                            }
                            else
                            {
                         //       //Log.i("Eric","2.23 !!!!!  C RIGHT+"+keyEvent.getKeyCode());
                                SeekBarFocus=false;
                            }
                            return true;
                        }
                    }
                }
                return false;
            }
        });
*/
        timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int process=seekBar.getProgress();
                if(videoView.isPlaying())

                {

                    if(SeekBarStart) {
                        videoView.seekTo(process*1000);
                        //  SeekBarStart=false;
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                ////Log.i("Eric","!!!!!!!!!!!Start touch ");
                SeekBarStart=true;
            }



            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SeekBarStart=false;
                int process=seekBar.getProgress();
                if(videoView.isPlaying())
                {
                    // //Log.i
                    videoView.seekTo(process*1000);
                    isPlaying=true;
                }
            }
        });

        //2023.03.14


    //2023.05.11
        SeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(PlayList.size()>0&&playlist.getCount()>0)
                {
                    for(int k=PlayList.size()-1;k>=0;k--) {
                        PlayList.remove(k);
                    }

                    if(FinalList1!=null&&FinalList1.size()>0)FinalList1.clear();
                    else if(FinalList2!=null&&FinalList2.size()>0)FinalList2.clear();
                    else if(FinalList3!=null&&FinalList3.size()>0)FinalList3.clear();
                    else if(FinalList4!=null&&FinalList4.size()>0)FinalList4.clear();

                    //currentfolder

                    handelPlayList();
                    if(PlayList.size()==0) CurrenMediaType = 0;

                    File e = new File(FinalList.get(0).Path);
                    getFileList(e.getParent(), CurrenMediaType);
                //    getFileList(FinalList.get(0).ParrentPath, CurrenMediaType);
                    handleFileListChoice();
                    adapter = new FlieListBaseAdapter(FinalList, inflater);
                    listview.setAdapter(adapter);
                    setFileListBG(0, false);
                    //07.04
                    if(CurrentLongClickFolderList!=null &&CurrentLongClickFolderList.size()>0)
                        CurrentLongClickFolderList.clear();

                }

            }});        PlayButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    view.setBackgroundResource(R.drawable.play_hover);

                }
                else
                    view.setBackgroundResource(R.drawable.play_default);
            }
        });





        PlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PlayList.size()>0 &&playlist.getCount()>0) {
             //    Log.i("Eric","2023.07.04 "+playlist.getCount());
                    PlayMedia();
                }
            }
        });





        SeamButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    view.setBackgroundResource(R.drawable.seam_hover);
                else
                    view.setBackgroundResource(R.drawable.seam_default);
            }
        });



        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

              //  //Log.i("Eric","Eri 2023.05.11  長按");
                if(FinalList.get(i).Filetype==1)
                {

                     CurrentLongClickFolder=FinalList.get(i);

                   // if(CurrentLongClickFolder!=null)
//               //     //Log.i("Eric","Eri 2023.06.07  長按"+CurrentLongClickFolder.Path);

                    File currentfolder =new File(FinalList.get(i).Path);
                    File[] children=currentfolder.listFiles();

                  //  CurrentLongClickFolder.ischose=false;
                  if(!CurrentLongClickFolder.ischose) {
                      if (children.length > 0) {

                          FolderFileList = new ArrayList<>();
                          MediaFile n;
                          for (File e : children) {
                              if (e.isFile()) {

                                  //     CurrenMediaType;
                                  n = null;
                                  //2023.05.16
                                  if (CurrenMediaType == 0 || CurrenMediaType == 2) {
                                      if (e.getName().toLowerCase().endsWith("mp4")
                                              || e.getName().toLowerCase().endsWith("3gp")
                                              || e.getName().toLowerCase().endsWith("mkv")
                                              || e.getName().toLowerCase().endsWith("avi")
                                              || e.getName().toLowerCase().endsWith("m2ts")
                                              || e.getName().toLowerCase().endsWith("m4v")
                                              || e.getName().toLowerCase().endsWith("ts")
                                              || e.getName().toLowerCase().endsWith("mpeg")
                                              || e.getName().toLowerCase().endsWith("mpg")
                                              || e.getName().toLowerCase().endsWith("vob")
                                              || e.getName().toLowerCase().endsWith("tp")
                                              || e.getName().toLowerCase().endsWith("trp")

                                              || e.getName().toLowerCase().endsWith("mov")
                                      ) {
                                          n = new MediaFile();
                                          n.Filetype = 2;

                                          n.ischose = false;
                                          n.Path = e.getPath();
                                          n.ParrentPath = currentfolder.getPath();
                                          n.Name = e.getName();
                                          FolderFileList.add(n);
                                      }
                                  } else if (CurrenMediaType == 3) {
                                      if (e.getName().toLowerCase().endsWith("mp3")
                                              || e.getName().toLowerCase().endsWith(".wav")
                                              || e.getName().toLowerCase().endsWith(".flac")

                                      ) {
                                          n = new MediaFile();
                                          n.Filetype = 3;
                                          n.ischose = false;
                                          n.Path = e.getPath();
                                          n.ParrentPath = currentfolder.getPath();
                                          n.Name = e.getName();
                                          FolderFileList.add(n);
                                      }
                                  } else if (CurrenMediaType == 4) {
                                      if (e.getName().toLowerCase().endsWith("jpg") ||
                                              e.getName().toLowerCase().endsWith("jpeg") ||
                                              e.getName().toLowerCase().endsWith("png") ||
                                              e.getName().toLowerCase().endsWith("bmp")
                                      ) {
                                          n = new MediaFile();
                                          n.Filetype = 4;
                                          n.ischose = false;
                                          n.Path = e.getPath();
                                          n.ParrentPath = currentfolder.getPath();
                                          n.Name = e.getName();
                                          FolderFileList.add(n);
                                      }

                                  }
                              }
                          }

                         // //Log.i("Eric", "2023 長按Folder:" + CurrenMediaType);

                          if (FolderFileList.size() > 0) {



                          //    //Log.i("Eric", "2023/02/13 folderfiles:" + FolderFileList.size());
                              for (MediaFile f : FolderFileList) {
                        //          //Log.i("Eric", "2023/02/13 f:" + f.Path);
                                  f.ischose = true;

                                  MediaFile playitem = f;
                                  File playitemfile = new File(playitem.Path);
                                  long time = MediaTimeCheck(playitemfile,f.Filetype);
                                  if(time==-99)
                                  {
                          //            //Log.i("Eric", "2023/05/11 f:" +MediaTimeCheck2(playitemfile));
                                    time=   MediaTimeCheck2(playitemfile);
                          //            //Log.i("Eric", "2023/05/11 f:" +time);

                                  }
                                  playitem.timeInMillisec = time;

                                  playitem.Timetext = timePrase(time);

                                  if (playitem.timeInMillisec == -1) {
                                      playitem.timeInMillisec = 5000;
                                      playitem.Timetext = "00:00:05";
                                  }
                                  //Log.i("Eric", "playitem " + playitem.Name + ":" + playitem.timeInMillisec + ":" + playitem.Timetext);
                                  // checkAudioCover(playitem);



                                  if (playitem.timeInMillisec != -99) {
                                   //   //Log.i("Eric", "!!!! add:" + playitem.Name);

                                      if(PlayListDevices!=currentdevice+1 && PlayList.size()>0)
                                      {

                                          PlayList.clear();
                                      }

                                      CanAdd=true;
                                      if(PlayList.size()>0)
                                      {
                                          for(int j=0;j<PlayList.size();j++)
                                          {
                                             if(PlayList.get(j).Path.equals(playitem.Path))
                                             {
                                              //   Log.i("Eric","Eric 2023.07.04 has"+PlayList.get(j).Path);
                                               //  Log.i("Eric","Eric 2023.07.04 has"+playitem.Path);
                                                 CanAdd=false;
                                                 break;
                                             }
                                          }

                                      }
                                      if(CanAdd)
                                      PlayList.add(playitem);
                                      //06.08
                                      if(FinalList1!=null&&FinalList1.size()>0)FinalList1.clear();
                                      else if(FinalList2!=null&&FinalList2.size()>0)FinalList2.clear();
                                      else if(FinalList3!=null&&FinalList3.size()>0)FinalList3.clear();
                                      else if(FinalList4!=null&&FinalList4.size()>0)FinalList4.clear();


                                  //    //Log.i("Eric","2023.05.20 clean playlist click add!!!! add:");

                                      PlayListDevices=currentdevice+1;

                                  } else {
                                   //   //Log.i("Eric", "!!!! not add:" + playitem.Name);



                                  }



                                  if (PlayList.size() == 1) {
                                      CurrenMediaType = PlayList.get(0).Filetype;
                                  }
                              }
                            handelPlayList();
            getFileList(currentfolder.getParent(), CurrenMediaType);
                              if(CurrentLongClickFolderList==null) CurrentLongClickFolderList=new ArrayList<MediaFile>();
                              CurrentLongClickFolderList.add(CurrentLongClickFolder);

                              handleFileListChoice();
                              adapter = new FlieListBaseAdapter(FinalList, inflater);
                              listview.setAdapter(adapter);
                              setFileListBG(0, false);

                          } //has file to add to Playlist

                          else {
                              CurrentLongClickFolder=null;

                          }


                      } //not Empty Folfer
                  }// not choiced Folder
                  else  //CHOICEed folder need remove
                  {
                      if(CurrentLongClickFolder!=null) {
                        //  //Log.i("Eric", "2023/05/11 is chooiced" + CurrentLongClickFolder.ischose);
                       //   //Log.i("Eric", "2023/05/11 is chooiced" + CurrentLongClickFolder.Path);
                      }
                    //  //Log.i("Eric", "2023/05/11 is StartClean");

                      if(PlayList.size()>0)
                      {


                         //currentfolder
                        if(children.length>0){

                            for(int k=PlayList.size()-1;k>=0;k--)
                            {
                                for(int k2=0;k2<children.length;k2++)
                                {

                                    if(children[k2].getPath().contains(PlayList.get(k).Path)){
                                      //  //Log.i("Eric", "2023/05/11 remove k:"+k+"path"+PlayList.get(k).Path);
                                        PlayList.remove(k);
                                        break;
                                    }
                                }
                            }

                        }
                        if(CurrentLongClickFolder!=null) {
                            Log.i("Eric", "Eric 2023.07.03 CurrentLongClickFolder:" + CurrentLongClickFolder.Path);
                            Log.i("Eric", "Eric 2023.07.03 index A:" + CurrentLongClickFolderList.size());

                            int index=0;
                            for  (int j=0;j<CurrentLongClickFolderList.size();j++) {
                                if(CurrentLongClickFolderList.get(j).Path.equals(CurrentLongClickFolder.Path));

                                index=j;

                            }


                            Log.i("Eric", "Eric 2023.07.03 index B:" + index);
                            Log.i("Eric", "Eric 2023.07.03 index:C" + CurrentLongClickFolderList.get(index).Path);

                            CurrentLongClickFolderList.remove(index);

                        }
                        else
                        {
                            Log.i("Eric", "Eric 2023.07.03 index: NULL");
                        }

                          CurrentLongClickFolder=null;


                          handelPlayList();
                        if(PlayList.size()==0) CurrenMediaType = 0;



                          getFileList(currentfolder.getParent(), CurrenMediaType);

                          adapter = new FlieListBaseAdapter(FinalList, inflater);
                          listview.setAdapter(adapter);
                          handleFileListChoice();

                          setFileListBG(0, false);

                      }


                  }

                }  // 長按 Folder
                else {
                    ////Log.i("Eric", "2023/02/13 長按  not Folder:");
                }
                //  listview.setSelection(2);
                return true;
                // return false; //會執行click
            }
        });

        listview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String msg = adapterView.getItemAtPosition(i).toString();
             //   //Log.i("Eric", "!!!!selected i:" + i + ":" + msg);
                LinearLayout yr = (LinearLayout) view.findViewById(R.id.filerowly);


                listview.getFirstVisiblePosition();
                listview.getLastVisiblePosition();

              //  //Log.i("Eric", "A: i:" + i + ":" + listview.getFirstVisiblePosition() + ":" + listview.getLastVisiblePosition());
                int offset=listview.getFirstVisiblePosition();

                for (int j = 0; j<listview.getCount(); j++) {
                 //   //Log.i("Eric", "B i:" + i + "j:" + j);
                    if (listview.getChildAt(j) != null) {
                     //   //Log.i("Eric", "c i:" + i + "j:" + j);
                        View ls = (View) listview.getChildAt(j); ls.findViewById(R.id.filerowly);
                        LinearLayout yr2 = (LinearLayout) ls.findViewById(R.id.filerowly);



                        if (listview.getChildAt(j).isSelected()) {
                      //      //Log.i("Eric","e:j"+j+":"+FinalList.get(j+offset).ischose);
                     //       //Log.i("Eric","eee:j"+j+":"+FinalList.get(j+offset).Name);


                            if(FinalList.get(j+offset).ischose) {


                                yr2.setBackgroundResource(R.drawable.list_hover_choose);


                            }
                            else {
                                if(FinalList.get(j+offset).Filetype>1)
                                    yr2.setBackgroundResource(R.drawable.list_hover);
                                else
                                    yr2.setBackgroundResource(R.drawable.folder_hover
                                    );

                            }

                        }
                        else
                        {


                            if(FinalList.get(j+offset).ischose) {
                                if(FinalList.get(j+offset).Filetype>=1)
                                    yr2.setBackgroundResource(R.drawable.list_choose);

                            }
                            else {
                                if(FinalList.get(j+offset).Filetype>1)
                                    yr2.setBackgroundResource(R.drawable.list);
                                else
                                    yr2.setBackgroundResource(R.drawable.folder_default);

                            }


                        }

                    }

                }


        //        //Log.i("Eric", "i:" + LastSelectedfileitem + ":" + LastSelectedfileitem);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        //2023.05.16
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
           //     //Log.i("Eric", "click i:" + i + "name:" + FinalList.get(i).Name);
           //     //Log.i("Eric", "click i:" + i + "name:" + FinalList.get(i).Filetype); //1 folder 2 file
                if (FinalList.get(i).Filetype == 1) {
                    // handelPlayList();
                    File currentItem = new File(FinalList.get(i).Path);

                //    //Log.i("Eric", "11:" + currentItem.getPath());
                //    //Log.i("Eric", "12:" + currentItem.getParent());


                    getFileList(currentItem.getPath(), CurrenMediaType);

                    if(FinalList.size()>0) {
                   //     //Log.i("Eric", "2023/02/08 EnterFolder currentfiletype:" + CurrenMediaType);
                        //check choice
                        if (PlayList.size() > 0) {
                           /*
                            for (int j = 0; j < FinalList.size(); j++) {
                                for (int k = 0; k < PlayList.size(); k++) {
                                    if (PlayList.get(k).Path.equals(FinalList.get(j).Path)) {
                                        FinalList.get(j).ischose = true;
                                        break;
                                    } else {
                                        FinalList.get(j).ischose = false;
                                    }

                                }
                            }  ///=handleFileListChoice(); 2023.07.04
                            */
                            handleFileListChoice();
                        }
                    }
                    else
                    {
                       // //Log.i("Eric","2023/02/09:"+currentdevice);
                        if(currentdevice==0)deviceicon1.requestFocus();
                        else if(currentdevice==1)deviceicon2.requestFocus();
                    }
                    adapter = new FlieListBaseAdapter(FinalList, inflater);
                    CurrentFileListLevel++;
                    ParentPath.add(currentItem.getParent());
                    listview.setAdapter(adapter);

                    Path.setText(currentItem.getPath());


                } // Enter Subfoder
                else {

                    //PlayList
                    if (!FinalList.get(i).ischose) {
                        MediaFile playitem= FinalList.get(i);

                        File playitemfile=new File(playitem.Path);
                        long time=MediaTimeCheck(playitemfile,playitem.Filetype);
                     //   //Log.i("Eric","Eric 2023.05.11 "+time);

                        if(time==-99)
                        {
                        //    //Log.i("Eric", "2023/05/11 f:" +MediaTimeCheck2(playitemfile));
                           // time=   MediaTimeCheck2(playitemfile);

                            //2023.05.12


                            time=120000;
                        //    //Log.i("Eric", "2023/05/11 f:" +time);

                        }
                        playitem.timeInMillisec=time;

                        playitem.Timetext=timePrase(time);

                        if(playitem.timeInMillisec==-1) {
                            playitem.timeInMillisec = 5000;
                            playitem.Timetext="00:00:05";
                        }
                    //    //Log.i("Eric","playitem "+playitem.Name+":"+playitem.timeInMillisec+":"+playitem.Timetext);

                        //   checkAudioCover(playitem);



                        // PlayList.add(playitem);

                        if(playitem.timeInMillisec!=-99) {
                           // //Log.i("Eric","2023.05.20 click add!!!! add:"+playitem.Name);

                            if(PlayList.size()>0)
                            {



                                if(playlist.getCount()==0)
                                {
                                    if(FinalList1!=null&&FinalList1.size()>0)FinalList1.clear();
                                    else if(FinalList2!=null&&FinalList2.size()>0)FinalList2.clear();
                                    else if(FinalList3!=null&&FinalList3.size()>0)FinalList3.clear();
                                    else if(FinalList4!=null&&FinalList4.size()>0)FinalList4.clear();

                                    PlayList.clear();
                                    PlayListDevices=currentdevice+1;
                                }
                            }

                            if( PlayListDevices==0)PlayListDevices=currentdevice+1;

                            PlayList.add(playitem);
                        }
                        else
                        {

                        }
                        //   FinalList.get(i).PlayListID=PlayList.size();
                        FinalList.get(i).ischose = true;
                        String choicePath = FinalList.get(i).Path;

                        //CurrenMediaType

                        if (PlayList.size() == 1) {
                            CurrenMediaType = PlayList.get(0).Filetype;

                            FinalShortList = new ArrayList<>();
                            //
                            for (int j = 0; j < FinalList.size(); j++) {
                                FinalShortList.add(FinalList.get(j));


                            }
                            File e = new File(FinalList.get(i).Path);
                            getFileList(e.getParent(), CurrenMediaType);


                            //Collections.sort(FinalList,comparator);//comparatorOnlyFile
                            Collections.sort(FinalList, comparatorOnlyFile);
                            adapter = new FlieListBaseAdapter(FinalList, inflater);
                            listview.setAdapter(adapter);


                            for (int k = 0; k < FinalList.size(); k++) {

                                if (FinalList.get(k).Path.equals(choicePath)) {
                                    FinalList.get(k).ischose = true;
                                    View V = (View) listview.getChildAt(i);
                                    LinearLayout lv = (LinearLayout) view.findViewById(R.id.filerowly);
                                    lv.setBackgroundResource(R.drawable.list_hover_choose);
                                } else {
                                    FinalList.get(k).ischose = false;
                                }



                            }
                        } else  //not first File in Play list
                        {


                            View V = (View) listview.getChildAt(i);
                            LinearLayout lv = (LinearLayout) view.findViewById(R.id.filerowly);
                            lv.setBackgroundResource(R.drawable.list_hover_choose);


                        }
                    } else {


                        for (int j = 0; j < PlayList.size(); j++) {

                            if (PlayList.get(j).Path.equals(FinalList.get(i).Path)) {
                                PlayList.remove(j);
                                 FinalList.get(i).ischose = false;
                            }
                        }

                        if (PlayList.size() < 1) {
                            //clean all Playlist
                            CurrenMediaType=0;
                            PlayListDevices=0;
                            File current=new File(FinalList.get(i).Path);
                            getFileList(current.getParent(), CurrenMediaType);
                            adapter = new FlieListBaseAdapter(FinalList, inflater);

                            listview.setAdapter(adapter);

                        }


                        else {


                            View V = (View) listview.getChildAt(i);
                            LinearLayout lv = (LinearLayout) view.findViewById(R.id.filerowly);
                            lv.setBackgroundResource(R.drawable.list_hover);

                        }

                    }


                    handelPlayList();
                }   //  add to Play List
            }

        });

        listview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                { //  //Log.i("Eric","2023/02/10+Enter List");

                   // //Log.i("Eric","2023/02/10+Enter List");
                }
                else
                {
                   // //Log.i("Eric","2023/02/10+leave List");
                }
            }
        });

        listview.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN) {

                    int firstVisibleItem = listview.getFirstVisiblePosition();

                    if(listview.getSelectedItemPosition()==0)return true;
                }
                return false;
            }
        });


        SeamButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // 按上键时，ListView 的第一个项目已经在顶部，不执行滚动
                    return true;
                }
                return false;
            }
        });

        PlayButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {



                if (keyCode == KeyEvent.KEYCODE_DPAD_UP ) {
                    // 按上键时，ListView 的第一个项目已经在顶部，不执行滚动


                  //  playlist.requestFocus();
/*

*/
                    return false;
                }
                else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {

                   // SeamButton.requestFocus();
                    return false;
                }

                return false;
            }
        });

        SeamButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {

                  //  listview.requestFocus();

                }
                return false;
            }
        });

        // llContent = findViewById(R.id.PlayListContent);

        // TextView  playlist=(TextView) findViewById(R.id.playlist);
        // String s=("01  1.mp3+")
        PlayList=new ArrayList<MediaFile>();
        OPlayList=new ArrayList<MediaFile>();

        CurrentFileListLevel=0;
        ParentPath=new ArrayList<String>();
        ReflashFileList=false;


        CurrenMediaType=0;
        PlayListDevices=0;
/*
        MediaFile test=new MediaFile();
        test.Name="1234567890abcdefg.mp4";
        test.Timetext="23:36.11";

        MediaFile test2=new MediaFile();
        test2.Name="A2.mp4";
        test2.Timetext="02:12.11";
*/

        imageView=(ImageView)findViewById(R.id.imageView);
        if(Build.MODEL.contains("M2e")){
          //  imageView.setScaleType(ImageView.ScaleType.CENTER);
          //  imageView.setScaleType(ImageView.ScaleType.FIT_START);
          //  //Log.i("Eric","2023.06.02 M2E !!!");
        }
        else {
           // //Log.i("Eric","2023.06.02 NOT M2E !!! image Center");
           // imageView.setScaleType(ImageView.ScaleType.CENTER);
        }
        videoView=(VideoView) findViewById(R.id.videoView);

        audiopic=(ImageView)findViewById(R.id.audiopic);


        InVideoViewUI=false;
        InAudioViewUI=false;
        InImageViewUI=false;
        CurrenPlayListIndex=0;

        RepeatMode=0;RandomMode=0;
        SeekBarStart=false;

        textureView=(TextureView) findViewById(R.id.textureView);

        textureView.setSurfaceTextureListener(f);

        RealMediaPlayer=new MediaPlayer();


        RealMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
                try {
              //      //Log.i("VS_Player", " Current video Side" + width + ":" + height);
                    float videoProportion = (float) width / (float) height;

                    View parentView = (View) textureView.getParent();

                    // 取得父視圖的寬度和高度
                    int parentWidth = parentView.getWidth();
                    int parentHeight = parentView.getHeight();
                    int ChangeWidth = width;
                    int ChangeHeight = height;


                    if (width > height) {

                        width = parentWidth;
                        height = (int) (width / videoProportion);
                     //   //Log.i("VS_Player", " Final Play Sizes 1:" + width + ":" + height);
                        if (height > parentHeight) {
                            width = (int) (parentWidth * parentHeight / height);
                            height = parentHeight;
                     //       //Log.i("VS_Player", " Final Play Sizes 1b:" + width + ":" + height);
                        }


                    } else {
                        //   height = parentHeight;
                        width = (int) (parentHeight * width / height);
                        height = parentHeight;
                   //     //Log.i("VS_Player", " Final Play Sizes 2:" + width + ":" + height);

                    }
                   // //Log.i("VS_Player", " Final Play Sizes" + width + ":" + height);
                    textureView.setLayoutParams(new FrameLayout.LayoutParams(width, height, Gravity.CENTER));


            }
            catch (Exception e)
            {}
        }});

        RealMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //     mMediaPlayer.prepareAsync();

               // if(2)
                //Log.i("Eric","2023.05.05  Enter setOnPreparedListener");
                RealMediaPlayer.start();

            //    Log.i("Eric","2023.06.28  V:"+imageView2.getVisibility());


                //imageView2
                 // if(smallplaylist.getCount()==PlayList.size())
                handleMiniPlaylistIcon();

                if(NextPause)
                {

                    RealMediaPlayer.pause();
                }
                //Log.i("Eric","2023.05.05  RealMediaPlayer.start()");

            }
        });

        //2023.05.



        RealMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                try {
                    //Log.i("Eric","Eric 05.06 media A3:"+CurrenPlayListIndex);

                    //Log.i("Eric","Eric 05.02 Repeat  :"+RepeatMode);
                    //Log.i("Eric","2023.05.05  RsetOnCompletionListener");
                    //Log.i("Eric","Eric 05.05 media A2:"+CurrenPlayListIndex);
                    if(InAudioViewUI||HasData)
                    CurrenPlayListIndex++;
                    //Log.i("Eric","Eric 05.02 size :"+PlayList.size());
                    if(RepeatMode!=0) {
                        if(RepeatMode==1) {
                            if (CurrenPlayListIndex >= PlayList.size()) CurrenPlayListIndex = 0;
                        }
                        else if(RepeatMode==2)
                        {
                            CurrenPlayListIndex--;
                        }
                    }
                 //   //Log.i("Eric","Eric 04.27 Change to :"+PlayList.get(CurrenPlayListIndex).Path);
                    //Log.i("Eric","Eric 04.27 Change to :"+CurrenPlayListIndex);

///            Play ALLL
                    if (CurrenPlayListIndex < PlayList.size()) {
                        RealMediaPlayer.reset();
                        RealMediaPlayer.setDataSource(PlayList.get(CurrenPlayListIndex).Path);

                        //Log.i("Eric","2023.05.10 Player date:"+PlayList.get(CurrenPlayListIndex).Path);
                        if(CurrenMediaType==3)   checkAudioCover(PlayList.get(CurrenPlayListIndex));

                        //Log.i("Eric", "2023.04.27 preparesync B");
                     //  RealMediaPlayer.prepareAsync();
                        RealMediaPlayer.prepare();

                        if(HasData)HasData=false;

                        time2.setText(PlayList.get(CurrenPlayListIndex).Timetext);//   PlayListUI.setVisibility(View.INVISIBLE);
                        //   InViewViewUI = true;
                        //  CurrenPlayListIndex=0;
                        timeBar.setMax((int)PlayList.get(CurrenPlayListIndex).timeInMillisec/1000);
                        timeBar.setProgress(0);
                        time1.setText("00:00.00");
                        CurrentDirection=0;
                        CurrentPlaySpeed=1;
                        FF2handler.removeCallbacksAndMessages(null);


                        TimeBarhandler.removeCallbacksAndMessages(null);
                        TimeBarhandler.sendEmptyMessage(UPDATE_TIME);

                        //2023.07.06  M2E OK 問題暫時 PEnndy

                        /*
                        if (PlayControlUI.getVisibility() == View.INVISIBLE) {


                            PlayControlUI.setVisibility(View.VISIBLE);
                            PlayPauseButton.requestFocus();
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);

                            Controlhandler.postAtTime(Crunable, next);

                        } else {
                            Controlhandler.removeCallbacks(Crunable);
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);

                            Controlhandler.postAtTime(Crunable, next);

                        }
                        */

                    }
                 else
                    {
                        //Log.i("Eric", "2023.05.03 Media Stop");
                       RealMediaPlayer.stop();
                        if(HasData)HasData=false;



                       //if(!PlayFromOutSide)
                       {
                           videoUI.setVisibility(View.INVISIBLE);
                           AudioUI.setVisibility(View.INVISIBLE);
                           VideoCover = true;
                           PlayListUI.setVisibility(View.VISIBLE);

                           isPlaying = false;

                           PlayControlUI.setVisibility(View.INVISIBLE);
                           //  MiniListUI.setVisibility(View.VISIBLE);
                           //  MiniListUI.setVisibility(View.INVISIBLE);
                           //  PlayPauseButton.requestFocus();

                           InVideoViewUI = false;
                           imageView2.setVisibility(View.VISIBLE);
                           VideoCover = true; TimeBarhandler.removeCallbacksAndMessages(null);
                           Controlhandler.removeCallbacksAndMessages(null);
                           FF2handler.removeCallbacksAndMessages(null);
                           PlayButton.requestFocus();
                           Log.i("Eric", "2023.06.09 Media Stop");
                       }
                       /*
                       else{
                           TimeBarhandler.removeCallbacksAndMessages(null);
                           Controlhandler.removeCallbacksAndMessages(null);
                           FF2handler.removeCallbacksAndMessages(null);
                           mActivity.finish();
                       }

                      */


                    }
                    // Repeat One{
                 //   else
                 //       MediaPlayer1.start();
                    // MediaPlayer2.setDataSource(Path2);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        imageView2=(ImageView) findViewById(R.id.imageView2);

        imageView2.setVisibility(View.VISIBLE);
        VideoCover=true;
        NextPause=false;

        Uri videoUri = getIntent().getData();
        if(videoUri!=null) {
            //2023.05.16
            //file:///mnt/usb/72D00E7DD00E4835/1t_seamless/E1.mp4
            //Log.i("Eric", "2023.05.18 Eric :" + videoUri);
            //Log.i("Eric", "2023.05.18 Eric :" + videoUri.toString());
            //Log.i("Eric", "2023.05.18 Eric :" + videoUri.getPath());

            ContentResolver contentResolver = getContentResolver();
            String mimeType = contentResolver.getType(videoUri);

            //Log.i("Eric", "2023.05.18 Eric mine:" + mimeType);

///mnt/usb/72D00E7DD00E4835/1t_video/100 Songs You´ve Heard And Don´t Know The Name.mp4
            MediaFile n = new MediaFile();
            File f=new File(videoUri.getPath());
            n.Name = f.getName();
            n.Path = f.getAbsolutePath();
            n.ischose = false;

            if ( videoUri.getPath().toLowerCase().endsWith(".mp4")
                    || videoUri.getPath().toLowerCase().endsWith(".3gp")
                    || videoUri.getPath().toLowerCase().endsWith(".mkv")
                    || videoUri.getPath().toLowerCase().endsWith(".avi")
                    || videoUri.getPath().toLowerCase().endsWith(".m2ts")
                    || videoUri.getPath().toLowerCase().endsWith(".m4v")
                    || videoUri.getPath().toLowerCase().endsWith(".ts")
                    || videoUri.getPath().toLowerCase().endsWith(".mpeg")
                    || videoUri.getPath().toLowerCase().endsWith(".mpg")
                    || videoUri.getPath().toLowerCase().endsWith(".vob")
                    || videoUri.getPath().toLowerCase().endsWith(".tp")
                    || videoUri.getPath().toLowerCase().endsWith(".trp")

                    || videoUri.getPath().toLowerCase().endsWith(".mov")) {
                CurrenMediaType = 2;

                n.Filetype = 2;

            }
            else if  ( videoUri.getPath().toLowerCase().endsWith(".mp3")
                    || videoUri.getPath().toLowerCase().endsWith(".wav")
                    || videoUri.getPath().toLowerCase().endsWith(".flac")
                   ) {
                CurrenMediaType = 3;

                n.Filetype = 3;

            }
            else if  ( videoUri.getPath().toLowerCase().endsWith(".jpg")
                    || videoUri.getPath().toLowerCase().endsWith(".jpeg")
                    || videoUri.getPath().toLowerCase().endsWith(".bmp")
                    || videoUri.getPath().toLowerCase().endsWith(".png")
            ) {


                CurrenMediaType = 4;

                n.Filetype = 4;

            }


                    n.PlayListID=0;




            OutSidePlayList(n);
        }


    }//onCreate


//06.02

    void setSpeed()
    {
        //Log.i("Eric","2023.06.01 Enter Current SetSpeed:"+CurrentPlaySpeed);
        if(CurrentPlaySpeed==0|CurrentPlaySpeed==1) {
            SpeedI.setVisibility(View.INVISIBLE);
            SpeedI_A.setVisibility(View.INVISIBLE);

        }

        else if(CurrentPlaySpeed==2) {
            SpeedI.setVisibility(View.VISIBLE);
            SpeedI.setImageResource(R.drawable.speed2x);
            SpeedI_A.setVisibility(View.VISIBLE);
            SpeedI_A.setImageResource(R.drawable.speed2x);
        }
        else if(CurrentPlaySpeed==4)
        {   SpeedI.setVisibility(View.VISIBLE);
            SpeedI.setImageResource(R.drawable.speed4x);
            SpeedI_A.setVisibility(View.VISIBLE);
            SpeedI_A.setImageResource(R.drawable.speed4x);
        }
        else if(CurrentPlaySpeed==8)
        {SpeedI.setVisibility(View.VISIBLE);
            SpeedI.setImageResource(R.drawable.speed8x);
            SpeedI_A.setVisibility(View.VISIBLE);
            SpeedI_A.setImageResource(R.drawable.speed8x);

        }
        else if(CurrentPlaySpeed==16)
        {SpeedI.setVisibility(View.VISIBLE);
            SpeedI.setImageResource(R.drawable.speed16x);
            SpeedI_A.setVisibility(View.VISIBLE);
            SpeedI_A.setImageResource(R.drawable.speed16x);

        }
        else if(CurrentPlaySpeed==32)
        {   SpeedI.setVisibility(View.VISIBLE);
            SpeedI.setImageResource(R.drawable.speed32x);
            SpeedI_A.setVisibility(View.VISIBLE);
            SpeedI_A.setImageResource(R.drawable.speed32x);
        }

    }


    void PlayMedia(){
        try{
        //  Toast.makeText(MainActivity.this, "SEAMLESS PLAY", Toast.LENGTH_LONG).show();
        //Log.i("Eric", "2023.05.25 Enter PlayMedia:"+playlist.getCount());
        //2023.05.20
        if(playlist.getCount()>0 || (playlist.getCount()==0 && PlayList.size()>0)){

        //if(PlayList.size()>0) {
            //Log.i("Eric", "2023.05.25 name" + CurrenMediaType);
            //Log.i("Eric", "2023.05.25 name" + PlayList.get(0).Path);
            //Log.i("Eric", "2023.05.25 name" + PlayList.size());

            SmallPlayList=PlayList;
            adapter3 = new PlayListBaseAdapter(SmallPlayList,inflater3);
            smallplaylist.setAdapter(adapter3);

            TimeBarhandler.removeCallbacksAndMessages(null);
            //if(CurrenMediaType!=2){
            //    PlayButton.performClick();
            // }
            if (CurrenMediaType==2)
            {
                CurrentDirection = 0;
                CurrentPlaySpeed = 1;

                //Log.i("Eric","2023.05.25  onClick");
                if(textureView==null)
                {
                    //Log.i("Eric","2023.05.25  text null");
                    textureView=(TextureView) findViewById(R.id.textureView);

                    //2023.05.11
                    // textureView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

                    //Log.i("Eric","2023.05.25 onCreate setSurfaceTextureListene ");
                    textureView.setSurfaceTextureListener(f);
                    //  RealMediaPlayer.setSurface(new Surface(textureView.getSurfaceTexture()));

                }

               // RealMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);


          //     RealMediaPlayer.setHardwareRendering(true);
           //     RealMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                //RealMediaPlayer.setVideoCodec(MediaPlayer.VIDEO_CODEC_HW_ACCELERATION);
                videoUI.setVisibility(View.VISIBLE);
                VideoCover=false;
                PlayListUI.setVisibility(View.INVISIBLE);
                PlayControlUI_A.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                isPlaying=true;
                PlayControlUI.setVisibility(View.VISIBLE);
                MiniListUI.setVisibility(View.INVISIBLE);
                PlayPauseButton.requestFocus();

                InVideoViewUI = true;


                time2.setText(PlayList.get(0).Timetext);
                timeBar.setMax((int)PlayList.get(0).timeInMillisec/1000);
                timeBar.setProgress(0);
                time1.setText("00:00.00");

                setSpeed();
                //2023.06.1
                //SpeedI.setImageResource(R.drawabNu);
                SpeedI.setVisibility(View.INVISIBLE);


                CurrenPlayListIndex=0;
                //RealMediaPlayer.setDataSource(Uri.parse(PlayList.get(0).Path));

                //Log.i("Eric","Eric 2023.05.25 Change to :"+PlayList.get(CurrenPlayListIndex).Path);
                //if(RealMediaPlayer==null)  //Log.i("Eric","Eric 2023.05.18 Change to :null");
               // else  //Log.i("Eric","Eric 2023.05.18 Change to :not null");
                try {
                    NextPause=false;

                 //   Log.i("Eric","2023.06.06 AAA");
                    RealMediaPlayer.reset();
                 RealMediaPlayer.setDataSource(PlayList.get(CurrenPlayListIndex).Path);
               //     Log.i("Eric","2023.06.06 CCC");

               //     Log.i("Eric","2023.05.17 Player date:"+PlayList.get(CurrenPlayListIndex).Path);

                    //Log.i("Eric","2023.05.17 preparesync A in !!!!");
                    //RealMediaPlayer.prepareAsync();
                    RealMediaPlayer.prepare();


                    //Log.i("Eric","2023.05.05  A");
                    TimeBarhandler.sendEmptyMessage(UPDATE_TIME);

                    long now = SystemClock.uptimeMillis();
                    long next=0;
                    if(!PlayFromOutSide) {
                         next = now + (3000);
                    }
                    else  next = now + (100);

                 //   if(!PlayFromOutSide) {
                        Controlhandler.postAtTime(Crunable, next);
                        PlayPauseButton.requestFocus();
                  //  }
                    //Log.i("Eric","Eric 05.25 Repeat  :"+RepeatMode);


/*
                    if(imageView2.getVisibility()==View.VISIBLE) {
                        //Log.i("Eric", "Eric 05.19 v2  :" + imageView2.getVisibility());
                        //Log.i("Eric", "Eric 05.19 v3 :" +  VideoCover);


                        if(PlayFromOutSide){
                            //Log.i("Eric", "Eric 05.19 v3  :" +  VideoCover);
                            imageView2.setVisibility(View.INVISIBLE);

                         //   RealMediaPlayer.setSurface(new Surface(mActivity.textureView));

                        }
                    }
*/
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else   if(CurrenMediaType==3)  //audio
            {
                CurrentDirection = 0;
                CurrentPlaySpeed = 1;

                //Log.i("Eric","2023.05.25  onClick");
                if(textureView==null)
                {
                    //Log.i("Eric","2023.05.25  text null");
                    textureView=(TextureView) findViewById(R.id.textureView);
                    //    textureView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    //Log.i("Eric","2023.05.27 onCreate setSurfaceTextureListene ");
                    textureView.setSurfaceTextureListener(f);
                    //  RealMediaPlayer.setSurface(new Surface(textureView.getSurfaceTexture()));

                }


                //    videoUI.setVisibility(View.VISIBLE);
                //    VideoCover=false;
                PlayListUI.setVisibility(View.INVISIBLE);
                PlayControlUI_A.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);

                isPlaying=true;
                AudioUI.setVisibility(View.VISIBLE);
                if(!PlayFromOutSide) {
                    PlayControlUI.setVisibility(View.VISIBLE);

                    MiniListUI.setVisibility(View.INVISIBLE);
                    PlayPauseButton.requestFocus();
                }
                InAudioViewUI = true;





                time2.setText(PlayList.get(0).Timetext);
                timeBar.setMax((int)PlayList.get(0).timeInMillisec/1000);
                timeBar.setProgress(0);
                time1.setText("00:00.00");

                setSpeed();
                SpeedI.setVisibility(View.INVISIBLE);

                CurrenPlayListIndex=0;
                //RealMediaPlayer.setDataSource(Uri.parse(PlayList.get(0).Path));

                //2023.05.05
                checkAudioCover(PlayList.get(CurrenPlayListIndex));

                //Log.i("Eric","Change to :"+PlayList.get(CurrenPlayListIndex));
                try {
                    NextPause=false;
                    RealMediaPlayer.reset();
                    RealMediaPlayer.setDataSource(PlayList.get(CurrenPlayListIndex).Path);
                    //Log.i("Eric","2023.05.10 Player date:"+PlayList.get(CurrenPlayListIndex).Path);

                    //Log.i("Eric","2023.05.05 preparesync A in !!!!");
                    //RealMediaPlayer.prepareAsync();
                    RealMediaPlayer.prepare();


                    //Log.i("Eric","2023.05.05  A");
                    TimeBarhandler.sendEmptyMessage(UPDATE_TIME);
                    long now = SystemClock.uptimeMillis();
                    long next = now + (3000);


                    if(!PlayFromOutSide) {
                        Controlhandler.postAtTime(Crunable, next);
                        PlayPauseButton.requestFocus();
                    }
                    //Log.i("Eric","Eric 05.05 Repeat  :"+RepeatMode);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else  //picture
            {
                //Log.i("Eric","2023.05.25 INTO Picture");
                PlayListUI.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);


                SmallPlayList = PlayList;
                adapter3 = new PlayListBaseAdapter(SmallPlayList, inflater4);
                smallplaylist_A.setAdapter(adapter3);
                //2023.04.20

                CurrentPlaySpeed = 1;
                CurrentDirection=0;

                CurrenPlayListIndex = 0;

                setSpeed();
                time2_A.setText(PlayList.get(0).Timetext);
                timeBar_A.setMax((int) 5);
                timeBar_A.setProgress(0);
                time1_A.setText("00:00.00");
                PictureSec = 0;

//04.20
                if(!PlayFromOutSide) {
                    PlayControlUI_A.setVisibility(View.VISIBLE);
                    PlayControlUI.setVisibility(View.INVISIBLE);
                    videoUI.setVisibility(View.INVISIBLE);
                    AudioUI.setVisibility(View.INVISIBLE);
                    MiniListUI_A.setVisibility(View.INVISIBLE);
                }
                long now = SystemClock.uptimeMillis();
                long next = now + (3000);
                //  long next = now + (5000);


                Controlhandler.postAtTime(Crunable, next);



                isPlaying = true;



                //2023.03.24

                InImageViewUI=true;
                CurrenPlayListIndex = 0;
                //2023.06.08 A  playMedia
              //  getPngDimensions(PlayList.get(0).Path);
                GlideSHowPiccture(PlayList.get(0).Path);





                long nowA = SystemClock.uptimeMillis();
                long nextA = nowA + (1000);

                picturehandler.postAtTime(Prunable, nextA);
                if(!PlayFromOutSide) {
                    PlayPauseButton_A.requestFocus();
                }
            }
        }
        }
        catch (Exception  e)
        {
           Log.i("VS Player","Error e:"+e.toString());

        }

    }


    public void GlideSHowPiccture(String path)
    {

        getPngDimensions(path);


        // Glide.with(this).load("imageurl").into(imageView);

        if(CurrentPictureW>=CurrentDeviceW||CurrentPictureH>=CurrentDeviceH) {
            Glide.with(this)
                    .load(path)  // 替换为您的 PNG 图片资源
                    .override(1920, 1080)  // 设置目标尺寸为 1920x1080
                    .centerInside()

                    .into(imageView);
        }
        else if (CurrentPictureW<CurrentDeviceW||CurrentPictureH<CurrentDeviceH)
        {
            Glide.with(this)
                    .load(path)  // 替换为您的 PNG 图片资源
                    .override(CurrentPictureW, CurrentPictureH)  // 设置目标尺寸为 1920x1080
                    .fitCenter()
                    .into(imageView);
        }
    }


    //2023.06.08
    public void getPngDimensions(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置为 true，仅解析图片的边界信息

        BitmapFactory.decodeFile(filePath, options); // 解析图片，但不加载像素数据

        //int width = options.outWidth; // 获取图片的宽度
        //int height = options.outHeight; // 获取图片的高度
        CurrentPictureW=options.outWidth;
        CurrentPictureH=options.outHeight;

        //Log.i("VSPLAYER","Eric 2023.06.08 this:"+filePath);
        //Log.i("VSPLAYER","Eric 2023.06.08 width:"+CurrentPictureW);
        //Log.i("VSPLAYER","Eric 2023.06.08 height:"+CurrentPictureH);

        // 在这里可以获取到 PNG 图片的真实宽度和高度
    }


    //2023/05/16

    //2023.05.17
    void OutSidePlayList(MediaFile currentFile) {

      //  Log.i("Eric", "Eric 11:" + currentFile.Path);
      //  Log.i("Eric", "Eric 12:" + currentFile.ParrentPath);

      //  Log.i("Eric", "Eric A:");

        File playitemfile = new File(currentFile.Path);
      //  Log.i("Eric", "Eric A2:" + playitemfile.getPath());

        long time = MediaTimeCheck(playitemfile, currentFile.Filetype);

      //  Log.i("Eric", "Eric B2:" + time);

       // Log.i("Eric", "Eric B:");

        /*
        if(time==-99)
        {
            //Log.i("Eric", "2023/05/11 f:" +MediaTimeCheck2(playitemfile));
            time=   MediaTimeCheck2(playitemfile);
            Log.i("Eric", "Eric ff:" +time);

        }
          */

        if (time == -99) {
            time=5000;
        }
        currentFile.timeInMillisec = time;

        currentFile.Timetext = timePrase(time);
        Log.i("Eric", "Eric C:");
        if (currentFile.timeInMillisec == -1 ) {

            currentFile.Timetext = "00:00:05";
        }
      //  //Log.i("Eric", "playitem " + playitem.Name + ":" + playitem.timeInMillisec + ":" + playitem.Timetext);
        // checkAudioCover(playitem);

       // Log.i("Eric", "Eric D:");
        if (currentFile.timeInMillisec != -99) {
            Log.i("Eric", "2023.05.25 !!!! add:" +currentFile.Name);
            PlayList.add(currentFile);
        }

      //    PlayList.add(currentFile);
      //  Log.i("Eric", "Eric E:");
        PlayFromOutSide=true;
     //   Log.i("Eric", "Eric F:");
        PlayMedia();


    }





    Boolean handleBackGroud=false;

    private Handler mCalHandler = new Handler(Looper.getMainLooper());




    private final Runnable mTicker = new Runnable() {
        public void run() {
            long now = SystemClock.uptimeMillis();
            long next = now + (100 - now % 100);

            mCalHandler.postAtTime(mTicker, next);
            //Log.e("Eric", now + "");
            if(videoView.isPlaying()&&videoView.getDuration()>0)
            {
                // Log.e("Eric", now + "");
                if (videoView.getDuration()-videoView.getCurrentPosition()<=200 )
                {
                    //v1.pause();
                    videoView.setVisibility(View.VISIBLE);



                    if (PlayList.size()!=1) {


                        Drawable drawable = new BitmapDrawable(getResources(), PlayList.get(CurrenPlayListIndex).LastPicture);
                        videoView.setBackground(drawable);
                    }

                    //v1.resume();
                    //  Log.e("Eric", now + ": 背景出現 CurrentVideo"+CurrentVideo);



                }
                else if(videoView.getCurrentPosition()>1000 && videoView.getDuration()-videoView.getCurrentPosition()>=100
                ) {
                    // Log.e("Eric", now + ": 消失: " + v1.getCurrentPosition());



                    if (PlayList.size()!=1) {
                        videoView.setBackground(null);
                        videoView.setBackgroundColor(Color.TRANSPARENT);
                    }


                }

            }

        }
    };





    //2023.03.01
    void   handleMiniPlaylistIcon(){

try {
    Boolean finded = false;


    if (CurrenPlayListIndex >= smallplaylist.getLastVisiblePosition()) {
        //    //Log.i("Eric","Eric 2023.05.03 handle  index "+CurrenPlayListIndex);


        if (CurrenPlayListIndex + 5 < smallplaylist.getCount() - 1)
            smallplaylist.smoothScrollToPosition(CurrenPlayListIndex + 5);
        else
            smallplaylist.smoothScrollToPosition(smallplaylist.getCount() - 1);
    } else if (CurrenPlayListIndex <= smallplaylist.getFirstVisiblePosition()) {
        //    //Log.i("Eric","Eric 2023.05.03 handle  index "+CurrenPlayListIndex);
        if (CurrenPlayListIndex - 3 > 0)
            smallplaylist.smoothScrollToPosition(CurrenPlayListIndex - 3);
        else
            smallplaylist.smoothScrollToPosition(0);
    }


    //   do {
    //   for(int i=0;i<smallplaylist.getCount();i++) {
    for (int i = 0; i < smallplaylist.getCount(); i++) {
        //Log.i("Eric", "Eric 2023.05.03 Enter" + smallplaylist.getFirstVisiblePosition());
        //Log.i("Eric", "Eric 2023.05.03 Enter " + smallplaylist.getLastVisiblePosition());


        //   //Log.i("Eric", "Eric 2023.05.03 Enter " + CurrenPlayListIndex);


        View now;
        now = smallplaylist.getChildAt(i);

        if (now != null) {
            TextView now2 = now.findViewById(R.id.playrowName);
            TextView number = now.findViewById(R.id.playrowID);
            ImageView play = now.findViewById(R.id.playicon);
            //Log.i("Eric", "2023.05.03 PlayName" + PlayList.get(CurrenPlayListIndex).Name);
            //Log.i("Eric", "2023.05.03 i:"+i+" name" + now2.getText());

            now2.setSelected(false);

            if (now2.getText().toString().contains(PlayList.get(CurrenPlayListIndex).Name)) {
                //   if (i == CurrenPlayListIndex) {
                finded = true;
                now2.setSelected(true);
                number.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            } else {
                now2.setSelected(false);
                number.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
            }
        }

    }
    // }while(CurrenPlayListIndex<smallplaylist.getFirstVisiblePosition() ||CurrenPlayListIndex>smallplaylist.getFirstVisiblePosition());

}
catch (Exception e)
{
    Log.i("VsPlayer","2023.07.05 Error :"+e.toString());
}

    }


    void   handleMiniPlaylistIcon3(){

  try {
      Boolean finded = false;

      if (CurrenPlayListIndex >= smallplaylist.getLastVisiblePosition()) {
          //    //Log.i("Eric","Eric 2023.05.03 handle  index "+CurrenPlayListIndex);

          if (CurrenPlayListIndex + 5 < smallplaylist.getCount() - 1)
              smallplaylist.smoothScrollToPosition(CurrenPlayListIndex + 5);
          else
              smallplaylist.smoothScrollToPosition(smallplaylist.getCount() - 1);
      } else if (CurrenPlayListIndex <= smallplaylist.getFirstVisiblePosition()) {
          //  //Log.i("Eric","Eric 2023.05.03 handle  index "+CurrenPlayListIndex);

          if (CurrenPlayListIndex - 3 > 0)
              smallplaylist.smoothScrollToPosition(CurrenPlayListIndex - 3);
          else
              smallplaylist.smoothScrollToPosition(0);
      }


      for (int i = 0; i < smallplaylist.getCount(); i++) {
          //   //Log.i("Eric", "Eric 2023.05.03 Enter" + smallplaylist.getFirstVisiblePosition());
          //   //Log.i("Eric", "Eric 2023.05.03 Enter " + smallplaylist.getLastVisiblePosition());
          //   //Log.i("Eric", "Eric 2023.05.03 Enter " + CurrenPlayListIndex);


          View now3;
          now3 = smallplaylist_A.getChildAt(i);

          if (now3 != null) {
              TextView now2 = now3.findViewById(R.id.playrowName);
              TextView number = now3.findViewById(R.id.playrowID);
              ImageView play = now3.findViewById(R.id.playicon);
              //Log.i("Eric", "2023.05.03 PlayName" + PlayList.get(CurrenPlayListIndex).Name);
              //Log.i("Eric", "2023.05.03 i:"+i+" name" + now2.getText());

              now2.setSelected(false);

              if (now2.getText().toString().contains(PlayList.get(CurrenPlayListIndex).Name)) {
                  //   if (i == CurrenPlayListIndex) {
                  finded = true;
                  now2.setSelected(true);
                  number.setVisibility(View.GONE);
                  play.setVisibility(View.VISIBLE);
              } else {
                  now2.setSelected(false);
                  number.setVisibility(View.VISIBLE);
                  play.setVisibility(View.GONE);
              }
          }

      }
      // }while(CurrenPlayListIndex<smallplaylist.getFirstVisiblePosition() ||CurrenPlayListIndex>smallplaylist.getFirstVisiblePosition());

  }
  catch (Exception e)
  {
      Log.i("VsPlayer","2023.07.05 Error :"+e.toString());
  }
    }

    void handleMiniPlaylistIcon2(){
        for(int i=0;i<PlayList.size();i++) {
            View now3;
            now3 = smallplaylist_A.getChildAt(i);

            TextView now4 = now3.findViewById(R.id.playrowName);
            TextView number2 = now3.findViewById(R.id.playrowID);
            ImageView play2 = now3.findViewById(R.id.playicon);
            if(i==CurrenPlayListIndex) {
                now4.setSelected(true);
                number2.setVisibility(View.GONE);
                play2.setVisibility(View.VISIBLE);
            }
            else
            {
                now4.setSelected(false);
                number2.setVisibility(View.VISIBLE);
                play2.setVisibility(View.GONE);
            }
        }

    }


    //2023.02.23
    void setRandon(){

        if(RandomMode==0) {
            RandomMode = 1;


            if(PlayList.size()>0) {
                //    OPlayList = PlayList;
                OPlayList.clear();
                for(MediaFile e:PlayList)
                {
                    OPlayList.add(e);
                }
                String OldFilePath="";
           //     if(videoView.isPlaying()){
                    OldFilePath=PlayList.get(CurrenPlayListIndex).Path;
           //     }


                Collections.shuffle(PlayList);
                SmallPlayList = PlayList;

if(InVideoViewUI||InAudioViewUI) {
    RandonButton.setBackgroundResource(R.drawable.pb1_choice_hover);
    adapter3 = new PlayListBaseAdapter(SmallPlayList, inflater3);
    smallplaylist.setAdapter(adapter3);



}
else if(InImageViewUI) {
    RandonButton_A.setBackgroundResource(R.drawable.pb1_choice_hover);
                    adapter3 = new PlayListBaseAdapter(SmallPlayList, inflater4);
                    smallplaylist_A.setAdapter(adapter3);
                }
    adapter2 = new PlayListBaseAdapter(PlayList, inflater2);
    playlist.setAdapter(adapter2);

                playlist.post(new Runnable() {

                    @Override
                    public void run() {
                        //         //Log.i("Eric","2023.06.09 post");

                        playlist.smoothScrollToPosition(playlist.getCount() - 1);
                    }
                });

    //2023.06.08 A

                if(OldFilePath!="") {
                    for (int i = 0; i < PlayList.size(); i++) {
                        if (PlayList.get(i).Path.equals(OldFilePath)){
                            CurrenPlayListIndex=i;
                        }
                    }
                }


            }
        }
        else {

            RandomMode=0;
            RandonButton.setBackgroundResource(R.drawable.pb1_hover);
            if(PlayList.size()>0)
            {

                String OldFilePath="";
         //       if(videoView.isPlaying()){
                    OldFilePath=PlayList.get(CurrenPlayListIndex).Path;

          //      }


                PlayList.clear();
                for(MediaFile e:OPlayList)
                {
                    PlayList.add(e);
                }
                //PlayList=OPlayList;
                // Collections.shuffle(PlayList);
                SmallPlayList = PlayList;


                if(InVideoViewUI||InAudioViewUI) {
                    RandonButton.setBackgroundResource(R.drawable.pb1_hover);
                    adapter3 = new PlayListBaseAdapter(SmallPlayList, inflater3);
                    //smallplaylist.removeAllViews();
                    smallplaylist.setAdapter(adapter3);
                    //smallplaylist.notifyAll();
                }

            else if(InImageViewUI) {
                    RandonButton_A.setBackgroundResource(R.drawable.pb1_hover);
                adapter3 = new PlayListBaseAdapter(SmallPlayList, inflater4);
                smallplaylist_A.setAdapter(adapter3);
            }


                adapter2 = new PlayListBaseAdapter(PlayList, inflater2);
                playlist.setAdapter(adapter2);

                playlist.post(new Runnable() {

                    @Override
                    public void run() {
                        //         //Log.i("Eric","2023.06.09 post");

                        playlist.smoothScrollToPosition(playlist.getCount() - 1);
                    }
                });

                //2023.06.08 A
                if(OldFilePath!="") {
                    for (int i = 0; i < PlayList.size(); i++) {
                        if (PlayList.get(i).Path.equals(OldFilePath)){
                            CurrenPlayListIndex=i;
                        }
                    }
                }


            }

        }


    }


    void setMediaButton()
    {

        PlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

           //     //Log.i("Eric","PlayPasue");
           //     //Log.i("Eric","Eric 2023.05.02:"+isPlaying);


                if(RealMediaPlayer.isPlaying())
                {//2023.03.01
                    //2023.04.14
                    if(CurrentDirection==0) {
                        RealMediaPlayer.pause();
                        TimeBarhandler.removeCallbacksAndMessages(null);
                        PlayPauseButton.setBackgroundResource(R.drawable.playbt_hover_2);
                        isPlaying = false;
                    }
                    else{
                        PlaybackParams nPlaybackParams = RealMediaPlayer.getPlaybackParams();
                        CurrentPlaySpeed = 1;
                        CurrentDirection=0;


                        SpeedI.setVisibility(View.INVISIBLE);

                        //String speedtxt = Integer.toString(CurrentPlaySpeed) + "X";

                        setSpeed();
                        RealMediaPlayer.pause();
                        nPlaybackParams.setSpeed(CurrentPlaySpeed);
                        RealMediaPlayer.setPlaybackParams(nPlaybackParams);
                        RealMediaPlayer.start();
                        TimeBarhandler.sendEmptyMessage(UPDATE_TIME);
                    }
                }
                else
                     {

                    RealMediaPlayer.start();
                    TimeBarhandler.sendEmptyMessage(UPDATE_TIME);

                    PlayPauseButton.setBackgroundResource(R.drawable.pause_hover_2);
                    isPlaying=true;
                }
                if (PlayControlUI.getVisibility() == View.INVISIBLE) {


                    PlayControlUI.setVisibility(View.VISIBLE);
                    PlayPauseButton.requestFocus();
                    long now = SystemClock.uptimeMillis();
                    long next = now + (5000);

                 //   Controlhandler.postAtTime(Crunable, next);

                } else {
                    Controlhandler.removeCallbacks(Crunable);
                    long now = SystemClock.uptimeMillis();
                    long next = now + (5000);

                    Controlhandler.postAtTime(Crunable, next);

                }
            }
        });
//2023.02.22
        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (InVideoViewUI||InAudioViewUI) {
                    isPlaying = false;
                    NextPause=true;
        //            //Log.i("Eric", "2022.05.022" +
        //                    "7 NExt:"+CurrenPlayListIndex);


                     //2023.05.02
                    //if (videoView.isPlaying()) isPlaying = true;
                    if(RealMediaPlayer.isPlaying()) {
                        isPlaying = true;
                        NextPause=false;
                    }
                    {
                        if (PlayList.size() > 1) {
                            CurrenPlayListIndex++;
                            if (CurrenPlayListIndex > PlayList.size() - 1) CurrenPlayListIndex = 0;
                        }
                        if(HasData)HasData=false;
                        RealMediaPlayer.stop();

                        RealMediaPlayer.reset();
                        try {
                            RealMediaPlayer.setDataSource(PlayList.get(CurrenPlayListIndex).Path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            RealMediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        //   videoView.setVideoURI(Uri.parse(PlayList.get(CurrenPlayListIndex).Path));
                        time2.setText(PlayList.get(CurrenPlayListIndex).Timetext);
                        if(CurrenMediaType==2)
                        InVideoViewUI = true;
                        else if(CurrenMediaType==3)
                        InAudioViewUI=true;

                        //   InViewViewUI = true;
                        //  CurrenPlayListIndex=0;
                        timeBar.setMax((int) PlayList.get(CurrenPlayListIndex).timeInMillisec / 1000);
                        time1.setText("00:00.00");
                        TimeBarhandler.sendEmptyMessage(UPDATE_TIME);

                        if(CurrenMediaType==3 && !(PlayList.get(CurrenPlayListIndex).Name.endsWith("mid")) ) {
                            //  setMp3background(PlayList.get(CurrenPlayListIndex).Path);
                            checkAudioCover(PlayList.get(CurrenPlayListIndex));
                        }

                    //    videoView.start();
                    //    handleBackGroud=true;
                            if(MiniListUI.getVisibility()==View.VISIBLE)
                        {
                            handleMiniPlaylistIcon();

                        }
                        //if (!isPlaying)
                         //   videoView.pause();

                    }

                    if (PlayControlUI.getVisibility() == View.INVISIBLE) {


                        PlayControlUI.setVisibility(View.VISIBLE);
                        PlayPauseButton.requestFocus();
                        long now = SystemClock.uptimeMillis();
                        long next = now + (5000);

                        Controlhandler.postAtTime(Crunable, next);

                    } else {
                        Controlhandler.removeCallbacks(Crunable);
                        long now = SystemClock.uptimeMillis();
                        long next = now + (5000);

                       Controlhandler.postAtTime(Crunable, next);

                    }
                }
            }
        });

        LastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(InVideoViewUI||InAudioViewUI) {
                    NextPause=true;

                    if(RealMediaPlayer.isPlaying()) {
                        isPlaying = true;
                        NextPause=false;
                    }
                    {
                        if(PlayList.size()>1)
                        {
                            CurrenPlayListIndex--;
                            if(CurrenPlayListIndex<0)CurrenPlayListIndex=PlayList.size()-1;
                        }
                        if(HasData)HasData=false;
                        RealMediaPlayer.stop();

                        RealMediaPlayer.reset();
                        try {
                            RealMediaPlayer.setDataSource(PlayList.get(CurrenPlayListIndex).Path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            RealMediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

              time2.setText(PlayList.get(CurrenPlayListIndex).Timetext);

                        if(CurrenMediaType==2)
                            InVideoViewUI = true;
                        else if(CurrenMediaType==3)
                            InAudioViewUI=true;

                        timeBar.setMax((int) PlayList.get(CurrenPlayListIndex).timeInMillisec / 1000);
                        time1.setText("00:00.00");
                        TimeBarhandler.sendEmptyMessage(UPDATE_TIME);


                        if(CurrenMediaType==3 && !(PlayList.get(CurrenPlayListIndex).Name.endsWith("mid")) ) {
                            checkAudioCover(PlayList.get(CurrenPlayListIndex));
                        }

                      //  videoView.start();
                     //   handleBackGroud=true;
                        if(MiniListUI.getVisibility()==View.VISIBLE)
                        {
                            handleMiniPlaylistIcon();

                        }
                     //   if(!isPlaying)
                     //       videoView.pause();

                    }
                    if (PlayControlUI.getVisibility() == View.INVISIBLE) {


                        PlayControlUI.setVisibility(View.VISIBLE);
                        PlayPauseButton.requestFocus();
                        long now = SystemClock.uptimeMillis();
                        long next = now + (5000);

                       Controlhandler.postAtTime(Crunable, next);

                    } else {
                        Controlhandler.removeCallbacks(Crunable);
                        long now = SystemClock.uptimeMillis();
                        long next = now + (5000);

                     Controlhandler.postAtTime(Crunable, next);

                    }
                }
            }
        });


        PlayPauseButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {

                    if(isPlaying)
                    PlayPauseButton.setBackgroundResource(R.drawable.pause_hover_2);
                    else
                        PlayPauseButton.setBackgroundResource(R.drawable.playbt_hover_2);


                }
                else {

                    if(isPlaying)
                    PlayPauseButton.setBackgroundResource(R.drawable.pause_2);
                   else
                        PlayPauseButton.setBackgroundResource(R.drawable.playbt_2);

                }
            }
        });

        NextButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    NextButton.setBackgroundResource(R.drawable.nextbt_hover_2);
                }
                else {
                    NextButton.setBackgroundResource(R.drawable.nextbt_2);
                }
            }
        });



//2023.04.13
        LastButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    LastButton.setBackgroundResource(R.drawable.lastbt_hover_2);
                }
                else {
                    LastButton.setBackgroundResource(R.drawable.lastbt_2);
                }
            }
        });

        //2023.04.13
        FastForwardButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    FastForwardButton.setBackgroundResource(R.drawable.ffbt_hover);
                }
                else {
                    FastForwardButton.setBackgroundResource(R.drawable.ffbt);
                }
            }
        });

        FastForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InVideoViewUI) {
                    if (RealMediaPlayer.isPlaying()) {
                        // try {
                        PlaybackParams nPlaybackParams = new PlaybackParams() ;
                        if (CurrentDirection == 0) {
                            if ((CurrentPlaySpeed < 8 && Build.VERSION.SDK_INT>23) ||(Build.MODEL.contains("M2e")&&CurrentPlaySpeed < 2)
                                ||(Build.MODEL.contains("M2w")&&CurrentPlaySpeed < 2)||(Build.MODEL.contains("X300")&&CurrentPlaySpeed < 2)||
                                    (CurrentPlaySpeed < 4 && Build.VERSION.SDK_INT<=23)) {
                                CurrentDirection = 1;
                                CurrentPlaySpeed = CurrentPlaySpeed * 2;
                               // Speed.setVisibility(View.VISIBLE);
                                SpeedI.setVisibility(View.VISIBLE);
                                setSpeed();
                            } else if (CurrentPlaySpeed < 32) {
                                CurrentDirection = 1;
                                CurrentPlaySpeed = CurrentPlaySpeed + 1;
                                setSpeed();
                            } else {
                                CurrentDirection = 0;
                                CurrentPlaySpeed = 0;
                            }
                            RealMediaPlayer.pause();
                            nPlaybackParams.setSpeed(CurrentPlaySpeed);
                            RealMediaPlayer.setPlaybackParams(nPlaybackParams);
                            RealMediaPlayer.start();
                        } else if (CurrentDirection == 1) {
                            if (CurrentPlaySpeed < 4 && (! Build.MODEL.toLowerCase().contains("m2e"))
                                   &&(! Build.MODEL.toLowerCase().contains("m2w"))
                                    &&(! Build.MODEL.toLowerCase().contains("x300"))
                                    && (!Build.MODEL.toLowerCase().contains("p1") )
                                ) {
                                CurrentDirection = 1;
                                CurrentPlaySpeed = CurrentPlaySpeed * 2;
                                String speedtxt = Integer.toString(CurrentPlaySpeed) + "X";
                                SpeedI.setVisibility(View.VISIBLE);

                               //06.02

                                setSpeed();

                              //Log.i("Eric","2023.06.08 AAAAAA:"+CurrentPlaySpeed);
                                RealMediaPlayer.pause();
                                nPlaybackParams.setSpeed(CurrentPlaySpeed);
                                RealMediaPlayer.setPlaybackParams(nPlaybackParams);
                                RealMediaPlayer.start();
                            } else if (CurrentPlaySpeed < 32) {
                                CurrentDirection = 1;

                                //Log.i("Eric","2023.06.0B BBBBB:"+CurrentPlaySpeed);

                                //Log.i("Eric","2023.06.09 !!!! "+CurrentPlaySpeed);
                                CurrentPlaySpeed = CurrentPlaySpeed * 2;
                              //  Speed.setVisibility(View.VISIBLE);

                                //Log.i("Eric","2023.06.09 !!!! "+CurrentPlaySpeed);

                                setSpeed();

                                FF2handler.removeCallbacksAndMessages(null);
                                                         FF2handler.sendEmptyMessage(MOVE);
                            }


                        } else if (CurrentDirection == 2) {

                            CurrentPlaySpeed = 1;
                            CurrentDirection = 0;
                            //  Speed.setVisibility(View.INVISIBLE);

                            setSpeed();
                            videoView.pause();
                            FF2handler.removeCallbacksAndMessages(null);
                            nPlaybackParams.setSpeed(CurrentPlaySpeed);
                            RealMediaPlayer.setPlaybackParams(nPlaybackParams);
                            videoView.start();

                        }
                        //}
                        //catch (Exception e)
                        //{
                        //  //Log.i("Eric","2023.04.14 ee"+e.toString());
                        // }
                    }
                }
                else if(InAudioViewUI) {
                    if (RealMediaPlayer.isPlaying()) {
                        // try {

                        PlaybackParams nPlaybackParams = new PlaybackParams() ;
                        if (CurrentDirection == 0) {
                            if (CurrentPlaySpeed < 8) {
                                CurrentDirection = 1;
                                CurrentPlaySpeed = CurrentPlaySpeed * 2;


                                setSpeed();
                                FF2handler.removeCallbacksAndMessages(null);
                                FF2handler.sendEmptyMessage(MOVE);
                            }
                        } else if (CurrentDirection == 1) {

                                if (CurrentPlaySpeed < 32) {
                                CurrentDirection = 1;
                                CurrentPlaySpeed = CurrentPlaySpeed * 2;

                                    setSpeed();
                                    FF2handler.removeCallbacksAndMessages(null);
                                   FF2handler.sendEmptyMessage(MOVE);
                            }


                        } else if (CurrentDirection == 2) {

                            CurrentPlaySpeed = 1;
                            CurrentDirection = 0;
                            //  Speed.setVisibility(View.INVISIBLE);

                            setSpeed();
                            //RealMediaPlayer.pause();
                            RealMediaPlayer.start();
                            FF2handler.removeCallbacksAndMessages(null);

                        }
                        //}
                        //catch (Exception e)
                        //{
                        //  //Log.i("Eric","2023.04.14 ee"+e.toString());
                        // }
                    }

                }
            }
        });

        FastBackwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(RealMediaPlayer.isPlaying()) {

                    PlaybackParams nPlaybackParams = RealMediaPlayer.getPlaybackParams();

                    if(CurrentDirection==1) {
                        CurrentPlaySpeed = 1;
                        CurrentDirection=0;
                        // Speed.setVisibility(View.INVISIBLE);

                        setSpeed();
                        RealMediaPlayer.pause();
                        nPlaybackParams.setSpeed(CurrentPlaySpeed);
                        RealMediaPlayer.setPlaybackParams(nPlaybackParams);
                        RealMediaPlayer.start();
                    }
                    else if(CurrentDirection==0){

                        CurrentPlaySpeed=2;
                        CurrentDirection=2;
                        FF2handler.removeCallbacksAndMessages(null);
                        setSpeed();
                        FF2handler.sendEmptyMessage(MOVE);

                    }
                    else if(CurrentDirection==2){

                        if (CurrentPlaySpeed<32)
                            CurrentPlaySpeed=CurrentPlaySpeed*2;
                        else CurrentPlaySpeed=2;

                        CurrentDirection=2;
                        //   Speed.setVisibility(View.VISIBLE);

                        FF2handler.removeCallbacksAndMessages(null);
                        setSpeed();
                        FF2handler.sendEmptyMessage(MOVE);

                    }

                }
            }
        });



        FastBackwardButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    //Log.i("Eric","has focus");
                    FastBackwardButton.setBackgroundResource(R.drawable.fbbt_hover);

                }
                else {
                    //Log.i("Eric","has no  focus");
                    FastBackwardButton.setBackgroundResource(R.drawable.fbbt);
                }
            }
        });

        ListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.i("Eric", "2.23 ListClick:" + MiniListUI.getVisibility());

                if (MiniListUI.getVisibility() == View.VISIBLE) {
                    MiniListUI.setVisibility(View.INVISIBLE);
                } else {
                    MiniListUI.setVisibility(View.VISIBLE);
                      handleMiniPlaylistIcon();
                    RandonButton.requestFocus();
                }

            }
        });

        ListButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ListButton.setBackgroundResource(R.drawable.minilistbt_hover);

                    handleMiniPlaylistIcon();
                } else {
                    ListButton.setBackgroundResource(R.drawable.minilistbt);
                    handleMiniPlaylistIcon();
                }
            }
        });

        // RandonButton,RAllButton,        ROneButton;
//2023.05.05
        RandonButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (RandomMode == 1)
                        view.setBackgroundResource(R.drawable.pb1_choice_hover);
                    else
                    view.setBackgroundResource(R.drawable.pb1_hover);
               //    handleMiniPlaylistIcon();

                } else {
                    if (RandomMode == 1)
                        view.setBackgroundResource(R.drawable.pb1_choice);
                    else
                    view.setBackgroundResource(R.drawable.pb1);
               //    handleMiniPlaylistIcon();
                }


            }
        });

        RAllButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (RepeatMode == 1) view.setBackgroundResource(R.drawable.pb2_choice_hover);
                    else
                    view.setBackgroundResource(R.drawable.pb2_hover);
                //     handleMiniPlaylistIcon();

                } else {
                    if (RepeatMode == 1) view.setBackgroundResource(R.drawable.pb2_choice);
                    else
                    view.setBackgroundResource(R.drawable.pb2);
                //    handleMiniPlaylistIcon();
                }


            }
        });


        ROneButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (RepeatMode == 2) view.setBackgroundResource(R.drawable.pb3_choice_hover);
                    else view.setBackgroundResource(R.drawable.pb3_hover);
                 //   handleMiniPlaylistIcon();
                } else {
                    if (RepeatMode == 2) view.setBackgroundResource(R.drawable.pb3_choice);
                    else
                    view.setBackgroundResource(R.drawable.pb3);
               //     handleMiniPlaylistIcon();
                }

            }
        });

//2023.02.23

        RAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RepeatMode != 1) {
                    RepeatMode = 1;

                    view.setBackgroundResource(R.drawable.pb2_choice_hover);
                    ROneButton.setBackgroundResource(R.drawable.pb3);
                } else {
                    RepeatMode = 0;

                    view.setBackgroundResource(R.drawable.pb2_hover);
                    ROneButton.setBackgroundResource(R.drawable.pb3);

                }

            }
        });

        ROneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RepeatMode != 2) {
                    RepeatMode = 2;

                    view.setBackgroundResource(R.drawable.pb3_choice_hover);
                    RAllButton.setBackgroundResource(R.drawable.pb2);
                } else {
                    RepeatMode = 0;

                    view.setBackgroundResource(R.drawable.pb3_hover);
                    RAllButton.setBackgroundResource(R.drawable.pb2);

                }

            }
        });

        RandonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRandon();
               // handleMiniPlaylistIcon();
                FF2handler.sendEmptyMessageDelayed(2,1000);

            }
        });


    }


    void setMediaButton2() {

        PlayPauseButton_A.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Log.i("Eric", "PlayPasue");


                        if (isPlaying) {//2023.03.01
                            //2023.04.14

                                //  videoView.pause();
                            PlayPauseButton_A.setBackgroundResource(R.drawable.playbt_hover_2);
                                isPlaying = false;
                                //long now = SystemClock.uptimeMillis();
                                //long next = now + (1);

                                time1_A.setText("00:00.0" + Integer.toString(PictureSec));

                                picturehandler.removeCallbacks(mActivity.Prunable);

                        } else {
                            //  videoView.start();

                            PlayPauseButton_A.setBackgroundResource(R.drawable.pause_hover_2);
                            isPlaying = true;
                            long now = SystemClock.uptimeMillis();
                            long next = now + (1000);

                            time1_A.setText("00:00.0" + Integer.toString(PictureSec));

                            picturehandler.postAtTime(Prunable, next);

                        }


                    }
                }

        );

        PlayPauseButton_A.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    //Log.i("Eric", "has focus");

                    //2023.03.01
                    if(!isPlaying)
                        PlayPauseButton_A.setBackgroundResource(R.drawable.playbt_hover_2);
                        else
                        PlayPauseButton_A.setBackgroundResource(R.drawable.pause_hover_2);


                } else {
                    //Log.i("Eric", "has no  focus");

                        if(!isPlaying)
                            PlayPauseButton_A.setBackgroundResource(R.drawable.playbt_2);
                            else
                            PlayPauseButton_A.setBackgroundResource(R.drawable.pause_2);

                }
            }
        });

        NextButton_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                    PictureSec = 0;
                    CurrenPlayListIndex++;
                    time2_A.setText(PlayList.get(0).Timetext);
                    timeBar_A.setMax((int) 5);
                    timeBar_A.setProgress(PictureSec);
                    time1_A.setText("00:00.00");
                     picturehandler.removeCallbacks(Prunable);
                    //Log.i("Eric", "2023.03.07:" + CurrenPlayListIndex);

                    if (RepeatMode == 1 && (CurrenPlayListIndex >=PlayList.size()))
                        CurrenPlayListIndex = 0;
                    else if (RepeatMode == 2) CurrenPlayListIndex--;
                    //Reapeat ALL
                    if (CurrenPlayListIndex < PlayList.size()) {
                        //   PlayListUI.setVisibility(View.INVISIBLE);
                        // imageView.setVisibility(View.VISIBLE);
                        InImageViewUI = true;

                        //2023.06.08

                        GlideSHowPiccture(PlayList.get(CurrenPlayListIndex).Path);

                        if (isPlaying) {
                            long now = SystemClock.uptimeMillis();
                            long next = now + (1000);

                            picturehandler.postAtTime(Prunable, next);
                        }
                    } else {
                        //  if(RepeatMode==1)
                        imageView.setVisibility(View.INVISIBLE);
                        videoView.setVisibility(View.INVISIBLE);
                        if (AudioUI.getVisibility() == View.VISIBLE)
                            AudioUI.setVisibility(View.INVISIBLE);
                        PlayListUI.setVisibility(View.VISIBLE);
                        PlayButton.requestFocus();
                        isPlaying = false;


                    }

            }
           });

                NextButton_A.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {
                            //Log.i("Eric", "has focus");
                            NextButton_A.setBackgroundResource(R.drawable.nextbt_hover_2);

                        } else {
                            //Log.i("Eric", "has no  focus");
                            NextButton_A.setBackgroundResource(R.drawable.nextbt_2);
                        }
                    }
                });

        LastButton_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                PictureSec = 0;
                CurrenPlayListIndex--;
                time2_A.setText(PlayList.get(0).Timetext);
                timeBar_A.setMax((int) 5);
                timeBar_A.setProgress(PictureSec);
                time1_A.setText("00:00.00");
                picturehandler.removeCallbacks(Prunable);
                //Log.i("Eric", "2023.03.07:" + CurrenPlayListIndex);

                if (RepeatMode == 1 && (CurrenPlayListIndex <0))
                    CurrenPlayListIndex = PlayList.size()-1;
                else if (RepeatMode == 2) CurrenPlayListIndex++;
                //Reapeat ALL
                if (CurrenPlayListIndex < PlayList.size()&&CurrenPlayListIndex>=0) {
                    //   PlayListUI.setVisibility(View.INVISIBLE);
                    // imageView.setVisibility(View.VISIBLE);
                    InImageViewUI = true;
                    //2023.06.08

                    GlideSHowPiccture(PlayList.get(CurrenPlayListIndex).Path);
                    if (isPlaying) {
                        long now = SystemClock.uptimeMillis();
                        long next = now + (1000);

                        picturehandler.postAtTime(Prunable, next);
                    }
                } else {
                    //  if(RepeatMode==1)
                    imageView.setVisibility(View.INVISIBLE);
                    videoView.setVisibility(View.INVISIBLE);
                    if (AudioUI.getVisibility() == View.VISIBLE)
                        AudioUI.setVisibility(View.INVISIBLE);
                    PlayListUI.setVisibility(View.VISIBLE);
                    PlayButton.requestFocus();
                    isPlaying = false;


                }

            }
        });

//2023.04.20
        LastButton_A.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    //Log.i("Eric", "has focus");
                    LastButton_A.setBackgroundResource(R.drawable.lastbt_hover_2);

                } else {
                    //Log.i("Eric", "has no  focus");
                    LastButton_A.setBackgroundResource(R.drawable.lastbt_2);
                }
            }
        });



       // CurrentPlaySpeed = 1;
       // CurrentDirection=0;
        FastForwardButton_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CurrentDirection==0){
                    CurrentDirection=1;
                    CurrentPlaySpeed=2;
                    CurrentDirection=1;

                    setSpeed();

                }
                else    if(CurrentDirection==1&&CurrentPlaySpeed<8){
                    //CurrentDirection=1;
                    CurrentPlaySpeed=CurrentPlaySpeed*2;
                    CurrentDirection=1;

                    setSpeed();

                }
                else    if(CurrentDirection==2){
                    //CurrentDirection=1;
                    CurrentPlaySpeed=1;
                    CurrentDirection=0;

                    setSpeed();

                }
            }
        });

        FastBackwardButton_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CurrentDirection==0){
                    CurrentDirection=1;
                    CurrentPlaySpeed=2;
                    CurrentDirection=2;

                    setSpeed();
                }
                else    if(CurrentDirection==2&&CurrentPlaySpeed<8){
                    //CurrentDirection=1;
                    CurrentPlaySpeed=CurrentPlaySpeed*2;
                    CurrentDirection=2;

                    setSpeed();
                }
                else    if(CurrentDirection==1){
                    //CurrentDirection=1;
                    CurrentPlaySpeed=1;
                    CurrentDirection=0;

                    setSpeed();
                }
            }
        });


        //2023.04.13
        FastForwardButton_A.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    //Log.i("Eric", "has focus");
                    FastForwardButton_A.setBackgroundResource(R.drawable.ffbt_hover);

                } else {
                    //Log.i("Eric", "has no  focus");
                    FastForwardButton_A.setBackgroundResource(R.drawable.ffbt);
                }
            }
        });


        FastBackwardButton_A.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    //Log.i("Eric", "has focus");
                    FastBackwardButton_A.setBackgroundResource(R.drawable.fbbt_hover);

                } else {
                    //Log.i("Eric", "has no  focus");
                    FastBackwardButton_A.setBackgroundResource(R.drawable.fbbt);
                }
            }
        });


        ListButton_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.i("Eric", "2.23 ListClick:" + MiniListUI.getVisibility());

                if (MiniListUI_A.getVisibility() == View.VISIBLE) {
                    MiniListUI_A.setVisibility(View.INVISIBLE);
                } else {
                    MiniListUI_A.setVisibility(View.VISIBLE);
                      handleMiniPlaylistIcon3();
                    RandonButton_A.requestFocus();
                }

            }
        });

        ListButton_A.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ListButton_A.setBackgroundResource(R.drawable.minilistbt_hover);
                       handleMiniPlaylistIcon3();
                } else {
                    ListButton_A.setBackgroundResource(R.drawable.minilistbt);
                            handleMiniPlaylistIcon3();
                }
            }
        });

        RandonButton_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRandon();
            }
        });

        RandonButton_A.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (RandomMode == 1)
                        view.setBackgroundResource(R.drawable.pb1_choice_hover);
                    else
                    view.setBackgroundResource(R.drawable.pb1_hover);
                      //  handleMiniPlaylistIcon2();

                } else {
                    if (RandomMode == 1)
                        view.setBackgroundResource(R.drawable.pb1_choice);
                    else
                    view.setBackgroundResource(R.drawable.pb1);
                  //    handleMiniPlaylistIcon2();
                }


            }
        });

        RAllButton_A.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (RepeatMode == 1) view.setBackgroundResource(R.drawable.pb2_choice_hover);
                    else
                    view.setBackgroundResource(R.drawable.pb2_hover);
                     //   handleMiniPlaylistIcon2();

                } else {
                    if (RepeatMode == 1) view.setBackgroundResource(R.drawable.pb2_choice);
                    else
                    view.setBackgroundResource(R.drawable.pb2);
                    //    handleMiniPlaylistIcon2();
                }


            }
        });


        ROneButton_A.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (RepeatMode == 2) view.setBackgroundResource(R.drawable.pb3_choice_hover);
                    else
                    view.setBackgroundResource(R.drawable.pb3_hover);
                    //   handleMiniPlaylistIcon2();
                } else {
                    if (RepeatMode == 2) view.setBackgroundResource(R.drawable.pb3_choice);
                    else
                    view.setBackgroundResource(R.drawable.pb3);
                    //    handleMiniPlaylistIcon2();
                }

            }
        });

        RAllButton_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RepeatMode != 1) {
                    RepeatMode = 1;

                    view.setBackgroundResource(R.drawable.pb2_choice_hover);
                    ROneButton_A.setBackgroundResource(R.drawable.pb3);
                } else {
                    RepeatMode = 0;

                    view.setBackgroundResource(R.drawable.pb2_hover);
                    ROneButton_A.setBackgroundResource(R.drawable.pb3);

                }

            }
        });

        ROneButton_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RepeatMode != 2) {
                    RepeatMode = 2;

                    view.setBackgroundResource(R.drawable.pb3_choice_hover);
                    RAllButton_A.setBackgroundResource(R.drawable.pb2);
                } else {
                    RepeatMode = 0;

                    view.setBackgroundResource(R.drawable.pb3_hover);
                    RAllButton_A.setBackgroundResource(R.drawable.pb2);

                }

            }
        });

    }


    private void updateTime (TextView tv,int millionSec,int total)
    {
        int second2= total/1000;
        int hh2=second2/3600;

        int second=millionSec/1000;
        int hh=second/3600;
        int mm=second%3600/60;
        int ss=second%60;

        String str=null;
        if(hh2!=0)
            str=String.format("%02d:%02d:%02d",hh,mm,ss);
        else
            str=String.format("%02d:%02d",mm,ss);

        tv.setText(str);



    }
    private Handler FFhandler=new    Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    break;
                case MOVE:
                    //Log.i("Eric","2023.04.14 "+CurrentPlaySpeed );
                    //Log.i("Eric","2023.04.14 "+CurrentDirection );

                    int position=videoView.getCurrentPosition();
                    int totalduration=videoView.getDuration();

                    //Log.i("Eric","2023.04.14: "+videoView.getCurrentPosition());
                    if(CurrentDirection==1)
                    {
                        if(position+CurrentPlaySpeed*1000>0&&position+CurrentPlaySpeed*1000<videoView.getDuration())
                        {
                            videoView.seekTo(position + CurrentPlaySpeed * 1000);
                            FFhandler.sendEmptyMessageDelayed(MOVE_SECOND,1000);
                        }
                        else
                        {

                            CurrentPlaySpeed=1;
                            CurrentDirection=0;
                            videoView.seekTo(videoView.getDuration());


                            setSpeed();

                        }
                    }
                    else if(CurrentDirection==2)
                    {
                        //Log.i("Eric","2023.04.14 "+String.valueOf(position-CurrentPlaySpeed*1000 ));

                        if(position-CurrentPlaySpeed*1000>0) {
                            videoView.seekTo(position - CurrentPlaySpeed * 1000);
                            FFhandler.sendEmptyMessageDelayed(MOVE_SECOND,1000);
                        }
                        else
                        {
                            CurrentPlaySpeed=1;
                            CurrentDirection=0;
                            videoView.seekTo(0);


                            setSpeed();
                        }
                    }

                    //  TimeBarhandler.sendEmptyMessageDelayed(MOVE,1000);

                    //   //Log.i("Eric","UPDATE B" );
                    //2023.03.24
                    break;
                case MOVE_SECOND:
                    //Log.i("Eric","2023.04.14 "+CurrentPlaySpeed );
                    //Log.i("Eric","2023.04.14 "+CurrentDirection );

                    position=videoView.getCurrentPosition();

                    //Log.i("Eric","2023.04.14: "+videoView.getCurrentPosition());
                    if(CurrentDirection==1)
                    {
                        if(position+CurrentPlaySpeed*1000>0&&position+CurrentPlaySpeed*1000<videoView.getDuration()) {
                            videoView.seekTo(position + (CurrentPlaySpeed) * 1000);
                            FFhandler.sendEmptyMessageDelayed(MOVE_SECOND,1000);
                        }
                        else
                        {
                            CurrentPlaySpeed=1;
                            CurrentDirection=0;
                            //  videoView.seekTo(0);

                            setSpeed();
                        }
                    }
                    else if(CurrentDirection==2)
                    {
                        //Log.i("Eric","2023.04.14 "+String.valueOf(position-CurrentPlaySpeed*1000 ));

                        if(position-CurrentPlaySpeed*1000>0) {
                            videoView.seekTo(position - (CurrentPlaySpeed+1) * 1000);
                            FFhandler.sendEmptyMessageDelayed(MOVE_SECOND,1000);
                        }
                        else
                        {
                            CurrentPlaySpeed=1;
                            CurrentDirection=0;
                            videoView.seekTo(0);

                            setSpeed();
                        }
                    }
                    break;

            }


        }
    };

int shouldSeekto=0;

    private Handler FF2handler=new    Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {

            super.handleMessage(msg);

       //     Log.i("Eric","2023.07.04  Enter FF2");

            switch (msg.what)
            {
                case 1:
                    break;
                case 2:
                    handleMiniPlaylistIcon();

                    break;
                case MOVE:
                    //Log.i("Eric","2023.05.19  Enter FF2");
                    //Log.i("Eric","2023.05.19 Enter MOVE" );
                    //Log.i("Eric","2023.05.19 speed:"+CurrentPlaySpeed );
                    //Log.i("Eric","2023.05.19 1"+CurrentDirection );

                    int position=RealMediaPlayer.getCurrentPosition();
                    int totalduration=RealMediaPlayer.getDuration();

                    //Log.i("Eric","2023.05.19 position:"+position );
                    //Log.i("Eric","2023.05.19 totaldurativon:"+totalduration);

               //     //Log.i("Eric","2023.04.14: "+videoView.getCurrentPosition());
                    if(CurrentDirection==1)
                    {

                        //Log.i("Eric","2023.06.08 !!!** MOVE whant to seek to: "+(position + (CurrentPlaySpeed) * 1000));

                        if(position+CurrentPlaySpeed*1000>0&&position+CurrentPlaySpeed*1000<RealMediaPlayer.getDuration())
                        {

                            shouldSeekto=position + CurrentPlaySpeed * 1000;
                            //RealMediaPlayer.seekTo(position + Cposition + CurrentPlaySpeed * 1000urrentPlaySpeed * 1000);
                            RealMediaPlayer.seekTo(shouldSeekto);
                            FF2handler.sendEmptyMessageDelayed(MOVE_SECOND,1000);
                        }
                        else
                        {

                            CurrentPlaySpeed=1;
                            CurrentDirection=0;
                            RealMediaPlayer.seekTo(RealMediaPlayer.getDuration());


                            setSpeed();

                        }
                    }
                    else if(CurrentDirection==2)
                    {
                        //Log.i("Eric","2023.05.19 "+String.valueOf(position-CurrentPlaySpeed*1000 ));

                        if(position-CurrentPlaySpeed*1000>0) {
                            RealMediaPlayer.seekTo(RealMediaPlayer.getCurrentPosition() - CurrentPlaySpeed * 1000);
                            FF2handler.sendEmptyMessageDelayed(MOVE_SECOND,1000);
                        }
                        else
                        {
                            CurrentPlaySpeed=1;
                            CurrentDirection=0;
                            RealMediaPlayer.seekTo(0);


                            setSpeed();
                        }
                    }

                    //  TimeBarhandler.sendEmptyMessageDelayed(MOVE,1000);

                    //   //Log.i("Eric","UPDATE B" );
                    //2023.03.24
                    break;
                case MOVE_SECOND:
                    //Log.i("Eric","2023.06.08  Enter FF2");
                    //Log.i("Eric","2023.06.08 Enter MOVE_SECOND" );
                    //Log.i("Eric","2023.06.08 speed:"+CurrentPlaySpeed );
                    //Log.i("Eric","2023.06.08 1"+CurrentDirection );


                   // position=RealMediaPlayer.getCurrentPosition();

                    //Log.i("Eric","2023.06.08 !!!: "+RealMediaPlayer.getCurrentPosition());

                    if(CurrentDirection==1)
                    {
                        if(shouldSeekto<RealMediaPlayer.getCurrentPosition()) {

                            shouldSeekto = RealMediaPlayer.getCurrentPosition();
                            //Log.i("Eric","2023.06.08 !!!  FIX seek !!!!: "+shouldSeekto);


                        }
                        else if(shouldSeekto>RealMediaPlayer.getCurrentPosition()){
                            shouldSeekto=shouldSeekto+1000;
                            //shouldSeekto = shouldSeekto+(RealMediaPlayer.getCurrentPosition())/2;
                            //Log.i("Eric","2023.06.08 !!!  FIX seek BBB!!!!: "+shouldSeekto);
                        }

                        if(shouldSeekto+CurrentPlaySpeed*1000>0&&shouldSeekto+CurrentPlaySpeed*1000<RealMediaPlayer.getDuration()) {

                            //Log.i("Eric","2023.06.08 !!! MOVE_SECOND whant to seek !!! P: "+RealMediaPlayer.getCurrentPosition() );
                            //Log.i("Eric","2023.06.08 !!! MOVE_SECOND whant to seek seek: "+(CurrentPlaySpeed * 1000 ));
                            //Log.i("Eric","2023.06.09 !!! MOVE_SECOND whant to seek to !!!: "+(RealMediaPlayer.getCurrentPosition() + (CurrentPlaySpeed) * 1000));
                            shouldSeekto=shouldSeekto+((CurrentPlaySpeed) * 1000);
                            RealMediaPlayer.seekTo(shouldSeekto);

                            FF2handler.sendEmptyMessageDelayed(MOVE_SECOND,1000);
                        }
                        else
                        {
                            CurrentPlaySpeed=1;
                            CurrentDirection=0;
                            //  videoView.seekTo(0);
                            RealMediaPlayer.seekTo(RealMediaPlayer.getDuration());
                            setSpeed();
                        }
                    }
                    else if(CurrentDirection==2)
                    {
                   //     Log.i("Eric","2023.07.04  "+RealMediaPlayer.getCurrentPosition());
                   //     Log.i("Eric","2023.07.04 shouldSeekto  "+shouldSeekto);
                   //     Log.i("Eric","2023.07.04 CurrentPlaySpeed "+CurrentPlaySpeed);

                        if(shouldSeekto>RealMediaPlayer.getCurrentPosition()) {

                          //  shouldSeekto = RealMediaPlayer.getCurrentPosition();
                            //Log.i("Eric","2023.06.08 !!!  FIX seek !!!!: "+shouldSeekto);


                        }
                        else if(shouldSeekto<RealMediaPlayer.getCurrentPosition()){
                            shouldSeekto=RealMediaPlayer.getCurrentPosition()-1000;
                            //shouldSeekto = shouldSeekto+(RealMediaPlayer.getCurrentPosition())/2;
                            //Log.i("Eric","2023.06.08 !!!  FIX seek BBB!!!!: "+shouldSeekto);
                        }


                        if(shouldSeekto-CurrentPlaySpeed*1000>0) {
                            shouldSeekto=shouldSeekto- (CurrentPlaySpeed) * 1000;
                             RealMediaPlayer.seekTo( shouldSeekto);
                            FF2handler.sendEmptyMessageDelayed(MOVE_SECOND,1000);
                        }
                        else
                        {
                            CurrentPlaySpeed=1;
                            CurrentDirection=0;
                            RealMediaPlayer.seekTo(0);
                            //Speed.setVisibility(View.INVISIBLE);

                            setSpeed();
                        }
                    }
                    break;

            }


        }
    };


    private Handler TimeBarhandler=new    Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    break;
                case UPDATE_TIME:
                    // //Log.i("Eric","UPDATE A" );
                    // if(videoView.isPlaying())

                   // int position=videoView.getCurrentPosition();

                    int position=RealMediaPlayer.getCurrentPosition();

                    //int totalduration=videoView.getDuration();
                    int totalduration=RealMediaPlayer.getDuration();

                    //  //Log.i("Eric","UPDATE "+position+ ":"+totalduration );
                    updateTime(time1,position,totalduration);
                    updateTime(time2,totalduration,totalduration);

                    //2023.06.28
                {
                    //Log.i("Eric","Eric 2023.06.28:"+totalduration);
                //    Log.i("Eric","Eric 2023.06.28 A:"+PlayList.get(CurrenPlayListIndex).timeInMillisec);

                     if(totalduration/1000!=PlayList.get(CurrenPlayListIndex).timeInMillisec/1000)
                    {
                   //     Log.i("Eric","Eric 2023.06.28 B:"+PlayList.get(CurrenPlayListIndex).timeInMillisec);

                        PlayList.get(CurrenPlayListIndex).timeInMillisec= totalduration;



                        PlayList.get(CurrenPlayListIndex).Timetext=timePrase(totalduration);


                             timeBar.setMax(((int)(PlayList.get(CurrenPlayListIndex).timeInMillisec)/1000));
                       //      playlist.clear()
                       //      handelPlayList() ;
                        adapter2.notifyDataSetChanged();
                        adapter3.notifyDataSetChanged();
                  //      Log.i("Eric","Eric 2023.06.28 C:"+PlayList.get(CurrenPlayListIndex).timeInMillisec);

                        //playlist


                    }


                }

                    if(!SeekBarStart)
                        timeBar.setProgress(position/1000);
                    

                    //  TimeBarhandler.sendEmptyMessageDelayed(UPDATE_TIME,500);
                    TimeBarhandler.sendEmptyMessageDelayed(UPDATE_TIME,1000);
                    //   //Log.i("Eric","UPDATE B" );
                    //2023.03.24
                    break;
                case HANDLE_LASTPICTURE:

                    if(videoView.getCurrentPosition()>1000)
                    {
                        videoView.pause();
                        videoView.seekTo(length/2);


                    }
                    else
                        TimeBarhandler.sendEmptyMessageDelayed(HANDLE_LASTPICTURE,200);
                    break;
            }


        }
    };


    private Handler Controlhandler=new Handler(Looper.getMainLooper());
    private Runnable Crunable=new Runnable() {
        @Override
        public void run() {
            if(PlayControlUI.getVisibility()==View.VISIBLE) {

                MiniListUI.setVisibility(View.INVISIBLE);
                PlayControlUI.setVisibility(View.INVISIBLE);
                SeekBarStart = false;
            }
            if(PlayControlUI_A.getVisibility()==View.VISIBLE) {

                MiniListUI_A.setVisibility(View.INVISIBLE);
                PlayControlUI_A.setVisibility(View.INVISIBLE);

            }
        }
    };
    private Handler picturehandler = new Handler(Looper.getMainLooper());
    private Runnable Prunable = new Runnable() {
        @Override
        public void run() {
//2023.04.20
            //CurrentPlaySpeed = 1;
            //CurrentDirection=0;

            if (isPlaying) {
                if (CurrentDirection != 2) {
                    PictureSec = PictureSec + CurrentPlaySpeed;
                    if (PictureSec >= 5) {

                        PictureSec = 0;
                        PictureSec = 0;
                        CurrenPlayListIndex++;
                        time2_A.setText(PlayList.get(0).Timetext);
                        timeBar_A.setMax((int) 5);
                        timeBar_A.setProgress(PictureSec);
                        time1_A.setText("00:00.00");
                        //Log.i("Eric", "2023.03.07:" + CurrenPlayListIndex);

                        if (RepeatMode == 1 && (CurrenPlayListIndex >= PlayList.size()))
                            CurrenPlayListIndex = 0;
                        else if (RepeatMode == 2) CurrenPlayListIndex--;
                        //Reapeat ALL
                        if (CurrenPlayListIndex < PlayList.size()) {
                            //   PlayListUI.setVisibility(View.INVISIBLE);
                            // imageView.setVisibility(View.VISIBLE);
                            InImageViewUI = true;
                            //2023.06.08

                            GlideSHowPiccture(PlayList.get(CurrenPlayListIndex).Path);

                            long now = SystemClock.uptimeMillis();
                            long next = now + (1000);

                            picturehandler.postAtTime(Prunable, next);
                        } else {
                            //  if(RepeatMode==1)
                            //if(!PlayFromOutSide)
                            {
                                imageView.setImageDrawable(null);
                                imageView.setVisibility(View.INVISIBLE);
                                videoView.setVisibility(View.INVISIBLE);
                                if (AudioUI.getVisibility() == View.VISIBLE)
                                    AudioUI.setVisibility(View.INVISIBLE);
                                PlayListUI.setVisibility(View.VISIBLE);
                                PlayButton.requestFocus();
                                isPlaying = false;
                                InImageViewUI = false;
                                picturehandler.removeCallbacks(Prunable);




                            }
                            /*
                            else
                            {  isPlaying = false;
                                InImageViewUI = false;
                                imageView.setImageDrawable(null);
                                picturehandler.removeCallbacks(Prunable);
                                mActivity.finish();
                            }

                             */
                        }
                    } else {
                        //  time2_A.setText(PlayList.get(0).Timetext);
                        timeBar_A.setProgress(PictureSec);
                        time1_A.setText("00:00.0" + Integer.toString(PictureSec));

                        long now = SystemClock.uptimeMillis();
                        long next = now + (1000);
                        timeBar_A.setProgress(PictureSec);
                        time1_A.setText("00:00.0" + Integer.toString(PictureSec));

                        picturehandler.postAtTime(Prunable, next);
                    }
                }
                else{  //  if (CurrentDirection == 2)


                    PictureSec = PictureSec - CurrentPlaySpeed;
                    if (PictureSec <= 0 ) {

                        PictureSec = 0;
                        PictureSec = 5;
                        CurrenPlayListIndex--;
                        time2_A.setText(PlayList.get(0).Timetext);
                        timeBar_A.setMax((int) 5);
                        timeBar_A.setProgress(PictureSec);
                        time1_A.setText("00:00.00");
                        //Log.i("Eric", "2023.03.07:" + CurrenPlayListIndex);

                        if (RepeatMode == 1 && (CurrenPlayListIndex <0))
                            CurrenPlayListIndex = PlayList.size()-1;
                        else if (RepeatMode == 2) CurrenPlayListIndex++;
                        //Reapeat ALL
                        if (CurrenPlayListIndex < PlayList.size()&&CurrenPlayListIndex>=0) {
                            //   PlayListUI.setVisibility(View.INVISIBLE);
                            // imageView.setVisibility(View.VISIBLE);
                            InImageViewUI = true;
                            //2023.06.08  habdle

                            GlideSHowPiccture(PlayList.get(CurrenPlayListIndex).Path);

                            long now = SystemClock.uptimeMillis();
                            long next = now + (1000);

                            picturehandler.postAtTime(Prunable, next);
                        } else {
                            //  if(RepeatMode==1)
                            //2023.06.08
                            imageView.setImageDrawable(null);
                                imageView.setVisibility(View.INVISIBLE);

                                if (AudioUI.getVisibility() == View.VISIBLE)
                                    AudioUI.setVisibility(View.INVISIBLE);
                                PlayListUI.setVisibility(View.VISIBLE);
                                PlayButton.requestFocus();
                            isPlaying = false;
                            InImageViewUI = false;
                                picturehandler.removeCallbacks(Prunable);


                        }
                    } else {
                        //  time2_A.setText(PlayList.get(0).Timetext);
                        timeBar_A.setProgress(PictureSec);
                        time1_A.setText("00:00.0" + Integer.toString(PictureSec));

                        long now = SystemClock.uptimeMillis();
                        long next = now + (1000);
                        timeBar_A.setProgress(PictureSec);
                        time1_A.setText("00:00.0" + Integer.toString(PictureSec));

                        picturehandler.postAtTime(Prunable, next);
                    }


                }
                if(isPlaying)
                handleMiniPlaylistIcon3();
            }
        }
            };



    //2023.03.01

    private void setMp3background(String filePath)
    {
        videoView.setBackgroundResource(R.drawable.santorini19201080);

    }


    public void checkAudioCover(MediaFile TestFile)
    {  // 2023.03.06
        //
        // AudioMan=(TextView)findViewById(R.id.AudioMan);
        //        AudioName=(TextView)findViewById(R.id.AudioName);
        //Log.i("Eric","2023.05.03 Enter check");

        TestFile.CoverPicture=null;
        MediaMetadataRetriever e = new MediaMetadataRetriever();
        Uri first = Uri.parse(TestFile.Path);
        //.setDataSource(list[i].getPath());

        e.setDataSource(getApplicationContext(), first);
        String Type=e.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
        String Singer=e.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String Songname=e.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        //   String Songname=e.extractMetadata(MediaMetadataRetriever.);

        e.extractMetadata(MediaMetadataRetriever.METADATA_KEY_IMAGE_HEIGHT);

        if(Type.contains("audio"))
        {
            try {
                //Log.i("Eric", "2023.03.06 A");
                byte[] buffer = e.getEmbeddedPicture()                                                                                                                                                              ;
                //Log.i("Eric", "2023/02/16 V" + buffer.length);



                if (buffer.length > 0) {

                    Bitmap cover = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);

                    if (cover != null) TestFile.CoverPicture = cover;

                    //Log.i("Eric", "A:" + cover.getByteCount());
                    //Log.i("Eric", "B:" + TestFile.CoverPicture.getByteCount());


                }
            }
            catch(Exception xe)
            {
                //Log.i("Eric", "Error:" + xe.toString());
            }
        }

        if(TestFile.CoverPicture!=null) {
            imageView.setVisibility(View.INVISIBLE);
            //Log.i("Eric", "2023.03.06 has cover");


            AudioUI.setVisibility(View.VISIBLE);


            videoView.setBackground(null);
            audiopic.setImageBitmap(TestFile.CoverPicture);

        }
        else{
            imageView.setVisibility(View.INVISIBLE);
            AudioUI.setVisibility(View.VISIBLE);
            //videoView.setBackgroundResource(R.drawable.santorini19201080);
            audiopic.setImageResource(R.drawable.audio2);
            videoView.setBackground(null);
        }
        if(Singer!=null&& Singer!="") AudioMan.setText(Singer);
        else AudioMan.setText("");
        if(Songname!=null&&Songname!="") AudioName.setText(Songname);
        else AudioName.setText(TestFile.Name);

        AudioMan.setSelected(true);
        AudioName.setSelected(true);

    }
 long time3=0;
    public long  MediaTimeCheck2(File file) {
        long time2=0;
        MediaPlayer checkMediaPlayer=new MediaPlayer();
        try {
            checkMediaPlayer.setDataSource(file.getPath());
                          Log.i("Eric","Eric fff A:+"+  file.getPath());

            checkMediaPlayer.prepare();
            checkMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    Log.i("Eric","Eric fff:+"+  mediaPlayer.getDuration());


                    if (mediaPlayer.getDuration()>0) {
                        Calendar filelong = new GregorianCalendar();
                        //  filelong.setTimeInMillis((long)mediaPlayer.getDuration());
                        time3 = (long)mediaPlayer.getDuration();
                        //Log.i("Eric","Eric 2023.05.10:+"+  time3);
                    }
                    else
                        time3=-99;

                    
                }
            });
        } catch (IOException ex) {
             Log.i("Eric","Eric fff4:+"+ ex.toString());
            ex.printStackTrace();
        }
        Log.i("Eric","Eric fff4:+"+ time3);
        
        return time3;

    }
   // long time=0;
    //public long MediaTimeCheck(File file) {
    public long MediaTimeCheck(File file,int filetype) {
        long time=0;
        //Log.i("Eric", "2023.05.11 list[i]." + file.getPath()+":"+filetype);

        try {
            if (file.isFile() && file.length() > 0) {
                MediaMetadataRetriever e = new MediaMetadataRetriever();

                //Log.i("Eric", "list[i]." + file.getPath());
                Uri first = Uri.parse(file.getPath());
                //Log.i("Eric", "2023.05.11 Start ana:" + file.getName() );


                e.setDataSource(getApplicationContext(), first);
                //Log.i("Eric", "!!!!" +   ":" );

                String E = e.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
                //Log.i("Eric", "!!!!" +   ":" + E );
                String D="";
                if(filetype==2 ||filetype==3) {
                    String A = e.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
                    //Log.i("Eric", "!!!!" + ":" + A);
                    String B = e.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO);
                    //Log.i("Eric", "!!!!" +   ":" + A + " " + B );
                     D = e.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    //Log.i("Eric", "!!!!" +   ":" + A + " " + B + " "  + D);

                }
                 if (filetype==3 ||filetype==4) {


                     String C = e.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_IMAGE);

                     //Log.i("Eric", "!!!!" + file.getName() +";" + C );

                 }
                if(E.contains("video")||E.contains("audio"))
                {
                    Calendar  filelong=new GregorianCalendar();
                    filelong.setTimeInMillis(Long.parseLong(D));
                    time=Long.parseLong(D);

                    //   //Log.i("Eric","!!! The File "+fr.getName()+" is "+E+" The time is "+filelong.getTime());
                    //Log.i("Eric","!!! The File "+file.getName()+" is "+E+" The time is "+filelong.get(Calendar.HOUR));
                }


                //video
            }
        }
        catch(Exception e)
        {
            ////Log.i("Eric","!!!!2023/02/15 Error  " +e.toString());

            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inJustDecodeBounds = true;
            String filePath =file.getPath();
            BitmapFactory.decodeFile(filePath, options);



            String mimeType = options.outMimeType;
            if(mimeType!=null&&mimeType.contains("image") ){

                //Log.i("Eric", "!!!!2023/02/15 pICTURE  " + mimeType);
                time=-1;
            }
            else
            {
                  if(filetype==2 ||filetype==3) {


                      //Log.i("Eric","2023.05.05 preparesync A in !!!!");
                      //RealMediaPlayer.prepareAsync();

                      time=-99;
                  }
                  else{

                      time=-1;
                  }



            }

        }
        return time;
    }



    void    handelPlayList()
    {

        //Log.i("Eric","213123 Enter handlePlayList");
        if(PlayList.size()>0) {



            adapter2 = new PlayListBaseAdapter(PlayList, inflater2);
            playlist.setAdapter(adapter2);
            //2023.06.08 A

            //2023.06.08 A
            playlist.post(new Runnable() {

                @Override
                public void run() {
           //         //Log.i("Eric","2023.06.09 post");

                    playlist.smoothScrollToPosition(playlist.getCount() - 1);
                }
            });
        }
        else
        {
            adapter2.notifyDataSetChanged();
        }

    }





    void getFileList(String path,int type)
    {
        try {
            ///storage/emulated/0
            //    File current=new File("/storage/emulated/0/");
            File current = new File(path);
         //   if (current.exists()) //Log.i("Eric", "2023.03.09 !!" + current.getAbsolutePath());

            File[] list = current.listFiles();
            //Log.i("Eric", "!!2023.03.09" + list.length);

            //FinalList; FolderList=null;
            FinalList = new ArrayList<>();
            FolderList = new ArrayList<>();
            FileList = new ArrayList<>();
            FinalShortList = new ArrayList<>();

            int currentID = 1;
            //Log.i("Eric", "2023.03.09 A list size " + list.length);
            for (int i = 0; i < list.length; i++) {

//             if (list[i].isDirectory()) {
                MediaFile n = new MediaFile();
                //  n.ID = currentID;
                //   n.Filetype = 1;
                //Log.i("Eric", "2023.03.10 A start ." + list[i].getName());

                //  if(list[i].getName().startsWith(".")) break;
                if (list[i].getName().startsWith(".")) {
                    //Log.i("Eric", "2023.03.10 B ." + list[i].getName());
                    continue;

                }
                n.Name = list[i].getName();
                n.Path = list[i].getAbsolutePath();
                n.ischose = false;


                //2023/02/15

                //    //Log.i("Eric","2023/02/15 !!"+timePrase(5710499));
                //  public static String timePrase(long t)
                //FolderList.add(n);
                // currentID++;
                //Log.i("Eric", "B !!" + list[i].isDirectory());
                if (list[i].isDirectory()) {
                    n.Filetype = 1;  //1 folder 2 file
                    FolderList.add(n);
                } else {
                    n.Filetype = 2;
                    if (n.Name.toLowerCase().endsWith(".mp4")
                            || n.Name.toLowerCase().endsWith("3gp")
                            || n.Name.toLowerCase().endsWith("mkv")
                            || n.Name.toLowerCase().endsWith("avi")
                            || n.Name.toLowerCase().endsWith("m2ts")
                            || n.Name.toLowerCase().endsWith("m4v")
                            || n.Name.toLowerCase().endsWith("ts")
                            || n.Name.toLowerCase().endsWith("vob")
                            || n.Name.toLowerCase().endsWith("tp")
                            || n.Name.toLowerCase().endsWith("trp")

                            || n.Name.toLowerCase().endsWith("mpeg")
                            || n.Name.toLowerCase().endsWith("mpg")
                            || n.Name.toLowerCase().endsWith("mov")
                    ) {
                        n.Filetype = 2;
                    } else if (n.Name.toLowerCase().endsWith(".mp3")
                            || n.Name.toLowerCase().endsWith(".wav")
                            // ||n.Name.toLowerCase().endsWith(".mid")
                            || n.Name.toLowerCase().endsWith(".flac")) {
                        n.Filetype = 3;
                    } else if (n.Name.toLowerCase().endsWith(".jpg") ||
                            n.Name.toLowerCase().endsWith(".jpeg") ||
                            n.Name.toLowerCase().endsWith(".png") ||
                            n.Name.toLowerCase().endsWith(".bmp")
                    ) {
                        n.Filetype = 4;
                    } else {
                        //break;
                        continue;

                    }
                    //Log.i("Eric", "C!!" + list[i].isDirectory());
                    n.PlayListID = 0;
                    FileList.add(n);
                    //Log.i("Eric", "D !!" + list[i].isDirectory());
                }

            }
            Collections.sort(FolderList, comparator);
            Collections.sort(FileList, comparator);
            //   //Log.i("Eric","currentID"+currentID);
            //Log.i("Eric", "size" + FolderList.size());
            //Log.i("Eric", "size" + FileList.size());

            if (FolderList.size() > 0) {
                for (MediaFile n : FolderList) {
                    n.ID = currentID;
                    currentID++;
                    FinalList.add(n);
                }
            }
            if (FileList.size() > 0) {
                for (MediaFile n : FileList) {
                    n.ID = currentID;
                    currentID++;
                    FinalList.add(n);
                }
            }

            //// choice Filetype  2 video 3 audio 4 picture
            if (type > 0) {
                for (int j = FinalList.size() - 1; j >= 0; j--) {
                    if (FinalList.get(j).Filetype > 1) {
                        // if (FinalList.get(j).Filetype != PlayList.get(0).Filetype)
                        if (FinalList.get(j).Filetype != type)
                            FinalList.remove(j);
                    }

                }

            }
        }
        catch (Exception e)
        {
            //Log.i("Eric","2023.05.26 catch e:"+e.toString());
        }
    }





    //01:35:10.020=5700020
    public static String timePrase(long t)
    {
        String time="";
        long hour=t/3600000;
        long minutes=t%3600000;
        long minute=minutes/60000;
        long seconds=minutes%60000;

        long second=Math.round((float) seconds/1000);

        //Log.i("Eric","2023 0214 second"+second);

        if(hour<10) time+="0";
        time+=hour+":";
        if(minute<10) time+="0";
        time+=minute +":";
        if(second<10) time+="0";
        time+=second;
        //Log.i("Eric","2023 0214 time"+time);

        return time;
    }




    //2023/02/14
    void handleFileListChoice()
    {
       // Log.i("Eric", "2023.6.09 handleFileListChoice()" );

      //  if(CurrentLongClickFolder!=null)
        //Log.i("Eric", "2023.06.06"+CurrentLongClickFolder.Path);
        for (int i = 0; i < FinalList.size(); i++) {
            ////Log.i("Eric", "2023/02/13+Fi"+FinalList.get(k).Path);
            FinalList.get(i).ischose=false;

            for (int j = 0; j < PlayList.size(); j++) {

               if (FinalList.get(i).Path.equals(PlayList.get(j).Path)


                ) {
                       FinalList.get(i).ischose=true;
                    break;
                }
               if(CurrentLongClickFolderList!=null) {
                   for (int k = 0; k < CurrentLongClickFolderList.size(); k++) {
                       if (CurrentLongClickFolderList.get(k).Path.equals(FinalList.get(i).Path)


                       ) {
                       //    Log.i("Eric", "2023.07.04!!" + i + ":" + FinalList.get(i).Path);
                       //    Log.i("Eric", "2023.07.04!!" + k+ ":" + CurrentLongClickFolderList.get(k).Path);

                           FinalList.get(i).ischose = true;
                           break;
                       }
                   }
               }
            }
        }
    }

    Comparator<MediaFile> comparator=new Comparator<MediaFile>() {
        @Override
        public int compare(MediaFile m1, MediaFile m2) {
            return m1.Name.compareTo(m2.Name);
        }
    };

    Comparator<MediaFile> comparatorOnlyFile=new Comparator<MediaFile>() {
        @Override
        public int compare(MediaFile m1, MediaFile m2) {
            if(m1.Filetype!=1&m2.Filetype!=1)
                return m1.Name.compareTo(m2.Name);

            else return 0;
        }
    };

    void setFileListBG(int select,boolean clean) {

        //Log.i("Eric", "2023 06.07" + listview.getCount());
        //Log.i("Eric", "2023 06.07" + FinalList.size());
        for (int j = 0; j <FinalList.size(); j++){

            //Log.i("Eric", "2023 06.07 "+j+":" + FinalList.get(j).Path);
            //Log.i("Eric", "2023 06.07 "+j+":" + FinalList.get(j).ischose);
            //Log.i("Eric", "2023 06.07 "+j+":" + FinalList.get(j).Filetype);
        }
        for(int j=0;j<=listview.getCount();j++)
        {
            //   //Log.i("Eric","&&&& i:"+i+"j:"+j);
            if(listview.getChildAt(j)!=null)
            {     View ls = (View) listview.getChildAt(j);
                LinearLayout yr2=null;
                if(FinalList.get(j).ischose) {
                    yr2 = (LinearLayout) ls.findViewById(R.id.filerowly);

                    if (j == select ) {
                        yr2.setBackgroundResource(R.drawable.list_hover_choose);
                    }
                    else {
                        if(FinalList.get(j).Filetype>0)
                            yr2.setBackgroundResource(R.drawable.list_choose);

                    }

                }
                else
                {
                    yr2 = (LinearLayout) ls.findViewById(R.id.filerowly);
                    if (j == select ) {
                        if(FinalList.get(j).Filetype>1)
                            yr2.setBackgroundResource(R.drawable.list_hover);
                        else
                            yr2.setBackgroundResource(R.drawable.folder_hover);

                    }
                    else {
                        if(FinalList.get(j).Filetype>1)
                            yr2.setBackgroundResource(R.drawable.list);
                        else
                            yr2.setBackgroundResource(R.drawable.folder_default);

                    }
                }

                if(clean) {
                    yr2 = (LinearLayout) ls.findViewById(R.id.filerowly);
                    if(FinalList.get(j).Filetype>1)
                        yr2.setBackgroundResource(R.drawable.list);
                    else
                        yr2.setBackgroundResource(R.drawable.folder_default);


                }


            }


        }
//        if(clean) listview.getChildAt(0).setSelected(true);
    }

    void setIconfocus()
    {
        deviceicon1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    //2023.06.06
                    device1.setImageResource(R.drawable.storage_hover);
                    if(ReflashFileList) {
                        Path.setText(devices[0]);
                        if (PlayList.size() > 0)
                            getFileList(devices[0],CurrenMediaType);
                        else
                            getFileList(devices[0],0);
                        filelistposition_Path="";
                        if(FinalList1!=null&& FinalList1.size()>0)
                        {
                            FinalList=new ArrayList<MediaFile>();
                            for(int i=0;i<FinalList1.size();i++) {
                                FinalList.add(FinalList1.get(i));
                            }
                        }
                        adapter = new FlieListBaseAdapter(FinalList, inflater);
                        listview.setAdapter(adapter);
                        //Log.i("Eric","2023.06.07 1 !!!"+PlayListDevices);
                        if (PlayList.size() > 0&&PlayListDevices==1) {

                            //Log.i("Eric","2023/02/08 Enter"+PlayListDevices);


                                handelPlayList();
                            }
                            else {

                                ArrayList<MediaFile> Empty=new ArrayList<MediaFile>();

                                adapter2 = new PlayListBaseAdapter(Empty, inflater2);
                                playlist.setAdapter(adapter2);

                            playlist.post(new Runnable() {

                                @Override
                                public void run() {
                                    //         //Log.i("Eric","2023.06.09 post");

                                    playlist.smoothScrollToPosition(playlist.getCount() - 1);
                                }
                            });

                            }

                        ReflashFileList=false;
                    }
                }
                else
                    device1.setImageResource(R.drawable.storage_default);
            }});
        deviceicon2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    device2.setImageResource(R.drawable.usb_hover);
                    if(ReflashFileList) {
                        Path.setText(devices[1]);
                        if (PlayList.size() >0)
                            getFileList(devices[1],CurrenMediaType);
                        else
                            getFileList(devices[1],0);
                        filelistposition_Path="";
                        if(FinalList2!=null&& FinalList2.size()>0)
                        {
                            FinalList=new ArrayList<MediaFile>();
                            for(int i=0;i<FinalList2.size();i++) {
                                FinalList.add(FinalList2.get(i));
                            }
                        }
                        adapter = new FlieListBaseAdapter(FinalList, inflater);
                        listview.setAdapter(adapter);

                        //Log.i("Eric","2023.06.07 2 !!!"+PlayListDevices);
                        if (PlayList.size() > 0&&PlayListDevices==2) {

                                handelPlayList();
                            }
                            else {

                                ArrayList<MediaFile> Empty=new ArrayList<MediaFile>();

                                adapter2 = new PlayListBaseAdapter(Empty, inflater2);
                                playlist.setAdapter(adapter2);

                            playlist.post(new Runnable() {

                                @Override
                                public void run() {
                                    //         //Log.i("Eric","2023.06.09 post");

                                    playlist.smoothScrollToPosition(playlist.getCount() - 1);
                                }
                            });

                            }

                        ReflashFileList=false;
                    }
                }
                else
                    device2.setImageResource(R.drawable.usb_default);
            }
        });

        deviceicon3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    device3.setImageResource(R.drawable.usb_hover);
                    if(ReflashFileList) {
                        Path.setText(devices[2]);
                        if (PlayList.size() > 0)
                            getFileList(devices[2],CurrenMediaType);
                        else
                            getFileList(devices[2],0);
                        filelistposition_Path="";
                        if(FinalList3!=null&& FinalList3.size()>0)
                        {
                            FinalList=new ArrayList<MediaFile>();
                            for(int i=0;i<FinalList3.size();i++) {
                                FinalList.add(FinalList3.get(i));
                            }
                        }
                        adapter = new FlieListBaseAdapter(FinalList, inflater);
                        listview.setAdapter(adapter);

                        //Log.i("Eric","2023.06.07 3 !!!"+PlayListDevices);
                        if (PlayList.size() > 0&&PlayListDevices==3) {

                                handelPlayList();
                            }
                            else {

                                ArrayList<MediaFile> Empty=new ArrayList<MediaFile>();

                                adapter2 = new PlayListBaseAdapter(Empty, inflater2);
                                playlist.setAdapter(adapter2);

                            playlist.post(new Runnable() {

                                @Override
                                public void run() {
                                    //         //Log.i("Eric","2023.06.09 post");

                                    playlist.smoothScrollToPosition(playlist.getCount() - 1);
                                }
                            });

                            }

                        ReflashFileList=false;
                    }
                }
                else
                    device3.setImageResource(R.drawable.usb_default);
            }
        });

        deviceicon4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                {
                    device4.setImageResource(R.drawable.sd_storage_hover);
                    if(ReflashFileList) {
                        Path.setText(devices[3]);
                        if (PlayList.size() > 0)
                            getFileList(devices[3],CurrenMediaType);
                        else
                            getFileList(devices[3],0);

                        filelistposition_Path="";
                        if(FinalList4!=null&& FinalList4.size()>0)
                        {
                            FinalList=new ArrayList<MediaFile>();
                            for(int i=0;i<FinalList4.size();i++) {
                                FinalList.add(FinalList4.get(i));
                            }
                        }
                        adapter = new FlieListBaseAdapter(FinalList, inflater);
                        listview.setAdapter(adapter);
                        //Log.i("Eric","2023.06.07 4 !!!"+PlayListDevices);

                        if (PlayList.size() > 0&&PlayListDevices==4) {

                                handelPlayList();
                            }
                            else {

                                ArrayList<MediaFile> Empty=new ArrayList<MediaFile>();

                                adapter2 = new PlayListBaseAdapter(Empty, inflater2);
                                playlist.setAdapter(adapter2);

                            playlist.post(new Runnable() {

                                @Override
                                public void run() {
                                    //         //Log.i("Eric","2023.06.09 post");

                                    playlist.smoothScrollToPosition(playlist.getCount() - 1);
                                }
                            });

                                //   llContent.removeAllViews();
                            }

                        ReflashFileList=false;
                    }
                    //   listview.setAdapter(adapter);
                }
                else
                    device4.setImageResource(R.drawable.sd_storage_default);
            }
        });


    }

    private void hadleDeviceIcon()
    {
        if(devices[1].equals(""))  deviceicon2.setVisibility(View.GONE) ;  else deviceicon2.setVisibility(View.VISIBLE);
        if(devices[2].equals(""))  deviceicon3.setVisibility(View.GONE) ;  else deviceicon3.setVisibility(View.VISIBLE);
        if(devices[3].equals(""))  deviceicon4.setVisibility(View.GONE) ;  else deviceicon4.setVisibility(View.VISIBLE);

        deviceicon1.requestFocus();
        device1.setImageResource(R.drawable.storage_hover);


    }

    private int checkDevice(){
        File e=new File(Environment.getExternalStorageDirectory().getParent());
        File f=new File(e.getParent());
        //Log.i("Eric", "223" + e);
        //Log.i("Eric", "224" + f);
        File[]ee=f.listFiles();
        //Log.i("Eric", "225" +ee.length );
        for(int i=0;i<ee.length;i++) {
            //Log.i("Eric", "226" + ee[i].getName());
        }

        return ee.length;
    }

    @Override
    protected void onDestroy() {
        //Log.i("Eric", "Eric 2023.03.01 Player Destory");
        this.finish();
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        //  intentFilter = new IntentFilter();

        //  intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        //   intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);

        //   usbBroadcastReceiver = new USBBroadcastReceiver();
        // registerReceiver(usbBroadcastReceiver, intentFilter);

        intentFilter2 = new IntentFilter();
        intentFilter2.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter2.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter2.addAction(Intent.ACTION_MEDIA_CHECKING);
        intentFilter2.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter2.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter2.addDataScheme("file");
        sdBroadcastReceiver=new SDBroadcastReceiver();
        registerReceiver(sdBroadcastReceiver, intentFilter2);
        if(filelistposition_Path==null)
            filelistposition_Path="";

        deviceicon1.requestFocus();

        Path.setText(devices[0]);

        //  listview.requestFocus();
        // device1.setImageResource(R.drawable.storage_hover);
        // deviceicon1.requestFocus();
        // if(listview.getChildAt(2)!=null)
        // listview.getChildAt(2).setSelected(true);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(sdBroadcastReceiver);
        //   unregisterReceiver(usbBroadcastReceiver);
        //Log.i("VS Player", " Player Pause");

        super.onPause();
        if(isFinishing())
        {

        }
        else
        {
         finish();
        }
    }



    @Override
    protected void onStop() {
        //Log.i("VS Player", " Player Stop");

        if(RealMediaPlayer!=null){

            RealMediaPlayer.release();
            RealMediaPlayer=null;
        }
        FF2handler.removeCallbacksAndMessages(null);
        FF2handler=null;
        TimeBarhandler.removeCallbacksAndMessages(null);
        TimeBarhandler=null;
        Controlhandler.removeCallbacksAndMessages(null);
        Controlhandler=null;

        super.onStop();
    }








    void deteUsbDevices() {

        HashMap<String, UsbDevice> deviceHashMap = ((UsbManager) getSystemService(USB_SERVICE)).getDeviceList();
        for (Map.Entry e : deviceHashMap.entrySet()) {
            //Log.i("Eric", "USB:" + e.getKey() + "!!!" + e.getValue());
            //2023-01-30 17:12:12.893 8568-8568/com.viewsonic.vsplayer1 I/Eric: 20230130 sdcard:android.intent.action.MEDIA_EJECT
            //2023-01-30 17:12:13.818 8568-8568/com.viewsonic.vsplayer1 I/Eric: 20230130 sdcard:android.intent.action.MEDIA_UNMOUNTED

            //2023-01-30 17:13:30.446 8568-8568/com.viewsonic.vsplayer1 I/Eric: 20230130 sdcard:android.intent.action.MEDIA_CHECKING
            //2023-01-30 17:13:30.450 8568-8568/com.viewsonic.vsplayer1 I/Eric: 20230130 sdcard:android.intent.action.MEDIA_MOUNTED


        }



        //String externalStorage = System.getenv("SECONDARY_STORAGE");
        ////Log.i("Eric",externalStorage);

    }


    public String getSDcardPath()
    {   String sdPath=null;
        StorageManager storageManager=(StorageManager) this.getSystemService(Context.STORAGE_SERVICE);
        Class<?> volumeInfoClazz = null;
        Class<?> diskInfoClazz=null;

        try{
            diskInfoClazz=Class.forName("android.os.storage.DiskInfo");
            Method isSd=diskInfoClazz.getMethod("isSd");
            volumeInfoClazz=Class.forName("android.os.storage.VolumeInfo");
            Method getType=volumeInfoClazz.getMethod("getType");
            Method getDisk=volumeInfoClazz.getMethod("getDisk");
            Field path=volumeInfoClazz.getDeclaredField("path");
            Method getVolumes=storageManager.getClass().getMethod("getVolumes");
            List<Class<?>> result=(List<Class<?>>) getVolumes.invoke(storageManager);

            //Log.i("Eric","234+"+result.size());

            for(int i=1;i<devices.length;i++)
                devices[i]="";

            for(int i=0;i<result.size();i++)
            {
                //Log.i("Eric","name:"+result.get(i));
                Object volimeInfo=result.get(i);
                //Log.i("Eric","type:"+getType.invoke(volimeInfo));

                if((int)getType.invoke(volimeInfo)==0)
                {
                    Object disk=getDisk.invoke(volimeInfo);
                    if(disk!=null)
                    {
                        if((boolean)isSd.invoke(disk))
                        {
                            sdPath=(String) path.get(volimeInfo);
                            devices[3]=sdPath;
                            // break;
                        }
                        else
                        {
                            //M2E,M1Pro  1USB
                            //Log.i("Eric","2023.06.06 current devices"+Build.MODEL);
                     //       if(Build.MODEL.contains("M2e"))
                            if(devices[1].equals(""))
                                devices[1]=(String) path.get(volimeInfo);
                            else
                                devices[2]=(String) path.get(volimeInfo);

                        }

                    }

                }


            }


        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //Log.i("Eric","SdPath="+sdPath);
        return sdPath;
    }

    public boolean isExistCard()
    {
        boolean result=false;
        StorageManager storageManager=(StorageManager) this.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz =null;
        Class<?> diskInfoClazz=null;

        try{
            storageVolumeClazz=Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList=storageManager.getClass().getMethod("getVolumeList");
            Method getPath=storageVolumeClazz.getMethod("getPath");
            // Method getType=storageVolumeClazz.getMethod("getType");
            Method isRemovable=storageVolumeClazz.getMethod("isRemovable");
            Method getStatus =storageVolumeClazz.getMethod("getState");

            Object obj=null;



            try{
                obj=getVolumeList.invoke(storageManager);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                //Log.i("Eric",e.toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                //Log.i("Eric",e.toString());
            }

            final int lengh= Array.getLength(obj);
            //Log.i("Eric","2222: "+lengh);


            for(int i=0;i<lengh;i++)
            {
                Object storageVolumeElement=Array.get(obj,i);
                String path=(String)getPath.invoke(storageVolumeElement);
                boolean removeable=(boolean)isRemovable.invoke(storageVolumeElement);
                String state=(String)getStatus.invoke(storageVolumeElement);
                // int type=(int)getType.invoke((storageVolumeElement));

                //Log.i("Eric","A: "+i+":"+path);
                //Log.i("Eric","A: "+i+":"+removeable);
                //Log.i("Eric","A: "+i+":"+state);
                ////Log.i("Eric","A: "+i+":"+type);
            }



        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            //Log.i("Eric",e.toString());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            //Log.i("Eric",e.toString());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            //Log.i("Eric",e.toString());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            //Log.i("Eric",e.toString());
        }

        return result;
    }

    class SDBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {



            //getSDcardPath();
            //hadleDeviceIcon();

            //Log.i("Eric","Eric 2023.05.17 !!!!!GGG:"+intent.getAction().toString());

            if(intent.getAction().equals("android.intent.action.MEDIA_EJECT"))
            {

                //Log.i("Eric","Eric 2023.05.17 !!!!!GG2:"+intent.getAction().toString());



            }

            if (intent.getAction().equals("android.intent.action.MEDIA_MOUNTED")) {

                //Log.i("Eric","eef:"+intent.getAction().toString());
                getSDcardPath();
                hadleDeviceIcon();



            }


            else if (intent.getAction().equals("android.intent.action.MEDIA_UNMOUNTED")) {

                try {
                    //   //Log.i("Eric","Eric 2023.05.17 !!!!!GGG3 :"+intent.getAction().toString());
                    //   //Log.i("Eric","05.24 get unmount !!!!!ffeef:"+intent.getAction().toString());
                    getSDcardPath();
                    ReflashFileList = true;
                    //Log.i("Eric","05.24 eef:"+PlayList.size() );

                    if (PlayList.size() > 0) {
                        //Log.i("Eric","05.24 eef2:"+PlayList.get(0).Path);

                        needclean = true;
                        for (int i = 0; i < devices.length; i++) {
                            //Log.i("Eric","05.19 2eef:"+devices[i] );
                            if (PlayList.get(0).Path.contains(devices[i])) {
                                //Log.i("Eric","05.19 find:"+devices[i] );
                                needclean = false;
                                break;
                            }

                        }


                        //Log.i("Eric","05.24 2eef:"+PlayList.size() );
                        //Log.i("Eric","05.24 2eef:"+needclean );
                        if (needclean) {
                            PlayList.clear();
                            PlayListDevices = 0;
                        }
                        handelPlayList();
                        if (PlayList.size() == 0) CurrenMediaType = 0;

                    }

                    currentdevice = 0;
                    hadleDeviceIcon();


                    if (RealMediaPlayer != null) {
                        // //Log.i("Eric","Eric 2023.05.17 !!!!!GG2:"+RealMediaPlayer.isPlaying());
                        // //Log.i("Eric","Eric 2023.05.17 !!!!!GG8:"+currentdevice);
                        // //Log.i("Eric","Eric 2023.05.17 !!!!!GG8:"+  PlayList.get(CurrenPlayListIndex).Path
                        // );


                        File playfile = new File(
                                PlayList.get(CurrenPlayListIndex).Path);

                        if (RealMediaPlayer.isPlaying()) {
                            //2023.06.
                            if (!playfile.exists()) {
                                RealMediaPlayer.stop();
                               // RealMediaPlayer.release();
                            }
                        }

                    }
                }
                catch (Exception e )
                {

                   // Log.i("Eric","error e"+e.toString());

                }
                }



        }
    }
    Boolean needclean;
    Boolean isUsbdevices;

    class USBBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // AllDeviceConnected();

            if(intent.hasExtra(UsbManager.EXTRA_PERMISSION_GRANTED)) {
                boolean permissionGranted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                // addText("permissionGranted : " + permissionGranted);
            }


            if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")  ||  intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_DETACHED")) {

                if(intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                    //Log.i("Eric", "20230130 attched");
                }
                else if(intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_DETACHED"))
                {
                    //Log.i("Eric","20230130 android.hardware.usb.action.USB_DEVICE_DETACHED");
                }


                UsbDevice usbDevice = (UsbDevice)intent.getExtras().get("device");

                //Toast.makeText(MainActivity.this, "usb device status changed  action="+intent.getAction()+" pid="+usbDevice.getProductId()+" vid="+usbDevice.getVendorId(), Toast.LENGTH_SHORT).show();
                //Log.i("Eric","usb device status changed  action="+intent.getAction()+" pid="+usbDevice.getProductId()+" vid="+usbDevice.getVendorId());
                //Log.i("Eric","usb device status changed  action="+intent.getAction()+" pid="+usbDevice.getDeviceName()+" vid="+usbDevice.getManufacturerName());

            }

            if (intent.getAction().equals("android.hardware.usb.action.USB_STATE")) {
                if (intent.getExtras().getBoolean("connected")) {
                    // usb 插入
                    //  Toast.makeText(MainActivity.this, "Eric usb insert", Toast.LENGTH_LONG).show();
                    //Log.i("Eric","Eric usb insert");
                } else {
                    //   usb 拔出
                    //Toast.makeText(MainActivity.this, "Eric usb pull out", Toast.LENGTH_LONG).show();
                    //Log.i("Eric","Eric usb  pull out");
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //Log.i("Eric", "keyevet code: " + keyCode);
        Log.i("Vs Player","!!!!! Key !!!!"+keyCode );

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP/*||keyCode == KeyEvent.KEYCODE_VOLUME_UP*/) {
            Toast.makeText(getApplicationContext(), "up", Toast.LENGTH_SHORT);
            //Log.i("Eric", "keyevet code: UP");


            if (InVideoViewUI || InAudioViewUI) {

                //Log.i("Eric", "2023.03.01 A");

                if (PlayControlUI.getVisibility() == View.INVISIBLE) {
                    //  PlayPauseButton.requestFocus();

                    //Log.i("Eric", "2023.03.01 B");
                    PlayControlUI.setVisibility(View.VISIBLE);
//                    PlayPauseButton.requestFocus();
                    long now = SystemClock.uptimeMillis();
                    long next = now + (5000);

                    Controlhandler.postAtTime(Crunable, next);
                    PlayPauseButton.requestFocus();
                    //    return true;

                } else {
                    //Log.i("Eric", "2023.03.01 C");
                    Controlhandler.removeCallbacks(Crunable);
                    long now = SystemClock.uptimeMillis();
                    long next = now + (5000);

                    Controlhandler.postAtTime(Crunable, next);

                }

                //   return true;
            } else if (InImageViewUI) {
                if (PlayControlUI_A.getVisibility() == View.INVISIBLE) {


                    PlayControlUI_A.setVisibility(View.VISIBLE);
                    PlayPauseButton_A.requestFocus();
                    long now = SystemClock.uptimeMillis();
                    long next = now + (5000);

                    //   Controlhandler.postAtTime(Crunable, next);

                } else {
                    Controlhandler.removeCallbacks(Crunable);
                    long now = SystemClock.uptimeMillis();
                    long next = now + (5000);

                    Controlhandler.postAtTime(Crunable, next);

                }

            }


            if (deviceicon1.hasFocus()) {
                /*
                if (PlayList.size() > 0) {
                  //  PlayListDevices = 1;
                    if(PlayListDevices == 1) {
                        FinalList1 = new ArrayList<MediaFile>();
                       // PlayList1 = new ArrayList<MediaFile>();
                        for (int i = 0; i < FinalList.size(); i++) {
                            FinalList1.add(FinalList.get(i));
                        }
                    }
                }*/
                return true;
            } else if (deviceicon2.hasFocus()) {
                ReflashFileList = true;
//                if(PlayList.size()>0&&PlayListDevices==0)
                if (PlayList.size() > 0) {
                    if(PlayListDevices == 2) {
                        //PlayListDevices = 2;
                        FinalList2 = new ArrayList<MediaFile>();
                        PlayList2 = new ArrayList<MediaFile>();
                        for (int i = 0; i < FinalList.size(); i++) {
                            FinalList2.add(FinalList.get(i));
                        }
                    }
                }


                deviceicon1.requestFocus();
                return true;
            } else if (deviceicon3.hasFocus()) {
                ReflashFileList = true;
                FinalList3 = new ArrayList<MediaFile>();
                if (PlayList.size() > 0) {
                    if (PlayListDevices == 3) {
                        //        PlayListDevices = 3;
                        for (int i = 0; i < FinalList.size(); i++) {
                            FinalList3.add(FinalList.get(i));
                        }
                    }
                }


                if (deviceicon2.getVisibility() == View.VISIBLE) deviceicon2.requestFocus();
                else deviceicon1.requestFocus();
                return true;
            }
         else if (deviceicon4.hasFocus()) {
            ReflashFileList = true;
            if (PlayList.size() > 0)
                {
               //     PlayListDevices = 4;
                    if(PlayListDevices == 4) {
                        FinalList4 = new ArrayList<MediaFile>();

                        for (int i = 0; i < FinalList.size(); i++) {
                            FinalList4.add(FinalList.get(i));
                        }

                    }
                }
                if (deviceicon3.getVisibility() == View.VISIBLE) deviceicon3.requestFocus();
                else if (deviceicon2.getVisibility() == View.VISIBLE) deviceicon2.requestFocus();
                  else deviceicon1.requestFocus();
                return true;
            } else if (PlayButton.hasFocus() || SeamButton.hasFocus()) {

                //Log.i("Eric","2023.06.08  "+playlist.getCount());


                return true;
            }
            else if (listview.hasFocus() ) {


                //Log.i("Eric","2023.-6.-7 UP"+listview.getSelectedItemPosition());

                return true;
            }

//2023.02.23
          /*
            if(InImageViewUI || InVideoViewUI)
            {
                //Change Repeat Mode
                if(RepeatMode==0)
                {
                    RepeatMode=1;
                    //Log.i("Eric","Reapeat All");
                    //Toast.makeText(getApplicationContext(),"Repeat All",Toast.LENGTH_SHORT);
                 //   Toast.makeText(getApplicationContext(),"Repeat All",Toast.LENGTH_SHORT);
                }else if( RepeatMode==1)
                {
                    RepeatMode=2;   Toast.makeText(getApplicationContext(),"Repeat one",Toast.LENGTH_SHORT);
                    //Log.i("Eric","Reapeat One");
                }
                else
                {
                    RepeatMode=0;   Toast.makeText(getApplicationContext(),"No Repeat",Toast.LENGTH_SHORT);
                    //Log.i("Eric","No Repeat");
                }
            }
            */

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN/*||keyCode == KeyEvent.KEYCODE_VOLUME_DOWN*/) {
            //    Toast.makeText(getApplicationContext(),"up",Toast.LENGTH_SHORT);
            //Log.i("Eric", "keyevet code: Down");

            if (InVideoViewUI || InAudioViewUI) {
                if (PlayControlUI.getVisibility() == View.INVISIBLE) {


                    PlayControlUI.setVisibility(View.VISIBLE);
                    PlayPauseButton.requestFocus();
                    long now = SystemClock.uptimeMillis();
                    long next = now + (5000);

                    Controlhandler.postAtTime(Crunable, next);

                } else {
                    Controlhandler.removeCallbacks(Crunable);
                    long now = SystemClock.uptimeMillis();
                    long next = now + (5000);

                    Controlhandler.postAtTime(Crunable, next);

                }
            } else if (InImageViewUI) {
                if (PlayControlUI_A.getVisibility() == View.INVISIBLE) {


                    PlayControlUI_A.setVisibility(View.VISIBLE);
                    PlayPauseButton_A.requestFocus();
                    long now = SystemClock.uptimeMillis();
                    long next = now + (5000);
                    Controlhandler.postAtTime(Crunable, next);

                } else {
                    Controlhandler.removeCallbacks(Crunable);
                    long now = SystemClock.uptimeMillis();
                    long next = now + (5000);

                    Controlhandler.postAtTime(Crunable, next);

                }

            }


            if (deviceicon1.hasFocus()) {

                if (PlayList.size() > 0) {
              //      PlayListDevices = 1;
                    //2023.06.06
                    if(PlayListDevices == 1) {
                        FinalList1 = new ArrayList<MediaFile>();
                        //        PlayList1=new ArrayList<MediaFile>();
                        for (int i = 0; i < FinalList.size(); i++) {
                            FinalList1.add(FinalList.get(i));
                        }
                    }

                }
                ReflashFileList = true;
                if (deviceicon2.getVisibility() == View.VISIBLE) deviceicon2.requestFocus();
                else if (deviceicon3.getVisibility() == View.VISIBLE) deviceicon3.requestFocus();
                else if (deviceicon4.getVisibility() == View.VISIBLE) deviceicon4.requestFocus();

                return true;
            } else if (deviceicon2.hasFocus()) {
                ReflashFileList = true;
                if (PlayList.size() > 0) {

               if(PlayListDevices == 2) {
                   //2023.06.06
                   FinalList2 = new ArrayList<MediaFile>();
                   // PlayList2=new ArrayList<MediaFile>();
                   for (int i = 0; i < FinalList.size(); i++) {
                       FinalList2.add(FinalList.get(i));
                   }
               }

                }
                if (deviceicon3.getVisibility() == View.VISIBLE) deviceicon3.requestFocus();
                else if (deviceicon4.getVisibility() == View.VISIBLE) deviceicon4.requestFocus();

                return true;
            } else if (deviceicon3.hasFocus()) {
                ReflashFileList = true;
                if (PlayList.size() > 0) {

                    if(PlayListDevices == 3) {
                        FinalList3 = new ArrayList<MediaFile>();

                        for (int i = 0; i < FinalList.size(); i++) {
                            FinalList3.add(FinalList.get(i));
                        }
                    }

                    }


                    if (deviceicon4.getVisibility() == View.VISIBLE) deviceicon4.requestFocus();
                    return true;
                } else if (deviceicon4.hasFocus()) {
                    if (PlayList.size() > 0) {

                        if(PlayListDevices == 4) {
                            FinalList4 = new ArrayList<MediaFile>();
                            PlayList1 = new ArrayList<MediaFile>();
                            for (int i = 0; i < FinalList.size(); i++) {
                                FinalList4.add(FinalList.get(i));
                            }
                        }
                        }
                        return true;
                    } else if (listview.hasFocus()) {
                        //Log.i("Eric", "listview.getCount()" + listview.getCount());
                        //Log.i("Eric", "listview.getCount()" + listview.getSelectedItemPosition());
                        //Log.i("Eric", "listview.getCount()" + listview.getSelectedItemId());
                        //Log.i("Eric", "listview.getCount()" + listview.getTransitionName());

                        //2  if(listview.getChildAt((listview.getCount()-2)).isSelected())
                        if (FinalList.size() < 0) {


                        }
                        //   setFileListBG(0,false);
                        return true;
                    } else if (PlayButton.hasFocus() || SeamButton.hasFocus()) {
                        return true;
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT/*||keyCode == KeyEvent.KEYCODE_VOLUME_DOWN*/) {
                    //Toast.makeText(getApplicationContext(),"up",Toast.LENGTH_SHORT);
                    //Log.i("Eric", "keyevet code: LEFT");

                    if (InVideoViewUI || InAudioViewUI) {

                        if (PlayControlUI.getVisibility() == View.INVISIBLE) {


                            PlayControlUI.setVisibility(View.VISIBLE);
                            PlayPauseButton.requestFocus();
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);

                            Controlhandler.postAtTime(Crunable, next);

                        } else {
                            Controlhandler.removeCallbacks(Crunable);
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);

                            Controlhandler.postAtTime(Crunable, next);

                        }
                    } else if (InImageViewUI) {
                        if (PlayControlUI_A.getVisibility() == View.INVISIBLE) {


                            PlayControlUI_A.setVisibility(View.VISIBLE);
                            PlayPauseButton_A.requestFocus();
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);

                            Controlhandler.postAtTime(Crunable, next);

                        } else {
                            Controlhandler.removeCallbacks(Crunable);
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);

                            Controlhandler.postAtTime(Crunable, next);

                        }

                    }


                    ReflashFileList = false;
                    if (deviceicon1.hasFocus()) {

                        return true;
                    } else if (deviceicon2.hasFocus()) {
                        return true;
                    } else if (deviceicon3.hasFocus()) {
                        return true;
                    } else if (deviceicon4.hasFocus()) {
                        return true;
                    } else if (listview.hasFocus()) {
                        filelistposition_Path = FinalList.get(listview.getSelectedItemPosition()).Path;

                        //Log.i("Eric", "Left " + listview.getSelectedItemPosition() + ":" + listview.getSelectedItemId());
                        if (currentdevice == 0) {
                            //Log.i("Eric", "1111" + FinalList.get(listview.getSelectedItemPosition()).Path);
                            // filelistposition=FinalList.get(listview.getSelectedItemPosition()).Path;


                            // filelistposition=listview.getSelectedItemPosition();
                            deviceicon1.requestFocus();
                            currentrow = 0;

                        } else if (currentdevice == 1) {
                            //   filelistposition=listview.getSelectedItemPosition();

                            deviceicon2.requestFocus();
                            currentrow = 0;

                        } else if (currentdevice == 2) {
                            //   filelistposition=listview.getSelectedItemPosition();

                            deviceicon3.requestFocus();
                            currentrow = 0;

                        } else if (currentdevice == 3) {


                            //Log.i("Eric", "1111" + FinalList.get(listview.getSelectedItemPosition()).Path);
                            // filelistposition=FinalList.get(listview.getSelectedItemPosition()).Path;

                            deviceicon4.requestFocus();
                            currentrow = 0;

                        }
                        // set setFileListBG(0,true);
                        //2023.04.21 //
                        //  setFileListBG(0,false);

                        return true;
                    } else if (PlayButton.hasFocus()) {

                        //Log.i("Eric", "2023.06.08 PlayButton.hasFocus() get LEFT " );


                /*
                if(!filelistposition_Path.equals("")) {
                    for (int i = listview.getFirstVisiblePosition(); i <= listview.getLastVisiblePosition(); i++) {
                        View v = null;
                        {
                            if (FinalList.get(i).Path.equals(filelistposition_Path)) {
                                //Log.i("Eric", "2023/02/28 i:" + i + ":" + listview.getFirstVisiblePosition() + ":" + listview.getLastVisiblePosition());
                                v = (View) listview.getChildAt(i - listview.getFirstVisiblePosition());
                            }
                        }
                        //View v=(View)listview.getChildAt(i-1);
                        if (v != null) {
                            LinearLayout l = (LinearLayout) v.findViewById(R.id.filerowly);
                            l.setBackgroundResource(R.drawable.list_hover);
                            continue;
                        }
                    }
                }
                else
                {
                    View  v = (View) listview.getChildAt(0);
                    LinearLayout l = (LinearLayout) v.findViewById(R.id.filerowly);
                    l.setBackgroundResource(R.drawable.list_hover);
                }

                 */
                        // deviceicon1.requestFocus();
                        //   listview.requestFocus();
                        //  return false;
                        return false;
                    }


                    //return false;


                    //  return true;

                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT/*||keyCode == KeyEvent.KEYCODE_VOLUME_DOWN*/) {
                    Toast.makeText(getApplicationContext(), "up", Toast.LENGTH_SHORT);
                    //Log.i("Eric", "keyevet code: right");


                    //if(InVideoViewUI||InImageViewUI) {
                    if (InVideoViewUI || InAudioViewUI) {
                        if (PlayControlUI.getVisibility() == View.INVISIBLE) {


                            PlayControlUI.setVisibility(View.VISIBLE);
                            PlayPauseButton.requestFocus();
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);

                            Controlhandler.postAtTime(Crunable, next);

                        } else {
                            Controlhandler.removeCallbacks(Crunable);
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);

                            Controlhandler.postAtTime(Crunable, next);

                        }

                    } else if (InImageViewUI) {
                        if (PlayControlUI_A.getVisibility() == View.INVISIBLE) {


                            PlayControlUI_A.setVisibility(View.VISIBLE);
                            PlayPauseButton_A.requestFocus();
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);

                            Controlhandler.postAtTime(Crunable, next);

                        } else {
                            Controlhandler.removeCallbacks(Crunable);
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);

                            Controlhandler.postAtTime(Crunable, next);

                        }

                    }


                    if (deviceicon1.hasFocus() || deviceicon2.hasFocus() || deviceicon3.hasFocus() || deviceicon4.hasFocus()) {
                        if (deviceicon1.hasFocus()) currentdevice = 0;
                        else if (deviceicon2.hasFocus()) currentdevice = 1;
                        else if (deviceicon3.hasFocus()) currentdevice = 2;
                        else if (deviceicon4.hasFocus()) currentdevice = 3;

                        //
                        // currentdevice=0;  //0 local 1 usb1 2 usb2 3 Sdcard
                        currentrow = 1;
                        listview.requestFocus();
                        if (currentdevice == 0) device1.setImageResource(R.drawable.storage_hover);
                        else if (currentdevice == 1) device2.setImageResource(R.drawable.usb_hover);
                        else if (currentdevice == 2) device3.setImageResource(R.drawable.usb_hover);
                        else if (currentdevice == 3)
                            device4.setImageResource(R.drawable.sd_storage_hover);


                        //2023.04.21 mark

                        if (!filelistposition_Path.equals("")) {
                            for (int i = listview.getFirstVisiblePosition(); i <= listview.getLastVisiblePosition(); i++) {
                                View v = null;
                                {
                                    if (FinalList.get(i).Path.equals(filelistposition_Path)) {
                                        //Log.i("Eric", "i:" + i + ":" + listview.getFirstVisiblePosition() + ":" + listview.getLastVisiblePosition());
                                        v = (View) listview.getChildAt(i - listview.getFirstVisiblePosition());
                                    }
                                }

                                if (v != null) {
                                    LinearLayout l = (LinearLayout) v.findViewById(R.id.filerowly);
                                    if (FinalList.get(i).Filetype > 1) {
                                        if (FinalList.get(i).ischose)
                                            l.setBackgroundResource(R.drawable.list_hover_choose);
                                        else
                                            l.setBackgroundResource(R.drawable.list_hover);
                                    } else {
                                        if (FinalList.get(i).ischose)
                                            l.setBackgroundResource(R.drawable.list_hover_choose);
                                        else
                                            l.setBackgroundResource(R.drawable.folder_hover);
                                    }

                                }
                            }
                        } else {
                            if (FinalList.size() > 0) {
                                View v = (View) listview.getChildAt(0);
                                LinearLayout l = (LinearLayout) v.findViewById(R.id.filerowly);
                                if (FinalList.get(0).Filetype > 1) {
                                    if (FinalList.get(0).ischose)
                                        l.setBackgroundResource(R.drawable.list_hover_choose);
                                    else
                                        l.setBackgroundResource(R.drawable.list_hover);
                                } else {
                                    if (FinalList.get(0).ischose)
                                        l.setBackgroundResource(R.drawable.list_hover_choose);
                                    else
                                        l.setBackgroundResource(R.drawable.folder_hover);
                                }
                            }
                        }


                        return true;
                    } else if (listview.hasFocus()) {

                        filelistposition_Path = FinalList.get(listview.getSelectedItemPosition()).Path;
                        //Log.i("Eric", "2023/02/08 :leaved item" + filelistposition_Path);

                        return false;
                    }

                    //  return true;

                } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME/*||keyCode == KeyEvent.KEYCODE_VOLUME_DOWN*/) {
                    //2023/02/14


                    //  textureView.setSurfaceTextureListener(null);
                    if (PlayControlUI.getVisibility() == View.VISIBLE) {

                        MiniListUI.setVisibility(View.INVISIBLE);
                        PlayControlUI.setVisibility(View.INVISIBLE);
                        SeekBarStart = false;
                        Controlhandler.removeCallbacksAndMessages(null);

                        return true;

                    } else if (PlayControlUI_A.getVisibility() == View.VISIBLE) {

                        MiniListUI_A.setVisibility(View.INVISIBLE);
                        PlayControlUI_A.setVisibility(View.INVISIBLE);
                        Controlhandler.removeCallbacksAndMessages(null);


                        return true;
                    } else if (!InVideoViewUI && !InImageViewUI && !InAudioViewUI) {

                        if (ParentPath.size() > 0) {
                            Boolean currenthasitem = true;

                            if (listview.getCount() == 0) {
                                currenthasitem = false;
                                //Log.i("Eric", "2023.02.6 ho item");
                            }
                            int lastparent = ParentPath.size() - 1;
                            File currentItem = new File(ParentPath.get(lastparent));

                            //    //Log.i("Eric", "11:" + currentItem.getPath());
                            //    //Log.i("Eric", "12:" + currentItem.getParent());

                            //Log.i("Eric", "2023/02/08 Bact to ParrentFolder CurrentMediaType:" + CurrenMediaType);
                        //    Log.i("Eric", "2023.07.04:" +currentItem.getPath());
                            getFileList(currentItem.getPath(), CurrenMediaType);
                            handleFileListChoice();

                            adapter = new FlieListBaseAdapter(FinalList, inflater);
                            CurrentFileListLevel--;
                            ParentPath.remove(lastparent);
                            listview.setAdapter(adapter);

                            Path.setText(currentItem.getPath());
/* ////back not clean PlayList
                if (PlayList.size() > 0) {
                    PlayList = new ArrayList<>();
                    llContent.removeAllViews();
                }
*/
                            if (!currenthasitem) {
                                listview.requestFocus();
                                if (currentdevice == 0)
                                    device1.setImageResource(R.drawable.storage_hover);
                                else if (currentdevice == 1)
                                    device2.setImageResource(R.drawable.usb_hover);
                                else if (currentdevice == 2)
                                    device3.setImageResource(R.drawable.usb_hover);
                                else if (currentdevice == 3)
                                    device4.setImageResource(R.drawable.sd_storage_hover);
                            }

                            return true;
                        } else {
                            if (PlayList.size() > 0) {
                                AlertDialog.Builder alertDialog =
                                        new AlertDialog.Builder(MainActivity.this);
                                alertDialog.setTitle("Media Player");
                                alertDialog.setMessage("Are you sure you want to exit App?");
                                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //   //Log.i("Eric", "Eric 2023.03.01 A Leave:" + Leave);
                                        Leave = true;
                                        // //Log.i("Eric", "Eric 2023.03.01 B Leave:" + Leave);
                                        mActivity.finish();
                                    }
                                });

                                alertDialog.setNeutralButton("No", (dialog, which) -> {
                                    Leave = false;
                                });
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                                //Log.i("Eric", "Eric 2023.03.01 Leave:" + Leave);
                                if (Leave) mActivity.finish();
                            }

                        }
                    } else if (InImageViewUI) {
                        //if (!PlayFromOutSide)
                        {
                            //
                            imageView.setImageDrawable(null);
                            imageView.setVisibility(View.INVISIBLE);
                            if (AudioUI.getVisibility() == View.VISIBLE)
                                AudioUI.setVisibility(View.INVISIBLE);
                            videoView.setVisibility(View.INVISIBLE);
                            PlayListUI.setVisibility(View.VISIBLE);
                            PlayButton.requestFocus();
                            InImageViewUI = false;
                            picturehandler.removeCallbacks(Prunable);


                            {
                                // listview.requestFocus();
                                if (currentdevice == 0)
                                    device1.setImageResource(R.drawable.storage_hover);
                                else if (currentdevice == 1)
                                    device2.setImageResource(R.drawable.usb_hover);
                                else if (currentdevice == 2)
                                    device3.setImageResource(R.drawable.usb_hover);
                                else if (currentdevice == 3)
                                    device4.setImageResource(R.drawable.sd_storage_hover);
                            }

                        }
                        /*
                        else {
                            mActivity.finish();
                            // picturehandler.removeCallbacks(Prunable);
                        }

                         */
                        return true;

                    } else if (InVideoViewUI) {
                        //Log.i("Eric", "2023.05.02 back invideoView");
                        //if (!PlayFromOutSide)
                        {
                            //  videoView.stopPlayback();
                            //  videoView.setVisibility(View.INVISIBLE);



                            PlayListUI.setVisibility(View.VISIBLE);
                            PlayControlUI.setVisibility(View.INVISIBLE);

                            Log.i("Eric","2023.06.29 Eric A");
//                            Log.i("Eric","2023.06.29 Eric");


                            //handelPlayList();

                            Log.i("Eric","2023.06.29 Eric B");
                            //   FFhandler.sendEmptyMessage(1);

                            if (RealMediaPlayer.isPlaying()) {   //RealMediaPlayer.seekTo(RealMediaPlayertDuration());
                                //Log.i("Eric", "2023.05.02 stop");
                                RealMediaPlayer.stop();
                               // RealMediaPlayer.release();
                                //   RealMediaPlayer.c
                            }
                            // RealMediaPlayer.reset();
                            if (textureView != null) {
                                //  textureView.getSurfaceTexture().release();
                                //textureView=null;
                            }

                            // textureView.setVisibility(View.INVISIBLE);
                            // textureView.setVisibility(View.GONE);
                            imageView2.setVisibility(View.VISIBLE);
                            VideoCover = true;
                            // RealMediaPlayer.setSurface(new Surface(textureView.getSurfaceTexture()));

                            TimeBarhandler.removeCallbacksAndMessages(null);
                            Controlhandler.removeCallbacksAndMessages(null);
                            FF2handler.removeCallbacksAndMessages(null);

                            if (imageView.getVisibility() == View.VISIBLE)
                                imageView.setVisibility(View.INVISIBLE);
                            if (AudioUI.getVisibility() == View.VISIBLE)
                                AudioUI.setVisibility(View.INVISIBLE);
                            PlayButton.requestFocus();
                            InVideoViewUI = false;
                            Controlhandler.removeCallbacksAndMessages(null);

                            //Log.i("Eric", "2023.05.20 currentdevice" + currentdevice);
                            ;

                            //  if (!currenthasitem) {
                            {
                               // listview.requestFocus();
                                if (currentdevice == 0)
                                    device1.setImageResource(R.drawable.storage_hover);
                                else if (currentdevice == 1)
                                    device2.setImageResource(R.drawable.usb_hover);
                                else if (currentdevice == 2)
                                    device3.setImageResource(R.drawable.usb_hover);
                                else if (currentdevice == 3)
                                    device4.setImageResource(R.drawable.sd_storage_hover);
                            }


                        }
                        /*
                        else {
                            mActivity.finish();
                        }

                         */

                        return true;
                    } else if (InAudioViewUI) {
                        //Log.i("Eric", "2023.05.02 back inAudioiew");
                        //if (!PlayFromOutSide)
                        {
                            //  videoView.stopPlayback();
                            //  videoView.setVisibility(View.INVISIBLE);
                            PlayListUI.setVisibility(View.VISIBLE);
                            PlayControlUI.setVisibility(View.INVISIBLE);
                            //   FFhandler.sendEmptyMessage(1);

                            if (RealMediaPlayer.isPlaying()) {   //RealMediaPlayer.seekTo(RealMediaPlayertDuration());
                                //Log.i("Eric", "2023.05.02 stop");
                                RealMediaPlayer.stop();
                            //    RealMediaPlayer.release();
                                //   RealMediaPlayer.c
                            }
                            // RealMediaPlayer.reset();
                            if (textureView != null) {
                                //  textureView.getSurfaceTexture().release();
                                //textureView=null;
                            }

                            // textureView.setVisibility(View.INVISIBLE);
                            // textureView.setVisibility(View.GONE);
                            imageView2.setVisibility(View.VISIBLE);
                            VideoCover = true;
                            // RealMediaPlayer.setSurface(new Surface(textureView.getSurfaceTexture()));

                            TimeBarhandler.removeCallbacksAndMessages(null);
                            Controlhandler.removeCallbacksAndMessages(null);
                            FF2handler.removeCallbacksAndMessages(null);

                            if (imageView.getVisibility() == View.VISIBLE)
                                imageView.setVisibility(View.INVISIBLE);
                            AudioUI.setVisibility(View.INVISIBLE);
                            PlayButton.requestFocus();

                            {
                                // listview.requestFocus();
                                if (currentdevice == 0)
                                    device1.setImageResource(R.drawable.storage_hover);
                                else if (currentdevice == 1)
                                    device2.setImageResource(R.drawable.usb_hover);
                                else if (currentdevice == 2)
                                    device3.setImageResource(R.drawable.usb_hover);
                                else if (currentdevice == 3)
                                    device4.setImageResource(R.drawable.sd_storage_hover);
                            }

                            InAudioViewUI = false;

                        }
                        /*
                        else {
                            mActivity.finish();
                        }
                        */
                        return true;
                    }
                } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE/*||keyCode == KeyEvent.KEYCODE_VOLUME_DOWN*/) {

                    if (InVideoViewUI || InAudioViewUI) {

                        PlayPauseButton.performClick();

                        if (!videoView.isPlaying()) {
                            if (PlayPauseButton.hasFocus())
                                PlayPauseButton.setBackgroundResource(R.drawable.playbt_hover_2);
                            else
                                PlayPauseButton.setBackgroundResource(R.drawable.playbt_2);
                            isPlaying = false;

                        } else {
                            if (PlayPauseButton.hasFocus())
                                PlayPauseButton.setBackgroundResource(R.drawable.pause_hover_2);
                            else
                                PlayPauseButton.setBackgroundResource(R.drawable.pause_2);
                            ;
                            isPlaying = true;
                        }


                        if (PlayControlUI.getVisibility() == View.INVISIBLE) {


                            PlayControlUI.setVisibility(View.VISIBLE);
                            //  MiniListUI.setVisibility(View.VISIBLE);

                            PlayPauseButton.requestFocus();
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);

                            Controlhandler.postAtTime(Crunable, next);

                        } else {
                            Controlhandler.removeCallbacks(Crunable);
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);

                            Controlhandler.postAtTime(Crunable, next);

                        }

                    } else if (InImageViewUI) {
                        PlayPauseButton_A.performClick();
                        if (PlayControlUI_A.getVisibility() == View.INVISIBLE) {


                            PlayControlUI_A.setVisibility(View.VISIBLE);
                            //  MiniListUI.setVisibility(View.VISIBLE);

                            PlayPauseButton_A.requestFocus();
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);
//
                            Controlhandler.postAtTime(Crunable, next);

                        } else {
                            Controlhandler.removeCallbacks(Crunable);
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);

                            Controlhandler.postAtTime(Crunable, next);

                        }


                    } else {
                        PlayButton.performClick();

                    }


                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
                    if (InVideoViewUI || InAudioViewUI) {
                        isPlaying = false;

                        if (videoView.isPlaying()) isPlaying = true;

                        NextButton.performClick();

                    } else if (InImageViewUI) {
                        NextButton_A.performClick();
                    }
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
                    if (InVideoViewUI || InAudioViewUI) {
                        LastButton.performClick();
                    } else if (InImageViewUI) {
                        LastButton_A.performClick();
                    }
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                    // if(InVideoViewUI||InImageViewUI) {
                    //  Log.i("Eric","Eric 2023.07.05  A Key Center"+keyCode );

                    if (InVideoViewUI || InAudioViewUI) {
                    //    Log.i("Eric","Eric 2023.07.05 B Key Center"+keyCode );

                        if (PlayControlUI.getVisibility() == View.INVISIBLE) {

                     //       Log.i("Eric","Eric 2023.07.05 c Key Center"+keyCode );
                            PlayControlUI.setVisibility(View.VISIBLE);
                            PlayPauseButton.requestFocus();
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);

                            Controlhandler.postAtTime(Crunable, next);

                        } else {
                       //     Log.i("Eric","Eric 2023.07.05 d Key Center"+keyCode );
                            Controlhandler.removeCallbacks(Crunable);
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);

                            Controlhandler.postAtTime(Crunable, next);

                        }
                    } else if (InImageViewUI) {
                        if (PlayControlUI_A.getVisibility() == View.INVISIBLE) {


                            PlayControlUI_A.setVisibility(View.VISIBLE);
                            PlayPauseButton_A.requestFocus();
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);

                            Controlhandler.postAtTime(Crunable, next);

                        } else {
                            Controlhandler.removeCallbacks(Crunable);
                            long now = SystemClock.uptimeMillis();
                            long next = now + (5000);

                            Controlhandler.postAtTime(Crunable, next);

                        }

                    }
            return true;
                }
                return super.onKeyDown(keyCode, event);
            }

            ///// 2023.04.27
            TextureView.SurfaceTextureListener f = new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                    //Log.i("Eric", "2023.04.27 onSurfaceTextureAvailable A");
                    RealMediaPlayer.setSurface(new Surface(surfaceTexture));
                    try {
                        //Log.i("Eric", "2023.05.08 AA  preparesync C");
                        //     RealMediaPlayer.prepareAsync();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                    //Log.i("Eric", "2023.04.28 onSurfaceTextureSizeChanged");
                }

                @Override
                public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                    //Log.i("Eric", "2023.04.28 onSurfaceTextureDestroyed");
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
                    ////Log.i("Eric","2023.04.28 onSurfaceTextureupdate");
                    if (!HasData) HasData = true;
                    if (imageView2.getVisibility() == View.VISIBLE && VideoCover == false) {

                        //Log.i("Eric", "2023.04.28  !!!!!!!!!!!!!!!!! onSurfaceTextureupdate");
                        //Log.i("Eric", "2023.05.19  !!!!!!!!!!!!!!!!!" + VideoCover + "V2 disapear");
                        imageView2.setVisibility(View.INVISIBLE);
                    }
                }
            };


}

