package com.shubilet.expedition_service.controllers;

import org.springframework.http.ResponseEntity;

import com.shubilet.expedition_service.dataTransferObjects.requests.ExpeditionViewByDateDTO;
import com.shubilet.expedition_service.dataTransferObjects.requests.ExpeditionViewByIdDTO;

public interface ViewForCompanyController {
    public ResponseEntity<?> viewExpeditionsByDate(ExpeditionViewByDateDTO expeditionViewByDateDTO);

    public ResponseEntity<?> viewActiveExpeditions(int companyId);

    public ResponseEntity<?> viewAllExpeditions(int companyId);

    public ResponseEntity<?> viewExpeditionDetails(ExpeditionViewByIdDTO expeditionViewById);
}
