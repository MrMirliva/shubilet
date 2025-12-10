package com.shubilet.expedition_service.controllers.Impl;

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
import com.shubilet.expedition_service.controllers.CreationController;
import com.shubilet.expedition_service.dataTransferObjects.requests.ExpeditionCreationDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.message.MessageDTO;
import com.shubilet.expedition_service.services.CityService;
import com.shubilet.expedition_service.services.ExpeditionService;
import com.shubilet.expedition_service.services.SeatService;


@RestController
@RequestMapping("/api/expeditions")
public class CreationControllerImpl implements CreationController {
    private static final Logger logger = LoggerFactory.getLogger(CreationControllerImpl.class);

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
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.MESSAGE_DTO);

        ///STEP 1: Classic validations
        if(request == null) {
            logger.error("Expedition creation request is null");
            return errorUtils.criticalError();
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
            logger.error("Arrival city is null or blank");
            return errorUtils.isNull("Arrival city");
        }

        if(StringUtils.isNullOrBlank(departureCity)) {
            logger.error("Departure city is null or blank");
            return errorUtils.isNull("Departure city");
        }

        if(StringUtils.isNullOrBlank(date)) {
            logger.error("Date is null or blank");
            return errorUtils.isNull("Date");
        }
        
        if(StringUtils.isNullOrBlank(time)) {
            logger.error("Time is null or blank");
            return errorUtils.isNull("Time");
        }

        if(!ValidationUtils.isValidBigDouble(price)) {
            logger.error("Price is invalid: {}", price);
            return errorUtils.isInvalidFormat("Price");
        }

        if(duration < 0) {
            logger.error("Duration is invalid: {}", duration);
            return errorUtils.isInvalidFormat("Duration");
        }

        if(companyId <= 0) {
            logger.error("Company Id is invalid: {}", companyId);
            return errorUtils.unauthorized();
        }

        if(capacity <= 0) {
            logger.error("Capacity is invalid: {}", capacity);
            return errorUtils.isInvalidFormat("Capacity");
        }

        //STEP 2: Spesific validations
        if(StringUtils.nullSafeEquals(arrivalCity, departureCity)) {
            logger.error("Arrival city and Departure city are the same: {}", arrivalCity);
            return errorUtils.sameCityError();
        }

        if(!cityService.cityExists(arrivalCity)) {
            logger.error("Arrival city not found: {}", arrivalCity);
            return errorUtils.notFound("Arrival city");
        }

        if(!cityService.cityExists(departureCity)) {
            logger.error("Departure city not found: {}", departureCity);
            return errorUtils.notFound("Departure city");
        }

        if(!ValidationUtils.isValidDate(date)) {
            logger.error("Date format is invalid: {}", date);
            return errorUtils.isInvalidFormat("Date");
        }

        if(!ValidationUtils.isValidTime(time)) {
            logger.error("Time format is invalid: {}", time);
            return errorUtils.isInvalidFormat("Time");
        }

        if(capacity > 1000) {
            logger.error("Capacity exceeds maximum limit: {}", capacity);
            return errorUtils.isInvalidFormat("Capacity");
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

        if(expeditionId == -1) {
            logger.error("Failed to create expedition due to invalid city IDs.");
            return errorUtils.criticalError();
        }

        seatService.generateSeats(expeditionId, capacity);

        logger.info("Expedition created successfully with Id: {}", expeditionId);
        return ResponseEntity.ok(new MessageDTO("Expedition created successfully."));
    }
    
}
