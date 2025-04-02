package com.goorm.api.headquarter.implement.headquarter;

import com.goorm.core.headquarter.domain.Headquarter;
import com.goorm.core.headquarter.domain.HeadquarterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeadquarterWriter {
    private final HeadquarterRepository headquarterRepository;

    public Headquarter createHeadquarter(Headquarter headquarter) {
        return headquarterRepository.save(headquarter);
    }

    public Headquarter updateHeadquarter(Headquarter headquarter, Headquarter updatedHeadquarter) {
        return headquarter.update(updatedHeadquarter);
    }

    public void deleteHeadquarter(Long id) {
        headquarterRepository.deleteById(id);
    }
}
