package com.example.android.udacitymovieproject.utils;

import android.content.Context;
import android.net.Uri;

import com.example.android.udacitymovieproject.R;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtils {


    public static URL buildUrl(Context context, @com.example.android.udacitymovieproject.MainActivity.ListingMode int listingEnum) throws MalformedURLException {
        Uri uri;

        if(listingEnum == com.example.android.udacitymovieproject.MainActivity.ListingMode.MOST_POPULAR){
            uri = Uri.parse(context.getString(R.string.api_domain_popular));
        } else if (listingEnum == com.example.android.udacitymovieproject.MainActivity.ListingMode.TOP_RATED) {
            uri = Uri.parse(context.getString(R.string.api_domain_top_rated));
        } else {
            throw new IllegalArgumentException();
        }
        uri = uri.buildUpon().appendQueryParameter(context.getString(R.string.api_key_query), context.getString(R.string.API_KEY))
                .build();
        URL url = new URL(uri.toString());
        return url;
    }

    public static URL buildMovieDetailUrl(Context context, int movieId) throws MalformedURLException {
        Uri uri;
        if(movieId != -1){
            uri = Uri.parse(context.getString(R.string.api_domain_movie_detail, movieId));
        } else {
            throw new IllegalArgumentException();
        }
        uri = uri.buildUpon().appendQueryParameter(context.getString(R.string.api_key_query), context.getString(R.string.API_KEY))
                .build();
        URL url = new URL(uri.toString());
        return url;
    }

    public static String buildMoviePosterUri(Context context, String posterPath) throws MalformedURLException {
        Uri uri = Uri.parse(context.getString(R.string.api_domain_images)+posterPath);
        uri = uri.buildUpon().appendQueryParameter(context.getString(R.string.api_key_query), context.getString(R.string.API_KEY))
                .build();
        return uri.toString();
    }

    public static URL buildMovieVideosUrl(Context context, int movieId) throws MalformedURLException {
        Uri uri;
        if(movieId != -1){
            uri = Uri.parse(context.getString(R.string.api_domain_movie_videos, movieId));
        } else {
            throw new IllegalArgumentException();
        }
        uri = uri.buildUpon().appendQueryParameter(context.getString(R.string.api_key_query), context.getString(R.string.API_KEY))
                .build();
        URL url = new URL(uri.toString());
        return url;
    }

    public static URL buildMovieReviewsUrl(Context context, int movieId) throws MalformedURLException {
        Uri uri;
        if(movieId != -1){
            uri = Uri.parse(context.getString(R.string.api_domain_movie_reviews, movieId));
        } else {
            throw new IllegalArgumentException();
        }
        uri = uri.buildUpon().appendQueryParameter(context.getString(R.string.api_key_query), context.getString(R.string.API_KEY))
                .build();
        URL url = new URL(uri.toString());
        return url;
    }

}
