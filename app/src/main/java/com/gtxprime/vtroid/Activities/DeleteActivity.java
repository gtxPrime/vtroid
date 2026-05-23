package com.gtxprime.vtroid.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Utils.Utils;
import com.gtxprime.vtroid.VideoPlayer.PlayerActivity;
import com.gtxprime.vtroid.model.ModelMediaFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class DeleteActivity extends AppCompatActivity {

    ArrayList<ModelMediaFiles> mediaUri = new ArrayList<>();
    int position;
    ConstraintLayout deleteBtn, playBtn, renameBtn, shareBtn, deleteAct;
    ImageView videoThumb;
    TextView fileName, filePath, fileSize, fileFormat, fileResolution, fileLength, videoName;
    Dialog deleteDialog;
    boolean isDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        mediaUri = com.gtxprime.vtroid.Utils.VideoListHolder.videoList;
        if (mediaUri == null) {
            if (getIntent().getExtras() != null) {
                mediaUri = getIntent().getExtras().getParcelableArrayList("videoArrayList");
            }
        }
        if (mediaUri == null) {
            mediaUri = new ArrayList<>();
        }
        isDelete = getIntent().getBooleanExtra("isDelete", false);
        position = getIntent().getIntExtra("position", 1);
        fileName = findViewById(R.id.fileName);
        filePath = findViewById(R.id.filePath);
        fileSize = findViewById(R.id.fileSize);
        fileFormat = findViewById(R.id.fileFormat);
        fileResolution = findViewById(R.id.fileResolution);
        fileLength = findViewById(R.id.fileLength);
        videoThumb = findViewById(R.id.videoThumb);
        deleteBtn = findViewById(R.id.deleteBtn);
        playBtn = findViewById(R.id.playBtn);
        shareBtn = findViewById(R.id.shareBtn);
        renameBtn = findViewById(R.id.renameBtn);
        deleteAct = findViewById(R.id.deleteAct);
        videoName = findViewById(R.id.videoName);

        deleteAct.setBackground(Utils.bgGrayGenerate(this));

        Utils.setPad(deleteAct, "bottom", this);

        if (mediaUri != null && !mediaUri.isEmpty() && position >= 0 && position < mediaUri.size()) {
            if (isDelete) {
                new Handler().postDelayed(() -> deleteBtn.performClick(), 500);
            }

            String path = mediaUri.get(position).getPath();
            int indexOfPath = path.lastIndexOf("/");
            int index = mediaUri.get(position).getDisplayName().lastIndexOf(".");
            String format = mediaUri.get(position).getDisplayName().substring(index + 1);
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(mediaUri.get(position).getPath());
            String height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String sixth = "Resolution: " + width + " x " + height;
            Uri contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, Long.parseLong(mediaUri.get(position).getId()));

            Glide.with(DeleteActivity.this).asBitmap().load(new File(mediaUri.get(position).getPath())).placeholder(R.drawable.placeholder).into(videoThumb);
            fileName.setText(String.format("File name: %s", mediaUri.get(position).getDisplayName()));
            videoName.setText(mediaUri.get(position).getDisplayName());
            filePath.setText(String.format("File path: %s", path.substring(0, indexOfPath)));
            fileSize.setText(String.format("Size: %s", android.text.format.Formatter.formatFileSize(DeleteActivity.this, Long.parseLong(mediaUri.get(position).getSize()))));
            fileLength.setText(String.format("Length: %s", timeConversion((long) Double.parseDouble(mediaUri.get(position).getDuration()))));
            fileFormat.setText(String.format("File format: %s", format));
            fileResolution.setText(sixth);

            deleteBtn.setOnClickListener(view -> {
                deleteDialog = new Dialog(DeleteActivity.this);
                deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                deleteDialog.setCancelable(true);
                deleteDialog.setContentView(R.layout.dialog_delete);
                TextView delete = deleteDialog.findViewById(R.id.deleteYes);
                TextView cancel = deleteDialog.findViewById(R.id.deleteCancel);

                delete.setOnClickListener(v -> deleteFile(contentUri));
                cancel.setOnClickListener(v -> deleteDialog.dismiss());
                deleteDialog.show();
            });

            playBtn.setOnClickListener(view -> {
                com.gtxprime.vtroid.Utils.VideoListHolder.videoList = mediaUri;
                startActivity(new Intent(DeleteActivity.this, PlayerActivity.class)
                        .putExtra("LocalPlayLink", mediaUri.get(position).getPath())
                        .putExtra("position", position));
            });
            renameBtn.setOnClickListener(view -> {
                Dialog renameDialog;
                renameDialog = new Dialog(DeleteActivity.this);
                renameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                renameDialog.setCancelable(true);
                renameDialog.setContentView(R.layout.dialog_rename);

                TextView renameYes = renameDialog.findViewById(R.id.renameYes);
                TextView renameCancel = renameDialog.findViewById(R.id.renameCancel);
                EditText renamedText = renameDialog.findViewById(R.id.renamedText);
                String pathX = mediaUri.get(position).getPath();
                final File file = new File(pathX);
                String originalName = file.getName();
                originalName = originalName.substring(0, originalName.lastIndexOf("."));
                renamedText.setText(originalName);
                renamedText.requestFocus();

                renameYes.setOnClickListener(v -> {
                    String onlyPath = Objects.requireNonNull(file.getParentFile()).getAbsolutePath();
                    String ext = file.getAbsolutePath();
                    ext = ext.substring(ext.lastIndexOf("."));
                    String newPath = onlyPath + "/" + renamedText.getText().toString() + ext;
                    File newFile = new File(newPath);
                    boolean rename = file.renameTo(newFile);
                    if (rename) {
                        ContentResolver resolver = getApplicationContext().getContentResolver();
                        resolver.delete(MediaStore.Files.getContentUri("external"),
                                MediaStore.MediaColumns.DATA + "=?", new String[]
                                        {file.getAbsolutePath()});
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.putExtra("video_title", fileName.getText().toString());
                        intent.setData(Uri.fromFile(newFile));
                        getApplicationContext().sendBroadcast(intent);

                        mediaUri.get(position).setPath(newPath);
                        fileName.setText(String.format("%s%s", renamedText.getText().toString(), ext));
                        Toast.makeText(DeleteActivity.this, "Video Renamed", Toast.LENGTH_SHORT).show();
                        renameDialog.dismiss();

                        SystemClock.sleep(200);
                        finish();
                        startActivity(getIntent());
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else {
                        Toast.makeText(DeleteActivity.this, "Process failed", Toast.LENGTH_SHORT).show();
                    }
                });
                renameCancel.setOnClickListener(v -> renameDialog.dismiss());
                renameDialog.show();
            });
            shareBtn.setOnClickListener(view -> {
                Uri uri = Uri.parse(mediaUri.get(position).getPath());
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("video/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(shareIntent, "Share Video via"));
            });
        }

    }

    private void deleteFile(Uri contentUri) {
        try {
            getContentResolver().delete(contentUri, null, null);
        } catch (SecurityException e) {
            PendingIntent pendingIntent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ArrayList<Uri> uris = new ArrayList<>();
                uris.add(contentUri);
                pendingIntent = MediaStore.createDeleteRequest(getContentResolver(), uris);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (e instanceof RecoverableSecurityException) {
                        RecoverableSecurityException exception = (RecoverableSecurityException) e;
                        pendingIntent = exception.getUserAction().getActionIntent();
                    }
                }
            }
            if (pendingIntent != null) {
                IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent.getIntentSender())
                        .setFillInIntent(null)
                        .setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION, 0)
                        .build();
                deleteLauncher.launch(intentSenderRequest);
            }
        }
        onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
    }


    public ActivityResultLauncher<IntentSenderRequest> deleteLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
            result -> {
                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                if (result.getResultCode() == RESULT_OK) {
                    onBackPressed();
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }
            });


    @SuppressLint("DefaultLocale")
    public String timeConversion(long value) {
        String videoTime;
        int duration = (int) value;
        int hrs = (duration / 3600000);
        int mns = (duration / 60000) % 60000;
        int scs = duration % 60000 / 1000;

        if (hrs > 0) {
            videoTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            videoTime = String.format("%02d:%02d", mns, scs);
        }
        return videoTime;

    }

}