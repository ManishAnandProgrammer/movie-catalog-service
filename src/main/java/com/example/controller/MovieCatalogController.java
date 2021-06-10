package com.example.controller;

import com.example.models.CatalogItem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogController {

    @GetMapping("/{userId}")
    public List<CatalogItem> movieCatalogs(@PathVariable("userId") final String userId) {
        return List.of(
                new CatalogItem("don", "don 1 1993", 5),
                new CatalogItem("don 2", "don 2 2003", 6)
        );
    }
}
