package com.gtxprime.vtroid.Fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.gtxprime.vtroid.Activities.Home.openDrawer;
import static com.gtxprime.vtroid.Utils.Utils.PREMIUM;
import static com.gtxprime.vtroid.Utils.Utils.premiumBol;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gtxprime.vtroid.Activities.MediaSaver;
import com.gtxprime.vtroid.Activities.VideoFiles;
import com.gtxprime.vtroid.Utils.Unity;
import com.gtxprime.vtroid.WatchHistory.WatchHistory;
import com.gtxprime.vtroid.Activities.WebActivity;
import com.gtxprime.vtroid.Adapters.AdapterRecent;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Search.History;
import com.gtxprime.vtroid.Utils.Utils;
import com.gtxprime.vtroid.model.ModelMediaFiles;


import java.util.ArrayList;
import java.util.Objects;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class Home_fragment extends Fragment {

    public RecyclerView recyclerView;
    public CardView cameraFolder, downloadFolder, whatsappFolder, moviesFolder, saver, header;
    public ImageView menu, searchBtn;
    public AdapterRecent recentAdapter;
    public FirebaseFirestore firebaseFirestore;
    public SharedPreferences premiumKey;
    public ScrollView scv;
    public int version;
    public ConstraintLayout update, issueLay, homeFrag, watchHistory, serverUp, serverDown, movieSeriesHolder;
    public Dialog permission, reportDialog;
    boolean isPremium;
    public SharedPreferences premiumPref;
    public ShapeableImageView serBtn, movBtn;
    Boolean active = false;
    String link;
    String domain;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Utils.setPad(view.findViewById(R.id.icon_holder), "top", requireActivity());


        firebaseFirestore = FirebaseFirestore.getInstance();
        scv = view.findViewById(R.id.scv);
        update = view.findViewById(R.id.updateLay);
        permission = new Dialog(requireContext());
        recyclerView = view.findViewById(R.id.recentRecycler);
        cameraFolder = view.findViewById(R.id.cameraFolder);
        downloadFolder = view.findViewById(R.id.downloadFolder);
        whatsappFolder = view.findViewById(R.id.whatsappFolder);
        moviesFolder = view.findViewById(R.id.moviesFolder);
        issueLay = view.findViewById(R.id.issueLay);
        searchBtn = view.findViewById(R.id.searchBtn);
        menu = view.findViewById(R.id.menuIconHolder);
        saver = view.findViewById(R.id.saverHolder);
        movBtn = view.findViewById(R.id.movieHolder);
        header = view.findViewById(R.id.mediaHolder);
        serBtn = view.findViewById(R.id.seriesHolder);
        homeFrag = view.findViewById(R.id.homeFrag);
        serverUp = view.findViewById(R.id.serverUp);
        serverDown = view.findViewById(R.id.serverDown);
        watchHistory = view.findViewById(R.id.watchHistory);
        movieSeriesHolder = view.findViewById(R.id.movieServer);
        premiumKey = requireActivity().getSharedPreferences("Premium Key", Context.MODE_PRIVATE);
        premiumPref = requireActivity().getSharedPreferences(PREMIUM, MODE_PRIVATE);
        isPremium = premiumPref.getBoolean(premiumBol, false);

        homeFrag.setBackground(Utils.bgGrayGenerate(requireContext()));

        scv.setVerticalScrollBarEnabled(false);
        scv.setHorizontalScrollBarEnabled(false);
        checkForUpdate(requireActivity());
        loadFiles();

        if (!isPremium) {
            header.setVisibility(View.GONE);
        } else {
            header.setVisibility(View.VISIBLE);
        }

        DocumentReference documentReference = firebaseFirestore.collection("imgCol").document("images");
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            String movieBgImg = (documentSnapshot.getString("movie"));
            String seriesImg = (documentSnapshot.getString("series"));

            Glide.with(movBtn.getContext()).load(movieBgImg).placeholder(R.color.black).error(R.color.black).apply(new RequestOptions().centerCrop())
                    .transform(new BlurTransformation(1, 3)).into(movBtn);
            Glide.with(serBtn.getContext()).load(seriesImg).placeholder(R.color.black).error(R.color.black).apply(new RequestOptions().centerCrop())
                    .transform(new BlurTransformation(1, 3)).into(serBtn);

        });

        DocumentReference documentReference1 = firebaseFirestore.collection("special").document("values");
        documentReference1.get().addOnSuccessListener(documentSnapshot -> {
            active = (documentSnapshot.getBoolean("active"));
            if (active != null && active) {
                link = (documentSnapshot.getString("link"));
                String imgLink = (documentSnapshot.getString("imgLink"));
                ImageView specialPoster = view.findViewById(R.id.specialImg);
                Glide.with(view).load(imgLink).into(specialPoster);
                specialPoster.setOnClickListener(v -> startActivity(new Intent(requireActivity(), WebActivity.class).putExtra("links", link)));
            } else {
                view.findViewById(R.id.specialHolder).setVisibility(View.GONE);
            }
        });

        DocumentReference documentReferenceX = firebaseFirestore.collection("server").document("status");
        documentReferenceX.get().addOnSuccessListener(documentSnapshot -> {
            String currentStatus = (documentSnapshot.getString("current"));

            if (currentStatus != null && currentStatus.equals("online")) {
                serverUp.setVisibility(View.VISIBLE);
                serverDown.setVisibility(View.GONE);
            } else {
                serverUp.setVisibility(View.GONE);
                serverDown.setVisibility(View.VISIBLE);
            }

        });

        DocumentReference documentReferenceX1 = firebaseFirestore.collection("domain").document("domainName");
        documentReferenceX1.get().addOnSuccessListener(documentSnapshot -> {
            domain = (documentSnapshot.getString("value"));
        });

        DocumentReference documentReferenceX2 = firebaseFirestore.collection("server").document("movie");
        documentReferenceX2.get().addOnSuccessListener(documentSnapshot -> {
            String active = (documentSnapshot.getString("status"));
            if (active != null && active.equals("offline")) {
                movieSeriesHolder.setVisibility(View.GONE);
            }
        });

        movBtn.setOnClickListener(v -> startActivity(new Intent(requireActivity(), WebActivity.class).putExtra("links", "http://" + domain + "/public_html/movies/all/newest.html")));
        serBtn.setOnClickListener(v -> startActivity(new Intent(requireActivity(), WebActivity.class).putExtra("links", "http://" + domain + "/public_html/series/all/newest.html")));

        issueLay.setOnClickListener(v -> {
            reportDialog = new Dialog(requireActivity(), R.style.MyDialogStyle);
            reportDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            reportDialog.setCancelable(true);
            reportDialog.setCanceledOnTouchOutside(true);
            reportDialog.setContentView(R.layout.dialog_report);

            TextView reportYes = reportDialog.findViewById(R.id.reportYes);
            reportYes.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), WebActivity.class).putExtra("links", "http://vtroid.tech/public_html/contact-us.html")));
            reportDialog.show();
        });
        update.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.gtxprime.vtroid")));
            } catch (android.content.ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.gtxprime.vtroid")));
            }
        });
        saver.setOnClickListener(view1 -> {
            startActivity(new Intent(requireContext(), MediaSaver.class));
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        cameraFolder.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), VideoFiles.class).putExtra("folderName", "/storage/emulated/0/DCIM/Camera").putExtra("folderShow", "Camera"));
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        downloadFolder.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), VideoFiles.class).putExtra("folderName", "/storage/emulated/0/Download").putExtra("folderShow", "Downloads"));
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        whatsappFolder.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), VideoFiles.class).putExtra("folderName", "/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Video").putExtra("folderShow", "WhatsApp Video"));
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        moviesFolder.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), VideoFiles.class).putExtra("folderName", "/storage/emulated/0/Movies").putExtra("folderShow", "Movies"));
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        searchBtn.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), History.class));
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        watchHistory.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), WatchHistory.class));
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
        menu.setOnClickListener(v -> openDrawer());

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadFiles() {
        ArrayList<ModelMediaFiles> videoFilesArrayList = fetchRecentMedia();
        recentAdapter = new AdapterRecent(videoFilesArrayList, getContext());
        recyclerView.setAdapter(recentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        recentAdapter.notifyDataSetChanged();
    }

    public ArrayList<ModelMediaFiles> fetchRecentMedia() {
        ArrayList<ModelMediaFiles> videoFiles = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.MediaColumns.DATE_ADDED + " DESC";

        @SuppressLint("Recycle") Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, sortOrder);

        if (cursor != null && cursor.moveToNext()) {
            do {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                @SuppressLint("Range") String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                @SuppressLint("Range") String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                @SuppressLint("Range") String dateAdded = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                ModelMediaFiles mediaFilesAdapter = new ModelMediaFiles(id, title, displayName, size, duration, path, dateAdded);

                videoFiles.add(mediaFilesAdapter);

            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        return videoFiles;
    }

    public void checkForUpdate(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info != null) {
            version = info.versionCode;
        }

        if (Utils.isNetworkAvailable(context)) {
            DocumentReference documentReference = firebaseFirestore.collection("Version").document("Code");
            documentReference.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    int checkDVersion = Objects.requireNonNull(documentSnapshot.getLong("Version Code")).intValue();
                    if (checkDVersion - version >= 1 && checkDVersion - version <= 10) {
                        DocumentReference documentReference3 = firebaseFirestore.collection("App Link").document("Share Link");
                        documentReference3.get().addOnSuccessListener(documentSnapshot2 -> {
                            String link = documentSnapshot2.getString("Link");
                            update.setVisibility(View.VISIBLE);
                            update.setOnClickListener(view -> intOpen(link));
                        });
                    } else if (checkDVersion - version > 10) {
                        DocumentReference documentReference3 = firebaseFirestore.collection("App Link").document("Share Link");
                        documentReference3.get().addOnSuccessListener(documentSnapshot2 -> {
                            Dialog updateDialog;
                            updateDialog = new Dialog(context);
                            updateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            updateDialog.setCancelable(false);
                            updateDialog.setContentView(R.layout.dialog_update);

                            TextView updateYes = updateDialog.findViewById(R.id.updateYes);
                            TextView updateCancel = updateDialog.findViewById(R.id.updateCancel);
                            TextView updateText = updateDialog.findViewById(R.id.updateText);

                            updateText.setText(R.string.updateTest);
                            updateYes.setOnClickListener(v -> {
                                DocumentReference documentReferenceX = firebaseFirestore.collection("App Link").document("Share Link");
                                documentReferenceX.get().addOnSuccessListener(documentSnapshot21 -> {
                                    String link = documentSnapshot21.getString("Link");
                                    intOpen(link);
                                });
                            });
                            updateCancel.setVisibility(View.GONE);
                            updateDialog.show();
                        });
                    }

                }
            });
        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void intOpen(String url) {
        if (url != null) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(Intent.createChooser(browserIntent, "Open with"));
        } else {
            Toast.makeText(getActivity(), "Empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        homeFrag.setBackground(Utils.bgGrayGenerate(requireContext()));
    }

}


