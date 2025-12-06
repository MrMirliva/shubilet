package com.shubilet.expedition_service.dataTransferObjects.responses;

public class ExpeditionForCompanyDTO {
    private int id; //Exp
    private String departureCity; //City
    private String arrivalCity; //City
    private String date; //Exp
    private String time; //Exp
    private double price; //Exp
    private int duration; //Exp
    private int capacity; //Exp
    private int numberOfBookedSeats; //calculated by booked tickets
    private double profit; //numberOfBookedSeats * price

    public ExpeditionForCompanyDTO(
        int id,
        String departureCity,
        String arrivalCity,
        String date,
        String time,
        double price,
        int duration,
        int capacity,
        int numberOfBookedSeats,
        double profit
    ) {
        this.id = id;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.date = date;
        this.time = time;
        this.price = price;
        this.duration = duration;
        this.capacity = capacity;
        this.numberOfBookedSeats = numberOfBookedSeats;
        this.profit = profit;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getDepartureCity() {
        return departureCity;
    }
    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
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

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getNumberOfBookedSeats() {
        return numberOfBookedSeats;
    }
    public void setNumberOfBookedSeats(int numberOfBookedSeats) {
        this.numberOfBookedSeats = numberOfBookedSeats;
    }

    public double getProfit() {
        return profit;
    }
    public void setProfit(double profit) {
        this.profit = profit;
    }
}
