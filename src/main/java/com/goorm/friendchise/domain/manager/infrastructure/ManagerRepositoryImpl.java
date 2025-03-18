package com.goorm.friendchise.domain.manager.infrastructure;

import com.goorm.friendchise.domain.manager.domain.Manager;
import com.goorm.friendchise.domain.manager.domain.ManagerRepository;
import com.goorm.friendchise.global.aop.ExecutionTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ManagerRepositoryImpl implements ManagerRepository {
	private final JpaManagerRepository jpaManagerRepository;

	@Override
	public Manager save(Manager manager) {
		return jpaManagerRepository.save(manager);
	}

	@Override
	public Optional<Manager> findById(Long id) {
		return jpaManagerRepository.findById(id);
	}

	@Override
	@ExecutionTime
	public Optional<Manager> findByUsername(String username) {
		return jpaManagerRepository.findByUsername(username);
	}

	@Override
	public void delete(Manager manager) {
		jpaManagerRepository.delete(manager);
	}
}
