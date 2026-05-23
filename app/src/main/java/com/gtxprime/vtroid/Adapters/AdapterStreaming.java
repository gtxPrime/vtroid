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
import com.gtxprime.vtroid.model.movieModel;

import java.util.ArrayList;

public class AdapterStreaming extends RecyclerView.Adapter<AdapterStreaming.ViewHolder> {

    Context context;
    ArrayList<movieModel> mMovie;

    public AdapterStreaming(Context context, ArrayList<movieModel> mMovie) {
        this.context = context;
        this.mMovie = mMovie;
    }

    @NonNull
    @Override
    public AdapterStreaming.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.holder_stream, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterStreaming.ViewHolder holder, int position) {
        movieModel movie = mMovie.get(position);
        Glide.with(context).load(movie.getImgLink()).placeholder(R.drawable.placeholder).centerCrop().into(holder.poster);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra("links", movie.getLink());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovie.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.posterHolder);
        }
    }
}
