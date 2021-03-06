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
public interface RoomManagementSessionBeanLocal {
    public List<RoomType> getAllRoomTypes() throws EmptyListException;

    public List<RoomRate> getRoomRates(Long roomTypeId) throws EmptyListException;
    public RoomType getRoomTypeByRank(int rank);
}
