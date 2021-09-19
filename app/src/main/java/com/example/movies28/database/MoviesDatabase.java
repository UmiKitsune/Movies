package com.example.movies28.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Movie.class, FavouriteMovie.class}, version = 3, exportSchema = false)
public abstract class MoviesDatabase extends RoomDatabase {
    private static MoviesDatabase database;
    private static final String DB_NAME = "movies.db";
    private static final Object LOCK = new Object();

    public static MoviesDatabase getInstance(Context context) {
        synchronized (LOCK) {
            if (database == null) {
                database = Room.databaseBuilder(context, MoviesDatabase.class, DB_NAME)
                        .fallbackToDestructiveMigration() //для изменения версии базы данных( все удаляем и создаем заново
                        .build();
            }
        }
        return database;
    }

    public abstract MoviesDao getMoviesDao();
}
