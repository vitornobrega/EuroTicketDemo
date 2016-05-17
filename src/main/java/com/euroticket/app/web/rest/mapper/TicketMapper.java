package com.euroticket.app.web.rest.mapper;

import com.euroticket.app.domain.*;
import com.euroticket.app.web.rest.dto.TicketDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Ticket and its DTO TicketDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TicketMapper {

    @Mapping(source = "homeTeam.id", target = "homeTeamId")
    @Mapping(source = "awayTeam.id", target = "awayTeamId")
    @Mapping(source = "matchGroup.id", target = "matchGroupId")
    @Mapping(source = "phase.id", target = "phaseId")
    TicketDTO ticketToTicketDTO(Ticket ticket);

    List<TicketDTO> ticketsToTicketDTOs(List<Ticket> tickets);

    @Mapping(source = "homeTeamId", target = "homeTeam")
    @Mapping(source = "awayTeamId", target = "awayTeam")
    @Mapping(source = "matchGroupId", target = "matchGroup")
    @Mapping(source = "phaseId", target = "phase")
    Ticket ticketDTOToTicket(TicketDTO ticketDTO);

    List<Ticket> ticketDTOsToTickets(List<TicketDTO> ticketDTOs);

    default Team teamFromId(Long id) {
        if (id == null) {
            return null;
        }
        Team team = new Team();
        team.setId(id);
        return team;
    }

    default MatchGroup matchGroupFromId(Long id) {
        if (id == null) {
            return null;
        }
        MatchGroup matchGroup = new MatchGroup();
        matchGroup.setId(id);
        return matchGroup;
    }

    default Phase phaseFromId(Long id) {
        if (id == null) {
            return null;
        }
        Phase phase = new Phase();
        phase.setId(id);
        return phase;
    }
}
