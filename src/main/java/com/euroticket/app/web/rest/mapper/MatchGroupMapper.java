package com.euroticket.app.web.rest.mapper;

import com.euroticket.app.domain.*;
import com.euroticket.app.web.rest.dto.MatchGroupDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity MatchGroup and its DTO MatchGroupDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface MatchGroupMapper {

    MatchGroupDTO matchGroupToMatchGroupDTO(MatchGroup matchGroup);

    List<MatchGroupDTO> matchGroupsToMatchGroupDTOs(List<MatchGroup> matchGroups);

    MatchGroup matchGroupDTOToMatchGroup(MatchGroupDTO matchGroupDTO);

    List<MatchGroup> matchGroupDTOsToMatchGroups(List<MatchGroupDTO> matchGroupDTOs);
}
