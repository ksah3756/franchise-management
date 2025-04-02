package com.goorm.core.headquarter.domain;

import com.goorm.core.common.domain.BaseEntity;
import com.goorm.core.common.exception.CustomException;
import com.goorm.core.common.exception.ErrorCode;
import com.goorm.core.store.domain.Store;
import com.goorm.core.user.domain.User;



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
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Column(unique = true, length = 50)
    private String franchiseName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RestaurantCategory restaurantCategory;

    // 세부 카테고리는 없을 수 있음 -> 이 경우 empty string으로 저장
    @NotNull
    @Enumerated(EnumType.STRING)
    private RestaurantSubCategory restaurantSubCategory;

    @NotNull
    @Builder.Default
    private String certificationNumber = UUID.randomUUID().toString();

    // Headquarter의 persist, remove 시 Item도 같이 처리
    // TODO: 라이프사이클이 같지 않으므로 엔티티 대신 ID로 관리
    @Builder.Default
    @OneToMany(mappedBy = "headquarter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Item> items = new ArrayList<>();

    // TODO: 라이프사이클이 같지 않으므로 엔티티 대신 ID로 관리
    @Builder.Default
    @OneToMany(mappedBy = "headquarterId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Store> stores = new ArrayList<>();

    public static Headquarter create(User user, String franchiseName, RestaurantCategory restaurantCategory, RestaurantSubCategory restaurantSubCategory) {
        return Headquarter.builder()
                .user(user)
                .franchiseName(franchiseName)
                .restaurantCategory(restaurantCategory)
                .restaurantSubCategory(restaurantSubCategory)
                .build();
    }

    public static Headquarter forUpdate(String franchiseName, RestaurantCategory restaurantCategory, RestaurantSubCategory restaurantSubCategory) {
        return Headquarter.builder()
                .franchiseName(franchiseName)
                .restaurantCategory(restaurantCategory)
                .restaurantSubCategory(restaurantSubCategory)
                .build();
    }

    /*
    * PATCH method를 구현할 때 사용하려고 만든 메소드인데, 특정 필드가 null인 경우는 없기 때문에 이렇게 했는데 만약 null인 경우가 생긴다면 로직 변경 필요
    */
    public Headquarter update(Headquarter headquarter) {
        this.franchiseName = headquarter.getFranchiseName();
        this.restaurantCategory = headquarter.getRestaurantCategory();
        this.restaurantSubCategory = headquarter.getRestaurantSubCategory();

        return this;
    }

    public void addItem(Item item) {
        this.items.add(item);
        item.setHeadquarter(this);
    }

    public void addStore(Store store) {
        this.stores.add(store);
    }

    public void validateCertificationNumber(String certificationNumber){
		if (!this.certificationNumber.equals(certificationNumber))
			throw new CustomException(ErrorCode.HEADQUARTER_AUTH_NOT_MATCH);
    }
}
