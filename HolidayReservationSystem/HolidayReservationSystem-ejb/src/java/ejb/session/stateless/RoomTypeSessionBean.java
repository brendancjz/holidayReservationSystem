/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.FindRoomTypeException;
import util.exception.RoomTypeQueryException;

/**
 *
 * @author brend
 */
@Stateless
public class RoomTypeSessionBean implements RoomTypeSessionBeanRemote, RoomTypeSessionBeanLocal {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewRoomType(RoomType roomType) {
        em.persist(roomType);
        em.flush();
        
        return roomType.getRoomTypeId();
    }
    
    @Override
    public List<RoomType> retrieveAllRoomTypes() throws RoomTypeQueryException{
        Query query = em.createQuery("SELECT r FROM RoomType r");

        List<RoomType> types = query.getResultList();

        if (types.isEmpty()) throw new RoomTypeQueryException("list of RoomTypes is empty.");
        
        
        return types;
    }
    
    @Override
    public List<RoomRate> getRoomRatesByRoomTypeId(Long id) throws FindRoomTypeException {

        RoomType roomType = em.find(RoomType.class, id);
        List<RoomRate> rates = roomType.getRates();

        if (rates.isEmpty()) throw new FindRoomTypeException("List of rates is empty");
        
        
        return rates;
    }

    @Override
    public RoomType getRoomTypeByRoomTypeId(Long newRoomTypeId) throws FindRoomTypeException{
        RoomType type;
        
        type = em.find(RoomType.class, newRoomTypeId);
        if (type == null) throw new FindRoomTypeException("RoomType is null");
        
        return type;
    }
}
