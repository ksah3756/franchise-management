package com.goorm.friendchise.domain.store.domain;

import com.goorm.friendchise.domain.store.dto.StoreReqDto;
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

}
