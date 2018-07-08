package com.example.android.udacitymovieproject.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.udacitymovieproject.R;
import com.example.android.udacitymovieproject.model.Video;

import java.util.List;

public class MovieVideosRecyclerViewAdapter extends RecyclerView.Adapter<MovieVideosRecyclerViewAdapter.VideosViewHolder> {

    private Context context;
    private List<Video> videos;


    public MovieVideosRecyclerViewAdapter(Context context, List<Video> videos) {
        this.context = context;
        this.videos = videos;
    }

    @NonNull
    @Override
    public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.videos_view_cell, parent, false);
        VideosViewHolder holder = new VideosViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideosViewHolder holder, int position) {
        Video video = videos.get(position);
        holder.mVideoName.setText(video.getName());
        holder.key = video.getVideoKey();
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }


    public class VideosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageButton mImageButton;
        TextView mVideoName;
        String key;

        public VideosViewHolder(View itemView) {
            super(itemView);
            mVideoName = (TextView) itemView.findViewById(R.id.video_name);
            mImageButton = (ImageButton) itemView.findViewById(R.id.video_button);
            mImageButton.setOnClickListener(this);
            mVideoName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(v.getClass().getCanonicalName(), "Try hard " + String.valueOf(getAdapterPosition()));
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.youtube_app, key)));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(context.getString(R.string.youtube_web, key)));
            try {
                context.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                context.startActivity(webIntent);
            }
//            onItemClick(v, getAdapterPosition());
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}
