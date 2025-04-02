package com.goorm.api.user.presentation;

import com.goorm.api.auth.resolver.AuthUser;
import com.goorm.api.user.application.UserService;
import com.goorm.api.user.dto.request.UserCreateRequest;
import com.goorm.api.user.dto.request.UserPasswordRequest;
import com.goorm.api.user.dto.response.UserDetailResponse;
import com.goorm.api.user.dto.response.UserPersistResponse;
import com.goorm.core.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserPersistResponse> register(
            @RequestBody @Valid UserCreateRequest request
    ) {
        return ResponseEntity.status(CREATED).body(userService.createUser(request));
    }

    @GetMapping("/mypage")
    public ResponseEntity<UserDetailResponse> mypage(
            @AuthUser User user
    ) {
        return ResponseEntity.ok(userService.getUserDetail(user));
    }


    @PutMapping("/update/password")
    public ResponseEntity<Void> updatePassword(
            @AuthUser User user,
            @RequestBody @Valid UserPasswordRequest request
    ) {
        userService.updatePassword(user, request.password());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@AuthUser User user) {
        userService.deleteUser(user);
        return ResponseEntity.noContent().build();
    }
}
