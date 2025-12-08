package com.shubilet.expedition_service.controllers.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shubilet.expedition_service.common.enums.BookStatus;
import com.shubilet.expedition_service.common.util.ErrorUtils;
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
@RequestMapping("/api/rezervation/")
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
            return ResponseEntity.badRequest().body(errorUtils.criticalError());
        }

        int customerId = buyTicketDTO.getCustomerId();
        int expeditionId = buyTicketDTO.getExpeditionId();
        int seatNo = buyTicketDTO.getSeatNo();
        int cardId = buyTicketDTO.getCardId();

        if(customerId <= 0) {
            logger.error("Invalid Customer ID: {}", customerId);
            return ResponseEntity.badRequest().body(errorUtils.isInvalidFormat(String.valueOf(customerId)));
        }

        if(expeditionId <= 0) {
            logger.error("Invalid Expedition ID: {}", expeditionId);
            return ResponseEntity.badRequest().body(errorUtils.isInvalidFormat(String.valueOf(expeditionId)));
        }

        if(seatNo <= 0) {
            logger.error("Invalid Seat Number: {}", seatNo);
            return ResponseEntity.badRequest().body(errorUtils.isInvalidFormat(String.valueOf(seatNo)));
        }

        if(cardId <= 0) {
            logger.error("Invalid Card ID: {}", cardId);
            return ResponseEntity.badRequest().body(errorUtils.isInvalidFormat(String.valueOf(cardId)));
        }
        //STEP 2: Spesific validation

        if(!expeditionService.expeditionExists(expeditionId)) {
            logger.error("Expedition not found: {}", expeditionId);
            return ResponseEntity.badRequest().body(errorUtils.notFound("Expedition ID: " + expeditionId));
        }

        if(seatService.seatExist(expeditionId, seatNo)) {
            logger.error("Seat already booked. Expedition ID: {}, Seat No: {}", expeditionId, seatNo);
            return ResponseEntity.badRequest().body(errorUtils.alreadyExists("Seat No: " + seatNo + " for Expedition ID: " + expeditionId));
        }

        ///TODO: CardId kontrolü yapılacak, Eureka servis ile Payment-Service'e istek atılacak

        
        //STEP 3: Logical processing

        ///TODO: Bilet oluşturma işlemi yapılacak, Eureka servis ile Payment-Service'e istek atılacak
        
        BookStatus bookStatus = seatService.bookSeat(expeditionId, seatNo);

        if(!bookStatus.isValid()) {

            if(bookStatus == BookStatus.SEAT_NOT_EXISTS) {
                logger.error("Seat does not exist. Expedition ID: {}, Seat No: {}", expeditionId, seatNo);
                return ResponseEntity.status(bookStatus.getHttpStatus()).body(errorUtils.notFound("Seat No: " + seatNo + " for Expedition ID: " + expeditionId));
            }

            if(bookStatus == BookStatus.ALREADY_BOOKED) {
                logger.error("Seat already booked. Expedition ID: {}, Seat No: {}", expeditionId, seatNo);
                return ResponseEntity.status(bookStatus.getHttpStatus()).body(errorUtils.alreadyExists("Seat No: " + seatNo + " for Expedition ID: " + expeditionId));
            }

            logger.error("Failed to book seat. Expedition ID: {}, Seat No: {}, Status: {}", expeditionId, seatNo, bookStatus);
            return ResponseEntity.status(bookStatus.getHttpStatus()).body(errorUtils.criticalError());
        }

        TicketDTO ticketDTO = ticketService.getTicketDetails(expeditionId, seatNo);

        return ResponseEntity.ok(new TicketInfoDTO(ticketDTO, "Ticket booked successfully."));
    }

    public ResponseEntity<CardsDTO> viewCards(CustomerIdDTO customerIdDTO) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.CARDS_DTO);
        //STEP 1: Classic validation
        if(customerIdDTO == null) {
            logger.error("CustomerIdDTO is null");
            return ResponseEntity.badRequest().body(errorUtils.criticalError());
        }

        int customerId = customerIdDTO.getCustomerId();

        if(customerId <= 0) {
            logger.error("Invalid Customer ID: {}", customerId);
            return ResponseEntity.badRequest().body(errorUtils.isInvalidFormat(String.valueOf(customerId)));
        }

        //STEP 2: Spesific validation

        //STEP 3: Logical processing

        ///TODO: Müşteriye ait kartlar getirilecek, Eureka servis ile Payment-Service'e istek atılacak.
        return null;
    }
    
}
