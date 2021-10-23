/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import util.exception.FindRoomTypeException;
import java.util.List;
import javax.ejb.Local;
import util.exception.RoomTypeQueryException;

/**
 *
 * @author brend
 */
@Local
public interface RoomTypeSessionBeanLocal {
    public List<RoomType> retrieveAllRoomTypes() throws RoomTypeQueryException;
    
    public Long createNewRoomType(RoomType roomType);
    public List<RoomRate> getRoomRatesByRoomTypeId(Long id);

    public RoomType getRoomTypeByRoomTypeId(Long newRoomTypeId) throws FindRoomTypeException;

    public RoomType getRoomTypeByRoomTypeName(String typeName) throws RoomTypeQueryException;
}
