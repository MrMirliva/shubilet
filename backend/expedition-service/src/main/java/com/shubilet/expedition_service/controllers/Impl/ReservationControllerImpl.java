package com.shubilet.expedition_service.controllers.Impl;

import java.util.LinkedHashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.shubilet.expedition_service.common.constants.ServiceURLs;
import com.shubilet.expedition_service.common.enums.forReservation.ExpeditionStatus;
import com.shubilet.expedition_service.common.enums.forReservation.SeatStatus;
import com.shubilet.expedition_service.common.util.ErrorUtils;
import com.shubilet.expedition_service.common.util.StringUtils;
import com.shubilet.expedition_service.controllers.RezervationController;
import com.shubilet.expedition_service.dataTransferObjects.internal.requests.CardIdDTORequest;
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
public class ReservationControllerImpl implements RezervationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationControllerImpl.class); 

    private final ExpeditionService expeditionService;
    private final TicketService ticketService;
    private final SeatService seatService;
    private final RestTemplate restTemplate;

    public ReservationControllerImpl(
        ExpeditionService expeditionService,
        TicketService ticketService,
        SeatService seatService,
        RestTemplate restTemplate
    ) {
        this.expeditionService = expeditionService;
        this.ticketService = ticketService;
        this.seatService = seatService;
        this.restTemplate = restTemplate;
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

        // START: Payment Service communication - Card Active Check
        String requestId = UUID.randomUUID().toString();
        logger.info("Start Login (requestId={})", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CardIdDTORequest> cardRequest = new HttpEntity<>(new CardIdDTORequest(cardId), headers);

        ResponseEntity<Object> cardResponse = restTemplate.exchange(
            ServiceURLs.PAYMENT_SERVICE_CHECK_ACTIVATE,
            HttpMethod.POST,
            cardRequest,
            Object.class
        );

        boolean isCardActive = false;

        /// BURASI GEÇİCİ BİR KONTROL MEKANİZMASI. PAYMENT SERVICE'TEN GELEN CEVABA GÖRE DÜZENLENECEK. ŞİMDİLİK OBJECT OLARAK VERİLMİŞ.
        /// FLAG START
        if(!cardResponse.getStatusCode().is2xxSuccessful()) {
            logger.error("Card is not active or error occurred. Card ID: {}, Response Status: {}", cardId, cardResponse.getStatusCode());

            if(cardResponse.getBody() != null) {
                logger.error("Card Service Response Body: {}", cardResponse.getBody().toString());
                Object responseBody = cardResponse.getBody();
                logger.error("Response Body Class: {}", responseBody.getClass().getName());

                if(responseBody instanceof LinkedHashMap) {
                    LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) responseBody;
                    Object messageObj = map.get("message");
                    if(messageObj instanceof String) {
                        logger.error("Card Service Error Message: {}", (String) messageObj);
                        return errorUtils.customError(cardResponse, (String) messageObj);
                    } else {
                        logger.error("Unexpected 'message' field type in Card Service response: {}", messageObj.getClass().getName());
                        return errorUtils.criticalError();
                    }
                }
                else {
                    logger.error("Unexpected response body type from Card Service: {}", responseBody.getClass().getName());
                    return errorUtils.criticalError();
                }
            }
            return errorUtils.isInvalidFormat("Card is not active or error occurred.");
        }
        else {
            Object resposeBody = cardResponse.getBody();

            if(resposeBody instanceof Boolean) {
                isCardActive = (Boolean) resposeBody;

                if(!isCardActive) {
                    logger.error("Card is not active. Card ID: {}", cardId);
                    return errorUtils.cardNotActive();
                }
            } else {
                logger.error("Unexpected response body type from Card Service: {}", resposeBody.getClass().getName());
                return errorUtils.criticalError();
            }
        }
        /// FLAG END
        
        logger.info("Card is active. Card ID: {}", cardId); 
        // END: Payment Service communication - Card Active Check

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
