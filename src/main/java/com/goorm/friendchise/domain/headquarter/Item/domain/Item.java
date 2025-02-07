package com.goorm.friendchise.domain.headquarter.Item.domain;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Entity
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "headquarter_id")
    private Headquarter headquarter;

    @NotNull
    @Column(length = 50)
    private String name;

    @NotNull
    private int price;

    public static Item of(String name, int price) {
        return Item.builder()
                .name(name)
                .price(price)
                .build();
    }

    public static Item of(Headquarter headquarter, String name, int price) {
        return Item.builder()
                .headquarter(headquarter)
                .name(name)
                .price(price)
                .build();
    }

    public void setHeadquarter(Headquarter headquarter) {
        this.headquarter = headquarter;
    }
}
