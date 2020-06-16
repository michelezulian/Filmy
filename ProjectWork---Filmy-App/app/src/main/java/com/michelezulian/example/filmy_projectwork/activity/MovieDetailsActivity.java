package com.michelezulian.example.filmy_projectwork.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.michelezulian.example.filmy_projectwork.Api.TmdbService;
import com.michelezulian.example.filmy_projectwork.DBinternal.MovieProvider;
import com.michelezulian.example.filmy_projectwork.DBinternal.MovieTableHelper;
import com.michelezulian.example.filmy_projectwork.R;
import com.michelezulian.example.filmy_projectwork.models.MovieDetails;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailsActivity extends AppCompatActivity {
    public static String BASE_URL = "https://api.themoviedb.org";
    public static String API_KEY = "3270865785ff34dfcca05eec92d0fe45";
    public static String LANGUAGE = "it";
    ImageView backdrop_path;
    TextView textView_overview, textView_title, textView_popularity, textView_voteCount;
    String backdrop_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        getSupportActionBar().setTitle("Details");
        int movie_id = Integer.parseInt(getIntent().getStringExtra("Movie"));

        //binding layout element
        backdrop_path = findViewById(R.id.backdrop_path);
        textView_overview = findViewById(R.id.overview);
        textView_title = findViewById(R.id.titleDetail);
        textView_popularity = findViewById(R.id.textview_popolarità);
        textView_voteCount = findViewById(R.id.textview_voteCount);


        /*if(checkInternet()){
            Retrofit retrofit = buildRetrofit();
            TmdbService service = retrofit.create(TmdbService.class);
            callMovieDetails(service, movie_id);
        }
        else{*/
            Cursor vCursor = getContentResolver().query(MovieProvider.MOVIES_URI, null, "movie_id = " + movie_id, null, null);
            if (vCursor.getCount() >= 1) {
                while (vCursor.moveToNext()) {
                    textView_title.setText(vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.TITLE)));
                    backdrop_image = vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.BACKDROP_PATH));
                    textView_overview.setText(vCursor.getString(vCursor.getColumnIndex(MovieTableHelper.OVERVIEW)));
                }
            }


        Glide.with(getApplicationContext())
                .load("https://image.tmdb.org/t/p/w500/" + backdrop_image)
                .centerCrop()
                .placeholder(R.drawable.poster_path_ph)
                .into(backdrop_path);
    }

    public Retrofit buildRetrofit (){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }


    public Boolean checkInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        else
            return false;
    }


    public void callMovieDetails (TmdbService service, int id){
        Call<MovieDetails> call = service.getMovieDetails(id, API_KEY, LANGUAGE);
        call.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                Log.d("asdfa", response.body().toString());
                MovieDetails result = response.body();
                textView_popularity.setText(result.getPopularity().toString());
                textView_title.setText(result.getTitle());
                textView_overview.setText(result.getOverview());
                textView_voteCount.setText(result.getVoteCount());
                backdrop_image = result.getBackdropPath();
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                Log.d("callMovieDetails", t.getMessage());
            }
        });
    }
}
