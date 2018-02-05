package com.example.tijo.movies_proj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tijo.movies_proj.data.Movie;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    ImageView ivMovie;
    TextView tvTitle, tvRelaseDate, tvSynopsis, tvUserRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //SETTING THE UP BUTTON
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //GETTING THE PARCELABLE'S
        Intent intent = getIntent();
        Movie currMovie = intent.getParcelableExtra("Movie Item");

        //ASSIGNING THE VIEWS PROGRAMMATICALLY
        ivMovie = findViewById(R.id.image_view_details);
        tvTitle = findViewById(R.id.text_view_title);
        tvRelaseDate = findViewById(R.id.text_view_release_date);
        tvSynopsis = findViewById(R.id.text_view_synopsis);
        tvUserRating = findViewById(R.id.text_view_user_rating);


        Picasso.with(this).load(currMovie.getImgUrlOriginal()).into(ivMovie);

        //SETTING VIEWS
        tvTitle.setText(currMovie.getTitle());
        tvRelaseDate.setText(currMovie.getReleaseDate());
        tvSynopsis.setText(currMovie.getSynopsis());

        String rating = currMovie.getRating().toString()+" / 10";
        tvUserRating.setText(rating);
//        tvUserRating.setText(currMovie.getRating().toString());

    }
}