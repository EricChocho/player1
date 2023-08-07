package com.viewsonic.vsplayer1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class PlayListBaseAdapter extends BaseAdapter {

  //  private ArrayList<String> ElementsData ;   //資料
  private ArrayList<MediaFile> ElementsData ;   //資料
    private LayoutInflater inflater;    //

    static class ViewHolder{
        LinearLayout itembackground;
        TextView itemID;
        TextView itemName;
        TextView itemTime;
        //int id;

    }


    //初始化
    public PlayListBaseAdapter(ArrayList<MediaFile> data, LayoutInflater inflater){
        this.ElementsData = data;
        this.inflater = inflater;

    }

    @Override
    public int getCount() {
        return ElementsData.size();
    }

    @Override
    public Object getItem(int position) {
        return ElementsData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        if(view==null)
        {
            holder=new ViewHolder();
            view=inflater.inflate(R.layout.playlist_row, null);
            holder.itemID=(TextView) view.findViewById(R.id.playrowID);
            holder.itemName=(TextView) view.findViewById(R.id.playrowName);
            holder.itemTime=(TextView) view.findViewById(R.id.playrowTime);
            holder.itembackground=(LinearLayout) view.findViewById(R.id.filerowly);
            holder.itemName.setSelected(true);
            view.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)view.getTag();
        }

        holder.itemName.setText(ElementsData.get(i).Name);
        if(ElementsData.get(i).Timetext.equals(""))
            holder.itemTime.setText("12:12.33");
        else
            holder.itemTime.setText(ElementsData.get(i).Timetext);

        if(i+1<10)
        {StringBuilder s=new StringBuilder();
            s.append("0");s.append(Integer.toString(i+1));
        holder.itemID.setText(s);
        }
        else holder.itemID.setText(Integer.toString(i+1));
        /*
        if(ElementsData.get(i).ischose)
         holder.itembackground.setBackgroundResource(R.drawable.list_choose);
        else
            holder.itembackground.setBackgroundResource(R.drawable.list);

         */
        return view;

    }
}
