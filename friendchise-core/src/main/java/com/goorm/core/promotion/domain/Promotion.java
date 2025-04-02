package com.goorm.core.promotion.domain;

import com.goorm.core.common.domain.BaseEntity;
import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;
import com.goorm.core.user.domain.User;
import com.goorm.core.user.domain.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Promotion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long headquarterId;  // 본사 ID

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 255)
    private String content;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

	public static Promotion create(User user, String title, String content, LocalDateTime startDate, LocalDateTime endDate) {
		if (user.getUserRole() != UserRole.HEADQUARTER) {
			throw new CustomException(ErrorCode.NO_HEADQUARTER_AUTHENTICATION_ERROR);
		}

		return Promotion.builder()
			.headquarterId(user.getId())
			.title(title)
			.content(content)
			.startDate(startDate)
			.endDate(endDate)
			.build();
	}
}
