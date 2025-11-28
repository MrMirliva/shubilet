package com.shubilet.member_service.controllers.Impl;

import com.shubilet.member_service.common.enums.Gender;
import com.shubilet.member_service.common.util.ErrorUtils;
import com.shubilet.member_service.common.util.StringUtils;
import com.shubilet.member_service.common.util.ValidationUtils;
import com.shubilet.member_service.controllers.CustomerController;
import com.shubilet.member_service.dataTransferObjects.requests.CustomerCreationDTO;
import com.shubilet.member_service.dataTransferObjects.responses.MessageDTO;
import com.shubilet.member_service.models.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.shubilet.member_service.services.CustomerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
public class CustomerControllerImpl implements CustomerController {
    private final CustomerService customerService;

    CustomerControllerImpl(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/customer")
    public ResponseEntity<MessageDTO> createCustomer(@RequestBody CustomerCreationDTO customerCreationDTO) {

        // DTO Existence Check
        if (customerCreationDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Creation Data"));
        }

        // Attributes Null or Blank Check
        if (StringUtils.isNullOrBlank(customerCreationDTO.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Name"));
        }
        if (customerCreationDTO.getSurname() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Surname"));
        }

        if (customerCreationDTO.getEmail() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Email"));
        }

        if (customerCreationDTO.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isNull("Password"));
        }

        // Validation Check
        if (!ValidationUtils.isValidGender(customerCreationDTO.getGender())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isInvalidFormat("Gender"));
        }

        if (!ValidationUtils.isValidEmail(customerCreationDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isInvalidFormat("Email"));
        }
        if (!ValidationUtils.isValidPassword(customerCreationDTO.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.isInvalidFormat("Password"));
        }

        // Uniqueness Check
        if (customerService.isCustomerExistsByEmail(customerCreationDTO.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorUtils.alreadyExists("Customer Email"));
        }

        Customer customer = new Customer(
                customerCreationDTO.getName(),
                customerCreationDTO.getSurname(),
                Gender.fromValue(customerCreationDTO.getGender()),
                customerCreationDTO.getEmail(),
                customerCreationDTO.getPassword()
        );

        if (!customerService.createCustomer(customer)) {
            return ResponseEntity.badRequest().body(ErrorUtils.criticalError());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Customer Creation Successful."));
    }
}
