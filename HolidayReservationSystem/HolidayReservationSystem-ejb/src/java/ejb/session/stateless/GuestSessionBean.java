/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author brend
 */
@Stateless
public class GuestSessionBean implements GuestSessionBeanRemote, GuestSessionBeanLocal {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public boolean verifyLoginDetails(String email) {
        return true;
    }
    
    @Override
    public boolean verifyRegisterDetails(String firstName, String lastName, Long contactNum, String email) {
        return true;
    }
    
    @Override
    public boolean checkGuestExists(String email) {
        boolean guestExists = false;
        
        try {
            Query query = em.createQuery("SELECT g FROM Guest g");
        
            
            List<Guest> guestList = query.getResultList();
            for (Guest guest : guestList) {
                if (guest.getEmail().equals(email)) {
                    guestExists = true;
                }
            }
        } catch (Exception e) {
            System.out.println("** checkGuestExists throwing error " + e.getMessage());
        }
   
        return guestExists;
    }
    
    @Override
    public Long createNewGuest(Guest guest) {
        em.persist(guest);
        em.flush();
        
        return guest.getCustomerId();
    }
    
    @Override
    public Guest getGuestByEmail(String email) {
        Guest guest = null;
        try {
            Query query = em.createQuery("SELECT g FROM Guest g WHERE g.email= :email");
            query.setParameter("email", email);
            
            guest = (Guest) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("** getGuestByEmail throwing error " + e.getMessage());
        }
        
        return guest;
    }
    
    @Override
    public List<Guest> retrieveAllGuests() {
        Query query = em.createQuery("SELECT g FROM Guest g");
        List<Guest> guests = query.getResultList();
        for (Guest guest: guests) {
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
}
