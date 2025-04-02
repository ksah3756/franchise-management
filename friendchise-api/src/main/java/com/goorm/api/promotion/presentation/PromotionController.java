package com.goorm.api.promotion.presentation;

import com.goorm.api.auth.resolver.AuthUser;
import com.goorm.api.promotion.application.PromotionService;
import com.goorm.api.promotion.dto.request.PromotionCreateRequest;
import com.goorm.api.promotion.dto.response.PromotionDetailResponse;
import com.goorm.core.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
@Secured("ROLE_HEADQUARTER")
public class PromotionController {
	private final PromotionService promotionService;

	@PostMapping("/create")
	public ResponseEntity<String> createPromotion(
			@AuthUser User user,
			@RequestBody PromotionCreateRequest request) {
		promotionService.createPromotion(user, request);
		return ResponseEntity.ok("프로모션이 성공적으로 생성되었습니다.");
	}

	@GetMapping("/my")
	public ResponseEntity<List<PromotionDetailResponse>> getMyPromotions(
			@AuthUser User user
	) {
		return ResponseEntity.ok(promotionService.getMyHeadquarterPromotions(user));
	}
}
