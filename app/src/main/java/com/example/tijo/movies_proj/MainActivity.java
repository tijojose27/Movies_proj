package com.example.tijo.movies_proj;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tijo.movies_proj.Adapters.MoviesAdapter;
import com.example.tijo.movies_proj.Utility.GetMovies;
import com.example.tijo.movies_proj.data.Movie;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //INSERT YOUR API KEY HERE
    final String APIKEY = "api_key=8628eac7c7875b9b1a42093545772f7b";

    final String IMDB_BASE_URL = "http://api.themoviedb.org/3/movie/";

    public String order = "";

    public String jsonMovie = "";

    ArrayList<Movie> myMovies;

    RecyclerView recyclerView;

    ImageView ivNoInternet;

    TextView tvNoInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myMovies = new ArrayList<>();
        order = "popular?";

        //ASSIGNING ID
        recyclerView = findViewById(R.id.activity_details);
        ivNoInternet = findViewById(R.id.image_view_no_internet);
        tvNoInternet = findViewById(R.id.text_view_no_internet);

        updateMovie();

    }

    private void updateMovie() {
        if (isConnected()) {
            myMovies.clear();
            getMovies();
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            ivNoInternet.setVisibility(View.VISIBLE);
            tvNoInternet.setVisibility(View.VISIBLE);
        }
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
        } else {
            //CHANGING THE ORDER TO HIGHEST RATED
            order = "popular?";
        }

        updateMovie();

        return super.onOptionsItemSelected(item);
    }

    public String getMovies() {

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
                            jsonMovie = json + "";
                            myMovies = GetMovies.jsonToArray(jsonMovie);
                            updateUI();
                        }
                    });
                }
            }
        });
        return jsonMovie;
    }

    //METHOD TO GET MOVIES FROM URL
//    public void getMovies() {
//
//        //CHECKING NETWORK STATE
//
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url(IMDB_BASE_URL + order + APIKEY)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                if (!response.isSuccessful()) {
//                    throw new IOException("Unexpected code " + response);
//                } else {
//                    final String json = response.body().string();
//                    MainActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            jsonMovie = json + "";
//                            myMovies = GetMovies.jsonToArray(jsonMovie);
//                            updateUI();
//                        }
//                    });
//                }
//            }
//        });
//    }

    //METHOD TO CHECK NETWORK CONNECTIVITY
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
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
