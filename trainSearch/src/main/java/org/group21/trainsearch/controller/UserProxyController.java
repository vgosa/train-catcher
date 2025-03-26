package org.group21.trainsearch.controller;

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
}
