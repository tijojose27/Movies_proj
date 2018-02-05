package com.example.tijo.movies_proj;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tijo.movies_proj.data.Movie;
import com.example.tijo.movies_proj.databinding.ActivityDetailsBinding;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    //DATABINDING
    ActivityDetailsBinding detailsBinding;

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

        String rating = currMovie.getRating().toString()+" / 10";

        detailsBinding.textViewUserRating.setText(rating);
        detailsBinding.textViewSynopsis.setText(currMovie.getSynopsis());

        Picasso.with(this).load(currMovie.getImgUrlOriginal()).into(detailsBinding.imageViewDetails);

    }
}