/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomRate;
import entity.RoomType;
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
        for (int i = 0; i < types.size(); i++) {
            RoomType type = types.get(i);
            type.getRooms().size();
            type.getRates().size();
            
//            if (type.getIsDisabled()) { //Only return the room types that are not disabled
//                types.remove(i);
//                System.out.println("-- removed room type from list.");
//            }
            
            
        } 
        
        return types;
    }
    
    @Override
    public List<RoomRate> getRoomRatesByRoomTypeId(Long id) {

        RoomType roomType = em.find(RoomType.class, id);
        List<RoomRate> rates = roomType.getRates();
        return rates;
    }

    @Override
    public RoomType getRoomTypeByRoomTypeId(Long newRoomTypeId) throws FindRoomTypeException{
        RoomType type;
        
        type = em.find(RoomType.class, newRoomTypeId);
        if (type == null) throw new FindRoomTypeException("RoomType is null");
        
        type.getRooms().size();
        type.getRates().size();
        return type;
    }

    @Override
    public RoomType getRoomTypeByRoomTypeName(String typeName) throws RoomTypeQueryException {
        Query query = em.createQuery("SELECT r FROM RoomType r WHERE r.roomTypeName=:name");
        query.setParameter("name", typeName);
        
        List<RoomType> types = query.getResultList();
        
        if (types.isEmpty()) throw new RoomTypeQueryException("Invalid Room Type Name");
        RoomType type = types.get(0);
        type.getRooms().size();
        type.getRates().size();
        return type;
    }

    @Override
    public RoomType getNonDisabledRoomTypeByRank(Integer rank) {
        Query query = em.createQuery("SELECT r FROM RoomType r WHERE r.typeRank = :rank AND r.isDisabled = false");
        query.setParameter("rank", rank);
        RoomType type = (RoomType) query.getSingleResult();
        if (type.getRooms() != null) type.getRooms().size();
        return type;
    }

    @Override
    public List<RoomType> retrieveAllNotDisabledRoomTypesByRankOrder() throws RoomTypeQueryException {
        Query query = em.createQuery("SELECT r FROM RoomType r WHERE r.isDisabled = false ORDER BY r.typeRank ASC");
        
        List<RoomType> types = query.getResultList();
        if (types.isEmpty()) throw new RoomTypeQueryException("List of RoomTypes is empty.");
        for (int i = 0; i < types.size(); i++) {
            RoomType type = types.get(i);
            type.getRooms().size();
            type.getRates().size();         
        }
        return types;
    }

    @Override
    public void associateRoomTypeWithRoomRate(Long roomTypeId, Long publishedRateDRId) {
        RoomType type = em.find(RoomType.class, roomTypeId);
        RoomRate rate = em.find(RoomRate.class, publishedRateDRId);
        
        type.getRates().add(rate);
    }

    @Override
    public void associateRoomTypeWithRoom(Long roomTypeId, Long roomId) {
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        Room room = em.find(Room.class, roomId);
        
        roomType.getRooms().add(room);
    }
}
