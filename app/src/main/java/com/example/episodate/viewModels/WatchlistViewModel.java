package com.example.episodate.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.episodate.database.TVShowDatabase;
import com.example.episodate.models.TVShow;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class WatchlistViewModel extends AndroidViewModel {

    private TVShowDatabase tvShowDatabase;

    public WatchlistViewModel(@NonNull Application application) {
        super(application);
        tvShowDatabase = TVShowDatabase.getTVShowDatabase(application);
    }

    public Flowable<List<TVShow>> getWatchlistTVShows(){
        return tvShowDatabase.tvShowDao().getWatchlistTVShows();
    }

    public Completable removeTVShowFromWatchlist(TVShow tvShow){
        return tvShowDatabase.tvShowDao().removeFormWatchlist(tvShow);
    }
}
