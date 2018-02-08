package com.example.tijo.movies_proj.Utility;

import com.example.tijo.movies_proj.data.Movie;
import com.example.tijo.movies_proj.data.Reviews;
import com.example.tijo.movies_proj.data.Trailers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by tijoj on 2/5/2018.
 */

public class GetMovies {

    final static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    final static String IMAGE_THUMBNAIL = "w185/";

    final static String IMAGE_ORIGINAL = "w780/";

    public static ArrayList<Movie> currMovie;

    public static ArrayList<Reviews> currReview;

    public static ArrayList<Trailers> currTrailer;

    //METHOD TO CONVERT JSON TO ARRAY
    public static ArrayList<Movie> movieToArray(String json) {

        currMovie = new ArrayList<>();

        JSONObject reader = null;
        try {
            reader = new JSONObject(json);
            JSONArray results = reader.getJSONArray("results");
            //String result = results.toString();

            //CONVERTING JSON AND POPULATING ARRAYLIST
            for (int i = 0; i < results.length(); i++) {
                JSONObject currObj = results.getJSONObject(i);

                String id = currObj.getString("id");
                String title = currObj.getString("title");
                String summary = currObj.getString("overview");
                String release = currObj.getString("release_date");

                String releaseYear = "";
                SimpleDateFormat apiDate = new SimpleDateFormat("yyyy-mm-dd");
                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

                try {
                    releaseYear = yearFormat.format(apiDate.parse(release));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Double rating = currObj.getDouble("vote_average");

                String url = currObj.getString("poster_path");

                String imageThumbURL = IMAGE_BASE_URL + IMAGE_THUMBNAIL + url;

                String imageOrgURL = IMAGE_BASE_URL + IMAGE_ORIGINAL + url;

                currMovie.add(new Movie(id, title, summary, imageThumbURL, rating, releaseYear, imageOrgURL));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return currMovie;
    }

    public static ArrayList<Reviews> reviewsToArray(String json){
        currReview = new ArrayList<>();

        JSONObject reader = null;
        try {
            reader = new JSONObject(json);
            JSONArray results = reader.getJSONArray("results");
            //String result = results.toString();

            //CONVERTING JSON AND POPULATING ARRAYLIST
            for (int i = 0; i < results.length(); i++) {
                JSONObject currObj = results.getJSONObject(i);

                String author = currObj.getString("author");
                String content = currObj.getString("content");

                currReview.add(new Reviews(author, content));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return currReview;
    }


    public static ArrayList<Trailers> tailersToArray(String json){
        currTrailer = new ArrayList<>();
        JSONObject reader = null;
        try {
            reader = new JSONObject(json);
            JSONArray results = reader.getJSONArray("results");
            //String result = results.toString();

            //CONVERTING JSON AND POPULATING ARRAYLIST
            for (int i = 0; i < results.length(); i++) {
                JSONObject currObj = results.getJSONObject(i);

                String key = currObj.getString("key");
                String type = currObj.getString("type");

                currTrailer.add(new Trailers(type, key));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currTrailer;
    }
}
