package com.shubilet.api_gateway.controllers;

import com.shubilet.api_gateway.dataTransferObjects.MessageDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.requests.profileManagement.*;
import com.shubilet.api_gateway.dataTransferObjects.external.responses.profileManagement.AdminProfileDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.responses.profileManagement.CompanyProfileDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.responses.profileManagement.CustomerProfileDTO;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ProfileManagementController {
    @PostMapping("/customer/edit/name")
    ResponseEntity<MessageDTO> customerEditName(HttpSession httpSession, @RequestBody MemberAttributeChangeExternalDTO memberAttributeChangeExternalDTO);

    @PostMapping("/customer/edit/surname")
    ResponseEntity<MessageDTO> customerEditSurname(HttpSession httpSession, @RequestBody MemberAttributeChangeExternalDTO memberAttributeChangeExternalDTO);

    @PostMapping("/customer/edit/gender")
    ResponseEntity<MessageDTO> customerEditGender(HttpSession httpSession, @RequestBody MemberAttributeChangeExternalDTO memberAttributeChangeExternalDTO);

    @PostMapping("/customer/edit/email")
    ResponseEntity<MessageDTO> customerEditEmail(HttpSession httpSession, @RequestBody MemberAttributeChangeExternalDTO memberAttributeChangeExternalDTO);

    @PostMapping("/customer/edit/password")
    ResponseEntity<MessageDTO> customerEditPassword(HttpSession httpSession, @RequestBody MemberAttributeChangeExternalDTO memberAttributeChangeExternalDTO);

    @PostMapping("/customer/edit/favoriteCompany/add")
    ResponseEntity<MessageDTO> addFavoriteCompany(HttpSession httpSession, FavoriteCompanyAdditionExternalDTO favoriteCompanyAdditionExternalDTO);

    @PostMapping("/customer/edit/favoriteCompany/delete")
    ResponseEntity<MessageDTO> deleteFavoriteCompany(HttpSession httpSession, FavoriteCompanyDeletionExternalDTO favoriteCompanyDeletionExternalDTO);

    @PostMapping("/customer/edit/card/add")
    ResponseEntity<MessageDTO> addCard(HttpSession httpSession, @RequestBody CardCreationExternalDTO cardCreationExternalDTO);

    @PostMapping("/customer/edit/card/delete")
    ResponseEntity<MessageDTO> deleteCard(HttpSession httpSession, @RequestBody CardDeletionExternalDTO cardDeletionExternalDTO);

    @PostMapping("/customer/get")
    ResponseEntity<CustomerProfileDTO> sendCustomerProfile(HttpSession httpSession);

    @PostMapping("/company/get")
    ResponseEntity<CompanyProfileDTO> sendCompanyProfile(HttpSession httpSession);

    @PostMapping
    ResponseEntity<AdminProfileDTO> sendAdminProfile(HttpSession httpSession);
}
