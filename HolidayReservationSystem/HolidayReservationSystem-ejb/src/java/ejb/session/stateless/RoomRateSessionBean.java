/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.EmptyListException;

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
    public List<RoomRate> retrieveAllRoomRates() throws EmptyListException {
        Query query = em.createQuery("SELECT r FROM RoomRate r");

        List<RoomRate>  rates = query.getResultList();
        
        if (rates.isEmpty()) throw new EmptyListException("List of RoomRates is empty.\n");
        for (int i = 0; i < rates.size(); i++) {
            RoomRate rate = rates.get(i);
            rate.getRoomType();
        }
        
        return rates;
    }

    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public RoomRate getRoomRateByRoomRateName(String rateName) {
        Query query = em.createQuery("SELECT r FROM RoomRate r WHERE r.roomRateName=:name");
        query.setParameter("name", rateName);
        
        try {
             RoomRate rate = (RoomRate) query.getSingleResult();
             rate.getRoomType();
             return rate;
        } catch (NoResultException e) {
            return null;
        }
        
       
    }

    @Override
    public RoomRate getRoomRateByRoomRateId(Long rateId) {
        RoomRate rate = em.find(RoomRate.class, rateId);
        rate.getRoomType();
        return rate;
    }

    @Override
    public void associateRoomRateWithRoomType(RoomRate rate1, Long roomTypeId) {
        RoomType type = em.find(RoomType.class, roomTypeId);
        rate1.setRoomType(type);
    }
}
