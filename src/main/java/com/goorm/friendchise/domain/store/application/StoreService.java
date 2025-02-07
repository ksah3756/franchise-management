package com.goorm.friendchise.domain.store.application;

import com.goorm.friendchise.domain.store.dto.StoreReqDto;
import com.goorm.friendchise.domain.store.dto.res.KakaoApiAddressResDto;
import com.goorm.friendchise.domain.store.dto.res.KakaoApiRes;
import com.goorm.friendchise.domain.store.exception.NotFoundAddressException;
import com.goorm.friendchise.domain.store.infrastructure.SalesRepository;
import com.goorm.friendchise.domain.store.infrastructure.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    @Value("${kakao.api.findPosition}")
    private String findPosition;

    private final StoreRepository storeRepository;
    private final SalesRepository salesRepository;
    private final WebClient webClient;

    // 주소 검색 시 관련된 주소 리스트 반환
    public List<KakaoApiAddressResDto> searchAddress(StoreReqDto req) {

        KakaoApiRes query = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(findPosition)
                        .queryParam("query", req.address())
                        .build())
                .retrieve()
                .bodyToMono(KakaoApiRes.class)
                .block();

        if(query == null || query.getDocumentList().isEmpty()) {
            throw new NotFoundAddressException();
        }

        return query.getDocumentList().stream()
                .map(doc -> {
                    String address = doc.getAddress().getAddress_name();
                    String roadAddress = doc.getRoad_address().getAddress_name();
                    String zoneNumber = doc.getRoad_address().getZone_no();
                    Long x = Long.valueOf(doc.getRoad_address().getX());
                    Long y = Long.valueOf(doc.getRoad_address().getY());
                    return new KakaoApiAddressResDto(address, roadAddress, zoneNumber, x, y);
                })
                .collect(Collectors.toList());
    }
}
