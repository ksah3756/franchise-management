package com.goorm.api.store.implement;

import com.goorm.api.store.dto.res.AddressDetailDto;

import java.util.List;

public interface AddressProvider {
    List<AddressDetailDto> getAddressInfos(String address);
}
