package com.goorm.friendchise.domain.store.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KakaoApiAddressResDto {

    private String address;
    private String roadAddress;
    private String zoneNumber;
    private Long x;
    private Long y;
}
