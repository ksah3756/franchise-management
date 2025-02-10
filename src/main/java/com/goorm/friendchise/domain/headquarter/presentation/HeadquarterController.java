package com.goorm.friendchise.domain.headquarter.presentation;

import com.goorm.friendchise.domain.headquarter.Item.application.ItemService;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemReqDtoList;
import com.goorm.friendchise.domain.headquarter.Item.dto.ItemResDto;
import com.goorm.friendchise.domain.headquarter.appilcation.HeadquarterService;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterReqDto;
import com.goorm.friendchise.domain.headquarter.dto.headquarter.HeadquarterResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/headquarter")
public class HeadquarterController {
    private final HeadquarterService headquarterService;
    private final ItemService itemService;

    @PostMapping("/register")
    public ResponseEntity<HeadquarterResDto> createHeadquarter(@Valid @RequestBody HeadquarterReqDto headquarterReqDto) {
        return ResponseEntity.ok().body(headquarterService.createHeadquarter(headquarterReqDto));
    }

    // path variable의 id를 다 fracnhiseName으로 바꾸거나 없애야 할까..

    @GetMapping("/{id}")
    public ResponseEntity<HeadquarterResDto> getHeadquarter(@PathVariable Long id) {
        return ResponseEntity.ok().body(headquarterService.getHeadquarter(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<HeadquarterResDto> updateHeadquarter(@PathVariable Long id, @Valid @RequestBody HeadquarterReqDto headquarterReqDto) {
        return ResponseEntity.ok().body(headquarterService.updateHeadquarterName(id, headquarterReqDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHeadquarter(@PathVariable Long id) {
        headquarterService.deleteHeadquarter(id);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/{id}/item/register")
    public ResponseEntity<List<ItemResDto>> createItems(@PathVariable Long headquarterId ,@Valid @RequestBody ItemReqDtoList itemReqDtoList) {
        return ResponseEntity.ok().body(itemService.createItems(headquarterId, itemReqDtoList));
    }

//    @GetMapping("/store-recommendation")
//    public ResponseEntity<Void> getRecommendationResult(
//            @RequestParam @NotNull Double y,
//            @RequestParam @NotNull Double x,
//            @RequestParam(required = false) List<String> category
//    ) {
////        return headquarterService.requestSameFranchiseData(y, x, category);
//
//    }

}
