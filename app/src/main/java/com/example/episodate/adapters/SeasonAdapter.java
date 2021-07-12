package com.example.episodate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.episodate.R;
import com.example.episodate.databinding.ItemContainerSeasonBinding;
import com.example.episodate.listeners.SeasonListener;
import com.example.episodate.models.Season;

import java.util.List;

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.SeasonViewHolder> {

    private List<Season> seasons;
    private LayoutInflater layoutInflater;
    private SeasonListener seasonListener;

    public SeasonAdapter(List<Season> seasons,SeasonListener seasonListener) {
        this.seasons = seasons;
        this.seasonListener = seasonListener;
    }

    @NonNull
    @Override
    public SeasonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemContainerSeasonBinding itemContainerSeasonBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.item_container_season,parent,false);
        return new SeasonViewHolder(itemContainerSeasonBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonViewHolder holder, int position) {
        holder.bindSeason(seasons.get(position));
    }

    @Override
    public int getItemCount() {
        return seasons.size();
    }

    class SeasonViewHolder extends RecyclerView.ViewHolder{

        private ItemContainerSeasonBinding itemContainerSeasonBinding;

        public SeasonViewHolder(@NonNull ItemContainerSeasonBinding itemContainerSeasonBinding) {
            super(itemContainerSeasonBinding.getRoot());
            this.itemContainerSeasonBinding = itemContainerSeasonBinding;
        }

        void bindSeason(Season season){
            itemContainerSeasonBinding.setImageURL(season.getImageUrl());
            itemContainerSeasonBinding.setTitle(season.getTitle());
            itemContainerSeasonBinding.setStartDate(season.getStartDate());
            itemContainerSeasonBinding.executePendingBindings();

            itemContainerSeasonBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    seasonListener.onClickSeason(season);
                }
            });
        }
    }
}
