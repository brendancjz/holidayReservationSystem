/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.AllocationExceptionSessionBeanLocal;
import ejb.session.stateless.AllocationSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.GuestSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Employee;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.EmployeeEnum;
import util.enumeration.RoomRateEnum;

/**
 *
 * @author brend
 */
@Singleton
@LocalBean
@Startup
public class TestDateSessionBean {

    @PersistenceContext(unitName = "HolidayReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;

    @EJB
    private RoomSessionBeanLocal roomSessionBean;

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBean;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;
 
    @PostConstruct
    public void postConstruct() {
        System.out.println("==== Inside Post Construct Method ====");
        try {
            if (em.find(Employee.class, 1L) == null) {
                employeeSessionBean.createNewEmployee(new Employee("Bren", "Dan", "sysadmin", EmployeeEnum.SYSTEMADMIN.toString(), "password"));
                employeeSessionBean.createNewEmployee(new Employee("Dan", "Bren", "opmanager", EmployeeEnum.OPSMANAGER.toString(), "password"));
                employeeSessionBean.createNewEmployee(new Employee("Chia", "Seeds", "salesmanager", EmployeeEnum.SALESMANAGER.toString(), "password"));
                employeeSessionBean.createNewEmployee(new Employee("Jun", "Zhe", "guestrelo", EmployeeEnum.GRELMANAGER.toString(), "password"));
                System.out.println("created all employees");
            }

            if (em.find(Room.class, 1L) == null && em.find(RoomType.class, 1L) == null && em.find(RoomRate.class, 1L) == null) {
                //Create some Room Types
                Long[] roomTypeIds = createRoomTypes();
                System.out.println("created all room types");
                //Create Room Rates first 
                //Linking them alr
                createRoomRates(roomTypeIds);
                System.out.println("created all room rates and linked");

                //Create some rooms 
                createRooms(roomTypeIds);
                System.out.println("created all rooms and linked");

            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createRoomRates(Long[] roomTypeIds) {

        //String roomRateName, String roomRateType, Double ratePerNight, Date startDate, Date endDate
        //String roomRateName, String roomRateType, Double ratePerNight
        //CREATE ROOM RATE
        RoomRate rate1 = new RoomRate("PublishedRateDR", RoomRateEnum.PublishedRate, Double.valueOf(100));
        //ASSOCIATE ROOM RATE WITH ROOM TYPE
        roomRateSessionBean.associateRoomRateWithRoomType(rate1, roomTypeIds[0]);
        //PERSIST ROOM RATE
        Long publishedRateDRId = roomRateSessionBean.createNewRoomRate(rate1);
        //ASSOCIATE ROOM TYPE WITH ROOM RATE
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[0], publishedRateDRId);

        RoomRate rate2 = new RoomRate("PublishedRatePR", RoomRateEnum.PublishedRate, Double.valueOf(200));
        roomRateSessionBean.associateRoomRateWithRoomType(rate2, roomTypeIds[1]);
        Long publishedRatePRId = roomRateSessionBean.createNewRoomRate(rate2);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[1], publishedRatePRId);

        RoomRate rate3 = new RoomRate("PublishedRateFR", RoomRateEnum.PublishedRate, Double.valueOf(300));
        roomRateSessionBean.associateRoomRateWithRoomType(rate3, roomTypeIds[2]);
        Long publishedRateFRId = roomRateSessionBean.createNewRoomRate(rate3);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[2], publishedRateFRId);

        RoomRate rate4 = new RoomRate("PublishedRateJS", RoomRateEnum.PublishedRate, Double.valueOf(400));
        roomRateSessionBean.associateRoomRateWithRoomType(rate4, roomTypeIds[3]);
        Long publishedRateJSId = roomRateSessionBean.createNewRoomRate(rate4);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[3], publishedRateJSId);

        RoomRate rate5 = new RoomRate("PublishedRateGS", RoomRateEnum.PublishedRate, Double.valueOf(500));
        roomRateSessionBean.associateRoomRateWithRoomType(rate5, roomTypeIds[4]);
        Long publishedRateGSId = roomRateSessionBean.createNewRoomRate(rate5);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[4], publishedRateGSId);

