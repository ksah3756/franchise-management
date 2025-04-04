package com.goorm.api.promotion.infrastructure;


import com.goorm.core.promotion.domain.Promotion;
import com.goorm.core.promotion.domain.PromotionRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FakePromotionRepository implements PromotionRepository {
	private final List<Promotion> promotions = Collections.synchronizedList(new ArrayList<>());
	private final AtomicLong sequence = new AtomicLong(1);

	@Override
	public void save(Promotion promotion) {
		Promotion newPromotion = new Promotion(
			sequence.getAndIncrement(),
			promotion.getHeadquarterId(),
			promotion.getTitle(),
			promotion.getContent(),
			promotion.getStartDate(),
			promotion.getEndDate()
		);
		promotions.add(newPromotion);
	}

	@Override
	public List<Promotion> findAll() {
		return new ArrayList<>(promotions);
	}

	@Override
	public Optional<Promotion> findById(Long id) {
		return promotions.stream().filter(promotion -> promotion.getId().equals(id)).findFirst();
	}

	@Override
	public void delete(Long id) {
		promotions.removeIf(promotion -> promotion.getId().equals(id));
	}

	@Override
	public List<Promotion> findByHeadquarterId(Long headquarterId) {
		return promotions.stream()
			.filter(promotion -> promotion.getHeadquarterId().equals(headquarterId))
			.collect(Collectors.toList());
	}
}
