/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.FindRoomTypeException;
import util.exception.RoomTypeQueryException;

/**
 *
 * @author brend
 */
@Stateless
public class RoomManagementSessionBean implements RoomManagementSessionBeanRemote, RoomManagementSessionBeanLocal {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;

    @EJB
    private RoomSessionBeanLocal roomSessionBean;
    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBean;

    @Override
    public List<RoomType> getAllRoomTypes() throws RoomTypeQueryException {
        return roomTypeSessionBean.retrieveAllRoomTypes();
    }
    
    @Override
    public List<RoomRate> getRoomRates(Long roomTypeId) throws FindRoomTypeException{
        return roomTypeSessionBean.getRoomRatesByRoomTypeId(roomTypeId);
    }

    @Override
    public void createNewRoomRate(Long roomTypeId, String rateEnum, LocalDateTime startDate, LocalDateTime endDate, double rateAmount) {
        Long roomRateId;
        String roomRateName = rateEnum + em.find(RoomType.class, roomTypeId).getRoomTypeName();
        if (startDate == null && endDate == null) {
            roomRateId = roomRateSessionBean.createNewRoomRate(new RoomRate(roomRateName,rateEnum,rateAmount));
        } else {
            Date start = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
            roomRateId = roomRateSessionBean.createNewRoomRate(new RoomRate(roomRateName,rateEnum,rateAmount, start, end));
        }
        
        //Link room type to roomrate
    }

   
}
