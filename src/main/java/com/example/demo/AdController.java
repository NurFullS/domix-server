package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
public class AdController {

    @Autowired
    private AdRepository adRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> createAd(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody AdRequest adRequest) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Требуется авторизация");
            }

            String token = authHeader.substring(7);
            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(401).body("Неверный токен");
            }

            String email = jwtUtil.getEmailFromToken(token);
            Optional<User> userOpt = userRepo.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Пользователь не найден");
            }

            Ad ad = new Ad();
            ad.setTitle(adRequest.getTitle());
            ad.setDescription(adRequest.getDescription());
            ad.setPrice(adRequest.getPrice());
            ad.setCategory(adRequest.getCategory());
            ad.setCity(adRequest.getCity());
            ad.setPhone(adRequest.getPhone());

            List<String> images = adRequest.getImage_url();
            if (images != null) {
                ad.setImageUrl(images); // Сохраняем список как есть
            } else {
                ad.setImageUrl(List.of()); // пустой список вместо null
            }

            ad.setUser(userOpt.get());

            adRepo.save(ad);

            return ResponseEntity.ok("Объявление создано");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Внутренняя ошибка сервера");
        }
    }

    @GetMapping
    public List<Ad> getAllAds() {
        return adRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAdById(@PathVariable Long id) {
        Optional<Ad> adOpt = adRepo.findById(id);
        if (adOpt.isPresent()) {
            return ResponseEntity.ok(adOpt.get());
        } else {
            return ResponseEntity.status(404).body("Объявление не найдено");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Ad>> searchAds(@RequestParam String keyword) {
        List<Ad> results = adRepo.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(keyword, keyword);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyAds(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Нет токена");
            }

            String token = authHeader.substring(7);
            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(401).body("Неверный токен");
            }

            String email = jwtUtil.getEmailFromToken(token);
            Optional<User> userOpt = userRepo.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Пользователь не найден");
            }

            List<Ad> ads = adRepo.findByUser(userOpt.get());
            return ResponseEntity.ok(ads);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Внутренняя ошибка сервера");
        }
    }
}
