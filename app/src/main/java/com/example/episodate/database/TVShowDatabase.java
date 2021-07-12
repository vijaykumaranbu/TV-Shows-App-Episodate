package com.example.episodate.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.episodate.dao.TVShowDao;
import com.example.episodate.models.TVShow;

@Database(entities = TVShow.class,version = 1,exportSchema = false)
public abstract class TVShowDatabase extends RoomDatabase {

    private static TVShowDatabase tvShowDatabase;

    public static synchronized TVShowDatabase getTVShowDatabase(Context context){
        if(tvShowDatabase == null){
            tvShowDatabase = Room.databaseBuilder(
                    context,
                    TVShowDatabase.class,
                    "tv_show_db"
            ).build();
        }
        return tvShowDatabase;
    }

    public abstract TVShowDao tvShowDao();
}
