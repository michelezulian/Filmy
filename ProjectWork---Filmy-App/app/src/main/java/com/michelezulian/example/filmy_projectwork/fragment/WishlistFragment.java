package com.michelezulian.example.filmy_projectwork.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.michelezulian.example.filmy_projectwork.Adapter.MyAdapter;
import com.michelezulian.example.filmy_projectwork.DBinternal.MovieProvider;
import com.michelezulian.example.filmy_projectwork.DBinternal.MovieTableHelper;
import com.michelezulian.example.filmy_projectwork.R;
import com.michelezulian.example.filmy_projectwork.activity.MovieDetailsActivity;
import com.michelezulian.example.filmy_projectwork.dialog.WatchedDialog;
import com.michelezulian.example.filmy_projectwork.models.MoviesResponse;

import java.util.ArrayList;

public class WishlistFragment extends Fragment  implements MyAdapter.ImyOnClickListener{


    private static final String TAG_WISHLIST ="WishlistFragment" ;
    private static final String TAG_BUNDLE ="bundle" ;
    public int orientation = 2;
    MyAdapter myAdapter;
    RecyclerView recyclerView;
    ArrayList<MoviesResponse.ResultsBean> listOfMovie;
    Context context;
    MoviesResponse.ResultsBean movie;
    TextView labelSeen;
    ImageView imagePopcorn;



    //setting that fragment has a menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("Already Seen");
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            orientation = bundle.getInt(TAG_BUNDLE);
        }
    }


  /*  //inflating the menu and handling the searched query
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.ricerca);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
              *//*  Retrofit retrofit = buildRetrofit();
                TmdbService service = retrofit.create(TmdbService.class);
                callMovieByTitle(service, page, query);
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (!recyclerView.canScrollVertically(1)) {
                            page++;
                            callMovieByTitle(service, page, query);
                        }
                    }
                });*//*
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Cursor vCursor = getActivity().getContentResolver().query(MovieProvider.MOVIES_URI, null, "title LIKE '%" + newText + "%'", null, null);
                int movieCount = vCursor.getCount();
                if(movieCount == 0){
                    Toast.makeText(getActivity(), "nessun film trovato", Toast.LENGTH_SHORT).show();
                }
                else{
                    listOfMovie.clear();
                    while (vCursor.moveToNext()) {
                        movie = new MoviesResponse.ResultsBean();
                        movie.setId(vCursor.getInt(vCursor.getColumnIndex(MovieTableHelper.MOVIE_ID)));
                        movie.setTitle(vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.TITLE)));
                        movie.setPoster_path(vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.POSTER_PATH)));
                        listOfMovie.add(movie);
                    }
                    myAdapter = new MyAdapter(getActivity(), SeenFragment.this);
                    myAdapter.setListMovie(listOfMovie);
                    recyclerView.setAdapter(myAdapter);
                    setGridNumber();
                    myAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seen, container, false);

        recyclerView = view.findViewById(R.id.filmList_seen);
        labelSeen= view.findViewById(R.id.labelSeen);
        imagePopcorn = view.findViewById(R.id.imagePopcorn);
        listOfMovie = new ArrayList<>();
        Cursor vCursor = getActivity().getContentResolver().query(MovieProvider.MOVIES_URI, null, "wishlist = 1", null, null);
        if(vCursor.getCount() == 0){
            recyclerView.setVisibility(View.GONE);
            labelSeen.setVisibility(View.VISIBLE);
            imagePopcorn.setVisibility(View.VISIBLE);
        }
        else {
            while (vCursor.moveToNext()) {
                movie = new MoviesResponse.ResultsBean();
                movie.setId(vCursor.getInt(vCursor.getColumnIndex(MovieTableHelper.MOVIE_ID)));
                movie.setTitle(vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.TITLE)));
                movie.setPoster_path(vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.POSTER_PATH)));
                listOfMovie.add(movie);
            }
            myAdapter = new MyAdapter(getActivity().getApplicationContext(), this);
            myAdapter.setListMovie(listOfMovie);
            recyclerView.setAdapter(myAdapter);
            setGridNumber();
            myAdapter.notifyDataSetChanged();
        }


        return view;
    }

    @Override
    public void onMovieClick(int position) {
        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra("Movie", String.valueOf(listOfMovie.get(position).getId()));
        startActivity(intent);
    }

    @Override
    public void onMovieLongClick(int position) {
        WatchedDialog vDialog = new WatchedDialog("Watched", "Do you want to remove this movie from the list?", position, TAG_WISHLIST);
        vDialog.show(getActivity().getSupportFragmentManager(), null);
    }

    public void onResponseDialog(boolean aResponse, long aId) {
        if (aResponse) {

            int movie_id = listOfMovie.get((int)aId).getId();

            ContentValues vValues = new ContentValues();

            vValues.put(MovieTableHelper.WISHLIST, 0);

            getActivity().getContentResolver().update(MovieProvider.MOVIES_URI, vValues, "movie_id = " + movie_id, null);

        }
        listOfMovie.clear();
        Cursor vCursor = getActivity().getContentResolver().query(MovieProvider.MOVIES_URI, null, "wishlist = 1", null, null);
        if(vCursor.getCount() == 0){
            recyclerView.setVisibility(View.GONE);
            labelSeen.setVisibility(View.VISIBLE);
            imagePopcorn.setVisibility(View.VISIBLE);
        }
        else {
            while (vCursor.moveToNext()) {
                movie = new MoviesResponse.ResultsBean();
                movie.setId(vCursor.getInt(vCursor.getColumnIndex(MovieTableHelper.MOVIE_ID)));
                movie.setTitle(vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.TITLE)));
                movie.setPoster_path(vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.POSTER_PATH)));
                listOfMovie.add(movie);
            }
        }
        myAdapter = new MyAdapter(getActivity().getApplicationContext(), this);
        myAdapter.setListMovie(listOfMovie);
        recyclerView.setAdapter(myAdapter);
        setGridNumber();
        myAdapter.notifyDataSetChanged();
    }

    public void setGridNumber(){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //set layout manager
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        }
        else{
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
    }
}
