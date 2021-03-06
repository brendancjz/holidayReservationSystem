/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import java.util.List;
import javax.ejb.Local;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
@Local
public interface RoomRateSessionBeanLocal {
    public Long createNewRoomRate(RoomRate roomRate);
    public List<RoomRate> retrieveAllRoomRates()  throws EmptyListException;

    public RoomRate getRoomRateByRoomRateName(String rateName);

    public RoomRate getRoomRateByRoomRateId(Long rateId) ;

    public void associateRoomRateWithRoomType(RoomRate rate, Long roomTypeId);
}
