/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Guest;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.EmptyListException;

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
    public List<Reservation> retrieveAllReservations() throws EmptyListException {
        Query query = em.createQuery("SELECT r FROM Reservation r");
        List<Reservation> reservations = query.getResultList();
        if (reservations.isEmpty()) {
            throw new EmptyListException("List of reservations is empty.\n");
        }
        for (Reservation r : reservations) {
            r.getRoomRates().size();
            r.getRoomType();
           
        }

        return reservations;
    }

    @Override
    public List<Reservation> getReservationsByRoomTypeId(Long typeId) throws EmptyListException {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.roomType.roomTypeId=:typeId"); //IMPT TO NOTE
        query.setParameter("typeId", typeId);

        List<Reservation> reservations = query.getResultList();
        if (reservations.isEmpty()) {
            throw new EmptyListException("List of reservations is empty.\n");
        }
        for (Reservation r : reservations) {
            r.getRoomRates().size();
            r.getRoomType();
        }

        return reservations;
    }

    @Override
    public Long createNewReservation(Reservation reservation, Long guestId, Long typeId, Long rateId) {
        this.associateReservationWithGuestAndRoomTypeAndRoomRate(reservation, guestId, typeId, rateId);
        em.persist(reservation);
        em.flush();

        return reservation.getReservationId();
    }

    @Override
    public void associateReservationWithGuestAndRoomTypeAndRoomRate(Reservation reservation, Long guestId, Long typeId, Long rateId) {
        System.out.println("Inside associateReservationWithGuestAndRoomTypeAndRoomRate() method");
        Customer customer = em.find(Customer.class, guestId);
        RoomType roomType = em.find(RoomType.class, typeId);
        RoomRate roomRate = em.find(RoomRate.class, rateId);

        reservation.setCustomer(customer);
        reservation.setRoomType(roomType);
        reservation.getRoomRates().add(roomRate);

    }

    @Override
    public void associateReservationWithGuestAndRoomTypeAndRoomRates(Reservation reservation, Long guestId, Long typeId, List<RoomRate> ratesUsed) {
        Customer customer = em.find(Customer.class, guestId);
        RoomType roomType = em.find(RoomType.class, typeId);
        for (RoomRate rate : ratesUsed) {
            em.merge(rate);
            reservation.getRoomRates().add(rate);
        }
        reservation.setCustomer(customer);
        reservation.setRoomType(roomType);

    }

    @Override
    public boolean isRoomTypeAvailableForReservation(Long typeId, LocalDate startDate, LocalDate endDate, int numOfRooms) {
        //1. Get num of rooms that are avail
        //2. Get num of reservations that collide with range, meaning num of rooms required to take those reservation
        //3. (1.) - (2.) and if this number is greater or equals to numOfRooms the person wants to reserve, return true;
        RoomType type = em.find(RoomType.class, typeId);
        type.getRooms().size();

        LocalDate currDate = LocalDate.now();
        Integer timeCheck = LocalDateTime.now().getHour();
        
        int count = 0;
        for (Room room : type.getRooms()) {
            if (room.getIsAvailable() && 
                    ((currDate.isEqual(startDate) && timeCheck >= 2 && room.getIsVacant()) || currDate.isBefore(startDate))) {
                
                count++;
            }
        }
        int countOfRoomsRequired = 0;
 
        try {
            List<Reservation> reservationsOfRoomType = this.getReservationsByRoomTypeId(typeId);

            for (Reservation reservation : reservationsOfRoomType) {
                Date start = reservation.getStartDate();
                Date end = reservation.getEndDate();
                if (isCollided(start, end, startDate, endDate)) {
                    System.out.println("Reservation ID: " + reservation.getReservationId() + " collides with this new reservation.");
                    countOfRoomsRequired += reservation.getNumOfRooms();
                    //countOfRoomsRequired++;
                }
            }
        } catch (EmptyListException e) {
            //no reservations, no problems;
            countOfRoomsRequired = 0;
        }

        return (count - countOfRoomsRequired - numOfRooms) >= 0;
    }

    private boolean isCollided(Date start, Date end, LocalDate startDate, LocalDate endDate) {
        LocalDate start1 = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end1 = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        boolean lowerBound = start1.isEqual(startDate) || (start1.isAfter(startDate) && start1.isBefore(endDate));
        boolean upperBound = (end1.isBefore(endDate) && end1.isAfter(startDate)) || end1.isEqual(endDate);
        return lowerBound || upperBound;
    }

    @Override
    public Reservation getReservationsByRoomTypeIdAndDuration(Long roomTypeId, LocalDate checkInDate, LocalDate checkOutDate, Long guestId) {

        Date endDate = Date.from(checkOutDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date startDate = Date.from(checkInDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Query query = em.createQuery("SELECT r FROM Guest g JOIN g.reservations r WHERE g.customerId = :guestId AND r.roomType.roomTypeId = :typeId AND r.startDate = :start AND r.endDate = :end");
        query.setParameter("guestId", guestId);
        query.setParameter("typeId", roomTypeId);
        query.setParameter("start", startDate);
        query.setParameter("end", endDate);

        try {
            Reservation reservation = (Reservation) query.getSingleResult();
            reservation.getRoomRates().size();
            reservation.getRoomType();
            return reservation;
        } catch (NoResultException e) {
            return null;
        }

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

            for (Reservation reservation : reservationsOfRoomType) {
                Date start = reservation.getStartDate();
                Date end = reservation.getEndDate();
                if (isCollided(start, end, startDate, endDate)) {
                    countOfRoomsRequired++;
                }
            }
        } catch (EmptyListException e) {
            countOfRoomsRequired = 0;
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

        if (reservation.getRoomRates() != null) {
            reservation.getRoomRates().size();
        }
        if (reservation.getRoomType() != null) {
            reservation.getRoomType().getRooms().size();
        }

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

    @Override
    public boolean isRoomTypeAvailableForWalkInReservation(Long roomTypeId, int numOfRooms) {
        RoomType type = em.find(RoomType.class, roomTypeId);
        List<Room> rooms = type.getRooms();
        int numOfVacantRooms = 0;
        for (Room room : rooms) {
            if (room.getIsVacant()) {
                numOfVacantRooms++;
            }
        }

        return numOfRooms <= numOfVacantRooms;
    }

    @Override
    public Long createNewReservation(Reservation reservation, Long guestId, Long roomTypeId, List<RoomRate> ratesUsed) {
        //ASSOCIATE THE RESERVATION WITH GUEST AND ROOM TYPE AND ROOM RATES
        this.associateReservationWithGuestAndRoomTypeAndRoomRates(reservation, guestId, roomTypeId, ratesUsed);
        em.persist(reservation);
        em.flush();

        return reservation.getReservationId();
    }

    @Override
    public List<Reservation> getReservationsByPartnerId(Long partnerId) throws EmptyListException {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.customer.customerId = :id");
        query.setParameter("id", partnerId);
        
        List<Reservation> reservations = query.getResultList();
        
        if (reservations.isEmpty()) throw new EmptyListException("Lsit of partner reservations is empty.\n");
        
        for (Reservation r : reservations) {
            r.getRoomRates().size();
            r.getRoomType();
        }
        
        return reservations;
    }

    @Override
    public Reservation getReservationsByDuration(LocalDate checkInDate, LocalDate checkOutDate, Long partnerId) {
        
        Date endDate = Date.from(checkOutDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date startDate = Date.from(checkInDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.customer.customerId = :id AND r.startDate = :start AND r.endDate = :end");
        query.setParameter("id", partnerId);
        query.setParameter("start", startDate);
        query.setParameter("end", endDate);

        try {
            Reservation reservation = (Reservation) query.getSingleResult();
            reservation.getRoomRates().size();
            reservation.getRoomType();
            return reservation;
        } catch (NoResultException e) {
            return null;
        }
    }
}
