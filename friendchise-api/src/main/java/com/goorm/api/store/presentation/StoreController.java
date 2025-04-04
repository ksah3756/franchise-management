package com.goorm.api.store.presentation;

import com.goorm.api.auth.resolver.AuthUser;
import com.goorm.api.store.application.StoreService;
import com.goorm.api.store.dto.StoreReqDto;
import com.goorm.api.store.dto.res.AddressDetailDto;
import com.goorm.core.user.domain.User;
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
    public ResponseEntity<List<AddressDetailDto>> searchAddress(
            @AuthUser User user,
            @RequestParam(name = "address") String address) {
        return ResponseEntity.ok(storeService.searchAddress(user, address));
    }

    @PostMapping
    public ResponseEntity createStore(
            @AuthUser User user,
            @RequestBody StoreReqDto reqDto) {
        storeService.createStore(user, reqDto);
        return ResponseEntity.ok("Store created successfully.");
    }

    @GetMapping
    public ResponseEntity getStore(
            @AuthUser User user
    ) {
        return ResponseEntity.ok(storeService.getStoreInfo(user));
    }

    @PutMapping()
    public ResponseEntity updateStore(
            @AuthUser User user,
            @RequestBody StoreReqDto storeReqDto) {
        storeService.updateStoreInfo(user, storeReqDto);
        return ResponseEntity.ok("Store updated successfully!");
    }

    @DeleteMapping
    public ResponseEntity deleteStore(
            @AuthUser User user
    ) {
        storeService.deleteStore(user);

        return ResponseEntity.ok("Store deleted successfully!");
    }
}
