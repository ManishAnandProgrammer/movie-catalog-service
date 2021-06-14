package com.example.service;

import com.example.dto.Movie;
import com.example.dto.Rating;
import com.example.models.CatalogItem;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class MovieService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieService.class);

    @Autowired
    private RestTemplate restTemplate;

    @CircuitBreaker(name = "movieInfo", fallbackMethod = "movieCatalogFallback")
    public Movie getMovie(Rating rating) {
        Movie movie = restTemplate.getForObject("http://movie-info-service/movie/" + rating.getMovieId(), Movie.class);
        return movie;
    }

    private Movie movieCatalogFallback(Rating rating, Throwable exception) {
        LOGGER.error("Exception in Fetching Movie Details:: ", exception);
        return new Movie("0", "no-name", "no-discription");
    }
}
