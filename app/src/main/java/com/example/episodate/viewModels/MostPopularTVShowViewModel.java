package com.example.episodate.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.episodate.repositories.MostPopularTVShowRepository;
import com.example.episodate.responses.TVShowResponse;

public class MostPopularTVShowViewModel extends ViewModel{

    private MostPopularTVShowRepository mostPopularTVShowRepository;

    public MostPopularTVShowViewModel(){
        mostPopularTVShowRepository = new MostPopularTVShowRepository();
    }

    public LiveData<TVShowResponse> getMostPopularTVShows(int page){
        return mostPopularTVShowRepository.getMostPopularTVShows(page);
    }
}
