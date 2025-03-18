package com.goorm.friendchise.domain.headquarter.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.locationtech.jts.geom.Polygon;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "commercial_area",
        indexes = {
                @Index(name = "geom", columnList = "geom")
        }
)
@Builder
@AllArgsConstructor
public class CommercialArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, length = 50) // 유니크 인덱스가 꼭 필요할까?
    private String areaName;

    @NotNull
    private BigDecimal rentalFee;

    @NotNull
    @Column(columnDefinition = "Geometry")
    private Polygon geom;
}
