package com.example.tijo.movies_proj.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TIJO on 1/21/2018.
 */

public class Movie implements Parcelable{

    public String movieId;
    public String title;
    public String synopsis;
    public String img_url;
    public Double rating;
    public String releaseDate;
    public String imgUrlOriginal;

    public Movie(String movieId, String title, String synopsis, String img_url, Double rating, String releaseDate, String imgUrlOriginal) {
        this.movieId = movieId;
        this.title = title;
        this.synopsis = synopsis;
        this.img_url = img_url;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.imgUrlOriginal = imgUrlOriginal;
    }

    public String getImgUrlOriginal() {
        return imgUrlOriginal;
    }

    public String getTitle() {
        return title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getImg_url() {
        return img_url;
    }

    public Double getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getMovieId() {
        return movieId;
    }

    protected Movie(Parcel in) {
        movieId = in.readString();
        title = in.readString();
        synopsis = in.readString();
        img_url = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readDouble();
        }
        releaseDate = in.readString();
        imgUrlOriginal = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(movieId);
        parcel.writeString(title);
        parcel.writeString(synopsis);
        parcel.writeString(img_url);
        if (rating == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(rating);
        }
        parcel.writeString(releaseDate);
        parcel.writeString(imgUrlOriginal);
    }
}
