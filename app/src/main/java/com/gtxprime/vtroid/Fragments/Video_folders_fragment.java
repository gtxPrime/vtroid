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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.model.ModelMediaFiles;
import com.gtxprime.vtroid.Adapters.AdapterFolder;

import java.util.ArrayList;

@SuppressLint({"NotifyDataSetChanged", "SetJavaScriptEnabled"})
public class Video_folders_fragment extends Fragment {

    ArrayList<ModelMediaFiles> mediaFilesAdapters = new ArrayList<>();
    ArrayList<String> allFolderList = new ArrayList<>();
    RecyclerView recyclerView;
    AdapterFolder adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    Cursor cursor;
    String sortOrder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        recyclerView = view.findViewById(R.id.folderRecycler);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshFolder);


        swipeRefreshLayout.setOnRefreshListener(() -> {
            showFolders();
            swipeRefreshLayout.setRefreshing(false);
        });

        showFolders();

        return view;
    }

    private void showFolders() {
        mediaFilesAdapters = fetchMedia();
        adapter = new AdapterFolder(mediaFilesAdapters, allFolderList, getContext());
        //adapter = new VideoFoldersAdapter(mediaFilesAdapters, allFolderList, FolderActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter.notifyDataSetChanged();
    }

    public ArrayList<ModelMediaFiles> fetchMedia() {
        ArrayList<ModelMediaFiles> mediaFilesAdapters = new ArrayList<>();
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

                int index = path.lastIndexOf("/");
                String subString = path.substring(0, index);
                if (!allFolderList.contains(subString)) {
                    allFolderList.add(subString);
                }
                mediaFilesAdapters.add(mediaFilesAdapter);
            } while (cursor.moveToNext());
        }
        return mediaFilesAdapters;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
    }
}