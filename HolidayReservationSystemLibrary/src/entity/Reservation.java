/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

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
    private Date startDateTime;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endDateTime;
    @ManyToOne
    private Guest guest;
    @OneToOne
    private RoomType roomType;

    public Reservation() {
    }

    public Reservation(Date startDateTime, Date endDateTime, Guest guest, RoomType roomType) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.guest = guest;
        this.roomType = roomType;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public Guest getGuest() {
        return guest;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    
    
}
