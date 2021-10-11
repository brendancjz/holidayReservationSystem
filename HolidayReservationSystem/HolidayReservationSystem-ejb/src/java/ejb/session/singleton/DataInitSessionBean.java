/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.GuestSessionBeanLocal;
import entity.Guest;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;

/**
 *
 * @author brend
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB
    private GuestSessionBeanLocal guestSessionBean;

    @PostConstruct
    public void postConstruct() {
        try {
            guestSessionBean.createNewGuest(new Guest("theo", "doric", 12341234L, "theo@gmail.com", new ArrayList<>()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    
}
