/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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
        
        Reservation reservation = em.find(Reservation.class, reservationId);
        Guest guest = em.find(Guest.class, guestId);
        RoomType roomType = em.find(RoomType.class, typeId);
        RoomRate roomRate = em.find(RoomRate.class, rateId);
        
        System.out.println("Inside associateExistingReservationWithGuestAndRoomTypeAndRoomRate() method");
        
        System.out.println(guest.getReservations() == null);
        reservation.setCustomer(guest);
        reservation.setRoomType(roomType);
        reservation.getRoomRates().add(roomRate);
        
        guest.getReservations().add(reservation);
    }
    
    @Override
    public void associateExistingReservationWithGuestAndRoomTypeAndRoomRates(Long reservationId, Long guestId, Long typeId, List<RoomRate> ratesUsed) {
        Reservation reservation = em.find(Reservation.class, reservationId);
        Guest guest = em.find(Guest.class, guestId);
        RoomType roomType = em.find(RoomType.class, typeId);
        for (RoomRate rate : ratesUsed) {
            em.merge(rate);
            reservation.getRoomRates().add(rate);
        }
        reservation.setCustomer(guest);
        reservation.setRoomType(roomType);
        
        
        guest.getReservations().add(reservation);
    }
    
    @Override
    public boolean isRoomTypeAvailableForReservation(Long typeId, LocalDate startDate, LocalDate endDate, int numOfRooms) throws ReservationQueryException {
        //1. Get num of rooms that are avail
        //2. Get num of reservations that collide with range, meaning num of rooms required to take those reservation
        //3. (1.) - (2.) and if this number is greater or equals to numOfRooms the person wants to reserve, return true;
        RoomType type = em.find(RoomType.class, typeId);
        type.getRooms().size();
        
        int count = 0;
        for (Room room : type.getRooms()) {
            if (room.getIsAvailable()) {
                
                count++;
            }
        }
        int countOfRoomsRequired = 0;
        
        try {
            List<Reservation> reservationsOfRoomType = this.getReservationsByRoomTypeId(typeId);
            
            for (Reservation reservation: reservationsOfRoomType) {
                Date start = reservation.getStartDate();
                Date end = reservation.getEndDate();
                if(isCollided(start, end, startDate, endDate)) {
                    countOfRoomsRequired++;
                }
            }
        } catch (ReservationQueryException e) {
            //no reservations, no problems;
            countOfRoomsRequired = 0;
        }
        

        
        
        return (count - countOfRoomsRequired - numOfRooms) >= 0;
    }
    
    private boolean isCollided(Date start, Date end, LocalDate startDate, LocalDate endDate) {
        LocalDate start1 = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end1 = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        boolean lowerBound = (start1.isEqual(startDate) || start1.isAfter(startDate)) && start1.isBefore(endDate);
        boolean upperBound = (end1.isBefore(endDate) || end1.isEqual(endDate)) && end1.isAfter(startDate);
        return lowerBound || upperBound;
    }

    @Override
    public Reservation getReservationsByRoomTypeIdAndDuration(Long roomTypeId, LocalDate checkInDate, LocalDate checkOutDate) throws ReservationQueryException {
        Date endDate = Date.from(checkOutDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date startDate = Date.from(checkInDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Query query = em.createQuery("SELECT r FROM Reservation r "
                + "WHERE r.roomType.roomTypeId = :typeId AND r.startDate = :start AND r.endDate = :end");
        query.setParameter("typeId", roomTypeId);
        query.setParameter("start", startDate);
        query.setParameter("end", endDate);
        
        List<Reservation> reservation = query.getResultList();
        
        if (reservation.isEmpty()) throw new ReservationQueryException("No such Reservation in record.");
        reservation.get(0).getRoomRates().size();
        return reservation.get(0);
    }

    @Override
    public int getNumberOfRoomsAvailableForReservation(Long typeId, LocalDate startDate, LocalDate endDate) {
        RoomType type = em.find(RoomType.class, typeId);
        type.getRooms().size();
        
        int count = 0;
        for (Room room : type.getRooms()) {
            if (room.getIsAvailable()) {
                
                count++;
            }
        }
        int countOfRoomsRequired = 0;
        try {
            List<Reservation> reservationsOfRoomType = this.getReservationsByRoomTypeId(typeId);
            
            for (Reservation reservation: reservationsOfRoomType) {
                Date start = reservation.getStartDate();
                Date end = reservation.getEndDate();
                if(isCollided(start, end, startDate, endDate)) {
                    countOfRoomsRequired++;
                }
            }
        } catch (ReservationQueryException e) {
            
        }
        
        //exception will throw is reservation isEmpty. Code below are only for reservations not empty.
        //get all available rooms of room type (check isAvailable)
        
        //minus the rooms that have been allocated already that clashes with the dates and (done in AllocationSessionBean)
        
        //tightly pack all available rooms w the current reservations
        
        //count remaining rooms
        return (count - countOfRoomsRequired);
    }

    @Override
    public Reservation getReservationByReservationId(Long reservationId) {
        Reservation reservation = em.find(Reservation.class, reservationId);
        
        reservation.getRoomRates().size();
        reservation.getRoomType().getRooms().size();
        return reservation;
    }

    @Override
    public List<Reservation> getReservationsToAllocate(LocalDate currDate) {
        Date currDay = Date.from(currDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.startDate = :date");
        query.setParameter("date", currDay);
        List<Reservation> list = query.getResultList();
        for (Reservation reservation : list) {
            reservation.getRoomRates().size();
            reservation.getRoomType().getRooms().size();
        }
        return query.getResultList();
    }
}
