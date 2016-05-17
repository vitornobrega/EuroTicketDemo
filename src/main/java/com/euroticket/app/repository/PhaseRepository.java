package com.euroticket.app.repository;

import com.euroticket.app.domain.Phase;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Phase entity.
 */
public interface PhaseRepository extends JpaRepository<Phase,Long> {

}
