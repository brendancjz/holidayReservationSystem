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
import entity.RoomRate;
import entity.RoomType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
        System.out.println("======= Inside Post Construct Method");
        try {
            if (em.find(Guest.class, 1L) == null) {
                guestSessionBean.createNewGuest(new Guest("Theo", "Doric", 8482L, "theo@gmail.com"));
            }
            
            if (roomSessionBean.retrieveAllRooms().isEmpty() && 
                    roomTypeSessionBean.retrieveAllRoomTypes().isEmpty() && roomRateSessionBean.retrieveAllRoomRates().isEmpty()) {
                //Create some Room Types
                Long[] roomTypeIds = createRoomTypes();
                
                //Create Room Rates first 
                Long[][] roomRatesIds = createRoomRates();
                
                //Link Room Types to Room Rates 
                //Link Room Rate to Room Types
                System.out.println("Linking RoomType and RoomRate");
                for (int i = 0; i < roomTypeIds.length; i++) {
                    RoomType roomType = em.find(RoomType.class, roomTypeIds[i]);
                    System.out.println("RoomType Id: " + roomType.getRoomTypeId()); 
                    for (int j = 0; j < roomRatesIds[i].length; j++) {
                        RoomRate roomRate = em.find(RoomRate.class, roomRatesIds[i][j]);
                        roomRate.setRoomType(roomType);
                        ArrayList<RoomRate> rates = roomType.getRates();
                        rates.add(roomRate);
                        System.out.println("Linking RoomType: " + roomType.getRoomTypeName() + " with RoomRate: " + roomRate.getRoomRateName());
                    }
                }
                
                //Create some rooms 
                Long[][] roomIds = createRooms();
                
                //Link Room Types to Room
                //Link Room to Room Types
                System.out.println("Linking RoomType and Room");
                for (int i = 0; i < roomTypeIds.length; i++) {
                    RoomType roomType = em.find(RoomType.class,roomTypeIds[i]);
                    for (int j = 0; j < roomIds[i].length; j++) {
                        Room room = em.find(Room.class, roomIds[i][j]);
                        roomType.getRooms().add(room);
                        room.setRoomType(roomType);
                        System.out.println("Linking RoomType: " + roomType.getRoomTypeName() + " with Room: " + room.getRoomId());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("** postConstruct throwing error " + e.getMessage());
        }
        
    }
    
    private Long[][] createRoomRates() {
        //String roomRateName, String roomRateType, Double ratePerNight, Date startDate, Date endDate
        //String roomRateName, String roomRateType, Double ratePerNight
        Long publishedRateDRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PublishedRateDR","PublishedRate",Double.valueOf(120)));
        Long publishedRatePRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PublishedRatePR","PublishedRate",Double.valueOf(140)));
        Long publishedRateFRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PublishedRateFR","PublishedRate",Double.valueOf(160)));
        Long publishedRateJSId = roomRateSessionBean.createNewRoomRate(new RoomRate("PublishedRateJS","PublishedRate",Double.valueOf(180)));
        Long publishedRateGSId = roomRateSessionBean.createNewRoomRate(new RoomRate("PublishedRateGS","PublishedRate",Double.valueOf(200)));

        Long normalRateDRId = roomRateSessionBean.createNewRoomRate(new RoomRate("NormalRateDR","NormalRate",Double.valueOf(100)));
        Long normalRatePRId = roomRateSessionBean.createNewRoomRate(new RoomRate("NormalRatePR","NormalRate",Double.valueOf(120)));
        Long normalRateFRId = roomRateSessionBean.createNewRoomRate(new RoomRate("NormalRateFR","NormalRate",Double.valueOf(140)));
        Long normalRateJSId = roomRateSessionBean.createNewRoomRate(new RoomRate("NormalRateJS","NormalRate",Double.valueOf(160)));
        Long normalRateGSId = roomRateSessionBean.createNewRoomRate(new RoomRate("NormalRateGS","NormalRate",Double.valueOf(180)));

        //Validity Period for Peak Rate
        LocalDateTime startLocalDateTime = LocalDateTime.of(2021,10, 10, 0, 0, 0);
        Date startDate = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
        LocalDateTime endLocalDateTime = LocalDateTime.of(2021,10, 14, 0, 0, 0);
        Date endDate = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Long peakRateDRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PeakRateDR","PeakRate",Double.valueOf(120), startDate, endDate));
        Long peakRatePRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PeakRatePR","PeakRate",Double.valueOf(140), startDate, endDate));
        Long peakRateFRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PeakRateFR","PeakRate",Double.valueOf(160), startDate, endDate));
        Long peakRateJSId = roomRateSessionBean.createNewRoomRate(new RoomRate("PeakRateJS","PeakRate",Double.valueOf(180), startDate, endDate));
        Long peakRateGSId = roomRateSessionBean.createNewRoomRate(new RoomRate("PeakRateGS","PeakRate",Double.valueOf(200), startDate, endDate));

        //Validity Period for Promo Rate
        startLocalDateTime = LocalDateTime.of(2021,10, 14, 0, 0, 0);
        startDate = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
        endLocalDateTime = LocalDateTime.of(2021,10, 21, 0, 0, 0);
        endDate = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Long promoRateDRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PromotionRateDR","PromotionRate",Double.valueOf(80), startDate, endDate));
        Long promoRatePRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PromotionRatePR","PromotionRate",Double.valueOf(100), startDate, endDate));
        Long promoRateFRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PromotionRateFR","PromotionRate",Double.valueOf(120), startDate, endDate));
        Long promoRateJSId = roomRateSessionBean.createNewRoomRate(new RoomRate("PromotionRateJS","PromotionRate",Double.valueOf(140), startDate, endDate));
        Long promoRateGSId = roomRateSessionBean.createNewRoomRate(new RoomRate("PromotionRateGS","PromotionRate",Double.valueOf(160), startDate, endDate));

        Long[] DRroomRates = new Long[] {publishedRateDRId, normalRateDRId, peakRateDRId, promoRateDRId};
        Long[] PRroomRates = new Long[] {publishedRatePRId, normalRatePRId, peakRatePRId, promoRatePRId};
        Long[] FRroomRates = new Long[] {publishedRateFRId, normalRateFRId, peakRateFRId, promoRateFRId};
        Long[] JSroomRates = new Long[] {publishedRateJSId, normalRateJSId, peakRateJSId, promoRateJSId};
        Long[] GSroomRates = new Long[] {publishedRateGSId, normalRateGSId, peakRateGSId, promoRateGSId};

        return new Long[][]{DRroomRates, PRroomRates, FRroomRates, JSroomRates, GSroomRates};
    }

    private Long[] createRoomTypes() {
        //String roomTypeName, String roomTypeDesc, Integer roomSize, Integer numOfBeds, Integer capacity, String amenities
        Long deluxeRoomId = roomTypeSessionBean.createNewRoomType(new RoomType("DeluxeRoom", "Deluxe Room", 400, 2, 4, "Bed, Toilet, TV"));
        Long premierRoomId = roomTypeSessionBean.createNewRoomType(new RoomType("PremierRoom", "Premier Room", 600, 4, 6, "Bed, Toilet, TV, Bathtub"));
        Long familyRoomId = roomTypeSessionBean.createNewRoomType(new RoomType("FamilyRoom", "Family Room", 800, 6, 8, "Bed, Toilet, TV, Bathtub, Balcony"));
        Long juniorSuiteId = roomTypeSessionBean.createNewRoomType(new RoomType("JuniorSuite", "Junior Suite Room", 1000, 8, 10, "Bed, Toilet, TV, Bathtub, Balcony, Fridge"));
        Long grandSuiteId = roomTypeSessionBean.createNewRoomType(new RoomType("GrandSuite", "Grand Suite Room", 1200, 10, 12, "Bed, Toilet, TV, Bathtub, Balcony, Fridge, Safe"));

        return new Long[] {deluxeRoomId, premierRoomId, familyRoomId, juniorSuiteId, grandSuiteId};
    }
    
    private Long[][] createRooms() {
        //Integer roomLevel, Integer roomNum, Boolean isAvailable
        //Create 3Rooms for each RoomType
        Long room1DRId = roomSessionBean.createNewRoom(new Room(1,1,true));
        Long room2DRId = roomSessionBean.createNewRoom(new Room(1,2,true));
        Long room3DRId = roomSessionBean.createNewRoom(new Room(1,3,true));
        
        Long room1PRId = roomSessionBean.createNewRoom(new Room(2,1,true));
        Long room2PRId = roomSessionBean.createNewRoom(new Room(2,2,true));
        Long room3PRId = roomSessionBean.createNewRoom(new Room(2,3,true));
        
        Long room1FRId = roomSessionBean.createNewRoom(new Room(3,1,true));
        Long room2FRId = roomSessionBean.createNewRoom(new Room(3,2,true));
        Long room3FRId = roomSessionBean.createNewRoom(new Room(3,3,true));
        
        Long room1JSId = roomSessionBean.createNewRoom(new Room(4,1,true));
        Long room2JSId = roomSessionBean.createNewRoom(new Room(4,2,true));
        Long room3JSId = roomSessionBean.createNewRoom(new Room(4,3,true));
        
        Long room1GSId = roomSessionBean.createNewRoom(new Room(5,1,true));
        Long room2GSId = roomSessionBean.createNewRoom(new Room(5,2,true));
        Long room3GSId = roomSessionBean.createNewRoom(new Room(5,3,true));
        
        Long[] DRroomIds = new Long[] {room1DRId, room2DRId, room3DRId};
        Long[] PRroomIds = new Long[] {room1PRId, room2PRId, room3PRId};
        Long[] FRroomIds = new Long[] {room1FRId, room2FRId, room3FRId};
        Long[] JSroomIds = new Long[] {room1JSId, room2JSId, room3JSId};
        Long[] GSroomIds = new Long[] {room1GSId, room2GSId, room3GSId};
        
    
        return new Long[][]{DRroomIds, PRroomIds, FRroomIds, JSroomIds, GSroomIds};
    }
    
    
}
