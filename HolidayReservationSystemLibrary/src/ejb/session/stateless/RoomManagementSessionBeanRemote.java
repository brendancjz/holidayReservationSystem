/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.time.LocalDateTime;
import java.util.List;
import javax.ejb.Remote;
import util.exception.FindRoomTypeException;
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
    
}
