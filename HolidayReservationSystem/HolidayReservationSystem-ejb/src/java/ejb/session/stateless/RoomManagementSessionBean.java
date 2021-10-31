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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.RoomRateEnum;
import util.exception.AllocationQueryException;
import util.exception.FindRoomException;
import util.exception.FindRoomRateException;
import util.exception.FindRoomTypeException;
import util.exception.ReservationQueryException;
import util.exception.RoomQueryException;
import util.exception.RoomRateQueryException;
import util.exception.RoomTypeQueryException;

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
    public List<RoomType> getAllRoomTypes() throws RoomTypeQueryException {
        return roomTypeSessionBean.retrieveAllRoomTypes();
    }
    
    @Override
    public List<RoomRate> getRoomRates(Long roomTypeId) {
        return roomTypeSessionBean.getRoomRatesByRoomTypeId(roomTypeId);
    }

    @Override
    public RoomRate createNewRoomRate(Long roomTypeId, RoomRateEnum rateEnum, LocalDateTime startDate, LocalDateTime endDate, double rateAmount) {
        Long roomRateId;
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        String roomRateName = rateEnum + roomType.getRoomTypeName();
        if (startDate == null && endDate == null) {
            roomRateId = roomRateSessionBean.createNewRoomRate(new RoomRate(roomRateName,rateEnum,rateAmount));
        } else {
            Date start = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
            roomRateId = roomRateSessionBean.createNewRoomRate(new RoomRate(roomRateName,rateEnum,rateAmount, start, end));
        }
        
        
        //Link Room Type to Room Rate
        //Link Room Rate to Room Type
        RoomRate roomRate = em.find(RoomRate.class, roomRateId);
        roomRate.setRoomType(roomType);
        ArrayList<RoomRate> rates = roomType.getRates();
        rates.add(roomRate);
        
        return roomRate;
    }

    @Override
    public RoomRate getRoomRate(String rateName) throws RoomRateQueryException {
        return roomRateSessionBean.getRoomRateByRoomRateName(rateName);
    }
    
    @Override
    public RoomRate getRoomRate(Long rateId) throws FindRoomRateException {
        return roomRateSessionBean.getRoomRateByRoomRateId(rateId);
    }

    @Override
    public void updateRoomRate(Long rateId, String name, Double amount, Date startDate, Date endDate) throws FindRoomRateException {
        RoomRate rate = this.getRoomRate(rateId);
        
        rate.setRoomRateName(name);
        rate.setRatePerNight(amount);
        rate.setStartDate(startDate);
        rate.setEndDate(endDate);
    }

    @Override
    public void deleteRoomRate(Long roomRateId) throws FindRoomRateException {
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
        } catch (ReservationQueryException ex) {
            //Delete cause no one using
            rate.getRoomType().getRates().remove(rate);
            em.remove(rate);
            System.out.println("Deleted Room Room: " + rate.getRoomRateName());
        }
        
        
    }

    @Override
    public List<RoomRate> getAllRoomRates() throws RoomRateQueryException {
        List<RoomRate> rates;
            
        Query query = em.createQuery("SELECT r FROM RoomRate r");
        rates = query.getResultList();

        if (rates.isEmpty()) throw new RoomRateQueryException("List of Rates is empty");
            
        return rates;
    }

    @Override
    public Long createNewRoomType(RoomType newRoomType) {
        return roomTypeSessionBean.createNewRoomType(newRoomType);
    }

    @Override
    public RoomType getRoomType(Long roomTypeId) throws FindRoomTypeException {
        return roomTypeSessionBean.getRoomTypeByRoomTypeId(roomTypeId);
    }

    @Override
    public RoomType getRoomType(String typeName) throws RoomTypeQueryException {
        RoomType type = roomTypeSessionBean.getRoomTypeByRoomTypeName(typeName);
        type.getRooms().size();
        return type;
    }

    @Override
    public void updateRoomType(Long roomTypeId, String name, String desc, Integer size, Integer beds, Integer cap, Integer rank, String amenities) throws FindRoomTypeException, RoomTypeQueryException {
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
    public void deleteRoomType(Long roomTypeId) throws FindRoomTypeException, RoomTypeQueryException, RoomRateQueryException, RoomQueryException, ReservationQueryException, FindRoomRateException, FindRoomException  {
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
        Long roomId = roomSessionBean.createNewRoom(room);
        
        //Link Room Type with Room
        RoomType roomType = em.find(RoomType.class, typeId);
        room = em.find(Room.class, roomId);
        
        roomType.getRooms().add(room);
        room.setRoomType(roomType);
        
        return room;
    } 

    @Override
    public Room getRoom(int level, int number) throws RoomQueryException {
        return roomSessionBean.getRoomByRoomLevelAndRoomNumber(level, number);
    }
    
    @Override
    public Room getRoom(Long roomId) throws FindRoomException {
        return roomSessionBean.getRoomByRoomId(roomId);
    }

    @Override
    public void updateRoom(Long roomId, int level, int number, boolean avail, RoomType type) throws FindRoomException {
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
    public void updateRoomVacancy(Long roomId, boolean vacancy) throws FindRoomException {
        Room room = this.getRoom(roomId);
        room.setIsVacant(vacancy);
        System.out.println("Room ID: " + roomId + " isVacant: " + vacancy);
    }

    @Override
    public void deleteRoom(Long roomId) throws FindRoomException, ReservationQueryException {
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
        } catch (AllocationQueryException ex) {
            //Room to be deleted cause there are no allocations
            room.getRoomType().getRooms().remove(room);
            em.remove(room);
        }
    }

    @Override
    public List<Room> retrieveAllRooms() throws RoomQueryException {
        return roomSessionBean.retrieveAllRooms();
    }

    @Override
    public void updateRoomTypeRankingsCreation(Integer rank) throws RoomTypeQueryException {
        System.out.println("============= updateRoomTypeRankingsCreation");
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
    public void updateRoomTypeRankingsDeletion(Integer rank) throws RoomTypeQueryException {
        System.out.println("========= updateRoomTypeRankingsDeletion");
        int lowestRank = roomTypeSessionBean.retrieveAllNotDisabledRoomTypesByRankOrder().size(); 
        List<RoomType> types = new ArrayList<>();
        while(rank < lowestRank) {
            System.out.println("=========== RANK " + rank);
            RoomType type = roomTypeSessionBean.getNonDisabledRoomTypeByRank(++rank);
            types.add(type);
        }
        for (RoomType type : types) {
            System.out.println("TYPES THAT ARE LOWER RANK:" + type.getRoomTypeName());
            type.setTypeRank(type.getTypeRank() - 1);
        }
    }

    @Override
    public void updateRoomTypeRankingsUpdate(Integer currRank, Integer newRank) throws RoomTypeQueryException {
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
    public List<RoomType> getAllNonDisabledRoomTypes() throws RoomTypeQueryException {
        return roomTypeSessionBean.retrieveAllNotDisabledRoomTypesByRankOrder();
    }

    @Override
    public RoomType getRoomTypeByRank(int rank) {
        return roomTypeSessionBean.getNonDisabledRoomTypeByRank(rank);
    }

    
}