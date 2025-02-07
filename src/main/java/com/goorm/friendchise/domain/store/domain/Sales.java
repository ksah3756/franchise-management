package com.goorm.friendchise.domain.store.domain;

import com.goorm.friendchise.domain.store.dto.SalesReqDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private Long dailySales;
    private String writer;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Builder
    public Sales(LocalDate date, Long dailySales, String writer, Store store) {
        this.date = date;
        this.dailySales = dailySales;
        this.writer = writer;
        this.store = store;
    }
}
