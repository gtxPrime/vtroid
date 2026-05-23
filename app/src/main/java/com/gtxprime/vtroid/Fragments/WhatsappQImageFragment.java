package com.gtxprime.vtroid.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

public class WhatsappQImageFragment extends Fragment {
    SwipeRefreshLayout swipeRefresh;
    private final ArrayList<Uri> fileArrayList;
    ArrayList<WhatsappStatusModel> statusModelArrayList;
    RelativeLayout tvNoResult;
    RecyclerView rvFileList;

    public WhatsappQImageFragment(ArrayList<Uri> fileArrayList) {
        this.fileArrayList = fileArrayList;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_whatsapp_image, container, false);
        swipeRefresh = view.findViewById(R.id.swiperefresh);
        tvNoResult = view.findViewById(R.id.noResult);
        rvFileList = view.findViewById(R.id.rvFileList);
        initViews();
        return view;
    }

    private void initViews() {
        statusModelArrayList = new ArrayList<>();
        getData();
        swipeRefresh.setOnRefreshListener(() -> {
            statusModelArrayList = new ArrayList<>();
            getData();
            swipeRefresh.setRefreshing(false);
        });

    }

    private void getData() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            try {
                for (int i = 0; i < fileArrayList.size(); i++) {
                    WhatsappStatusModel whatsappStatusModel;
                    Uri uri = fileArrayList.get(i);
                    if (uri.toString().endsWith(".png") || uri.toString().endsWith(".jpg")) {
                        whatsappStatusModel = new WhatsappStatusModel("WhatsStatus: " + (i + 1),
                                uri,
                                new File(uri.toString()).getAbsolutePath(),
                                new File(uri.toString()).getName());
                        statusModelArrayList.add(whatsappStatusModel);
                    }
                }
                if (statusModelArrayList.size() != 0) {
                    tvNoResult.setVisibility(View.GONE);
                } else {
                    tvNoResult.setVisibility(View.VISIBLE);
                }
                WhatsappStatusAdapter whatsappStatusAdapter = new WhatsappStatusAdapter(getActivity(), statusModelArrayList, requireActivity());
                rvFileList.setAdapter(whatsappStatusAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
