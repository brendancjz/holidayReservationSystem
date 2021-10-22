/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import java.util.List;
import javax.ejb.Local;
import util.exception.FindRoomException;
import util.exception.RoomQueryException;

/**
 *
 * @author brend
 */
@Local
public interface RoomSessionBeanLocal {
    public Long createNewRoom(Room room);
    public List<Room> retrieveAllRooms() throws RoomQueryException;

    public Room getRoomByRoomLevelAndRoomNumber(int level, int number) throws RoomQueryException;

    public Room getRoomByRoomId(Long roomId) throws FindRoomException;
}
