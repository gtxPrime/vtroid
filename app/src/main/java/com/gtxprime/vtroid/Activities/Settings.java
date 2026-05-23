package com.gtxprime.vtroid.Activities;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Utils.Utils;
import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {
    ConstraintLayout colorHolder, settingsHolder, mainHolder;
    Dialog colorDialog;
    CircleImageView colorPreview;
    ImageView colorPrev;
    HSLColorPicker hslColorPicker;
    TextView done;
    public static final String themeColorPrefName = "themeColorPrefName";
    public static final String themeColor = "themeColor";
    SharedPreferences preferences;
    ColorDrawable cd;
    GradientDrawable gd;
    int finalColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //to store value
        SharedPreferences.Editor editor = getSharedPreferences(themeColorPrefName, MODE_PRIVATE).edit();

        Utils.setPad(findViewById(R.id.mainHolder), "bottom", this);
        //to retrieve value
        preferences = getSharedPreferences(themeColorPrefName, MODE_PRIVATE);
        int savedColor = preferences.getInt(themeColor, getColor(R.color.primary));

        colorHolder = findViewById(R.id.changeThemeBtn);
        mainHolder = findViewById(R.id.mainHolder);
        settingsHolder = findViewById(R.id.playerSettings);
        colorPrev = findViewById(R.id.colorPreview);

        gd = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{savedColor, getColor(R.color.mainSec), getColor(R.color.mainSec)});
        mainHolder.setBackground(gd);

        cd = new ColorDrawable(savedColor);
        colorPrev.setImageDrawable(cd);

        colorHolder.setOnClickListener(v ->{
            colorDialog = new Dialog(Settings.this);
            colorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            colorDialog.setCancelable(true);
            colorDialog.setContentView(R.layout.dialog_color_picker);
            colorDialog.setCanceledOnTouchOutside(false);

            colorPreview = colorDialog.findViewById(R.id.colorPreviewX);
            colorPreview.setImageDrawable(cd);
            hslColorPicker = colorDialog.findViewById(R.id.hslPicker);
            done = colorDialog.findViewById(R.id.done);

            hslColorPicker.setColorSelectionListener(new SimpleColorSelectionListener() {
                @Override
                public void onColorSelected(int color) {
                    super.onColorSelected(color);
                    cd = new ColorDrawable(color);
                    colorPreview.setImageDrawable(cd);
                    colorPreview.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                    finalColor = color;

                }
            });

            done.setOnClickListener(v1 -> {
                colorDialog.dismiss();
                colorDialog.cancel();
                editor.putInt(themeColor, finalColor);
                editor.apply();
                colorPrev.setImageDrawable(cd);
                gd = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{finalColor, getColor(R.color.mainSec), getColor(R.color.mainSec)});
                mainHolder.setBackground(gd);
            });
            colorDialog.show();

        });

    }
}