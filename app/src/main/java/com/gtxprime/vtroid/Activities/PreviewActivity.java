package com.gtxprime.vtroid.Activities;

import static com.gtxprime.vtroid.Utils.Utils.RootDirectoryWhatsappShow;
import static com.gtxprime.vtroid.Utils.Utils.createFileFolder;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.gtxprime.vtroid.Adapters.PreviewAdapter;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Utils.Utils;
import com.gtxprime.vtroid.model.WhatsappStatusModel;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PreviewActivity extends AppCompatActivity {

    ViewPager viewPager;
    ArrayList<WhatsappStatusModel> imageList;
    int position;
    LinearLayout downloadIV, shareIV, deleteIV, wAppIV;
    PreviewAdapter previewAdapter;
    public String saveFilePath = RootDirectoryWhatsappShow + File.separator;
    String statusDownload;
    String fileName = "";
    ImageView backIV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        getWindow().setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));

        backIV = findViewById(R.id.backIV);
        viewPager = findViewById(R.id.viewPager);
        shareIV = findViewById(R.id.shareIV);
        downloadIV = findViewById(R.id.downloadIV);
        deleteIV = findViewById(R.id.deleteIV);
        wAppIV = findViewById(R.id.repostIV);

        imageList = getIntent().getParcelableArrayListExtra("images");
        position = getIntent().getIntExtra("position", -1);
        statusDownload = getIntent().getStringExtra("statusdownload");

        if (statusDownload.equals("download")) {
            downloadIV.setVisibility(View.GONE);
        } else {
            downloadIV.setVisibility(View.VISIBLE);
        }

        previewAdapter = new PreviewAdapter(PreviewActivity.this, imageList);
        viewPager.setAdapter(previewAdapter);
        viewPager.setCurrentItem(position);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int positionX, float positionOffset, int positionOffsetPixels) {
                position = positionX;
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        downloadIV.setOnClickListener(clickListener);
        shareIV.setOnClickListener(clickListener);
        deleteIV.setOnClickListener(clickListener);
        backIV.setOnClickListener(clickListener);
        wAppIV.setOnClickListener(clickListener);

        /*admob*/
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.backIV) {
                onBackPressed();
            } else if (id == R.id.downloadIV) {
                if (imageList.size() > 0) {
                    //ads
                    createFileFolder();
                    WhatsappStatusModel fileItem = imageList.get(viewPager.getCurrentItem());

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                        try {
                            if (fileItem.getUri().toString().endsWith(".mp4")) {
                                fileName = "status_" + System.currentTimeMillis() + ".mp4";
                                DownloadFileTask(fileItem.getUri().toString());
                            } else {
                                fileName = "status_" + System.currentTimeMillis() + ".png";
                                DownloadFileTask(fileItem.getUri().toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
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
                        MediaScannerConnection.scanFile(PreviewActivity.this, new String[]{newFile.getAbsolutePath()}, new String[]{contentType},
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
                        Toast.makeText(PreviewActivity.this, PreviewActivity.this.getResources().getString(R.string.saved_to) + saveFilePath + fileNameChange, Toast.LENGTH_LONG).show();
                    }
                } else {
                    finish();
                }
            } else if (id == R.id.shareIV) {
                if (imageList.size() > 0) {
                    if (isImageFile(imageList.get(viewPager.getCurrentItem()).getPath())) {
                        File imageFileToShare = new File(imageList.get(viewPager.getCurrentItem()).getPath());
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        share.setType("image/*");
                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                getApplicationContext().getPackageName() + ".provider", imageFileToShare);
                        share.putExtra(Intent.EXTRA_STREAM, photoURI);
                        startActivity(Intent.createChooser(share, "Share via"));

                    } else if (isVideoFile(imageList.get(viewPager.getCurrentItem()).getPath())) {

                        Uri videoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext()
                                .getPackageName() + ".provider", new File(imageList.get(viewPager.getCurrentItem()).getPath()));
                        Intent videoshare = new Intent(Intent.ACTION_SEND);
                        videoshare.setType("*/*");
                        videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        videoshare.putExtra(Intent.EXTRA_STREAM, videoURI);

                        startActivity(videoshare);
                    }
                } else {
                    finish();
                }
            } else if (id == R.id.deleteIV) {
                if (imageList.size() > 0) {
                    Dialog alertDialog = new Dialog(PreviewActivity.this);
                    alertDialog.setTitle(R.string.confirm);
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setCancelable(true);
                    alertDialog.setContentView(R.layout.dialog_delete);

                    TextView delete = alertDialog.findViewById(R.id.deleteYes);
                    TextView cancel = alertDialog.findViewById(R.id.deleteCancel);

                    delete.setOnClickListener(v1 -> {
                        alertDialog.dismiss();
                        int currentItem = 0;

                        File file = new File(imageList.get(viewPager.getCurrentItem()).getPath());
                        if (file.exists()) {
                            if (imageList.size() > 0 && viewPager.getCurrentItem() < imageList.size()) {
                                currentItem = viewPager.getCurrentItem();
                            }
                            imageList.remove(viewPager.getCurrentItem());
                            previewAdapter = new PreviewAdapter(PreviewActivity.this, imageList);
                            viewPager.setAdapter(previewAdapter);

                            Intent intent = new Intent();
                            setResult(10, intent);

                            if (imageList.size() > 0) {
                                viewPager.setCurrentItem(currentItem);
                            } else {
                                finish();
                            }
                            alertDialog.dismiss();
                        }
                    });
                    cancel.setOnClickListener(v1 -> alertDialog.dismiss());
                    alertDialog.show();
                } else {
                    finish();
                }
            } else if (id == R.id.repostIV) {
                if (isImageFile(imageList.get(viewPager.getCurrentItem()).getPath())) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/*");
                    share.setPackage("com.whatsapp");
                    File imageFileToShare = new File(imageList.get(viewPager.getCurrentItem()).getPath());
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext()
                            .getPackageName() + ".provider", imageFileToShare);
                    share.putExtra(Intent.EXTRA_STREAM, photoURI);
                    startActivity(Intent.createChooser(share, "Share Image!"));
                } else if (isVideoFile(imageList.get(viewPager.getCurrentItem()).getPath())) {
                    Uri videoURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext()
                            .getPackageName() + ".provider", new File(imageList.get(viewPager.getCurrentItem()).getPath()));
                    Intent videoshare = new Intent(Intent.ACTION_SEND);
                    videoshare.setType("*/*");
                    videoshare.setPackage("com.whatsapp");
                    videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    videoshare.putExtra(Intent.EXTRA_STREAM, videoURI);
                    startActivity(videoshare);
                }
            }
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public void DownloadFileTask(String... furl) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            //Background work here
            try {
                InputStream in = PreviewActivity.this.getContentResolver().openInputStream(Uri.parse(furl[0]));
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
                Utils.setToast(PreviewActivity.this, PreviewActivity.this.getResources().getString(R.string.download_complete));
                try {
                    MediaScannerConnection.scanFile(PreviewActivity.this, new String[]
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
