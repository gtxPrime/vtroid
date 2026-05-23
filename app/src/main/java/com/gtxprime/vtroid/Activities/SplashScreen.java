package com.gtxprime.vtroid.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.FirebaseApp;
import com.greenfrvr.rubberloader.RubberLoaderView;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Utils.Unity;
import com.gtxprime.vtroid.Utils.Utils;



@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    String[] permissionsList;
    Intent i;
    private String bannerId = "Banner_Android";
    private String interstitialId = "Interstitial_Android";
    private String rewardedId = "Rewarded_Android";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseApp.initializeApp(SplashScreen.this);

        Utils.unity = new Unity(this, "5591203", false);
        Utils.unity.setAdUnit(bannerId, interstitialId, rewardedId);

        Utils.setPad(findViewById(R.id.splashContainer), "bottom", this);

        i = getIntent();
        Bundle extras = i.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.d("FMC", "Extras received at onCreate:  Key: " + key + " Value: " + value);
            }
            String work = extras.getString("work");
            String link = extras.getString("link");
            gotoNext(work, link);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsList = new String[]{Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissionsList = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }

        ((RubberLoaderView) findViewById(R.id.loader1)).startLoading();
        if (Utils.hasPermissions(this, permissionsList)) {
            ActivityCompat.requestPermissions(this, permissionsList, Utils.perRequest);
        } else {
            if (i.getExtras() == null)
                gotoNext("null", "null");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Utils.perRequest) {
            if (Utils.hasPermissions(this, permissionsList)) {
                ActivityCompat.requestPermissions(this, permissionsList, Utils.perRequest);
            } else {
                if (i.getExtras() == null)
                    gotoNext("null", "null");
            }
        }
    }

    void gotoNext(String work, String link) {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreen.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .putExtra("work", work)
                    .putExtra("link", link));
            finish();
        }, 600);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.d("FMC", "Extras received at onCreate:  Key: " + key + " Value: " + value);
            }
            String work = extras.getString("work");
            String link = extras.getString("link");
            gotoNext(work, link);
        }
    }


}