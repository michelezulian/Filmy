package com.michelezulian.example.filmy_projectwork.Api;

import com.michelezulian.example.filmy_projectwork.models.CallMovieByTitleResponse;
import com.michelezulian.example.filmy_projectwork.models.MovieDetails;
import com.michelezulian.example.filmy_projectwork.models.MoviesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbService {
    @GET("3/movie/{category}")
    Call<MoviesResponse> getMovies(
            @Path("category") String category,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("3/search/movie")
    Call<CallMovieByTitleResponse> getMovieByTitle(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("language") String language,
            @Query("page") int page
    );

    @GET("3/movie/{id}")
    Call<MovieDetails> getMovieDetails(
            @Path("id") int id,
            @Query("language") String language,
            @Query("api_key") String apiKey
    );
}
