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
    public boolean verifyLoginDetails(String email, long contactNum) {
        return true;
    }
    
    @Override
    public boolean verifyRegisterDetails(String firstName, String lastName, String email, long contactNum) {
        return true;
    }
    
    @Override
    public boolean checkGuestExists(String email, long contactNum) {
        Query query = em.createQuery("SELECT g FROM Guest g");
        
        boolean guestExists = false;
        List<Guest> guestList = query.getResultList();
        for (Guest guest : guestList) {
            if (guest.getContactNumber() == contactNum && guest.getEmail().equals(email)) {
                guestExists = true;
            }
        }
        
        return guestExists;
    }
    
    @Override
    public Long createNewGuest(Guest guest) {
        em.persist(guest);
        em.flush();
        
        return guest.getGuestId();
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
