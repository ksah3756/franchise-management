package com.goorm.api.auth.presentation;

import com.goorm.api.auth.application.TokenService;
import com.goorm.api.auth.dto.request.TokenReissueRequest;
import com.goorm.api.auth.dto.response.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class TokenController {
    private final TokenService tokenService;
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(
            @RequestBody @Valid TokenReissueRequest request
    ) {
        TokenResponse response = tokenService.reissue(request);
        return ResponseEntity.ok(response);
    }
}
