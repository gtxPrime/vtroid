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

import java.io.File;
import java.util.ArrayList;

public class AdapterRecent extends RecyclerView.Adapter<AdapterRecent.ViewHolder22> {
    private final ArrayList<ModelMediaFiles> videoList;
    private final Context context;

    public AdapterRecent(ArrayList<ModelMediaFiles> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder22 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.holder_recent_items, parent, false);
        return new ViewHolder22(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder22 holder, @SuppressLint("RecyclerView") int position) {
        holder.videoName.setText(videoList.get(position).getDisplayName());

        holder.itemView.setOnClickListener(v -> {
            com.gtxprime.vtroid.Utils.VideoListHolder.videoList = videoList;
            context.startActivity(new Intent(context, PlayerActivity.class)
                    .putExtra("LocalPlayLink", videoList.get(position).getPath())
                    .putExtra("position", position)
                    .putExtra("title", videoList.get(position).getDisplayName()));

        });

        Glide.with(context).asBitmap().load(new File(videoList.get(position).getPath())).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class ViewHolder22 extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView videoName;

        public ViewHolder22(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.recent_img);
            videoName = itemView.findViewById(R.id.recent_file_name);
        }
    }


}
