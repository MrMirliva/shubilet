package com.shubilet.expedition_service.controllers.Impl;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.shubilet.expedition_service.common.util.ErrorUtils;
import com.shubilet.expedition_service.common.util.StringUtils;
import com.shubilet.expedition_service.common.util.ValidationUtils;
import com.shubilet.expedition_service.controllers.ViewForCustomerController;
import com.shubilet.expedition_service.dataTransferObjects.requests.ExpeditionIdDTO;
import com.shubilet.expedition_service.dataTransferObjects.requests.ViewDetailsForCustomerDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.base.ExpeditionForCustomerDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.base.SeatForCustomerDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.ExpeditionsForCustomerDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.SeatsForCustomerDTO;
import com.shubilet.expedition_service.services.ExpeditionService;
import com.shubilet.expedition_service.services.SeatService;


@RestController
@RequestMapping("/api/view/customer/")
public class ViewForCustomerControllerImpl implements ViewForCustomerController {

    private static final Logger logger = LoggerFactory.getLogger(ViewForCustomerControllerImpl.class);

    private final ExpeditionService expeditionService;
    private final SeatService seatService;

    public ViewForCustomerControllerImpl(
        ExpeditionService expeditionService,
        SeatService seatService
    ) {
        this.expeditionService = expeditionService;
        this.seatService = seatService;
    }

    @PostMapping("/availableExpeditions")
    public ResponseEntity<ExpeditionsForCustomerDTO> viewAvailableExpeditions(@RequestBody ViewDetailsForCustomerDTO viewDetailsForCustomerDTO) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.EXPEDITIONS_FOR_CUSTOMER_DTO);

        //STEP 1 : Classic validation
        if(viewDetailsForCustomerDTO == null) {
            logger.error("ViewDetailsForCustomerDTO is null");
            return ResponseEntity.badRequest().body(errorUtils.criticalError());
        }

        String departureCity = viewDetailsForCustomerDTO.getDepartureCity();
        String arrivalCity = viewDetailsForCustomerDTO.getArrivalCity();
        String date = viewDetailsForCustomerDTO.getDate();

        if(StringUtils.isNullOrBlank(departureCity)) {
            logger.error("Departure City is null or blank");
            return ResponseEntity.badRequest().body(errorUtils.isNull("Departure City"));
        }

        if(StringUtils.isNullOrBlank(arrivalCity)) {
            logger.error("Arrival City is null or blank");
            return ResponseEntity.badRequest().body(errorUtils.isNull("Arrival City"));
        }

        if(StringUtils.isNullOrBlank(date)) {
            logger.error("Date is null or blank");
            return ResponseEntity.badRequest().body(errorUtils.isNull("Date"));
        }

        if(!ValidationUtils.isValidDate(date)) {
            logger.error("Date format is invalid: {}", date);
            return ResponseEntity.badRequest().body(errorUtils.isInvalidFormat("Date"));
        }

        //STEP 2 : Spesific validation
        if(StringUtils.nullSafeEquals(arrivalCity, departureCity)) {
            logger.error("Arrival City and Departure City are the same: {}", arrivalCity);
            return ResponseEntity.badRequest().body(errorUtils.sameCityError());
        }

        //STEP 3 : Business Logic
        List<ExpeditionForCustomerDTO> expeditions = expeditionService.findExpeditionsByInstantAndRoute(departureCity, arrivalCity, date);
        
        if(expeditions.isEmpty()) {
            logger.info("No expeditions found for route {} to {} on date {}", departureCity, arrivalCity, date);
            return ResponseEntity.ok(new ExpeditionsForCustomerDTO("No expeditions found", expeditions));
        }

        logger.info("Found {} expeditions for route {} to {} on date {}", expeditions.size(), departureCity, arrivalCity, date);
        return ResponseEntity.ok(new ExpeditionsForCustomerDTO("Expeditions found", expeditions));
    }

    @PostMapping("/availableSeats")
    public ResponseEntity<SeatsForCustomerDTO> viewAvailableSeats(@RequestBody ExpeditionIdDTO expeditionIdDTO) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.SEATS_FOR_CUSTOMER_DTO);

        //STEP 1 : Classic validation
        if(expeditionIdDTO == null) {
            logger.error("ExpeditionIdDTO is null");
            return ResponseEntity.badRequest().body(errorUtils.criticalError());
        }

        int expeditionId = expeditionIdDTO.getExpeditionId();

        if(expeditionId <= 0) {
            logger.error("Expedition Id is invalid: {}", expeditionId);
            return ResponseEntity.badRequest().body(errorUtils.isNull("Expedition Id"));
        }

        //STEP 2 : Spesific validation
        if(!expeditionService.expeditionExists(expeditionId)) {
            logger.error("Expedition not found for Id: {}", expeditionId);
            return ResponseEntity.badRequest().body(errorUtils.notFound("Expedition"));
        }

        //STEP 3 : Business Logic
        List<SeatForCustomerDTO> availableSeats = seatService.getAvailableSeats(expeditionId);

        logger.info("Found {} available seats for expedition Id {}", availableSeats.size(), expeditionId);
        return ResponseEntity.ok(new SeatsForCustomerDTO("Available seats found", availableSeats));
    }
    
}
