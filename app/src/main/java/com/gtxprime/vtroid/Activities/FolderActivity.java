package com.gtxprime.vtroid.Activities;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.gtxprime.vtroid.Fragments.Videos_fragament.mediaFilesAdapters;
import static com.gtxprime.vtroid.Utils.Utils.setToast;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.Window;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.gtxprime.vtroid.Adapters.AdapterViewPager;
import com.gtxprime.vtroid.Fragments.Video_folders_fragment;
import com.gtxprime.vtroid.Fragments.Videos_fragament;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Utils.Utils;
import com.gtxprime.vtroid.model.ModelMediaFiles;

import java.io.File;
import java.util.ArrayList;

public class FolderActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ImageView vMenu, vSearch, crossV;
    public static final String MY_PREFS = "my prefs";
    Transition transition;
    SearchView videoSearch;
    ConstraintLayout vToolbar, searchHolderV, extraHolderV;
    ConstraintLayout videoHome, videoMusic;
    public static ArrayList<ModelMediaFiles> videoFiles = new ArrayList<>();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);

        findViewById(R.id.homeFrag).setBackground(Utils.bgGrayGenerate(this));
        Utils.setPad(findViewById(R.id.homeFrag), "bottom", this);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        videoSearch = findViewById(R.id.videoSearch);
        videoHome = findViewById(R.id.vH);
        videoMusic = findViewById(R.id.vM);
        vMenu = findViewById(R.id.vMenu);
        transition = new Fade();
        transition.setDuration(500);
        transition.addTarget(R.id.searchHolderV);
        transition.addTarget(R.id.extraHolderV);
        vToolbar = findViewById(R.id.vToolbar);
        searchHolderV = findViewById(R.id.searchHolderV);
        extraHolderV = findViewById(R.id.extraHolderV);
        vSearch = findViewById(R.id.vSearch);
        crossV = findViewById(R.id.crossV);

        videoSearch.setOnQueryTextListener(this);

        vMenu.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(FolderActivity.this, vMenu);
            popupMenu.getMenuInflater().inflate(R.menu.video_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                SharedPreferences preferences = getSharedPreferences(MY_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                int id = item.getItemId();
                if (id == R.id.refresh_files) {
                    finish();
                    startActivity(getIntent());
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (id == R.id.sort_by) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FolderActivity.this);
                    dialogBuilder.setTitle("Sort By");
                    dialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
                        editor.apply();
                        finish();
                        startActivity(getIntent());
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        dialog.dismiss();
                    });
                    String[] items = {"Name (A to Z)", "Size (Large to Small)", "Date (New to Old)", "Length (Long to Short)"};
                    dialogBuilder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    editor.putString("sort", "sortName");
                                    break;
                                case 1:
                                    editor.putString("sort", "sortSize");
                                    break;
                                case 2:
                                    editor.putString("sort", "sortDate");
                                    break;
                                case 3:
                                    editor.putString("sort", "sortLength");
                                    break;
                            }
                        }
                    });
                    dialogBuilder.create().show();
                } else if (id == R.id.downloadsM) {
                    Dialog downloadDialog = new Dialog(FolderActivity.this);
                    downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    downloadDialog.setCancelable(true);
                    downloadDialog.setContentView(R.layout.dialog_download);
                    TextView dowYes = downloadDialog.findViewById(R.id.dowYes);
                    TextView dowCancel = downloadDialog.findViewById(R.id.dowCancel);
                    EditText dowText = downloadDialog.findViewById(R.id.dowText);
                    dowYes.setOnClickListener(view1 -> {
                        if (dowText.getText().toString().trim().startsWith("https") || dowText.getText().toString().trim().startsWith("http")) {
                            startDownloadVideo(dowText.getText().toString(), FolderActivity.this, URLUtil.guessFileName(dowText.getText().toString(), null, null));
                        } else {
                            Toast.makeText(FolderActivity.this, "Enter a valid url", Toast.LENGTH_SHORT).show();
                        }
                        downloadDialog.dismiss();
                        downloadDialog.cancel();
                    });
                    dowCancel.setOnClickListener(view1 -> {
                        downloadDialog.cancel();
                        downloadDialog.dismiss();
                    });
                    downloadDialog.setCancelable(false);
                    downloadDialog.show();
                }
                return true;

            });
            popupMenu.show();
        });

        vSearch.setOnClickListener(view -> {
            TransitionManager.beginDelayedTransition(vToolbar, transition);
            searchHolderV.setVisibility(View.VISIBLE);
            extraHolderV.setVisibility(View.GONE);
        });

        videoHome.setOnClickListener(v -> {
            startActivity(new Intent(FolderActivity.this, Home.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        videoMusic.setOnClickListener(v -> {
            startActivity(new Intent(FolderActivity.this, MusicPlayer.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        crossV.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(vToolbar, transition);
            searchHolderV.setVisibility(View.GONE);
            extraHolderV.setVisibility(View.VISIBLE);

        });

        TabLayout tabLayout = findViewById(R.id.videoFragTab);
        ViewPager viewPager = findViewById(R.id.videoFragPager);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        AdapterViewPager viewPagerAdapter = new AdapterViewPager(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new Videos_fragament(), "Video");
        viewPagerAdapter.addFragment(new Video_folders_fragment(), "Folder");

        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String inputs = newText.toLowerCase();
        ArrayList<ModelMediaFiles> mediaFiles = new ArrayList<>();
        for (ModelMediaFiles media : mediaFilesAdapters) {
            if (media.getTitle().toLowerCase().contains(inputs)) {
                mediaFiles.add(media);
            }
        }
        Videos_fragament.recentAdapter.updateVideoFilesX(mediaFiles);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchHolderV.getVisibility() == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(vToolbar, transition);
            searchHolderV.setVisibility(View.GONE);
            extraHolderV.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    public static void startDownloadVideo(String uriLink, Context context, String FileName) {
        setToast(context, context.getResources().getString(R.string.download_started));
        Uri uri = Uri.parse(uriLink); // Path where you want to download file.
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
        request.setTitle(FileName + ""); // Title for notification.
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, FileName);  // Storage directory path
        ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading

        try {
            MediaScannerConnection.scanFile(context, new String[]{new File(DIRECTORY_DOWNLOADS + "/" + FileName).getAbsolutePath()},
                    null, (path, uri1) -> {
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            // your code
            Toast.makeText(ctxt, "Download Complete", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    };

}