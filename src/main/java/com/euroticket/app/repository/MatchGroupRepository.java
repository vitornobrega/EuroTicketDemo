package com.euroticket.app.repository;

import com.euroticket.app.domain.MatchGroup;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the MatchGroup entity.
 */
public interface MatchGroupRepository extends JpaRepository<MatchGroup,Long> {

}
