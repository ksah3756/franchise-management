package com.goorm.friendchise.domain.store.presentation;

import com.goorm.friendchise.domain.store.application.StoreService;

import com.goorm.friendchise.domain.store.dto.StoreReqDto;
import com.goorm.friendchise.domain.store.dto.res.KakaoApiAddressResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
@Secured("ROLE_STORE")
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/search")
    public ResponseEntity<List<KakaoApiAddressResDto>> searchAddress(@RequestParam(name = "address") String address) {
        return ResponseEntity.ok(storeService.searchAddress(address));
    }

    @PostMapping
    public ResponseEntity createStore(@RequestBody StoreReqDto reqDto) {
        storeService.createStore(reqDto);
        return ResponseEntity.ok("Store created successfully.");
    }

    @GetMapping
    public ResponseEntity getStore() {
        return ResponseEntity.ok(storeService.getStoreInfo());
    }

    @PutMapping()
    public ResponseEntity updateStore(@RequestBody StoreReqDto storeReqDto) {
        storeService.updateStoreInfo(storeReqDto);
        return ResponseEntity.ok("Store updated successfully!");
    }

    @DeleteMapping
    public ResponseEntity deleteStore() {
        storeService.deleteStore();

        return ResponseEntity.ok("Store deleted successfully!");
    }
}
