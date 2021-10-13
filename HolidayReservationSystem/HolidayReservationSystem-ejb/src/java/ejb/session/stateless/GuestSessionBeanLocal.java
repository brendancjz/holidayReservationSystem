/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import javax.ejb.Local;

/**
 *
 * @author brend
 */
@Local
public interface GuestSessionBeanLocal {
    
    public boolean checkGuestExists(String email);
    public boolean verifyLoginDetails(String email);
    public boolean verifyRegisterDetails(String firstName, String lastName, String email, long contactNum);
    public Long createNewGuest(Guest guest);
    public Guest getGuestByEmail(String email);
    
}
