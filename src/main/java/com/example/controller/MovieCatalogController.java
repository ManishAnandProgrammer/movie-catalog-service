package com.example.controller;

import com.example.dto.Movie;
import com.example.dto.Rating;
import com.example.dto.UserRatings;
import com.example.models.CatalogItem;
import com.example.service.MovieService;
import com.example.service.RatingService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieCatalogController.class);

    private final RestTemplate restTemplate;
    private final WebClient.Builder webClientBuilder;
    private final MovieService movieService;
    private final RatingService ratingService;

    public MovieCatalogController(final RestTemplate restTemplate,
                                  final WebClient.Builder webClientBuilder,
                                  final MovieService movieService,
                                  final RatingService ratingService) {
        this.restTemplate = restTemplate;
        this.webClientBuilder = webClientBuilder;
        this.movieService = movieService;
        this.ratingService = ratingService;
    }

    @GetMapping("/{userId}")
    public List<CatalogItem> movieCatalogs(@PathVariable("userId") final String userId) {
        UserRatings ratings = ratingService.getUserRatings(userId);
        return ratings.getRatings().stream()
                        .map(rating -> {
                            Movie movie = movieService.getMovie(rating);
                            return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
                        }).collect(Collectors.toList());
    }

    @GetMapping("/webclient/{userId}")
    public List<CatalogItem> movieCatalogsWithWebClient(@PathVariable("userId") final String userId) {
        List<Rating> ratings = List.of(new Rating("a", 4), new Rating("b", 5));

        return ratings.stream()
                    .map(rating -> {
                        Movie movie = webClientBuilder.build()
                                .get()
                                .uri("http://localhost:8081/movie/" + rating.getMovieId())
                                .retrieve()
                                .bodyToMono(Movie.class)
                                .block();
                        return new CatalogItem(movie.getName(), movie.getId(), rating.getRating());
                    }).collect(Collectors.toList());
    }
}
