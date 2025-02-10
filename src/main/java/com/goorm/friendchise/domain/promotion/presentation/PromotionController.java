package com.goorm.friendchise.domain.promotion.presentation;

import com.goorm.friendchise.domain.promotion.application.PromotionService;
import com.goorm.friendchise.domain.promotion.dto.request.PromotionCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/promotions")
@RequiredArgsConstructor
public class PromotionController {
	private final PromotionService promotionService;

	@PostMapping("/create")
	public ResponseEntity<String> createPromotion(@RequestBody PromotionCreateRequest request) {
		promotionService.createPromotion(request);
		return ResponseEntity.ok("프로모션이 성공적으로 생성되었습니다.");
	}
}
