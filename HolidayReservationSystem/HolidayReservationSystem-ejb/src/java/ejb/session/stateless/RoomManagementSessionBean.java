/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

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
import util.exception.FindRoomRateException;
import util.exception.FindRoomTypeException;
import util.exception.ReservationQueryException;
import util.exception.RoomRateQueryException;
import util.exception.RoomTypeQueryException;

/**
 *
 * @author brend
 */
@Stateless
public class RoomManagementSessionBean implements RoomManagementSessionBeanRemote, RoomManagementSessionBeanLocal {

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
    public List<RoomRate> getRoomRates(Long roomTypeId) throws FindRoomTypeException{
        return roomTypeSessionBean.getRoomRatesByRoomTypeId(roomTypeId);
    }

    @Override
    public RoomRate createNewRoomRate(Long roomTypeId, String rateEnum, LocalDateTime startDate, LocalDateTime endDate, double rateAmount) {
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
    public void deleteRoomRate(Long roomRateId) throws FindRoomRateException, ReservationQueryException {
        RoomRate rate = roomRateSessionBean.getRoomRateByRoomRateId(roomRateId);
        List<Reservation> reservations = reservationSessionBean.retrieveAllReservations();
        
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getRoomRate().getRoomRateId().equals(rate.getRoomRateId())) {
                rate.setIsDisabled(Boolean.TRUE);
                System.out.println("Room Rate " + rate.getRoomRateName() + " is now disabled.");
                return;
            }
        } 
        
        //Delete cause no one using
        rate.getRoomType().getRates().remove(rate);
        
        em.remove(rate);
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
    public void updateRoomType(Long roomTypeId, String name, String desc, Integer size, Integer beds, Integer cap, String amenities) throws FindRoomTypeException {
        RoomType type = this.getRoomType(roomTypeId);
        type.setRoomTypeName(name);
        type.setRoomTypeDesc(desc);
        type.setSize(size);
        type.setNumOfBeds(beds);
        type.setCapacity(cap);
        type.setAmenities(amenities);
    }

    @Override
    public void deleteRoomType(Long roomTypeId) throws FindRoomTypeException, ReservationQueryException, FindRoomRateException  {
        RoomType type = this.getRoomType(roomTypeId);
        List<Reservation> reservations = reservationSessionBean.retrieveAllReservations();
        
        for (int i = 0; i < reservations.size(); i++) {
            System.out.println("Inside loop");
            if (reservations.get(i).getRoomType().getRoomTypeId().equals(type.getRoomTypeId())) {
                System.out.println("Found a room type");
                type.setIsDisabled(Boolean.TRUE);
                break;
            }
        }
        
        if (!type.getIsDisabled()) {

            List<RoomRate> rates = type.getRates();
            for (RoomRate rate : rates) {
                em.remove(rate);
            }
            List<Room> rooms = type.getRooms();
            for (Room room : rooms) {
                em.remove(room);
            }
            em.flush();
            em.remove(type);
        } else {
            List<RoomRate> rates = type.getRates();
            for (int i = 0; i < rates.size(); i++) {
                System.out.println("== ROOM RATE " + rates.get(i).getRoomRateName());
                this.deleteRoomRate(rates.get(i).getRoomRateId());
            }

    //            //disable/delete all the room rooms tagged to room type
    //            List<Room> rooms = type.getRooms();
    //            for (Room room : rooms) {
    //                this.deleteRoom(room.getRoomId());
    //            }
        }
        
        
        
    }

    private void deleteRoom(Long roomId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
}
