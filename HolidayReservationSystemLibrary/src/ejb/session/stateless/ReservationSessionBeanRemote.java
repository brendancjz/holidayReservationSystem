/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.RoomRate;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Remote;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
@Remote
public interface ReservationSessionBeanRemote {

    public List<Reservation> retrieveAllReservations() throws EmptyListException;

    public Long createNewReservation(Reservation reservation);

    public List<Reservation> getReservationsByRoomTypeId(Long typeId) throws EmptyListException;

    public void associateReservationWithGuestAndRoomTypeAndRoomRate(Reservation reservation, Long guestId, Long typeId, Long rateId);

    public boolean isRoomTypeAvailableForReservation(Long typeId, LocalDate startDate, LocalDate endDate, int numOfRooms);

    public Reservation getReservationsByRoomTypeIdAndDuration(Long roomTypeId, LocalDate checkInDate, LocalDate checkOutDate, Long guestId) throws EmptyListException;

    public int getNumberOfRoomsAvailableForReservation(Long roomTypeId, LocalDate checkInDate, LocalDate checkOutDate);

    public Reservation getReservationByReservationId(Long reservationId);

    public void associateReservationWithGuestAndRoomTypeAndRoomRates(Reservation reservation, Long guestId, Long roomTypeId, List<RoomRate> ratesUsed);

    public List<Reservation> getReservationsToAllocate(LocalDate currDate);

    public boolean isRoomTypeAvailableForWalkInReservation(Long roomTypeId, int numOfRooms);

    public Long createNewReservation(Reservation reservation, Long guestId, Long roomTypeId, List<RoomRate> ratesUsed);
    
}
