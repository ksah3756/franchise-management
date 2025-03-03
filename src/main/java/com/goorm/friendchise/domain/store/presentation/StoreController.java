package com.goorm.friendchise.domain.store.presentation;

import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.store.application.StoreService;

import com.goorm.friendchise.domain.store.dto.StoreReqDto;
import com.goorm.friendchise.domain.store.dto.res.KakaoApiAddressResDto;
import com.goorm.friendchise.global.auth.resolver.AuthManager;
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
    public ResponseEntity<List<KakaoApiAddressResDto>> searchAddress(
            @AuthManager Manager manager,
            @RequestParam(name = "address") String address) {
        return ResponseEntity.ok(storeService.searchAddress(manager, address));
    }

    @PostMapping
    public ResponseEntity createStore(
            @AuthManager Manager manager,
            @RequestBody StoreReqDto reqDto) {
        storeService.createStore(manager, reqDto);
        return ResponseEntity.ok("Store created successfully.");
    }

    @GetMapping
    public ResponseEntity getStore(
            @AuthManager Manager manager
    ) {
        return ResponseEntity.ok(storeService.getStoreInfo(manager));
    }

    @PutMapping()
    public ResponseEntity updateStore(
            @AuthManager Manager manager,
            @RequestBody StoreReqDto storeReqDto) {
        storeService.updateStoreInfo(manager, storeReqDto);
        return ResponseEntity.ok("Store updated successfully!");
    }

    @DeleteMapping
    public ResponseEntity deleteStore(
            @AuthManager Manager manager
    ) {
        storeService.deleteStore(manager);

        return ResponseEntity.ok("Store deleted successfully!");
    }
}
