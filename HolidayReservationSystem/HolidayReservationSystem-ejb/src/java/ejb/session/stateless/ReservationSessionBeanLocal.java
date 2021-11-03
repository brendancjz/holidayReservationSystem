/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import java.util.List;
import javax.ejb.Local;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
@Local
public interface ReservationSessionBeanLocal {
    public List<Reservation> retrieveAllReservations() throws EmptyListException;

    public Long createNewReservation(Reservation reservation);
    
    public List<Reservation> getReservationsByRoomTypeId(Long typeId) throws EmptyListException;

    public void associateReservationWithGuestAndRoomTypeAndRoomRate(Reservation reservation, Long guestId, Long typeId, Long rateId);
}
