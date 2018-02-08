package com.example.tijo.movies_proj.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tijoj on 2/8/2018.
 */

public class Trailers implements Parcelable{

    public String type;
    public String key;
    public String BASE_URL = "https://youtu.be/";

    public Trailers(String type, String key) {
        this.type = type;
        this.key = key;
    }

    protected Trailers(Parcel in) {
        type = in.readString();
        key = in.readString();
        BASE_URL = in.readString();
    }

    public static final Creator<Trailers> CREATOR = new Creator<Trailers>() {
        @Override
        public Trailers createFromParcel(Parcel in) {
            return new Trailers(in);
        }

        @Override
        public Trailers[] newArray(int size) {
            return new Trailers[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeString(key);
        parcel.writeString(BASE_URL);
    }
}
