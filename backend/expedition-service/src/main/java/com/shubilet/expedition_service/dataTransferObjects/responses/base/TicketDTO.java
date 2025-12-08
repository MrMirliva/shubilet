package com.shubilet.expedition_service.dataTransferObjects.responses.base;

public class TicketDTO {
    private String PNR;
    private int seatNo;
    private int expeditionId;
    private int companyId;
    private String depertureCity;
    private String arrivalCity;
    private String date;
    private String time;
    private String duration;

    public TicketDTO() {
    
    }

    public TicketDTO(
        String PNR, 
        int seatNo, 
        int expeditionId, 
        int companyId, 
        String depertureCity, 
        String arrivalCity, 
        String date, 
        String time, 
        String duration
    ) {
        this.PNR = PNR;
        this.seatNo = seatNo;
        this.expeditionId = expeditionId;
        this.companyId = companyId;
        this.depertureCity = depertureCity;
        this.arrivalCity = arrivalCity;
        this.date = date;
        this.time = time;
        this.duration = duration;
    }

    public String getPNR() {
        return PNR;
    }
    public void setPNR(String PNR) {
        this.PNR = PNR;
    }

    public int getSeatNo() {
        return seatNo;
    }
    public void setSeatNo(int seatNo) {
        this.seatNo = seatNo;
    }

    public int getExpeditionId() {
        return expeditionId;
    }
    public void setExpeditionId(int expeditionId) {
        this.expeditionId = expeditionId;
    }

    public int getCompanyId() {
        return companyId;
    }
    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getDepertureCity() {
        return depertureCity;
    }
    public void setDepertureCity(String depertureCity) {
        this.depertureCity = depertureCity;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }
    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
}
