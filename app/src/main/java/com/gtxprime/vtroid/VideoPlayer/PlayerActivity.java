package com.gtxprime.vtroid.VideoPlayer;

import static com.google.android.exoplayer2.extractor.mp3.Mp3Extractor.FLAG_ENABLE_INDEX_SEEKING;
import static com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES;
import static com.gtxprime.vtroid.Activities.Settings.themeColor;
import static com.gtxprime.vtroid.Activities.Settings.themeColorPrefName;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.LoudnessEnhancer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.accessibility.CaptioningManager;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.mediarouter.app.MediaRouteButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.bullhead.equalizer.EqualizerFragment;
import com.bullhead.equalizer.Settings;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.TracksInfo;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionOverrides;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.CaptionStyleCompat;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.material.snackbar.Snackbar;
import com.gtxprime.vtroid.Adapters.AdapterPlayBackIcon;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.VideoPlayer.dtpv.DoubleTapPlayerView;
import com.gtxprime.vtroid.VideoPlayer.dtpv.youtube.YouTubeOverlay;
import com.gtxprime.vtroid.model.IconModel;
import com.gtxprime.vtroid.model.ModelMediaFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import pl.droidsonroids.casty.Casty;
import pl.droidsonroids.casty.MediaData;

@SuppressLint("InflateParams")
public class PlayerActivity extends AppCompatActivity {

    private PlayerListener playerListener;
    private BroadcastReceiver mReceiver;
    private AudioManager mAudioManager;
    private MediaSessionCompat mediaSession;
    private DefaultTrackSelector trackSelector;
    public static LoudnessEnhancer loudnessEnhancer;
    public com.gtxprime.vtroid.VideoPlayer.CustomStyledPlayerView playerView;
    public static ExoPlayer playerVid;
    private Object mPictureInPictureParamsBuilder;
    public com.gtxprime.vtroid.VideoPlayer.Prefs mPrefs;
    @SuppressLint("StaticFieldLeak")
    public static BrightnessControl mBrightnessControl;
    public static boolean haveMedia;
    private boolean videoLoading;
    public static boolean controllerVisible;
    public static boolean controllerVisibleFully;
    public static Snackbar snackbar;
    private ExoPlaybackException errorToShow;
    public static int boostLevel = 0;
    public static final int CONTROLLER_TIMEOUT = 3500;
    private static final String ACTION_MEDIA_CONTROL = "media_control";
    private static final String EXTRA_CONTROL_TYPE = "control_type";
    private static final int REQUEST_PLAY = 1;
    private static final int REQUEST_PAUSE = 2;
    private static final int CONTROL_TYPE_PLAY = 1;
    private static final int CONTROL_TYPE_PAUSE = 2;
    private CoordinatorLayout coordinatorLayout;
    private ImageButton buttonPiP;
    private ImageButton buttonAspectRatio;
    private ImageButton exoSettings;
    private ProgressBar loadingProgressBar;
    private boolean restorePlayState;
    private boolean play;
    private float subtitlesScale;
    private boolean isScrubbing;
    private boolean scrubbingNoticeable;
    private long scrubbingStart;
    public boolean frameRendered;
    public static boolean focusPlay = false;
    public static boolean locked = false;
    public static boolean restoreControllerTimeout = false;
    public static boolean shortControllerTimeout = false;
    final Rational rationalLimitWide = new Rational(239, 100);
    final Rational rationalLimitTall = new Rational(100, 239);
    static final String API_POSITION = "position";
    static final String API_RETURN_RESULT = "return_result";
    boolean apiAccess;
    boolean intentReturnResult;
    boolean playbackFinished;
    DisplayManager displayManager;
    DisplayManager.DisplayListener displayListener;
    DefaultTimeBar timeBar;
    RecyclerView recyclerView, recyclerView2;
    private final ArrayList<IconModel> iconModelArrayList = new ArrayList<>();
    private final ArrayList<IconModel> iconModelArrayList2 = new ArrayList<>();
    AdapterPlayBackIcon playBackIconAdapter, playBackIconAdapter2;
    View nightMode;
    boolean dark = false;
    String playLink;
    public static boolean alive;
    String LocalPlayLink;
    ImageView nextButton, previousButton;
    int position;
    ArrayList<ModelMediaFiles> mediaFilesAdapters = new ArrayList<>();
    FrameLayout eqContainer;
    private Casty casty;
    MediaRouteButton mediaRouteButton;
    DoubleTapPlayerView video_view;
    ImageButton buttonRotation, exo_subtitle, exo_settings, exo_repeat_toggle;
    SharedPreferences firstRun;
    Window window;
    ImageView playPause;
    public static int savedColor;

    @SuppressLint({"NotifyDataSetChanged", "CutPasteId", "PrivateResource"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Rotate ASAP, before super/inflating to avoid glitches with activity launch animation
        mPrefs = new Prefs(this);
        Utils.setOrientation(this, mPrefs.orientation);
        firstRun = PlayerActivity.this.getSharedPreferences("first", MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT == 28 && Build.MANUFACTURER.equalsIgnoreCase("xiaomi") && Build.DEVICE.equalsIgnoreCase("oneday")) {
            setContentView(R.layout.activity_player_textureview);
        } else {
            setContentView(R.layout.activity_player);
        }
        window = getWindow();

        WindowInsetsControllerCompat windowInsetsControllerx =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        windowInsetsControllerx.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );

        windowInsetsControllerx.hide(WindowInsetsCompat.Type.systemBars());

        if (Build.VERSION.SDK_INT >= 31) {
            if (window != null) {
                window.setDecorFitsSystemWindows(false);
                WindowInsetsController windowInsetsController = window.getInsetsController();
                if (windowInsetsController != null) {
                    // On Android 12 BEHAVIOR_DEFAULT allows system gestures without visible system bars
                    windowInsetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_DEFAULT);
                }
            }
        }
        if (window != null) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        SharedPreferences preferences = getSharedPreferences(themeColorPrefName, MODE_PRIVATE);
        savedColor = preferences.getInt(themeColor, getColor(R.color.primary));

        video_view = findViewById(R.id.video_view);

        com.gtxprime.vtroid.Utils.Utils.setPad(video_view, "bottom", this);

        casty = Casty.create(this).withMiniController();
        mediaRouteButton = findViewById(R.id.media_route_button);
        mediaRouteButton.setForegroundTintList(ColorStateList.valueOf(savedColor));
        casty.setUpMediaRouteButton(mediaRouteButton);

        nextButton = findViewById(R.id.btn);
        nextButton.setEnabled(true);
        nextButton.setActivated(true);
        previousButton = findViewById(R.id.exo_prev);
        exo_subtitle = findViewById(R.id.exo_subtitle);
        exo_settings = findViewById(R.id.exo_settings);
        exo_repeat_toggle = findViewById(R.id.exo_repeat_toggle);

