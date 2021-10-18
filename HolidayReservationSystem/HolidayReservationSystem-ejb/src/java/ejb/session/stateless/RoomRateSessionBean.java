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
import util.exception.RoomRateQueryException;

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
    public List<RoomRate> retrieveAllRoomRates() throws RoomRateQueryException {
        Query query = em.createQuery("SELECT r FROM RoomRate r");

        List<RoomRate>  rates = query.getResultList();
        
        if (rates.isEmpty()) throw new RoomRateQueryException("List of RoomRates is empty");
        
        return rates;
    }

    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    @Override
    public RoomRate getRoomRateByRoomRateName(String rateName) throws RoomRateQueryException {
        Query query = em.createQuery("SELECT r FROM RoomRate r WHERE r.roomRateName=:name");
        query.setParameter("name", rateName);
        List<RoomRate> rates = query.getResultList();
        if (rates.isEmpty()) throw new RoomRateQueryException("No such Rate Name in Database.");
        
        return rates.get(0);
    }
}
