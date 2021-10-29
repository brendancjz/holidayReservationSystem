/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author brend
 */
@Entity
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;
    @NotNull
    @Size(min=5, max=15)
    private String roomTypeName;
    @NotNull
    @Size(min=5, max=255)
    private String roomTypeDesc;
    @NotNull
    private Integer roomSize;
    @NotNull
    private Integer numOfBeds;
    @NotNull
    private Integer capacity;
    @NotNull
    private Integer typeRank;
    @NotNull
    @Size(min=5, max=255)
    private String amenities;
    @NotNull
    private Boolean isDisabled;
    @OneToMany(mappedBy= "roomType")
    private ArrayList<RoomRate> rates;
    @OneToMany(mappedBy= "roomType")
    private ArrayList<Room> rooms;

    public RoomType() {
        this.rates = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.isDisabled = false;
    }
 
    public RoomType(String roomTypeName, String roomTypeDesc, Integer roomSize, Integer numOfBeds, Integer capacity, Integer rank, String amenities) {
        this();
        this.roomTypeName = roomTypeName;
        this.roomTypeDesc = roomTypeDesc;
        this.roomSize = roomSize;
        this.numOfBeds = numOfBeds;
        this.capacity = capacity;
        this.typeRank = rank;
        this.amenities = amenities;
        
    }

    public Integer getTypeRank() {
        return typeRank;
    }

    public void setTypeRank(Integer rank) {
        this.typeRank = rank;
    }
    
    public Boolean getIsDisabled() {
        return isDisabled;
    }

    public void setIsDisabled(Boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public String getRoomTypeDesc() {
        return roomTypeDesc;
    }

    public void setRoomTypeDesc(String roomTypeDesc) {
        this.roomTypeDesc = roomTypeDesc;
    }

    public Integer getRoomSize() {
        return roomSize;
    }

    public void setSize(Integer roomSize) {
        this.roomSize = roomSize;
    }

    public Integer getNumOfBeds() {
        return numOfBeds;
    }

    public void setNumOfBeds(Integer numOfBeds) {
        this.numOfBeds = numOfBeds;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public ArrayList<RoomRate> getRates() {
        return rates;
    }

    public void setRates(ArrayList<RoomRate> rates) {
        this.rates = rates;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    
}
