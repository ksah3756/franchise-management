package com.goorm.friendchise.domain.location.infrastructure;

import com.goorm.friendchise.domain.location.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaLocationRepository extends JpaRepository<Location, Long>
{
    List<Location> findByCustomerUsernameOrderByRecordedAtDesc(String username);

}
