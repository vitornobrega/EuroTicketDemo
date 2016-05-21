package com.euroticket.app.web.rest.mapper;

import com.euroticket.app.domain.*;
import com.euroticket.app.web.rest.dto.PaymentDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Payment and its DTO PaymentDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PaymentMapper {

    PaymentDTO paymentToPaymentDTO(Payment payment);

    List<PaymentDTO> paymentsToPaymentDTOs(List<Payment> payments);

    Payment paymentDTOToPayment(PaymentDTO paymentDTO);

    List<Payment> paymentDTOsToPayments(List<PaymentDTO> paymentDTOs);
}
