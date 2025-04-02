package com.goorm.core.headquarter.infrastructure;


import com.goorm.core.headquarter.domain.Headquarter;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaHeadquarterRepository extends JpaRepository<Headquarter, Long> {

    boolean existsByFranchiseName(@NotNull String franchiseName);

    Optional<Headquarter> findByFranchiseName(@NotNull String franchiseName);

    Optional<Headquarter> findByUserId(@NotNull Long userId);
}
