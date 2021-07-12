package com.example.episodate.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.episodate.adapters.TVShowAdapter;
import com.example.episodate.listeners.TVShowListener;
import com.example.episodate.models.TVShow;
import com.example.episodate.R;
import com.example.episodate.utilities.ItemOffsetDecoration;
import com.example.episodate.utilities.NetworkStateReceiver;
import com.example.episodate.viewModels.MostPopularTVShowViewModel;
import com.example.episodate.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TVShowListener,
        NetworkStateReceiver.NetworkStateListener {

    private ActivityMainBinding activityMainBinding;
    private MostPopularTVShowViewModel viewModel;
    private TVShowAdapter tvShowAdapter;
    private final List<TVShow> tvShows = new ArrayList<>();
    private NetworkStateReceiver networkStateReceiver;
    private Snackbar snackbar;
    private int currentPage = 1;
    private int totalAvailablePages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        doInit();
    }

    private void doInit(){
        setLayoutManager();
        viewModel = new ViewModelProvider(this).get(MostPopularTVShowViewModel.class);
        tvShowAdapter = new TVShowAdapter(tvShows,this);
        activityMainBinding.tvShowRecyclerView.setAdapter(tvShowAdapter);
        activityMainBinding.tvShowRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!activityMainBinding.tvShowRecyclerView.canScrollVertically(1)){
                    if(currentPage <= totalAvailablePages){
                        currentPage++;
                        getMostPopularTVShows();
                    }
                }
            }
        });
        activityMainBinding.imageWatchlist.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,WatchlistActivity.class)));
        activityMainBinding.imageSearch.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,SearchActivity.class)));
    }

    private void getMostPopularTVShows(){
        toggleLoading();
        viewModel.getMostPopularTVShows(currentPage).observe(this, tvShowResponse -> {
            toggleLoading();
            if(tvShowResponse != null){
                totalAvailablePages = tvShowResponse.getTotalPages();
                if(tvShowResponse.getTvShows() != null){
                    int oldSize = tvShows.size();
                    tvShows.addAll(tvShowResponse.getTvShows());
                    tvShowAdapter.notifyItemRangeInserted(oldSize,tvShows.size());
                    activityMainBinding.imageNoInternet.setVisibility(View.GONE);
                }
            }
        });
    }

    private void toggleLoading() {
        if (currentPage == 1) {
            activityMainBinding.setIsLoading(activityMainBinding.getIsLoading() == null ||
                    !activityMainBinding.getIsLoading());
        } else {
            activityMainBinding.setIsLoadingMore(activityMainBinding.getIsLoadingMore() == null ||
                    !activityMainBinding.getIsLoadingMore());
        }
    }

    private void setLayoutManager(){
        activityMainBinding.tvShowRecyclerView.setHasFixedSize(true);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            activityMainBinding.tvShowRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        }
        else {
            activityMainBinding.tvShowRecyclerView.setLayoutManager(new GridLayoutManager(this,5));
        }

        ItemOffsetDecoration offsetDecoration = new ItemOffsetDecoration(getApplicationContext(),R.dimen.item_offset);
        activityMainBinding.tvShowRecyclerView.addItemDecoration(offsetDecoration);
    }

    @Override
    public void onClickedTVShow(TVShow tvShow) {
        Intent intent = new Intent(MainActivity.this,TVShowDetailsActivity.class);
        intent.putExtra("tvShow",tvShow);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(networkStateReceiver == null){
            networkStateReceiver = new NetworkStateReceiver();
            networkStateReceiver.addNetworkListener(this);
            this.registerReceiver(networkStateReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(networkStateReceiver != null){
            networkStateReceiver.removeNetworkListener(this);
            this.unregisterReceiver(networkStateReceiver);
            networkStateReceiver = null;
        }
    }

    @Override
    public void networkAvailable() {
        if(tvShows.isEmpty())
            getMostPopularTVShows();

        if(snackbar != null) {
            snackbar.dismiss();
            snackbar = null;
        }

    }

    @Override
    public void networkUnAvailable() {
        if (tvShows.isEmpty())
            activityMainBinding.imageNoInternet.setVisibility(View.VISIBLE);
        else {
            if (snackbar == null) {
                snackbar = Snackbar.make(activityMainBinding.tvShowRecyclerView, "No internet connection !", Snackbar.LENGTH_INDEFINITE)
                        .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark))
                        .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextIcons));
                TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                Typeface font = ResourcesCompat.getFont(getApplicationContext(),R.font.ubuntu_regular);
                textView.setTypeface(font);
                snackbar.show();
            }
        }
    }
}