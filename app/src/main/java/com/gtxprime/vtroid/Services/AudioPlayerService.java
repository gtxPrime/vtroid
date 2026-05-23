package com.gtxprime.vtroid.Services;

import static android.app.NotificationManager.IMPORTANCE_LOW;

import static com.gtxprime.vtroid.Activities.MusicPlayer.allSongs;
import static com.gtxprime.vtroid.Activities.MusicPlayer.isActivityRunning;
import static com.gtxprime.vtroid.Activities.MusicPlayer.playerView;
import static com.gtxprime.vtroid.Utils.Utils.IS_ACTIVITY_RUNNING;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.gtxprime.vtroid.Activities.MusicPlayer;
import com.gtxprime.vtroid.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class AudioPlayerService extends Service {
    MediaSessionCompat mediaSession;
    private final IBinder serviceBinder = new ServiceBinder();
    public static ExoPlayer player;
    public PlayerNotificationManager playerNotificationManager;

    public static boolean isRunning = true;

    public static class ServiceBinder extends Binder {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IS_ACTIVITY_RUNNING = true;
        player = new ExoPlayer.Builder(this).build();
        mediaSession = new MediaSessionCompat(this, "V-Troid");
        MediaSessionConnector mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setPlayer(player);

        mediaSessionConnector.setEnabledPlaybackActions(
                PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_SEEK_TO
                        | PlaybackStateCompat.ACTION_FAST_FORWARD
                        | PlaybackStateCompat.ACTION_REWIND
                        | PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_SET_REPEAT_MODE
                        | PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
        );

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .build();
        player.setAudioAttributes(audioAttributes, true);
        mediaSession.setActive(true);

        final String channelId = getResources().getString(R.string.app_name) + "Music Channel";
        final int notificationId = 11111111;
        playerNotificationManager = new PlayerNotificationManager.Builder(this, notificationId, channelId)
                .setNotificationListener(notificationListener)
                .setMediaDescriptionAdapter(mediaDescriptionAdapter)
                .setChannelImportance(IMPORTANCE_LOW)
                .setChannelDescriptionResourceId(R.string.app_name)
                .setNextActionIconResourceId(R.drawable.ic_next)
                .setPreviousActionIconResourceId(R.drawable.ic_previous)
                .setPauseActionIconResourceId(R.drawable.ic_pause)
                .setPlayActionIconResourceId(R.drawable.ic_play)
                .setChannelNameResourceId(R.string.app_name)
                .setStopActionIconResourceId(R.drawable.ic_close)
                .build();

        playerNotificationManager.setPlayer(player);
        playerNotificationManager.setPriority(NotificationCompat.PRIORITY_LOW);
        playerNotificationManager.setUseRewindAction(false);
        playerNotificationManager.setUseFastForwardAction(false);
        playerNotificationManager.setColorized(true);
        playerNotificationManager.setUseStopAction(true);
        playerNotificationManager.setUsePreviousActionInCompactView(true);
        playerNotificationManager.setUseNextActionInCompactView(true);
        playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());
    }

    PlayerNotificationManager.NotificationListener notificationListener = new PlayerNotificationManager.NotificationListener() {
        @Override
        public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
            PlayerNotificationManager.NotificationListener.super.onNotificationCancelled(notificationId, dismissedByUser);
            stopForeground(true);
            if (isActivityRunning) {
                if (player.isPlaying()) {
                    player.pause();
                    player.release();
                    player.clearMediaItems();
                }
            }
        }

        @Override
        public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
            PlayerNotificationManager.NotificationListener.super.onNotificationPosted(notificationId, notification, ongoing);
            startForeground(notificationId, notification);
        }
    };

    PlayerNotificationManager.MediaDescriptionAdapter mediaDescriptionAdapter = new PlayerNotificationManager.MediaDescriptionAdapter() {

        @Override
        public CharSequence getCurrentContentTitle(Player player) {
            return allSongs.get(player.getCurrentMediaItemIndex()).getTitle();
        }

        @Nullable
        @Override
        public PendingIntent createCurrentContentIntent(Player player) {
            Intent openAppIntent = new Intent(getApplicationContext(), MusicPlayer.class).putExtra("noti", "notify");
            playerView.setPlayer(player);
            return PendingIntent.getActivity(getApplicationContext(), 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }

        @Nullable
        @Override
        public CharSequence getCurrentContentText(Player player) {
            return allSongs.get(player.getCurrentMediaItemIndex()).getArtistName();
        }

        @Nullable
        @Override
        public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
            Bitmap bitmap;
            Drawable drawable;
            try {
                InputStream inputStream = getContentResolver().openInputStream(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.artworkUri);
                drawable = Drawable.createFromStream(inputStream, String.valueOf(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.artworkUri));
            } catch (FileNotFoundException e) {
                drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.music_thumb, null);
            }

            assert drawable != null;
            bitmap = ((BitmapDrawable) drawable).getBitmap();

            ImageView view = new ImageView(getApplicationContext());
            view.setImageBitmap(bitmap);

            BitmapDrawable bitmapDrawable = (BitmapDrawable) view.getDrawable();
            if (bitmapDrawable == null) {
                bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.music_thumb);
            }

            assert bitmapDrawable != null;
            return bitmapDrawable.getBitmap();
        }
    };

    @Override
    public void onDestroy() {
        if (player.isPlaying()) player.stop();
        playerNotificationManager.setPlayer(null);
        player.release();
        player.clearMediaItems();
        isRunning = false;

        stopForeground(true);
        stopSelf();
        super.onDestroy();
        mediaSession.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

}
