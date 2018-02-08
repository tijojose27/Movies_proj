package com.example.tijo.movies_proj;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tijo.movies_proj.Utility.GetMovies;
import com.example.tijo.movies_proj.data.Movie;
import com.example.tijo.movies_proj.data.Reviews;
import com.example.tijo.movies_proj.databinding.ActivityDetailsBinding;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {


    //DATABINDING
    ActivityDetailsBinding detailsBinding;

    public final String IMDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    public final String APIKEY = "api_key=8628eac7c7875b9b1a42093545772f7b";
    public final String REVIEW_ADD = "/reviews?";

    public String movieId;

    ArrayList<Reviews> currReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //SETTING THE UP BUTTON
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //GETTING THE PARCELABLE'S
        Intent intent = getIntent();
        Movie currMovie = intent.getParcelableExtra("Movie Item");

        // SETTING UP DATA IN THE VIEW USING THE BINDING
        detailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        detailsBinding.textViewTitle.setText(currMovie.getTitle());
        detailsBinding.textViewReleaseDate.setText(currMovie.getReleaseDate());

        String rating = currMovie.getRating().toString() + " / 10";

        detailsBinding.textViewUserRating.setText(rating);
        detailsBinding.textViewSynopsis.setText(currMovie.getSynopsis());

        Picasso.with(this).load(currMovie.getImgUrlOriginal()).into(detailsBinding.imageViewDetails);

        //GETTING THE MOVIE ID FOR THE MOVIE CLICKED
        movieId = currMovie.getMovieId();

        //OKHTTP TO GET THE REVIEWS USING THE  MOVIE ID
        getReviews();


    }


    public void getReviews() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(IMDB_BASE_URL + movieId + REVIEW_ADD + APIKEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    final String json = response.body().string();
                    DetailsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currReviews = GetMovies.reviewsToArray(json);
                            updateReviewList(currReviews);
                        }
                    });
                }
            }
        });
    }





    public void updateReviewList(ArrayList<Reviews> currReviews) {

        if (currReviews.size() == 0) {
            detailsBinding.textViewReviewLabel.setVisibility(View.INVISIBLE);
        } else {


            LinearLayout linearLayout = detailsBinding.includeReview.linearLayoutSimple;

            for (int i = 0; i < currReviews.size(); i++) {
                TextView author = new TextView(this);
                author.setText(currReviews.get(i).author);
                author.setTypeface(Typeface.DEFAULT_BOLD);
                author.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                author.setTextSize(20);
                linearLayout.addView(author);

                TextView review = new TextView(this);
                review.setText(currReviews.get(i).content);
                linearLayout.addView(review);

                View v = new View(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 20);

                params.setMargins(0, 15, 0, 35);
                v.setLayoutParams(params);
                linearLayout.addView(v);
            }
        }

    }

}