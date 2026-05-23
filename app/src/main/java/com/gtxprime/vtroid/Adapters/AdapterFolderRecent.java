package com.gtxprime.vtroid.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.VideoPlayer.PlayerActivity;
import com.gtxprime.vtroid.model.ModelMediaFiles;

import java.util.ArrayList;

public class AdapterFolderRecent extends RecyclerView.Adapter<AdapterFolderRecent.ViewHolder22> {
    private final ArrayList<ModelMediaFiles> videoList;
    private final Context context;
    double milliSeconds = 0.0;
    public AdapterFolderRecent(ArrayList<ModelMediaFiles> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder22 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.holder_video_item, parent, false);
        return new ViewHolder22(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder22 holder, @SuppressLint("RecyclerView") int position) {

        if (videoList.get(position).getDuration() != null) {
            milliSeconds = Double.parseDouble(videoList.get(position).getDuration());
        } else {
            milliSeconds = 0.0;
        }

        holder.itemView.setOnClickListener(v -> {
            com.gtxprime.vtroid.Utils.VideoListHolder.videoList = videoList;
            context.startActivity(new Intent(context, PlayerActivity.class)
                    .putExtra("LocalPlayLink", videoList.get(position).getPath())
                    .putExtra("position", position)
                    .putExtra("title", videoList.get(position).getDisplayName()));

        });

        Glide.with(context).load(videoList.get(position).getPath()).placeholder(R.drawable.placeholder).into(holder.thumbnail);
        holder.duration.setText(timeConversion((long) milliSeconds));

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class ViewHolder22 extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView duration;

        public ViewHolder22(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.recent_img);
            duration = itemView.findViewById(R.id.videoFrag_duration);
        }
    }

    @SuppressLint("DefaultLocale")
    public String timeConversion(long value) {
        String videoTime;
        int duraton = (int) value;
        int hrs = (duraton / 3600000);
        int mns = (duraton / 60000) % 60000;
        int scs = duraton % 60000 / 1000;

        if (hrs > 0) {
            videoTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            videoTime = String.format("%02d:%02d", mns, scs);
        }
        return videoTime;

    }


}
