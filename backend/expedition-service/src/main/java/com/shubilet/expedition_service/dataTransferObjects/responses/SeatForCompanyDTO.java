package com.shubilet.expedition_service.dataTransferObjects.responses;

public class SeatForCompanyDTO {
    private int id;
    private int expeditionId;
    private int seatNo;
    private String customerName;
    private String customerSurname;
    private String status;

    public SeatForCompanyDTO(int id, int expeditionId, int seatNo, String customerName, String customerSurname, String status) {
        this.id = id;
        this.expeditionId = expeditionId;
        this.seatNo = seatNo;
        this.customerName = customerName;
        this.customerSurname = customerSurname;
        this.status = status;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getExpeditionId() {
        return expeditionId;
    }
    public void setExpeditionId(int expeditionId) {
        this.expeditionId = expeditionId;
    }

    public int getSeatNo() {
        return seatNo;
    }
    public void setSeatNo(int seatNo) {
        this.seatNo = seatNo;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerSurname() {
        return customerSurname;
    }
    public void setCustomerSurname(String customerSurname) {
        this.customerSurname = customerSurname;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
