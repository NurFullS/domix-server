package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.User.ResetPasswordRequest;

import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AdRepository adRepo;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private JwtUtil jwtUtil;

    public static class UpdateEmailRequest {
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class UpdatePhoneRequest {
        private String phone;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    public static class UpdateUsernameRequest {
        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    public static class LoginResponse {
        private String token;
        private String username;
        private String email;

        public LoginResponse(String token, String username, String email) {
            this.token = token;
            this.username = username;
            this.email = email;
        }

        public String getToken() {
            return token;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }
    }

    @GetMapping
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam("file") MultipartFile file) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Отсутствует токен");
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

        User user = userOpt.get();

        try {
            Map<String, Object> uploadResult = cloudinaryService.uploadFile(file.getBytes(), "avatars/" + user.getId());
            String avatarUrl = (String) uploadResult.get("secure_url");
            user.setAvatarUrl(avatarUrl);
            userRepo.save(user);

            return ResponseEntity.ok(avatarUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Ошибка при загрузке файла");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User newUser) {
        if (userRepo.findByEmail(newUser.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body("Электронный адрес уже используется!");
        }

        User savedUser = userRepo.save(newUser);
        String token = jwtUtil.generateToken(savedUser.getEmail());

        return ResponseEntity.ok(new LoginResponse(token, savedUser.getUsername(), savedUser.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        String email = loginRequest.getEmail() != null ? loginRequest.getEmail().trim() : null;
        String password = loginRequest.getPassword() != null ? loginRequest.getPassword().trim() : null;

        Optional<User> userOpt = userRepo.findByEmail(email);

        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            return ResponseEntity.status(401).body("Неправильный электронный адрес либо пароль!");
        }

        User user = userOpt.get();
        String token = jwtUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getEmail()));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
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

        return ResponseEntity.ok(userOpt.get());
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepo.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Пользователь не найден");
        }
        User user = userOpt.get();

        adRepo.deleteAllByUser(user);
        userRepo.delete(user);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/email")
    public ResponseEntity<?> updateEmail(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody UpdateEmailRequest request) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Отсутствует токен");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateJwtToken(token)) {
            return ResponseEntity.status(401).body("Неверный токен");
        }

        String currentEmail = jwtUtil.getEmailFromToken(token);
        Optional<User> userOpt = userRepo.findByEmail(currentEmail);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Пользователь не найден");
        }

        String newEmail = request.getEmail();
        if (newEmail == null || !newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return ResponseEntity.badRequest().body("Некорректный email");
        }

        Optional<User> existingUser = userRepo.findByEmail(newEmail);
        if (existingUser.isPresent() && !existingUser.get().getEmail().equals(currentEmail)) {
            return ResponseEntity.status(409).body("Email уже используется другим пользователем");
        }

        User user = userOpt.get();
        user.setEmail(newEmail);
        userRepo.save(user);

        String newToken = jwtUtil.generateToken(newEmail);
        return ResponseEntity.ok(new LoginResponse(newToken, user.getUsername(), newEmail));
    }

    @PutMapping("/phone")
    public ResponseEntity<?> updatePhone(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody UpdatePhoneRequest request) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Отсутствует токен");
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

        String phone = request.getPhone();
        if (phone == null || !phone.matches("\\d{7,15}")) {
            return ResponseEntity.badRequest().body("Некорректный номер");
        }

        User user = userOpt.get();
        user.setTelefonNumber(phone);
        userRepo.save(user);

        return ResponseEntity.ok("Номер успешно обновлён");
    }

    @PutMapping("/username")
    public ResponseEntity<?> updateUsername(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody UpdateUsernameRequest request) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Отсутствует токен");
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

        String newUsername = request.getUsername();
        if (newUsername == null || newUsername.trim().length() < 2) {
            return ResponseEntity.badRequest().body("Имя должно содержать минимум 2 символа");
        }

        User user = userOpt.get();
        user.setUsername(newUsername);
        userRepo.save(user);

        return ResponseEntity.ok("Имя пользователя успешно обновлено");
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ResetPasswordRequest request) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Отсутствует токен");
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

        User user = userOpt.get();

        if (!user.getPassword().equals(request.getCurrentPassword())) {
            return ResponseEntity.status(403).body("Неверный текущий пароль");
        }

        String newPassword = request.getNewPassword();
        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body("Пароль должен быть не менее 6 символов");
        }

        user.setPassword(newPassword);
        userRepo.save(user);

        return ResponseEntity.ok("Пароль успешно обновлён");
    }
}
