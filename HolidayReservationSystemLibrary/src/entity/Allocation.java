/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author brend
 */
@Entity
public class Allocation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long allocationId;
    @OneToOne
    private Reservation reservation;
    @OneToOne
    private Room room;

    public Allocation() {
    }

    public Allocation(Reservation reservation, Room room) {
        this.reservation = reservation;
        this.room = room;
    }

    public Long getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(Long allocationId) {
        this.allocationId = allocationId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    
   
    
}
