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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
    public List<RoomType> retrieveAllRoomTypes() {
        List<RoomType> rooms = null;
        try {
            Query query = em.createQuery("SELECT r FROM RoomType r");
            
            rooms = query.getResultList();
        } catch (Exception e) {
            System.out.println("** retrieveAllRoomTypes throwing error " + e.getMessage());
        }
        
        return rooms;
    }
    
    @Override
    public List<RoomRate> getRoomRatesByRoomTypeId(Long id) {
        List<RoomRate> rates = null;
        try {
            RoomType roomType = em.find(RoomType.class, id);
            rates = roomType.getRates();
        } catch (Exception e) {
            System.out.println("** getRoomRatesByRoomTypeId throwing error " + e.getMessage());
        }
        
        return rates;
    }
}
