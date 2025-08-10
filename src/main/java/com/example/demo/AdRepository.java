package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdRepository extends JpaRepository<Ad, Long> {
    List<Ad> findByUser(User user);

    void deleteAllByUser(User user);

    List<Ad> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String title, String category);

}
