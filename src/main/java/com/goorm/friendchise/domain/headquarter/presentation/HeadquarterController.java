package com.goorm.friendchise.domain.headquarter.presentation;

import com.goorm.friendchise.domain.headquarter.Item.application.ItemService;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemReqDtoList;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemResDto;
import com.goorm.friendchise.domain.headquarter.application.HeadquarterService;
import com.goorm.friendchise.domain.headquarter.application.StoreRecommendationService;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterDetailResDto;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterReqDto;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterResDto;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.StoreRecommendReqDto;
import com.goorm.friendchise.domain.headquarter.dto.openai.ChatCompletionResponseDto;
import com.goorm.friendchise.domain.headquarter.dto.openai.ChatCompletionStreamResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private final StoreRecommendationService storeRecommendationService;

    @PostMapping("/register")
    public ResponseEntity<HeadquarterResDto> createHeadquarter(@Valid @RequestBody HeadquarterReqDto headquarterReqDto) {
        return ResponseEntity.created(URI.create("/headquarter")).body(headquarterService.createHeadquarter(headquarterReqDto));
    }

    @GetMapping
    public ResponseEntity<HeadquarterDetailResDto> getHeadquarter() {
        return ResponseEntity.ok().body(headquarterService.getHeadquarter());
    }

    // 엔티티 전체 필드가 들어오는 경우 PUT, 일부만 들어오는 경우 PATCH로 구분하는게 맞을거같은데..그냥 PATCH로 구현
    @PatchMapping("/update")
    public ResponseEntity<HeadquarterResDto> updateHeadquarter(@Valid @RequestBody HeadquarterReqDto headquarterReqDto) {
        return ResponseEntity.ok().body(headquarterService.updateHeadquarterName(headquarterReqDto));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteHeadquarter() {
        headquarterService.deleteHeadquarter();
        return ResponseEntity.ok().body(null);
    }

    @PageableAsQueryParam
    @GetMapping("/items")
    public ResponseEntity<Slice<ItemResDto>> getItems(Pageable pageable) {
        return ResponseEntity.ok().body(itemService.getItemsNative(pageable));
    }

    @PostMapping("/items/register")
    public ResponseEntity<List<ItemResDto>> createItems(@Valid @RequestBody ItemReqDtoList itemReqDtoList) {
        return ResponseEntity.created(URI.create("/headquarter/items")).body(itemService.createItems(itemReqDtoList));
    }

    @PostMapping("/store-recommendation")
    public ResponseEntity<ChatCompletionResponseDto> getRecommendationResult(@Valid @RequestBody StoreRecommendReqDto req) {
        return ResponseEntity.ok().body(storeRecommendationService.getRecommendation(req));
    }

    @PostMapping(value = "/store-recommendation-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<String>> getRecommendationStreamResult(@Valid @RequestBody StoreRecommendReqDto req) {
        return ResponseEntity.ok().body(storeRecommendationService.getRecommendationStream(req));
    }

    @PostMapping("/store-recommendation-dummy")
    public ResponseEntity<ChatCompletionResponseDto> getRecommendationResultDummy(@Valid @RequestBody StoreRecommendReqDto req) throws InterruptedException {
        return ResponseEntity.ok().body(storeRecommendationService.getRecommendationDummy(req));
    }

}
