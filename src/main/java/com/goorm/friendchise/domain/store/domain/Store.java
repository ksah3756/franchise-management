package com.goorm.friendchise.domain.store.domain;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.store.dto.StoreReqDto;
import com.goorm.friendchise.domain.store.dto.res.StoreRegisterDto;
import com.goorm.friendchise.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;
    private String dong;
    private Double pointX;
    private Double pointY;
    private String franchiseName;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Sales> salesList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "headquarter_id", nullable = false)
    private Headquarter headquarter;

    // 양방향 연관관계 편의 메소드, Store 저장할 때 한번 호출 필요
    public void setHeadquarter(Headquarter headquarter) {
        this.headquarter = headquarter;
        headquarter.addStore(this);
    }

    @Builder
    private Store(String address, String dong, Double pointX, Double pointY, String franchiseName) {
        this.address = address;
        this.dong = dong;
        this.pointX = pointX;
        this.pointY = pointY;
        this.franchiseName = franchiseName;
    }

    public static Store createStore(StoreRegisterDto storeRegisterDto, Headquarter headquarter) {
        Store store = Store.builder()
                .address(storeRegisterDto.address())
                .dong(storeRegisterDto.dong())
                .pointX(storeRegisterDto.pointX())
                .pointY(storeRegisterDto.pointY())
                .build();
        store.setHeadquarter(headquarter); // headquarter 측 리스트에도 추가하기 위해 사용
        return store;
    }
}
