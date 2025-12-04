package com.shubilet.expedition_service.controllers.Impl;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.shubilet.expedition_service.common.util.ErrorUtils;
import com.shubilet.expedition_service.common.util.StringUtils;
import com.shubilet.expedition_service.common.util.ValidationUtils;
import com.shubilet.expedition_service.controllers.ViewForCustomerController;
import com.shubilet.expedition_service.dataTransferObjects.requests.ViewDetailsForCustomerDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.ExpeditionForCustomerDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.SeatForCustomerDTO;
import com.shubilet.expedition_service.services.ExpeditionService;
import com.shubilet.expedition_service.services.SeatService;


@RestController
@RequestMapping("/api/view/customer/")
public class ViewForCustomerControllerImpl implements ViewForCustomerController {

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
    public ResponseEntity<?> viewAvailableExpeditions(@RequestBody ViewDetailsForCustomerDTO viewDetailsForCustomerDTO) {

        //STEP 1 : Classic validation
        if(viewDetailsForCustomerDTO == null) {
            return ResponseEntity.badRequest().body(ErrorUtils.criticalError());
        }

        String departureCity = viewDetailsForCustomerDTO.getDepartureCity();
        String arrivalCity = viewDetailsForCustomerDTO.getArrivalCity();
        String date = viewDetailsForCustomerDTO.getDate();

        if(StringUtils.isNullOrBlank(departureCity)) {
            return ResponseEntity.badRequest().body(ErrorUtils.isNull("Departure City"));
        }

        if(StringUtils.isNullOrBlank(arrivalCity)) {
            return ResponseEntity.badRequest().body(ErrorUtils.isNull("Arrival City"));
        }

        if(StringUtils.isNullOrBlank(date)) {
            return ResponseEntity.badRequest().body(ErrorUtils.isNull("Date"));
        }

        if(!ValidationUtils.isValidDate(date)) {
            return ResponseEntity.badRequest().body(ErrorUtils.isInvalidFormat("Date"));
        }

        //STEP 2 : Spesific validation
        if(StringUtils.nullSafeEquals(arrivalCity, departureCity)) {
            return ResponseEntity.badRequest().body(ErrorUtils.sameCityError());
        }

        //STEP 3 : Business Logic
        List<ExpeditionForCustomerDTO> expeditions = expeditionService.findExpeditionsByInstantAndRoute(departureCity, arrivalCity, date);
        
        return ResponseEntity.ok(expeditions);
    }

    @PostMapping("/availableSeats")
    public ResponseEntity<?> viewAvailableSeats(@RequestBody int expeditionId) {
        //STEP 1 : Classic validation
        if(expeditionId <= 0) {
            return ResponseEntity.badRequest().body(ErrorUtils.isNull("Expedition Id"));
        }

        //STEP 2 : Spesific validation
        if(!expeditionService.doesExpeditionExist(expeditionId)) {
            return ResponseEntity.badRequest().body(ErrorUtils.notFound("Expedition"));
        }

        //STEP 3 : Business Logic
        List<SeatForCustomerDTO> availableSeats = seatService.getAvailableSeats(expeditionId);

        return ResponseEntity.ok(availableSeats);
    }
    
}
