package com.shubilet.expedition_service.dataTransferObjects.responses.forRepositories;

import com.shubilet.expedition_service.common.enums.SeatStatus;

public class SeatForCompanyRepoDTO {
    private Integer seatId;
    private Integer expeditionId;
    private Integer seatNo;
    private Integer customerId;
    private SeatStatus status;

    public SeatForCompanyRepoDTO(
        Integer seatId, 
        Integer expeditionId, 
        Integer seatNo, 
        Integer customerId, 
        SeatStatus status
    ) {
        this.seatId = seatId;
        this.expeditionId = expeditionId;
        this.seatNo = seatNo;
        this.customerId = customerId;
        this.status = status;
    }

    public Integer getSeatId() {
        return seatId;
    }
    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    public Integer getExpeditionId() {
        return expeditionId;
    }
    public void setExpeditionId(Integer expeditionId) {
        this.expeditionId = expeditionId;
    }

    public Integer getSeatNo() {
        return seatNo;
    }
    public void setSeatNo(Integer seatNo) {
        this.seatNo = seatNo;
    }

    public Integer getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public SeatStatus getStatus() {
        return status;
    }
    public void setStatus(SeatStatus status) {
        this.status = status;
    }
}
