package org.group21.trainsearch.controller;

import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.*;
import org.group21.*;
import org.group21.trainsearch.model.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserProxyController {

    private final RestTemplate restTemplate;

    public UserProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        String url = "http://user/user/login";
        // Forward the login request to the user service
        ResponseEntity<Map> response = restTemplate.postForEntity(url, loginRequest, Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> user) {
        String url = "http://user/user";
        // Forward the user creation request to the user service
        ResponseEntity<Map> response = restTemplate.postForEntity(url, user, Map.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PostMapping("/{id}/topup")
    public ResponseEntity<?> topUpUser(
            @PathVariable Integer id,
            @RequestBody Double amount,
            @RequestHeader(name = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or malformed Authorization header");
        }

        String token = authHeader.substring(7);
        DecodedJWT decoded;
        try {
            decoded = JwtUtil.verifyToken(token);
        } catch (JWTVerificationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token");
        }

        Long tokenUserId = decoded.getClaim("userId").asLong();
        if (!tokenUserId.equals(id.longValue())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("User ID in token does not match requested user ID");
        }

        String url = "http://user/user/" + id + "/topup";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<Double> request = new HttpEntity<>(amount, headers);

        ResponseEntity<Double> response = restTemplate.exchange(
                url, HttpMethod.POST, request, Double.class
        );
        return ResponseEntity.ok(response.getBody());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable Integer id,
            @RequestHeader(name = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or malformed Authorization header");
        }

        String token = authHeader.substring(7);
        DecodedJWT decoded;
        try {
            decoded = JwtUtil.verifyToken(token);
        } catch (JWTVerificationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token");
        }

        Long tokenUserId = decoded.getClaim("userId").asLong();
        if (!tokenUserId.equals(id.longValue())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("User ID in token does not match requested user ID");
        }

        String url = "http://user/user/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<User> resp = restTemplate.exchange(
                url, HttpMethod.GET, request, User.class
        );
        return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
    }

}
