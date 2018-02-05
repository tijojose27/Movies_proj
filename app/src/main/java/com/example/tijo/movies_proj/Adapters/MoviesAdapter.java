package com.example.tijo.movies_proj.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tijo.movies_proj.DetailsActivity;
import com.example.tijo.movies_proj.R;
import com.example.tijo.movies_proj.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by TIJO on 1/21/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private List<Movie> movieList;
    private Context mContext;


    public MoviesAdapter(Context context, List<Movie> movieList) {
        mContext = context;
        this.movieList = movieList;
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.movies_cards, null);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {

        final Movie movie = movieList.get(position);

        holder.textViewTitle.setText(movie.getTitle());

        String imgURL = movie.getImg_url();

        //LOADING THE COMPRESSED IMAGE
        Picasso.with(mContext).load(imgURL).into(holder.imageView);

        //HANDLING THE CLICK EVENT
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailsActivity.class);
                intent.putExtra("Movie Item", movie);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return movieList.size();
    }

    //CREATING VIEW HOLDER
    class MoviesViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textViewTitle;


        public MoviesViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view_movie_card);
            textViewTitle = itemView.findViewById(R.id.text_view_movie_card);
        }
    }
}
