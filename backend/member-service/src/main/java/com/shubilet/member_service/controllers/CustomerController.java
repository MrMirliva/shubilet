package com.shubilet.member_service.controllers;

import com.shubilet.member_service.common.enums.Gender;
import com.shubilet.member_service.common.util.StringUtils;
import com.shubilet.member_service.common.util.ValidationUtils;
import com.shubilet.member_service.dataTransferObjects.requests.CustomerCreationDTO;
import com.shubilet.member_service.models.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.servlet.http.HttpSession;

import com.shubilet.member_service.services.CustomerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {
    private final CustomerService customerService;

    CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/test")
    public ResponseEntity<String> createCustomer(@RequestBody CustomerCreationDTO customerCreationDTO) {

        // DTO Existence Check
        if (customerCreationDTO == null) {
            return new ResponseEntity<>("Customer creation package is null", HttpStatus.BAD_REQUEST);
        }

        // Attributes Null or Blank Check
        if (StringUtils.isNullOrBlank(customerCreationDTO.getName())) {
            return new ResponseEntity<>("Customer Name Could not be Null", HttpStatus.BAD_REQUEST);
        }
        if (customerCreationDTO.getSurname() == null) {
            return new ResponseEntity<>("Customer Surname Could not be Null", HttpStatus.BAD_REQUEST);
        }

        if (customerCreationDTO.getEmail() == null) {
            return new ResponseEntity<>("Customer Email Could not be Null", HttpStatus.BAD_REQUEST);
        }

        if (customerCreationDTO.getPassword() == null) {
            return new ResponseEntity<>("Customer Password Could not be Null", HttpStatus.BAD_REQUEST);
        }

        // Validation Check
        if (!Gender.isValidGender(customerCreationDTO.getGender())) {

            return new ResponseEntity<>("Customer Gender is Invalid", HttpStatus.BAD_REQUEST);
        }

        if (!ValidationUtils.isValidEmail(customerCreationDTO.getEmail())) {
            return new ResponseEntity<>("Customer Email is Invalid", HttpStatus.BAD_REQUEST);
        }
        if (!ValidationUtils.isValidPassword(customerCreationDTO.getPassword())) {
            return new ResponseEntity<>("Customer Password is Invalid", HttpStatus.BAD_REQUEST);
        }

        Customer customer = new Customer(
                customerCreationDTO.getName(),
                customerCreationDTO.getSurname(),
                Gender.fromValue(customerCreationDTO.getGender()),
                customerCreationDTO.getEmail(),
                customerCreationDTO.getPassword()
        );
        customerService.createCustomer(customer);
        return new ResponseEntity<>("Customer Created", HttpStatus.OK);
    }
}
