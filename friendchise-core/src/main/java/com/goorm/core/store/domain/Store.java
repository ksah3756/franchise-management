package com.goorm.core.store.domain;

import com.goorm.core.common.domain.BaseEntity;
import com.goorm.core.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(length = 50)
    private String name;

    @NotNull
    @Column(unique = true, length = 100)
    private String address;

    @NotNull
    @Column(length = 50)
    private String dong;

    @NotNull
    private Double pointX;

    @NotNull
    private Double pointY;

    @NotNull
    @Column(unique = true, length = 50)
    private String franchiseName;

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Sales> salesList = new ArrayList<>();

    // 객체 참조 X
    @Column(nullable = false)
    private Long headquarterId;

    public Store(String name, String address, String dong, Double pointX, Double pointY, String franchiseName, Long headquarterId, User user) {
        this.name = name;
        this.address = address;
        this.dong = dong;
        this.pointX = pointX;
        this.pointY = pointY;
        this.franchiseName = franchiseName;
        this.user = user;
        this.headquarterId = headquarterId;
    }

    public static Store create(String name, String address, String dong, Double pointX, Double pointY, String franchiseName, Long headquarterId, User user) {
        return Store.builder()
            .name(name)
            .address(address)
            .dong(dong)
            .pointX(pointX)
            .pointY(pointY)
            .franchiseName(franchiseName)
            .headquarterId(headquarterId)
            .user(user)
            .build();
    }

    public void updateStore(Store store) {
        this.name = store.getName();
        this.address = store.getAddress();
        this.dong = store.getDong();
        this.pointX = store.getPointX();
        this.pointY = store.getPointY();
        this.franchiseName = store.getFranchiseName();
        this.headquarterId = store.getHeadquarterId();
    }
}
