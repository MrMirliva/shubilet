package com.shubilet.expedition_service.controllers;

import org.springframework.http.ResponseEntity;

import com.shubilet.expedition_service.dataTransferObjects.requests.ExpeditionCreationDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.message.MessageDTO;

public interface CreationController {

    public ResponseEntity<MessageDTO> createExpedition(ExpeditionCreationDTO request);
}
