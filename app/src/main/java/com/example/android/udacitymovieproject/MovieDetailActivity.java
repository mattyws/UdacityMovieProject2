package com.example.android.udacitymovieproject;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.udacitymovieproject.adapters.MovieReviewsRecyclerViewAdapter;
import com.example.android.udacitymovieproject.adapters.MovieVideosRecyclerViewAdapter;
import com.example.android.udacitymovieproject.database.AppDatabase;

import com.example.android.udacitymovieproject.databinding.ActivityMovieDetailBinding;
import com.example.android.udacitymovieproject.model.Movie;
import com.example.android.udacitymovieproject.model.Review;
import com.example.android.udacitymovieproject.model.Video;
import com.example.android.udacitymovieproject.utils.AppExecutors;
import com.example.android.udacitymovieproject.utils.NetworkUtils;
import com.example.android.udacitymovieproject.utils.UrlUtils;
import com.example.android.udacitymovieproject.view_models.MovieDetailViewModel;
import com.example.android.udacitymovieproject.view_models.MovieDetailViewModelFactory;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int DEFAULT_MOVIE_ID = -1;
    public static final String INTENT_ID_EXTRA = "movieID";

    private Movie mMovie;
    private RequestQueue requestQueue;
    public ActivityMovieDetailBinding mMovieDetailBinding;

    private RecyclerView mVideosRecyclerView;
    private MovieVideosRecyclerViewAdapter mVideosRecyclerViewAdpter;

    private RecyclerView mReviewsRecyclerView;
    private MovieReviewsRecyclerViewAdapter mReviewsRecyclerViewAdapter;


    private AppDatabase mDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mMovieDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        mMovieDetailBinding.detailStar.setOnClickListener(this);

        mVideosRecyclerView = (RecyclerView) findViewById(R.id.video_info);
        mVideosRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.review_info);
        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        mDB = AppDatabase.getInstance(getApplicationContext());

        if(intent != null && intent.hasExtra(INTENT_ID_EXTRA) && NetworkUtils.isOnline(this) ){
            final int id = intent.getIntExtra(INTENT_ID_EXTRA, DEFAULT_MOVIE_ID);

            if( id == DEFAULT_MOVIE_ID)
                throw new IllegalArgumentException();

            MovieDetailViewModelFactory factory = new MovieDetailViewModelFactory(mDB, id);

            final MovieDetailViewModel viewModel
                    = ViewModelProviders.of(this, factory).get(MovieDetailViewModel.class);

            // COMPLETED (12) Observe the LiveData object in the ViewModel. Use it also when removing the observer
            viewModel.getMovie().observe(this, new Observer<Movie>() {
                @Override
                public void onChanged(@Nullable Movie movie) {
                    viewModel.getMovie().removeObserver(this);
                    if(movie != null){
                        showFavoriteDetailStar();
                    } else {
                        showNotFavoriteDetailStar();
                    }
                }
            });

            // Search if the movie is on database
            requestDataFromInternet(id);
        }
    }

    private void requestDataFromInternet(int id) {
        requestQueue = Volley.newRequestQueue(this);
        try {
            // As Volley request is done by queue, it will execute first the movie detail > videos > reviews
            requestAndShowMovie(id);
            requestAndShowVideos(id);
            requestAndShowReviews(id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void requestAndShowMovie(int movieId) throws MalformedURLException {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                UrlUtils.buildMovieDetailUrl(this, movieId).toString(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            createMovie(response);
                            bindMovieToView();
                            showMovieDetail();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
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

    private void requestAndShowVideos(final int movieId) throws MalformedURLException {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                UrlUtils.buildMovieVideosUrl(this, movieId).toString(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(mMovie != null) {
                            try {
                                getVideosList(response);
                                bindVideosToView();
                                showVideosList();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

    private void requestAndShowReviews(int movieId) throws MalformedURLException {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                UrlUtils.buildMovieReviewsUrl(this, movieId).toString(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(mMovie != null) {
                            try {
                                getReviewsList(response);
                                bindReviewsToView();
                                showReviewsList();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

    private void showReviewsList() {
        if(mMovie.isReviewed()) {
            mMovieDetailBinding.detailReviewsLabel.setVisibility(View.VISIBLE);
            mReviewsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void bindReviewsToView() {
        if(mMovie.isReviewed()) {
            mReviewsRecyclerViewAdapter = new MovieReviewsRecyclerViewAdapter(this, mMovie.getReviews());
            mReviewsRecyclerView.setAdapter(mReviewsRecyclerViewAdapter);
            mReviewsRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void getReviewsList(JSONObject response) throws JSONException {
        JSONArray results = response.getJSONArray(getString(R.string.json_review_result));
        List<Review> reviews = new ArrayList<>();
        for (int i = 0; i < results.length(); i++){
            JSONObject jsonVideo = results.getJSONObject(i);
            String id = jsonVideo.getString(getString(R.string.json_review_id));
            String author = jsonVideo.getString(getString(R.string.json_review_author));
            String content = jsonVideo.getString(getString(R.string.json_review_text));
            Review r = new Review(id, author, content);
            reviews.add(r);
        }
        mMovie.setReviews(reviews);
    }

    private void showVideosList() {
        if(mMovie.hasTrailers()) {
            mVideosRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void bindVideosToView() {
        if(mMovie.hasTrailers()) {
            mVideosRecyclerViewAdpter = new MovieVideosRecyclerViewAdapter(this, mMovie.getVideos());
            mVideosRecyclerView.setAdapter(mVideosRecyclerViewAdpter);
            mVideosRecyclerViewAdpter.notifyDataSetChanged();
        }
    }

    private void getVideosList(JSONObject response) throws JSONException {
        JSONArray results = response.getJSONArray(getString(R.string.json_video_result));
        List<Video> videos = new ArrayList<>();
        for (int i = 0; i < results.length(); i++){
            JSONObject jsonVideo = results.getJSONObject(i);
            String id = jsonVideo.getString(getString(R.string.json_video_id));
            String name = jsonVideo.getString(getString(R.string.json_video_name));
            String key = jsonVideo.getString(getString(R.string.json_video_key));
            Video v = new Video(id, name, key);
            videos.add(v);
        }
        mMovie.setVideos(videos);
    }


    private void showMovieDetail() {
        mMovieDetailBinding.detailProgressBar.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.detailMoviePoster.setVisibility(View.VISIBLE);
        mMovieDetailBinding.detailMovieTitle.setVisibility(View.VISIBLE);
        mMovieDetailBinding.detailOriginalTitle.setVisibility(View.VISIBLE);
        mMovieDetailBinding.detailMovieReleaseDate.setVisibility(View.VISIBLE);
        mMovieDetailBinding.detailUserRatingLabel.setVisibility(View.VISIBLE);
        mMovieDetailBinding.detailMovieUserRating.setVisibility(View.VISIBLE);
        mMovieDetailBinding.detailMovieRuntime.setVisibility(View.VISIBLE);
        mMovieDetailBinding.detailSynopsisLabel.setVisibility(View.VISIBLE);
        mMovieDetailBinding.detailMovieSynopsis.setVisibility(View.VISIBLE);
    }

    private void showLoadingDetail(){
        mMovieDetailBinding.detailProgressBar.setVisibility(View.VISIBLE);
        mMovieDetailBinding.detailMoviePoster.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.detailMovieTitle.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.detailOriginalTitle.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.detailMovieReleaseDate.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.detailUserRatingLabel.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.detailMovieUserRating.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.detailMovieRuntime.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.detailSynopsisLabel.setVisibility(View.INVISIBLE);
        mMovieDetailBinding.detailMovieSynopsis.setVisibility(View.INVISIBLE);
    }

    private void bindMovieToView() throws MalformedURLException {
        mMovieDetailBinding.detailMovieTitle.setText(mMovie.getTitle());
        mMovieDetailBinding.detailOriginalTitle.setText(mMovie.getOriginalTitle());
        mMovieDetailBinding.detailMovieReleaseDate.setText(mMovie.getReleaseDate());
        mMovieDetailBinding.detailMovieUserRating.setText(String.valueOf(mMovie.getUserRating()));
        mMovieDetailBinding.detailMovieRuntime.setText(getString(R.string.detail_runtime, mMovie.getRuntime()));
        mMovieDetailBinding.detailMovieSynopsis.setText(mMovie.getSynopsis());
        Picasso.with(this).load(UrlUtils.buildMoviePosterUri(this, mMovie.getPosterPath())).fit().centerInside().into(mMovieDetailBinding.detailMoviePoster);
    }

    private void createMovie(JSONObject response) throws JSONException {
        int id = response.getInt(getString(R.string.json_movie_id));
        int runtime = response.getInt(getString(R.string.json_movie_runtime));
        double userRating = response.getDouble(getString(R.string.json_movie_user_rating));
        String title = response.getString(getString(R.string.json_movie_title));
        String posterPath = response.getString(getString(R.string.json_movie_poster_path));
        String synopsis = response.getString(getString(R.string.json_movie_synopsis));
        String releaseDate = response.getString(getString(R.string.json_movie_release_date));
        String originalTitle = response.getString(getString(R.string.json_movie_original_title));
        mMovie = new Movie(id, posterPath, title, originalTitle, releaseDate, synopsis, userRating, runtime);
    }

    @Override
    public void onClick(View v) {
        if(!isStarFavorite()) {
            showNotFavoriteDetailStar();
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDB.moviesDAO().insertTask(mMovie);
                }
            });
        } else {
            showFavoriteDetailStar();
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDB.moviesDAO().deleteTask(mMovie);
                }
            });
        }
    }

    /**
     * Change the drawing for the star to state favorite
     */
    private void showFavoriteDetailStar(){
        mMovieDetailBinding.detailStar.setImageResource(R.drawable.star_favorite);
        mMovieDetailBinding.detailStar.setTag(R.drawable.star_favorite);
        if(mMovieDetailBinding.detailStar.getVisibility() == View.INVISIBLE){
            mMovieDetailBinding.detailStar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Change the drawing for the star to state not favorite
     */
    private void showNotFavoriteDetailStar() {
        mMovieDetailBinding.detailStar.setImageResource(R.drawable.star_not_favorite);
        mMovieDetailBinding.detailStar.setTag(R.drawable.star_not_favorite);
        if(mMovieDetailBinding.detailStar.getVisibility() == View.INVISIBLE){
            mMovieDetailBinding.detailStar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Is the star's state favorite?
     * @return true if is favorite, false otherwise
     */
    private boolean isStarFavorite(){
        return mMovieDetailBinding.detailStar.getTag() != null && (int) mMovieDetailBinding.detailStar.getTag() == R.drawable.star_favorite;
    }


//    public static void watchYoutubeVideo(Context context, String id){
//        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
//        Intent webIntent = new Intent(Intent.ACTION_VIEW,
//                Uri.parse("http://www.youtube.com/watch?v=" + id));
//        try {
//            context.startActivity(appIntent);
//        } catch (ActivityNotFoundException ex) {
//            context.startActivity(webIntent);
//        }
//    }
}