/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
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
public class GuestSessionBean implements GuestSessionBeanRemote, GuestSessionBeanLocal {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;


    @Override
    public boolean checkGuestExists(String email) {
        Query query = em.createQuery("SELECT g FROM Guest g WHERE g.email = :email");
        query.setParameter("email", email);
        try {
            Guest g = (Guest) query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public Long createNewGuest(Guest guest) {
        em.persist(guest);
        em.flush();

        return guest.getCustomerId();
    }

    @Override
    public Guest getGuestByEmail(String email) {
        
        try {
            Query query = em.createQuery("SELECT g FROM Guest g WHERE g.email= :email");
            query.setParameter("email", email);

            Guest guest = (Guest) query.getSingleResult();
            guest.getReservations().size();
            
            return guest;
        } catch (NoResultException e) {
            return null;
        }

        
    }

    @Override
    public List<Guest> retrieveAllGuests() throws EmptyListException {
        Query query = em.createQuery("SELECT g FROM Guest g");
        List<Guest> guests = query.getResultList();
        if (guests.isEmpty()) throw new EmptyListException("List of guest is empty.\n");
        for (Guest guest : guests) {
            guest.getReservations().size();
        }

        return guests;
    }

    @Override
    public Guest getGuestByGuestId(Long guestId) {
        Guest guest = em.find(Guest.class, guestId);
        guest.getReservations().size();

        return guest;
    }

    @Override
    public void associateGuestWithReservation(Long guestId, Long reservationId) {
        Reservation reservation = em.find(Reservation.class, reservationId);
        Guest guest = em.find(Guest.class, guestId);
        guest.getReservations().add(reservation);
    }

    @Override
    public Guest getGuestByContactNum(Long number) {
        try {
            Query query = em.createQuery("SELECT g FROM Guest g WHERE g.contactNumber = :number");
            query.setParameter("number", number);

            Guest guest = (Guest) query.getSingleResult();
            guest.getReservations().size();
            
            return guest;
        } catch (NoResultException e) {
            return null;
        }
    }
}
