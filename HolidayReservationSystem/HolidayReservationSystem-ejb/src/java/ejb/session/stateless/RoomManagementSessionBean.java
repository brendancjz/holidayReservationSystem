/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Allocation;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.RoomRateEnum;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
@Stateless
public class RoomManagementSessionBean implements RoomManagementSessionBeanRemote, RoomManagementSessionBeanLocal {

    @EJB
    private AllocationSessionBeanLocal allocationSessionBean;

    @EJB
    private ReservationSessionBeanLocal reservationSessionBean;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;

    @EJB
    private RoomSessionBeanLocal roomSessionBean;
    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBean;
    
    

    @Override
    public List<RoomType> getAllRoomTypes() throws EmptyListException {
        return roomTypeSessionBean.retrieveAllRoomTypes();
    }
    
    @Override
    public List<RoomRate> getRoomRates(Long roomTypeId) throws EmptyListException {
        return roomTypeSessionBean.getRoomRatesByRoomTypeId(roomTypeId);
    }

    @Override
    public RoomRate createNewRoomRate(Long roomTypeId, RoomRateEnum rateEnum, LocalDateTime startDate, LocalDateTime endDate, double rateAmount) {
        Long roomRateId;
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        String roomRateName = rateEnum + roomType.getRoomTypeName();
        if (startDate == null && endDate == null) {
           
            //CREATE ROOM RATE
            RoomRate rate = new RoomRate(roomRateName,rateEnum,rateAmount);
            //ASSOCIATE ROOM RATE WITH ROOM TYPE
            roomRateSessionBean.associateRoomRateWithRoomType(rate, roomTypeId);
            //PERSIST ROOM RATE
            roomRateId = roomRateSessionBean.createNewRoomRate(rate);
            //ASSOCIATE ROOM TYPE WITH ROOM RATE
            roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeId, roomRateId);
            

        } else {
            Date start = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

            RoomRate rate = new RoomRate(roomRateName,rateEnum,rateAmount, start, end);
            roomRateSessionBean.associateRoomRateWithRoomType(rate, roomTypeId);
            roomRateId = roomRateSessionBean.createNewRoomRate(rate);
            roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeId, roomRateId);

        }
        
        
        RoomRate roomRate = em.find(RoomRate.class, roomRateId);
        return roomRate;
    }

    @Override
    public RoomRate getRoomRate(String rateName) {
        return roomRateSessionBean.getRoomRateByRoomRateName(rateName);
    }
    
    @Override
    public RoomRate getRoomRate(Long rateId) {
        return roomRateSessionBean.getRoomRateByRoomRateId(rateId);
    }

    @Override
    public void updateRoomRate(Long rateId, String name, Double amount, Date startDate, Date endDate) {
        RoomRate rate = this.getRoomRate(rateId);
        
        rate.setRoomRateName(name);
        rate.setRatePerNight(amount);
        rate.setStartDate(startDate);
        rate.setEndDate(endDate);
    }

    @Override
    public void deleteRoomRate(Long roomRateId) {
        RoomRate rate = roomRateSessionBean.getRoomRateByRoomRateId(roomRateId);
        List<Reservation> reservations;
        try {
            reservations = reservationSessionBean.retrieveAllReservations();
            
            for (int i = 0; i < reservations.size(); i++) {
                if (reservations.get(i).getRoomRates().contains(rate)) {
                    rate.setIsDisabled(Boolean.TRUE);
                    System.out.println("Room Rate " + rate.getRoomRateName() + " is now disabled.");
                    return;
                }
            } 

            //Delete cause no one using
            rate.getRoomType().getRates().remove(rate);
            em.remove(rate);
            System.out.println("Deleted Room Room: " + rate.getRoomRateName());
        } catch (EmptyListException ex) {
            //Delete cause no one using
            rate.getRoomType().getRates().remove(rate);
            em.remove(rate);
            System.out.println("Deleted Room Room: " + rate.getRoomRateName());
        }
        
        
    }

    @Override
    public List<RoomRate> getAllRoomRates() throws EmptyListException {
        List<RoomRate> rates;
            
        Query query = em.createQuery("SELECT r FROM RoomRate r");
        rates = query.getResultList();

        if (rates.isEmpty()) throw new EmptyListException("List of Rates is empty.\n");
            
        return rates;
    }

    @Override
    public Long createNewRoomType(RoomType newRoomType) {
        return roomTypeSessionBean.createNewRoomType(newRoomType);
    }

    @Override
    public RoomType getRoomType(Long roomTypeId) {
        
        return roomTypeSessionBean.getRoomTypeByRoomTypeId(roomTypeId);
    }

    @Override
    public RoomType getRoomType(String typeName) {
        
        RoomType type = roomTypeSessionBean.getRoomTypeByRoomTypeName(typeName);
        if (type != null) {
            type.getRooms().size();
            type.getRates().size();
        }
        
        return type;
    }

    @Override
    public void updateRoomType(Long roomTypeId, String name, String desc, Integer size, Integer beds, Integer cap, Integer rank, String amenities) {
        RoomType type = this.getRoomType(roomTypeId);
        type.setRoomTypeName(name);
        type.setRoomTypeDesc(desc);
        type.setSize(size);
        type.setNumOfBeds(beds);
        type.setCapacity(cap);
        type.setTypeRank(rank);
        type.setAmenities(amenities);
    }

    @Override
    public void deleteRoomType(Long roomTypeId) throws EmptyListException  {
        RoomType type = this.getRoomType(roomTypeId);
        List<Reservation> reservations = reservationSessionBean.retrieveAllReservations();
        List<RoomRate> rates = roomRateSessionBean.retrieveAllRoomRates();
        List<Room> rooms = roomSessionBean.retrieveAllRooms();
        
        //Update rankings
        this.updateRoomTypeRankingsDeletion(type.getTypeRank());
        
        for (int i = 0; i < reservations.size(); i++) {
            System.out.println("Inside loop");
            if (reservations.get(i).getRoomType().getRoomTypeId().equals(type.getRoomTypeId())) {
                System.out.println("Found a room type");
                type.setIsDisabled(Boolean.TRUE);
                type.setTypeRank(-1);
                return;
            }
        }
        
        for (RoomRate rate : rates) {
            if (rate.getRoomType().getRoomTypeId().equals(type.getRoomTypeId())) {
                type.setIsDisabled(Boolean.TRUE);
                type.setTypeRank(-1);
                return;
            }
        }
        
        for (Room room : rooms) {
            if (room.getRoomType().getRoomTypeId().equals(type.getRoomTypeId())) {
                type.setIsDisabled(Boolean.TRUE);
                type.setTypeRank(-1);
                return;
            }
        }
        
        //Delete room type
        em.remove(type);
    }

    @Override
    public Room createNewRoom(Room room, Long typeId) {
        //ASSOCIATE ROOM WITH ROOM TYPE
        roomSessionBean.associateRoomWithRoomType(room, typeId);
        //PERSIST ROOM
        Long roomId = roomSessionBean.createNewRoom(room);
        //ASSOCIATE ROOM TYPE WITH ROOM
        roomTypeSessionBean.associateRoomTypeWithRoom(typeId, roomId);
        
        
        return roomSessionBean.getRoomByRoomId(roomId);
    } 

    @Override
    public Room getRoom(int level, int number) {
        return roomSessionBean.getRoomByRoomLevelAndRoomNumber(level, number);
    }
    
    @Override
    public Room getRoom(Long roomId) {
        return roomSessionBean.getRoomByRoomId(roomId);
    }

    @Override
    public void updateRoom(Long roomId, int level, int number, boolean avail, RoomType type) {
        Room room = this.getRoom(roomId);
        type = em.merge(type);
        RoomType currType = room.getRoomType();
        
        room.setIsAvailable(avail);
        room.setRoomLevel(level);
        room.setRoomNum(number);
        if (currType.getRoomTypeId() != type.getRoomTypeId()) {
            currType.getRooms().remove(room);
            room.setRoomType(type);
            type.getRooms().add(room);
            System.out.println("linked new room type to room");
        }
        System.out.println("successfully updated room");
    }
    
    @Override
    public void updateRoomVacancy(Long roomId, boolean vacancy) {
        Room room = this.getRoom(roomId);
        room.setIsVacant(vacancy);
        System.out.println("Room ID: " + roomId + " isVacant: " + vacancy);
    }

    @Override
    public void deleteRoom(Long roomId) {
         //delete/disable room
        Room room = roomSessionBean.getRoomByRoomId(roomId);
        //Get list of allocations
        //check check allocation if the room is used
        //if so, disable it //dont remove from room type
        //if no, delete it.//remove from room type
        List<Allocation> allocations;
        try {
            allocations = allocationSessionBean.retrieveAllAllocations();
            for (Allocation allocation : allocations) {
                allocation = allocationSessionBean.getAllocationByAllocationId(allocation.getAllocationId());
                List<Room> rooms = allocation.getRooms();
                if (rooms.contains(room)) {
                    room.setIsDisabled(Boolean.TRUE);
                    return;
                }
            }
            
            //Room to be deleted
            room.getRoomType().getRooms().remove(room);
            em.remove(room);
        } catch (EmptyListException ex) {
            //Room to be deleted cause there are no allocations
            room.getRoomType().getRooms().remove(room);
            em.remove(room);
        }
    }

    @Override
    public List<Room> retrieveAllRooms() throws EmptyListException {
        return roomSessionBean.retrieveAllRooms();
    }

    @Override
    public void updateRoomTypeRankingsCreation(Integer rank) throws EmptyListException {
        System.out.println(":: Updating Room Type Rankings Creation");
        int lowestRank = roomTypeSessionBean.retrieveAllNotDisabledRoomTypesByRankOrder().size() + 1; //current num of room types
        List<RoomType> types = new ArrayList<>();
        while(rank < lowestRank) {
            RoomType type = roomTypeSessionBean.getNonDisabledRoomTypeByRank(rank++);
            types.add(type);
        }
        
        for (RoomType type: types) {
            type.setTypeRank(type.getTypeRank() + 1);
        }
        
    }
    
    @Override 
    public void updateRoomTypeRankingsDeletion(Integer rank) throws EmptyListException {
        System.out.println(":: Updating Room Type Rankings Deletion");
        int lowestRank = roomTypeSessionBean.retrieveAllNotDisabledRoomTypesByRankOrder().size(); 
        List<RoomType> types = new ArrayList<>();
        while(rank < lowestRank) {
            System.out.println("=========== RANK " + rank);
            RoomType type = roomTypeSessionBean.getNonDisabledRoomTypeByRank(++rank);
            types.add(type);
        }
        for (RoomType type : types) {
            System.out.println(type.getRoomTypeName() + " is lower rank.");
            type.setTypeRank(type.getTypeRank() - 1);
        }
    }

    @Override
    public void updateRoomTypeRankingsUpdate(Integer currRank, Integer newRank) throws EmptyListException {
        System.out.println(":: Updating Room Type Rankings Update");
        //get all room types ordered by rank
        List<RoomType> rTypes = roomTypeSessionBean.retrieveAllNotDisabledRoomTypesByRankOrder();
        RoomType type = rTypes.get(currRank - 1);
        rTypes.remove(type);
        rTypes.add(newRank - 1, type);
        for (int i = 0; i < rTypes.size(); i++) {
            RoomType t = rTypes.get(i);
            t.setTypeRank(i + 1);
        }
          
    }

    @Override
    public List<RoomType> getAllNonDisabledRoomTypes() throws EmptyListException {
        return roomTypeSessionBean.retrieveAllNotDisabledRoomTypesByRankOrder();
    }

    @Override
    public RoomType getRoomTypeByRank(int rank) {
        return roomTypeSessionBean.getNonDisabledRoomTypeByRank(rank);
    }

    
}