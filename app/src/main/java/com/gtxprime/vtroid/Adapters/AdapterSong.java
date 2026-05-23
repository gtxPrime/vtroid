package com.gtxprime.vtroid.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Services.AudioPlayerService;
import com.gtxprime.vtroid.model.MusicModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterSong extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<MusicModel> songs;
    ExoPlayer player;
    ConstraintLayout playerViewHolder, bottomNavFolder, homeWrapper;
    StyledPlayerView playerView;

    public AdapterSong(Context context, List<MusicModel> songs, ExoPlayer player, ConstraintLayout playerViewHolder,
                       StyledPlayerView playerView, ConstraintLayout bottomNavFolder, ConstraintLayout homeWrapper) {
        this.context = context;
        this.songs = songs;
        this.player = player;
        this.playerViewHolder = playerViewHolder;
        this.playerView = playerView;
        this.homeWrapper = homeWrapper;
        this.bottomNavFolder = bottomNavFolder;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_music_item, parent,false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MusicModel song = songs.get(position);
        SongViewHolder viewHolder = (SongViewHolder) holder;

        viewHolder.name.setText(song.getTitle());
        Uri artworkUri = song.getArtworkUri();
        if (artworkUri !=null){
            Glide.with(context).asBitmap().load(artworkUri).error(R.drawable.music_thumb).into(viewHolder.artWorkUri);
        } else {
            Glide.with(context).load(R.drawable.album).into(viewHolder.artWorkUri);
        }

        viewHolder.itemView.setOnClickListener(view -> {

            context.startService(new Intent(context.getApplicationContext(), AudioPlayerService.class));

            assert player != null;
            if (!player.isPlaying()){
                player.setMediaItems(getMediaItems(), position, 0);
            } else {
                player.pause();
                player.seekTo(position, 0);
            }

            playerView.setPlayer(player);
            playerViewHolder.setVisibility(View.VISIBLE);
            bottomNavFolder.setVisibility(View.GONE);
            homeWrapper.setVisibility(View.GONE);

            playerViewHolder.animate().translationY(0).setDuration(200);
            homeWrapper.animate().translationY(homeWrapper.getHeight()).setDuration(200);
            bottomNavFolder.animate().translationY(bottomNavFolder.getHeight()).setDuration(200);

            player.prepare();
            player.play();


        });

    }

    private List<MediaItem> getMediaItems() {
        List<MediaItem> mediaItems = new ArrayList<>();
        for (MusicModel song : songs){
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(song.getUri())
                    .setMediaMetadata(getMetadata(song))
                    .build();
            mediaItems.add(mediaItem);
        }
        return mediaItems;
    }

    private MediaMetadata getMetadata(MusicModel song) {
        return new MediaMetadata.Builder()
                .setTitle(song.getTitle())
                .setArtworkUri(song.getArtworkUri())
                .setArtist(song.getArtistName())
                .build();
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView artWorkUri;
        TextView name;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            artWorkUri = itemView.findViewById(R.id.music_img);
            name = itemView.findViewById(R.id.music_file_name);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterSongs(List<MusicModel> filteredSongs){
        songs = filteredSongs;
        notifyDataSetChanged();
    }


}
