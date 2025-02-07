package com.goorm.friendchise.domain.headquarter.infrastructure;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import com.goorm.friendchise.domain.headquarter.domain.HeadquarterRepository;
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
    public boolean existsByFranchiseName(String franchiseName) {
        return jpaHeadquarterRepository.existsByFranchiseName(franchiseName);
    }

    @Override
    public void deleteById(Long id) {
        jpaHeadquarterRepository.deleteById(id);
    }

}
