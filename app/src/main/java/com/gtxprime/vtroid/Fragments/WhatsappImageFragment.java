package com.gtxprime.vtroid.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gtxprime.vtroid.Activities.PreviewActivity;
import com.gtxprime.vtroid.Adapters.WhatsappStatusAdapter;
import com.gtxprime.vtroid.Interfaces.FileListWhatsappClickInterface;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.model.WhatsappStatusModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class WhatsappImageFragment extends Fragment {
    SwipeRefreshLayout swiperefresh;
    ArrayList<WhatsappStatusModel> statusModelArrayList;
    RelativeLayout tvNoResult;
    RecyclerView rvFileList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_whatsapp_image, container, false);
        swiperefresh = view.findViewById(R.id.swiperefresh);
        tvNoResult = view.findViewById(R.id.noResult);
        tvNoResult = view.findViewById(R.id.noResult);
        rvFileList = view.findViewById(R.id.rvFileList);
        initViews();
        return view;
    }

    private void initViews() {
        statusModelArrayList = new ArrayList<>();
        getData();
       swiperefresh.setOnRefreshListener(() -> {
            statusModelArrayList = new ArrayList<>();
           swiperefresh.setRefreshing(false);
        });

    }

    private void getData() {
        WhatsappStatusModel whatsappStatusModel;
        String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp/Media/.Statuses";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses";
        }
        File targetDirector = new File(targetPath);
        File[] allfiles = targetDirector.listFiles();

        String targetPathBusiness = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp Business/Media/.Statuses";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            targetPathBusiness = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses";
        }
        File targetDirectorBusiness = new File(targetPathBusiness);
        File[] allfilesBusiness = targetDirectorBusiness.listFiles();
        if (allfilesBusiness == null) {
            File targetDirectorBusinessNew = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp Business/Media/.Statuses");
            allfilesBusiness = targetDirectorBusinessNew.listFiles();
        }

        try {
            if (allfiles != null) {
                Arrays.sort(allfiles, (o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
                for (int i = 0; i < allfiles.length; i++) {
                    File file = allfiles[i];
                    if (Uri.fromFile(file).toString().endsWith(".png") || Uri.fromFile(file).toString().endsWith(".jpg")) {
                        whatsappStatusModel = new WhatsappStatusModel("WhatsStatus: " + (i + 1),
                                Uri.fromFile(file),
                                allfiles[i].getAbsolutePath(),
                                file.getName());
                        statusModelArrayList.add(whatsappStatusModel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (allfilesBusiness != null) {
                Arrays.sort(allfilesBusiness, (o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
                for (int i = 0; i < allfilesBusiness.length; i++) {
                    File file = allfilesBusiness[i];
                    if (Uri.fromFile(file).toString().endsWith(".png") || Uri.fromFile(file).toString().endsWith(".jpg")) {
                        whatsappStatusModel = new WhatsappStatusModel("WhatsStatusB: " + (i + 1),
                                Uri.fromFile(file),
                                allfilesBusiness[i].getAbsolutePath(),
                                file.getName());
                        statusModelArrayList.add(whatsappStatusModel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (statusModelArrayList.size() != 0) {
            tvNoResult.setVisibility(View.GONE);
        } else {
            tvNoResult.setVisibility(View.VISIBLE);
        }
        WhatsappStatusAdapter whatsappStatusAdapter;
        whatsappStatusAdapter = new WhatsappStatusAdapter(getActivity(), statusModelArrayList, requireActivity());
        rvFileList.setAdapter(whatsappStatusAdapter);
    }
}
