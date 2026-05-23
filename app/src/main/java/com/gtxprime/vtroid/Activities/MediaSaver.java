package com.gtxprime.vtroid.Activities;

import static android.content.ContentValues.TAG;
import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static com.gtxprime.vtroid.Activities.Home.closeDrawer;
import static com.gtxprime.vtroid.Utils.Utils.createFileFolder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;

import com.google.android.material.tabs.TabLayout;
import com.greenfrvr.rubberloader.RubberLoaderView;
import com.gtxprime.vtroid.Fragments.WhatsappImageFragment;
import com.gtxprime.vtroid.Fragments.WhatsappQImageFragment;
import com.gtxprime.vtroid.Fragments.WhatsappQVideoFragment;
import com.gtxprime.vtroid.Fragments.WhatsappVideoFragment;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Utils.KMoSupport;
import com.gtxprime.vtroid.Utils.Unity;
import com.gtxprime.vtroid.Utils.Utils;
import com.gtxprime.vtroid.api.CommonClassForAPI;
import com.gtxprime.vtroid.model.TwitterVideoDownloader;
import com.gtxprime.vtroid.model.VimeoVideoDownloader;
import com.kessi.allstatussaver.utils.InstaDownload;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaSaver extends AppCompatActivity {
    LottieAnimationView loaderLay;
    ViewPager viewPager;
    String[] tabs;
    TabLayout tabLayout;
    CommonClassForAPI commonClassForAPI;
    public ArrayList<Uri> fileArrayList;
    Dialog waPermissionDialog, keyCheckDialog;
    EditText link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_saver);

        Utils.setPad(findViewById(R.id.saverHolder), "bottom", this);
        closeDrawer();
        findViewById(R.id.saverHolder).setBackground(Utils.bgGrayGenerate(this));

        loaderLay = findViewById(R.id.lottieAnim);
        viewPager = findViewById(R.id.viewpager);
        link = findViewById(R.id.dowLink);
        commonClassForAPI = CommonClassForAPI.getInstance(this);
        waPermissionDialog = new Dialog(this, R.style.MyDialogStyle);
        fileArrayList = new ArrayList<>();
        initView();

        ImageView menuIconHolderDown = findViewById(R.id.imBackSaver);
        menuIconHolderDown.setOnClickListener(v -> onBackPressed());

        link.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                String inputLink = link.getText().toString().trim().toLowerCase();
                if (!inputLink.isEmpty()) {
                    processLink(inputLink, MediaSaver.this, MediaSaver.this);
                    link.setText("");
                }
            }
            return true;
        });

        findViewById(R.id.btnGo).setOnClickListener(v -> {
            String inputLink = link.getText().toString().trim().toLowerCase();
            if (!inputLink.isEmpty()) {
                processLink(inputLink, MediaSaver.this, MediaSaver.this);
                link.setText("");
            }
        });

        Intent intent = getIntent();
        String dowLink = intent.getStringExtra("dowLinks");
        if (dowLink != null) {
            processLink(dowLink.trim().toLowerCase(), MediaSaver.this, MediaSaver.this);
        }

    }



    public static void processLink(String link, Context context, Activity activity) {
        if (link.contains("instagram")) {
            try {
                createFileFolder();
                URL url = new URL(link);
                String host = url.getHost();
                Log.e("initViews: ", host);
                if (host.equals("www.instagram.com")) {
                    InstaDownload.INSTANCE.startInstaDownload(String.valueOf(url), activity);
                } else {
                    Utils.setToast(context, context.getResources().getString(R.string.enter_valid_url));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (link.contains("vimeo")) {
            VimeoVideoDownloader downloader = new VimeoVideoDownloader(context, link);
            downloader.DownloadVideo();
        } else if (link.contains("twitter")) {
            TwitterVideoDownloader downloader = new TwitterVideoDownloader(context, link);
            downloader.DownloadVideo();
        } else if (link.contains("roposo")) {
            Utils.roposo(context, link);
        } else if (link.contains("moj")) {
            KMoSupport.KMojDownload(context, link);
        } else if (link.contains("mitron")) {
            Utils.mitronDownload(context, link);
        } else if (link.contains("chingari")) {
            Utils.chingariDownload(context, link);
        } else if (link.contains("fb") || link.contains("facebook")) {
            //TODO
        } else {
            Toast.makeText(context, "Link is not supported at now", Toast.LENGTH_SHORT).show();
        }
    }
    private void initView() {
        tabs = new String[2];
        tabs[0] = getResources().getString(R.string.wapp);
        tabs[1] = getResources().getString(R.string.wbapp);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (this.getContentResolver().getPersistedUriPermissions().size() > 0) {
                LoadAllFiles(true);
            } else {
                waPermissionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                waPermissionDialog.setCancelable(false);
                waPermissionDialog.setCanceledOnTouchOutside(false);
                waPermissionDialog.setContentView(R.layout.dialog_wa_permission);
                TextView allow = waPermissionDialog.findViewById(R.id.allowYes);
                allow.setOnClickListener(v -> checkWhatsAppPermission());
                waPermissionDialog.show();
            }
        } else {
            setupViewPager(viewPager);
        }

    }
    private void checkWhatsAppPermission() {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                StorageManager sm = (StorageManager) this.getSystemService(Context.STORAGE_SERVICE);
                Intent intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
                String startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";
                Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");
                String scheme = uri.toString();
                scheme = scheme.replace("/root/", "/document/");
                scheme += "%3A" + startDir;
                uri = Uri.parse(scheme);
                intent.putExtra("android.provider.extra.INITIAL_URI", uri);
                someActivityResultLauncher.launch(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        try {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                Uri dataUri;
                if (data != null) {
                    dataUri = data.getData();
                    if (dataUri.toString().contains(".Statuses")) {
                        this.getContentResolver().takePersistableUriPermission(dataUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                            LoadAllFiles(false);
                            waPermissionDialog.dismiss();
                            waPermissionDialog.cancel();
                        }
                    } else {
                        Toast.makeText(this, "You have selected wrong folder", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    });
    public View getTabViewUn(int pos) {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView txt = v.findViewById(R.id.tab);
        txt.setText(tabs[pos]);
        txt.setTextColor(ResourcesCompat.getColor(getResources(), R.color.tab_txt_unpress, null));
        txt.setBackgroundResource(R.drawable.unpress_tab);
        FrameLayout.LayoutParams tab = new FrameLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels * 438 / 1080,
                getResources().getDisplayMetrics().heightPixels * 140 / 1920);
        txt.setLayoutParams(tab);
        return v;
    }
    private void setupViewPager(ViewPager viewPager) {
        loaderLay.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new WhatsappImageFragment(), "Images");
        adapter.addFragment(new WhatsappVideoFragment(), "Videos");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            Objects.requireNonNull(tabLayout.getTabAt(i)).setCustomView(getTabViewUn(i));
        }
    }
    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        ViewPagerAdapter(FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    public void LoadAllFiles(boolean get) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            //Background work here
            if (get) {
                loaderLay.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
            }
            DocumentFile documentFile = DocumentFile.fromTreeUri(this, this.getContentResolver().getPersistedUriPermissions().get(0).getUri());
            if (documentFile != null) {
                for (DocumentFile file : documentFile.listFiles()) {
                    if (file.isDirectory()) {
                        Log.d("isDirectory", "True");
                    } else {
                        if (!Objects.equals(file.getName(), ".nomedia")) {
                            fileArrayList.add(file.getUri());
                        }
                    }
                }
            }
            handler.post(() -> {
                //UI Thread work here
                loaderLay.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
                ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
                adapter.addFragment(new WhatsappQImageFragment(fileArrayList), "Images");
                adapter.addFragment(new WhatsappQVideoFragment(fileArrayList), "Videos");
                viewPager.setAdapter(adapter);
                viewPager.setOffscreenPageLimit(1);
                tabLayout.setupWithViewPager(viewPager);
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    Objects.requireNonNull(tabLayout.getTabAt(i)).setCustomView(getTabViewUn(i));
                }

            });
        });
    }


}

