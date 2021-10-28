/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

/**
 *
 * @author brend
 */
@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date startDate;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endDate;
    @NotNull
    private Integer numOfRooms;
    @NotNull
    private Double reservationFee;
    @ManyToOne
    private Customer customer;
    @OneToOne
    private RoomType roomType;
    @OneToOne
    private ArrayList<RoomRate> roomRates;

    

    public Reservation() {
        this.customer = null;
        this.roomType = null;
        this.roomRates = new ArrayList<>();
    }

    public Reservation(Date startDate, Date endDate, Integer numOfRooms, Double reservationFee) {
        this();
        this.startDate = startDate;
        this.endDate = endDate;
        this.numOfRooms = numOfRooms;
        this.reservationFee = reservationFee;
    }

    public Double getReservationFee() {
        return reservationFee;
    }

    public void setReservationFee(Double reservationFee) {
        this.reservationFee = reservationFee;
    }

    public ArrayList<RoomRate> getRoomRates() {
        return roomRates;
    }

    public void setRoomRates(ArrayList<RoomRate> roomRates) {
        this.roomRates = roomRates;
    }
    
    public Long getReservationId() {
        return reservationId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Integer getNumOfRooms() {
        return numOfRooms;
    }
    
    public Customer getCustomer() {
        return customer;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setNumOfRooms(Integer numOfRooms) {
        this.numOfRooms = numOfRooms;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    
    
}
