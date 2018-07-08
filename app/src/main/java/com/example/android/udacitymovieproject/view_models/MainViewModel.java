package com.example.android.udacitymovieproject.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.udacitymovieproject.database.AppDatabase;
import com.example.android.udacitymovieproject.model.Movie;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> movies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(getApplication());
        movies = database.moviesDAO().loadAllTasks();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }
}
