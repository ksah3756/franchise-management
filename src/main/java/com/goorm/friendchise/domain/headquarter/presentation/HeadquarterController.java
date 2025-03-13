package com.goorm.friendchise.domain.headquarter.presentation;

import com.goorm.friendchise.domain.headquarter.Item.application.ItemService;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemReqDtoList;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemResDto;
import com.goorm.friendchise.domain.headquarter.business.HeadquarterService;
import com.goorm.friendchise.domain.headquarter.business.LocalAnalysisService;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterDetailResponse;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterRequest;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterResponse;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.LocalAnalysisRequest;
import com.goorm.friendchise.domain.headquarter.dto.store.StoreIdDto;
import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.global.auth.resolver.AuthManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
    private final ItemService itemService;
    private final LocalAnalysisService localAnalysisService;

    @Secured("ROLE_HEADQUARTER")
    @PostMapping("/register")
    public ResponseEntity<HeadquarterResponse> createHeadquarter(
            @AuthManager Manager manager,
            @Valid @RequestBody HeadquarterRequest headquarterRequest) {
        return ResponseEntity.created(URI.create("/headquarter")).body(headquarterService.createHeadquarter(manager, headquarterRequest));
    }

    @Secured("ROLE_HEADQUARTER")
    @GetMapping
    public ResponseEntity<HeadquarterDetailResponse> getHeadquarter(@AuthManager Manager manager) {
        return ResponseEntity.ok().body(headquarterService.getHeadquarter(manager));
    }

    // 엔티티 전체 필드가 들어오는 경우 PUT, 일부만 들어오는 경우 PATCH로 구분하는게 맞을거같은데..그냥 PATCH로 구현
    @Secured("ROLE_HEADQUARTER")
    @PatchMapping("/update")
    public ResponseEntity<HeadquarterResponse> updateHeadquarter(
            @AuthManager Manager manager,
            @Valid @RequestBody HeadquarterRequest headquarterRequest) {
        return ResponseEntity.ok().body(headquarterService.updateHeadquarterName(manager, headquarterRequest));
    }

    @Secured("ROLE_HEADQUARTER")
    @DeleteMapping
    public ResponseEntity<Void> deleteHeadquarter(@AuthManager Manager manager) {
        headquarterService.deleteHeadquarter(manager);
        return ResponseEntity.ok().body(null);
    }

    @Secured("ROLE_HEADQUARTER")
    @PageableAsQueryParam
    @GetMapping("/items")
    public ResponseEntity<Slice<ItemResDto>> getItems(@AuthManager Manager manager, Pageable pageable) {
        return ResponseEntity.ok().body(itemService.getItems(manager, pageable));
    }

    @Secured("ROLE_HEADQUARTER")
    @PostMapping("/items/register")
    public ResponseEntity<List<ItemResDto>> createItems(
            @AuthManager Manager manager,
            @Valid @RequestBody ItemReqDtoList itemReqDtoList) {
        return ResponseEntity.created(URI.create("/headquarter/items")).body(itemService.createItems(manager, itemReqDtoList));
    }

    @Secured("ROLE_HEADQUARTER")
    @GetMapping("/stores")
    public ResponseEntity<List<StoreIdDto>> getStores(@AuthManager Manager manager) {
        return ResponseEntity.ok().body(headquarterService.getStores(manager));
    }

    @Secured("ROLE_HEADQUARTER")
    @PostMapping("/store-recommendation")
    public ResponseEntity<List<String>> getRecommendationResult(
            @AuthManager Manager manager,
            @Valid @RequestBody LocalAnalysisRequest req) {
        return ResponseEntity.ok().body(localAnalysisService.getRecommendation(manager, req));
    }

    @PostMapping(value = "/store-recommendation-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<String>> getRecommendationStreamResult(
            @AuthManager Manager manager,
            @Valid @RequestBody LocalAnalysisRequest req) {
        return ResponseEntity.ok().body(localAnalysisService.getRecommendationStream(manager, req));
    }

    @PostMapping("/store-recommendation-dummy")
    public ResponseEntity<List<String>> getRecommendationResultDummy(@Valid @RequestBody LocalAnalysisRequest req) throws InterruptedException {
        return ResponseEntity.ok().body(localAnalysisService.getRecommendationDummy(req));
    }

    @PostMapping(value = "/store-recommendation-stream-dummy", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<String>> getRecommendationStreamResultDummy(
            @AuthManager Manager manager,
            @Valid @RequestBody LocalAnalysisRequest req) throws InterruptedException {
        return ResponseEntity.ok().body(localAnalysisService.getRecommendationStreamDummy(req));
    }

}
