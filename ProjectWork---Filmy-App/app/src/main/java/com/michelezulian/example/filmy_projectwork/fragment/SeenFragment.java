package com.michelezulian.example.filmy_projectwork.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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

public class SeenFragment extends Fragment implements MyAdapter.ImyOnClickListener{

    private static final String TAG_SEEN ="SeenFragment" ;
    MyAdapter myAdapter;
    RecyclerView recyclerView;
    ArrayList<MoviesResponse.ResultsBean> listOfMovie;
    MoviesResponse.ResultsBean movie;
    TextView labelSeen;
    ImageView imagePopcorn;


    //setting that fragment has a menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("Already Seen");
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.ricerca);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Cursor vCursor = getContext().getContentResolver().query(MovieProvider.MOVIES_URI, null, "title LIKE '%" + newText + "%' and seen = 1", null, null);
                int movieCount = vCursor.getCount();
                if(movieCount == 0){
                    Toast.makeText(getContext(), "nessun film trovato", Toast.LENGTH_SHORT).show();
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
                    myAdapter = new MyAdapter(getContext(), SeenFragment.this);
                    myAdapter.setListMovie(listOfMovie);
                    recyclerView.setAdapter(myAdapter);
                    setGridNumber();
                    myAdapter.notifyDataSetChanged();
                }

                return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seen, container, false);
        Context context = getContext();
        if(context == null) return view;
        if(savedInstanceState == null) {
            recyclerView = view.findViewById(R.id.filmList_seen);
            labelSeen = view.findViewById(R.id.labelSeen);
            imagePopcorn = view.findViewById(R.id.imagePopcorn);
            listOfMovie = new ArrayList<>();
        }
        Cursor vCursor = context.getContentResolver().query(MovieProvider.MOVIES_URI, null, "seen = 1", null, null);
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
            myAdapter = new MyAdapter(getContext().getApplicationContext(), this);
            myAdapter.setListMovie(listOfMovie);
            recyclerView.setAdapter(myAdapter);
            setGridNumber();
            myAdapter.notifyDataSetChanged();
        }


        return view;
    }

    @Override
    public void onMovieClick(int position) {
        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
        intent.putExtra("Movie", String.valueOf(listOfMovie.get(position).getId()));
        startActivity(intent);
    }

    @Override
    public void onMovieLongClick(int position) {
        WatchedDialog vDialog = new WatchedDialog("Watched", "Do you want to remove this movie from the list?", position, TAG_SEEN);
        vDialog.show(getActivity().getSupportFragmentManager(), null);
    }

    public void onResponseDialog(boolean aResponse, long aId) {
        if (aResponse) {

            int movie_id = listOfMovie.get((int)aId).getId();

            ContentValues vValues = new ContentValues();

            vValues.put(MovieTableHelper.SEEN, 0);

            getContext().getContentResolver().update(MovieProvider.MOVIES_URI, vValues, "movie_id = " + movie_id, null);

        }
        listOfMovie.clear();
        Cursor vCursor = getContext().getContentResolver().query(MovieProvider.MOVIES_URI, null, "seen = 1", null, null);
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
        myAdapter = new MyAdapter(getContext(), this);
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
