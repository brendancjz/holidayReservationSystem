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
import javax.persistence.Temporal;

/**
 *
 * @author brend
 */
@Entity
public class RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomRateId;
    private String roomRateName;
    private String roomRateType;
    private Double ratePerNight;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date startDate;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endDate;
    private Boolean isDisabled;
    @ManyToOne
    private RoomType roomType;

    public RoomRate() {
        this.roomType = null;
        this.isDisabled = false;
    }

    

    public RoomRate(String roomRateName, String roomRateType, Double ratePerNight) {
        this();
        this.roomRateName = roomRateName;
        this.roomRateType = roomRateType;
        this.ratePerNight = ratePerNight;
        this.startDate = null;
        this.endDate = null;
    }
    
    public RoomRate(String roomRateName, String roomRateType, Double ratePerNight, Date startDate, Date endDate) {
        this();
        this.roomRateName = roomRateName;
        this.roomRateType = roomRateType;
        this.ratePerNight = ratePerNight;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }
    
    public Long getRoomRateId() {
        return roomRateId;
    }

    public void setRoomRateId(Long roomRateId) {
        this.roomRateId = roomRateId;
    }

    public String getRoomRateName() {
        return roomRateName;
    }

    public void setRoomRateName(String roomRateName) {
        this.roomRateName = roomRateName;
    }

    public String getRoomRateType() {
        return roomRateType;
    }

    public void setRoomRateType(String roomRateType) {
        this.roomRateType = roomRateType;
    }

    public Double getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(Double ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    
    
}
