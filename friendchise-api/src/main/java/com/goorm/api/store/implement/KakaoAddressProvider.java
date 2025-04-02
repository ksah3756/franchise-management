package com.goorm.api.store.implement;

import com.goorm.api.store.dto.res.AddressDetailDto;
import com.goorm.api.store.dto.res.KakaoApiRes;
import com.goorm.api.store.exception.NotFoundAddressException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KakaoAddressProvider implements AddressProvider {
    @Value("${kakao.api.findPosition}")
    private String findPosition;

    private final WebClient webClient;

    @Override
    public List<AddressDetailDto> getAddressInfos(String address) {
        KakaoApiRes query = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(findPosition)
                        .queryParam("query", address)
                        .build())
                .retrieve()
                .bodyToMono(KakaoApiRes.class)
                .block();

        if(query == null || query.getDocuments().isEmpty()) {
            throw new NotFoundAddressException();
        }

        return getCollect(query);
    }

    private static List<AddressDetailDto> getCollect(KakaoApiRes query) {
        return query.getDocuments().stream()
                .filter(doc -> doc.getRoad_address() != null)
                .map(doc -> {
                    String address = doc.getRoad_address().getAddress_name();
                    String roadAddress = doc.getRoad_address().getAddress_name();
                    String zoneNumber = doc.getRoad_address().getZone_no();
                    Double x = Double.valueOf(doc.getRoad_address().getX());
                    Double y = Double.valueOf(doc.getRoad_address().getY());
                    String dong = doc.getRoad_address().getRegion_3depth_name();
                    return new AddressDetailDto(address, roadAddress, zoneNumber, dong, x, y);
                })
                .collect(Collectors.toList());
    }
}
