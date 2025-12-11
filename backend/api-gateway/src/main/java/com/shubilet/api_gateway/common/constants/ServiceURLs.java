package com.shubilet.api_gateway.common.constants;

public class ServiceURLs {
    // Expedition Service URLs
    public static final String EXPEDITION_SERVICE_SEARCH_URL = "http://expedition-service/api/view/customer/api/view/customer/";

    // Member Service URLs
    public static String MEMBER_SERVICE_CREDENTIALS_CHECK_URL = "http://member-service/api/auth/checkCredentials";

    public static String MEMBER_SERVICE_CUSTOMER_REGISTRATION_URL = "http://member-service/api/register/customer";
    public static String MEMBER_SERVICE_COMPANY_REGISTRATION_URL = "http://member-service/api/register/company";
    public static String MEMBER_SERVICE_ADMIN_REGISTRATION_URL = "http://member-service/api/register/admin";

    public static String MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_NAME_URL = "http://member-service/api/customer/profile/edit/name";
    public static String MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_SURNAME_URL = "http://member-service/api/customer/profile/edit/surname";
    public static String MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_GENDER_URL = "http://member-service/api/customer/profile/edit/gender";
    public static String MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_EMAIL_URL = "http://member-service/api/customer/profile/edit/email";
    public static String MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_PASSWORD_URL = "http://member-service/api/customer/profile/edit/password";

    // Security Service URLs
    public static String SECURITY_SERVICE_CREATE_SESSION_URL = "http://security-service/api/auth/createSession";
    public static String SECURITY_SERVICE_CHECK_SESSION_URL = "http://security-service/api/auth/check";
    public static String SECURITY_SERVICE_CHECK_CUSTOMER_SESSION_URL = "http://security-service/api/auth/checkCustomer";

}
