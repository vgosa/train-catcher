package org.group21.user.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.group21.user.exception.UserNotFoundException;
import org.group21.user.model.*;
import org.group21.user.service.*;
import org.group21.user.views.Views;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    @JsonView(Views.Public.class)
    public ResponseEntity<User> createUser(@RequestBody User user){
        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/createWithList")
    @JsonView(Views.Public.class)
    public ResponseEntity<List<User>> createUsersWithListInput(@RequestBody List<User> users){
        List<User> createdList = userService.createUsers(users);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdList);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody LoginRequest loginRequest){
        try {
            String token = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
            Optional<User> userOpt = userService.getUserByEmail(loginRequest.getEmail());
            if (userOpt.isEmpty()) {
                throw new UserNotFoundException("User not found during login", loginRequest.getEmail());
            }
            Long userId = userOpt.get().getId();
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);  //TODO: Send as HTTP-only cookie
            response.put("userId", userId);
            return ResponseEntity.ok(response);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestBody(required = false) Object logoutRequest){
        //TODO: For simplicity, no session management is implemented yet
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/{id}")
    @JsonView(Views.Public.class)
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    @JsonView(Views.Public.class)
    public ResponseEntity<List<User>> getAllUsers() {
        log.debug("Fetching users");
        List<User> allUsers = userService.getAllUsers();
        log.debug(Arrays.toString(allUsers.toArray()));
        return ResponseEntity.ok(allUsers);
    }


    @PutMapping("/{id}")
    @JsonView(Views.Public.class)
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody User user){
        try {
            User updated = userService.updateUser(id, user);
            return ResponseEntity.ok(updated);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/topup")
    public ResponseEntity<Double> topUpUser(
            @PathVariable Long id,
            @RequestBody Double amount) {

        User updated = userService.topUpBalance(id, amount);
        return ResponseEntity.ok(updated.getBalance());
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id){
        try {
            userService.deleteUserById(id);
            log.info("User with ID {} deleted", id);
            return ResponseEntity.noContent().build();
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
