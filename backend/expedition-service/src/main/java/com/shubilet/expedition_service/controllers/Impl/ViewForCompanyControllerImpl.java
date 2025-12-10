package com.shubilet.expedition_service.controllers.Impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shubilet.expedition_service.common.util.ErrorUtils;
import com.shubilet.expedition_service.common.util.StringUtils;
import com.shubilet.expedition_service.common.util.ValidationUtils;
import com.shubilet.expedition_service.controllers.ViewForCompanyController;
import com.shubilet.expedition_service.dataTransferObjects.requests.CompanyIdDTO;
import com.shubilet.expedition_service.dataTransferObjects.requests.ExpeditionViewByDateDTO;
import com.shubilet.expedition_service.dataTransferObjects.requests.ExpeditionViewByIdDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.base.ExpeditionForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.base.SeatForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.ExpeditionsForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.SeatsForCompanyDTO;
import com.shubilet.expedition_service.services.ExpeditionService;
import com.shubilet.expedition_service.services.SeatService;

@RestController
@RequestMapping("/api/view/company/")
public class ViewForCompanyControllerImpl implements ViewForCompanyController {
    private static final Logger logger = LoggerFactory.getLogger(ViewForCompanyControllerImpl.class);

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
    public ResponseEntity<ExpeditionsForCompanyDTO> viewExpeditionsByDate(@RequestBody ExpeditionViewByDateDTO expeditionViewByDateDTO) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.EXPEDITIONS_FOR_COMPANY_DTO);

        //STEP 1: Classic validation
        if(expeditionViewByDateDTO == null) {
            logger.error("ExpeditionViewByDateDTO is null");
            return errorUtils.criticalError();
        }

        int companyId = expeditionViewByDateDTO.getCompanyId();
        String date = expeditionViewByDateDTO.getDate();

        if(companyId <= 0) {
            logger.error("Company Id is invalid: {}", companyId);
            return errorUtils.isInvalidFormat("Company Id");
        }

        if(StringUtils.isNullOrBlank(date)) {
            logger.error("Date is null or blank");
            return errorUtils.isInvalidFormat("Date");
        }

        if(!ValidationUtils.isValidDate(date)) {
            logger.error("Date format is invalid: {}", date);
            return errorUtils.isInvalidFormat("Date");
        }

        //STEP 2: Spesific validations
        
        //STEP 3: Business Logic
        List<ExpeditionForCompanyDTO> expeditions = expeditionService.findExpeditionsByInstantAndCompanyId(date, companyId);

        if(expeditions.isEmpty()) {
            logger.error("No expeditions found for date: {}", date);
            return errorUtils.notFound("Expeditions");
        }

        logger.info("Expeditions found for date: {}", date);
        return ResponseEntity.ok().body(new ExpeditionsForCompanyDTO("Expeditions found", expeditions));
    }
    
    @PostMapping("/activeExpeditions")
    public ResponseEntity<ExpeditionsForCompanyDTO> viewActiveExpeditions(@RequestBody CompanyIdDTO companyIdDTO) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.EXPEDITIONS_FOR_COMPANY_DTO);

        //STEP 1: Classic validation
        if(companyIdDTO == null) {
            logger.error("CompanyIdDTO is null");
            return errorUtils.criticalError();
        }

        int companyId = companyIdDTO.getCompanyId();

        if(companyId <= 0) {
            logger.error("Company Id is invalid: {}", companyId);
            return errorUtils.isInvalidFormat("Company Id");
        }

        //STEP 2: Spesific validations

        //STEP 3: Business Logic
        List<ExpeditionForCompanyDTO> expeditions = expeditionService.findUpcomingExpeditions(companyId);

        if(expeditions.isEmpty()) {
            logger.error("No active expeditions found for company id: {}", companyId);
            return errorUtils.notFound("Expeditions");
        }

        logger.info("Active expeditions found for company id: {}", companyId);
        return ResponseEntity.ok().body(new ExpeditionsForCompanyDTO("Expeditions found", expeditions));
    }

    @PostMapping("/allExpeditions")
    public ResponseEntity<ExpeditionsForCompanyDTO> viewAllExpeditions(@RequestBody CompanyIdDTO companyIdDTO) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.EXPEDITIONS_FOR_COMPANY_DTO);

        //STEP 1: Classic validation
        if(companyIdDTO == null) {
            logger.error("CompanyIdDTO is null");
            return errorUtils.criticalError();
        }

        int companyId = companyIdDTO.getCompanyId();

        if(companyId <= 0) {
            logger.error("Company Id is invalid: {}", companyId);
            return errorUtils.isInvalidFormat("Company Id");
        }

        //STEP 2: Spesific validations

        //STEP 3: Business Logic
        List<ExpeditionForCompanyDTO> expeditions = expeditionService.findAllExpeditions(companyId);

        if(expeditions.isEmpty()) {
            logger.error("No expeditions found for company id: {}", companyId);
            return errorUtils.notFound("Expeditions");
        }

        logger.info("Expeditions found for company id: {}", companyId);
        return ResponseEntity.ok().body(new ExpeditionsForCompanyDTO("Expeditions found", expeditions));
    }

    @PostMapping("/expeditionDetails")
    public ResponseEntity<SeatsForCompanyDTO> viewExpeditionDetails(@RequestBody ExpeditionViewByIdDTO expeditionViewById) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.SEATS_FOR_COMPANY_DTO);
        
        //STEP 1: Classic validation
        if(expeditionViewById == null) {
            logger.error("ExpeditionViewByIdDTO is null");
            return errorUtils.criticalError();
        }

        int companyId = expeditionViewById.getCompanyId();
        int expeditionId = expeditionViewById.getExpeditionId();

        if(companyId <= 0) {
            logger.error("Company Id is invalid: {}", companyId);
            return errorUtils.isInvalidFormat("Company Id");
        }
        if(expeditionId <= 0) {
            logger.error("Expedition Id is invalid: {}", expeditionId);
            return errorUtils.isInvalidFormat("Expedition Id");
        }

        //STEP 2: Spesific validations

        //STEP 3: Business Logic
        List<SeatForCompanyDTO> seats = seatService.getSeatsByExpeditionIdAndCompanyId(expeditionId, companyId);

        if(seats.isEmpty()) {
            logger.error("No seats found for expedition id: {}", expeditionId);
            return errorUtils.notFound("Seats");
        }

        logger.info("Expedition details found for expedition id: {}", expeditionId);
        return ResponseEntity.ok().body(new SeatsForCompanyDTO("Expedition details found", seats));
    }
}
