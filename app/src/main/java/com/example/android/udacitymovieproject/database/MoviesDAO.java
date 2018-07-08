package com.example.android.udacitymovieproject.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.example.android.udacitymovieproject.model.Movie;

import java.util.List;

@Dao
public interface MoviesDAO {

    @Transaction
    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> loadAllTasks();

    @Insert
    void insertTask(Movie taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(Movie taskEntry);

    @Delete
    void deleteTask(Movie taskEntry);

    @Transaction
    @Query("SELECT * FROM movies WHERE id = :id")
    LiveData<Movie> loadMovieById(int id);
}
