package com.goorm.friendchise.domain.promotion.presentation;

import com.goorm.friendchise.domain.promotion.application.PromotionService;
import com.goorm.friendchise.domain.promotion.dto.request.PromotionCreateRequest;
import com.goorm.friendchise.domain.promotion.dto.response.PromotionDetailResponse;
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
	public ResponseEntity<String> createPromotion(@RequestBody PromotionCreateRequest request) {
		promotionService.createPromotion(request);
		return ResponseEntity.ok("프로모션이 성공적으로 생성되었습니다.");
	}

	@GetMapping("/my")
	public ResponseEntity<List<PromotionDetailResponse>> getMyPromotions() {
		return ResponseEntity.ok(promotionService.getMyHeadquarterPromotions());
	}
}
