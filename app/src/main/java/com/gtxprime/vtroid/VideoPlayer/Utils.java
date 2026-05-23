package com.gtxprime.vtroid.VideoPlayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.LocaleList;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.multidex.BuildConfig;

import com.arthenica.ffmpegkit.FFmpegKitConfig;
import com.arthenica.ffmpegkit.FFprobeKit;
import com.arthenica.ffmpegkit.MediaInformation;
import com.arthenica.ffmpegkit.MediaInformationSession;
import com.arthenica.ffmpegkit.StreamInformation;
import com.google.android.exoplayer2.Format;
import com.gtxprime.vtroid.R;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class Utils {
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static float pxToDp(float px) {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    public static boolean fileExists(final Context context, final Uri uri) {
        final String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            try {
                final InputStream inputStream = context.getContentResolver().openInputStream(uri);
                inputStream.close();
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            String path;
            if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                path = uri.getPath();
            } else {
                path = uri.toString();
            }
            final File file = new File(path);
            return file.exists();
        }
    }

    public static void hideSystemUi(final com.gtxprime.vtroid.VideoPlayer.CustomStyledPlayerView playerView) {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // demo
//        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    public static String getFileName(Context context, Uri uri) {
        String result = null;
        try {
            if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                try (Cursor cursor = context.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        final int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (columnIndex > -1)
                            result = cursor.getString(columnIndex);
                    }
                }
            }
            if (result == null) {
                result = uri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
            if (result.indexOf(".") > 0)
                result = result.substring(0, result.lastIndexOf("."));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isVolumeMax(final AudioManager audioManager) {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public static boolean isVolumeMin(final AudioManager audioManager) {
        int min = Build.VERSION.SDK_INT >= 28 ? audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC) : 0;
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == min;
    }

    public static void adjustVolume(final AudioManager audioManager, final com.gtxprime.vtroid.VideoPlayer.CustomStyledPlayerView playerView, final boolean raise, boolean canBoost) {
        final int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        final int volumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        boolean volumeActive = volume != 0;

        // Handle volume changes outside the app (lose boost if volume is not maxed out)
        if (volume != volumeMax) {
            com.gtxprime.vtroid.VideoPlayer.PlayerActivity.boostLevel = 0;
        }

        if (com.gtxprime.vtroid.VideoPlayer.PlayerActivity.loudnessEnhancer == null)
            canBoost = false;

        if (volume != volumeMax || (com.gtxprime.vtroid.VideoPlayer.PlayerActivity.boostLevel == 0 && !raise)) {
            if (com.gtxprime.vtroid.VideoPlayer.PlayerActivity.loudnessEnhancer != null)
                com.gtxprime.vtroid.VideoPlayer.PlayerActivity.loudnessEnhancer.setEnabled(false);
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, raise ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            final int volumeNew = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (raise && volume == volumeNew && !isVolumeMin(audioManager)) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE | AudioManager.FLAG_SHOW_UI);
            } else {
                volumeActive = volumeNew != 0;
                playerView.setCustomErrorMessage(volumeActive ? " " + volumeNew : "");
            }
        } else {
            if (canBoost && raise && com.gtxprime.vtroid.VideoPlayer.PlayerActivity.boostLevel < 10)
                com.gtxprime.vtroid.VideoPlayer.PlayerActivity.boostLevel++;
            else if (!raise && com.gtxprime.vtroid.VideoPlayer.PlayerActivity.boostLevel > 0)
                com.gtxprime.vtroid.VideoPlayer.PlayerActivity.boostLevel--;

            if (com.gtxprime.vtroid.VideoPlayer.PlayerActivity.loudnessEnhancer != null) {
                try {
                    com.gtxprime.vtroid.VideoPlayer.PlayerActivity.loudnessEnhancer.setTargetGain(com.gtxprime.vtroid.VideoPlayer.PlayerActivity.boostLevel * 200);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
            playerView.setCustomErrorMessage(" " + (volumeMax + com.gtxprime.vtroid.VideoPlayer.PlayerActivity.boostLevel));
        }

        playerView.setIconVolume(volumeActive);
        if (com.gtxprime.vtroid.VideoPlayer.PlayerActivity.loudnessEnhancer != null)
            com.gtxprime.vtroid.VideoPlayer.PlayerActivity.loudnessEnhancer.setEnabled(com.gtxprime.vtroid.VideoPlayer.PlayerActivity.boostLevel > 0);
        playerView.setHighlight(com.gtxprime.vtroid.VideoPlayer.PlayerActivity.boostLevel > 0);
    }

    public static void setButtonEnabled(final Context context, final ImageButton button, final boolean enabled) {
        button.setEnabled(enabled);
        button.setAlpha(enabled ?
                        (float) 33 / 100 :
                        (float) 100 / 100
                );
    }

    public static void showText(final com.gtxprime.vtroid.VideoPlayer.CustomStyledPlayerView playerView, final String text, final long timeout) {
        playerView.removeCallbacks(playerView.textClearRunnable);
        playerView.clearIcon();
        playerView.setCustomErrorMessage(text);
        playerView.postDelayed(playerView.textClearRunnable, timeout);
    }

    public static void showText(final com.gtxprime.vtroid.VideoPlayer.CustomStyledPlayerView playerView, final String text) {
        showText(playerView, text, 1200);
    }

    public enum Orientation {
        VIDEO(0, R.string.video_orientation_video),
        SENSOR(1, R.string.video_orientation_sensor);

        public final int value;
        public final int description;

        Orientation(int type, int description) {
            this.value = type;
            this.description = description;
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public static void setOrientation(Activity activity, Orientation orientation) {
        switch (orientation) {
            case VIDEO:
                if (com.gtxprime.vtroid.VideoPlayer.PlayerActivity.playerVid != null) {
                    final Format format = com.gtxprime.vtroid.VideoPlayer.PlayerActivity.playerVid.getVideoFormat();
                    if (format != null && isPortrait(format))
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                    else
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                } else {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                }

                break;
            case SENSOR:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                break;
            /*case SYSTEM:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                break;*/
        }
    }

    public static Orientation getNextOrientation(Orientation orientation) {
        switch (orientation) {
            case VIDEO:
                return Orientation.SENSOR;
            case SENSOR:
            default:
                return Orientation.VIDEO;
        }
    }

    public static boolean isRotated(final Format format) {
        return format.rotationDegrees == 90 || format.rotationDegrees == 270;
    }

    public static boolean isPortrait(final Format format) {
        if (isRotated(format)) {
            return format.width > format.height;
        } else {
            return format.height > format.width;
        }
    }

    public static Rational getRational(final Format format) {
        if (isRotated(format))
            return new Rational(format.height, format.width);
        else
            return new Rational(format.width, format.height);
    }

    public static String formatMilis(long time) {
        final int totalSeconds = Math.abs((int) time / 1000);
        final int seconds = totalSeconds % 60;
        final int minutes = totalSeconds % 3600 / 60;
        final int hours = totalSeconds / 3600;

        return (hours > 0 ? String.format("%d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds));
    }

    public static String formatMilisSign(long time) {
        if (time > -1000 && time < 1000)
            return formatMilis(time);
        else
            return (time < 0 ? "−" : "+") + formatMilis(time);
    }

    public static void log(final String text) {
        if (BuildConfig.DEBUG) {
            Log.d("JustPlayer", text);
        }
    }

    public static void setViewMargins(final View view, int marginLeft, int marginTop, int marginRight, int marginBottom) {
        final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        view.setLayoutParams(layoutParams);
    }

    public static void setViewParams(final View view, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom, int marginLeft, int marginTop, int marginRight, int marginBottom) {
        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        setViewMargins(view, marginLeft, marginTop, marginRight, marginBottom);
    }

    public static boolean isSupportedNetworkUri(final Uri uri) {
        final String scheme = uri.getScheme();
        if (scheme == null)
            return false;
        return scheme.startsWith("http") || scheme.equals("rtsp") || scheme.startsWith("https");
    }


    public static int normRate(float rate) {
        return (int)(rate * 100f);
    }

    public static boolean switchFrameRate(final com.gtxprime.vtroid.VideoPlayer.PlayerActivity activity, final float frameRateExo, final Uri uri, final boolean play) {
        // preferredDisplayModeId only available on SDK 23+
        // ExoPlayer already uses Surface.setFrameRate() on Android 11+ but may not detect actual video frame rate
        if (Build.VERSION.SDK_INT < 30 || frameRateExo == Format.NO_VALUE) {
            String path;
            if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                path = FFmpegKitConfig.getSafParameterForRead(activity, uri);
            } else if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
                // TODO: FFprobeKit doesn't accept encoded uri (like %20) (?!)
                path = uri.getSchemeSpecificPart();
            } else {
                path = uri.toString();
            }
            // Use ffprobe as ExoPlayer doesn't detect video frame rate for lots of videos
            // and has different precision than ffprobe (so do not mix that)
            FFprobeKit.getMediaInformationAsync(path, session -> {
                if (session == null)
                    return;

                float frameRate = Format.NO_VALUE;

                MediaInformationSession mediaInformationSession;
                mediaInformationSession = session;

                MediaInformation mediaInformation = mediaInformationSession.getMediaInformation();
                if (mediaInformation == null)
                    return;
                List<StreamInformation> streamInformations = mediaInformation.getStreams();
                for (StreamInformation streamInformation : streamInformations) {
                    if (streamInformation.getType().equals("video")) {
                        String averageFrameRate = streamInformation.getAverageFrameRate();
                        if (averageFrameRate.contains("/")) {
                            String[] vals = averageFrameRate.split("/");
                            frameRate = Float.parseFloat(vals[0]) / Float.parseFloat(vals[1]);
                            break;
                        }
                    }
                }

                handleFrameRate(activity, frameRate, play);
            }, null, 8_000);
            return true;
        } else {
            return false;
        }
    }

    private static void handleFrameRate(final com.gtxprime.vtroid.VideoPlayer.PlayerActivity activity, float frameRate, boolean play) {
        activity.runOnUiThread(() -> {
            boolean switchingModes = false;

            if (BuildConfig.DEBUG)
                Toast.makeText(activity, "Video frameRate: " + frameRate, Toast.LENGTH_LONG).show();

            if (frameRate > 0) {
                Display display = activity.getWindow().getDecorView().getDisplay();
                if (display == null) {
                    return;
                }
                Display.Mode[] supportedModes = display.getSupportedModes();
                Display.Mode activeMode = display.getMode();

                if (supportedModes.length > 1) {
                    // Refresh rate >= video FPS
                    List<Display.Mode> modesHigh = new ArrayList<>();
                    // Max refresh rate
                    Display.Mode modeTop = activeMode;
                    int modesResolutionCount = 0;

                    // Filter only resolutions same as current
                    for (Display.Mode mode : supportedModes) {
                        if (mode.getPhysicalWidth() == activeMode.getPhysicalWidth() &&
                                mode.getPhysicalHeight() == activeMode.getPhysicalHeight()) {
                            modesResolutionCount++;

                            if (normRate(mode.getRefreshRate()) >= normRate(frameRate))
                                modesHigh.add(mode);

                            if (normRate(mode.getRefreshRate()) > normRate(modeTop.getRefreshRate()))
                                modeTop = mode;
                        }
                    }

                    if (modesResolutionCount > 1) {
                        Display.Mode modeBest = null;

                        for (Display.Mode mode : modesHigh) {
                            if (normRate(mode.getRefreshRate()) % normRate(frameRate) <= 0.0001f) {
                                if (modeBest == null || normRate(mode.getRefreshRate()) > normRate(modeBest.getRefreshRate())) {
                                    modeBest = mode;
                                }
                            }
                        }

                        Window window = activity.getWindow();
                        WindowManager.LayoutParams layoutParams = window.getAttributes();

                        if (modeBest == null)
                            modeBest = modeTop;

                        switchingModes = !(modeBest.getModeId() == activeMode.getModeId());
                        if (switchingModes) {
                            layoutParams.preferredDisplayModeId = modeBest.getModeId();
                            window.setAttributes(layoutParams);
                        }
                        if (BuildConfig.DEBUG)
                            Toast.makeText(activity, "Video frameRate: " + frameRate + "\nDisplay refreshRate: " + modeBest.getRefreshRate(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            if (!switchingModes) {
                if (play) {
                    if (com.gtxprime.vtroid.VideoPlayer.PlayerActivity.playerVid != null)
                        com.gtxprime.vtroid.VideoPlayer.PlayerActivity.playerVid.play();
                    if (activity.playerView != null)
                        activity.playerView.hideController();
                }
            }
        });
    }

    public static boolean isPiPSupported(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE);
    }


    public static boolean isProgressiveContainerUri(final Uri uri) {
        String path = uri.getPath();
        if (path == null) {
            return false;
        }
        path = path.toLowerCase();
        // FIXME: Have a single list of supported containers - same as from file system?
        return (path.endsWith(".3gp") || path.endsWith(".m4v") || path.endsWith(".mkv") || path.endsWith(".mov")
                || path.endsWith(".mp4") || path.endsWith(".ts") || path.endsWith(".webm"));
    }

    public static String[] getDeviceLanguages() {
        final List<String> locales = new ArrayList<>();
        final LocaleList localeList = Resources.getSystem().getConfiguration().getLocales();
        for (int i = 0; i < localeList.size(); i++) {
            locales.add(localeList.get(i).getISO3Language());
        }
        return locales.toArray(new String[0]);
    }
}
