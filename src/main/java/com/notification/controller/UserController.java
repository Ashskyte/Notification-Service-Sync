package com.notification.controller;

import com.notification.dto.ApiResponse;
import com.notification.dto.UserRegistrationRequest;
import com.notification.dto.UserResponse;
import com.notification.model.enums.NotificationChannel;
import com.notification.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> responses = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", responses));
    }

    @PutMapping("/{id}/preferences")
    public ResponseEntity<ApiResponse<UserResponse>> updateChannelPreferences(
            @PathVariable Long id,
            @RequestBody List<NotificationChannel> channels) {
        UserResponse response = userService.updateChannelPreferences(id, channels);
        return ResponseEntity.ok(ApiResponse.success("Channel preferences updated", response));
    }
}
