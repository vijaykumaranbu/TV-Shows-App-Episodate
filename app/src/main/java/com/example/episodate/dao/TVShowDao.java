package com.example.episodate.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.episodate.models.TVShow;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface TVShowDao {

    @Query("SELECT * FROM tvshows")
    Flowable<List<TVShow>> getWatchlistTVShows();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable addToWatchlist(TVShow tvShow);

    @Query("SELECT * FROM tvShows wHERE id = :tvShowId")
    Flowable<TVShow> getTVShowFromWatchlist(String tvShowId);

    @Delete
    Completable removeFormWatchlist(TVShow tvShow);

}
