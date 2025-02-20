package com.goorm.friendchise.domain.manager.presentation;

import com.goorm.friendchise.domain.manager.application.ManagerService;
import com.goorm.friendchise.domain.manager.dto.request.ManageCreateRequest;
import com.goorm.friendchise.domain.manager.dto.request.ManageLoginRequest;
import com.goorm.friendchise.domain.manager.dto.request.ManagerPasswordRequest;
import com.goorm.friendchise.domain.manager.dto.response.ManagerDetailResponse;
import com.goorm.friendchise.domain.manager.dto.response.ManagerPersistResponse;
import com.goorm.friendchise.global.auth.application.AuthService;
import com.goorm.friendchise.global.auth.dto.request.TokenReissueRequest;
import com.goorm.friendchise.global.auth.dto.response.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping("/manager")
@RestController
@RequiredArgsConstructor
public class ManagerController {
	private final ManagerService managerService;
	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<ManagerPersistResponse> register(
		@RequestBody @Valid ManageCreateRequest request
	) {
		return ResponseEntity.status(CREATED).body(managerService.create(request));
	}

	@PostMapping("/login")
	public ResponseEntity<TokenResponse> login(
		@RequestBody @Valid ManageLoginRequest request
	) {
		return ResponseEntity.ok(managerService.login(request));
	}

	@GetMapping("/mypage")
	public ResponseEntity<ManagerDetailResponse> mypage() {
		return ResponseEntity.ok(managerService.mypage());
	}

	@Secured({"ROLE_HEADQUARTER", "ROLE_STORE"})
	@PutMapping("/update/store-id")
	public ResponseEntity<Void> update(
		@RequestParam Long newStoreId
	) {
		managerService.updateManager(newStoreId);
		return ResponseEntity.noContent().build();
	}

	@Secured({"ROLE_HEADQUARTER", "ROLE_STORE"})
	@PutMapping("/update/password")
	public ResponseEntity<Void> updatePassword(
		@RequestBody @Valid ManagerPasswordRequest request
	) {
		managerService.updatePassword(request.password());
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping
	public ResponseEntity<Void> delete() {
		managerService.delete();
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/reissue")
	public ResponseEntity<TokenResponse> reissue(
		@RequestBody @Valid TokenReissueRequest request
	) {
		TokenResponse response = authService.reissue(request);
		return ResponseEntity.ok(response);
	}

}
