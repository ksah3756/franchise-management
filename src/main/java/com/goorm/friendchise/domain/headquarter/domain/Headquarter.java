package com.goorm.friendchise.domain.headquarter.domain;

import com.goorm.friendchise.domain.headquarter.Item.domain.Item;
import com.goorm.friendchise.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Entity
public class Headquarter extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, length = 50)
    private String franchiseName;

    // Headquarter의 persist, remove 시 Item도 같이 처리
    @OneToMany(mappedBy = "headquarter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Item> items;

    public static Headquarter of(String franchiseName) {
        return Headquarter.builder()
                .franchiseName(franchiseName)
                .build();
    }

    public void updateFranchiseName(String franchiseName) {
        this.franchiseName = franchiseName;
    }

    public void addItem(Item item) {
        this.items.add(item);
        item.setHeadquarter(this);
    }
}
