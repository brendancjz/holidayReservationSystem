/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
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
    public List<Room> retrieveAllRooms() throws EmptyListException {
        Query query = em.createQuery("SELECT r FROM Room r");

        List<Room> rooms  = query.getResultList();
        if (rooms.isEmpty()) throw new EmptyListException("Room list is empty.\n");
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            room.getRoomType();
        }
        return rooms;
    }

    @Override
    public Room getRoomByRoomLevelAndRoomNumber(int level, int number) {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomLevel=:level AND r.roomNum=:num");
        query.setParameter("level", level);
        query.setParameter("num", number);
        try {
            Room room = (Room) query.getSingleResult();
            room.getRoomType();
            return room;
        } catch (NoResultException e) {
            return null;
        }
        
        
        
    }

    @Override
    public Room getRoomByRoomId(Long roomId) {
        Room room = em.find(Room.class, roomId);
        room.getRoomType();
        return room;
    }

    @Override
    public void associateRoomWithRoomType(Room room, Long roomTypeId) {
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        room.setRoomType(roomType);
    }
}
