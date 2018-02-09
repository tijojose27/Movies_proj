package com.example.tijo.movies_proj;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tijo.movies_proj.Adapters.MoviesAdapter;
import com.example.tijo.movies_proj.Database.MoviesContract;
import com.example.tijo.movies_proj.Utility.CheckConn;
import com.example.tijo.movies_proj.Utility.GetMovies;
import com.example.tijo.movies_proj.data.Movie;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //INSERT YOUR API KEY HERE
    public final String APIKEY = "api_key=";

    public final String IMDB_BASE_URL = "http://api.themoviedb.org/3/movie/";

    public String order = "";

    public final String FAV_MOVIE ="favoriate";

    ArrayList<Movie> myMovies;

    RecyclerView recyclerView;

    ImageView ivNoInternet;

    TextView tvNoInternet;

    private Context context;

    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myMovies = new ArrayList<>();
//        order = "popular?";
        order = "popular?";

        //ASSIGNING ID
        recyclerView = findViewById(R.id.activity_details);
        ivNoInternet = findViewById(R.id.image_view_no_internet);
        tvNoInternet = findViewById(R.id.text_view_no_internet);

        context = this;

        updateMovie();

    }

    //INFLATING MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //HANDLING THE MENU CLICKS
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedSort = item.getItemId();
        if (selectedSort == R.id.menu_sort_top_rated) {
            //CHANGING THE ORDER TO TOP RATED
            order = "top_rated?";
        } else if(selectedSort == R.id.menu_sort_most_popular){
            //CHANGING THE ORDER TO HIGHEST RATED
            order = "popular?";
        }
        else{
            order=FAV_MOVIE;
        }

        updateMovie();

        return super.onOptionsItemSelected(item);
    }


    private void updateMovie() {
        if(order.equals(FAV_MOVIE)){
            toggleNoInternetBanner(true);
            getfavoriateMovies();
        }
        else if (CheckConn.isConnected(context)) {
            toggleNoInternetBanner(true);
            getMovies();
        } else {
            toggleNoInternetBanner(false);
        }
    }

    public void toggleNoInternetBanner(boolean conn){
        if(!conn){
            recyclerView.setVisibility(View.INVISIBLE);
            ivNoInternet.setVisibility(View.VISIBLE);
            tvNoInternet.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            ivNoInternet.setVisibility(View.INVISIBLE);
            tvNoInternet.setVisibility(View.INVISIBLE);
        }
    }


    public void getMovies() {

        myMovies.clear();

        if(order.equals(FAV_MOVIE)){
            getfavoriateMovies();
        }else {

            //CHECKING NETWORK STATE
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(IMDB_BASE_URL + order + APIKEY)
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
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myMovies = GetMovies.movieToArray(json);
                                updateUI();
                            }
                        });
                    }
                }
            });
        }
    }

    private void getfavoriateMovies() {

        myMovies.clear();

        cursor = getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                null, null, null, null);

        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                String movieId = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID));
                String movieTitle = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE));
                String movieSynopsis = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_SYNOPSIS));
                String movieImgUrl = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_IMAGE_URL));
                Double movieRating = cursor.getDouble(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RATING));
                String movieReleaseDate = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE));
                String movieImgOriginal = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_IMAGE_URL_ORIGINAL));
                myMovies.add(new Movie(movieId, movieTitle, movieSynopsis, movieImgUrl, movieRating, movieReleaseDate, movieImgOriginal));
                cursor.moveToNext();
            }
        }

        cursor.close();

        updateUI();
    }

    //UPDATING UI
    public void updateUI() {

        //CREATING RECYCLER VIEW OBJECT
        recyclerView.setHasFixedSize(true);

        //SETTING GRIDA LAYOUT MANAGER ON THE RECYCLER VIEW
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        //SETTING THE ADAPTER AND CONNECTING IT TO THE MOVIES ARRAY
        MoviesAdapter adapter = new MoviesAdapter(this, myMovies);

        //SETTING THE ADAPTER TO THE RECYCLERVIEW
        recyclerView.setAdapter(adapter);
    }
}
