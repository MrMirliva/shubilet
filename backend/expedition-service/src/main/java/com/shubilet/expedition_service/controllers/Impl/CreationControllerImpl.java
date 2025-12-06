package com.shubilet.expedition_service.controllers.Impl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.shubilet.expedition_service.common.util.ErrorUtils;
import com.shubilet.expedition_service.common.util.StringUtils;
import com.shubilet.expedition_service.common.util.ValidationUtils;
import com.shubilet.expedition_service.controllers.CreationController;
import com.shubilet.expedition_service.dataTransferObjects.requests.ExpeditionCreationDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.MessageDTO;
import com.shubilet.expedition_service.services.CityService;
import com.shubilet.expedition_service.services.ExpeditionService;
import com.shubilet.expedition_service.services.SeatService;


@RestController
@RequestMapping("/api/expeditions")
public class CreationControllerImpl implements CreationController {

    private final CityService cityService;
    private final ExpeditionService expeditionService;
    private final SeatService seatService;

    public CreationControllerImpl(
        CityService cityService,
        ExpeditionService expeditionService,
        SeatService seatService
    ) {
        this.cityService = cityService;
        this.expeditionService = expeditionService;
        this.seatService = seatService;
    }
    
    @PostMapping("/create")
    public ResponseEntity<MessageDTO> createExpedition(@RequestBody ExpeditionCreationDTO request) {

        ///STEP 1: Classic validations
        if(request == null) {
            return ResponseEntity.badRequest().body(ErrorUtils.criticalError());
        }

        String departureCity = request.getDepartureCity();
        String arrivalCity = request.getArrivalCity();
        String date = request.getDate();
        String time = request.getTime();
        double price = request.getPrice();
        int duration = request.getDuration();
        int companyId = request.getCompanyId();
        int capacity = request.getCapacity();
        
        if(StringUtils.isNullOrBlank(arrivalCity)) {
            return ResponseEntity.badRequest().body(ErrorUtils.isNull("Arrival city"));
        }

        if(StringUtils.isNullOrBlank(departureCity)) {
            return ResponseEntity.badRequest().body(ErrorUtils.isNull("Departure city"));
        }

        if(StringUtils.isNullOrBlank(date)) {
            return ResponseEntity.badRequest().body(ErrorUtils.isNull("Date"));
        }
        
        if(StringUtils.isNullOrBlank(time)) {
            return ResponseEntity.badRequest().body(ErrorUtils.isNull("Time"));
        }

        if(!ValidationUtils.isValidBigDouble(price)) {
            return ResponseEntity.badRequest().body(ErrorUtils.isInvalidFormat("Price"));
        }

        if(duration < 0) {
            return ResponseEntity.badRequest().body(ErrorUtils.isInvalidFormat("Duration"));
        }

        if(companyId <= 0) {
            return ResponseEntity.badRequest().body(ErrorUtils.unauthorized());
        }

        if(capacity <= 0) {
            return ResponseEntity.badRequest().body(ErrorUtils.isInvalidFormat("Capacity"));
        }

        //STEP 2: Spesific validations
        if(StringUtils.nullSafeEquals(arrivalCity, departureCity)) {
            return ResponseEntity.badRequest().body(ErrorUtils.sameCityError());
        }

        if(!cityService.cityExists(arrivalCity)) {
            return ResponseEntity.badRequest().body(ErrorUtils.notFound("Arrival city"));
        }

        if(!cityService.cityExists(departureCity)) {
            return ResponseEntity.badRequest().body(ErrorUtils.notFound("Departure city"));
        }

        if(!ValidationUtils.isValidDate(date)) {
            return ResponseEntity.badRequest().body(ErrorUtils.isInvalidFormat("Date"));
        }

        if(!ValidationUtils.isValidTime(time)) {
            return ResponseEntity.badRequest().body(ErrorUtils.isInvalidFormat("Time"));
        }

        if(capacity > 1000) {
            return ResponseEntity.badRequest().body(ErrorUtils.isInvalidFormat("Capacity"));
        }

        //STEP 3: Generation logic 

        int expeditionId = expeditionService.createExpedition(
            companyId,
            departureCity,
            arrivalCity,
            date,
            time,
            capacity,
            price,
            duration
        );

        seatService.generateSeats(expeditionId, capacity);

        return ResponseEntity.ok(new MessageDTO("Expedition created successfully."));
    }
    
}
