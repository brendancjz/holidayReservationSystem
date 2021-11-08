/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import javax.ejb.Remote;

/**
 *
 * @author brend
 */
@Remote
public interface GuestSessionBeanRemote {
       
    public Long createNewGuest(Guest guest);

    public boolean checkGuestExists(String email);
    public Guest getGuestByEmail(String email);

    public Guest getGuestByGuestId(Long guestId);

    public void associateGuestWithReservation(Long guestId, Long reservationId);

    public Guest getGuestByContactNum(Long number);
    
}
