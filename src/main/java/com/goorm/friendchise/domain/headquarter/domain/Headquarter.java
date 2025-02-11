package com.goorm.friendchise.domain.headquarter.domain;

import com.goorm.friendchise.domain.headquarter.Item.domain.Item;
import com.goorm.friendchise.domain.manager.exception.HeadquarterAuthNotMatchException;
import com.goorm.friendchise.domain.store.domain.Store;
import com.goorm.friendchise.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    // 세부 카테고리는 없을 수 있음 -> 이 경우 empty string으로 저장
    @NotNull
    @Enumerated(EnumType.STRING)
    private SubCategory subCategory;

    @NotNull
    @Builder.Default
    private String certificationNumber = UUID.randomUUID().toString();

    // Headquarter의 persist, remove 시 Item도 같이 처리
    @Builder.Default
    @OneToMany(mappedBy = "headquarter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Item> items = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "headquarter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Store> stores = new ArrayList<>();

    public static Headquarter of(String franchiseName, Category category, SubCategory subCategory) {
        return Headquarter.builder()
                .franchiseName(franchiseName)
                .category(category)
                .subCategory(subCategory)
                .build();
    }

    public void updateFranchiseName(String franchiseName) {
        this.franchiseName = franchiseName;
    }

    public void addItem(Item item) {
        this.items.add(item);
        item.setHeadquarter(this);
    }

    public void addStore(Store store) {
        this.stores.add(store);
    }

    public void validateCertificationNumber(String certificationNumber){
		if (! this.certificationNumber.equals(certificationNumber))
			throw new HeadquarterAuthNotMatchException();
    }
}
