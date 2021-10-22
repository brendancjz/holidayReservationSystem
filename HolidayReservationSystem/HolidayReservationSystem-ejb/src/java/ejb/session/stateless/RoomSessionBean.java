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
import util.exception.FindRoomException;
import util.exception.RoomQueryException;

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
    public List<Room> retrieveAllRooms() throws RoomQueryException {
        
        
        Query query = em.createQuery("SELECT r FROM Room r");

        List<Room> rooms  = query.getResultList();
        if (rooms.isEmpty()) throw new RoomQueryException("Room list is missing");
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);

            if (room.getIsDisabled()) { //Only return the rooms that are not disabled.
                rooms.remove(i);
            }
        }
        return rooms;
    }

    @Override
    public Room getRoomByRoomLevelAndRoomNumber(int level, int number) throws RoomQueryException {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomLevel=:level AND r.roomNum=:num");
        query.setParameter("level", level);
        query.setParameter("num", number);
        
        List<Room> room = query.getResultList();
        if (room.isEmpty()) throw new RoomQueryException("Room is missing");
        return room.get(0);
    }

    @Override
    public Room getRoomByRoomId(Long roomId) throws FindRoomException {
        Room room = em.find(Room.class, roomId);
        
        if (room == null) throw new FindRoomException("Room is null");
        
        return room;
    }
}
