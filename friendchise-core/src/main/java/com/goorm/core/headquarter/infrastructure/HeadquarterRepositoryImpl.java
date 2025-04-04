package com.goorm.core.headquarter.infrastructure;

import com.goorm.core.headquarter.domain.Headquarter;
import com.goorm.core.headquarter.domain.HeadquarterRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HeadquarterRepositoryImpl implements HeadquarterRepository {
    private final JpaHeadquarterRepository jpaHeadquarterRepository;

    @Override
    public Headquarter save(Headquarter headquarter) {
        return jpaHeadquarterRepository.save(headquarter);
    }

    @Override
    public Optional<Headquarter> findById(Long id) {
        return jpaHeadquarterRepository.findById(id);
    }

    @Override
    public Optional<Headquarter> findByUserId(Long userId) {
        return jpaHeadquarterRepository.findByUserId(userId);
    }

    @Override
    public boolean existsByFranchiseName(String franchiseName) {
        return jpaHeadquarterRepository.existsByFranchiseName(franchiseName);
    }

    @Override
    public void deleteById(Long id) {
        jpaHeadquarterRepository.deleteById(id);
    }

    @Override
    public Optional<Headquarter> findByFranchiseName(String franchiseName) {
        return jpaHeadquarterRepository.findByFranchiseName(franchiseName);
    }

    @Override
    public void deleteAll() {
        jpaHeadquarterRepository.deleteAll();
    }
}
