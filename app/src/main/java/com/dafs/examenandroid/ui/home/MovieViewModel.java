package com.dafs.examenandroid.ui.home;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.dafs.examenandroid.data.MovieRepository;
import com.dafs.examenandroid.data.local.entity.MovieEntity;
import com.dafs.examenandroid.data.network.Resource;

import java.util.List;


public class MovieViewModel extends ViewModel {

    private final LiveData<Resource<List<MovieEntity>>> popularMovies;
    private MovieRepository movieRepository;

    public MovieViewModel() {
        movieRepository = new MovieRepository();

        popularMovies = movieRepository.getPopularMovies();
    }

    public LiveData<Resource<List<MovieEntity>>> getPopularMovies() {
        return popularMovies;
    }




}