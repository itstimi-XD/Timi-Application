package com.love.timi.exception

enum class ErrorMessage(val exception: CustomException) {
    //공통 오류 (Service Exception Code 의 서비스코드번호 10로 시작)
    INVALID_PARAMETER(CustomException(400, 100101, "Invalid `{{}}` Parameter")), // 입력 파라미터 오류
    UNKNOWN_ERROR(CustomException(500, 100102, "Unknown Error")), //서버 내 처리되지 못한 오류
    NOT_FOUND(CustomException(404, 100103, "Not found")), //잘못된 URL 접근
    SERVER_API_FAIL(CustomException(200, 100105, "There was a problem with the server communication")), //서버간 통신에 문제가 발생헀습니다
    PERMISSION_DENIED(CustomException(403, 100106, "Permission Denied.")),

    // 서비스 내 관련 오류
    /**
     * CustomException (Http Status Code, Service Exception Code, Exception Message)
     * Http Status Code = 400 (고정)
     * Service Exception Code = (서비스코드번호)(분류번호 2자리)(순서번호 2자리)
     * Exception Message = 서비스 오류 설명 메시지
     */


    //회원 관련 오류, 분류번호 01
    EMAIL_ALREADY_REGISTERED(CustomException(400, 10101, "The email is already registered.")), // The email has already been registered
    EMAIL_VERIFICATION_NOT_FOUND(CustomException(400, 10102, "Email verification information not found.")), // Verification information not found
    EMAIL_VERIFICATION_CODE_MISMATCH(CustomException(400, 10103, "Verification code does not match.")), // Verification code mismatch
    EMAIL_VERIFICATION_CODE_EXPIRED(CustomException(400, 10104, "Verification code has expired.")), // Verification code expired
    INVALID_EMAIL(CustomException(400, 10105, "Invalid email format.")), // Invalid email format
    NO_IP_ADDRESS(CustomException(400, 10106, "No IP address was entered.")), // IP address is missing
    EMAIL_MISSING(CustomException(400, 10107, "Email was not entered.")),
    PASSWORD_MISSING(CustomException(400, 10108, "Password was not entered.")),
    NAME_MISSING(CustomException(400, 10109, "Name was not entered.")),
    COMPANY_NAME_MISSING(CustomException(400, 10110, "Company name was not entered.")),
    TEAM_NAME_MISSING(CustomException(400, 10111, "Team name was not entered.")),
    ROLE_NAME_MISSING(CustomException(400, 10112, "Role was not entered.")),
    TERMS_NOT_ACCEPTED(CustomException(400, 10113, "Terms of service have not been accepted.")),
    INVALID_IP_ADDRESS(CustomException(400, 10114, "Invalid IP address.")),
    USER_NOT_FOUND(CustomException(400, 10115, "User not found.")),
    FAIL_TO_DECODE(CustomException(400, 10116, "Failed to decode.")),
    NOT_YET_CONFIRM(CustomException(400, 10117, "Awaiting approval for registration.")), // Waiting for approval
    INACTIVE_USER(CustomException(400, 10118, "Account is inactive.")), // Inactive account
    LOCK_USER(CustomException(400, 10119, "Account is locked.")), // Locked account
    WITHDRAWN_USER(CustomException(400, 10120, "Account has been withdrawn.")), // Withdrawn account
    PASSWORD_MISMATCH(CustomException(400, 10121, "Password does not match.")), // Password mismatch
    LOGIN_FAIL_EXCEED_LIMIT(CustomException(400, 10122, "Login attempts exceeded.")), // Exceeded login attempts
    OTP_MISMATCH(CustomException(400, 10123, "OTP does not match.")), // OTP mismatch
    DORMANCY_USER(CustomException(400, 10124, "Account is dormant.")), // Dormant account
    IP_NOT_ALLOWED(CustomException(400, 10125, "IP address not allowed.")), // Unpermitted IP address
    PASSWORD_NEED_DIFFERENCE(CustomException(400, 10126, "Please enter a different password from the current one when changing.")), // Please enter a different password for change
    IP_DONT_NEEDED(CustomException(400, 10127, "IP address is not required for this role.")), // IP address not needed
    IP_NOT_FOUND(CustomException(400, 10128, "The IP address could not be found.")), // IP address could not be found
    INVALID_EMAIL_DOMAIN(CustomException(400, 10129, "The email domain is not allowed.")), // The email domain is not permitted
    IP_ADDRESS_MISSING(CustomException(400, 10130, "Cannot found ip address")),
    TERM_AGREEMENT_REQUIRED(CustomException(400, 10131, "ALL Terms and Conditions must be agreed to complete the registration")),

    //권한 관련 오류, 분류 번호 02
    ROLE_ALREADY_EXISTS(CustomException(400, 10201, "The role already exists.")), // The role already exists
    ROLE_NOT_FOUND(CustomException(400, 10202, "The role could not be found.")), // The role could not be found
    NO_TWO_FACTOR_SECRET(CustomException(400, 10203, "Two-factor authentication info not found.")), // Two-factor authentication information is missing
    STATUS_NOT_FOUND(CustomException(400, 10204, "The status could not be found.")), // The status could not be found
    ROLE_MENU_NOT_FOUND(CustomException(400, 10205, "The menu for this role could not be found.")), // The menu for this role could not be found,
    REVIEW_APPROVAL_ROLE_DUPLICATED(CustomException(400, 10206, "Role that has approval permission is more than one")),
    MANAGE_APPROVAL_ROLE_DUPLICATED(CustomException(400, 10207, "Role that has approval permission is more than one")),
    USER_LIST_APPROVAL_ROLE_DUPLICATED(CustomException(400,10208, "Role that has approval permission is more than one")),
    REVIEW_APPROVAL_ROLE_NOT_FOUND(CustomException(400,10209, " Role that has approval permission is not found.(reviewer)" )),
    MANAGE_APPROVAL_ROLE_NOT_FOUND(CustomException(400,10210, " Role that has approval permission is not found.(manager)" )),

    // Other errors, category number 03
    CANNOT_GET_USER_INFO(CustomException(400, 10301, "Failed to retrieve user information.")), // Failed to retrieve user information
    CANNOT_GET_USER_KEY(CustomException(400, 10302, "Cannot get UserKey(UserId) From Gateway.")),

    // Authentication-related errors, category number 04
    MAIL_SEND_ERROR(CustomException(500, 990102, "Mail sending failed")),
    MAIL_SEND_CONNECTION_ERROR(CustomException(500, 990103, "Mail sending failed: Unable to connect to mail server."))
}