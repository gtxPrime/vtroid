package com.gtxprime.vtroid.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gtxprime.vtroid.Activities.PreviewActivity;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.model.StatusModel;
import com.gtxprime.vtroid.model.WhatsappStatusModel;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@SuppressLint({"ViewHolder", "InflateParams"})
public class MyStatusAdapter extends BaseAdapter {
    Context context;
    List<WhatsappStatusModel> arrayList;
    int width;
    LayoutInflater inflater;
    public OnCheckboxListener onCheckboxListener;

    public MyStatusAdapter(Context context, List<WhatsappStatusModel> arrayList, OnCheckboxListener onCheckboxListener) {
        this.context = context;
        this.arrayList = arrayList;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels; // width of the device

        this.onCheckboxListener = onCheckboxListener;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int arg0, View arg1, ViewGroup arg2) {

        View grid = inflater.inflate(R.layout.row_my_status, null);

        ImageView play = grid.findViewById(R.id.play);

        if (isVideoFile(arrayList.get(arg0).getPath())) {
            play.setVisibility(View.VISIBLE);
        } else {
            play.setVisibility(View.GONE);
        }

        grid.setLayoutParams(new GridView.LayoutParams((width * 320 / 1080), (width * 390 / 1080)));
        ImageView imageView = grid.findViewById(R.id.gridImageVideo);
        Glide.with(context).load(arrayList.get(arg0).getPath()).placeholder(R.drawable.placeholder).into(imageView);

        CheckBox checkbox = grid.findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            arrayList.get(arg0).setSelected(isChecked);
            if (onCheckboxListener != null) {
                onCheckboxListener.onCheckboxListener(buttonView, arrayList);
            }
        });
        checkbox.setChecked(arrayList.get(arg0).isSelected());

        grid.setOnClickListener(view -> {
            Intent intent = new Intent(context, PreviewActivity.class);
            intent.putParcelableArrayListExtra("images", (ArrayList<? extends Parcelable>) arrayList);
            intent.putExtra("position", arg0);
            intent.putExtra("statusdownload", "download");
            context.startActivity(intent);

        });

        return grid;
    }


    public interface OnCheckboxListener {
        void onCheckboxListener(View view, List<WhatsappStatusModel> list);
    }

    public boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

}
