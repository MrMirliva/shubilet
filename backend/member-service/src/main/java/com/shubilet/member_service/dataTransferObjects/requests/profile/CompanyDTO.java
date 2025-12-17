package com.shubilet.member_service.dataTransferObjects.requests.profile;

public class CompanyDTO {
    private String title;
    private String email;
    private boolean isVerified;

    public CompanyDTO() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}
