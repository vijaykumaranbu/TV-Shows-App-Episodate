package com.example.episodate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.episodate.R;
import com.example.episodate.databinding.ItemContainerSliderBinding;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder>{

    private final String[] slidingPictures;
    private LayoutInflater layoutInflater;

    public ImageSliderAdapter(String[] slidingPictures) {
        this.slidingPictures = slidingPictures;
    }

    @NonNull
    @Override
    public ImageSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemContainerSliderBinding itemContainerSliderBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.item_container_slider,parent,false);

        return new ImageSliderViewHolder(itemContainerSliderBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderViewHolder holder, int position) {
        holder.setImageBinding(slidingPictures[position]);
    }

    @Override
    public int getItemCount() {
        return slidingPictures.length;
    }

    static class ImageSliderViewHolder extends RecyclerView.ViewHolder{

        private final ItemContainerSliderBinding itemContainerSliderBinding;

        public ImageSliderViewHolder(@NonNull ItemContainerSliderBinding itemContainerSliderBinding) {
            super(itemContainerSliderBinding.getRoot());
            this.itemContainerSliderBinding = itemContainerSliderBinding;
        }

        void setImageBinding(String url){
            itemContainerSliderBinding.setImageURL(url);
            itemContainerSliderBinding.executePendingBindings();
        }
    }
}
