package com.gtxprime.vtroid.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.databinding.HolderWhatsappItemBinding;
import com.gtxprime.vtroid.model.story.ItemModel;

import java.util.ArrayList;

import static com.gtxprime.vtroid.Utils.Utils.RootDirectoryInsta;
import static com.gtxprime.vtroid.Utils.Utils.startDownload;

public class AdapterStoriesList extends RecyclerView.Adapter<AdapterStoriesList.ViewHolder> {
    private final Context context;
    private final ArrayList<ItemModel> storyItemModelList;

    public AdapterStoriesList(Context context, ArrayList<ItemModel> list) {
        this.context = context;
        this.storyItemModelList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        return new ViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.holder_whatsapp_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ItemModel itemModel = storyItemModelList.get(position);
        try {
            if (itemModel.getMedia_type()==2) {
                viewHolder.binding.ivPlay.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.ivPlay.setVisibility(View.GONE);
            }
            Glide.with(context)
                    .load(itemModel.getImage_versions2().getCandidates().get(0).getUrl())
                    .into(viewHolder.binding.pcw);

        }catch (Exception ex){
            ex.printStackTrace();
        }

        viewHolder.binding.tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemModel.getMedia_type()==2) {
                    startDownload(itemModel.getVideo_versions().get(0).getUrl(),
                            RootDirectoryInsta, context,"story_"+itemModel.getId()+".mp4" );
                }else {
                    startDownload(itemModel.getImage_versions2().getCandidates().get(0).getUrl(),
                            RootDirectoryInsta, context, "story_"+itemModel.getId()+".png");
                }
            }
        });


    }
    @Override
    public int getItemCount() {
        return storyItemModelList == null ? 0 : storyItemModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        HolderWhatsappItemBinding binding;
        public ViewHolder(HolderWhatsappItemBinding mbinding) {
            super(mbinding.getRoot());
            this.binding = mbinding;
        }
    }
}