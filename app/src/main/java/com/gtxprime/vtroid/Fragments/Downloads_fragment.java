package com.gtxprime.vtroid.Fragments;

import static com.gtxprime.vtroid.Activities.Home.closeDrawer;
import static com.gtxprime.vtroid.Activities.Home.openDrawer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Adapters.AdapterViewPager;
import com.gtxprime.vtroid.Utils.Utils;

public class Downloads_fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        closeDrawer();
        ConstraintLayout mainHolderGallery = view.findViewById(R.id.mainHolderGallery);
        TabLayout tabLayout = view.findViewById(R.id.galleryTab);
        ViewPager viewPager = view.findViewById(R.id.galleryViewPager);
        ImageView menuIconHolderDown = view.findViewById(R.id.menuIconHolderDown);
        menuIconHolderDown.setOnClickListener(v -> openDrawer());

        mainHolderGallery.setBackground(Utils.bgGrayGenerate(requireContext()));

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        AdapterViewPager viewPagerAdapter = new AdapterViewPager(getChildFragmentManager());
        viewPagerAdapter.addFragment(new Whatsapp_down_fragment(), "WhatsApp");
        viewPagerAdapter.addFragment(new Insta_down_fragment(), "Instagram");
        viewPagerAdapter.addFragment(new Facebook_down_fragment(), "Others");

        viewPager.setAdapter(viewPagerAdapter);
    }


}

