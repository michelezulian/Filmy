package com.michelezulian.example.filmy_projectwork.Adapter;

import android.content.Context;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.michelezulian.example.filmy_projectwork.R;
import com.michelezulian.example.filmy_projectwork.models.CallMovieByTitleResponse;
import com.michelezulian.example.filmy_projectwork.models.MoviesResponse;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    //initialization of variables
    List<MoviesResponse.ResultsBean> listMovie = new ArrayList<>();   //List num: 1
    List<CallMovieByTitleResponse.Result> listSearchedMovie = new ArrayList<>();   //List num: 2
    Context context;
    ImyOnClickListener clickListener;
    int choosenList = 0;

    //interface for handling click of movie
    public interface ImyOnClickListener{
        void onMovieClick(int position);
        void onMovieLongClick(int position);
    }

    //constructor
    public MyAdapter(Context context, ImyOnClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
    }

    //handling changing of dataset
    public void setListMovie(List<MoviesResponse.ResultsBean> listMovie){
        this.listMovie = listMovie;
        choosenList = 1;
    }

    //handling changing of dataset
    public void setListSearchedMovie(List<CallMovieByTitleResponse.Result> listSearchedMovie){
        this.listSearchedMovie = listSearchedMovie;
        choosenList = 2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.film_row, parent, false);
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (choosenList) {
            case 1:
                holder.filmTitle.setText(listMovie.get(position).getTitle());
                Glide.with(context)
                        .load("https://image.tmdb.org/t/p/w500/" + listMovie.get(position)
                                .getPoster_path()).placeholder(R.drawable.poster_path_ph)
                        .fitCenter()
                        .centerCrop()
                        .into(holder.poster_path);
                break;

            case 2:
                holder.filmTitle.setText(listSearchedMovie.get(position).getTitle());
                Glide.with(context)
                        .load("https://image.tmdb.org/t/p/w500/" + listSearchedMovie.get(position)
                                .getPosterPath()).placeholder(R.drawable.poster_path_ph)
                        .fitCenter()
                        .centerCrop()
                        .into(holder.poster_path);
                break;
        }

    }

    @Override
    public int getItemCount() {
        switch (choosenList){
            case 1:
                return listMovie.size();

            case 2:
                return listSearchedMovie.size();

            default:
                return 0;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView filmTitle;
        ImageView poster_path;
        ImyOnClickListener imyOnClickListener;

        public ViewHolder(@NonNull View itemView, ImyOnClickListener imyOnClickListener) {
            super(itemView);
            filmTitle = itemView.findViewById(R.id.filmTitle);
            poster_path = itemView.findViewById(R.id.poster_path);
            this.imyOnClickListener = imyOnClickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            imyOnClickListener.onMovieClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            imyOnClickListener.onMovieLongClick(getAdapterPosition());
            return true;
        }
    }
}
