package com.gtxprime.vtroid.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.gtxprime.vtroid.Activities.FolderActivity.MY_PREFS;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gtxprime.vtroid.Adapters.AdapterRecentVideo;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.model.ModelMediaFiles;

import java.util.ArrayList;

@SuppressLint({"NotifyDataSetChanged", "SetJavaScriptEnabled"})
public class Videos_fragament extends Fragment {

    RecyclerView recyclerView;
    public static ArrayList<ModelMediaFiles> mediaFilesAdapters = new ArrayList<>();
    public static AdapterRecentVideo recentAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    String sortOrder;
    public static AppCompatActivity activity;
    Cursor cursor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        recyclerView = view.findViewById(R.id.VideoRecycler);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshVideo);
        activity = (AppCompatActivity) getActivity();


        swipeRefreshLayout.setOnRefreshListener(() -> {
            showFolders();
            swipeRefreshLayout.setRefreshing(false);
        });

        showFolders();

        return view;
    }

    private void showFolders() {
        mediaFilesAdapters = fetchRecentMedia();
        recentAdapter = new AdapterRecentVideo(mediaFilesAdapters, getContext(), getActivity());
        //adapter = new VideoFoldersAdapter(mediaFilesAdapters, allFolderList, FolderActivity.this);
        recyclerView.setAdapter(recentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        showFolders();
    }

    private ArrayList<ModelMediaFiles> fetchRecentMedia() {

        ArrayList<ModelMediaFiles> videoFiles = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        SharedPreferences preferences = requireActivity().getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        String sort_value = preferences.getString("sort", "abcd");

        switch (sort_value) {
            case "sortName":
                sortOrder = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
                break;
            case "sortSize":
                sortOrder = MediaStore.MediaColumns.SIZE + " DESC";
                break;
            case "sortLength":
                sortOrder = MediaStore.Video.Media.DURATION + " DESC";
                break;
            default:
                sortOrder = MediaStore.MediaColumns.DATE_ADDED + " DESC";
                break;
        }

        cursor = requireActivity().getContentResolver().query(uri, null, null, null, sortOrder);

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
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
    }
}