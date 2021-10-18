/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
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
        
        for (Reservation reservation : reservations) {
            if (reservation.getRoomRate().getRoomRateId().equals(rate.getRoomRateId())) {
                rate.setIsDisabled(Boolean.TRUE);
                return;
            }
        } 
        
        rate.getRoomType().getRates().remove(rate);
        
        em.remove(rate);
    }

   
}
