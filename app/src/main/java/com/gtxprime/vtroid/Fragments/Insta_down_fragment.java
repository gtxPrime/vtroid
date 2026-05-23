package com.gtxprime.vtroid.Fragments;

import static com.gtxprime.vtroid.Utils.Utils.RootDirectoryInstaShow;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gtxprime.vtroid.Adapters.MyStatusAdapter;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.model.WhatsappStatusModel;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Insta_down_fragment extends Fragment implements MyStatusAdapter.OnCheckboxListener {

    GridView imageGrid;
    ArrayList<WhatsappStatusModel> filesToDelete = new ArrayList<>();
    ArrayList<WhatsappStatusModel> f = new ArrayList<>();
    LinearLayout actionLay, deleteIV;
    RelativeLayout loaderLay, emptyLay;
    CheckBox selectAll;
    MyStatusAdapter myAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        loaderLay = view.findViewById(R.id.loaderLay);
        emptyLay = view.findViewById(R.id.emptyLay);
        swipeRefreshLayout = view.findViewById(R.id.galleryRefresh);
        imageGrid = view.findViewById(R.id.videoGrid);
        actionLay = view.findViewById(R.id.actionLay);
        deleteIV = view.findViewById(R.id.deleteIV);
        selectAll = view.findViewById(R.id.selectAll);

        deleteIV.setOnClickListener(view1 -> {
            if (!filesToDelete.isEmpty()) {
                Dialog alertDialog = new Dialog(getContext());
                alertDialog.setTitle(R.string.confirm);
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setCancelable(true);
                alertDialog.setContentView(R.layout.dialog_delete);
                TextView delete = alertDialog.findViewById(R.id.deleteYes);
                TextView cancel = alertDialog.findViewById(R.id.deleteCancel);
                delete.setOnClickListener(v1 -> {
                    int success = -1;
                    ArrayList<WhatsappStatusModel> deletedFiles = new ArrayList<>();

                    for (WhatsappStatusModel details : filesToDelete) {
                        File file = new File(details.getPath());
                        if (file.exists()) {
                            if (file.delete()) {
                                deletedFiles.add(details);
                                if (success == 0) {
                                    return;
                                }
                                success = 1;
                            } else {
                                success = 0;
                            }
                        } else {
                            success = 0;
                        }
                    }

                    filesToDelete.clear();
                    for (WhatsappStatusModel deletedFile : deletedFiles) {
                        f.remove(deletedFile);
                    }
                    myAdapter.notifyDataSetChanged();
                    if (success == 0) {
                        Toast.makeText(getContext(), getResources().getString(R.string.delete_error), Toast.LENGTH_SHORT).show();
                    } else if (success == 1) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                    }
                    actionLay.setVisibility(View.GONE);
                    selectAll.setChecked(false);
                    alertDialog.dismiss();
                });
                cancel.setOnClickListener(v1 -> alertDialog.dismiss());
                alertDialog.show();
            }
        });
        selectAll.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!compoundButton.isPressed()) {
                return;
            }

            filesToDelete.clear();

            for (int i = 0; i < f.size(); i++) {
                if (!f.get(i).selected) {
                    b = true;
                    break;
                }
            }

            if (b) {
                for (int i = 0; i < f.size(); i++) {
                    f.get(i).selected = true;
                    filesToDelete.add(f.get(i));
                }
                selectAll.setChecked(true);
            } else {
                for (int i = 0; i < f.size(); i++) {
                    f.get(i).selected = false;
                }
                actionLay.setVisibility(View.GONE);
            }
            myAdapter.notifyDataSetChanged();
        });
        populateGrid();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            populateGrid();
            swipeRefreshLayout.setRefreshing(false);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateGrid();
    }

    public void populateGrid() {
        loaderLay.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {

            loaderLay.setVisibility(View.GONE);
            getFromSdcard();
            if (f == null || f.size() == 0) {
                emptyLay.setVisibility(View.VISIBLE);
            } else {
                emptyLay.setVisibility(View.GONE);
            }

            myAdapter = new MyStatusAdapter(getActivity(), f, Insta_down_fragment.this);
            imageGrid.setAdapter(myAdapter);

        }, 1000);
    }

    public void getFromSdcard() {
        f = new ArrayList<>();
        if (RootDirectoryInstaShow.isDirectory()) {
            File[] listFile = RootDirectoryInstaShow.listFiles();
            assert listFile != null;
            Arrays.sort(listFile, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            for (File file : listFile) {
                f.add(new WhatsappStatusModel(file.getName(), Uri.fromFile(file), file.getPath(), file.getName()));
            }
        }
    }

    @Override
    public void onCheckboxListener(View view, List<WhatsappStatusModel> list) {
        filesToDelete.clear();
        for (WhatsappStatusModel details : list) {
            if (details.isSelected()) {
                filesToDelete.add(details);
            }
        }
        if (filesToDelete.size() == f.size()) {
            selectAll.setChecked(true);
        }
        if (!filesToDelete.isEmpty()) {
            actionLay.setVisibility(View.VISIBLE);
            return;
        }
        selectAll.setChecked(false);
        actionLay.setVisibility(View.GONE);
    }

}

