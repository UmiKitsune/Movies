package com.example.movies28.workWithSite;

import com.example.movies28.database.Movie;
import com.example.movies28.database.Review;
import com.example.movies28.database.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSON {
    //переменные для нужных в получении ключей
    private static final String KEY_RESULTS = "results";

    //для отывов
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";

    //для видео
    private static final String KEY_VIDEO = "key";
    private static final String KEY_VIDEO_NAME = "name";
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    //информация о фильме
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";

    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    public static final String SMALL_POSTER_SIZE = "w185";
    public static final String BIG_POSTER_SIZE = "w780";

    //метод для отзывов
    public static ArrayList<Review> getReviewsFromJSON (JSONObject jsonObject) {
        ArrayList<Review> reviews = new ArrayList<>();
        if (jsonObject == null) {
            return reviews;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectReview = jsonArray.getJSONObject(i);
                String author = objectReview.getString(KEY_AUTHOR);
                String content = objectReview.getString(KEY_CONTENT);
                Review review = new Review(author, content);
                reviews.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    //метод для трейлеров и видео
    public static ArrayList<Trailer> getTrailersFromJSON (JSONObject jsonObject) {
        ArrayList<Trailer> trailers = new ArrayList<>();
        if (jsonObject == null) {
            return trailers;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectTrailer = jsonArray.getJSONObject(i);
                String key = BASE_YOUTUBE_URL + objectTrailer.getString(KEY_VIDEO);
                String name = objectTrailer.getString(KEY_VIDEO_NAME);
                Trailer trailer = new Trailer(key, name);
                trailers.add(trailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailers;
    }

    //1 - получаем массив Json по ключу results
    public static ArrayList<Movie> getMoviesFromJSON (JSONObject jsonObject) {
        ArrayList<Movie> movies = new ArrayList<>();
        if (jsonObject == null) {
            return movies;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectMovie = jsonArray.getJSONObject(i);
                int id = objectMovie.getInt(KEY_ID);
                int voteCount = objectMovie.getInt(KEY_VOTE_COUNT);
                String title = objectMovie.getString(KEY_TITLE);
                String originalTitle = objectMovie.getString(KEY_ORIGINAL_TITLE);
                String overview = objectMovie.getString(KEY_OVERVIEW);
                String posterPath = BASE_POSTER_URL + SMALL_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String bigPosterPath = BASE_POSTER_URL + BIG_POSTER_SIZE + objectMovie.getString(KEY_POSTER_PATH);
                String backdropPath = objectMovie.getString(KEY_BACKDROP_PATH);
                double voteAverage = objectMovie.getDouble(KEY_VOTE_AVERAGE);
                String releaseDate = objectMovie.getString(KEY_RELEASE_DATE);
                Movie movie = new Movie(id, voteCount, title, originalTitle, overview, posterPath, bigPosterPath, backdropPath, voteAverage, releaseDate);
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }
}
