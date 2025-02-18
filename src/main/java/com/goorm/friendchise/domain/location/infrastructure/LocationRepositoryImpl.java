package com.goorm.friendchise.domain.location.infrastructure;

import com.goorm.friendchise.domain.location.domain.Location;
import com.goorm.friendchise.domain.location.domain.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LocationRepositoryImpl implements LocationRepository
{
    private  final JpaLocationRepository jpaLocationRepository;
    @Override
    public Location save(Location location) {
        return jpaLocationRepository.save(location);
    }

    @Override
    public Optional<Location> findById(Long id) {
        return jpaLocationRepository.findById(id);
    }

    @Override
    public List<Location> findAll() {
        return jpaLocationRepository.findAll();
    }

    @Override
    public void delete(Location location) {
        jpaLocationRepository.delete(location);
    }

    @Override
    public List<Location> findByCustomerUsernameOrderByRecordedAtDesc(String username) {
        return jpaLocationRepository.findByCustomerUsernameOrderByRecordedAtDesc(username);
    }
}
