package com.goorm.friendchise.domain.store.presentation;

import com.goorm.friendchise.domain.store.application.SalesService;
import com.goorm.friendchise.domain.store.domain.Sales;
import com.goorm.friendchise.domain.store.dto.SalesDetailedResDto;
import com.goorm.friendchise.domain.store.dto.SalesReqDto;
import com.goorm.friendchise.domain.store.dto.SalesResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class SalesController {

    private final SalesService salesService;

    @PostMapping("/{storeId}/sales")
    public ResponseEntity<String> registerSale(@PathVariable("storeId") Long storeId, @RequestBody SalesReqDto reqDto) {
        salesService.registerSales(storeId, reqDto);

        return ResponseEntity.ok("sales registered successfully");
    }

    @GetMapping("/{storeId}/sales")
    public ResponseEntity<Page<SalesResDto>> getSales(@PageableDefault(size = 8) Pageable pageable, @PathVariable("storeId") Long storeId) {
        return ResponseEntity.ok(salesService.getSales(storeId, pageable));
    }

    @GetMapping("/{storeId}/sales/{salesId}")
    public ResponseEntity<SalesDetailedResDto> getDetailedSale(@PathVariable("storeId") Long storeId, @PathVariable("salesId") Long salesId) {
        return ResponseEntity.ok(salesService.getSalesInfo(storeId, salesId));
    }

}
