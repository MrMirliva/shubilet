package com.shubilet.member_service.controllers.Impl;

import com.shubilet.member_service.common.enums.Gender;
import com.shubilet.member_service.common.util.ErrorUtils;
import com.shubilet.member_service.common.util.StringUtils;
import com.shubilet.member_service.common.util.ValidationUtils;
import com.shubilet.member_service.controllers.RegistrationController;
import com.shubilet.member_service.dataTransferObjects.requests.AdminRegistrationDTO;
import com.shubilet.member_service.dataTransferObjects.requests.CompanyRegistrationDTO;
import com.shubilet.member_service.dataTransferObjects.requests.CustomerRegistrationDTO;
import com.shubilet.member_service.dataTransferObjects.responses.MessageDTO;
import com.shubilet.member_service.models.Admin;
import com.shubilet.member_service.models.Company;
import com.shubilet.member_service.models.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.shubilet.member_service.services.RegistrationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
public class RegistrationControllerImpl implements RegistrationController {
    private final RegistrationService registrationService;

    RegistrationControllerImpl(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/customer")
    public ResponseEntity<MessageDTO> registerCustomer(@RequestBody CustomerRegistrationDTO customerRegistrationDTO) {

        // DTO Existence Check
        if (customerRegistrationDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Creation Data"));
        }

        // Attributes Null or Blank Check
        if (StringUtils.isNullOrBlank(customerRegistrationDTO.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Name"));
        }
        if (StringUtils.isNullOrBlank(customerRegistrationDTO.getSurname())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Surname"));
        }

        if (StringUtils.isNullOrBlank(customerRegistrationDTO.getGender())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Gender"));
        }

        if (StringUtils.isNullOrBlank(customerRegistrationDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Email"));
        }

        if (StringUtils.isNullOrBlank(customerRegistrationDTO.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Password"));
        }

        // Validation Check
        if (!ValidationUtils.isValidGender(customerRegistrationDTO.getGender())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isInvalidFormat("Gender"));
        }

        if (!ValidationUtils.isValidEmail(customerRegistrationDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isInvalidFormat("Email"));
        }
        if (!ValidationUtils.isValidPassword(customerRegistrationDTO.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isInvalidFormat("Password"));
        }

        // Uniqueness Check
        if (registrationService.isUserExistsByEmail(customerRegistrationDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.alreadyExists("Customer Email"));
        }

        Customer customer = new Customer(
                customerRegistrationDTO.getName(),
                customerRegistrationDTO.getSurname(),
                Gender.fromValue(customerRegistrationDTO.getGender()),
                customerRegistrationDTO.getEmail(),
                customerRegistrationDTO.getPassword()
        );

        if (!registrationService.registerCustomer(customer)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorUtils.criticalError());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Customer Creation Successful."));
    }

    @PostMapping("/company")
    public ResponseEntity<MessageDTO> registerCompany(@RequestBody CompanyRegistrationDTO companyRegistrationDTO) {
        // DTO Existence Check
        if (companyRegistrationDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Creation Data"));
        }

        // Attributes Null or Blank Check
        if (StringUtils.isNullOrBlank(companyRegistrationDTO.getTitle())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Title"));
        }
        if (StringUtils.isNullOrBlank(companyRegistrationDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Email"));
        }

        if (StringUtils.isNullOrBlank(companyRegistrationDTO.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Password"));
        }

        // Validation Check
        if (!ValidationUtils.isValidEmail(companyRegistrationDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isInvalidFormat("Email"));
        }
        if (!ValidationUtils.isValidPassword(companyRegistrationDTO.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isInvalidFormat("Password"));
        }

        // Uniqueness Check
        if (registrationService.isUserExistsByEmail(companyRegistrationDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.alreadyExists("Customer Email"));
        }

        Company company = new Company(
                companyRegistrationDTO.getTitle(),
                companyRegistrationDTO.getEmail(),
                companyRegistrationDTO.getPassword()
        );

        if (!registrationService.registerCompany(company)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorUtils.criticalError());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Company Registration Successful."));
    }

    @PostMapping("/admin")
    public ResponseEntity<MessageDTO> registerAdmin(@RequestBody AdminRegistrationDTO adminRegistrationDTO) {

        // DTO Existence Check
        if (adminRegistrationDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Creation Data"));
        }

        // Attributes Null or Blank Check
        if (StringUtils.isNullOrBlank(adminRegistrationDTO.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Name"));
        }
        if (StringUtils.isNullOrBlank(adminRegistrationDTO.getSurname())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Surname"));
        }

        if (StringUtils.isNullOrBlank(adminRegistrationDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Email"));
        }

        if (StringUtils.isNullOrBlank(adminRegistrationDTO.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Password"));
        }

        // Validation Check
        if (!ValidationUtils.isValidEmail(adminRegistrationDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isInvalidFormat("Email"));
        }
        if (!ValidationUtils.isValidPassword(adminRegistrationDTO.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isInvalidFormat("Password"));
        }

        Admin admin = new Admin(
                adminRegistrationDTO.getName(),
                adminRegistrationDTO.getSurname(),
                adminRegistrationDTO.getEmail(),
                adminRegistrationDTO.getPassword()
        );

        if (!registrationService.registerAdmin(admin)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorUtils.criticalError());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Admin Registration Successful."));
    }

}
