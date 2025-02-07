package com.goorm.friendchise.domain.store.presentation;

import com.goorm.friendchise.domain.store.application.StoreService;
import com.goorm.friendchise.domain.store.dto.StoreReqDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

//    @PostMapping
//    public ResponseEntity searchAddress(@RequestParam String address) {
//
//    }
//
//    @GetMapping
//    public ResponseEntity getStore() {
//
//    }
//
//    @PutMapping("/{storeId}")
//    public ResponseEntity updateStore(@PathVariable("storeId") String storeId) {
//
//    }
//
//    @DeleteMapping("/{storeId}")
//    public ResponseEntity deleteStore(@PathVariable("storeId") String storeId) {}
}
