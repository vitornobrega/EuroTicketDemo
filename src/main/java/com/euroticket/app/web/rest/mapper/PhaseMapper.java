package com.euroticket.app.web.rest.mapper;

import com.euroticket.app.domain.*;
import com.euroticket.app.web.rest.dto.PhaseDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Phase and its DTO PhaseDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PhaseMapper {

    PhaseDTO phaseToPhaseDTO(Phase phase);

    List<PhaseDTO> phasesToPhaseDTOs(List<Phase> phases);

    Phase phaseDTOToPhase(PhaseDTO phaseDTO);

    List<Phase> phaseDTOsToPhases(List<PhaseDTO> phaseDTOs);
}
