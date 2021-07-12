package com.example.episodate.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.episodate.databinding.ItemContainerWatchlistBinding;
import com.example.episodate.listeners.WatchlistListener;
import com.example.episodate.models.TVShow;
import com.example.episodate.R;

import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.WatchlistViewHolder>{
    private final List<TVShow> TVShows;
    private LayoutInflater layoutInflater;
    private final WatchlistListener watchlistListener;

    public WatchlistAdapter(List<TVShow> TVShows, WatchlistListener watchlistListener) {
        this.TVShows = TVShows;
        this.watchlistListener = watchlistListener;
    }

    @NonNull
    @Override
    public WatchlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemContainerWatchlistBinding itemContainerWatchlistBinding = DataBindingUtil
                .inflate(layoutInflater, R.layout.item_container_watchlist,parent,false);

        return new WatchlistViewHolder(itemContainerWatchlistBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchlistViewHolder holder, int position) {
        holder.setBinding(TVShows.get(position));
    }

    @Override
    public int getItemCount() {
        return TVShows.size();
    }

   class WatchlistViewHolder extends RecyclerView.ViewHolder{

        private final ItemContainerWatchlistBinding itemContainerWatchlistBinding;

        public WatchlistViewHolder(@NonNull ItemContainerWatchlistBinding itemContainerWatchlistBinding) {
            super(itemContainerWatchlistBinding.getRoot());
            this.itemContainerWatchlistBinding = itemContainerWatchlistBinding;
        }

        public void setBinding(TVShow tvShow){
            itemContainerWatchlistBinding.setTvShow(tvShow);
            itemContainerWatchlistBinding.executePendingBindings();

            itemContainerWatchlistBinding.getRoot().setOnClickListener(view -> {
                watchlistListener.onClickedWatchlistTVShow(tvShow);
            });
        }
    }
}
