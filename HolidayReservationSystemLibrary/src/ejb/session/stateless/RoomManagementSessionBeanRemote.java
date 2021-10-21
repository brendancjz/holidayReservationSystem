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
import util.exception.FindRoomRateException;
import util.exception.FindRoomTypeException;
import util.exception.ReservationQueryException;
import util.exception.RoomRateQueryException;
import util.exception.RoomTypeQueryException;

/**
 *
 * @author brend
 */
@Remote
public interface RoomManagementSessionBeanRemote {

    public List<RoomType> getAllRoomTypes() throws RoomTypeQueryException;

    public List<RoomRate> getRoomRates(Long roomTypeId) throws FindRoomTypeException;

    public RoomRate createNewRoomRate(Long roomTypeId, String rateEnum, LocalDateTime startDate, LocalDateTime endDate, double rateAmount);

    public RoomRate getRoomRate(String rateName) throws RoomRateQueryException;
    public RoomRate getRoomRate(Long rateId) throws FindRoomRateException;

    public void updateRoomRate(Long rateId, String name, Double amount, Date startDate, Date endDate) throws FindRoomRateException;

    public void deleteRoomRate(Long roomRateId) throws FindRoomRateException, ReservationQueryException;

    public List<RoomRate> getAllRoomRates() throws RoomRateQueryException;

    public Long createNewRoomType(RoomType newRoomType);

    public RoomType getRoomType(Long newRoomTypeId) throws FindRoomTypeException;

    public RoomType getRoomType(String typeName) throws RoomTypeQueryException;

    public void updateRoomType(Long roomTypeId, String name, String desc, Integer size, Integer beds, Integer cap, String amenities) throws FindRoomTypeException;

    public void deleteRoomType(Long roomTypeId) throws FindRoomTypeException, ReservationQueryException, FindRoomRateException ;

    public Room createNewRoom(Room newRoom, Long roomTypeId);

    
    
}
