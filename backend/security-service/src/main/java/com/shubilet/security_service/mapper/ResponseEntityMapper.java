package com.shubilet.security_service.mapper;

import org.springframework.http.ResponseEntity;

import com.shubilet.security_service.dataTransferObjects.responses.CheckMessageDTO;
import com.shubilet.security_service.dataTransferObjects.responses.MessageDTO;

public class ResponseEntityMapper {
    private ResponseEntityMapper() {
        throw new IllegalStateException("Utility class" );
    }

    public static ResponseEntity<CheckMessageDTO> toCheckMessageDTOResponseEntity(ResponseEntity<?> responseEntity) {
        if (responseEntity.getBody() instanceof MessageDTO) {
            @SuppressWarnings("unchecked")
            ResponseEntity<MessageDTO> messageDTOResponse = (ResponseEntity<MessageDTO>) responseEntity;
            return messageDTOResponseEntitytoCheckMessageDTOResponseEntity(messageDTOResponse);
        }
        throw new IllegalArgumentException("Response body is not of type MessageDTO");
    }


    // 
    // HELPER METHODS
    // --------------------------------------------------
    private static ResponseEntity<CheckMessageDTO> messageDTOResponseEntitytoCheckMessageDTOResponseEntity(ResponseEntity<MessageDTO> responseEntity) {
        MessageDTO body = responseEntity.getBody();
        if (body != null) {
            CheckMessageDTO dto = new CheckMessageDTO(body.getMessage(), -1);
            return new ResponseEntity<>(dto, responseEntity.getStatusCode());
        }
        return new ResponseEntity<>(null, responseEntity.getStatusCode());
    }
}
