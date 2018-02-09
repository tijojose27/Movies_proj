package com.example.tijo.movies_proj.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tijoj on 2/5/2018.
 */

// CREATING THE REVIEW CLASS AND IMPLEMENTING PARCELABLES
public class Reviews implements Parcelable {

    public String author;
    public String content;


    public Reviews(String author, String content) {
        this.author = author;
        this.content = content;
    }

    protected Reviews(Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    public static final Creator<Reviews> CREATOR = new Creator<Reviews>() {
        @Override
        public Reviews createFromParcel(Parcel in) {
            return new Reviews(in);
        }

        @Override
        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(content);
    }
}
