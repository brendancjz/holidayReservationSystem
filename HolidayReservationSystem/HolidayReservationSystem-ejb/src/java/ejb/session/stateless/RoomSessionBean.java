/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
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
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public Long createNewRoom(Room room) {
        em.persist(room);
        em.flush();
        
        return room.getRoomId();
    }
    
    @Override
    public List<Room> retrieveAllRooms() {
        List<Room> rooms = null;
        try {
            Query query = em.createQuery("SELECT r FROM Room r");
            
            rooms = query.getResultList();
        } catch (Exception e) {
            System.out.println("** retrieveAllRooms throwing error " + e.getMessage());
        }
        
        return rooms;
    }
}
