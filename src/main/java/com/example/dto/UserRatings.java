package com.example.dto;

import java.util.List;

public class UserRatings {
    private List<Rating> ratings;

    public UserRatings() {}

    public UserRatings(final List<Rating> ratings) {
        this.ratings = ratings;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }
}
