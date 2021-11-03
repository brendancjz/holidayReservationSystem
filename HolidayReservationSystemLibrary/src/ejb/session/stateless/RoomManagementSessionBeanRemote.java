/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.enumeration.RoomRateEnum;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
@Remote
public interface RoomManagementSessionBeanRemote {

    public List<RoomType> getAllRoomTypes() throws EmptyListException;

    public List<RoomRate> getRoomRates(Long roomTypeId) throws EmptyListException;

    public RoomRate createNewRoomRate(Long roomTypeId, RoomRateEnum rateEnum, LocalDateTime startDate, LocalDateTime endDate, double rateAmount);

    public RoomRate getRoomRate(String rateName);
    public RoomRate getRoomRate(Long rateId);

    public void updateRoomRate(Long rateId, String name, Double amount, Date startDate, Date endDate);

    public void deleteRoomRate(Long roomRateId);

    public List<RoomRate> getAllRoomRates() throws EmptyListException;

    public Long createNewRoomType(RoomType newRoomType);

    public RoomType getRoomType(Long newRoomTypeId);

    public RoomType getRoomType(String typeName);

    public void updateRoomType(Long roomTypeId, String name, String desc, Integer size, Integer beds, Integer cap, Integer rank, String amenities);

    public void deleteRoomType(Long roomTypeId) throws EmptyListException;

    public Room createNewRoom(Room newRoom, Long roomTypeId);

    public Room getRoom(int level, int number) ;
    public Room getRoom(Long roomId);
    
    public void updateRoom(Long roomId, int level, int number, boolean avail, RoomType type);

    public void deleteRoom(Long roomId);

    public List<Room> retrieveAllRooms() throws EmptyListException;
    
    public void updateRoomVacancy(Long roomId, boolean vacancy);

    public void updateRoomTypeRankingsCreation(Integer rank) throws EmptyListException;
    public void updateRoomTypeRankingsDeletion(Integer rank) throws EmptyListException;
    public void updateRoomTypeRankingsUpdate(Integer currRank, Integer newRank) throws EmptyListException;

    public List<RoomType> getAllNonDisabledRoomTypes() throws EmptyListException;

    public RoomType getRoomTypeByRank(int rank);
    
}
