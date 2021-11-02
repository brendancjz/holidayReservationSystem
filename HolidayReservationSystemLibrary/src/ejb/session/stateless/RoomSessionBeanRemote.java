/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import java.util.List;
import javax.ejb.Remote;
import util.exception.RoomQueryException;

/**
 *
 * @author brend
 */
@Remote
public interface RoomSessionBeanRemote {

    public Long createNewRoom(Room room);

    public List<Room> retrieveAllRooms() throws RoomQueryException;
    
    public void associateRoomWithRoomType(Room room1, Long roomTypeId);
}
