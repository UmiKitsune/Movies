package com.example.movies28.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MoviesDao {
    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getAllMovies();

    //что бы получать конкретный фильм(возвращает экземпляр фильма по его id)
    @Query("SELECT * FROM movies WHERE id = :movieId")
    Movie getMovieById(int movieId);

    @Query("DELETE FROM movies")
    void deleteAllMovies();

    @Delete
    void deleteMovie(Movie movie);

    @Insert
    void insertMovie(Movie movie);

    //методы для favourite_movies
    @Query("SELECT * FROM favourite_movies")
    LiveData<List<FavouriteMovie>> getAllFavouriteMovies();

    @Delete
    void deleteFavouriteMovie(FavouriteMovie favouriteMovie);

    @Insert
    void insertFavouriteMovie(FavouriteMovie favouriteMovie);

    @Query("SELECT * FROM favourite_movies WHERE id = :movieId")
    FavouriteMovie getFavouriteMovieById(int movieId);

}
