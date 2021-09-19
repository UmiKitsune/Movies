package com.example.movies28.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.loader.content.AsyncTaskLoader;

import com.example.movies28.FavouriteActivity;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {

    private static MoviesDatabase database;
    private LiveData<List<Movie>> movies;
    private LiveData<List<FavouriteMovie>> favouriteMovies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = MoviesDatabase.getInstance(getApplication());
        movies = database.getMoviesDao().getAllMovies();
        favouriteMovies = database.getMoviesDao().getAllFavouriteMovies();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public LiveData<List<FavouriteMovie>> getFavouriteMovies() {
        return favouriteMovies;
    }

    public Movie getMovieById(int id) {
        try {
            return new GetMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAllMovies() {
        new DeleteAllMoviesTask().execute();
    }

    public void deleteMovie(Movie movie) {
       new DeleteMovieTask().execute(movie);
    }

    public void insertMovie(Movie movie) {
        new InsertMovieTask().execute(movie);
    }

    public void deleteFavouriteMovie(FavouriteMovie favouriteMovie) {
        new DeleteFavouriteMovieTask().execute(favouriteMovie);
    }

    public void insertFavouriteMovie(FavouriteMovie favouriteMovie) {
        new InsertFavouriteMovieTask().execute(favouriteMovie);
    }

    public FavouriteMovie getFavouriteMovieById(int id) {
        try {
            return new GetFavouriteMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class GetFavouriteMovieTask extends AsyncTask<Integer, Void, FavouriteMovie> {
        @Override
        protected FavouriteMovie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return  database.getMoviesDao().getFavouriteMovieById(integers[0]);
            }
            return null;
        }
    }

    private static class GetMovieTask extends AsyncTask<Integer, Void, Movie> {
        @Override
        protected Movie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0) {
                return  database.getMoviesDao().getMovieById(integers[0]);
            }
            return null;
        }
    }

    private static class DeleteAllMoviesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            database.getMoviesDao().deleteAllMovies();
            return null;
        }
    }

    private static class DeleteMovieTask extends AsyncTask<Movie, Void, Void> {
        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0) {
                database.getMoviesDao().deleteMovie(movies[0]);
            }
            return null;
        }
    }

    private static class InsertMovieTask extends AsyncTask<Movie, Void, Void> {
        @Override
        protected Void doInBackground(Movie... movies) {
            if (movies != null && movies.length > 0) {
                database.getMoviesDao().insertMovie(movies[0]);
            }
            return null;
        }
    }

    private static class DeleteFavouriteMovieTask extends AsyncTask<FavouriteMovie, Void, Void> {
        @Override
        protected Void doInBackground(FavouriteMovie... movies) {
            if (movies != null && movies.length > 0) {
                database.getMoviesDao().deleteFavouriteMovie(movies[0]);
            }
            return null;
        }
    }

    private static class InsertFavouriteMovieTask extends AsyncTask<FavouriteMovie, Void, Void> {
        @Override
        protected Void doInBackground(FavouriteMovie... movies) {
            if (movies != null && movies.length > 0) {
                database.getMoviesDao().insertFavouriteMovie(movies[0]);
            }
            return null;
        }
    }
}
