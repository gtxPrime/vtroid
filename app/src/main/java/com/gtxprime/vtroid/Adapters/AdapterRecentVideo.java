package com.gtxprime.vtroid.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.gtxprime.vtroid.Activities.DeleteActivity;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.VideoPlayer.PlayerActivity;
import com.gtxprime.vtroid.model.ModelMediaFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class AdapterRecentVideo extends RecyclerView.Adapter<AdapterRecentVideo.ViewHolder22> {
    private ArrayList<ModelMediaFiles> videoList;
    private final Context context;
    private final Activity activity;
    double milliSeconds = 0.0;
    BottomSheetDialog bottomSheetDialog;


    public AdapterRecentVideo(ArrayList<ModelMediaFiles> videoList, Context context, Activity activity) {
        this.videoList = videoList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder22 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.holder_video_file, parent, false);
        return new ViewHolder22(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder22 holder, @SuppressLint("RecyclerView") int position) {
        holder.videoName.setText(videoList.get(position).getDisplayName());

        holder.videoName.setText(videoList.get(position).getDisplayName());
        String size = videoList.get(position).getSize();
        holder.videoSize.setText(android.text.format.Formatter.formatFileSize(context, Long.parseLong(size)));

        if (videoList.get(position).getDuration() != null) {
            milliSeconds = Double.parseDouble(videoList.get(position).getDuration());
        } else {
            milliSeconds = 0.0;
        }

        holder.videoDuration.setText(timeConversion((long) milliSeconds));


        holder.menu_more.setOnClickListener(v -> {
            bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme);
            View bsView = LayoutInflater.from(context).inflate(R.layout.layout_video_bs, v.findViewById(R.id.bottom_sheet));

            bsView.findViewById(R.id.bs_play).setOnClickListener(v16 -> {
                holder.itemView.performClick();
                bottomSheetDialog.dismiss();
            });

            bsView.findViewById(R.id.bs_rename).setOnClickListener(v15 -> {
                bottomSheetDialog.dismiss();
                Dialog renameDialog;
                renameDialog = new Dialog(context);
                renameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                renameDialog.setCancelable(true);
                renameDialog.setContentView(R.layout.dialog_rename);

                TextView renameYes = renameDialog.findViewById(R.id.renameYes);
                TextView renameCancel = renameDialog.findViewById(R.id.renameCancel);
                EditText renamedText = renameDialog.findViewById(R.id.renamedText);
                String path = videoList.get(position).getPath();
                final File file = new File(path);
                String originalName = file.getName();
                originalName = originalName.substring(0, originalName.lastIndexOf("."));
                renamedText.setText(originalName);
                renamedText.requestFocus();

                renameYes.setOnClickListener(v151 -> {
                    String onlyPath = Objects.requireNonNull(file.getParentFile()).getAbsolutePath();
                    String ext = file.getAbsolutePath();
                    ext = ext.substring(ext.lastIndexOf("."));
                    String newPath = onlyPath + "/" + renamedText.getText().toString() + ext;
                    File newFile = new File(newPath);
                    boolean rename = file.renameTo(newFile);
                    if (rename) {
                        ContentResolver resolver = context.getApplicationContext().getContentResolver();
                        resolver.delete(MediaStore.Files.getContentUri("external"),
                                MediaStore.MediaColumns.DATA + "=?", new String[]
                                        {file.getAbsolutePath()});
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.putExtra("video_title", holder.videoName.getText().toString());
                        intent.setData(Uri.fromFile(newFile));
                        context.getApplicationContext().sendBroadcast(intent);


                        videoList.get(position).setPath(newPath);
                        holder.videoName.setText(String.format("%s%s", renamedText.getText().toString(), ext));
                        notifyDataSetChanged();
                        Toast.makeText(context, "Video Renamed", Toast.LENGTH_SHORT).show();

                        SystemClock.sleep(200);
                        ((Activity) context).recreate();
                    } else {
                        Toast.makeText(context, "Process failed", Toast.LENGTH_SHORT).show();
                    }
                });
                renameCancel.setOnClickListener(v12 -> renameDialog.dismiss());
                renameDialog.show();
            });

            bsView.findViewById(R.id.bs_share).setOnClickListener(v1 -> {
                Uri uri = Uri.parse(videoList.get(position).getPath());
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("video/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                context.startActivity(Intent.createChooser(shareIntent, "Share Video via"));
                bottomSheetDialog.dismiss();
            });

            bsView.findViewById(R.id.bs_delete).setOnClickListener(v13 -> {
                bottomSheetDialog.dismiss();
                com.gtxprime.vtroid.Utils.VideoListHolder.videoList = videoList;
                context.startActivity(new Intent(context, DeleteActivity.class)
                        .putExtra("position", position)
                        .putExtra("isDelete", true));
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            });

            bsView.findViewById(R.id.bs_properties).setOnClickListener(v14 -> {
                bottomSheetDialog.dismiss();
                com.gtxprime.vtroid.Utils.VideoListHolder.videoList = videoList;
                context.startActivity(new Intent(context, DeleteActivity.class)
                        .putExtra("position", position));
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });

            bottomSheetDialog.setContentView(bsView);
            bottomSheetDialog.show();

        });

        holder.itemView.setOnLongClickListener(v -> {
            holder.menu_more.performClick();
            return false;
        });

        holder.itemView.setOnClickListener(v -> {
            com.gtxprime.vtroid.Utils.VideoListHolder.videoList = videoList;
            context.startActivity(new Intent(context, PlayerActivity.class)
                    .putExtra("LocalPlayLink", videoList.get(position).getPath())
                    .putExtra("position", position)
                    .putExtra("title", videoList.get(position).getDisplayName()));


        });

        Glide.with(context).asBitmap().load(new File(videoList.get(position).getPath())).placeholder(R.drawable.placeholder).into(holder.thumbnail);
    }


    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class ViewHolder22 extends RecyclerView.ViewHolder {
        ImageView thumbnail, menu_more;
        TextView videoName, videoSize, videoDuration;

        public ViewHolder22(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            menu_more = itemView.findViewById(R.id.video_menu_more);
            videoName = itemView.findViewById(R.id.video_name);
            videoSize = itemView.findViewById(R.id.video_size);
            videoDuration = itemView.findViewById(R.id.video_duration);
        }
    }

    @SuppressLint("DefaultLocale")
    public String timeConversion(long value) {
        String videoTime;
        int duration = (int) value;
        int hrs = (duration / 3600000);
        int mns = (duration / 60000) % 60000;
        int scs = duration % 60000 / 1000;

        if (hrs > 0) {
            videoTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            videoTime = String.format("%02d:%02d", mns, scs);
        }
        return videoTime;

    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateVideoFilesX(ArrayList<ModelMediaFiles> files) {
        videoList = new ArrayList<>();
        videoList.addAll(files);
        notifyDataSetChanged();
    }

}
