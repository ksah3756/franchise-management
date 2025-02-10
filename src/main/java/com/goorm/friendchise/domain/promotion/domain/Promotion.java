
package com.goorm.friendchise.domain.promotion.domain;

import com.goorm.friendchise.global.common.BaseEntity;
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

    public static Promotion create(Long headquarterId, String title, String content, LocalDateTime startDate, LocalDateTime endDate) {
        return Promotion.builder()
                .headquarterId(headquarterId)
                .title(title)
                .content(content)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
