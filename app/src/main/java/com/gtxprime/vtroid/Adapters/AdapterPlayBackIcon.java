package com.gtxprime.vtroid.Adapters;

import static android.content.Context.MODE_PRIVATE;
import static com.gtxprime.vtroid.Activities.Settings.themeColor;
import static com.gtxprime.vtroid.Activities.Settings.themeColorPrefName;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.model.IconModel;

import java.util.ArrayList;

public class AdapterPlayBackIcon extends RecyclerView.Adapter<AdapterPlayBackIcon.ViewHolder> {
    private ArrayList<IconModel> iconModelsList;
    private Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;

    }

    public AdapterPlayBackIcon(ArrayList<IconModel> iconModelsList, Context context) {
        this.iconModelsList = iconModelsList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterPlayBackIcon.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.holder_playback_icon, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPlayBackIcon.ViewHolder holder, int position) {

        holder.icon.setImageResource(iconModelsList.get(position).getImageView());

        SharedPreferences preferences = context.getSharedPreferences(themeColorPrefName, MODE_PRIVATE);
        int savedColor = preferences.getInt(themeColor, context.getColor(R.color.primary));
        holder.icon.setColorFilter(savedColor);
    }

    @Override
    public int getItemCount() {
        return iconModelsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            icon = itemView.findViewById(R.id.playback_icon);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
