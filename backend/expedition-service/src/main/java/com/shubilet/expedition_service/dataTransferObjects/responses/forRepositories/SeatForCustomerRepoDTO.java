package com.shubilet.expedition_service.dataTransferObjects.responses.forRepositories;

import com.shubilet.expedition_service.common.enums.SeatStatus;

public class SeatForCustomerRepoDTO {
    private Integer customerId;
    private Integer expeditionId;
    private Integer seatNo;
    private SeatStatus status;

    public SeatForCustomerRepoDTO(
        Integer customerId,
        Integer expeditionId,
        Integer seatNo,
        SeatStatus status
    ) {
        this.customerId = customerId;
        this.expeditionId = expeditionId;
        this.seatNo = seatNo;
        this.status = status;
    }

    public Integer getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
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

    public SeatStatus getStatus() {
        return status;
    }
    public void setStatus(SeatStatus status) {
        this.status = status;
    }
}