        RoomRate rate6 = new RoomRate("NormalRateDR", RoomRateEnum.NormalRate, Double.valueOf(50));
        roomRateSessionBean.associateRoomRateWithRoomType(rate6, roomTypeIds[0]);
        Long normalRateDRId = roomRateSessionBean.createNewRoomRate(rate6);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[0], normalRateDRId);

        RoomRate rate7 = new RoomRate("NormalRatePR", RoomRateEnum.NormalRate, Double.valueOf(100));
        roomRateSessionBean.associateRoomRateWithRoomType(rate7, roomTypeIds[1]);
        Long normalRatePRId = roomRateSessionBean.createNewRoomRate(rate7);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[1], normalRatePRId);

        RoomRate rate8 = new RoomRate("NormalRateFR", RoomRateEnum.NormalRate, Double.valueOf(150));
        roomRateSessionBean.associateRoomRateWithRoomType(rate8, roomTypeIds[2]);
        Long normalRateFRId = roomRateSessionBean.createNewRoomRate(rate8);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[2], normalRateFRId);

        RoomRate rate9 = new RoomRate("NormalRateJS", RoomRateEnum.NormalRate, Double.valueOf(200));
        roomRateSessionBean.associateRoomRateWithRoomType(rate9, roomTypeIds[3]);
        Long normalRateJSId = roomRateSessionBean.createNewRoomRate(rate9);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[3], normalRateJSId);

        RoomRate rate10 = new RoomRate("NormalRateGS", RoomRateEnum.NormalRate, Double.valueOf(250));
        roomRateSessionBean.associateRoomRateWithRoomType(rate10, roomTypeIds[4]);
        Long normalRateGSId = roomRateSessionBean.createNewRoomRate(rate10);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[4], normalRateGSId);

    }

    private Long[] createRoomTypes() {
        //String roomTypeName, String roomTypeDesc, Integer roomSize, Integer numOfBeds, Integer capacity, Integer rank, String amenities
        Long deluxeRoomId = roomTypeSessionBean.createNewRoomType(new RoomType("DeluxeRoom", "Deluxe Room", 400, 2, 4, 5, "Bed, Toilet, TV"));
        Long premierRoomId = roomTypeSessionBean.createNewRoomType(new RoomType("PremierRoom", "Premier Room", 600, 4, 6, 4, "Bed, Toilet, TV, Bathtub"));
        Long familyRoomId = roomTypeSessionBean.createNewRoomType(new RoomType("FamilyRoom", "Family Room", 800, 6, 8, 3, "Bed, Toilet, TV, Bathtub, Balcony"));
        Long juniorSuiteId = roomTypeSessionBean.createNewRoomType(new RoomType("JuniorSuite", "Junior Suite Room", 1000, 8, 10, 2, "Bed, Toilet, TV, Bathtub, Balcony, Fridge"));
        Long grandSuiteId = roomTypeSessionBean.createNewRoomType(new RoomType("GrandSuite", "Grand Suite Room", 1200, 10, 12, 1, "Bed, Toilet, TV, Bathtub, Balcony, Fridge, Safe"));

        return new Long[]{deluxeRoomId, premierRoomId, familyRoomId, juniorSuiteId, grandSuiteId};
    }

    private void createRooms(Long[] roomTypeIds) {

        //Integer roomLevel, Integer roomNum
        //Create 3Rooms for each RoomType
        //CREATE ROOM
        Room room1 = new Room(1, 1);
        //ASSOCIATE ROOM WITH ROOM TYPE
        roomSessionBean.associateRoomWithRoomType(room1, roomTypeIds[0]);
        //PERSIST ROOM
        Long room1DRId = roomSessionBean.createNewRoom(room1);
        //ASSOCIATE ROOM TYPE WITH ROOM
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[0], room1DRId);

        Room room2 = new Room(2, 1);
        roomSessionBean.associateRoomWithRoomType(room2, roomTypeIds[0]);
        Long room2DRId = roomSessionBean.createNewRoom(room2);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[0], room2DRId);

        Room room3 = new Room(3, 1);
        roomSessionBean.associateRoomWithRoomType(room3, roomTypeIds[0]);
        Long room3DRId = roomSessionBean.createNewRoom(room3);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[0], room3DRId);

        Room room4 = new Room(4, 1);
        roomSessionBean.associateRoomWithRoomType(room4, roomTypeIds[0]);
        Long room4DRId = roomSessionBean.createNewRoom(room4);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[0], room4DRId);

        Room room5 = new Room(5, 1);
        roomSessionBean.associateRoomWithRoomType(room5, roomTypeIds[0]);
        Long room5DRId = roomSessionBean.createNewRoom(room5);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[0], room5DRId);

        Room room6 = new Room(1, 2);
        roomSessionBean.associateRoomWithRoomType(room6, roomTypeIds[1]);
        Long room1PRId = roomSessionBean.createNewRoom(room6);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[1], room1PRId);

        Room room7 = new Room(2, 2);
        roomSessionBean.associateRoomWithRoomType(room7, roomTypeIds[1]);
        Long room2PRId = roomSessionBean.createNewRoom(room7);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[1], room2PRId);

        Room room8 = new Room(3, 2);
        roomSessionBean.associateRoomWithRoomType(room8, roomTypeIds[1]);
        Long room3PRId = roomSessionBean.createNewRoom(room8);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[1], room3PRId);

        Room room9 = new Room(4, 2);
        roomSessionBean.associateRoomWithRoomType(room9, roomTypeIds[1]);
        Long room4PRId = roomSessionBean.createNewRoom(room9);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[1], room4PRId);

        Room room10 = new Room(5, 2);
        roomSessionBean.associateRoomWithRoomType(room10, roomTypeIds[1]);
        Long room5PRId = roomSessionBean.createNewRoom(room10);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[1], room5PRId);

        Room room11 = new Room(1, 3);
        roomSessionBean.associateRoomWithRoomType(room11, roomTypeIds[2]);
        Long room1FRId = roomSessionBean.createNewRoom(room11);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[2], room1FRId);

        Room room12 = new Room(2, 3);
        roomSessionBean.associateRoomWithRoomType(room12, roomTypeIds[2]);
        Long room2FRId = roomSessionBean.createNewRoom(room12);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[2], room2FRId);

        Room room13 = new Room(3, 3);
        roomSessionBean.associateRoomWithRoomType(room13, roomTypeIds[2]);
        Long room3FRId = roomSessionBean.createNewRoom(room13);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[2], room3FRId);

        Room room14 = new Room(4, 3);
        roomSessionBean.associateRoomWithRoomType(room14, roomTypeIds[2]);
        Long room4FRId = roomSessionBean.createNewRoom(room14);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[2], room4FRId);

        Room room15 = new Room(5, 3);
        roomSessionBean.associateRoomWithRoomType(room15, roomTypeIds[2]);
        Long room5FRId = roomSessionBean.createNewRoom(room15);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[2], room5FRId);

        Room room16 = new Room(1, 4);
        roomSessionBean.associateRoomWithRoomType(room16, roomTypeIds[3]);
        Long room1JSId = roomSessionBean.createNewRoom(room16);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[3], room1JSId);

        Room room17 = new Room(2, 4);
        roomSessionBean.associateRoomWithRoomType(room17, roomTypeIds[3]);
        Long room2JSId = roomSessionBean.createNewRoom(room17);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[3], room2JSId);

        Room room18 = new Room(3, 4);
        roomSessionBean.associateRoomWithRoomType(room18, roomTypeIds[3]);
        Long room3JSId = roomSessionBean.createNewRoom(room18);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[3], room3JSId);

        Room room19 = new Room(4, 4);
        roomSessionBean.associateRoomWithRoomType(room19, roomTypeIds[3]);
        Long room4JSId = roomSessionBean.createNewRoom(room19);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[3], room4JSId);

        Room room20 = new Room(5, 4);
        roomSessionBean.associateRoomWithRoomType(room20, roomTypeIds[3]);
        Long room5JSId = roomSessionBean.createNewRoom(room20);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[3], room5JSId);

        Room room21 = new Room(1, 5);
        roomSessionBean.associateRoomWithRoomType(room21, roomTypeIds[4]);
        Long room1GSId = roomSessionBean.createNewRoom(room21);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[4], room1GSId);

        Room room22 = new Room(2, 5);
        roomSessionBean.associateRoomWithRoomType(room22, roomTypeIds[4]);
        Long room2GSId = roomSessionBean.createNewRoom(room22);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[4], room2GSId);

        Room room23 = new Room(3, 5);
        roomSessionBean.associateRoomWithRoomType(room23, roomTypeIds[4]);
        Long room3GSId = roomSessionBean.createNewRoom(room23);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[4], room3GSId);

        Room room24 = new Room(4, 5);
        roomSessionBean.associateRoomWithRoomType(room24, roomTypeIds[4]);
        Long room4GSId = roomSessionBean.createNewRoom(room24);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[4], room4GSId);

        Room room25 = new Room(5, 5);
        roomSessionBean.associateRoomWithRoomType(room25, roomTypeIds[4]);
        Long room5GSId = roomSessionBean.createNewRoom(room25);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[4], room5GSId);

    }
 
}
