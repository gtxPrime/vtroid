package com.gtxprime.vtroid.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.Player;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Utils.Utils;
import com.gtxprime.vtroid.VideoPlayer.PlayerActivity;
import com.gtxprime.vtroid.model.StatusModel;
import com.gtxprime.vtroid.model.WhatsappStatusModel;

import java.io.File;
import java.util.ArrayList;

public class PreviewAdapter extends PagerAdapter {
    Context context;
    ArrayList<WhatsappStatusModel> imageList;

    public PreviewAdapter(Context context, ArrayList<WhatsappStatusModel> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.preview_list_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        ImageView iconplayer = itemView.findViewById(R.id.iconplayer);

        File fileItem = new File(imageList.get(position).getPath());

        try {
            String extension = fileItem.getName().substring(fileItem.getName().lastIndexOf("."));
            if (extension.equals(".mp4")) {
                iconplayer.setVisibility(View.VISIBLE);
            } else {
                iconplayer.setVisibility(View.GONE);
            }
            Glide.with(context).load(fileItem.getPath()).placeholder(R.drawable.placeholder).into(imageView);
        } catch (Exception ignored) {
        }

        iconplayer.setOnClickListener(view -> {
            String extension = fileItem.getName().substring(fileItem.getName().lastIndexOf("."));
            if (extension.equals(".mp4")) {
                context.startActivity(new Intent(context, PlayerActivity.class).putExtra("playLink", imageList.get(position).getPath()));
            }
//
        });
        container.addView(itemView);

        return itemView;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
