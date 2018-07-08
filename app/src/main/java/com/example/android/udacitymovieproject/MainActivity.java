package com.example.android.udacitymovieproject;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.udacitymovieproject.adapters.PostersRecyclerViewAdapter;
import com.example.android.udacitymovieproject.model.Movie;
import com.example.android.udacitymovieproject.utils.NetworkUtils;
import com.example.android.udacitymovieproject.utils.RecyclerViewClickListener;
import com.example.android.udacitymovieproject.utils.UrlUtils;
import com.example.android.udacitymovieproject.view_models.MainViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener {


    @ListingMode
    int listingMode = ListingMode.MOST_POPULAR;
    PostersRecyclerViewAdapter mAdapter;
    RecyclerView recyclerView;
    ProgressBar moviesProgressBar;
    private boolean mShowFavoriteData = false;
    private List<Movie> mMovies;
    private RequestQueue requestQueue;
    TextView noNetworkTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // data to populate the RecyclerView with
        moviesProgressBar = (ProgressBar) findViewById(R.id.pb_posters_progress_bar);
        recyclerView = (RecyclerView) findViewById(R.id.mv_poster_view);
        noNetworkTextView = (TextView) findViewById(R.id.no_network_text_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mMovies = new ArrayList<>();
        if(NetworkUtils.isOnline(this)) {

            requestQueue = Volley.newRequestQueue(this);
            try {
                parseJsonMovies();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            moviesProgressBar.setVisibility(View.INVISIBLE);
            noNetworkTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void retrieveFromViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if(mShowFavoriteData) {
                    mShowFavoriteData = false;
                    mAdapter.setMovies(movies);
                    mAdapter.notifyDataSetChanged();
                }
                moviesProgressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();
        if(NetworkUtils.isOnline(this)) {
            if (itemSelected == R.id.menu_sort_popular) {
                listingMode = ListingMode.MOST_POPULAR;
            } else if (itemSelected == R.id.menu_sort_top_rated) {
                listingMode = ListingMode.TOP_RATED;
            } else if (itemSelected == R.id.menu_show_favorites){
                listingMode = ListingMode.FAVORITES;
                mShowFavoriteData = true;
            }
            try {
                moviesProgressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                mMovies = new ArrayList<>();
                if(listingMode == ListingMode.FAVORITES)
                    retrieveFromViewModel();
                else
                    refreshMovies();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, getString(R.string.no_network_connection), Toast.LENGTH_LONG)
                .show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshMovies() throws MalformedURLException {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                UrlUtils.buildUrl(this, listingMode).toString(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            createMovieArray(response);
                            mAdapter.setMovies(mMovies);
                            moviesProgressBar.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }

        );
        requestQueue.add(request);
    }

    private void parseJsonMovies() throws MalformedURLException {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                UrlUtils.buildUrl(this, listingMode).toString(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            createMovieArray(response);
                            mAdapter = new PostersRecyclerViewAdapter(MainActivity.this, mMovies);
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.setClickListener(MainActivity.this);
                            moviesProgressBar.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }

        );
        requestQueue.add(request);
    }

    private void createMovieArray(JSONObject response) throws JSONException {
        JSONArray jsonMovies = response.getJSONArray(getString(R.string.json_movies_result));
        for (int i = 0; i < jsonMovies.length(); i++){
            JSONObject jsonMovie = jsonMovies.getJSONObject(i);
            int id = jsonMovie.getInt(getString(R.string.json_movie_id));
            String posterPath = jsonMovie.getString(getString(R.string.json_movie_poster_path));
            Movie m = new Movie(id, posterPath);
            mMovies.add(m);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent initDetailActivity = new Intent(MainActivity.this, MovieDetailActivity.class);
        initDetailActivity.putExtra(MovieDetailActivity.INTENT_ID_EXTRA, mAdapter.getMovies().get(position).getId());
        startActivity(initDetailActivity);
    }


    @android.support.annotation.IntDef({ListingMode.TOP_RATED, ListingMode.MOST_POPULAR})
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.SOURCE)
    public @interface ListingMode {
        int TOP_RATED = 0;
        int MOST_POPULAR = 1;
        int FAVORITES = 2;
    }
}
