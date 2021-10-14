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

/**
 *
 * @author brend
 */
@Remote
public interface RoomTypeSessionBeanRemote {

    public List<RoomType> retrieveAllRoomTypes();

    public Long createNewRoomType(RoomType roomType);

    public List<RoomRate> getRoomRatesByRoomTypeId(Long id);
    
}
