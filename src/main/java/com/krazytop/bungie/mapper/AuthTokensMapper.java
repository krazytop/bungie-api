package com.krazytop.bungie.mapper;

import com.krazytop.bungie.entity.AuthTokens;
import com.krazytop.bungie.model.generated.AuthTokensDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthTokensMapper {

    AuthTokensDTO toDTO(AuthTokens tokens);
}
