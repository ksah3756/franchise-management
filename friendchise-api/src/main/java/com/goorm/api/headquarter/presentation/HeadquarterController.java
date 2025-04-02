package com.goorm.api.headquarter.presentation;

import com.goorm.api.auth.resolver.AuthUser;
import com.goorm.api.headquarter.application.HeadquarterService;
import com.goorm.api.headquarter.application.LocalAnalysisService;
import com.goorm.api.headquarter.dto.headquarter.*;
import com.goorm.api.headquarter.dto.item.ItemRequestList;
import com.goorm.api.headquarter.dto.item.ItemResponse;
import com.goorm.api.headquarter.dto.store.StoreIdDto;
import com.goorm.core.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/headquarter")
public class HeadquarterController {
    private final HeadquarterService headquarterService;
    private final LocalAnalysisService localAnalysisService;

    @PreAuthorize("hasRole('HEADQUARTER')")
    @PostMapping("/register")
    public ResponseEntity<HeadquarterResponse> createHeadquarter(
            @AuthUser User user,
            @Valid @RequestBody HeadquarterCreateRequest headquarterRequest) {
        return ResponseEntity.created(URI.create("/headquarter")).body(headquarterService.createHeadquarter(user, headquarterRequest));
    }

    @PreAuthorize("hasRole('HEADQUARTER')")
    @GetMapping
    public ResponseEntity<HeadquarterDetailResponse> getHeadquarter(@AuthUser User user) {
        return ResponseEntity.ok().body(headquarterService.getHeadquarter(user));
    }

    // 엔티티 전체 필드가 들어오는 경우 PUT, 일부만 들어오는 경우 PATCH로 구분하는게 맞을거같은데..그냥 PATCH로 구현
    @PreAuthorize("hasRole('HEADQUARTER')")
    @PatchMapping("/update")
    public ResponseEntity<HeadquarterResponse> updateHeadquarter(
            @AuthUser User user,
            @Valid @RequestBody HeadquarterUpdateRequest headquarterRequest) {
        return ResponseEntity.ok().body(headquarterService.updateHeadquarter(user, headquarterRequest));
    }

    @PreAuthorize("hasRole('HEADQUARTER')")
    @DeleteMapping
    public ResponseEntity<Void> deleteHeadquarter(@AuthUser User user) {
        headquarterService.deleteHeadquarter(user);
        return ResponseEntity.ok().body(null);
    }

    @PreAuthorize("hasRole('HEADQUARTER')")
    @PageableAsQueryParam
    @GetMapping("/items")
    public ResponseEntity<Slice<ItemResponse>> getItems(@AuthUser User user, Pageable pageable) {
        return ResponseEntity.ok().body(headquarterService.getItems(user, pageable));
    }

    @PreAuthorize("hasRole('HEADQUARTER')")
    @PostMapping("/items/register")
    public ResponseEntity<List<ItemResponse>> createItems(
            @AuthUser User user,
            @Valid @RequestBody ItemRequestList itemRequestList) {
        return ResponseEntity.created(URI.create("/headquarter/items")).body(headquarterService.createItems(user, itemRequestList));
    }

    @PreAuthorize("hasRole('HEADQUARTER')")
    @GetMapping("/stores")
    public ResponseEntity<List<StoreIdDto>> getStores(@AuthUser User user) {
        return ResponseEntity.ok().body(headquarterService.getStores(user));
    }

    @PreAuthorize("hasRole('HEADQUARTER')")
    @PostMapping("/store-recommendation")
    public ResponseEntity<List<String>> getRecommendationResult(
            @AuthUser User user,
            @Valid @RequestBody LocalAnalysisRequest req) {
        return ResponseEntity.ok().body(localAnalysisService.getRecommendation(user, req));
    }

    @PostMapping(value = "/store-recommendation-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<String>> getRecommendationStreamResult(
            @AuthUser User user,
            @Valid @RequestBody LocalAnalysisRequest req) {
        return ResponseEntity.ok().body(localAnalysisService.getRecommendationStream(user, req));
    }

    @PostMapping("/store-recommendation-dummy")
    public ResponseEntity<List<String>> getRecommendationResultDummy(@Valid @RequestBody LocalAnalysisRequest req) throws InterruptedException {
        return ResponseEntity.ok().body(localAnalysisService.getRecommendationDummy(req));
    }

    @PostMapping(value = "/store-recommendation-stream-dummy", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<String>> getRecommendationStreamResultDummy(
            @AuthUser User user,
            @Valid @RequestBody LocalAnalysisRequest req) throws InterruptedException {
        return ResponseEntity.ok().body(localAnalysisService.getRecommendationStreamDummy(req));
    }

}
