package com.euroticket.app.repository;

import com.euroticket.app.domain.Payment;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Payment entity.
 */
public interface PaymentRepository extends JpaRepository<Payment,Long> {

}
