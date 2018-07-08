package com.example.android.udacitymovieproject.view_models;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.udacitymovieproject.database.AppDatabase;
import com.example.android.udacitymovieproject.model.Movie;

public class MovieDetailViewModel extends ViewModel {
    private LiveData<Movie> movies;

    public MovieDetailViewModel(AppDatabase database, int taskId) {
        movies = database.moviesDAO().loadMovieById(taskId);
    }

    public LiveData<Movie> getMovie() {
        return movies;
    }
}
