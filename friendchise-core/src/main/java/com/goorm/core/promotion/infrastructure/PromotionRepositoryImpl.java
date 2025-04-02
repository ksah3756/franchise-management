package com.goorm.core.promotion.infrastructure;

import com.goorm.core.promotion.domain.Promotion;
import com.goorm.core.promotion.domain.PromotionRepository;
import com.goorm.core.promotion.infrastructure.JpaPromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PromotionRepositoryImpl implements PromotionRepository {
	private final JpaPromotionRepository jpaPromotionRepository;

	@Override
	public void save(Promotion promotion) {
		jpaPromotionRepository.save(promotion);
	}

	@Override
	public List<Promotion> findAll() {
		return jpaPromotionRepository.findAll();
	}

	@Override
	public Optional<Promotion> findById(Long id) {
		return jpaPromotionRepository.findById(id);
	}

	@Override
	public void delete(Long id) {
		jpaPromotionRepository.deleteById(id);
	}

	@Override
	public List<Promotion> findByHeadquarterId(Long headquarterId) {
		return jpaPromotionRepository.findByHeadquarterId(headquarterId);
	}
}
