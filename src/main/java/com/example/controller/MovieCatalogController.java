package com.example.controller;

import com.example.dto.Movie;
import com.example.dto.Rating;
import com.example.dto.UserRatings;
import com.example.models.CatalogItem;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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

    public MovieCatalogController(final RestTemplate restTemplate,
                                  final WebClient.Builder webClientBuilder) {
        this.restTemplate = restTemplate;
        this.webClientBuilder = webClientBuilder;
    }

    @GetMapping("/{userId}")
    @CircuitBreaker(name = "my", fallbackMethod = "movieCatalogFallback")
    public List<CatalogItem> movieCatalogs(@PathVariable("userId") final String userId) {
        UserRatings ratings = restTemplate.getForObject("http://ratings-data-service/ratingData/user/" + userId, UserRatings.class);
        return
                ratings.getRatings().stream()
                        .map(rating -> {
                            Movie movie = restTemplate.getForObject("http://movie-info-service/movie/" + rating.getMovieId(), Movie.class);
                            return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
                        }).collect(Collectors.toList());
    }

    public List<CatalogItem> movieCatalogFallback(String userId, Throwable exception) {
        LOGGER.error("Exception in Fetching Movie Details:: ", exception);
        return List.of(new CatalogItem("No Movie Name", "No Discription", 0));
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
