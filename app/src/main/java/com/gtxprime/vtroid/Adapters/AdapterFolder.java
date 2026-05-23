package com.gtxprime.vtroid.Adapters;

import static android.content.Context.MODE_PRIVATE;
import static com.gtxprime.vtroid.Activities.Settings.themeColor;
import static com.gtxprime.vtroid.Activities.Settings.themeColorPrefName;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Activities.VideoFiles;
import com.gtxprime.vtroid.model.ModelMediaFiles;

import java.util.ArrayList;

public class AdapterFolder extends RecyclerView.Adapter<AdapterFolder.ViewHolder2> {

    ArrayList<ModelMediaFiles> mediaFilesAdapters;
    ArrayList<String> folderPath;
    Context context;
    Cursor cursor;

    public AdapterFolder(ArrayList<ModelMediaFiles> mediaFilesAdapters, ArrayList<String> folderPath, Context context) {
        this.mediaFilesAdapters = mediaFilesAdapters;
        this.folderPath = folderPath;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.holder_folder_item, parent, false);
        return new ViewHolder2(view);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder2 holder, int position) {
        int indexPath = folderPath.get(position).lastIndexOf("/");
        String nameOfFolder = folderPath.get(position).substring(indexPath + 1);
        ArrayList<ModelMediaFiles> videoFilesArrayList;
        videoFilesArrayList = fetchMedia(nameOfFolder, context);
        AdapterFolderRecent recentAdapter = new AdapterFolderRecent(videoFilesArrayList, context);
        recentAdapter.notifyDataSetChanged();

        holder.folderName.setText(nameOfFolder);
        holder.number.setText(noOfFiles(nameOfFolder) + " videos");
        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, VideoFiles.class).putExtra("folderName", nameOfFolder)));
        holder.recyclerView.setAdapter(recentAdapter);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

        SharedPreferences preferences = context.getSharedPreferences(themeColorPrefName, MODE_PRIVATE);
        int savedColor = preferences.getInt(themeColor, context.getColor(R.color.primary));
        holder.imageHead.setColorFilter(savedColor);
    }

    @Override
    public int getItemCount() {
        return folderPath.size();
    }

    public static class ViewHolder2 extends RecyclerView.ViewHolder {
        public TextView folderName, number;
        RecyclerView recyclerView;
        ImageView imageHead;

        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.videoFragHeading);
            number = itemView.findViewById(R.id.filesNumber);
            recyclerView = itemView.findViewById(R.id.videoFragVideoRec);
            imageHead = itemView.findViewById(R.id.imageHead);

        }
    }

    int noOfFiles(String folder_name) {
        int files_no = 0;
        for (ModelMediaFiles mediaFilesAdapter : mediaFilesAdapters) {
            if (mediaFilesAdapter.getPath().substring(0, mediaFilesAdapter.getPath().lastIndexOf("/"))
                    .endsWith(folder_name)) {
                files_no++;
            }

        }
        return files_no;
    }

    private ArrayList<ModelMediaFiles> fetchMedia(String folderName, Context context) {
        String sortOrder;

        ArrayList<ModelMediaFiles> videoFiles = new ArrayList<>(3);
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        sortOrder = MediaStore.MediaColumns.DATE_ADDED + " DESC";


        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArg = new String[]{"%" + folderName + "%"};
        cursor = context.getContentResolver().query(uri, null, selection, selectionArg, sortOrder);


        if (cursor != null && cursor.moveToNext()) {
            do {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                @SuppressLint("Range") String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                @SuppressLint("Range") String dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                ModelMediaFiles mediaFilesAdapter = new ModelMediaFiles(id, title, displayName, size, duration, path, dateAdded);

                videoFiles.add(mediaFilesAdapter);

            } while (cursor.moveToNext());
        }

        return videoFiles;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        cursor.close();
    }
}
