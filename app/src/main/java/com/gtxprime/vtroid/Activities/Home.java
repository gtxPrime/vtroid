package com.gtxprime.vtroid.Activities;

import static com.gtxprime.vtroid.Activities.Settings.themeColor;
import static com.gtxprime.vtroid.Activities.Settings.themeColorPrefName;
import static com.gtxprime.vtroid.Utils.Utils.PREMIUM;
import static com.gtxprime.vtroid.Utils.Utils.premiumBol;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;


import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.greenfrvr.rubberloader.RubberLoaderView;
import com.gtxprime.vtroid.Fragments.Downloads_fragment;
import com.gtxprime.vtroid.Fragments.Home_fragment;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Utils.Utils;
import com.gtxprime.vtroid.VideoPlayer.PlayerActivity;
import com.gtxprime.vtroid.VideoPlayer.SettingsActivity;

import java.util.Objects;

public class Home extends AppCompatActivity {

    NavigationView navigationView;
    ConstraintLayout contentView;
    FirebaseFirestore firebaseFirestore;
    Dialog keyDialog, keyVerifyDialog, keyCheckDialog, enterPremium;
    Intent intent;
    Uri webLink;
    String action, linkX, type, work = "media";
    ConstraintLayout videoActivity, musicActivity, headerMainHolder;
    public SharedPreferences isPremium, premiumKey, premiumPref, checkPreKey, firstRun;
    boolean premiumPrefBoolean;
    public static boolean keyChecked = false;
    public static DrawerLayout drawerLayout;
    ShapeableImageView homeSelectHolder;
    int savedColor;

    CardView donationHolder;
    int key;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        drawerLayout = findViewById(R.id.drawer_layout);
        checkLink();


        Utils.setPad(drawerLayout, "bottom", this);

        isPremium = getSharedPreferences(PREMIUM, Context.MODE_PRIVATE);
        premiumKey = getSharedPreferences("Premium Key", Context.MODE_PRIVATE);
        premiumPref = getSharedPreferences(PREMIUM, Context.MODE_PRIVATE);
        premiumPrefBoolean = premiumPref.getBoolean(premiumBol, false);
        navigationView = findViewById(R.id.navigation_view);
        contentView = findViewById(R.id.contentX);
        firstRun = getSharedPreferences("first", MODE_PRIVATE);
        videoActivity = findViewById(R.id.hV);
        musicActivity = findViewById(R.id.hM);
        intent = getIntent();
        webLink = intent.getData();
        work = intent.getStringExtra("work");
        firebaseFirestore = FirebaseFirestore.getInstance();
        action = intent.getAction();
        type = intent.getType();
        donationHolder = findViewById(R.id.donationHolder);

        SharedPreferences preferences = getSharedPreferences(themeColorPrefName, MODE_PRIVATE);
        savedColor = preferences.getInt(themeColor, getColor(R.color.primary));

