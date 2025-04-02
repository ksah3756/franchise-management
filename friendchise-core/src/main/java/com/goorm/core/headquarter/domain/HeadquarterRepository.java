package com.goorm.core.headquarter.domain;

import java.util.Optional;


public interface HeadquarterRepository {
    Headquarter save(Headquarter headquarter);

    Optional<Headquarter> findById(Long id);

    boolean existsByFranchiseName(String franchiseName);

    void deleteById(Long id);

    Optional<Headquarter> findByFranchiseName(String franchiseName);

    void deleteAll();
}
