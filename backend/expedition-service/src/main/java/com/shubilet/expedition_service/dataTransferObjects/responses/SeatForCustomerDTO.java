package com.shubilet.expedition_service.dataTransferObjects.responses;

public class SeatForCustomerDTO {
    private int id;
    private int expeditionId;
    private int seatNo;
    private String status;

    public SeatForCustomerDTO(int id, int expeditionId, int seatNo, String status) {
        this.id = id;
        this.expeditionId = expeditionId;
        this.seatNo = seatNo;
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

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
}
