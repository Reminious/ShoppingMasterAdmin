package com.example.demo.Repository;

import com.example.demo.Entity.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WebsiteRepository extends JpaRepository<Website, Integer> {
    List<Website> findByShopNameContaining(String searchTerm);

    @Query("SELECT w.url FROM Website w")
    List<String> findAllUrls();

    Website findByUrl(String url);
}
