package com.euroticket.app.web.rest.mapper;

import com.euroticket.app.domain.*;
import com.euroticket.app.web.rest.dto.SaleStatusDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity SaleStatus and its DTO SaleStatusDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SaleStatusMapper {

    SaleStatusDTO saleStatusToSaleStatusDTO(SaleStatus saleStatus);

    List<SaleStatusDTO> saleStatusesToSaleStatusDTOs(List<SaleStatus> saleStatuses);

    SaleStatus saleStatusDTOToSaleStatus(SaleStatusDTO saleStatusDTO);

    List<SaleStatus> saleStatusDTOsToSaleStatuses(List<SaleStatusDTO> saleStatusDTOs);
}
