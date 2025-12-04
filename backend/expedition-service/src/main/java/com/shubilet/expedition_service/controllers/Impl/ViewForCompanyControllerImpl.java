package com.shubilet.expedition_service.controllers.Impl;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shubilet.expedition_service.common.util.ErrorUtils;
import com.shubilet.expedition_service.common.util.StringUtils;
import com.shubilet.expedition_service.common.util.ValidationUtils;
import com.shubilet.expedition_service.controllers.ViewForCompanyController;
import com.shubilet.expedition_service.dataTransferObjects.requests.ExpeditionViewByDateDTO;
import com.shubilet.expedition_service.dataTransferObjects.requests.ExpeditionViewByIdDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.ExpeditionForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.SeatForCompanyDTO;
import com.shubilet.expedition_service.services.ExpeditionService;
import com.shubilet.expedition_service.services.SeatService;

@RestController
@RequestMapping("/api/view/company/")
public class ViewForCompanyControllerImpl implements ViewForCompanyController {

    private final ExpeditionService expeditionService;
    private final SeatService seatService;
    
    public ViewForCompanyControllerImpl(
        ExpeditionService expeditionService,
        SeatService seatService
    ) {
        this.expeditionService = expeditionService;
        this.seatService = seatService;
    }
    
    @PostMapping("/expeditionsByDate")
    public ResponseEntity<?> viewExpeditionsByDate(@RequestBody ExpeditionViewByDateDTO expeditionViewByDateDTO) {
        //STEP 1: Classic validation
        if(expeditionViewByDateDTO == null) {
            return ResponseEntity.badRequest().body(ErrorUtils.criticalError());
        }

        int companyId = expeditionViewByDateDTO.getCompanyId();
        String date = expeditionViewByDateDTO.getDate();

        if(companyId <= 0) {
            return ResponseEntity.badRequest().body(ErrorUtils.isInvalidFormat("Company Id"));
        }

        if(StringUtils.isNullOrBlank(date)) {
            return ResponseEntity.badRequest().body(ErrorUtils.isInvalidFormat("Date"));
        }

        if(!ValidationUtils.isValidDate(date)) {
            return ResponseEntity.badRequest().body(ErrorUtils.isInvalidFormat("Date"));
        }

        //STEP 2: Spesific validations
        
        //STEP 3: Business Logic
        List<ExpeditionForCompanyDTO> expeditions = expeditionService.findExpeditionsByInstant(date);

        //TODO: Belki not found hatası eklenebilir.

        return ResponseEntity.ok().body(expeditions);
    }

    @PostMapping("/activeExpeditions")
    public ResponseEntity<?> viewActiveExpeditions(@RequestBody int companyId) {

        //STEP 1: Classic validation
        if(companyId <= 0) {
            return ResponseEntity.badRequest().body(ErrorUtils.isInvalidFormat("Company Id"));
        }

        //STEP 2: Spesific validations

        //STEP 3: Business Logic
        List<ExpeditionForCompanyDTO> expeditions = expeditionService.findUpcomingExpeditions(companyId);

        return ResponseEntity.ok().body(expeditions);
    }

    @PostMapping("/allExpeditions")
    public ResponseEntity<?> viewAllExpeditions(@RequestBody int companyId) {
        //STEP 1: Classic validation
        if(companyId <= 0) {
            return ResponseEntity.badRequest().body(ErrorUtils.isInvalidFormat("Company Id"));
        }

        //STEP 2: Spesific validations

        //STEP 3: Business Logic
        List<ExpeditionForCompanyDTO> expeditions = expeditionService.findAllExpeditions(companyId);

        return ResponseEntity.ok().body(expeditions);
    }

    @PostMapping("/expeditionDetails")
    public ResponseEntity<?> viewExpeditionDetails(@RequestBody ExpeditionViewByIdDTO expeditionViewById) {
        //STEP 1: Classic validation
        if(expeditionViewById == null) {
            return ResponseEntity.badRequest().body(ErrorUtils.criticalError());
        }

        int companyId = expeditionViewById.getCompanyId();
        int expeditionId = expeditionViewById.getExpeditionId();

        if(companyId <= 0) {
            return ResponseEntity.badRequest().body(ErrorUtils.isInvalidFormat("Company Id"));
        }
        if(expeditionId <= 0) {
            return ResponseEntity.badRequest().body(ErrorUtils.isInvalidFormat("Expedition Id"));
        }

        //STEP 2: Spesific validations

        //STEP 3: Business Logic
        List<SeatForCompanyDTO> seats = seatService.getSeatsByExpeditionId(expeditionId);

        //TODO: Belki not found hatası eklenebilir.

        return ResponseEntity.ok().body(seats);
    }
}
