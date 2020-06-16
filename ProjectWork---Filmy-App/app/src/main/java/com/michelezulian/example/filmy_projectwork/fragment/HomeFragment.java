package com.michelezulian.example.filmy_projectwork.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.michelezulian.example.filmy_projectwork.Adapter.MyAdapter;
import com.michelezulian.example.filmy_projectwork.Api.TmdbService;
import com.michelezulian.example.filmy_projectwork.DBinternal.MovieProvider;
import com.michelezulian.example.filmy_projectwork.DBinternal.MovieTableHelper;
import com.michelezulian.example.filmy_projectwork.R;
import com.michelezulian.example.filmy_projectwork.activity.MovieDetailsActivity;
import com.michelezulian.example.filmy_projectwork.dialog.WatchedDialog;
import com.michelezulian.example.filmy_projectwork.models.CallMovieByTitleResponse;
import com.michelezulian.example.filmy_projectwork.models.MoviesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment implements MyAdapter.ImyOnClickListener{

    //CONSTANTS
    private static final String TAG_HOME ="HomeFragment" ;
    public static String BASE_URL = "https://api.themoviedb.org";
    public int page = 1;
    public int pageSearched = 1;
    public int orientation = 2;
    public static String API_KEY = "3270865785ff34dfcca05eec92d0fe45";
    public static String LANGUAGE = "it";
    public String mCategory;

    //Variables
    MyAdapter myAdapter;
    List<MoviesResponse.ResultsBean> listOfMovie;
    MoviesResponse.ResultsBean movie;

    List<CallMovieByTitleResponse.Result> listOfSearchedMovies;
    CallMovieByTitleResponse.Result searchedMovie;

    //Layout elements
    RecyclerView recyclerView;




    //setting that fragment has a menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("popolari");
    }

    //inflating the menu and handling the searched query
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater){
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item_popular = menu.findItem(R.id.popular_popUp);
        item_popular.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mCategory = "popular";
                loadDefaultList(mCategory);
                getActivity().setTitle("popolari");
                return false;
            }
        });

        MenuItem item_upComing = menu.findItem(R.id.upcoming_popUp);
        item_upComing.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mCategory = "upcoming";
                loadDefaultList(mCategory);
                getActivity().setTitle("In arrivo");
                return false;
            }
        });
        MenuItem item_playingNow= menu.findItem(R.id.playingNow_popUp);
        item_playingNow.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mCategory = "now_playing";
                loadDefaultList(mCategory);
                getActivity().setTitle("In onda");
                return false;
            }
        });
        MenuItem item_TopRated= menu.findItem(R.id.topRated_popUp);
        item_TopRated.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mCategory = "top_rated";
                loadDefaultList(mCategory);
                getActivity().setTitle("Top Rated");
                return false;
            }
        });
        MenuItem item = menu.findItem(R.id.ricerca);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listOfMovie.clear();
                listOfSearchedMovies.clear();
                if(newText.isEmpty()){
                    loadDefaultList(mCategory);
                }
                else {
                    if (checkInternet()) {
                        Retrofit retrofit = buildRetrofit();
                        TmdbService service = retrofit.create(TmdbService.class);
                        callMovieByTitle(service, pageSearched, newText);
                        recyclerView.clearOnScrollListeners();
                        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (!recyclerView.canScrollVertically(1)) {
                                    pageSearched++;
                                    callMovieByTitle(service, pageSearched, newText);
                                }
                            }
                        });
                    }
                    else{
                        Cursor vCursor = getContext().getContentResolver().query(MovieProvider.MOVIES_URI, null, "title LIKE '%" + newText + "%'", null, null);
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
                            myAdapter = new MyAdapter(getContext(), HomeFragment.this);
                            myAdapter.setListMovie(listOfMovie);
                            recyclerView.setAdapter(myAdapter);
                            setGridNumber();
                            myAdapter.notifyDataSetChanged();
                        }

                    }
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void loadDefaultList(String category){
        //call API
        if (checkInternet()) {
            //instantiate retrofit
            Toast.makeText(getContext(), category, Toast.LENGTH_SHORT).show();
            Retrofit retrofit = buildRetrofit();
            TmdbService service = retrofit.create(TmdbService.class);
            callPopularMovie(service, 1 , category);
            recyclerView.clearOnScrollListeners();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (!recyclerView.canScrollVertically(1)) {
                        page++;
                        callPopularMovie(service, page, category);
                    }
                }
            });
        }
        else{
            Cursor vCursor = getContext().getContentResolver().query(MovieProvider.MOVIES_URI, null, null, null, null);
            int movieCount = vCursor.getCount();
            if(movieCount == 0){
                Toast.makeText(getContext(), "Devi accendere la connessione internet per la prima volta", Toast.LENGTH_SHORT).show();
            }
            else{
                while (vCursor.moveToNext()) {
                    movie = new MoviesResponse.ResultsBean();
                    movie.setId(vCursor.getInt(vCursor.getColumnIndex(MovieTableHelper.MOVIE_ID)));
                    movie.setTitle(vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.TITLE)));
                    movie.setPoster_path(vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.POSTER_PATH)));
                    listOfMovie.add(movie);
                }
                myAdapter.setListMovie(listOfMovie);
                myAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("category", mCategory);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        if(savedInstanceState == null) {
            //variable initialization
            listOfMovie = new ArrayList<>();
            listOfSearchedMovies = new ArrayList<>();
            myAdapter = new MyAdapter(getContext(), this);
            mCategory = "popular";
            //binding layout
            recyclerView = view.findViewById(R.id.filmList);
            //setting adapters
            recyclerView.setAdapter(myAdapter);
        }
        else{
            mCategory = savedInstanceState.getString("category");
        }
        loadDefaultList(mCategory);
        setGridNumber();
        return view;
    }

    //checking internet
    public Boolean checkInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        else
            return false;
    }

    //retrofit instantiation
    public Retrofit buildRetrofit (){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    //API return popular movies
    public void callPopularMovie (TmdbService service, int page, String category){
        Call<MoviesResponse> call = service.getMovies(category, API_KEY, LANGUAGE, page);
        call.enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                Context context = getContext();
                if(context == null) return;
                if(page==1){
                    listOfMovie.clear();
                }
                MoviesResponse results = response.body();
                listOfMovie.addAll(results.getResults());
                myAdapter.setListMovie(listOfMovie);
                myAdapter.notifyDataSetChanged();
                for(int i = 0; i < listOfMovie.size(); i++) {
                    Cursor vCursor = context.getContentResolver().query(MovieProvider.MOVIES_URI, null, "movie_id = " + listOfMovie.get(i).getId(), null, null);
                    if(vCursor.getCount() == 0) {
                        ContentValues vValues = new ContentValues();
                        vValues.put(MovieTableHelper.TITLE, listOfMovie.get(i).getTitle());
                        vValues.put(MovieTableHelper.OVERVIEW, listOfMovie.get(i).getOverview());
                        vValues.put(MovieTableHelper.MOVIE_ID, listOfMovie.get(i).getId());
                        vValues.put(MovieTableHelper.SEEN, 0);
                        vValues.put(MovieTableHelper.POSTER_PATH, listOfMovie.get(i).getPoster_path());
                        vValues.put(MovieTableHelper.BACKDROP_PATH, listOfMovie.get(i).getBackdrop_path());
                        Uri vResultUri = context.getContentResolver().insert(MovieProvider.MOVIES_URI, vValues);
                    }
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                Log.d("afd", t.getMessage());
            }
        });

    }

    //API return movie by title
    public void callMovieByTitle (TmdbService service, int page, String query){
        Call<CallMovieByTitleResponse> call = service.getMovieByTitle( API_KEY, query, LANGUAGE, page);
        call.enqueue(new Callback<CallMovieByTitleResponse>() {
            @Override
            public void onResponse(Call<CallMovieByTitleResponse> call, Response<CallMovieByTitleResponse> response) {
                listOfMovie.clear();
                CallMovieByTitleResponse results = response.body();
                listOfSearchedMovies.addAll(results.getResults());
                myAdapter.setListSearchedMovie(listOfSearchedMovies);
                myAdapter.notifyDataSetChanged();
                for(int i = 0; i < listOfSearchedMovies.size(); i++) {
                    Cursor vCursor = getContext().getContentResolver().query(MovieProvider.MOVIES_URI, null, "movie_id = " + listOfSearchedMovies.get(i).getId(), null, null);
                    if(vCursor.getCount() == 0) {
                        ContentValues vValues = new ContentValues();
                        vValues.put(MovieTableHelper.TITLE, listOfSearchedMovies.get(i).getTitle());
                        vValues.put(MovieTableHelper.OVERVIEW, listOfSearchedMovies.get(i).getOverview());
                        vValues.put(MovieTableHelper.MOVIE_ID, listOfSearchedMovies.get(i).getId());
                        vValues.put(MovieTableHelper.SEEN, 0);
                        vValues.put(MovieTableHelper.POSTER_PATH, listOfSearchedMovies.get(i).getPosterPath());
                        vValues.put(MovieTableHelper.BACKDROP_PATH, listOfSearchedMovies.get(i).getBackdropPath());
                        Uri vResultUri = getContext().getContentResolver().insert(MovieProvider.MOVIES_URI, vValues);
                    }
                }
            }

            @Override
            public void onFailure(Call<CallMovieByTitleResponse> call, Throwable t) {
                Log.d("ciao", t.getMessage());
            }
        });

    }

    //implemented onMovieClick from adapter
    @Override
    public void onMovieClick(int position) {
        Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
        intent.putExtra("Movie", String.valueOf(listOfMovie.get(position).getId()));
        startActivity(intent);
    }

    //implemented onMovieLongClick from adapter
    @Override
    public void onMovieLongClick(int position) {
        WatchedDialog vDialog = new WatchedDialog("Watched", "Have you already seen this movie?", position, TAG_HOME);
        vDialog.show(getActivity().getSupportFragmentManager(), null);
    }

    //onResponse implemented
    public void onResponseDialog(boolean aResponse, long aId) {
        if (aResponse) {
            int movie_id = listOfMovie.get((int)aId).getId();
            ContentValues vValues = new ContentValues();
            vValues.put(MovieTableHelper.SEEN, 1);
            int vUpdatedRows = getContext().getContentResolver().update(MovieProvider.MOVIES_URI, vValues,
                    "movie_id = " + movie_id, null);
        }
    }

    //setting the number of film in a row of the recyclerview
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