        if (work != null && work.equals("update")) {
            Dialog updateDialog = getDialog();
            updateDialog.show();
        } else if (work != null && work.equals("media")) {
            linkX = intent.getStringExtra("link");
            startActivity(new Intent(Home.this, WebActivity.class).putExtra("links", linkX));
        }

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent);
            } else if (type.startsWith("video/")) {
                handleSendVideo(intent); // Handle single image being sent
            }
        }

        SharedPreferences.Editor editor = firstRun.edit();
        boolean firstBool = firstRun.getBoolean("firstRun", true);

        if (webLink != null) {
            String webStringLink = webLink.toString();
            if (webStringLink.contains("vtroid-media.com")) {
                webStringLink = webStringLink.replace("vtroid-media.com", "vtroid-media.blogspot.com");
                startActivity(new Intent(Home.this, WebActivity.class).putExtra("links", webStringLink));
            } else if (webStringLink.contains("vtroid") && webStringLink.contains("key")) {
                updatePremium();
            } else if (webStringLink.contains("vtroid.tech")) {
                startActivity(new Intent(Home.this, WebActivity.class).putExtra("links", webStringLink));
            } else if (webStringLink.contains("www.dropbox.com")) {
                if (webStringLink.contains(".mkv") || webStringLink.contains(".mp4") || webStringLink.contains(".3gp") || webStringLink.contains(".m4v")) {
                    webStringLink = webStringLink.replaceAll("www.dropbox.com", "www.dl.dropboxusercontent.com");
                    startActivity(new Intent(Home.this, PlayerActivity.class).putExtra("playLink", webStringLink));
                }
            } else {
                startActivity(new Intent(Home.this, PlayerActivity.class).putExtra("playLink", webStringLink));
            }
        }

        videoActivity.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, FolderActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        musicActivity.setOnClickListener(v -> {
            startActivity(new Intent(Home.this, MusicPlayer.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        donationHolder.setOnClickListener(v -> {
            DocumentReference documentReference = firebaseFirestore.collection("donation").document("link");
            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                String link = Objects.requireNonNull(documentSnapshot.getString("value"));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(Intent.createChooser(browserIntent, "Open with"));
            });
        });

        //Navigation Drawer
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home_fragment()).commit();
        navigationView.bringToFront();

        View header = navigationView.getHeaderView(0);

        ConstraintLayout premHolder = header.findViewById(R.id.premHolder);
        ConstraintLayout downloads = header.findViewById(R.id.downHolder);
        ConstraintLayout mediaSaver = header.findViewById(R.id.statHolder);
        ConstraintLayout setHolder = header.findViewById(R.id.setHolder);
        ConstraintLayout setVHolder = header.findViewById(R.id.setVHolder);
        ConstraintLayout shareHolder = header.findViewById(R.id.shareHolder);
        ConstraintLayout homeHolder = header.findViewById(R.id.homeHolder);
        headerMainHolder = header.findViewById(R.id.headerMainHolder);
        homeSelectHolder = header.findViewById(R.id.homeSelectHolder);
        headerMainHolder.setBackground(Utils.bgGrayGenerate(this));

        homeSelectHolder.setColorFilter(savedColor);

        TextView premiumText = header.findViewById(R.id.userHolderText);
        TextView zPass = header.findViewById(R.id.zPass);
        TextView getPremText = header.findViewById(R.id.getPremText);

        if (premiumPrefBoolean) {
            if (!keyChecked) {
                checkKey();
            }
            getPremText.setText("You have a Z Pass");
            getPremText.setOnClickListener(v -> {
                remPremium();
                closeDrawer();
            });
            zPass.setBackground(ContextCompat.getDrawable(this, R.drawable.z_pass_bg));
            premiumText.setText(R.string.premiumUser);
        } else {
            if (firstBool) {
                new Handler().postDelayed(() -> {
                    enterPremium = new Dialog(Home.this, R.style.MyDialogStyle);
                    enterPremium.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    enterPremium.setCancelable(true);
                    enterPremium.setCanceledOnTouchOutside(true);
                    enterPremium.setContentView(R.layout.dialog_premium);

                    TextView reportYes = enterPremium.findViewById(R.id.enterYes);
                    reportYes.setOnClickListener(v1 -> {
                        enterPremium.dismiss();
                        enterPremium.cancel();
                        getPremium();
                    });

                    TextView enterGo = enterPremium.findViewById(R.id.enterGet);
                    enterGo.setOnClickListener(v1 -> {
                        Uri uri = Uri.parse("https://instagram.com/_u/vtroid");
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri).setPackage("com.instagram.android");
                            startActivity(intent);
                        } catch (Exception e) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri).setPackage("com.instagram.android");
                            startActivity(intent);
                        }
                        enterPremium.dismiss();
                        enterPremium.cancel();
                    });
                    enterPremium.show();

                    editor.putBoolean("firstRun", false);
                    editor.apply();
                }, 200);
            }
            premiumText.setText(R.string.normal_user);
            zPass.setBackground(ContextCompat.getDrawable(this, R.drawable.z_pass_bg_de));
            getPremText.setText("Enter Pro Key");
            getPremText.setOnClickListener(v -> {
                getPremium();
                closeDrawer();
            });
        }

        premHolder.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://instagram.com/_u/vtroid");
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, uri).setPackage("com.instagram.android"));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
            closeDrawer();
        });

        homeHolder.setOnClickListener(v -> {
            returnToHome();
            closeDrawer();
        });

        setHolder.setOnClickListener(v -> {
            Intent intentX = new Intent(this, Settings.class);
            startActivity(intentX);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            closeDrawer();
        });

        setVHolder.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            closeDrawer();
        });

        shareHolder.setOnClickListener(v -> {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share V-Troid");
                String shareMessage = "Download *V-Troid*, an amazing app and to get these amazing features- \n◉ Watch Movies and Series For Free. \n◉ Save WhatsApp and Instagram status. \n◉ Play videos and Music and *many more*\n\n";
                shareMessage = shareMessage + "*All this in one app for free-* " + "https://play.google.com/store/apps/details?id=com.gtxprime.vtroid";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                Toast.makeText(this, "Unable to share - " + e, Toast.LENGTH_SHORT).show();
            }
            closeDrawer();
        });

        downloads.setOnClickListener(v -> {
            Fragment fragment = new Downloads_fragment();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, fragment, "downFrag").commit();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        mediaSaver.setOnClickListener(v -> {
            startActivity(new Intent(this, MediaSaver.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });


    }

    @NonNull
    private Dialog getDialog() {
        Dialog updateDialog = new Dialog(Home.this, R.style.MyDialogStyle);
        updateDialog.setContentView(R.layout.dialog_update);
        TextView yes = updateDialog.findViewById(R.id.updateYes);
        TextView no = updateDialog.findViewById(R.id.updateCancel);

        yes.setOnClickListener(v1 -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.gtxprime.vtroid")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.gtxprime.vtroid")));
            }
        });

        no.setOnClickListener(v1 -> updateDialog.dismiss());
        return updateDialog;
    }

    private void checkLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(this, pendingDynamicLinkData -> {
            Log.i("MainActivity", "We have the link");
            Uri deepLink = null;
            if (pendingDynamicLinkData != null) {
                deepLink = pendingDynamicLinkData.getLink();
            }
            if (deepLink != null) {
                Log.i("Link we got", String.valueOf(deepLink));
                startActivity(new Intent(this, WebActivity.class).putExtra("links", String.valueOf(deepLink)));
            }
        }).addOnFailureListener(this, e -> Toast.makeText(Home.this, "Error !!", Toast.LENGTH_SHORT).show());
    }

    private void handleSendVideo(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        String videoUri = imageUri.toString();
        startActivity(new Intent(Home.this, PlayerActivity.class).putExtra("playLink", videoUri));
    }

    private void handleSendText(Intent intent) {
        if (intent.getStringExtra(Intent.EXTRA_TEXT) != null) {
            String link = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (link.contains("www.instagram.com")) {
                startActivity(new Intent(Home.this, MediaSaver.class).putExtra("dowLinks", link));
            } else if (link.endsWith(".m3u8")) {
                startActivity(new Intent(Home.this, PlayerActivity.class).putExtra("playLink", link));
            } else
                Toast.makeText(this, "Not supported", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment myFragment1 = getSupportFragmentManager().findFragmentByTag("downFrag");
        Fragment myFragment2 = getSupportFragmentManager().findFragmentByTag("mediaFrag");
        Fragment myFragment4 = getSupportFragmentManager().findFragmentByTag("musicFrag");
        Fragment myFragment5 = getSupportFragmentManager().findFragmentByTag("videoFrag");
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (myFragment1 != null && myFragment1.isVisible()) {
            returnToHome();
        } else if (myFragment2 != null && myFragment2.isVisible()) {
            returnToHome();
        } else if (myFragment4 != null && myFragment4.isVisible()) {
            returnToHome();
        } else if (myFragment5 != null && myFragment5.isVisible()) {
            returnToHome();
        } else {
            keyDialog = new Dialog(Home.this, R.style.MyDialogStyle);
            keyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            keyDialog.setCancelable(true);
            keyDialog.setContentView(R.layout.dialog_exit);

            TextView yes = keyDialog.findViewById(R.id.exitYes);
            TextView cancel = keyDialog.findViewById(R.id.exitCancel);
            yes.setOnClickListener(v -> {
                Intent _intentOBJ = new Intent(Intent.ACTION_MAIN);
                _intentOBJ.addCategory(Intent.CATEGORY_HOME);
                _intentOBJ.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                _intentOBJ.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(_intentOBJ);
                finish();
            });

            cancel.setOnClickListener(v -> keyDialog.dismiss());


            keyDialog.show();
        }
    }

    public void returnToHome() {
        Fragment fragment = new Home_fragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }


    public void getPremium() {
        keyVerifyDialog = new Dialog(Home.this, R.style.MyDialogStyle);
        keyVerifyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        keyVerifyDialog.setCancelable(false);
        keyVerifyDialog.setCanceledOnTouchOutside(false);
        keyVerifyDialog.setContentView(R.layout.dialog_progress);

        keyDialog = new Dialog(Home.this);
        keyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        keyDialog.setCancelable(true);
        keyDialog.setContentView(R.layout.dialog_key);

        TextView verifyText = keyVerifyDialog.findViewById(R.id.loadingText);
        verifyText.setText(R.string.verifyingKey);
        EditText keyEdit = keyDialog.findViewById(R.id.premiumKey);
        TextView yes = keyDialog.findViewById(R.id.keyYes);

        yes.setOnClickListener(v -> {
            keyVerifyDialog.show();
            ((RubberLoaderView) keyVerifyDialog.findViewById(R.id.loader1)).startLoading();
            DocumentReference documentReference = firebaseFirestore.collection("PremiumKeyValue").document("Key");
            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                key = Objects.requireNonNull(documentSnapshot.getLong("Premium key")).intValue();
                if (String.valueOf(key).equals(keyEdit.getText().toString().trim())) {
                    keyVerifyDialog.dismiss();
                    updatePremium();
                } else {
                    Toast.makeText(Home.this, "Key is not correct", Toast.LENGTH_SHORT).show();
                    keyVerifyDialog.dismiss();
                }
            });
            keyDialog.dismiss();
        });
        keyDialog.show();

    }

    private void updatePremium() {
        SharedPreferences.Editor isPremiumEditor = isPremium.edit();
        SharedPreferences.Editor preKey = premiumKey.edit();
        isPremiumEditor.putBoolean(premiumBol, true).apply();
        preKey.putInt("key", key).apply();
        finish();
        startActivity(getIntent());
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        Toast.makeText(Home.this, "Premium Activated", Toast.LENGTH_SHORT).show();
    }

    private void remPremium() {
        keyDialog = new Dialog(Home.this);
        keyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        keyDialog.setCancelable(true);
        keyDialog.setContentView(R.layout.dialog_key);

        EditText keyEdit = keyDialog.findViewById(R.id.premiumKey);
        TextView yes = keyDialog.findViewById(R.id.keyYes);
        TextView keyText = keyDialog.findViewById(R.id.keyText);

        keyText.setText(R.string.sure_to_rem_key);
        keyEdit.setVisibility(View.GONE);
        yes.setText(R.string.yes);
        yes.setOnClickListener(v -> {
            SharedPreferences.Editor isPremiumEditor = isPremium.edit();
            isPremiumEditor.putBoolean(premiumBol, false).apply();
            Toast.makeText(Home.this, "Key Removed", Toast.LENGTH_SHORT).show();
            keyDialog.dismiss();
            finish();
            startActivity(getIntent());
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        keyDialog.show();

    }

    public static void openDrawer() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    public static void closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void checkKey() {
        checkPreKey = getSharedPreferences("Premium Key", Context.MODE_PRIVATE);
        int savedKey = checkPreKey.getInt("key", 0);
        keyCheckDialog = new Dialog(Home.this);
        keyCheckDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        keyCheckDialog.setCancelable(false);
        keyCheckDialog.setCanceledOnTouchOutside(false);
        keyCheckDialog.setContentView(R.layout.dialog_progress);
        TextView progressText = keyCheckDialog.findViewById(R.id.loadingText);
        progressText.setText(R.string.checkingKey);
        keyCheckDialog.show();
        ((RubberLoaderView) keyCheckDialog.findViewById(R.id.loader1)).startLoading();

        DocumentReference documentReference = firebaseFirestore.collection("PremiumKeyValue").document("Key");
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            int serverKey = Objects.requireNonNull(documentSnapshot.getLong("Premium key")).intValue();
            if (serverKey != savedKey) {
                SharedPreferences.Editor isPremiumEditor = isPremium.edit();
                isPremiumEditor.putBoolean(premiumBol, false).apply();
                keyCheckDialog.dismiss();
                Toast.makeText(this, "Your premium key is expired get a new one", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                ((RubberLoaderView) keyCheckDialog.findViewById(R.id.loader1)).stopLoading();
                keyCheckDialog.dismiss();
            }
        });
        keyChecked = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLink();
        homeSelectHolder.setColorFilter(savedColor);
        headerMainHolder.setBackground(Utils.bgGrayGenerate(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        homeSelectHolder.setColorFilter(savedColor);
        headerMainHolder.setBackground(Utils.bgGrayGenerate(this));
    }


}