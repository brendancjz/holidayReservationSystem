/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.GuestSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Guest;
import entity.Room;
import entity.RoomType;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author brend
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {
    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private RoomSessionBeanLocal roomSessionBean;

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBean;
    
    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;

    @EJB
    private GuestSessionBeanLocal guestSessionBean;

    @PostConstruct
    public void postConstruct() {
        try {
            if (em.find(Guest.class, 1L) == null) {
                guestSessionBean.createNewGuest(new Guest("Theo", "Doric", 8482L, "theo@gmail.com"));
            }
            
            if (em.find(Room.class,1L) == null) {
                //Create some Room Types
                //String roomTypeName, String roomTypeDesc, Integer roomSize, Integer numOfBeds, Integer capacity, String amenities
                Long deluxeRoomId = roomTypeSessionBean.createNewRoomType(new RoomType("DeluxeRoom", "Deluxe Room", 400, 2, 4, "Bed, Toilet, TV"));
                Long premierRoomId = roomTypeSessionBean.createNewRoomType(new RoomType("PremierRoom", "Premier Room", 600, 4, 6, "Bed, Toilet, TV, Bathtub"));
                Long familyRoomId = roomTypeSessionBean.createNewRoomType(new RoomType("FamilyRoom", "Family Room", 800, 6, 8, "Bed, Toilet, TV, Bathtub, Balcony"));
                Long juniorSuiteId = roomTypeSessionBean.createNewRoomType(new RoomType("JuniorSuite", "Junior Suite Room", 1000, 8, 10, "Bed, Toilet, TV, Bathtub, Balcony, Fridge"));
                Long grandSuiteId = roomTypeSessionBean.createNewRoomType(new RoomType("GrandSuite", "Grand Suite Room", 1200, 10, 12, "Bed, Toilet, TV, Bathtub, Balcony, Fridge, Safe"));
                
                //Create Room Rates first 
                
                
                //Link Room Types to Room Rates
                //Link Room Rate to Room Types
                
                //Create some rooms
                
                //Link Room Types to Room
                //Link Room to Room Types
                
                
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }
    
    
}
