package com.example.episodate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.episodate.R;
import com.example.episodate.adapters.WatchlistAdapter;
import com.example.episodate.databinding.ActivityWatchlistBinding;
import com.example.episodate.listeners.WatchlistListener;
import com.example.episodate.models.TVShow;
import com.example.episodate.utilities.TempDataHelper;
import com.example.episodate.viewModels.WatchlistViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WatchlistActivity extends AppCompatActivity implements WatchlistListener{

    private ActivityWatchlistBinding activityWatchlistBinding;
    private List<TVShow> watchlist;
    private WatchlistAdapter watchlistAdapter;
    private WatchlistViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWatchlistBinding = DataBindingUtil.setContentView(this,R.layout.activity_watchlist);
        doInit();
    }

    private void doInit(){
        watchlist = new ArrayList<>();
        viewModel = new ViewModelProvider(this).get(WatchlistViewModel.class);
        activityWatchlistBinding.watchlistRecyclerView.setHasFixedSize(true);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(activityWatchlistBinding.watchlistRecyclerView);
        activityWatchlistBinding.imageBack.setOnClickListener(view -> onBackPressed());
        loadWatchlist();
    }

    private void loadWatchlist(){
        activityWatchlistBinding.setIsLoading(true);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(viewModel.getWatchlistTVShows()
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(tvShow -> {
           activityWatchlistBinding.setIsLoading(false);
           if(watchlist.size() > 0){
              watchlist.clear();
           }
            watchlist.addAll(tvShow);
            checkWatchlistIsEmpty();
            watchlistAdapter = new WatchlistAdapter(watchlist, this);
            activityWatchlistBinding.watchlistRecyclerView.setAdapter(watchlistAdapter);
            activityWatchlistBinding.watchlistRecyclerView.setVisibility(View.VISIBLE);
            compositeDisposable.dispose();
        }));
    }

    private void checkWatchlistIsEmpty(){
        if(watchlist.isEmpty())
            activityWatchlistBinding.imageEmpty.setVisibility(View.VISIBLE);
        else
            activityWatchlistBinding.imageEmpty.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(TempDataHelper.IS_WATCHLIST_UPDATED){
            loadWatchlist();
            TempDataHelper.IS_WATCHLIST_UPDATED = false;
        }
    }

    @Override
    public void onClickedWatchlistTVShow(TVShow tvShow) {
        Intent intent = new Intent(WatchlistActivity.this,TVShowDetailsActivity.class);
        intent.putExtra("tvShow",tvShow);
        startActivity(intent);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT |
            ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            compositeDisposable.add(viewModel.removeTVShowFromWatchlist(watchlist.get(position))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(() -> {
                watchlist.remove(position);
                watchlistAdapter.notifyItemRemoved(position);
                checkWatchlistIsEmpty();
                Toast.makeText(getApplicationContext(),"Removed from watchlist",Toast.LENGTH_SHORT).show();
                compositeDisposable.dispose();
            }));
        }
    };
}