package com.viewsonic.vsplayer1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.selection.Selection;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<String> mDataList;
    private LayoutInflater mInflater;
    //private ItemClickListener mClickListener;


    MyRecyclerViewAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mDataList = data;
    }

    @NonNull
    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.filelist_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapter.ViewHolder holder, int position) {
        String dddd = mDataList.get(position);
        holder.myTextView.setText(dddd);


    }

    @Override
    public int getItemCount() {
        return 0;
    }





    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;
        LinearLayout myRowly;
        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.filerowtext);
            myRowly=itemView.findViewById(R.id.filerowly);
            //  itemView.setOnClickListener(this);
        }
    }


}





