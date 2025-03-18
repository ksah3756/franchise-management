package com.goorm.friendchise.domain.headquarter.infrastructure;

import com.goorm.friendchise.domain.headquarter.domain.CommercialArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommercialAreaRepository extends JpaRepository<CommercialArea, Long> {

    @Query("SELECT ca FROM CommercialArea ca " +
            "WHERE ST_Contains(ca.geom, ST_GeomFromText(:point, 4326)) = true " +
            "OR ST_Touches(ca.geom, ST_GeomFromText(:point, 4326)) = true")
    List<CommercialArea> findByPoint(@Param("point") String point);
}
