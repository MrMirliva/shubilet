package com.shubilet.member_service.controllers.Impl;

import com.shubilet.member_service.common.enums.Gender;
import com.shubilet.member_service.common.util.StringUtils;
import com.shubilet.member_service.common.util.ValidationUtils;
import com.shubilet.member_service.dataTransferObjects.requests.resourceDTOs.CardDTO;
import com.shubilet.member_service.dataTransferObjects.requests.CustomerAttributeDTO;
import com.shubilet.member_service.dataTransferObjects.requests.FavoriteCompanyDTO;
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
    public ResponseEntity<MessageDTO> editCustomerProfileName(@RequestBody CustomerAttributeDTO customerAttributeDTO) {
        // DTO Existence Check
        if (customerAttributeDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }

        // Attributes Null or Blank Check
        if (StringUtils.isNullOrBlank(customerAttributeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Attribute can't be empty"));

        }

        // Validation Check
        if (customerAttributeDTO.getCustomerId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Customer Id"));
        }

        if (!profileService.isCustomerExists(customerAttributeDTO.getCustomerId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Customer not Found"));
        }

        if (!profileService.editName(customerAttributeDTO.getCustomerId(), customerAttributeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Critical Error"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Success"));
    }

    @PostMapping("/surname")
    public ResponseEntity<MessageDTO> editCustomerProfileSurname(@RequestBody CustomerAttributeDTO customerAttributeDTO) {
        // DTO Existence Check
        if (customerAttributeDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }

        // Attributes Null or Blank Check
        if (StringUtils.isNullOrBlank(customerAttributeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Attribute can't be empty"));
        }

        // Validation Check
        if (customerAttributeDTO.getCustomerId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Customer Id"));
        }

        if (!profileService.isCustomerExists(customerAttributeDTO.getCustomerId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Customer not Found"));
        }

        if (!profileService.editSurname(customerAttributeDTO.getCustomerId(), customerAttributeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Critical Error"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Success"));
    }

    @PostMapping("/gender")
    public ResponseEntity<MessageDTO> editCustomerProfileGender(@RequestBody CustomerAttributeDTO customerAttributeDTO) {
        // DTO Existence Check
        if (customerAttributeDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }

        // Attributes Null or Blank Check
        if (StringUtils.isNullOrBlank(customerAttributeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Attribute can't be empty"));

        }

        // Validation Check
        if (customerAttributeDTO.getCustomerId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Customer Id"));
        }
        if (!ValidationUtils.isValidGender(customerAttributeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Gender Type"));
        }

        if (!profileService.isCustomerExists(customerAttributeDTO.getCustomerId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Customer not Found"));
        }

        if (!profileService.editGender(customerAttributeDTO.getCustomerId(), Gender.fromValue(customerAttributeDTO.getAttribute()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Critical Error"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Success"));
    }

    @PostMapping("/email")
    public ResponseEntity<MessageDTO> editCustomerProfileEmail(@RequestBody CustomerAttributeDTO customerAttributeDTO) {
        // DTO Existence Check
        if (customerAttributeDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }

        // Attributes Null or Blank Check
        if (StringUtils.isNullOrBlank(customerAttributeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Attribute can't be empty"));

        }

        // Validation Check
        if (customerAttributeDTO.getCustomerId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Customer Id"));
        }
        if (ValidationUtils.isValidEmail(customerAttributeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Email Address"));
        }

        if (!profileService.isCustomerExists(customerAttributeDTO.getCustomerId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Customer not Found"));
        }

        if (!profileService.editEmail(customerAttributeDTO.getCustomerId(), customerAttributeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Critical Error"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Success"));
    }

    @PostMapping("/password")
    public ResponseEntity<MessageDTO> editCustomerProfilePassword(@RequestBody CustomerAttributeDTO customerAttributeDTO) {
        // DTO Existence Check
        if (customerAttributeDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }

        // Attributes Null or Blank Check
        if (StringUtils.isNullOrBlank(customerAttributeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Attribute can't be empty"));

        }

        // Validation Check
        if (customerAttributeDTO.getCustomerId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Customer Id"));
        }
        if (ValidationUtils.isValidPassword(customerAttributeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Password"));
        }

        if (!profileService.isCustomerExists(customerAttributeDTO.getCustomerId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Customer not Found"));
        }

        if (!profileService.editPassword(customerAttributeDTO.getCustomerId(), customerAttributeDTO.getAttribute())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Critical Error"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Success"));
    }

    @PostMapping("/favoriteCompany/add")
    public ResponseEntity<MessageDTO> customerProfileAddFavoriteCompany(@RequestBody FavoriteCompanyDTO favoriteCompanyDTO) {
        // DTO Existence Check
        if (favoriteCompanyDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }

        // Validation Check
        if (favoriteCompanyDTO.getCustomerId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Customer Id"));
        }
        if (favoriteCompanyDTO.getCompanyId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Company Id"));
        }

        if (!profileService.isCustomerExists(favoriteCompanyDTO.getCustomerId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Customer not Found with Given ID"));
        }
        if (!profileService.isCompanyExists(favoriteCompanyDTO.getCompanyId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Company not Found with Given ID"));
        }
        int customerId = favoriteCompanyDTO.getCustomerId();
        int companyId = favoriteCompanyDTO.getCompanyId();
        FavoriteCompany favoriteCompany = new FavoriteCompany(customerId, companyId);

        if (!profileService.addFavoriteCompany(favoriteCompany)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Critical Error"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Successfully Added"));
    }

    @PostMapping("/favoriteCompany/delete")
    public ResponseEntity<MessageDTO> customerProfileDeleteFavoriteCompany(@RequestBody FavoriteCompanyDTO favoriteCompanyDTO) {
        // DTO Existence Check
        if (favoriteCompanyDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }

        // Validation Check
        if (favoriteCompanyDTO.getRelationId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Relation Id"));
        }

        if (!profileService.isRelationExists(favoriteCompanyDTO.getRelationId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Relation not Found with Given ID"));
        }

        if (!profileService.deleteFavoriteCompany(favoriteCompanyDTO.getRelationId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Critical Error"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Successfully deleted Favorite Company"));
    }

    @PostMapping("/card/add")
    public ResponseEntity<MessageDTO> customerProfileAddCard(@RequestBody CardDTO cardDTO) {
        // DTO Existence Check
        if (cardDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }

        // Attributes Null or Blank Check
        if (StringUtils.isNullOrBlank(cardDTO.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Card Holder Name can't be empty"));
        }

        if (StringUtils.isNullOrBlank(cardDTO.getSurname())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Card Holder Surname can't be empty"));
        }

        if (StringUtils.isNullOrBlank(cardDTO.getCardNo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Card No Can't be empty"));
        }

        if (StringUtils.isNullOrBlank(cardDTO.getExpirationDate())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Card Expiration Date can't be empty"));
        }

        if (StringUtils.isNullOrBlank(cardDTO.getCvv())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Card CVV can't be empty"));
        }

        // TODO: Card No, Expiration Date, CVC Validations will be added later
        // Validation Check
        if (cardDTO.getCustomerId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Customer Id"));
        }

        String requestId = UUID.randomUUID().toString();

        logger.info("Start Card Addition (requestId={})", requestId);

        String paymentService = "http://payment-service/api/customer/card/add";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        HttpEntity<CardDTO> paymentServiceRequest = new HttpEntity<>(cardDTO, headers);
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
    public ResponseEntity<MessageDTO> customerProfileDeleteCard(@RequestBody CardDTO cardDTO) {
        // DTO Existence Check
        if (cardDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Body can't be null"));
        }


        // Validation Check
        if (cardDTO.getCustomerId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Customer Id"));
        }

        if (cardDTO.getCardId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Card Id"));
        }

        if (!profileService.isCustomerExists(cardDTO.getCustomerId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Customer not Found"));
        }
        String requestId = UUID.randomUUID().toString();

        logger.info("Start Card Deletion (requestId={})", requestId);

        String paymentService = "http://payment-service/api/customer/card/delete";

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        HttpEntity<CardDTO> paymentServiceRequest = new HttpEntity<>(cardDTO, headers);
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
