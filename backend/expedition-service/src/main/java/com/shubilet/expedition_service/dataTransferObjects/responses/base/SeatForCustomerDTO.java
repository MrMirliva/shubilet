package com.shubilet.expedition_service.dataTransferObjects.responses.base;

public class SeatForCustomerDTO {
    private int customerId;
    private int expeditionId;
    private int seatNo;
    private String status;

    public SeatForCustomerDTO() {

    }

    public SeatForCustomerDTO(
        int customerId, 
        int expeditionId, 
        int seatNo, 
        String status
    ) {
        this.customerId = customerId;
        this.expeditionId = expeditionId;
        this.seatNo = seatNo;
        this.status = status;
    }

    public int getCustomerId() {
        return customerId;
    }
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
}
