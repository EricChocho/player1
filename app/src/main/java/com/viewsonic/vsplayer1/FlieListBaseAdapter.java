package com.viewsonic.vsplayer1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class FlieListBaseAdapter extends BaseAdapter {

  //  private ArrayList<String> ElementsData ;   //資料
  private ArrayList<MediaFile> ElementsData ;   //資料
    private LayoutInflater inflater;    //

    static class ViewHolder{
        LinearLayout itembackground;
        TextView itemName;
        int id;

    }


    //初始化
    public FlieListBaseAdapter(ArrayList<MediaFile> data, LayoutInflater inflater){
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
            view=inflater.inflate(R.layout.filelist_row, null);
            holder.itemName=(TextView) view.findViewById(R.id.filerowtext);
            holder.itembackground=(LinearLayout) view.findViewById(R.id.filerowly);
            view.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)view.getTag();
        }

        holder.itemName.setText(ElementsData.get(i).Name);
        holder.id=i;


        /*
        if(ElementsData.get(i).ischose)
         holder.itembackground.setBackgroundResource(R.drawable.list_choose);
        else
            holder.itembackground.setBackgroundResource(R.drawable.list);

         */
        return view;

    }
}
