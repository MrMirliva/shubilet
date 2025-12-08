package com.shubilet.expedition_service.controllers;

import org.springframework.http.ResponseEntity;

import com.shubilet.expedition_service.dataTransferObjects.requests.CompanyIdDTO;
import com.shubilet.expedition_service.dataTransferObjects.requests.ExpeditionViewByDateDTO;
import com.shubilet.expedition_service.dataTransferObjects.requests.ExpeditionViewByIdDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.ExpeditionsForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.SeatsForCompanyDTO;

public interface ViewForCompanyController {
    public ResponseEntity<ExpeditionsForCompanyDTO> viewExpeditionsByDate(ExpeditionViewByDateDTO expeditionViewByDateDTO);

    public ResponseEntity<ExpeditionsForCompanyDTO> viewActiveExpeditions(CompanyIdDTO companyIdDTO);

    public ResponseEntity<ExpeditionsForCompanyDTO> viewAllExpeditions(CompanyIdDTO companyIdDTO);

    public ResponseEntity<SeatsForCompanyDTO> viewExpeditionDetails(ExpeditionViewByIdDTO expeditionViewById);
}
