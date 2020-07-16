package com.adaptive.exoplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mayur Solanki (mayursolanki120@gmail.com) on 25/02/19, 5:48 PM.
 */
public class SampleVideoAdapter extends RecyclerView.Adapter<SampleVideoAdapter.MyViewHolder> { //implements Filterable

    List<VideoModel> videosList;
    Context context;



    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvVideoTitle,tvVideoUrl;




        public MyViewHolder(View view) {
            super(view);

            tvVideoTitle = view.findViewById(R.id.tv_video_title);
            tvVideoUrl = view.findViewById(R.id.tv_video_url);

        }
    }


    public SampleVideoAdapter(Context context) {
        this.context = context;
        this.videosList = new ArrayList<>();




    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        VideoModel videoModel = videosList.get(position);
        holder.tvVideoTitle.setText(videoModel.getVideoName());
        holder.tvVideoUrl.setText(videoModel.getVideoUrl());



    }



    @Override
    public int getItemCount() {
        return videosList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public VideoModel getItem(int position) {
        return videosList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void addItems(List<VideoModel> lst) {
        this.videosList = lst;
        notifyDataSetChanged();
    }





}




