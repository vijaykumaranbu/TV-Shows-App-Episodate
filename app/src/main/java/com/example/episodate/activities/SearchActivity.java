package com.example.episodate.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.adapters.SearchViewBindingAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.example.episodate.R;
import com.example.episodate.adapters.TVShowAdapter;
import com.example.episodate.adapters.WatchlistAdapter;
import com.example.episodate.databinding.ActivitySearchBinding;
import com.example.episodate.listeners.TVShowListener;
import com.example.episodate.listeners.WatchlistListener;
import com.example.episodate.models.TVShow;
import com.example.episodate.responses.TVShowResponse;
import com.example.episodate.utilities.ItemOffsetDecoration;
import com.example.episodate.viewModels.SearchTVShowViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity implements TVShowListener {

    private ActivitySearchBinding activitySearchBinding;
    private SearchTVShowViewModel viewModel;
    private final List<TVShow> tvShows = new ArrayList<>();
    private TVShowAdapter searchAdapter;
    private Timer timer;
    private int currentPage = 1;
    private int totalPages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySearchBinding = DataBindingUtil.setContentView(this,R.layout.activity_search);
        doInit();
    }

    private void doInit(){
        activitySearchBinding.imageBack.setOnClickListener(view -> onBackPressed());
        viewModel = new ViewModelProvider(this).get(SearchTVShowViewModel.class);
        setLayoutManager();
        searchAdapter = new TVShowAdapter(tvShows,this);
        activitySearchBinding.searchRecyclerView.setAdapter(searchAdapter);
        activitySearchBinding.imageSearchEmpty.setVisibility(View.VISIBLE);
        activitySearchBinding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(timer != null){
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().trim().isEmpty()){
                    activitySearchBinding.imageSearchEmpty.setVisibility(View.GONE);
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                currentPage = 1;
                                totalPages = 1;
                                searchTVShow(editable.toString().trim());
                            });
                        }
                    },800);
                }
                else {
                    tvShows.clear();
                    searchAdapter.notifyDataSetChanged();
                    activitySearchBinding.imageSearchEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
        activitySearchBinding.searchRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!activitySearchBinding.searchRecyclerView.canScrollVertically(1)){
                    if (!activitySearchBinding.searchInput.getText().toString().isEmpty()) {
                        if (currentPage <= totalPages) {
                            currentPage++;
                            searchTVShow(activitySearchBinding.searchInput.getText().toString().trim());
                        }
                    }
                }
            }
        });
        activitySearchBinding.searchInput.requestFocus();
    }

    private void searchTVShow(String query){
        toggleLoading();
        viewModel.getSearchTVShows(query,currentPage).observe(this, tvShowResponse -> {
            toggleLoading();
            if(tvShowResponse != null){
                totalPages = tvShowResponse.getTotalPages();
                if(tvShowResponse.getTvShows() != null){
                    int oldSize = tvShows.size();
                    tvShows.addAll(tvShowResponse.getTvShows());
                    searchAdapter.notifyItemRangeInserted(oldSize,tvShows.size());
                }
            }
        });
    }

    private void toggleLoading() {
        if (currentPage == 1) {
            activitySearchBinding.setIsLoading(activitySearchBinding.getIsLoading() == null ||
                    !activitySearchBinding.getIsLoading());
        } else {
            activitySearchBinding.setIsLoadingMore(activitySearchBinding.getIsLoadingMore() == null ||
                    !activitySearchBinding.getIsLoadingMore());
        }
    }

    private void setLayoutManager(){
        activitySearchBinding.searchRecyclerView.setHasFixedSize(true);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            activitySearchBinding.searchRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        }
        else {
            activitySearchBinding.searchRecyclerView.setLayoutManager(new GridLayoutManager(this,5));
        }

        ItemOffsetDecoration offsetDecoration = new ItemOffsetDecoration(getApplicationContext(),R.dimen.item_offset);
        activitySearchBinding.searchRecyclerView.addItemDecoration(offsetDecoration);
    }

    @Override
    public void onClickedTVShow(TVShow tvShow) {
        Intent intent = new Intent(SearchActivity.this,TVShowDetailsActivity.class);
        intent.putExtra("tvShow",tvShow);
        startActivity(intent);
    }
}