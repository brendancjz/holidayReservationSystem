/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.util.List;
import javax.ejb.Remote;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
@Remote
public interface RoomTypeSessionBeanRemote {

    public List<RoomType> retrieveAllRoomTypes() throws EmptyListException;

    public Long createNewRoomType(RoomType roomType);

    public List<RoomRate> getRoomRatesByRoomTypeId(Long id) throws EmptyListException;

    public void associateRoomTypeWithRoomRate(Long roomTypeId, Long publishedRateDRId);
    
    public void associateRoomTypeWithRoom(Long roomTypeId, Long room1DRId);
}
