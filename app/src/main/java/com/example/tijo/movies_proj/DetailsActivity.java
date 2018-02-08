package com.example.tijo.movies_proj;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tijo.movies_proj.Utility.GetMovies;
import com.example.tijo.movies_proj.data.Movie;
import com.example.tijo.movies_proj.data.Reviews;
import com.example.tijo.movies_proj.data.Trailers;
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
    public final String TRAILER_ADD = "/videos?";

    public String movieId;

    ArrayList<Reviews> currReviews;
    ArrayList<Trailers> currTrailers;

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

        getTrailers();

    }

    // OPEN OKHTTP AND GET THE REVIEWS WITH THE CURRENT MOVIE ID
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

    // UPDATE THE REVIEW  LIST
    public void updateReviewList(ArrayList<Reviews> currReviews) {

        if (currReviews.size() == 0) {
            detailsBinding.textViewReviewLabel.setVisibility(View.INVISIBLE);
        } else {

            LinearLayout linearLayout = detailsBinding.includeLinearLayoutReview.linearLayoutSimple;

            //PROGRAMATICALLY CREATE VIEWS AND POPULATE IT WITH THE AUTHOR AND REVIEW
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

                // CREATING A DIVIDER BETWEEN THE REVIEWS FOR READABILITY
                View v = new View(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 20);

                params.setMargins(0, 15, 0, 35);
                v.setLayoutParams(params);
                linearLayout.addView(v);
            }
        }

    }

    public void getTrailers(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(IMDB_BASE_URL+movieId+TRAILER_ADD+APIKEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(!response.isSuccessful()){
                    throw new IOException("Unexpected code "+response);
                }else {
                    final String json = response.body().string();
                    DetailsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currTrailers = GetMovies.tailersToArray(json);
                            updateTrailerList(currTrailers);
                        }
                    });
                }
            }
        });
    }

    //UPDATING THE UI FOR THE TAILER SECTION IF AVAILABLE
    private void updateTrailerList(final ArrayList<Trailers> currTrailers) {

        if(currTrailers.size()==0){
            detailsBinding.textViewTrailerLabel.setVisibility(View.INVISIBLE);

        }else {
            LinearLayout linearLayout = detailsBinding.includeLinearLayoutTrailer.linearLayoutSimple;

            if(currTrailers.size()<=3) {
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            }else {
                linearLayout.setOrientation(LinearLayout.VERTICAL);
            }

            for(int i =0;i<currTrailers.size();i++){

                final String url =currTrailers.get(i).BASE_URL+ currTrailers.get(i).key;

                TextView trailer = new TextView(this);
                trailer.setText(currTrailers.get(i).name);
                trailer.setBackgroundResource(R.drawable.custom_button);
                trailer.setTypeface(Typeface.DEFAULT_BOLD);
                trailer.setGravity(Gravity.CENTER);
                trailer.setTextSize(15);
                trailer.setTextColor(getResources().getColor(android.R.color.white));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f);
                params.setMargins(10,10,10,10);
                trailer.setLayoutParams(params);

                linearLayout.addView(trailer);


                trailer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                });

            }
        }
    }

}