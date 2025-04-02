package com.goorm.api.promotion.domain;

import com.goorm.core.promotion.domain.Promotion;
import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRole;
import org.apache.catalina.Manager;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PromotionTest {

	@Test
	void create_success() {
		// given
		Long headquarterId = 1L;
		String title = "깜짝적 봄맞이 할인 이벤트";
		String content = "전 제품 30% 할인!";
		LocalDateTime startDate = LocalDateTime.of(2025, 3, 1, 9, 0);
		LocalDateTime endDate = LocalDateTime.of(2025, 3, 7, 23, 59);

		User headquarterManager = User.builder()
			.id(headquarterId)
			.username("test")
			.password("test")
			.userRole(UserRole.HEADQUARTER)
			.build();

		// when
		Promotion promotion = Promotion.create(headquarterManager, title, content, startDate, endDate);

		// then
		assertThat(promotion).isNotNull();
		assertThat(promotion.getHeadquarterId()).isEqualTo(headquarterId);
		assertThat(promotion.getTitle()).isEqualTo(title);
		assertThat(promotion.getContent()).isEqualTo(content);
		assertThat(promotion.getStartDate()).isEqualTo(startDate);
		assertThat(promotion.getEndDate()).isEqualTo(endDate);
	}
}
