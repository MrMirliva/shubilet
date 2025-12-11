package com.shubilet.api_gateway.mappers;

import com.shubilet.api_gateway.dataTransferObjects.external.requests.MemberCredentialsDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.CookieDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.requests.SessionCreationDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.MemberSessionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface MemberSessionMapper {

    @Mapping(source = "cookieDTO", target = "cookie")
    @Mapping(source = "memberSessionDTO.userId", target = "userId")
    @Mapping(source = "memberSessionDTO.userType", target = "userType")
    public SessionCreationDTO toSessionCreationDTO(CookieDTO cookieDTO, MemberSessionDTO memberSessionDTO);
}
