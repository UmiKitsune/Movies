package com.example.movies28;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movies28.adapters.MovieAdapter;
import com.example.movies28.database.MainViewModel;
import com.example.movies28.database.Movie;
import com.example.movies28.workWithSite.JSON;
import com.example.movies28.workWithSite.Network;

import org.json.JSONObject;

import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {

    private Switch switchSort;
    RecyclerView recyclerViewPosters;
    private MovieAdapter adapter;
    private TextView textViewTopRated;
    private TextView textViewPopularity;
    private MainViewModel viewModel;
    private ProgressBar progressBarLoading;

    //для работы с AsyncLoader
    private static final int LOADER_ID = 123;
    private LoaderManager loaderManager;
    private static int page = 1;
    private static int methodOfSort;
    private static String lang;
    private static boolean isLoading = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemFavourite:
                Intent intentToFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(intentToFavourite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getColumnCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return width / 185 > 2 ? width/ 185 : 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lang = Locale.getDefault().getLanguage();
        //работа с AsyncLoader
        loaderManager = LoaderManager.getInstance(this); //используется патерн синглтон

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        switchSort = findViewById(R.id.switchSort);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        progressBarLoading = findViewById(R.id.progressBarLoading);

        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, getColumnCount()));
        adapter = new MovieAdapter();
        recyclerViewPosters.setAdapter(adapter);

        switchSort.setChecked(true);
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                page = 1;
                setMethodOfSort(isChecked);
            }
        });
        switchSort.setChecked(false);

        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = adapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });

        adapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                if(!isLoading) {
                    downloadData(methodOfSort, page, lang);
                }
            }
        });

        LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                if (page == 1) {
                    adapter.setMovies(movies);
                }
                //adapter.setMovies(movies);
            }
        });
    }

    public void onClickSetPopularity(View view) {
        setMethodOfSort(false);
        switchSort.setChecked(false);
    }

    public void onClickSetTopRated(View view) {
        setMethodOfSort(true);
        switchSort.setChecked(true);
    }

    private void setMethodOfSort(boolean isTopRated) {
        if (isTopRated) {
            textViewTopRated.setTextColor(ContextCompat.getColor(this ,R.color.colorAccent));
            textViewPopularity.setTextColor(ContextCompat.getColor(this, R.color.white));
            methodOfSort = Network.VOTE_AVERAGE;
        } else {
            textViewPopularity.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            textViewTopRated.setTextColor(ContextCompat.getColor(this, R.color.white));
            methodOfSort = Network.POPULARITY;
        }
        downloadData(methodOfSort, page, lang);
    }

    private void downloadData(int methodOfSort, int page, String lang) {
        URL url = Network.buildUrl(methodOfSort, page, lang);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        loaderManager.restartLoader(LOADER_ID, bundle, this);

//        JSONObject jsonObject = Network.getJSONFromNetwork(methodOfSort, 1);
//        List<Movie> movies = JSON.getMoviesFromJSON(jsonObject);
//        if (movies != null && !movies.isEmpty()) {
//            viewModel.deleteAllMovies();
//            for (Movie movie : movies) {
//                viewModel.insertMovie(movie);
//            }
//        }
    }

    //методы которые переопределили для работы с AsyncLoader
    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle bundle) {
        Network.JSONLoader jsonLoader = new Network.JSONLoader(this, bundle);
        jsonLoader.setOnStartLoadingListener(new Network.JSONLoader.OnStartLoadingListener() {
            @Override
            public void onStartLoading() {
                progressBarLoading.setVisibility(View.VISIBLE);
                isLoading = true;
            }
        });
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject jsonObject) {
        List<Movie> movies = JSON.getMoviesFromJSON(jsonObject);
        if (movies != null && !movies.isEmpty()) {
            if (page == 1) {
                viewModel.deleteAllMovies();
                adapter.clear();
            }
            for (Movie movie : movies) {
                viewModel.insertMovie(movie);
            }
            adapter.addMovies(movies);
            page++;
        }
        progressBarLoading.setVisibility(View.INVISIBLE);
        isLoading = false;
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}