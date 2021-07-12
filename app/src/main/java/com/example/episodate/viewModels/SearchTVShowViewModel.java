package com.example.episodate.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.episodate.repositories.SearchTVShowRepository;
import com.example.episodate.responses.TVShowResponse;

public class SearchTVShowViewModel extends ViewModel {

    private SearchTVShowRepository searchTVShowRepository;

    public SearchTVShowViewModel(){
        searchTVShowRepository = new SearchTVShowRepository();
    }

    public LiveData<TVShowResponse> getSearchTVShows(String query,int page){
        return searchTVShowRepository.getSearchTVShows(query,page);
    }
}
