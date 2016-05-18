package com.euroticket.app.repository;

import com.euroticket.app.domain.Sale;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Sale entity.
 */
public interface SaleRepository extends JpaRepository<Sale,Long> {

    @Query("select sale from Sale sale where sale.user.login = ?#{principal}")
    List<Sale> findByUserIsCurrentUser();

}
