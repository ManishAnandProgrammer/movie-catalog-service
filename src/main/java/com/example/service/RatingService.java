package com.example.service;

import com.example.dto.Rating;
import com.example.dto.UserRatings;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class RatingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RatingService.class);

    @Autowired
    private RestTemplate restTemplate;

    @CircuitBreaker(name="ratings", fallbackMethod = "userRatingFallBack")
    public UserRatings getUserRatings(String userId) {
        UserRatings ratings = restTemplate.getForObject("http://ratings-data-service/ratingData/user/" + userId, UserRatings.class);
        return ratings;
    }

    private UserRatings userRatingFallBack(String userId, Throwable throwable) {
        LOGGER.error("Exception in Fetching User's Ratings:: ", throwable);
        UserRatings userRatings = new UserRatings();
        userRatings.setRatings(List.of(new Rating("0", 0)));
        return userRatings;
    }
}
