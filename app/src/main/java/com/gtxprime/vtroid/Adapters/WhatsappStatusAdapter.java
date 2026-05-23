package com.gtxprime.vtroid.Adapters;


import static com.gtxprime.vtroid.Utils.Utils.RootDirectoryWhatsappShow;
import static com.gtxprime.vtroid.Utils.Utils.createFileFolder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.greenfrvr.rubberloader.RubberLoaderView;
import com.gtxprime.vtroid.Activities.PreviewActivity;
import com.gtxprime.vtroid.Interfaces.FileListWhatsappClickInterface;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Utils.Unity;
import com.gtxprime.vtroid.Utils.Utils;
import com.gtxprime.vtroid.VideoPlayer.PlayerActivity;
import com.gtxprime.vtroid.model.WhatsappStatusModel;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WhatsappStatusAdapter extends RecyclerView.Adapter<WhatsappStatusAdapter.ViewHolder> {
    private final Context context;
    private final Activity activity;
    private final ArrayList<WhatsappStatusModel> fileArrayList;
    private LayoutInflater layoutInflater;
    String fileName = "";
    public String saveFilePath = RootDirectoryWhatsappShow + File.separator;
    Dialog keyCheckDialog;

    public WhatsappStatusAdapter(Context context, ArrayList<WhatsappStatusModel> files, Activity activity) {
        this.context = context;
        this.fileArrayList = files;
        this.activity = activity;
    }

    public WhatsappStatusAdapter(Context context, ArrayList<WhatsappStatusModel> files, FileListWhatsappClickInterface fileListClickInterface, Activity activity) {
        this.context = context;
        this.fileArrayList = files;
        this.activity = activity;
    }

    @NonNull
    @Override
    public WhatsappStatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        View view = LayoutInflater.from(context).inflate(R.layout.holder_whatsapp_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WhatsappStatusAdapter.ViewHolder viewHolder, int i) {
        WhatsappStatusModel fileItem = fileArrayList.get(i);

        if (fileItem.getUri().toString().endsWith(".mp4")) {
            viewHolder.ivPlay.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivPlay.setVisibility(View.GONE);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            Glide.with(context).load(fileItem.getUri()).into(viewHolder.pcw);
        } else {
            Glide.with(context).load(fileItem.getPath()).into(viewHolder.pcw);
        }

        viewHolder.tvDownload.setOnClickListener(view -> {
            createFileFolder();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                try {
                    if (fileItem.getUri().toString().endsWith(".mp4")) {
                        fileName = "status_" + System.currentTimeMillis() + ".mp4";
                    } else {
                        fileName = "status_" + System.currentTimeMillis() + ".png";
                    }
                    DownloadFileTask(fileItem.getUri().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                loadFromAndroid10(fileArrayList.get(i));
            }

        });
        viewHolder.itemView.setOnClickListener(v -> {
            if (fileItem.getUri().toString().endsWith(".mp4")) {
                context.startActivity(new Intent(context, PlayerActivity.class).putExtra("playLink", fileItem.getUri().toString()));
            } else {
                Intent inNext = new Intent(context, PreviewActivity.class);
                inNext.putParcelableArrayListExtra("images", fileArrayList);
                inNext.putExtra("position", i);
                inNext.putExtra("statusdownload", "noDownload");
                context.startActivity(inNext);
            }
        });
    }

    private void loadFromAndroid10(WhatsappStatusModel fileItem) {
        final String path = fileItem.getPath();
        String filename = path.substring(path.lastIndexOf("/") + 1);
        final File file = new File(path);
        File destFile = new File(saveFilePath);
        try {
            FileUtils.copyFileToDirectory(file, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileNameChange = filename.substring(12);
        File newFile = new File(saveFilePath + fileNameChange);
        String contentType;
        if (fileItem.getUri().toString().endsWith(".mp4")) {
            contentType = "video/*";
        } else {
            contentType = "image/*";
        }
        MediaScannerConnection.scanFile(context, new String[]{newFile.getAbsolutePath()}, new String[]{contentType},
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    public void onMediaScannerConnected() {
                        //NA
                    }

                    public void onScanCompleted(String path, Uri uri) {
                        //NA
                    }
                });

        File from = new File(saveFilePath, filename);
        File to = new File(saveFilePath, fileNameChange);
        from.renameTo(to);
        Toast.makeText(context, context.getResources().getString(R.string.saved_to) + saveFilePath + fileNameChange, Toast.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        return fileArrayList == null ? 0 : fileArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlay;
        ImageView pcw;
        TextView tvDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlay = itemView.findViewById(R.id.iv_play);
            pcw = itemView.findViewById(R.id.pcw);
            tvDownload = itemView.findViewById(R.id.tv_download);
        }
    }

    public void DownloadFileTask(String... furl) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            //Background work here
            try {
                InputStream in = context.getContentResolver().openInputStream(Uri.parse(furl[0]));
                File f;
                f = new File(RootDirectoryWhatsappShow + File.separator + fileName);
                f.setWritable(true, true);
                OutputStream outputStream = new FileOutputStream(f);
                byte[] buffer = new byte[1024];
                int length;

                while ((length = in.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
                in.close();
            } catch (IOException e) {
                System.out.println("error in creating a file");
                e.printStackTrace();
            }
            handler.post(() -> {
                //UI Thread work here
                Utils.setToast(context, context.getResources().getString(R.string.download_complete));
                try {
                    MediaScannerConnection.scanFile(context, new String[]
                                    {new File(RootDirectoryWhatsappShow + File.separator + fileName).getAbsolutePath()},
                            null, (path, uri) -> {
                                //no action
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }


}