        exo_repeat_toggle.setColorFilter(savedColor);
        exo_settings.setColorFilter(savedColor);
        exo_subtitle.setColorFilter(savedColor);
        nextButton.setOnClickListener(v -> {
            if (mediaFilesAdapters != null && position + 1 < mediaFilesAdapters.size()) {
                playerVid.stop();
                position++;
                initializePlayer();
            } else {
                Toast.makeText(PlayerActivity.this, "No next video found", Toast.LENGTH_SHORT).show();
            }
        });

        previousButton.setOnClickListener(v -> {
            if (mediaFilesAdapters != null && position - 1 >= 0 && position - 1 < mediaFilesAdapters.size()) {
                playerVid.stop();
                position--;
                initializePlayer();
            } else {
                Toast.makeText(PlayerActivity.this, "No Previous video found", Toast.LENGTH_SHORT).show();
            }
        });

        playLink = getIntent().getStringExtra("playLink");
        position = getIntent().getIntExtra("position", 1);
        mediaFilesAdapters = com.gtxprime.vtroid.Utils.VideoListHolder.videoList;
        if (mediaFilesAdapters == null) {
            if (getIntent().getExtras() != null) {
                mediaFilesAdapters = getIntent().getExtras().getParcelableArrayList("videoArrayList");
            }
        }
        if (mediaFilesAdapters == null) {
            mediaFilesAdapters = new ArrayList<>();
        }
        LocalPlayLink = getIntent().getStringExtra("LocalPlayLink");
        eqContainer = findViewById(R.id.eqFrame);

        final Intent launchIntent = getIntent();
        nightMode = findViewById(R.id.night_mode);

        iconModelArrayList.add(new IconModel(R.drawable.nightmode, "Night Mode"));
        iconModelArrayList.add(new IconModel(R.drawable.equalizer, "Equalizer"));

