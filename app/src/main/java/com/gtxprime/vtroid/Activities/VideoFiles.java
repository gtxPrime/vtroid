package com.gtxprime.vtroid.Activities;

import static com.gtxprime.vtroid.Activities.FolderActivity.MY_PREFS;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gtxprime.vtroid.Adapters.AdapterVideoFiles;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Utils.Utils;
import com.gtxprime.vtroid.model.ModelMediaFiles;

import java.util.ArrayList;

public class VideoFiles extends AppCompatActivity {
    RecyclerView recyclerView;
    public static ArrayList<ModelMediaFiles> videoFilesArrayList = new ArrayList<>();
    public static AdapterVideoFiles videoFilesAdapter;
    public String folder_name;
    SwipeRefreshLayout swipeRefreshLayout;
    String sortOrder;
    Cursor cursor;
    TextView folderNameTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_files);

        findViewById(R.id.avfv).setBackground(Utils.bgGrayGenerate(this));
        Utils.setPad(findViewById(R.id.avfv), "bottom", this);

        recyclerView = findViewById(R.id.videoRecycler);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshVideo);
        folderNameTag = findViewById(R.id.folderNameTag);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            showVideoFiles();
            swipeRefreshLayout.setRefreshing(false);
        });

        folder_name = getIntent().getStringExtra("folderName");
        folderNameTag.setText(getIntent().getStringExtra("folderShow"));

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS, MODE_PRIVATE).edit();
        editor.putString("playlistFolderName", folder_name);
        editor.apply();

        showVideoFiles();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void showVideoFiles() {
        videoFilesArrayList = fetchMedia(folder_name);
        videoFilesAdapter = new AdapterVideoFiles(videoFilesArrayList, this, 0, VideoFiles.this);
        recyclerView.setAdapter(videoFilesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        videoFilesAdapter.notifyDataSetChanged();

    }

    private ArrayList<ModelMediaFiles> fetchMedia(String folderName) {
        SharedPreferences preferences = getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        String sort_value = preferences.getString("sort", "abcd");

        ArrayList<ModelMediaFiles> videoFiles = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        switch (sort_value) {
            case "sortName":
                sortOrder = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;
            case "sortSize":
                sortOrder = MediaStore.MediaColumns.SIZE + " DESC";
                break;
            case "sortDate":
                sortOrder = MediaStore.MediaColumns.DATE_ADDED + " DESC";
                break;
            default:
                sortOrder = MediaStore.Video.Media.DURATION + " DESC";
                break;
        }

        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArg = new String[]{"%" + folderName + "%"};
        cursor = getContentResolver().query(uri, null, selection, selectionArg, sortOrder);

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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
    }
}