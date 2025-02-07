package com.goorm.friendchise.domain.headquarter.infrastructure;

import com.goorm.friendchise.domain.headquarter.domain.Headquarter;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaHeadquarterRepository extends JpaRepository<Headquarter, Long> {

    boolean existsByFranchiseName(@NotNull String franchiseName);
}
