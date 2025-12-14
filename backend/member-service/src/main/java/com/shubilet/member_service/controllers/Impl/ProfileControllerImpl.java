package com.shubilet.member_service.controllers.Impl;

import com.shubilet.member_service.common.enums.Gender;
import com.shubilet.member_service.common.util.StringUtils;
import com.shubilet.member_service.common.util.ValidationUtils;
import com.shubilet.member_service.dataTransferObjects.requests.FavoriteCompanyDeletionDTO;
import com.shubilet.member_service.dataTransferObjects.requests.resourceDTOs.CardCreationDTO;
import com.shubilet.member_service.dataTransferObjects.requests.MemberAttributeChangeDTO;
import com.shubilet.member_service.dataTransferObjects.requests.FavoriteCompanyAdditionDTO;
import com.shubilet.member_service.dataTransferObjects.requests.resourceDTOs.CardDeletionDTO;
import com.shubilet.member_service.dataTransferObjects.responses.MessageDTO;
import com.shubilet.member_service.models.FavoriteCompany;
import com.shubilet.member_service.services.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
@RequestMapping("/api/customer/profile/edit")
public class ProfileControllerImpl {
    private Logger logger = LoggerFactory.getLogger(ProfileControllerImpl.class);
    private ProfileService profileService;
    private RestTemplate restTemplate;

    ProfileControllerImpl(RestTemplate restTemplate, ProfileService profileService) {
        this.restTemplate = restTemplate;
        this.profileService = profileService;
    }

