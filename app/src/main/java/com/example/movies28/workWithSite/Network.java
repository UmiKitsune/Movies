package com.example.movies28.workWithSite;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class Network {
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    //основной url для подкачки прикрепленного видео. %s - сюда вставим id
    private static final String BASE_URL_VIDEOS = "https://api.themoviedb.org/3/movie/%s/videos";
    //основной url для подкачки отзывов. %s - сюда вставим id фильма
    private static final String BASE_URL_REVIEWS = "https://api.themoviedb.org/3/movie/%s/reviews";

    //необходимые для запроса параметры
    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_LANGUAGE = "language";
    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_PAGE = "page";
    private static final String PARAMS_MIN_VOTE_COUNT = "vote_count.gte";

    //их значения
    private static final String API_KEY = "1d4b79a9c6931bcb441aaabdeb2f5e68";
    //private static final String LANGUAGE_RU = "ru-RU";
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_VOTE_AVERAGE = "vote_average.desc";
    private static final String MIN_VOTE_COUNT = "1000";


    //переменные для метода который проверяет какая сортировка была выбрана и возвращает результаты
    public static final int POPULARITY = 0;
    public static final int VOTE_AVERAGE = 1;

    //метода для создания полного url для подкачки видео
    public static URL buildURLToVideos(int id, String lang) {
        Uri uri = Uri.parse(String.format(BASE_URL_VIDEOS, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, lang)
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //создает json для видео
    public static JSONObject getJSONForVideos(int movieId,String lang) {
        JSONObject result = null;
        URL url = buildURLToVideos(movieId, lang);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  result;
    }

    //метода для создания полного url для отзывов
    public static URL buildURLToReviews(int id, String lang) {
        Uri uri = Uri.parse(String.format(BASE_URL_REVIEWS, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, lang)
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //создает json для отзывов
    public static JSONObject getJSONForReviews(int movieId, String lang) {
        JSONObject result = null;
        URL url = buildURLToReviews(movieId, lang);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  result;
    }


    //метод для формирования запроса
    public static URL buildUrl(int sortBy, int page,String lang) {
        URL result_url = null;
        String methodOfSort;
        if (sortBy == POPULARITY) {
            methodOfSort = SORT_BY_POPULARITY;
        } else {
            methodOfSort = SORT_BY_VOTE_AVERAGE;
        }
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, lang)
                .appendQueryParameter(PARAMS_SORT_BY, methodOfSort)
                .appendQueryParameter(PARAMS_MIN_VOTE_COUNT, MIN_VOTE_COUNT)
                .appendQueryParameter(PARAMS_PAGE, Integer.toString(page))
                .build();
        try {
            result_url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result_url;
    }

    //метод который будет загружать данные из интернета
    public static JSONObject getJSONFromNetwork(int sortBy, int page, String lang) {
        JSONObject result = null;
        URL url = buildUrl(sortBy, page, lang);
        System.out.println(url);
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  result;
    }

    //для другого потока
    private static class JSONLoadTask extends AsyncTask<URL, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject result = null;
            if (urls == null || urls.length == 0) {
                return result;
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) urls[0].openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuilder builder = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }
                result = new JSONObject(builder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }
    }

    //для подгрузки данных из интеренета
    //источник от куда мы будем подгружать обычно передают в бандл(save instant state...)
    //бандл используется для сохранения состояния про повороте экрана
    public static class JSONLoader extends AsyncTaskLoader<JSONObject> {
        //создаем объект бандл
        private Bundle bundle;

        private OnStartLoadingListener onStartLoadingListener;
        public interface OnStartLoadingListener{
            void onStartLoading();
        }
        public void setOnStartLoadingListener(OnStartLoadingListener onStartLoadingListener) {
            this.onStartLoadingListener = onStartLoadingListener;
        }

        //и передаем обект бандл в конструктор дополнительным параметром
        public JSONLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (onStartLoadingListener != null) {
                onStartLoadingListener.onStartLoading();
            }
            forceLoad();
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {
            if (bundle == null) {
                return null;
            }
            String urlAsString = bundle.getString("url");
            URL url = null;
            try {
                url = new URL(urlAsString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            //этот код из JSONLoadTask extends AsyncTask
            JSONObject result = null;
            if (url == null) {
                return null;
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuilder builder = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }
                result = new JSONObject(builder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }
    }

}
