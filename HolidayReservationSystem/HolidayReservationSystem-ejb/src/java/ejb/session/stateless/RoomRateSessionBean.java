/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author brend
 */
@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewRoomRate(RoomRate roomRate) {
        em.persist(roomRate);
        em.flush();
        
        return roomRate.getRoomRateId();
    }
    
    @Override
    public List<RoomRate> retrieveAllRoomRates() {
        List<RoomRate> rooms = null;
        try {
            Query query = em.createQuery("SELECT r FROM RoomRate r");
            
            rooms = query.getResultList();
        } catch (Exception e) {
            System.out.println("** retrieveAllRoomRates throwing error " + e.getMessage());
        }
        
        return rooms;
    }

    @Override
    public List<RoomRate> getRoomRatesByRoomTypeIdAndDates(Long roomTypeId, String checkIn, String checkOut) {
        List<RoomRate> rates = null;
        try {
            Query query = em.createQuery("SELECT r FROM RoomRate r WHERE r.roomType=?1 "
                    + "AND r.startDate<=?2 AND r.endDate>=?3 OR (r.startDate IS NULL AND r.endDate IS NULL)");
            query.setParameter(1, roomTypeId);
            query.setParameter(2, checkIn);
            query.setParameter(3, checkOut);
            
            rates = query.getResultList();
            
        } catch (Exception e) {
            System.out.println("** getRoomRatesByRoomTypeIdAndDates throwing error " + e.getMessage());
        }
        
        return rates;
        
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
