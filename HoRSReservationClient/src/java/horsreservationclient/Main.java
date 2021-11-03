/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomManagementSessionBeanRemote;
import java.util.Scanner;
import javax.ejb.EJB;

/**
 *
 * @author brend
 */
public class Main {

    @EJB
    private static PartnerSessionBeanRemote partnerSessionBean;

    @EJB
    private static RoomManagementSessionBeanRemote roomManagementSessionBean;
    
    @EJB
    private static ReservationSessionBeanRemote reservationSessionBean;

    @EJB
    private static GuestSessionBeanRemote guestSessionBean;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner sc = new Scanner(System.in);
        MainApp app = new MainApp(roomManagementSessionBean, guestSessionBean, partnerSessionBean, reservationSessionBean);
        app.run();
        
    }
}
