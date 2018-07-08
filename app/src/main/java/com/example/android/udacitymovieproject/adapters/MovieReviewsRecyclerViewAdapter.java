package com.example.android.udacitymovieproject.adapters;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.udacitymovieproject.R;
import com.example.android.udacitymovieproject.model.Review;

import java.util.List;

public class MovieReviewsRecyclerViewAdapter extends RecyclerView.Adapter<MovieReviewsRecyclerViewAdapter.ReviewViewHolder>{

    private Context mContext;
    private List<Review> mReviews;

    public MovieReviewsRecyclerViewAdapter(Context mContext, List<Review> mReviews) {
        this.mContext = mContext;
        this.mReviews = mReviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_view_cell, parent, false);
        ReviewViewHolder holder = new ReviewViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review r = mReviews.get(position);
        holder.mReviewAuthor.setText(r.getAuthor());
        holder.mReviewText.setText(r.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mReviewAuthor;
        TextView mReviewText;
        ImageButton mArrowButton;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mReviewAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
            mReviewText = (TextView) itemView.findViewById(R.id.tv_review_text);
            mArrowButton = (ImageButton) itemView.findViewById(R.id.arrow_button);

            mReviewAuthor.setOnClickListener(this);
            mArrowButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mReviewText.getVisibility() == View.INVISIBLE || mReviewText.getVisibility() == View.GONE){
                mReviewText.setVisibility(View.VISIBLE);
                mArrowButton.setImageResource(R.drawable.ic_arrow_drop_up);
            } else {
                mReviewText.setVisibility(View.GONE);
                mArrowButton.setImageResource(R.drawable.ic_arrow_drop_down);
            }
        }
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public List<Review> getmReviews() {
        return mReviews;
    }

    public void setmReviews(List<Review> mReviews) {
        this.mReviews = mReviews;
    }
}
