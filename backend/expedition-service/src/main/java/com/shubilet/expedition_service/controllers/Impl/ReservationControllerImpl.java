package com.shubilet.expedition_service.controllers.Impl;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
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
import com.shubilet.expedition_service.dataTransferObjects.internal.requests.CustomerIdRequestDTO;
import com.shubilet.expedition_service.dataTransferObjects.internal.requests.TicketPaymentRequestDTO;
import com.shubilet.expedition_service.dataTransferObjects.internal.responses.CardSummaryDTO;
import com.shubilet.expedition_service.dataTransferObjects.internal.responses.TicketPaymentResponseDTO;
import com.shubilet.expedition_service.dataTransferObjects.requests.BuyTicketDTO;
import com.shubilet.expedition_service.dataTransferObjects.requests.CustomerIdDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.base.CardDTO;
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
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CardIdDTORequest> cardRequest = new HttpEntity<>(new CardIdDTORequest(cardId), headers);

        boolean isCardActive;

        try {
            // IMPORTANT: Boolean.class not String.class
            ResponseEntity<String> cardResponse = restTemplate.exchange(
                ServiceURLs.PAYMENT_SERVICE_CHECK_ACTIVATE,
                HttpMethod.POST,
                cardRequest,
                String.class
            );

            // If 2xx, body will be "true"/"false" (JSON boolean converted to string)
            if (!cardResponse.getStatusCode().is2xxSuccessful()) {
                // Normally won't reach here (RestTemplate throws exception on 4xx),
                // but still safe to check.
                return errorUtils.criticalError();
            }

            String body = cardResponse.getBody(); // "true" veya "false"
            isCardActive = Boolean.parseBoolean(body);

        } catch (HttpStatusCodeException ex) {
            // Falls here when 4xx/5xx occurs and body is usually {"message":"..."}
            String errorBody = ex.getResponseBodyAsString();

            // Here you have two options:
            // A) Directly consider "card not active" and proceed
            // B) Parse the message from the body and return it to the user

            // Simple and robust:
            logger.error("Card active check failed. status={}, body={}", ex.getStatusCode(), errorBody);
            return errorUtils.cardNotActive();

        } catch (Exception ex) {
            logger.error("Card active check unexpected error", ex);
            return errorUtils.criticalError();
        }

        if (!isCardActive) {
            logger.error("Card is not active. Card ID: {}", cardId);
            return errorUtils.cardNotActive();
        }

        logger.info("Card is active. Card ID: {}", cardId);

        // END: Payment Service communication - Card Active Check

        //STEP 3: Logical processing
        int amount = expeditionService.getExpeditionPrice(expeditionId);

        //START: Payment Service communication - Make Payment
        HttpEntity<TicketPaymentRequestDTO> paymentRequest = new HttpEntity<>(
            new TicketPaymentRequestDTO(cardId, String.valueOf(amount), customerId),
            headers
        );

        int paymentId;

        try {
            ResponseEntity<TicketPaymentResponseDTO> paymentResponse = restTemplate.exchange(
                ServiceURLs.PAYMENT_SERVICE_MAKE_PAYMENT,
                HttpMethod.POST,
                paymentRequest,
                TicketPaymentResponseDTO.class
            );

            if (!paymentResponse.getStatusCode().is2xxSuccessful()) {
                logger.error("Payment failed (non-2xx). Expedition ID: {}, Customer ID: {}, Seat No: {}, Status: {}",
                    expeditionId, customerId, seatNo, paymentResponse.getStatusCode());
                return errorUtils.criticalError();
            }

            TicketPaymentResponseDTO body = paymentResponse.getBody();
            if (body == null) {
                logger.error("Payment response body is null. Expedition ID: {}, Customer ID: {}, Seat No: {}",
                    expeditionId, customerId, seatNo);
                return errorUtils.criticalError();
            }

            paymentId = body.getPaymentId();
            if (paymentId <= 0) {
                logger.error("PaymentId is missing/invalid. Expedition ID: {}, Customer ID: {}, Seat No: {}, paymentId: {}",
                    expeditionId, customerId, seatNo, paymentId);
                return errorUtils.criticalError();
            }

            logger.info("Payment successful. Payment ID: {}, Expedition ID: {}, Customer ID: {}, Seat No: {}",
                paymentId, expeditionId, customerId, seatNo
            );
        }catch (HttpStatusCodeException ex) {
            logger.error("Payment failed. Expedition ID: {}, Customer ID: {}, Seat No: {}, Status: {}, Body: {}",
                expeditionId, customerId, seatNo, ex.getStatusCode(), ex.getResponseBodyAsString());

            ResponseEntity<Object> dummy = ResponseEntity.status(ex.getStatusCode()).build();
            return errorUtils.customError(dummy, "Payment failed");
        } catch (Exception ex) {
            logger.error("Payment unexpected error", ex);
            return errorUtils.criticalError();
        }
        //END: Payment Service communication - Make Payment
        
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

    @PostMapping("/view_cards")
    public ResponseEntity<CardsDTO> viewCards(@RequestBody CustomerIdDTO customerIdDTO) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.CARDS_DTO);

        // STEP 1: Classic validation
        if (customerIdDTO == null) {
            logger.error("CustomerIdDTO is null");
            return errorUtils.criticalError();
        }

        int customerId = customerIdDTO.getCustomerId();
        if (customerId <= 0) {
            logger.error("Invalid Customer ID: {}", customerId);
            return errorUtils.isInvalidFormat("Customer Id");
        }

        // STEP 2: Specific validation
        // (none – intentional)

        // STEP 3: Business Logic (Payment-Service call)
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CustomerIdRequestDTO> request =
                new HttpEntity<>(new CustomerIdRequestDTO(customerId), headers);

            ResponseEntity<CardSummaryDTO[]> response = restTemplate.exchange(
                ServiceURLs.PAYMENT_SERVICE_CUSTOMER_CARDS,
                HttpMethod.POST,
                request,
                CardSummaryDTO[].class
            );

            CardSummaryDTO[] body = response.getBody();

            if (body == null || body.length == 0) {
                logger.info("No cards found for customerId={}", customerId);
                return errorUtils.notFound("Cards");
            }

            // Map Payment DTO → Expedition DTO
            List<CardDTO> cards = Arrays.stream(body)
                .map(c -> new CardDTO(
                    Integer.parseInt(c.getCardId()),   // String → int
                    c.getLast4Digits(),
                    c.getExpirationMonth(),
                    c.getExpirationYear()
                ))
                .toList();

            logger.info("Cards found for customerId={}", customerId);
            return ResponseEntity.ok(new CardsDTO("Cards found", cards));

        } catch (HttpStatusCodeException ex) {
            // Payment-service 4xx/5xx
            logger.error(
                "Payment-service error while fetching cards. customerId={}, status={}, body={}",
                customerId,
                ex.getStatusCode(),
                ex.getResponseBodyAsString()
            );

            ResponseEntity<Object> dummy =
                ResponseEntity.status(ex.getStatusCode()).build();

            return errorUtils.customError(dummy, "Cards could not be fetched");

        } catch (Exception ex) {
            logger.error("Unexpected error while fetching cards", ex);
            return errorUtils.criticalError();
        }
    }
    
}
