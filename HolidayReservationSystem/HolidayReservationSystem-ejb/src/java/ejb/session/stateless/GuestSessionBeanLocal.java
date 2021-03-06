/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import java.util.List;
import javax.ejb.Local;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
@Local
public interface GuestSessionBeanLocal {
    
    public boolean checkGuestExists(String email);
    
    public Long createNewGuest(Guest guest);
    public Guest getGuestByEmail(String email);

    public List<Guest> retrieveAllGuests() throws EmptyListException;
    public void associateGuestWithReservation(Long guestId, Long reservationId);
}