        playBackIconAdapter = new AdapterPlayBackIcon(iconModelArrayList, this);
        recyclerView = findViewById(R.id.recyclerView_icons);
        LinearLayoutManager layout = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(playBackIconAdapter);
        playBackIconAdapter.notifyDataSetChanged();
        playBackIconAdapter.setOnItemClickListener(position -> {
            if (position == 0) {
                if (dark) {
                    nightMode.setVisibility(View.GONE);
                    iconModelArrayList.set(position, new IconModel(R.drawable.nightmode, "Night Mode"));
                    playBackIconAdapter.notifyDataSetChanged();
                    dark = false;
                } else {
                    nightMode.setVisibility(View.VISIBLE);
                    iconModelArrayList.set(position, new IconModel(R.drawable.nightmode, "Day Mode"));
                    playBackIconAdapter.notifyDataSetChanged();
                    dark = true;
                }
            }

            if (position == 1) {
                eqContainer = findViewById(R.id.eqFrame);
                if (eqContainer.getVisibility() == View.GONE) {
                    eqContainer.setVisibility(View.VISIBLE);
                }
                final int sessionId = playerVid.getAudioSessionId();
                Settings.isEditing = false;
                EqualizerFragment equalizerFragment = EqualizerFragment.newBuilder()
                        .setAccentColor(Color.parseColor("#31c0ca"))
                        .setAudioSessionId(sessionId)
                        .build();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.eqFrame, equalizerFragment).commit();
                playBackIconAdapter.notifyDataSetChanged();
            }

        });

        iconModelArrayList2.add(new IconModel(R.drawable.rotatexml, "Rotate"));
        iconModelArrayList2.add(new IconModel(R.drawable.share_v, "Share"));

        playBackIconAdapter2 = new AdapterPlayBackIcon(iconModelArrayList2, this);
        recyclerView2 = findViewById(R.id.recyclerView_icons2);
        LinearLayoutManager layout2 = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        recyclerView2.setLayoutManager(layout2);
        recyclerView2.setAdapter(playBackIconAdapter2);
        playBackIconAdapter2.notifyDataSetChanged();
        playBackIconAdapter2.setOnItemClickListener(position -> {
            if (position == 0) {
                buttonRotation.performClick();
            }

            if (position == 1) {
                if (LocalPlayLink != null) {
                    com.gtxprime.vtroid.Utils.Utils.shareVideo(PlayerActivity.this, LocalPlayLink);
                } else {
                    Toast.makeText(PlayerActivity.this, "Online Video Files Can't Be Share", Toast.LENGTH_SHORT).show();
                }
            }

        });

        if (launchIntent.getData() != null) {
            Bundle bundle = launchIntent.getExtras();
            if (bundle != null) {
                apiAccess = bundle.containsKey(API_POSITION) || bundle.containsKey(API_RETURN_RESULT);
                if (apiAccess)
                    mPrefs.setPersistent(false);
            }

            if (bundle != null) {
                intentReturnResult = bundle.getBoolean(API_RETURN_RESULT);

                if (bundle.containsKey(API_POSITION)) {
                    mPrefs.updatePosition(bundle.getInt(API_POSITION));
                }
            }
        }

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        playerView = findViewById(R.id.video_view);

        loadingProgressBar = findViewById(R.id.loading);

        playerView.setRepeatToggleModes(Player.REPEAT_MODE_ONE);

        ((DoubleTapPlayerView) playerView).setDoubleTapEnabled(false);

        timeBar = playerView.findViewById(R.id.exo_progress);
        timeBar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(TimeBar timeBar, long position) {
                if (playerVid == null) {
                    return;
                }
                restorePlayState = playerVid.isPlaying();
                if (restorePlayState) {
                    playerVid.pause();
                }
                scrubbingNoticeable = false;
                isScrubbing = true;
                frameRendered = true;
                playerView.setControllerShowTimeoutMs(CONTROLLER_TIMEOUT);
                scrubbingStart = playerVid.getCurrentPosition();
                playerVid.setSeekParameters(SeekParameters.CLOSEST_SYNC);
                reportScrubbing(position);
            }

            @Override
            public void onScrubMove(TimeBar timeBar, long position) {
                reportScrubbing(position);
            }

            @Override
            public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                playerView.setCustomErrorMessage(null);
                isScrubbing = false;
                if (restorePlayState) {
                    restorePlayState = false;
                    playerView.setControllerShowTimeoutMs(PlayerActivity.CONTROLLER_TIMEOUT);
                    playerVid.setPlayWhenReady(true);
                }
            }
        });
        timeBar.setPlayedColor(ColorUtils.blendARGB(savedColor, Color.WHITE, 0.1f));
        timeBar.setUnplayedColor(ColorUtils.blendARGB(savedColor, Color.WHITE, 0.6f));
        timeBar.setBufferedColor(ColorUtils.blendARGB(savedColor, Color.WHITE, 0.6f));
        timeBar.setScrubberColor(ColorUtils.blendARGB(savedColor, Color.WHITE, 0.1f));

        TextView exo_position = findViewById(R.id.exo_position);
        exo_position.setTextColor(ColorUtils.blendARGB(savedColor, Color.WHITE, 0.6f));

        TextView dot = findViewById(R.id.dot);
        dot.setTextColor(ColorUtils.blendARGB(savedColor, Color.WHITE, 0.6f));

        TextView exo_duration = findViewById(R.id.exo_duration);
        exo_duration.setTextColor(ColorUtils.blendARGB(savedColor, Color.WHITE, 0.6f));

        if (Utils.isPiPSupported(this)) {
            // https://developer.android.com/about/versions/12/features/pip-improvements
            mPictureInPictureParamsBuilder = new PictureInPictureParams.Builder();
            updatePictureInPictureActions(R.drawable.play, "Play", CONTROL_TYPE_PLAY, REQUEST_PLAY);

            buttonPiP = new ImageButton(this, null, 0, R.style.ExoStyledControls_Button_Bottom);
            buttonPiP.setImageResource(R.drawable.pipxml);
            buttonPiP.setColorFilter(savedColor);
            buttonPiP.setOnClickListener(view -> enterPiP());
        }

        buttonRotation = new ImageButton(this, null, 0, R.style.ExoStyledControls_Button_Bottom);
        buttonRotation.setImageResource(R.drawable.rotatexml);
        buttonRotation.setColorFilter(savedColor);
        buttonRotation.setOnClickListener(view -> {
            mPrefs.orientation = Utils.getNextOrientation(mPrefs.orientation);
            Utils.setOrientation(PlayerActivity.this, mPrefs.orientation);
            Utils.showText(playerView, getString(mPrefs.orientation.description), 2500);
            resetHideCallbacks();
        });

        buttonAspectRatio = new ImageButton(this, null, 0, R.style.ExoStyledControls_Button_Bottom);
        buttonAspectRatio.setImageResource(R.drawable.fitxml);
        buttonAspectRatio.setColorFilter(savedColor);
        buttonAspectRatio.setOnClickListener(view -> {
            playerView.setScale(1.f);
            if (playerView.getResizeMode() == AspectRatioFrameLayout.RESIZE_MODE_FIT) {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                Utils.showText(playerView, getString(R.string.video_resize_crop));
            } else {
                // Default mode
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                Utils.showText(playerView, getString(R.string.video_resize_fit));
            }
            resetHideCallbacks();
        });

        StyledPlayerControlView controlView = playerView.findViewById(R.id.exo_controller);
        controlView.setOnApplyWindowInsetsListener((view, windowInsets) -> {
            if (windowInsets != null) {
                view.setPadding(0, windowInsets.getSystemWindowInsetTop(),
                        0, windowInsets.getSystemWindowInsetBottom());

                int insetLeft = windowInsets.getSystemWindowInsetLeft();
                int insetRight = windowInsets.getSystemWindowInsetRight();

                int paddingLeft = 0;
                int marginLeft = insetLeft;

                int paddingRight = 0;
                int marginRight = insetRight;

                if (Build.VERSION.SDK_INT >= 28 && windowInsets.getDisplayCutout() != null) {
                    if (windowInsets.getDisplayCutout().getSafeInsetLeft() == insetLeft) {
                        paddingLeft = insetLeft;
                        marginLeft = 0;
                    }
                    if (windowInsets.getDisplayCutout().getSafeInsetRight() == insetRight) {
                        paddingRight = insetRight;
                        marginRight = 0;
                    }
                }

                Utils.setViewParams(findViewById(R.id.exo_bottom_bar), paddingLeft, 0, paddingRight, 0,
                        marginLeft, 0, marginRight, 0);

                findViewById(R.id.exo_progress).setPadding(windowInsets.getSystemWindowInsetLeft(), 0,
                        windowInsets.getSystemWindowInsetRight(), 0);

                Utils.setViewMargins(findViewById(R.id.exo_error_message), 0, windowInsets.getSystemWindowInsetTop() / 2, 0, getResources().getDimensionPixelSize(R.dimen.exo_error_message_margin_bottom) + windowInsets.getSystemWindowInsetBottom() / 2);

                windowInsets.consumeSystemWindowInsets();
            }
            return windowInsets;
        });

        // Prevent double tap actions in controller
        //findViewById(R.id.exo_bottom_bar).setOnTouchListener((v, event) -> true);

        playerListener = new PlayerListener();

        mBrightnessControl = new BrightnessControl(this);
        if (mPrefs.brightness >= 0) {
            mBrightnessControl.currentBrightnessLevel = mPrefs.brightness;
            mBrightnessControl.setScreenBrightness(mBrightnessControl.levelToBrightness(mBrightnessControl.currentBrightnessLevel));
        }

        final LinearLayout exoBasicControls = playerView.findViewById(R.id.exo_basic_controls);
        final ImageButton exoSubtitle = exoBasicControls.findViewById(R.id.exo_subtitle);
        exoBasicControls.removeView(exoSubtitle);

        exoSettings = exoBasicControls.findViewById(R.id.exo_settings);
        exoBasicControls.removeView(exoSettings);
        //exoBasicControls.setVisibility(View.GONE);

        exoSettings.setOnLongClickListener(view -> {
            //askForScope(false, false);
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        });

        updateButtons(false);

        final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) getLayoutInflater().inflate(R.layout.controls, null);
        final LinearLayout controls = horizontalScrollView.findViewById(R.id.controls);

        controls.addView(exoSubtitle);
        controls.addView(buttonAspectRatio);
        if (Utils.isPiPSupported(this)) {
            controls.addView(buttonPiP);
        }
        controls.addView(exoSettings);
        controls.addView(buttonRotation);

        exoBasicControls.addView(horizontalScrollView);
        playPause = findViewById(R.id.playPause);

        horizontalScrollView.setOnScrollChangeListener((view, i, i1, i2, i3) -> resetHideCallbacks());

        playerView.setControllerVisibilityListener(visibility -> {
            controllerVisible = visibility == View.VISIBLE;
            controllerVisibleFully = playerView.isControllerFullyVisible();

            if (PlayerActivity.restoreControllerTimeout) {
                restoreControllerTimeout = false;
                if (playerVid == null || !playerVid.isPlaying()) {
                    playerView.setControllerShowTimeoutMs(CONTROLLER_TIMEOUT);
                    playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.avd_pause_to_play, null));
                } else {
                    playerView.setControllerShowTimeoutMs(PlayerActivity.CONTROLLER_TIMEOUT);
                    playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.avd_play_to_pause, null));
                }
            }

            // https://developer.android.com/training/system-ui/immersive
            if (visibility == View.VISIBLE) {
                // Because when using dpad controls, focus resets to first item in bottom controls bar

                findViewById(R.id.playPause).requestFocus();
            } else {
                Utils.hideSystemUi(playerView);
            }

            if (controllerVisible && playerView.isControllerFullyVisible()) {
                if (errorToShow != null) {
                    showError(errorToShow);
                    errorToShow = null;
                }
            }
        });

        playPause.setOnClickListener(v -> onPlayPausePress(playPause));
    }

    public void onPlayPausePress(ImageView holder) {
        AnimatedVectorDrawableCompat avd;
        AnimatedVectorDrawable avd2;
        if (playerVid.isPlaying()) {
            holder.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.avd_pause_to_play, null));
            Drawable drawable = holder.getDrawable();
            playerVid.pause();

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
            playerVid.play();

            if (drawable instanceof AnimatedVectorDrawableCompat) {
                avd = (AnimatedVectorDrawableCompat) drawable;
                avd.start();
            } else if (drawable instanceof AnimatedVectorDrawable) {
                avd2 = (AnimatedVectorDrawable) drawable;
                avd2.start();
            }
        }
    }

    private void tapTarget() {
        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(findViewById(R.id.exo_repeat_toggle), "Toggle repeat")
                                .outerCircleColor(R.color.teal_700)
                                .targetCircleColor(R.color.fui_transparent)
                                .textColor(R.color.white)
                                .textTypeface(ResourcesCompat.getFont(this, R.font.corbel))
                                .cancelable(false)
                                .transparentTarget(true)
                                .targetRadius(50)
                                .cancelable(false)
                                .outerCircleAlpha(0.7f)
                                .transparentTarget(false),
                        TapTarget.forView(findViewById(R.id.exo_subtitle), "Toggle Subtitles")
                                .outerCircleColor(R.color.teal_700)
                                .targetCircleColor(R.color.fui_transparent)
                                .textColor(R.color.white)
                                .textTypeface(ResourcesCompat.getFont(this, R.font.corbel))
                                .cancelable(false)
                                .tintTarget(false)
                                .cancelable(false)
                                .transparentTarget(true)
                                .targetRadius(50)
                                .outerCircleAlpha(0.7f)
                                .transparentTarget(false),
                        TapTarget.forView(buttonAspectRatio, "Change Aspect ratio")
                                .outerCircleColor(R.color.teal_700)
                                .targetCircleColor(R.color.fui_transparent)
                                .textColor(R.color.white)
                                .textTypeface(ResourcesCompat.getFont(this, R.font.corbel))
                                .cancelable(false)
                                .tintTarget(false)
                                .transparentTarget(true)
                                .cancelable(false)
                                .targetRadius(50)
                                .outerCircleAlpha(0.7f)
                                .transparentTarget(false),
                        TapTarget.forView(buttonPiP, "Pop-Up player")
                                .outerCircleColor(R.color.teal_700)
                                .targetCircleColor(R.color.fui_transparent)
                                .textColor(R.color.white)
                                .textTypeface(ResourcesCompat.getFont(this, R.font.corbel))
                                .cancelable(false)
                                .tintTarget(false)
                                .transparentTarget(true)
                                .cancelable(false)
                                .targetRadius(50)
                                .outerCircleAlpha(0.7f)
                                .transparentTarget(false),
                        TapTarget.forView(findViewById(R.id.exo_settings), "Settings", "long press for more settings")
                                .outerCircleColor(R.color.teal_700)
                                .targetCircleColor(R.color.fui_transparent)
                                .textColor(R.color.white)
                                .textTypeface(ResourcesCompat.getFont(this, R.font.corbel))
                                .cancelable(false)
                                .transparentTarget(true)
                                .cancelable(false)
                                .targetRadius(50)
                                .tintTarget(false)
                                .outerCircleAlpha(0.7f)
                                .transparentTarget(false),
                        TapTarget.forView(buttonRotation, "Change Orientation")
                                .outerCircleColor(R.color.teal_700)
                                .targetCircleColor(R.color.fui_transparent)
                                .textColor(R.color.white)
                                .textTypeface(ResourcesCompat.getFont(this, R.font.corbel))
                                .cancelable(false)
                                .transparentTarget(true)
                                .tintTarget(false)
                                .targetRadius(50)
                                .cancelable(false)
                                .outerCircleAlpha(0.7f)
                                .transparentTarget(false),
                        TapTarget.forView(findViewById(R.id.recyclerView_icons), "Toggle Equalizer and night mode")
                                .outerCircleColor(R.color.teal_700)
                                .targetCircleColor(R.color.fui_transparent)
                                .textColor(R.color.white)
                                .textTypeface(ResourcesCompat.getFont(this, R.font.corbel))
                                .cancelable(false)
                                .tintTarget(false)
                                .cancelable(false)
                                .transparentTarget(true)
                                .targetRadius(60)
                                .outerCircleAlpha(0.7f)
                                .transparentTarget(false),
                        TapTarget.forView(findViewById(R.id.recyclerView_icons2), "Share and change orientation")
                                .outerCircleColor(R.color.teal_700)
                                .targetCircleColor(R.color.fui_transparent)
                                .textColor(R.color.white)
                                .cancelable(true)
                                .textTypeface(ResourcesCompat.getFont(this, R.font.corbel))
                                .cancelable(false)
                                .tintTarget(false)
                                .transparentTarget(true)
                                .targetRadius(60)
                                .outerCircleAlpha(0.7f)
                                .transparentTarget(false)

                ).listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        SharedPreferences.Editor firstEditor = firstRun.edit();
                        firstEditor.putBoolean("isFirst", true).apply();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                })
                .start();
    }

    @Override
    public void onStart() {
        super.onStart();
        alive = true;
        updateSubtitleStyle();
        initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        alive = false;
        releasePlayer();
    }

    @Override
    public void finish() {
        if (intentReturnResult) {
            Intent intent = new Intent();
            if (!playbackFinished) {
                if (playerVid.isCurrentMediaItemSeekable()) {
                    long position;
                    if (mPrefs.persistentMode)
                        position = mPrefs.nonPersitentPosition;
                    else
                        position = playerVid.getCurrentPosition();
                    intent.putExtra(API_POSITION, (int) position);
                }
            }
            setResult(Activity.RESULT_OK, intent);
        }

        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                playerView.removeCallbacks(playerView.textClearRunnable);
                Utils.adjustVolume(mAudioManager, playerView, keyCode == KeyEvent.KEYCODE_VOLUME_UP, event.getRepeatCount() == 0);
                return true;
            case KeyEvent.KEYCODE_BUTTON_SELECT:
            case KeyEvent.KEYCODE_BUTTON_START:
            case KeyEvent.KEYCODE_BUTTON_A:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
            case KeyEvent.KEYCODE_SPACE:
                if (playerVid == null)
                    break;
                if (!controllerVisibleFully) {
                    if (playerVid.isPlaying()) {
                        playerVid.pause();
                    } else {
                        playerVid.play();
                    }
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_BUTTON_L2:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                if (!controllerVisibleFully || keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
                    if (playerVid == null)
                        break;
                    playerView.removeCallbacks(playerView.textClearRunnable);
                    long seekTo = playerVid.getCurrentPosition() - 10_000;
                    if (seekTo < 0)
                        seekTo = 0;
                    playerVid.setSeekParameters(SeekParameters.PREVIOUS_SYNC);
                    playerVid.seekTo(seekTo);
                    playerView.setCustomErrorMessage(Utils.formatMilis(seekTo));
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_BUTTON_R2:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                if (!controllerVisibleFully || keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
                    if (playerVid == null)
                        break;
                    playerView.removeCallbacks(playerView.textClearRunnable);
                    long seekTo = playerVid.getCurrentPosition() + 10_000;
                    long seekMax = playerVid.getDuration();
                    if (seekMax != C.TIME_UNSET && seekTo > seekMax)
                        seekTo = seekMax;
                    PlayerActivity.playerVid.setSeekParameters(SeekParameters.NEXT_SYNC);
                    playerVid.seekTo(seekTo);
                    playerView.setCustomErrorMessage(Utils.formatMilis(seekTo));
                    return true;
                }
                break;

            default:
                if (!controllerVisibleFully) {
                    playerView.showController();
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                playerView.postDelayed(playerView.textClearRunnable, com.gtxprime.vtroid.VideoPlayer.CustomStyledPlayerView.MESSAGE_TIMEOUT_KEY);
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_BUTTON_L2:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_BUTTON_R2:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                playerView.postDelayed(playerView.textClearRunnable, com.gtxprime.vtroid.VideoPlayer.CustomStyledPlayerView.MESSAGE_TIMEOUT_KEY);
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        return super.dispatchKeyEvent(event);

    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, @NonNull Configuration newConfig) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        }

        if (isInPictureInPictureMode) {
            // On Android TV it is required to hide controller in this PIP change callback
            playerView.hideController();
            setSubtitleTextSizePiP();
            playerView.setScale(1.f);
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent == null || !ACTION_MEDIA_CONTROL.equals(intent.getAction()) || playerVid == null) {
                        return;
                    }

                    switch (intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)) {
                        case CONTROL_TYPE_PLAY:
                            playerVid.play();
                            break;
                        case CONTROL_TYPE_PAUSE:
                            playerVid.pause();
                            break;
                    }
                }
            };
            registerReceiver(mReceiver, new IntentFilter(ACTION_MEDIA_CONTROL));
        } else {
            setSubtitleTextSize();
            if (mPrefs.resizeMode == AspectRatioFrameLayout.RESIZE_MODE_ZOOM) {
                playerView.setScale(mPrefs.scale);
            }
            if (mReceiver != null) {
                unregisterReceiver(mReceiver);
                mReceiver = null;
            }
            playerView.setControllerAutoShow(true);
            if (playerVid != null) {
                if (playerVid.isPlaying())
                    Utils.hideSystemUi(playerView);
                else
                    playerView.showController();
            }
        }
    }

    public void initializePlayer() {
        Uri uri;
        String castUri;
        if (playLink != null) {
            uri = Uri.parse(playLink);
            castUri = String.valueOf(uri);
            casty.setOnConnectChangeListener(new Casty.OnConnectChangeListener() {
                @Override
                public void onConnected() {
                    Log.d("Casty", "Connected with Chromecast");
                    Toast.makeText(PlayerActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    if (casty.isConnected() && playerVid != null) {
                        MediaData mediaData = new MediaData.Builder(castUri)
                                .setStreamType(MediaData.STREAM_TYPE_BUFFERED) //required
                                .setContentType("videos/mp4") //required
                                .setMediaType(MediaData.MEDIA_TYPE_MOVIE)
                                .setTitle(URLUtil.guessFileName(castUri, null, null))
                                .setSubtitle("Casting using V-Troid")
                                .addPhotoUrl(String.valueOf(R.drawable.hd_logo))
                                .build();
                        casty.getPlayer().loadMediaAndPlay(mediaData);
                    }
                    casty.getPlayer().play();
                }

                @Override
                public void onDisconnected() {
                    Log.d("Casty", "Disconnected from Chromecast");
                    Toast.makeText(PlayerActivity.this, "Disconnected from Chromecast", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (mediaFilesAdapters == null || mediaFilesAdapters.isEmpty() || position < 0 || position >= mediaFilesAdapters.size()) {
                Toast.makeText(PlayerActivity.this, "Video not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            String path = mediaFilesAdapters.get(position).getPath();
            //uri = Uri.parse(path);
            uri = Uri.fromFile(new File(path));
            mediaRouteButton.setVisibility(View.GONE);
            Log.e("Uri = ", "" + uri);
        }

        mPrefs.mediaUri = uri;
        boolean isNetworkUri = mPrefs.mediaUri != null && Utils.isSupportedNetworkUri(mPrefs.mediaUri);
        haveMedia = mPrefs.mediaUri != null && (Utils.fileExists(this, mPrefs.mediaUri) || isNetworkUri);

        if (playerVid == null) {
            trackSelector = new DefaultTrackSelector(this);
            if (mPrefs.tunneling) {
                trackSelector.setParameters(trackSelector.buildUponParameters()
                        .setTunnelingEnabled(true)
                );
            }
            trackSelector.setParameters(trackSelector.buildUponParameters()
                    .setPreferredAudioLanguages(Utils.getDeviceLanguages())
            );
            RenderersFactory renderersFactory = new DefaultRenderersFactory(this)
                    .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);

            final DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory()
                    .setTsExtractorFlags(FLAG_ALLOW_NON_IDR_KEYFRAMES)
                    .setMp3ExtractorFlags(FLAG_ENABLE_INDEX_SEEKING)
                    .setTsExtractorFlags(DefaultTsPayloadReaderFactory.FLAG_ENABLE_HDMV_DTS_AUDIO_STREAMS)
                    .setTsExtractorTimestampSearchBytes(1500 * TsExtractor.TS_PACKET_SIZE);

            playerVid = new ExoPlayer.Builder(this, renderersFactory)
                    .setTrackSelector(trackSelector)
                    .setMediaSourceFactory(new DefaultMediaSourceFactory(this, extractorsFactory))
                    .build();

            final AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.CONTENT_TYPE_MOVIE)
                    .build();


            playerVid.setAudioAttributes(audioAttributes, true);

            if (mPrefs.skipSilence) {
                playerVid.setSkipSilenceEnabled(true);
            }

            final YouTubeOverlay youTubeOverlay = findViewById(R.id.youtube_overlay);

            youTubeOverlay.performListener(new YouTubeOverlay.PerformListener() {
                @Override
                public void onAnimationStart() {
                    youTubeOverlay.setAlpha(1.0f);
                    youTubeOverlay.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd() {
                    youTubeOverlay.animate()
                            .alpha(0.0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    youTubeOverlay.setVisibility(View.GONE);
                                    youTubeOverlay.setAlpha(1.0f);
                                }
                            });
                }
            });

            youTubeOverlay.player(playerVid);
        }

        playerView.setPlayer(playerVid);

        mediaSession = new MediaSessionCompat(this, getString(R.string.app_name));
        MediaSessionConnector mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setPlayer(playerVid);
        mediaSessionConnector.setMediaMetadataProvider(player -> {
            final String title = Utils.getFileName(PlayerActivity.this, mPrefs.mediaUri);
            if (title == null) {
                return new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "name")
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "name")
                        .build();
            } else {
                return new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .build();
            }
        });


        locked = false;

        if (haveMedia) {

            // https://github.com/google/ExoPlayer/issues/5765
            playerView.setResizeMode(mPrefs.resizeMode);

            if (mPrefs.resizeMode == AspectRatioFrameLayout.RESIZE_MODE_ZOOM) {
                playerView.setScale(mPrefs.scale);
            } else {
                playerView.setScale(1.f);
            }
            // Create a data source factory.
            MediaItem.Builder mediaItemBuilder = new MediaItem.Builder().setUri(uri);
            if (uri != null) {
                if (uri.getLastPathSegment().contains("m3u8")) {
                    mediaItemBuilder.setMimeType((MimeTypes.APPLICATION_M3U8));
                } else if (uri.getLastPathSegment().contains("mkv")) {
                    mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MATROSKA);
                } else if (uri.getLastPathSegment().contains("mpd")) {
                    mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MPD);
                }
            }


            if (mPrefs.subtitleUri != null && Utils.fileExists(this, mPrefs.subtitleUri)) {
                MediaItem.SubtitleConfiguration subtitle = SubtitleUtils.buildSubtitle(this, mPrefs.subtitleUri);
                mediaItemBuilder.setSubtitleConfigurations(Collections.singletonList(subtitle));
            }


            assert uri != null;
            playerVid.setMediaSource(buildMediaSource(uri));
            playerVid.setMediaItem(mediaItemBuilder.build(), mPrefs.getPosition());


            if (loudnessEnhancer != null) {
                loudnessEnhancer.release();
            }
            try {
                loudnessEnhancer = new LoudnessEnhancer(playerVid.getAudioSessionId());
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            notifyAudioSessionUpdate(true);

            videoLoading = true;

            updateLoading(true);

            if (mPrefs.getPosition() == 0L || apiAccess) {
                play = true;
            }

            updateButtons(true);

            ((DoubleTapPlayerView) playerView).setDoubleTapEnabled(true);


            playerVid.setHandleAudioBecomingNoisy(true);
            mediaSession.setActive(true);
        } else {
            playerView.showController();
        }

        playerVid.addListener(playerListener);
        playerVid.prepare();


        if (restorePlayState) {
            restorePlayState = false;
            playerView.showController();
            playerVid.play();
        }

        boolean isFirstRun = getSharedPreferences("first", 0).getBoolean("isFirst", false);
        if (!isFirstRun) {
            tapTarget();
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        if (uri.getLastPathSegment().contains("mp4") || uri.getLastPathSegment().contains("mkv")) {
            DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
            return new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri));
        } else if (uri.getLastPathSegment().contains("m3u8")) {
            DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
            return new HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri));
        } else if (uri.getLastPathSegment().contains("mpd")) {
            DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
            return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
        } else {
            DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
            return new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri));
        }
    }

    public void releasePlayer() {
        if (playerVid != null) {
            notifyAudioSessionUpdate(false);

            mediaSession.setActive(false);
            mediaSession.release();

            mPrefs.updateBrightness(mBrightnessControl.currentBrightnessLevel);
            mPrefs.updateOrientation();

            if (haveMedia) {
                // Prevent overwriting temporarily inaccessible media position
                if (playerVid.isCurrentMediaItemSeekable()) {
                    mPrefs.updatePosition(playerVid.getCurrentPosition());
                }
                mPrefs.updateMeta(getSelectedTrack(C.TRACK_TYPE_AUDIO), getSelectedTrack(C.TRACK_TYPE_TEXT), playerView.getResizeMode(), Objects.requireNonNull(playerView.getVideoSurfaceView()).getScaleX());
            }

            if (playerVid.isPlaying()) {
                restorePlayState = true;
            }
            playerVid.removeListener(playerListener);
            playerVid.clearMediaItems();
            playerVid.release();
            playerVid = null;
        }

        updateButtons(false);
    }

    private class PlayerListener implements Player.Listener {
        @Override
        public void onAudioSessionIdChanged(int audioSessionId) {
            if (loudnessEnhancer != null) {
                loudnessEnhancer.release();
            }
            try {
                loudnessEnhancer = new LoudnessEnhancer(audioSessionId);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            notifyAudioSessionUpdate(true);
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            playerView.setKeepScreenOn(isPlaying);
            if (Utils.isPiPSupported(PlayerActivity.this)) {
                if (isPlaying) {
                    try {
                        updatePictureInPictureActions(R.drawable.pausexml, "Pause", CONTROL_TYPE_PAUSE, REQUEST_PAUSE);
                    } catch (NumberFormatException e) {
                        Log.e("PiP error", String.valueOf(e));
                    }

                } else {
                    try {
                        updatePictureInPictureActions(R.drawable.playxml, "Play", CONTROL_TYPE_PLAY, REQUEST_PLAY);
                    } catch (NumberFormatException e) {
                        Log.e("PiP error", String.valueOf(e));
                    }

                }
            }

            if (!isScrubbing) {
                if (isPlaying) {
                    if (shortControllerTimeout) {
                        playerView.setControllerShowTimeoutMs(CONTROLLER_TIMEOUT / 3);
                        shortControllerTimeout = false;
                        restoreControllerTimeout = true;
                    } else {
                        playerView.setControllerShowTimeoutMs(CONTROLLER_TIMEOUT);
                    }
                } else {
                    playerView.setControllerShowTimeoutMs(3);
                }
            }

            if (!isPlaying) {
                PlayerActivity.locked = false;
            }
        }

        @SuppressLint("SourceLockedOrientationActivity")
        @Override
        public void onPlaybackStateChanged(int state) {
            if (state == Player.STATE_READY) {
                frameRendered = true;
                playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.avd_pause_to_play, null));
                if (videoLoading) {
                    videoLoading = false;

                    final Format format = playerVid.getVideoFormat();
                    float frameRateExo = Format.NO_VALUE;

                    if (format != null) {
                        if (mPrefs.orientation == Utils.Orientation.VIDEO) {
                            if (Utils.isPortrait(format)) {
                                PlayerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                            } else {
                                PlayerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                            }
                        }
                        frameRateExo = format.frameRate;

                        updateSubtitleViewMargin(format);
                    }

                    boolean switched = false;
                    if (mPrefs.frameRateMatching) {
                        if (play) {
                            if (displayManager == null) {
                                displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
                            }
                            if (displayListener == null) {
                                displayListener = new DisplayManager.DisplayListener() {
                                    @Override
                                    public void onDisplayAdded(int displayId) {

                                    }

                                    @Override
                                    public void onDisplayRemoved(int displayId) {

                                    }

                                    @Override
                                    public void onDisplayChanged(int displayId) {
                                        if (play) {
                                            play = false;
                                            displayManager.unregisterDisplayListener(this);
                                            if (playerVid != null) {
                                                playerVid.play();
                                            }
                                            if (playerView != null) {
                                                playerView.hideController();
                                            }
                                        }
                                    }
                                };
                            }
                            displayManager.registerDisplayListener(displayListener, null);
                        }
                        switched = Utils.switchFrameRate(PlayerActivity.this, frameRateExo, mPrefs.mediaUri, play);
                    }
                    if (!switched) {
                        if (displayManager != null) {
                            displayManager.unregisterDisplayListener(displayListener);
                        }
                        if (play) {
                            play = false;
                            playerVid.play();
                            playerView.hideController();
                        }
                    }

                    updateLoading(false);

                    setSelectedTracks(mPrefs.subtitleTrackId, mPrefs.audioTrackId);
                }
            } else if (state == Player.STATE_ENDED) {
                if (playLink != null) {
                    onBackPressed();
                    playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.avd_play_to_pause, null));
                } else {
                    playbackFinished = true;
                    if (apiAccess) {
                        finish();
                        playPause.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.avd_play_to_pause, null));
                    }
                }
            }
        }

        @Override
        public void onPlayerError(@NonNull PlaybackException error) {
            updateLoading(false);
            if (error instanceof ExoPlaybackException) {
                final ExoPlaybackException exoPlaybackException = (ExoPlaybackException) error;
                if (controllerVisible && controllerVisibleFully) {
                    showError(exoPlaybackException);
                } else {
                    errorToShow = exoPlaybackException;
                }
            }
        }
    }

    private TrackGroup getTrackGroupFromFormatId(int trackType, String id) {
        if (id == null || playerVid == null) {
            return null;
        }
        for (TracksInfo.TrackGroupInfo groupInfo : playerVid.getCurrentTracksInfo().getTrackGroupInfos()) {
            if (groupInfo.getTrackType() == trackType) {
                final TrackGroup trackGroup = groupInfo.getTrackGroup();
                final Format format = trackGroup.getFormat(0);
                if (Objects.equals(id, format.id)) {
                    return trackGroup;
                }
            }
        }
        return null;
    }

    public void setSelectedTracks(final String subtitleId, final String audioId) {
        trackSelector = new DefaultTrackSelector(this);
        if ("#none".equals(subtitleId)) {
            trackSelector.setParameters(trackSelector.buildUponParameters().setDisabledTextTrackSelectionFlags(C.SELECTION_FLAG_DEFAULT | C.SELECTION_FLAG_FORCED));
        }

        TrackGroup subtitleGroup = getTrackGroupFromFormatId(C.TRACK_TYPE_TEXT, subtitleId);
        TrackGroup audioGroup = getTrackGroupFromFormatId(C.TRACK_TYPE_AUDIO, audioId);

        TrackSelectionOverrides.Builder overridesBuilder = new TrackSelectionOverrides.Builder();
        final List<Integer> tracks = new ArrayList<>();
        tracks.add(0);
        if (subtitleGroup != null) {
            overridesBuilder.addOverride(new TrackSelectionOverrides.TrackSelectionOverride(subtitleGroup, tracks));
        }
        if (audioGroup != null) {
            overridesBuilder.addOverride(new TrackSelectionOverrides.TrackSelectionOverride(audioGroup, tracks));
        }

        if (playerVid != null) {
            TrackSelectionParameters.Builder trackSelectionParametersBuilder = playerVid.getTrackSelectionParameters().buildUpon();
            trackSelectionParametersBuilder.setTrackSelectionOverrides(overridesBuilder.build());
            playerVid.setTrackSelectionParameters(trackSelectionParametersBuilder.build());
        }
    }

    private boolean hasOverrideType() {
        TrackSelectionParameters trackSelectionParameters = playerVid.getTrackSelectionParameters();
        for (TrackSelectionOverrides.TrackSelectionOverride override : trackSelectionParameters.trackSelectionOverrides.asList()) {
            if (override.getTrackType() == C.TRACK_TYPE_AUDIO)
                return true;
        }
        return false;
    }

    public String getSelectedTrack(final int trackType) {
        if (playerVid == null) {
            return null;
        }
        TracksInfo tracksInfo = playerVid.getCurrentTracksInfo();

        // Disabled (e.g. selected subtitle "None" - different than default)
        if (!tracksInfo.isTypeSelected(trackType)) {
            return "#none";
        }

        // Audio track set to "Auto"
        if (trackType == C.TRACK_TYPE_AUDIO) {
            if (!hasOverrideType()) {
                return null;
            }
        }

        for (TracksInfo.TrackGroupInfo groupInfo : tracksInfo.getTrackGroupInfos()) {
            if (groupInfo.isSelected() && groupInfo.getTrackType() == trackType) {
                Format format = groupInfo.getTrackGroup().getFormat(0);
                return format.id;
            }
        }

        return null;
    }

    void setSubtitleTextSize() {
        setSubtitleTextSize(getResources().getConfiguration().orientation);
    }

    void setSubtitleTextSize(final int orientation) {
        // Tweak text size as fraction size doesn't work well in portrait
        final SubtitleView subtitleView = playerView.getSubtitleView();
        if (subtitleView != null) {
            final float size;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                size = SubtitleView.DEFAULT_TEXT_SIZE_FRACTION * subtitlesScale;
            } else {
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                float ratio = ((float) metrics.heightPixels / (float) metrics.widthPixels);
                if (ratio < 1)
                    ratio = 1 / ratio;
                size = SubtitleView.DEFAULT_TEXT_SIZE_FRACTION * subtitlesScale / ratio;
            }

            subtitleView.setFractionalTextSize(size);
        }
    }

    void updateSubtitleViewMargin() {
        if (playerVid == null) {
            return;
        }

        updateSubtitleViewMargin(playerVid.getVideoFormat());
    }

    // Set margins to fix PGS aspect as subtitle view is outside of content frame
    void updateSubtitleViewMargin(Format format) {
        if (format == null) {
            return;
        }

        final Rational aspectVideo = Utils.getRational(format);
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        final Rational aspectDisplay = new Rational(metrics.widthPixels, metrics.heightPixels);

        int marginHorizontal = 0;
        int marginVertical = 0;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (aspectDisplay.floatValue() > aspectVideo.floatValue()) {
                // Left & right bars
                int videoWidth = metrics.heightPixels / aspectVideo.getDenominator() * aspectVideo.getNumerator();
                marginHorizontal = (metrics.widthPixels - videoWidth) / 2;
            }
        }

        Utils.setViewParams(Objects.requireNonNull(playerView.getSubtitleView()), 0, 0, 0, 0,
                marginHorizontal, marginVertical, marginHorizontal, marginVertical);
    }

    void setSubtitleTextSizePiP() {
        final SubtitleView subtitleView = playerView.getSubtitleView();
        if (subtitleView != null)
            subtitleView.setFractionalTextSize(SubtitleView.DEFAULT_TEXT_SIZE_FRACTION * 2);
    }

    @TargetApi(26)
    void updatePictureInPictureActions(final int iconId, final String resTitle, final int controlType, final int requestCode) {
        final ArrayList<RemoteAction> actions = new ArrayList<>();
        final PendingIntent intent = PendingIntent.getBroadcast(PlayerActivity.this, requestCode,
                new Intent(ACTION_MEDIA_CONTROL).putExtra(EXTRA_CONTROL_TYPE, controlType), PendingIntent.FLAG_IMMUTABLE);
        final Icon icon = Icon.createWithResource(PlayerActivity.this, iconId);
        actions.add(new RemoteAction(icon, resTitle, resTitle, intent));
        ((PictureInPictureParams.Builder) mPictureInPictureParamsBuilder).setActions(actions);
        setPictureInPictureParams(((PictureInPictureParams.Builder) mPictureInPictureParamsBuilder).build());
    }

    private boolean isInPip() {
        if (!Utils.isPiPSupported(this))
            return false;
        return isInPictureInPictureMode();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (!isInPip()) {
            setSubtitleTextSize(newConfig.orientation);
        }
        updateSubtitleViewMargin();
    }

    void showError(ExoPlaybackException error) {
        final String errorGeneral = error.getLocalizedMessage();
        String errorDetailed;

        switch (error.type) {
            case ExoPlaybackException.TYPE_SOURCE:
                errorDetailed = error.getSourceException().getLocalizedMessage();
                break;
            case ExoPlaybackException.TYPE_RENDERER:
                errorDetailed = error.getRendererException().getLocalizedMessage();
                break;
            case ExoPlaybackException.TYPE_UNEXPECTED:
                errorDetailed = error.getUnexpectedException().getLocalizedMessage();
                break;
            case ExoPlaybackException.TYPE_REMOTE:
            default:
                errorDetailed = errorGeneral;
                break;
        }

        showSnack(errorGeneral, errorDetailed);
    }

    void showSnack(final String textPrimary, final String textSecondary) {
        snackbar = Snackbar.make(coordinatorLayout, textPrimary, Snackbar.LENGTH_LONG);
        if (textSecondary != null) {
            snackbar.setAction(R.string.error_details, v -> {
                final AlertDialog.Builder builder = new AlertDialog.Builder(PlayerActivity.this);
                builder.setMessage(textSecondary);
                builder.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss());
                final AlertDialog dialog = builder.create();
                dialog.show();
            });
        }
        snackbar.setAnchorView(R.id.exo_bottom_bar);
        snackbar.show();
    }

    void reportScrubbing(long position) {
        final long diff = position - scrubbingStart;
        if (Math.abs(diff) > 1000) {
            scrubbingNoticeable = true;
        }
        if (scrubbingNoticeable) {
            playerView.clearIcon();
            playerView.setCustomErrorMessage(Utils.formatMilisSign(diff));
        }
        if (frameRendered) {
            frameRendered = false;
            playerVid.seekTo(position);
        }
    }

    void updateSubtitleStyle() {
        final CaptioningManager captioningManager = (CaptioningManager) getSystemService(Context.CAPTIONING_SERVICE);
        final SubtitleView subtitleView = playerView.getSubtitleView();
        if (!captioningManager.isEnabled()) {
            subtitlesScale = 1.05f;
            final CaptionStyleCompat captionStyle = new CaptionStyleCompat(Color.WHITE, Color.TRANSPARENT, Color.TRANSPARENT, CaptionStyleCompat.EDGE_TYPE_OUTLINE, Color.BLACK, Typeface.DEFAULT_BOLD);
            if (subtitleView != null) {
                subtitleView.setStyle(captionStyle);
                subtitleView.setApplyEmbeddedStyles(true);
            }
        } else {
            subtitlesScale = captioningManager.getFontScale();
            if (subtitleView != null) {
                subtitleView.setUserDefaultStyle();
                // Do not apply embedded style as currently the only supported color style is PrimaryColour
                // https://github.com/google/ExoPlayer/issues/8435#issuecomment-762449001
                // This may result in poorly visible text (depending on user's selected edgeColor)
                // The same can happen with style provided using setStyle but enabling CaptioningManager should be a way to change the behavior
                subtitleView.setApplyEmbeddedStyles(false);
            }
        }

        if (subtitleView != null)
            subtitleView.setBottomPaddingFraction(SubtitleView.DEFAULT_BOTTOM_PADDING_FRACTION * 2f / 3f);

        setSubtitleTextSize();
    }


    void resetHideCallbacks() {
        if (haveMedia && playerVid != null && playerVid.isPlaying()) {
            // Keep controller UI visible - alternative to resetHideCallbacks()
            playerView.setControllerShowTimeoutMs(PlayerActivity.CONTROLLER_TIMEOUT);
        }
    }

    private void updateLoading(final boolean enableLoading) {
        if (enableLoading) {
            //playPause.setVisibility(View.GONE);
            loadingProgressBar.setVisibility(View.VISIBLE);
        } else {
            loadingProgressBar.setVisibility(View.GONE);
            // playPause.setVisibility(View.VISIBLE);
            if (focusPlay) {
                focusPlay = false;
                //  playPause.requestFocus();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onUserLeaveHint() {
        if (mPrefs != null && mPrefs.autoPiP && playerVid != null && playerVid.isPlaying() && Utils.isPiPSupported(this))
            enterPiP();
        else
            super.onUserLeaveHint();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enterPiP() {
        final AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        if (AppOpsManager.MODE_ALLOWED != appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, android.os.Process.myUid(), getPackageName())) {
            final Intent intent = new Intent("android.settings.PICTURE_IN_PICTURE_SETTINGS", Uri.fromParts("package", getPackageName(), null));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            return;
        }

        if (playerVid == null) {
            return;
        }

        playerView.setControllerAutoShow(false);
        playerView.hideController();

        final Format format = playerVid.getVideoFormat();

        if (format != null) {
            // https://github.com/google/ExoPlayer/issues/8611
            // TODO: Test/disable on Android 11+
            final View videoSurfaceView = playerView.getVideoSurfaceView();
            if (videoSurfaceView instanceof SurfaceView) {
                ((SurfaceView) videoSurfaceView).getHolder().setFixedSize(format.width, format.height);
            }

            Rational rational = Utils.getRational(format);
            if (rational.floatValue() > rationalLimitWide.floatValue())
                rational = rationalLimitWide;
            else if (rational.floatValue() < rationalLimitTall.floatValue())
                rational = rationalLimitTall;

            ((PictureInPictureParams.Builder) mPictureInPictureParamsBuilder).setAspectRatio(rational);
        }
        enterPictureInPictureMode(((PictureInPictureParams.Builder) mPictureInPictureParamsBuilder).build());
    }

    void notifyAudioSessionUpdate(final boolean active) {
        final Intent intent = new Intent(active ? AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION
                : AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION);
        intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, playerVid.getAudioSessionId());
        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        if (active) {
            intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MOVIE);
        }
        sendBroadcast(intent);
    }

    void updateButtons(final boolean enable) {
        if (buttonPiP != null) {
            Utils.setButtonEnabled(this, buttonPiP, enable);
        }
        Utils.setButtonEnabled(this, buttonAspectRatio, enable);
        Utils.setButtonEnabled(this, exoSettings, enable);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.eqFrame);
        if (eqContainer.getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            assert fragment != null;
            if (fragment.isVisible() && eqContainer.getVisibility() == View.VISIBLE) {
                eqContainer.setVisibility(View.GONE);
            } else {
                if (playerVid != null) {
                    playerVid.release();
                }
                super.onBackPressed();
            }
        }
    }

}