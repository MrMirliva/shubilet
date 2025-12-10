package com.shubilet.expedition_service.controllers.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shubilet.expedition_service.common.enums.forReservation.ExpeditionStatus;
import com.shubilet.expedition_service.common.enums.forReservation.SeatStatus;
import com.shubilet.expedition_service.common.util.ErrorUtils;
import com.shubilet.expedition_service.common.util.StringUtils;
import com.shubilet.expedition_service.controllers.RezervationController;
import com.shubilet.expedition_service.dataTransferObjects.requests.BuyTicketDTO;
import com.shubilet.expedition_service.dataTransferObjects.requests.CustomerIdDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.base.TicketDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.CardsDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.middle.TicketInfoDTO;
import com.shubilet.expedition_service.services.ExpeditionService;
import com.shubilet.expedition_service.services.SeatService;
import com.shubilet.expedition_service.services.TicketService;

@RestController
@RequestMapping("/api/reservation")
public class RezervaitonControllerImpl implements RezervationController {

    private static final Logger logger = LoggerFactory.getLogger(RezervaitonControllerImpl.class); 

    private final ExpeditionService expeditionService;
    private final TicketService ticketService;
    private final SeatService seatService;

    public RezervaitonControllerImpl(
        ExpeditionService expeditionService,
        TicketService ticketService,
        SeatService seatService
    ) {
        this.expeditionService = expeditionService;
        this.ticketService = ticketService;
        this.seatService = seatService;
    }

    @PostMapping("/buy_ticket")
    public ResponseEntity<TicketInfoDTO> buyTicket(@RequestBody BuyTicketDTO buyTicketDTO) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.TICKET_INFO_DTO);

        //STEP 1: Classic validation
        if(buyTicketDTO == null) {
            logger.error("BuyTicketDTO is null");
            return errorUtils.criticalError();
        }

        int customerId = buyTicketDTO.getCustomerId();
        int expeditionId = buyTicketDTO.getExpeditionId();
        int seatNo = buyTicketDTO.getSeatNo();
        int cardId = buyTicketDTO.getCardId();

        if(customerId <= 0) {
            logger.error("Invalid Customer ID: {}", customerId);
            return errorUtils.isInvalidFormat(String.valueOf(customerId));
        }

        if(expeditionId <= 0) {
            logger.error("Invalid Expedition ID: {}", expeditionId);
            return errorUtils.isInvalidFormat(String.valueOf(expeditionId));
        }

        if(seatNo <= 0) {
            logger.error("Invalid Seat Number: {}", seatNo);
            return errorUtils.isInvalidFormat(String.valueOf(seatNo));
        }

        if(cardId <= 0) {
            logger.error("Invalid Card ID: {}", cardId);
            return errorUtils.isInvalidFormat(String.valueOf(cardId));
        }
        //STEP 2: Spesific validation

        if(!expeditionService.expeditionExists(expeditionId)) {
            logger.error("Expedition not found: {}", expeditionId);
            return errorUtils.notFound("Expedition ID: " + expeditionId);
        }

        if(!seatService.seatExist(expeditionId, seatNo)) {
            logger.error("Seat not found. Expedition ID: {}, Seat No: {}", expeditionId, seatNo);
            return errorUtils.notFound("Seat No: " + seatNo + " for Expedition ID: " + expeditionId);
        }

        SeatStatus seatStatus = seatService.canBeReserved(expeditionId, seatNo);
        ExpeditionStatus expeditionStatus = expeditionService.canBeReserved(expeditionId);

        if(seatStatus != SeatStatus.SUCCESS) {
            if(seatStatus == SeatStatus.NOT_FOUND) {
                logger.error("Seat does not exist. Expedition ID: {}, Seat No: {}", expeditionId, seatNo);
                return errorUtils.notFound("Seat No: " + seatNo + " for Expedition ID: " + expeditionId);
            }

            if(seatStatus == SeatStatus.ALREADY_BOOKED) {
                logger.error("Seat already booked. Expedition ID: {}, Seat No: {}", expeditionId, seatNo);
                return errorUtils.alreadyBooked("Seat No: " + seatNo + " for Expedition ID: " + expeditionId);
            }

            logger.error("Failed to book seat. Expedition ID: {}, Seat No: {}, Status: {}", expeditionId, seatNo, seatStatus);
            return errorUtils.criticalError();
        }

        if(expeditionStatus != ExpeditionStatus.SUCCESS) {
            if(expeditionStatus == ExpeditionStatus.NOT_FOUND) {
                logger.error("Expedition does not exist: {}", expeditionId);
                return errorUtils.notFound("Expedition ID: " + expeditionId);
            }

            if(expeditionStatus == ExpeditionStatus.INVALID_TIME) {
                logger.error("Cannot book seat for past expedition time: {}", expeditionId);
                return errorUtils.isInvalidFormat("Cannot book seat for past expedition time.");
            }

            if(expeditionStatus == ExpeditionStatus.ALREADY_BOOKED) {
                logger.error("No available seats in expedition: {}", expeditionId);
                return errorUtils.alreadyBooked("Expedition ID: " + expeditionId);
            }

            logger.error("Failed to update expedition after booking seat. Expedition ID: {}, Status: {}", expeditionId, expeditionStatus);
            return errorUtils.criticalError();
        }

        ///TODO: CardId kontrolü yapılacak, Eureka servis ile Payment-Service'e istek atılacak

        //STEP 3: Logical processing

        ///TODO: Ödeme işlemi yapılacak, Eureka servis ile Payment-Service'e istek atılacak
        int paymentId = 86952;
        
        int seatId = seatService.bookSeat(expeditionId, customerId, seatNo);
        if(seatId == -1) {
            logger.error("Failed to book seat. Expedition ID: {}, Seat No: {}", expeditionId, seatNo);
            return errorUtils.criticalError();
        }

        boolean expeditionSuccess = expeditionService.bookSeat(expeditionId);
        if(!expeditionSuccess) {
            logger.error("Failed to update expedition after booking seat. Expedition ID: {}", expeditionId);
            return errorUtils.criticalError();
        }

        String ticketPNR = ticketService.generateTicket(paymentId, seatId, customerId);
        if(StringUtils.isNullOrBlank(ticketPNR)) {
            logger.error("Failed to create ticket. Expedition ID: {}, Seat No: {}", expeditionId, seatNo);
            return errorUtils.criticalError();
        }

        TicketDTO ticketDTO = ticketService.getTicketDetails(ticketPNR);

        return ResponseEntity.ok(new TicketInfoDTO(ticketDTO, "Ticket booked successfully."));
    }

    ///TODO: Daha implemente edilmedi.
    @PostMapping("/view_cards")
    public ResponseEntity<CardsDTO> viewCards(@RequestBody CustomerIdDTO customerIdDTO) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.CARDS_DTO);

        //STEP 1: Classic validation
        if(customerIdDTO == null) {
            logger.error("CustomerIdDTO is null");
            return errorUtils.criticalError();
        }

        int customerId = customerIdDTO.getCustomerId();

        if(customerId <= 0) {
            logger.error("Invalid Customer ID: {}", customerId);
            return errorUtils.isInvalidFormat(String.valueOf(customerId));
        }

        //STEP 2: Spesific validation

        //STEP 3: Logical processing

        ///TODO: Müşteriye ait kartlar getirilecek, Eureka servis ile Payment-Service'e istek atılacak.
        return null;
    }
    
}
