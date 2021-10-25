/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import entity.RoomRate;
import entity.RoomType;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.ReservationQueryException;

/**
 *
 * @author brend
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @Override
    public Long createNewReservation(Reservation reservation) {
        em.persist(reservation);
        em.flush();
        
        return reservation.getReservationId();
    }
    
    @Override
    public List<Reservation> retrieveAllReservations() throws ReservationQueryException {
        Query query = em.createQuery("SELECT r FROM Reservation r");
        List<Reservation> reservations = query.getResultList();
        if (reservations.isEmpty()) throw new ReservationQueryException("List of reservations is empty.");
        
        return reservations;
    }
   
   
    @Override
    public List<Reservation> getReservationsByRoomTypeId(Long typeId) throws ReservationQueryException {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.roomType.roomTypeId=:typeId"); //IMPT TO NOTE
        query.setParameter("typeId", typeId);
        
        List<Reservation> reservations = query.getResultList();
        if (reservations.isEmpty()) throw new ReservationQueryException("List of reservations is empty.");
        
        return reservations;
    }
    
    @Override
    public void associateExistingReservationWithGuestAndRoomTypeAndRoomRate(Long reservationId, Long guestId, Long typeId, Long rateId) {
        System.out.println("==== RESERVATION ASSOCIATION ++++");
        Reservation reservation = em.find(Reservation.class, reservationId);
        Guest guest = em.find(Guest.class, guestId);
        RoomType roomType = em.find(RoomType.class, typeId);
        RoomRate roomRate = em.find(RoomRate.class, rateId);
        
        reservation.setCustomer(guest);
        reservation.setRoomType(roomType);
        reservation.setRoomRate(roomRate);
        System.out.println("============ RESERVATION NULL " + (reservation == null));
        System.out.println("============ guest NULL " + (guest == null));
        guest.getReservations().add(reservation);
    }
    
    public boolean isRoomTypeAvailableForReservation(Long typeId, LocalDate startDate, LocalDate endDate) throws ReservationQueryException {
        List<Reservation> reservations = this.getReservationsByRoomTypeId(typeId);
        //exception will throw is reservation isEmpty. Code below are only for reservations not empty.
        //get all available rooms of room type (check isAvailable)
        
        //minus the rooms that have been allocated already that clashes with the dates and (done in AllocationSessionBean)
        
        //tightly pack all available rooms w the current reservations
        
        //count remaining rooms 
        
        
        return true;
    }
}
