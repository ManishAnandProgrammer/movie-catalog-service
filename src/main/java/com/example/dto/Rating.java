package com.example.dto;

public class Rating {
    private String movieId;
    private int rating;

    public Rating() {}

    public Rating(final String movieId, final int rating) {
        this.movieId = movieId;
        this.rating = rating;
    }

    public String getMovieId() {
        return movieId;
    }

    public int getRating() {
        return rating;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
