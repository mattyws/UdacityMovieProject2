package com.example.android.udacitymovieproject.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Relation;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity(tableName = "movies")
public class Movie implements Serializable{

    @PrimaryKey
    private int id;
    @Ignore
    private double userRating;
    @Ignore
    private int runtime;
    @Ignore
    private String title;
    private String posterPath;
    @Ignore
    private String synopsis;
    @Ignore
    private String releaseDate;
    @Ignore
    private String originalTitle;
    @Ignore
    private List<Video> videos;
    @Ignore
    private List<Review> reviews;

    public Movie(int id, String posterPath){
        setId(id);
        setPosterPath(posterPath);
    }

    @Ignore
    public Movie(int id, String posterPath, String title, String originalTitle, String releaseDate, String synopsis,
                 double userRating, int runtime){
        setId(id);
        setTitle(title);
        setPosterPath(posterPath);
        setOriginalTitle(originalTitle);
        setReleaseDate(releaseDate);
        setSynopsis(synopsis);
        setUserRating(userRating);
        setRuntime(runtime);
    }

    public boolean isReviewed(){
        return (reviews != null) && (reviews.size() > 0);
    }

    public boolean hasTrailers(){
        return (videos != null) && (videos.size() > 0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getReleaseDate() {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(releaseDate);
            String formattedDate = new SimpleDateFormat("dd MMM yyyy").format(date);
            return formattedDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }
}
