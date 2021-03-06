/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.util.List;
import javax.ejb.Local;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
@Local
public interface RoomTypeSessionBeanLocal {
    public List<RoomType> retrieveAllRoomTypes() throws EmptyListException;
    
    public Long createNewRoomType(RoomType roomType);
    public List<RoomRate> getRoomRatesByRoomTypeId(Long id) throws EmptyListException;

    public RoomType getRoomTypeByRoomTypeId(Long newRoomTypeId);

    public RoomType getRoomTypeByRoomTypeName(String typeName);

    public RoomType getNonDisabledRoomTypeByRank(Integer rank);

    public List<RoomType> retrieveAllNotDisabledRoomTypesByRankOrder() throws EmptyListException;

    public void associateRoomTypeWithRoomRate(Long roomTypeId, Long publishedRateDRId);

    public void associateRoomTypeWithRoom(Long roomTypeId, Long room1DRId);
}
