package com.example.tijo.movies_proj;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tijo.movies_proj.Database.MoviesContract;
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

    // CRAEATING ARRAY
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
        final Movie currMovie = intent.getParcelableExtra("Movie Item");

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

        // CHECKING IF MOVIE WAS FAVIORATED
        boolean favoriated = isFavoriate(movieId);

        // IF IT WAS WE CHANGE THE TEXT FOR THE BUTTON TO REMOVE FROM FAVORIATE
        if (favoriated) {
            detailsBinding.favBut.setText("REMOVE FROM FAVORIATE");
            detailsBinding.favBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(movieId).build();

                    int deleted = getContentResolver().delete(uri, null, null);

                    if (deleted != 0) {
                        Toast.makeText(getBaseContext(), "DELETED", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            });

            // IF IT WAS NOT THEN WE GO AHEAD AND ADD IT TO THE FAVOROATES
        } else {
            detailsBinding.favBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, currMovie.getMovieId());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, currMovie.getTitle());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_SYNOPSIS, currMovie.getSynopsis());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_IMAGE_URL, currMovie.getImg_url());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_RATING, currMovie.getRating());
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, currMovie.releaseDate);
                    contentValues.put(MoviesContract.MoviesEntry.COLUMN_IMAGE_URL_ORIGINAL, currMovie.getImgUrlOriginal());

                    Uri uri = getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, contentValues);

                    if (uri != null) {
                        Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            });
        }

    }

    // HELPER METHOD TO CHECK IF THE CURRENT MOVIE IS FAVORIATED
    private boolean isFavoriate(String movieId) {
        Cursor cursor = getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                null, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if (movieId.equals(cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID)))) {
                    return true;
                }
                cursor.moveToNext();
            }
        }
        return false;
    }

    // OPEN OKHTTP AND GET THE REVIEWS WITH THE CURRENT MOVIE ID
    public void getReviews() {

        currReviews = new ArrayList<>();
        currReviews.clear();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(IMDB_BASE_URL + movieId + REVIEW_ADD + APIKEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                //SENDING AN EMPTY LIST SO IT WOULD HID THE VIEW
                updateReviewList(currReviews);
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

    // GETTING TRAILERS
    public void getTrailers() {
        currTrailers = new ArrayList<>();
        currTrailers.clear();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(IMDB_BASE_URL + movieId + TRAILER_ADD + APIKEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                updateTrailerList(currTrailers);
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
                            currTrailers = GetMovies.tailersToArray(json);
                            updateTrailerList(currTrailers);
                        }
                    });
                }
            }
        });
    }

    //UPDATING THE UI FOR THE TRAILER SECTION IF AVAILABLE
    private void updateTrailerList(final ArrayList<Trailers> currTrailers) {

        if (currTrailers.size() == 0) {
            detailsBinding.textViewTrailerLabel.setVisibility(View.INVISIBLE);

        } else {
            LinearLayout linearLayout = detailsBinding.includeLinearLayoutTrailer.linearLayoutSimple;

            if (currTrailers.size() <= 3) {
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            } else {
                linearLayout.setOrientation(LinearLayout.VERTICAL);
            }

            for (int i = 0; i < currTrailers.size(); i++) {

                final String url = currTrailers.get(i).BASE_URL + currTrailers.get(i).key;

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
                params.setMargins(10, 10, 10, 10);
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