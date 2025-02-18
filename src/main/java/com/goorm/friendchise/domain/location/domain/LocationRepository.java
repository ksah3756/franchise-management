package com.goorm.friendchise.domain.location.domain;


import java.util.List;
import java.util.Optional;

public interface LocationRepository {
    Location save(Location location);

    Optional<Location> findById(Long id);

    List<Location> findAll();

    void delete(Location location);

    List<Location> findByCustomerUsernameOrderByRecordedAtDesc(String username);
}
