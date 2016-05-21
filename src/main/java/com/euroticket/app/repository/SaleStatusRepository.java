package com.euroticket.app.repository;

import com.euroticket.app.domain.SaleStatus;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the SaleStatus entity.
 */
public interface SaleStatusRepository extends JpaRepository<SaleStatus,Long> {
	
	

}
