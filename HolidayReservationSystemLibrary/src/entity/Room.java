/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author brend
 */
@Entity
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    @Column(nullable = false)
    @NotNull
    private Integer roomLevel;
    @Column(nullable = false)
    @NotNull
    private Integer roomNum;
    @Column(nullable = false)
    @NotNull
    private Boolean isAvailable;
    @Column(nullable = false)
    @NotNull
    private Boolean isVacant;
    @Column(nullable = false)
    @NotNull
    private Boolean isDisabled;
    @ManyToOne
    @JoinColumn(nullable = false)
    private RoomType roomType; 

    public Room() {
        this.roomType = null;
        this.isDisabled = false;
        this.isAvailable = true;
        this.isVacant = true;
    }

    public Room(Integer roomLevel, Integer roomNum) {
        this();
        this.roomLevel = roomLevel;
        this.roomNum = roomNum;
    }

    public Boolean getIsVacant() {
        return isVacant;
    }

    public void setIsVacant(Boolean isVacant) {
        this.isVacant = isVacant;
    }
    
    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Integer getRoomLevel() {
        return roomLevel;
    }

    public void setRoomLevel(Integer roomLevel) {
        this.roomLevel = roomLevel;
    }

    public Integer getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(Integer roomNum) {
        this.roomNum = roomNum;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

   
    
}
