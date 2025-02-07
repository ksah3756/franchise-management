package com.goorm.friendchise.domain.store.application;

import com.goorm.friendchise.domain.store.domain.Sales;
import com.goorm.friendchise.domain.store.domain.Store;
import com.goorm.friendchise.domain.store.dto.SalesDetailedResDto;
import com.goorm.friendchise.domain.store.dto.SalesReqDto;
import com.goorm.friendchise.domain.store.dto.SalesResDto;
import com.goorm.friendchise.domain.store.exception.SalesNotFoundException;
import com.goorm.friendchise.domain.store.exception.StoreNotFoundException;
import com.goorm.friendchise.domain.store.infrastructure.SalesRepository;
import com.goorm.friendchise.domain.store.infrastructure.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final StoreRepository storeRepository;
    private final SalesRepository salesRepository;

    public void registerSales(Long storeId, SalesReqDto reqDto){
        Store store = findIfStoreExists(storeId);
        LocalDate date = LocalDate.parse(reqDto.today());
        Long dailySales = Long.valueOf(reqDto.dailySales());

        Sales sales = new Sales(date, dailySales, reqDto.writer(), store);
        salesRepository.save(sales);
    }

    @Transactional(readOnly = true)
    public Page<SalesResDto> getSales(Long storeId, Pageable pageable){
        findIfStoreExists(storeId);
        List<Sales> salesList = salesRepository.findAllByStoreId(storeId, pageable);
        List<SalesResDto> salesResDtoList = salesList.stream()
                .map(this::toResponse)
                .toList();

        return new PageImpl<>(salesResDtoList, pageable, salesList.size());
    }

    public SalesDetailedResDto getSalesInfo(Long storeId, Long salesId) {
        findIfStoreExists(storeId);
        Sales result = findIfSalesExists(salesId);

        return new SalesDetailedResDto(result);
    }

    private SalesResDto toResponse(Sales sales){
        return SalesResDto.builder()
                .id(sales.getId())
                .date(sales.getDate())
                .writer(sales.getWriter())
                .build();
    }

    private Store findIfStoreExists(Long storeId) {
        return storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
    }

    private Sales findIfSalesExists(Long salesId) {
        return salesRepository.findById(salesId).orElseThrow(SalesNotFoundException::new);
    }
}
