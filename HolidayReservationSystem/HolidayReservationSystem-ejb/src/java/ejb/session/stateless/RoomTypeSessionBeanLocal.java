/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author brend
 */
@Local
public interface RoomTypeSessionBeanLocal {
    public List<RoomType> retrieveAllRoomTypes();
    
    public Long createNewRoomType(RoomType roomType);
}
