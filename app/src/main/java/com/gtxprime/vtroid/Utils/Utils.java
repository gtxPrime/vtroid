package com.gtxprime.vtroid.Utils;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.DIRECTORY_DOWNLOADS;

import static com.gtxprime.vtroid.Activities.Settings.themeColor;
import static com.gtxprime.vtroid.Activities.Settings.themeColorPrefName;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.model.TwitterVideoDownloader;
import com.gtxprime.vtroid.model.VimeoVideoDownloader;
import com.shashank.sony.fancytoastlib.FancyToast;


import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static Unity unity;
    public static int perRequest = 21;
    public static boolean loadShow;
    public static Dialog customDialog;
    public static String instaDirPath = "V Troid/Instagram/";

    public static String otherDirPath = "All Saver/Other/";
    public static String RootDirectoryFacebook = "/V Troid/Facebook/";
    public static String RootDirectoryInsta = "/V Troid/Instagram/";
    public static File RootDirectoryFacebookShow = new File(Environment.getExternalStorageDirectory() + "/Download/V Troid/Facebook/");
    public static File RootDirectoryInstaShow = new File(Environment.getExternalStorageDirectory() + "/Download/V Troid/Instagram");
    public static File RootDirectoryWhatsappShow = new File(Environment.getExternalStorageDirectory() + "/" + DIRECTORY_DCIM + "/V Troid/WhatsApp Status");
    public static File RootDirectoryExtrasShow = new File(Environment.getExternalStorageDirectory() + "/Download/V Troid/Extras");
    public static boolean IS_ACTIVITY_RUNNING = false;
    public static final String PREMIUM = "isPremium";
    public static final String premiumBol = "premium";

    public Utils(Context _mContext) {
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void setToast(Context _mContext, String str) {
        Toast toast = Toast.makeText(_mContext, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void createFileFolder() {
        if (!RootDirectoryFacebookShow.exists()) {
            RootDirectoryFacebookShow.mkdirs();
        }
        if (!RootDirectoryInstaShow.exists()) {
            RootDirectoryInstaShow.mkdirs();
        }
        if (!RootDirectoryWhatsappShow.exists()) {
            RootDirectoryWhatsappShow.mkdirs();
        }
        if (!RootDirectoryExtrasShow.exists()) {
            RootDirectoryExtrasShow.mkdirs();
        }

    }

    public static void showProgressDialog(Activity activity) {
        System.out.println("Show");
        if (customDialog != null) {
            customDialog.dismiss();
            customDialog = null;
        }
        customDialog = new Dialog(activity);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View mView = inflater.inflate(R.layout.dialog_progress, null);
        customDialog.setCancelable(false);
        customDialog.setContentView(mView);
        if (!customDialog.isShowing() && !activity.isFinishing()) {
            customDialog.show();
        }
    }

    public static void hideProgressDialog() {
        System.out.println("Hide");
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo =
                connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void shareVideo(Context context, String filePath) {
        Uri mainUri = Uri.parse(filePath);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("video/mp4");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getResources().getString(R.string.no_app_installed), Toast.LENGTH_LONG).show();
        }
    }

    public static void startDownload(String downloadPath, String destinationPath, Context context, String FileName) {
        setToast(context, context.getResources().getString(R.string.download_started));
        Uri uri = Uri.parse(downloadPath); // Path where you want to download file.
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
        request.setTitle(FileName + ""); // Title for notification.
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, destinationPath + FileName);  // Storage directory path
        ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading

        try {
            MediaScannerConnection.scanFile(context, new String[]{new File(DIRECTORY_DOWNLOADS + "/" + destinationPath + FileName).getAbsolutePath()},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void DownloadingFace(final Context context, String url, String title) {
        String cutTitle;

        cutTitle = title + ".mp4";

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(title);
        request.setDescription(context.getString(R.string.downloading_des));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, RootDirectoryFacebook + cutTitle);
        Utils.ShowToast(context, context.getResources().getString(R.string.don_start));

        request.allowScanningByMediaScanner();
        long downloadID = downloadManager.enqueue(request);
        Log.e("downloadFileName", cutTitle);
    }

    public static void OpenApp(Context context, String Package) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(Package);
        if (launchIntent != null) {
            context.startActivity(launchIntent);
        } else {
            setToast(context, context.getResources().getString(R.string.app_not_available));
        }
    }

    public static boolean isNullOrEmpty(String s) {
        return (s == null) || (s.length() == 0) || (s.equalsIgnoreCase("null")) || (s.equalsIgnoreCase("0"));
    }

    public static void ShowToast(Context context, String str) {
        FancyToast.makeText(context, str, FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
    }

    public static String makeLink(String url) {
        String prefix = "https://www.google.com/search?q=";
        if (!url.startsWith("http://") && !url.startsWith("https://") &&
                !url.endsWith(".com")) {
            url = prefix + url;
        }
        if (url.endsWith(".com") || url.endsWith(".in") || url.endsWith(".xyz") || url.endsWith(".uk") || url.endsWith(".as")) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
        }
        return url;
    }

    public static void returnToHome(Context context, Class goTo) {
        context.startActivity(new Intent(context, goTo));
    }

    public static void setPad(View view, String angle, Activity context) {
        WindowCompat.setDecorFitsSystemWindows(context.getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            if (angle.equals("top")) {
                mlp.topMargin = insets.top;
            } else if (
                    angle.equals("bottom")) {
                mlp.bottomMargin = insets.bottom;
            }

            v.setLayoutParams(mlp);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    public static boolean download(Context context, String path) {
        return copyFileInSavedDir(context, path);
    }

    static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    static boolean copyFileInSavedDir(Context context, String file) {
        try {
            if (isImageFile(file)) {
                FileUtils.copyFileToDirectory(new File(file), getDir());
                mediaScanner(context, getDir() + "/", file, "image/*");
            } else {
                FileUtils.copyFileToDirectory(new File(file), getDir());
                mediaScanner(context, getDir() + "/", file, "video/*");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static File getDir() {
        RootDirectoryWhatsappShow.mkdirs();
        return RootDirectoryWhatsappShow;

    }

    public static void mediaScanner(Context context, String newFilePath, String oldFilePath, String fileType) {
        try {
            MediaScannerConnection.scanFile(context, new String[]{newFilePath + new File(oldFilePath).getName()}, new String[]{fileType},
                    new MediaScannerConnection.MediaScannerConnectionClient() {
                        public void onMediaScannerConnected() {
                        }

                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AlertDialog alertDialog = null;

    public static void displayLoader(Context context) {
        if (alertDialog == null) {

            LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
            View view = layoutInflaterAndroid.inflate(R.layout.dialog_progress, null);
            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setView(view);

            alertDialog = alert.create();
            alertDialog.setCancelable(false);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        }

    }

    public static void dismissLoader() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    public static GradientDrawable bgGrayGenerate(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(themeColorPrefName, MODE_PRIVATE);
        int savedColor = preferences.getInt(themeColor, context.getColor(R.color.primary));

//        SharedPreferences preferencesX = context.getSharedPreferences(themeSecPrefName, MODE_PRIVATE);
//        int savedSecColor = preferencesX.getInt(themeSecColor, context.getColor(R.color.mainSec));

        return new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{savedColor, context.getColor(R.color.mainSec), context.getColor(R.color.mainSec)});
    }

    public static void downloader(Context context, String downloadURL, String path, String fileName) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(context, "" + "Download Started", Toast.LENGTH_SHORT).show());

        String desc = "Please wait downloading media";
        Uri Download_Uri = Uri.parse(downloadURL);


        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(true);
        request.setTitle(context.getString(R.string.app_name));
        request.setVisibleInDownloadsUi(true);
        request.setDescription(desc);
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, path + fileName);
        dm.enqueue(request);

        Utils.mediaScanner(context, path, fileName);
    }

    public static void mediaScanner(Context context, String filePath, String fileName) {
        try {
            MediaScannerConnection.scanFile(context, new String[]{new File(DIRECTORY_DOWNLOADS + "/" + filePath + fileName).getAbsolutePath()},
                    null, (path, uri) -> {
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void chingariDownload(Context context, String url) {
        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            url = Objects.requireNonNull(document.select("meta[property=\"og:video\"]").last()).attr("content");
            if (!url.equals("")) {

                try {
                    String timeStamp = String.valueOf(System.currentTimeMillis());
                    String file = "chingari" + "_" + timeStamp;
                    String ext = "mp4";
                    String fileName = file + "." + ext;
                    Utils.downloader(context, url, Utils.otherDirPath, fileName);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void mitronDownload(Context context, String url) {
        String splitUrl;
        Document document;
        try {
            if (url.contains("api.mitron.tv")) {
                String[] split = url.split("=");
                splitUrl = "https://web.mitron.tv/video/" + split[split.length - 1];
            } else {
                splitUrl = url;
            }
            document = Jsoup.connect(splitUrl).get();
            mitronAfter(document, splitUrl, context);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void mitronAfter(Document document, String url, Context context) {
        try {
            String html = Objects.requireNonNull(document.select("script[id=\"__NEXT_DATA__\"]").last()).html();
            if (!html.equals("")) {
                url = String.valueOf(new JSONObject(html).getJSONObject("props").getJSONObject("pageProps").getJSONObject("video").get("videoUrl"));

                if (!url.equals("")) {

                    try {

                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        String file = "mitron" + "_" + timeStamp;
                        String ext = "mp4";
                        String fileName = file + "." + ext;

                        Utils.downloader(context, url, Utils.otherDirPath, fileName);

                        url = "";


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void mojDownload(Context context, String url) {
        KMoSupport.KMojDownload(context, url);
    }

    public static void roposo(Context context, String url) {
        Document document;
        try {
            document = Jsoup.connect(url).get();
            roposoAfter(document, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void roposoAfter(Document document, Context context) {
        try {
            String url = document.select("meta[property=\"og:video\"]").last().attr("content");
            if (!url.equals("")) {

                try {
                    String timeStamp = String.valueOf(System.currentTimeMillis());
                    String file = "roposo" + "_" + timeStamp;
                    String ext = "mp4";
                    String fileName = file + "." + ext;
                    Utils.downloader(context, url, Utils.otherDirPath, fileName);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
