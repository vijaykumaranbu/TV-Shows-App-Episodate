package com.example.episodate.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.episodate.R;
import com.example.episodate.databinding.ItemContainerTvshowsBinding;
import com.example.episodate.listeners.TVShowListener;
import com.example.episodate.models.TVShow;

import java.util.List;

public class TVShowAdapter extends RecyclerView.Adapter<TVShowAdapter.TVShowsViewHolder>{
    private final List<TVShow> TVShows;
    private LayoutInflater layoutInflater;
    private final TVShowListener tvShowListener;

    public TVShowAdapter(List<TVShow> TVShows, TVShowListener tvShowListener) {
        this.TVShows = TVShows;
        this.tvShowListener = tvShowListener;
    }


    @NonNull
    @Override
    public TVShowsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemContainerTvshowsBinding itemContainerTvshowsBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.item_container_tvshows,parent,false);

        return new TVShowsViewHolder(itemContainerTvshowsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TVShowsViewHolder holder, int position) {
        holder.setBinding(TVShows.get(position));
    }

    @Override
    public int getItemCount() {
        return TVShows.size();
    }

    class TVShowsViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerTvshowsBinding itemContainerTvshowsBinding;

        public TVShowsViewHolder(@NonNull ItemContainerTvshowsBinding itemContainerTvshowsBinding) {
            super(itemContainerTvshowsBinding.getRoot());
            this.itemContainerTvshowsBinding = itemContainerTvshowsBinding;
        }

        public void setBinding(TVShow tvShow){
            itemContainerTvshowsBinding.setTvShow(tvShow);
            itemContainerTvshowsBinding.executePendingBindings();

            itemContainerTvshowsBinding.getRoot().setOnClickListener(view -> {
                tvShowListener.onClickedTVShow(tvShow);
            });
        }
    }
}
