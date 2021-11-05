/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import javax.ejb.Remote;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
@Remote
public interface GuestSessionBeanRemote {
    public boolean verifyLoginDetails(String email);
    public boolean verifyRegisterDetails(String firstName, String lastName, Long contactNum, String email);    
    public Long createNewGuest(Guest guest);

    public boolean checkGuestExists(String email);
    public Guest getGuestByEmail(String email);

    public Guest getGuestByGuestId(Long guestId);

    public void associateGuestWithReservation(Long guestId, Long reservationId);
    
}
