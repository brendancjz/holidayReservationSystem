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
import util.exception.ReservationQueryException;

/**
 *
 * @author brend
 */
@Remote
public interface ReservationSessionBeanRemote {

    public List<Reservation> retrieveAllReservations() throws ReservationQueryException;

    public Long createNewReservation(Reservation reservation);

    public List<Reservation> getReservationsByRoomTypeId(Long typeId) throws ReservationQueryException;

    public void associateExistingReservationWithGuestAndRoomTypeAndRoomRate(Long reservationId, Long guestId, Long typeId, Long rateId);

    public boolean isRoomTypeAvailableForReservation(Long typeId, LocalDate startDate, LocalDate endDate, int numOfRooms) throws ReservationQueryException;

    public Reservation getReservationsByRoomTypeIdAndDuration(Long roomTypeId, LocalDate checkInDate, LocalDate checkOutDate) throws ReservationQueryException;

    public int getNumberOfRoomsAvailableForReservation(Long roomTypeId, LocalDate checkInDate, LocalDate checkOutDate);

    public Reservation getReservationByReservationId(Long reservationId);

    public void associateExistingReservationWithGuestAndRoomTypeAndRoomRates(Long reservationId, Long guestId, Long roomTypeId, List<RoomRate> ratesUsed);

    public List<Reservation> getReservationsToAllocate(LocalDate currDate);

    public boolean isRoomTypeAvailableForWalkInReservation(Long roomTypeId, int numOfRooms);
    
}
