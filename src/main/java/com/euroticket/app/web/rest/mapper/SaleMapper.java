package com.euroticket.app.web.rest.mapper;

import com.euroticket.app.domain.*;
import com.euroticket.app.web.rest.dto.SaleDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Sale and its DTO SaleDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class,ItemMapper.class,PaymentMapper.class })
public interface SaleMapper {

    @Mapping(source = "saleStatus.id", target = "saleStatusId")
    @Mapping(source = "payment", target = "payment")
  //  @Mapping(source = "payment.id", target = "paymentId")
    @Mapping(source = "user.id", target = "userId")
    SaleDTO saleToSaleDTO(Sale sale);

    List<SaleDTO> salesToSaleDTOs(List<Sale> sales);

    @Mapping(source = "items", target = "items")
    @Mapping(source = "payment", target = "payment")
    @Mapping(source = "saleStatusId", target = "saleStatus")
   // @Mapping(source = "paymentId", target = "payment.id")
    @Mapping(source = "userId", target = "user")
    Sale saleDTOToSale(SaleDTO saleDTO);

    List<Sale> saleDTOsToSales(List<SaleDTO> saleDTOs);

    default SaleStatus saleStatusFromId(Long id) {
        if (id == null) {
            return null;
        }
        SaleStatus saleStatus = new SaleStatus();
        saleStatus.setId(id);
        return saleStatus;
    }

    default Payment paymentFromId(Long id) {
        if (id == null) {
            return null;
        }
        Payment payment = new Payment();
        payment.setId(id);
        return payment;
    }
}
