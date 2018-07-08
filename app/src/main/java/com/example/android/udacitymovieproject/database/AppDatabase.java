package com.example.android.udacitymovieproject.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.android.udacitymovieproject.model.Movie;
import com.example.android.udacitymovieproject.model.Review;
import com.example.android.udacitymovieproject.model.Video;

@Database(entities = {Movie.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "udacity_movie_project";
    private static AppDatabase sInstance;
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE reviews");
            database.execSQL("DROP TABLE videos");
            database.execSQL("DROP TABLE movies");
            database.execSQL("CREATE TABLE movies(id INTEGER PRIMARY KEY NOT NULL, posterPath TEXT)");
        }
    };

    public static AppDatabase getInstance(Context context) {

        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .addMigrations(MIGRATION_1_2)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract MoviesDAO moviesDAO();

}