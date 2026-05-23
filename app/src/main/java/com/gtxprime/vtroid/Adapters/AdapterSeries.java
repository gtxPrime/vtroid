package com.gtxprime.vtroid.Adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Activities.WebActivity;
import com.gtxprime.vtroid.model.seriesModel;

import java.util.ArrayList;

public class AdapterSeries extends RecyclerView.Adapter<AdapterSeries.ViewHolder> {

    Context context;
    ArrayList<seriesModel> seriesModels;

    public AdapterSeries(Context context, ArrayList<seriesModel> seriesModel) {
        this.context = context;
        this.seriesModels = seriesModel;
    }

    @NonNull
    @Override
    public AdapterSeries.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.holder_stream, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSeries.ViewHolder holder, int position) {
        seriesModel series = seriesModels.get(position);
        Glide.with(context).load(series.getImgLink()).placeholder(R.drawable.placeholder).centerCrop().into(holder.poster);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, WebActivity.class);
            intent.putExtra("links", series.getLink());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return seriesModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.posterHolder);
        }
    }
}
