package com.gtxprime.vtroid.Activities;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.DIRECTORY_MUSIC;
import static com.gtxprime.vtroid.Services.AudioPlayerService.isRunning;
import static com.gtxprime.vtroid.Services.AudioPlayerService.player;
import static com.gtxprime.vtroid.Utils.Utils.setToast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.gtxprime.vtroid.Adapters.AdapterSong;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Services.AudioPlayerService;
import com.gtxprime.vtroid.Utils.Utils;
import com.gtxprime.vtroid.model.MusicModel;
import com.marcinmoskala.arcseekbar.ArcSeekBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;

@SuppressLint("StaticFieldLeak")
public class MusicPlayer extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static String MyArtPref, MyArtistPref, MyTitlePref;
    public static List<MusicModel> allSongs = new ArrayList<>();
    public static boolean isActivityRunning;
    public static StyledPlayerView playerView;
    int defaultStatusColor, repeatMode = 1;
    boolean isBound = false;
    SharedPreferences.Editor artEditor, artistEditor, titleEditor;
    SharedPreferences artPref, artistPref, titlePref;
    ActivityResultLauncher<String> storageLauncher;
    String[] permissionsList;
    ImageView search, cross, artWork, backBtn, icAudio, homePrev, homePlayPauseBtn, homeNext, exo_repeat_toggle, exo_shuffle, exo_next, exo_prev, exoPlayPauseMain, bgImg;
    ConstraintLayout extraHolder, searchHolder, mToolbar, homeWrapper, playerWrapper, bottomNavFolder;
    Transition transition;
    SearchView searchText;
    RecyclerView recyclerView;
    AdapterSong songAdapter;
    TextView songName, artistName, homeSongName, dowYes, dowCancel;
    DefaultTimeBar exo_progress;
    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;
    String artUri, artistUri, titleUri;
    ArcSeekBar seekArc;
    Dialog downloadDialog;
    EditText dowText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_music_player);
        isActivityRunning = true;

        findViewById(R.id.homeFrag).setBackground(Utils.bgGrayGenerate(this));
        Utils.setPad(findViewById(R.id.homeFrag), "bottom", this);
        Utils.setPad(findViewById(R.id.extraHolder), "top", this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsList = new String[]{android.Manifest.permission.READ_MEDIA_AUDIO,
                    android.Manifest.permission.READ_MEDIA_VIDEO, android.Manifest.permission.POST_NOTIFICATIONS, android.Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissionsList = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }

        defaultStatusColor = getWindow().getStatusBarColor();
        getWindow().setNavigationBarColor(ColorUtils.setAlphaComponent(defaultStatusColor, 199));

        transition = new Fade();
        //player = new ExoPlayer.Builder(this).build();
        cross = findViewById(R.id.cross);
        extraHolder = findViewById(R.id.extraHolder);
        searchHolder = findViewById(R.id.searchHolder);
        mToolbar = findViewById(R.id.homeFrag);
        search = findViewById(R.id.mSearch);
        searchText = findViewById(R.id.OpSearchText);
        exo_repeat_toggle = findViewById(R.id.exo_repeat_toggle);
        exo_shuffle = findViewById(R.id.exo_shuffle);
        exo_next = findViewById(R.id.exo_next);
        exo_prev = findViewById(R.id.exo_prev);
        exo_progress = findViewById(R.id.exo_progress);
        exoPlayPauseMain = findViewById(R.id.mainPlayPause);
        icAudio = findViewById(R.id.icAudio);
        playerView = findViewById(R.id.exoplayer_view);
        songName = findViewById(R.id.song_name);
        artistName = findViewById(R.id.song_artist);
        homeNext = findViewById(R.id.homeNextBtn);
        homePrev = findViewById(R.id.homePreviousBtn);
        homeSongName = findViewById(R.id.homeSongNameView);
        homePlayPauseBtn = findViewById(R.id.homePlayPauseBtn);
        homeWrapper = findViewById(R.id.HomeControlWrapper);
        playerWrapper = findViewById(R.id.playerViewHolder);
        artWork = findViewById(R.id.artwork);
        backBtn = findViewById(R.id.back_btn);
        bottomNavFolder = findViewById(R.id.bottomNavFolder);
        seekArc = findViewById(R.id.seekArc);
        bgImg = findViewById(R.id.bgImg);

        recyclerView = findViewById(R.id.musicRecycler);
        storageLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) {
                fetchSongs();
            }
        });

        ActivityCompat.requestPermissions(this, permissionsList, Utils.perRequest);

        transition.setDuration(500);
        transition.addTarget(R.id.searchHolder);
        transition.addTarget(R.id.extraHolder);
        search.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(mToolbar, transition);
            searchHolder.setVisibility(View.VISIBLE);
            extraHolder.setVisibility(View.GONE);
        });

        searchText.setOnQueryTextListener(this);
        cross.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(mToolbar, transition);
            searchHolder.setVisibility(View.GONE);
            extraHolder.setVisibility(View.VISIBLE);

        });

        findViewById(R.id.mV).setOnClickListener(v -> {
            startActivity(new Intent(MusicPlayer.this, FolderActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        findViewById(R.id.mH).setOnClickListener(v -> {
            startActivity(new Intent(MusicPlayer.this, Home.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        findViewById(R.id.mDownloads).setOnClickListener(view -> {
            downloadDialog = new Dialog(MusicPlayer.this);
            downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            downloadDialog.setCancelable(true);
            downloadDialog.setContentView(R.layout.dialog_download);
            dowYes = downloadDialog.findViewById(R.id.dowYes);
            dowCancel = downloadDialog.findViewById(R.id.dowCancel);
            dowText = downloadDialog.findViewById(R.id.dowText);
            dowYes.setOnClickListener(view12 -> {
                if (dowText.getText().toString().trim().startsWith("https") || dowText.getText().toString().trim().startsWith("http")) {
                    startDownloadMusic(dowText.getText().toString(), MusicPlayer.this, URLUtil.guessFileName(dowText.getText().toString(), null, null));
                } else {
                    Toast.makeText(MusicPlayer.this, "Enter a valid url", Toast.LENGTH_SHORT).show();
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
        });

        //Bind player services
        doBinding();

        if (isRunning && player != null && player.getCurrentMediaItem() != null) {
            artPref = getSharedPreferences(MyArtPref, MODE_PRIVATE);

            showCurrentArtwork();

            artistPref = getSharedPreferences(MyArtistPref, MODE_PRIVATE);
            titlePref = getSharedPreferences(MyTitlePref, MODE_PRIVATE);

            songName.setText(titlePref.getString(titleUri, "<Unknown>"));
            homeSongName.setText(titlePref.getString(titleUri, "<Unknown>"));
            artistName.setText(artistPref.getString(artistUri, "<Unknown Artist>"));
            playerView.setPlayer(player);

            if (player.isPlaying()) {
                homePlayPauseBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.avd_play_to_pause, null));
            }
        }
    }


    private void doBinding() {
        Intent intent = new Intent(this, AudioPlayerService.class);
        bindService(intent, playerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection playerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isBound = true;
            playerView.setPlayer(player);
            playerControls();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public void playerControls() {
        //exitPlayer
        backBtn.setOnClickListener(v -> exitPlayer());
        //openPlayer
        homeWrapper.setOnClickListener(v -> showPlayer());

        player.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                if (reason != ExoPlayer.COMMAND_STOP) {
                    showCurrentArtwork();
                    songName.setText(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.title);
                    artistName.setText(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.artist);
                    homeSongName.setText(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.title);

                    artEditor = getSharedPreferences(MyArtPref, MODE_PRIVATE).edit();
                    artEditor.putString(artUri, String.valueOf(Objects.requireNonNull(player.getCurrentMediaItem().mediaMetadata.artworkUri)));
                    artEditor.apply();

                    artistEditor = getSharedPreferences(MyArtistPref, MODE_PRIVATE).edit();
                    artistEditor.putString(artistUri, String.valueOf(Objects.requireNonNull(player.getCurrentMediaItem().mediaMetadata.artist)));
                    artistEditor.apply();

                    titleEditor = getSharedPreferences(MyTitlePref, MODE_PRIVATE).edit();
                    titleEditor.putString(titleUri, String.valueOf(Objects.requireNonNull(player.getCurrentMediaItem().mediaMetadata.title)));
                    titleEditor.apply();
                }
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if (playbackState == ExoPlayer.STATE_READY) {
                    showCurrentArtwork();
                    songName.setText(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.title);
                    artistName.setText(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.artist);
                    homeSongName.setText(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.title);

                    artEditor = getSharedPreferences(MyArtPref, MODE_PRIVATE).edit();
                    artEditor.putString(artUri, String.valueOf(Objects.requireNonNull(player.getCurrentMediaItem().mediaMetadata.artworkUri)));
                    artEditor.apply();

                    artistEditor = getSharedPreferences(MyArtistPref, MODE_PRIVATE).edit();
                    artistEditor.putString(artistUri, String.valueOf(Objects.requireNonNull(player.getCurrentMediaItem().mediaMetadata.artist)));
                    artistEditor.apply();

                    titleEditor = getSharedPreferences(MyTitlePref, MODE_PRIVATE).edit();
                    titleEditor.putString(titleUri, String.valueOf(Objects.requireNonNull(player.getCurrentMediaItem().mediaMetadata.title)));
                    titleEditor.apply();

//               } else {
//                   homePlayPauseBtn.setImageResource(R.drawable.avd_play_to_pause);
//                   exoPlayPauseMain.setImageResource(R.drawable.avd_play_to_pause);
                }
            }
        });

        homePlayPauseBtn.setOnClickListener(v -> onPlayPausePress(homePlayPauseBtn));
        homeNext.setOnClickListener(v -> onNextPress());
        homePrev.setOnClickListener(v -> onPrevPress());
        exoPlayPauseMain.setOnClickListener(v -> onPlayPausePress(exoPlayPauseMain));
        exo_repeat_toggle.setOnClickListener(v -> {
            if (repeatMode == 1) {
                player.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
                repeatMode = 2;
            } else if (repeatMode == 2) {
                player.setShuffleModeEnabled(true);
                player.setRepeatMode(Player.REPEAT_MODE_ALL);
                repeatMode = 3;
            } else if (repeatMode == 3) {
                player.setRepeatMode(Player.REPEAT_MODE_ALL);
                player.setShuffleModeEnabled(false);
                repeatMode = 0;
            } else if (repeatMode == 0) {
                player.setRepeatMode(Player.REPEAT_MODE_OFF);
                repeatMode = 1;
            }
        });
        if (player == null) {
            homeWrapper.setVisibility(View.GONE);
        }

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        seekArc.setMaxProgress(maxVolume);

        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekArc.setProgress(currentVolume);
        seekArc.setOnProgressChangedListener(i -> audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0));

        updateColors();

    }

    private void showCurrentArtwork() {
        Glide.with(MusicPlayer.this).load(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.artworkUri).placeholder(R.color.black).error(R.color.black).apply(new RequestOptions().centerCrop())
                .transform(new BlurTransformation(18, 3)).into(bgImg);

        Glide.with(getApplicationContext()).load(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.artworkUri).error(R.drawable.music_thumb).into(artWork);

        Glide.with(getApplicationContext()).load(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.artworkUri)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        updateColors();
                        return false;
                    }
                }).error(R.drawable.music_thumb).into(icAudio);

    }

    private void showPlayer() {
        playerWrapper.setVisibility(View.VISIBLE);
        bottomNavFolder.setVisibility(View.GONE);
        homeWrapper.setVisibility(View.GONE);

        playerWrapper.animate().translationY(0).setDuration(200);
        homeWrapper.animate().translationY(homeWrapper.getHeight()).setDuration(200);
        bottomNavFolder.animate().translationY(bottomNavFolder.getHeight()).setDuration(200);
        updateColors();
    }

    public void updateColors() {
        BitmapDrawable bitmapDrawable;
        if (playerWrapper.getVisibility() == View.GONE) {
            bitmapDrawable = (BitmapDrawable) artWork.getDrawable();
        } else {
            bitmapDrawable = (BitmapDrawable) icAudio.getDrawable();
        }

        if (bitmapDrawable == null) {
            bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.music_thumb);
        }

        assert bitmapDrawable != null;
        Bitmap bmp = bitmapDrawable.getBitmap();

        Palette.from(bmp).generate(palette -> {
            if (palette != null) {
                Palette.Swatch swatch = palette.getDarkVibrantSwatch();
                if (swatch == null) {
                    swatch = palette.getMutedSwatch();
                    if (swatch == null) {
                        swatch = palette.getDominantSwatch();
                    }
                }

                assert swatch != null;
                int titleColor = swatch.getTitleTextColor();
                int bodyColor = swatch.getBodyTextColor();
                int rgbColor = swatch.getRgb();

                if (playerWrapper.getVisibility() == View.VISIBLE) {
                    getWindow().setNavigationBarColor(rgbColor);
                }
                songName.setTextColor(Color.rgb(255, 255, 255));
                artistName.setTextColor(Color.rgb(255, 255, 255));

                exo_repeat_toggle.getDrawable().setTint(bodyColor);
                exo_shuffle.getDrawable().setTint(bodyColor);
                exo_prev.getDrawable().setTint(bodyColor);
                exo_next.getDrawable().setTint(bodyColor);

                exo_progress.setPlayedColor(titleColor);
                exo_progress.setUnplayedColor(Color.WHITE);
                homeWrapper.setBackgroundColor(rgbColor);

                seekArc.setProgressColor(rgbColor);
                seekArc.setProgressBackgroundColor(Color.WHITE);
                findViewById(R.id.card).setBackgroundTintList(ColorStateList.valueOf(titleColor));

            }
        });
    }

    private void exitPlayer() {
        playerWrapper.setVisibility(View.GONE);
        homeWrapper.setVisibility(View.VISIBLE);
        bottomNavFolder.setVisibility(View.VISIBLE);

        playerWrapper.animate().translationY(playerWrapper.getHeight()).setDuration(200);
        homeWrapper.animate().translationY(0).setDuration(200);
        bottomNavFolder.animate().translationY(0).setDuration(200);

        getWindow().setStatusBarColor(defaultStatusColor);
        getWindow().setNavigationBarColor(ColorUtils.setAlphaComponent(defaultStatusColor, 199));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Utils.perRequest) {
            if (Utils.hasPermissions(this, permissionsList)) {
                ActivityCompat.requestPermissions(this, permissionsList, Utils.perRequest);
            } else {
                fetchSongs();

            }
        }
    }

    private void fetchSongs() {
        List<MusicModel> song = new ArrayList<>();
        Uri mediaStoreUri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mediaStoreUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST
        };

        String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";

        try (Cursor cursor = getContentResolver().query(mediaStoreUri, projection, null, null, sortOrder)) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                long albumId = cursor.getLong(albumColumn);
                String artist = cursor.getString(artistColumn);

                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                Uri albumUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
                name = name.substring(0, name.lastIndexOf("."));

                MusicModel songs = new MusicModel(name, uri, albumUri, artist);
                song.add(songs);

            }
            showSongs(song);

        }

    }

    private void showSongs(List<MusicModel> song) {
        if (song.size() == 0) {
            Toast.makeText(this, "No songs found", Toast.LENGTH_SHORT).show();
            return;
        }
        allSongs.clear();
        allSongs.addAll(song);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        songAdapter = new AdapterSong(this, song, player, playerWrapper, playerView, bottomNavFolder, homeWrapper);
        //recyclerView.setAdapter(songAdapter);

        SlideInLeftAnimationAdapter slideInRightAnimationAdapter = new SlideInLeftAnimationAdapter(songAdapter);
        slideInRightAnimationAdapter.setDuration(500);
        slideInRightAnimationAdapter.setInterpolator(new OvershootInterpolator());
        slideInRightAnimationAdapter.setFirstOnly(false);
        recyclerView.setAdapter(slideInRightAnimationAdapter);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filterSongs(newText.toLowerCase());
        return true;
    }

    private void filterSongs(String query) {
        List<MusicModel> filteredList = new ArrayList<>();
        if (allSongs.size() > 0) {
            for (MusicModel song : allSongs) {
                if (song.getTitle().toLowerCase().contains(query)) {
                    filteredList.add(song);
                }
            }

            if (songAdapter != null) {
                songAdapter.filterSongs(filteredList);
            }

        }
    }

    private void doUnbind() {
        if (isBound) {
            unbindService(playerServiceConnection);
            isBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbind();
        isActivityRunning = false;
    }

    @Override
    public void onBackPressed() {
        if (playerWrapper.getVisibility() == View.VISIBLE) {
            exitPlayer();
        } else {
            super.onBackPressed();
        }
    }

    public void onPrevPress() {
        if (player.hasPreviousMediaItem()) {
            player.seekToPrevious();
        } else {
            homePrev.setEnabled(false);
        }
    }

    public void onNextPress() {
        if (player.hasNextMediaItem()) {
            player.seekToNext();
        } else {
            homeNext.setEnabled(false);
        }
    }

    public void onPlayPausePress(ImageView holder) {
        if (player.isPlaying()) {
            holder.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.avd_pause_to_play, null));
            Drawable drawable = holder.getDrawable();
            player.pause();

            if (drawable instanceof AnimatedVectorDrawableCompat) {
                avd = (AnimatedVectorDrawableCompat) drawable;
                avd.start();
            } else if (drawable instanceof AnimatedVectorDrawable) {
                avd2 = (AnimatedVectorDrawable) drawable;
                avd2.start();
            }
        } else {
            holder.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.avd_play_to_pause, null));
            Drawable drawable = holder.getDrawable();
            player.play();

            if (drawable instanceof AnimatedVectorDrawableCompat) {
                avd = (AnimatedVectorDrawableCompat) drawable;
                avd.start();
            } else if (drawable instanceof AnimatedVectorDrawable) {
                avd2 = (AnimatedVectorDrawable) drawable;
                avd2.start();
            }
        }
    }

    public static void startDownloadMusic(String uriLink, Context context, String FileName) {
        setToast(context, context.getResources().getString(R.string.download_started));
        Uri uri = Uri.parse(uriLink); // Path where you want to download file.
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
        request.setTitle(FileName + ""); // Title for notification.
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(DIRECTORY_MUSIC, FileName);  // Storage directory path
        ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading

        try {
            MediaScannerConnection.scanFile(context, new String[]{new File(DIRECTORY_DOWNLOADS + "/" + FileName).getAbsolutePath()}, null, (path, uri1) -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}