package com.euroticket.app.web.rest.mapper;

import com.euroticket.app.domain.*;
import com.euroticket.app.web.rest.dto.ItemDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Item and its DTO ItemDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ItemMapper {

    @Mapping(source = "ticket.id", target = "ticketId")
    @Mapping(source = "ticket.location", target = "ticketLocation")
    @Mapping(source = "ticket.matchDate", target = "ticketMatchDate")
    @Mapping(source = "ticket.availableQtt", target = "ticketAvailableQtt")
    @Mapping(source = "ticket.totalQtt", target = "ticketTotalQtt")
    @Mapping(source = "ticket.homeTeam.id", target = "ticketHomeTeamId")
    @Mapping(source = "ticket.awayTeam.id", target = "ticketAwayTeamId")
    @Mapping(source = "ticket.matchGroup.id", target = "ticketMatchGroupId")
    @Mapping(source = "ticket.phase.id", target = "ticketPhaseId")
    @Mapping(source = "ticket.homeTeam.name", target = "ticketHomeTeamName")
    @Mapping(source = "ticket.awayTeam.name", target = "ticketAwayTeamName")
    @Mapping(source = "ticket.phase.name", target = "ticketPhaseName")
    @Mapping(source = "ticket.matchGroup.name", target = "ticketMatchGroupName")
    @Mapping(source = "ticket.unitPrice", target = "unitPrice")
    @Mapping(source = "sale.id", target = "saleId")
    ItemDTO itemToItemDTO(Item item);

    List<ItemDTO> itemsToItemDTOs(List<Item> items);

    @Mapping(source = "ticketId", target = "ticket")
    @Mapping(source = "saleId", target = "sale")
    Item itemDTOToItem(ItemDTO itemDTO);

    List<Item> itemDTOsToItems(List<ItemDTO> itemDTOs);

    default Ticket ticketFromId(Long id) {
        if (id == null) {
            return null;
        }
        Ticket ticket = new Ticket();
        ticket.setId(id);
        return ticket;
    }

    default Sale saleFromId(Long id) {
        if (id == null) {
            return null;
        }
        Sale sale = new Sale();
        sale.setId(id);
        return sale;
    }
}