    @PostMapping("/name")
    public ResponseEntity<MessageDTO> editCustomerProfileName(@RequestBody MemberAttributeChangeDTO memberAttributeChangeDTO) {
        // DTO Existence Check
        if (memberAttributeChangeDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }

        // Attributes Null or Blank Check
        if (StringUtils.isNullOrBlank(memberAttributeChangeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Attribute can't be empty"));

        }

        // Validation Check
        if (memberAttributeChangeDTO.getMemberId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Customer Id"));
        }

        if (!profileService.isCustomerExists(memberAttributeChangeDTO.getMemberId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Customer not Found"));
        }

        if (!profileService.editName(memberAttributeChangeDTO.getMemberId(), memberAttributeChangeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Critical Error"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Success"));
    }

    @PostMapping("/surname")
    public ResponseEntity<MessageDTO> editCustomerProfileSurname(@RequestBody MemberAttributeChangeDTO memberAttributeChangeDTO) {
        // DTO Existence Check
        if (memberAttributeChangeDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }

        // Attributes Null or Blank Check
        if (StringUtils.isNullOrBlank(memberAttributeChangeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Attribute can't be empty"));
        }

        // Validation Check
        if (memberAttributeChangeDTO.getMemberId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Customer Id"));
        }

        if (!profileService.isCustomerExists(memberAttributeChangeDTO.getMemberId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Customer not Found"));
        }

        if (!profileService.editSurname(memberAttributeChangeDTO.getMemberId(), memberAttributeChangeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Critical Error"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Success"));
    }

    @PostMapping("/gender")
    public ResponseEntity<MessageDTO> editCustomerProfileGender(@RequestBody MemberAttributeChangeDTO memberAttributeChangeDTO) {
        // DTO Existence Check
        if (memberAttributeChangeDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }

        // Attributes Null or Blank Check
        if (StringUtils.isNullOrBlank(memberAttributeChangeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Attribute can't be empty"));

        }

        // Validation Check
        if (memberAttributeChangeDTO.getMemberId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Customer Id"));
        }
        if (!ValidationUtils.isValidGender(memberAttributeChangeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Gender Type"));
        }

        if (!profileService.isCustomerExists(memberAttributeChangeDTO.getMemberId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Customer not Found"));
        }

        if (!profileService.editGender(memberAttributeChangeDTO.getMemberId(), Gender.fromValue(memberAttributeChangeDTO.getAttribute()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Critical Error"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Success"));
    }

    @PostMapping("/email")
    public ResponseEntity<MessageDTO> editCustomerProfileEmail(@RequestBody MemberAttributeChangeDTO memberAttributeChangeDTO) {
        // DTO Existence Check
        if (memberAttributeChangeDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }

        // Attributes Null or Blank Check
        if (StringUtils.isNullOrBlank(memberAttributeChangeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Attribute can't be empty"));

        }

        // Validation Check
        if (memberAttributeChangeDTO.getMemberId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Customer Id"));
        }
        if (!ValidationUtils.isValidEmail(memberAttributeChangeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Email Address"));
        }

        if (!profileService.isCustomerExists(memberAttributeChangeDTO.getMemberId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Customer not Found"));
        }

        if (!profileService.editEmail(memberAttributeChangeDTO.getMemberId(), memberAttributeChangeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Critical Error"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Success"));
    }

    @PostMapping("/password")
    public ResponseEntity<MessageDTO> editCustomerProfilePassword(@RequestBody MemberAttributeChangeDTO memberAttributeChangeDTO) {
        // DTO Existence Check
        if (memberAttributeChangeDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }

        // Attributes Null or Blank Check
        if (StringUtils.isNullOrBlank(memberAttributeChangeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Attribute can't be empty"));

        }

        // Validation Check
        if (memberAttributeChangeDTO.getMemberId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Customer Id"));
        }
        if (!ValidationUtils.isValidPassword(memberAttributeChangeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Password"));
        }

        if (!profileService.isCustomerExists(memberAttributeChangeDTO.getMemberId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Customer not Found"));
        }

        if (!profileService.editPassword(memberAttributeChangeDTO.getMemberId(), memberAttributeChangeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Critical Error"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Success"));
    }

    @PostMapping("/favoriteCompany/add")
    public ResponseEntity<MessageDTO> customerProfileAddFavoriteCompany(@RequestBody FavoriteCompanyAdditionDTO favoriteCompanyAdditionDTO) {
        // DTO Existence Check
        if (favoriteCompanyAdditionDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }

        // Validation Check
        if (favoriteCompanyAdditionDTO.getCustomerId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Customer Id"));
        }
        if (favoriteCompanyAdditionDTO.getCompanyId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Company Id"));
        }

        if (!profileService.isCustomerExists(favoriteCompanyAdditionDTO.getCustomerId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Customer not Found with Given ID"));
        }
        if (!profileService.isCompanyExists(favoriteCompanyAdditionDTO.getCompanyId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Company not Found with Given ID"));
        }
        int customerId = favoriteCompanyAdditionDTO.getCustomerId();
        int companyId = favoriteCompanyAdditionDTO.getCompanyId();
        FavoriteCompany favoriteCompany = new FavoriteCompany(customerId, companyId);

        if (!profileService.addFavoriteCompany(favoriteCompany)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Critical Error"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Successfully Added"));
    }

    @PostMapping("/favoriteCompany/delete")
    public ResponseEntity<MessageDTO> customerProfileDeleteFavoriteCompany(@RequestBody FavoriteCompanyDeletionDTO favoriteCompanyDeletionDTO) {
        // DTO Existence Check
        if (favoriteCompanyDeletionDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }

        // Validation Check
        if (favoriteCompanyDeletionDTO.getRelationId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Relation Id"));
        }

        if (!profileService.isRelationExists(favoriteCompanyDeletionDTO.getRelationId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Relation not Found with Given ID"));
        }

        if (!profileService.deleteFavoriteCompany(favoriteCompanyDeletionDTO.getRelationId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Critical Error"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Successfully Deleted Favorite Company"));
    }

    @PostMapping("/card/add")
    public ResponseEntity<MessageDTO> customerProfileAddCard(@RequestBody CardCreationDTO cardCreationDTO) {
        // DTO Existence Check
        if (cardCreationDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }

        // Attributes Null or Blank Check
        if (StringUtils.isNullOrBlank(cardCreationDTO.getCardHolderName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Card Holder Name can't be empty"));
        }

        if (StringUtils.isNullOrBlank(cardCreationDTO.getCardNumber())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Card No Can't be empty"));
        }

        if (StringUtils.isNullOrBlank(cardCreationDTO.getExpirationYear())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Card Expiration Year can't be empty"));
        }
        if (StringUtils.isNullOrBlank(cardCreationDTO.getExpirationMonth())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Card Expiration Month can't be empty"));
        }


        if (StringUtils.isNullOrBlank(cardCreationDTO.getCvc())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Card CVC can't be empty"));
        }

        // Validation Check
        if (cardCreationDTO.getCustomerId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Customer Id"));
        }

        String requestId = UUID.randomUUID().toString();

        logger.info("Start Card Addition (requestId={})", requestId);

        String paymentService = "http://payment-service/api/customer/card/add";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        HttpEntity<CardCreationDTO> paymentServiceRequest = new HttpEntity<>(cardCreationDTO, headers);
        ResponseEntity<MessageDTO> paymentServiceResponse = restTemplate.exchange(paymentService, HttpMethod.POST, paymentServiceRequest, MessageDTO.class);

        // OK
        if (paymentServiceResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("End Card Addition Successfully (requestId={})", requestId);
        }

        // Bad Request
        if (paymentServiceResponse.getStatusCode().is4xxClientError()) {
            logger.warn("End Card Addition Failed (requestId={})", requestId);
            return ResponseEntity.status(paymentServiceResponse.getStatusCode()).body(paymentServiceResponse.getBody());
        }

        // Internal Server Error
        if (paymentServiceResponse.getStatusCode().is5xxServerError()) {
            logger.warn("End Card Addition Failed (requestId={})", requestId);
            return ResponseEntity.status(paymentServiceResponse.getStatusCode()).body(paymentServiceResponse.getBody());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Success"));
    }

    @PostMapping("/card/delete")
    public ResponseEntity<MessageDTO> customerProfileDeleteCard(@RequestBody CardDeletionDTO cardDeletionDTO) {
        // DTO Existence Check
        if (cardDeletionDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }


        // Validation Check
        if (cardDeletionDTO.getCustomerId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Customer Id"));
        }

        if (cardDeletionDTO.getCardId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Card Id"));
        }

        if (!profileService.isCustomerExists(cardDeletionDTO.getCustomerId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Customer not Found"));
        }
        String requestId = UUID.randomUUID().toString();

        logger.info("Start Card Deletion (requestId={})", requestId);

        String paymentService = "http://payment-service/api/customer/card/delete";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        HttpEntity<CardDeletionDTO> paymentServiceRequest = new HttpEntity<>(cardDeletionDTO, headers);
        ResponseEntity<MessageDTO> paymentServiceResponse = restTemplate.exchange(paymentService, HttpMethod.POST, paymentServiceRequest, MessageDTO.class);

        // OK
        if (paymentServiceResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("End Card Deletion Successfully (requestId={})", requestId);
        }

        // Bad Request
        if (paymentServiceResponse.getStatusCode().is4xxClientError()) {
            logger.warn("End Card Deletion Failed (requestId={})", requestId);
            return ResponseEntity.status(paymentServiceResponse.getStatusCode()).body(paymentServiceResponse.getBody());
        }

        // Internal Server Error
        if (paymentServiceResponse.getStatusCode().is5xxServerError()) {
            logger.warn("End Card Deletion Failed (requestId={})", requestId);
            return ResponseEntity.status(paymentServiceResponse.getStatusCode()).body(paymentServiceResponse.getBody());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Success"));
    }
}
