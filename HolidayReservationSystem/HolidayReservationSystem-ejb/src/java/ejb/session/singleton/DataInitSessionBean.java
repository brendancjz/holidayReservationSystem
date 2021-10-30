/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.AllocationSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.GuestSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Allocation;
import entity.Employee;
import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.EmployeeEnum;
import util.enumeration.RoomRateEnum;
import util.exception.AllocationQueryException;
import util.exception.EmployeeQueryException;
import util.exception.ReservationQueryException;
import util.exception.RoomQueryException;
import util.exception.RoomRateQueryException;
import util.exception.RoomTypeQueryException;

/**
 *
 * @author brend
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB
    private AllocationSessionBeanLocal allocationSessionBean;
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

    @EJB
    private GuestSessionBeanLocal guestSessionBean;
    
    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;
    
    @EJB
    private ReservationSessionBeanLocal reservationSessionBean;

    @PostConstruct
    public void postConstruct() {
        System.out.println("==== Inside Post Construct Method ====");
        try {
            if (em.find(Employee.class, 1L) == null) {
                employeeSessionBean.createNewEmployee(new Employee("Bren", "Dan", EmployeeEnum.SYSTEMADMIN.toString(), "password"));
                employeeSessionBean.createNewEmployee(new Employee("Dan", "Bren", EmployeeEnum.OPSMANAGER.toString(), "password"));
                employeeSessionBean.createNewEmployee(new Employee("Chia", "Seeds", EmployeeEnum.SALESMANAGER.toString(), "password"));
                employeeSessionBean.createNewEmployee(new Employee("Jun", "Zhe", EmployeeEnum.GRELMANAGER.toString(), "password"));
                System.out.println("created all employees"); 
            }
           
            if (em.find(Guest.class, 1L) == null) {
                guestSessionBean.createNewGuest(new Guest("Theo", "Doric", 84826789L, "theo@gmail.com"));
                guestSessionBean.createNewGuest(new Guest("Iggy", "Goh", 12345678L, "iggy@gmail.com"));
                guestSessionBean.createNewGuest(new Guest("Xiang", "Yong", 84821234L, "xy@gmail.com"));
                guestSessionBean.createNewGuest(new Guest("Guoo", "Jun", 84822345L, "junjun@gmail.com"));
                System.out.println("created all guests"); 
            }
            
            if (em.find(Partner.class, 5L) == null) { //hardcoded the 5L cause four guests are created first.
                partnerSessionBean.createNewPartner(new Partner("Theo", "Doric", 84826789L, "mbs@gmail.com"));
                partnerSessionBean.createNewPartner(new Partner("Iggy", "Goh", 12345678L, "hotels@gmail.com"));
                partnerSessionBean.createNewPartner(new Partner("Xiang", "Yong", 84821234L, "fourseasons@gmail.com"));
                System.out.println("created all partners"); 
            }

            if (em.find(Room.class, 1L) == null && em.find(RoomType.class, 1L) == null && em.find(RoomRate.class, 1L) == null) {
                //Create some Room Types
                Long[] roomTypeIds = createRoomTypes();
                System.out.println("created all room types");
                //Create Room Rates first 
                Long[][] roomRatesIds = createRoomRates();
                System.out.println("created all room rates");
                
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
                System.out.println("created all rooms");
                
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
              
            if (em.find(Reservation.class, 1L) == null) {
                LocalDateTime startLocalDateTime = LocalDateTime.of(2021,10, 10, 0, 0, 0);
                Date startDate = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
                LocalDateTime endLocalDateTime = LocalDateTime.of(2021,10, 14, 0, 0, 0);
                Date endDate = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
                Long reservationId = reservationSessionBean.createNewReservation(new Reservation(startDate, endDate, 1, Double.valueOf(480)));
                reservationSessionBean.associateExistingReservationWithGuestAndRoomTypeAndRoomRate(reservationId, 1L, 1L, 1L);
                
                startLocalDateTime = LocalDateTime.of(2021,10, 12, 0, 0, 0);
                startDate = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
                endLocalDateTime = LocalDateTime.of(2021,10, 16, 0, 0, 0);
                endDate = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
                reservationId = reservationSessionBean.createNewReservation(new Reservation(startDate, endDate, 1, Double.valueOf(480)));
                reservationSessionBean.associateExistingReservationWithGuestAndRoomTypeAndRoomRate(reservationId, 1L, 2L, 2L);
                
                startLocalDateTime = LocalDateTime.of(2021,10, 15, 0, 0, 0);
                startDate = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
                endLocalDateTime = LocalDateTime.of(2021,10, 18, 0, 0, 0);
                endDate = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
                reservationId = reservationSessionBean.createNewReservation(new Reservation(startDate, endDate, 1, Double.valueOf(420)));
                reservationSessionBean.associateExistingReservationWithGuestAndRoomTypeAndRoomRate(reservationId, 1L, 4L, 4L);
                
                System.out.println("created all reservations");
            }
             
            if (em.find(Allocation.class, 1L) == null) {
                LocalDateTime startLocalDateTime = LocalDateTime.of(2021,10, 10, 0, 0, 0);
                Date currDate = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
                Long allocationId = allocationSessionBean.createNewAllocation(new Allocation(currDate));
                allocationSessionBean.associateAllocationWithReservation(allocationId, 1L);
                allocationSessionBean.associateAllocationWithRoom(allocationId, 1L);
                System.out.println("created all allocations");
            }

            System.out.println("== Printing out Guests");
            List<Guest> guests = guestSessionBean.retrieveAllGuests();
            for (Guest guest : guests) {
                System.out.println("Guest ID: " + guest.getCustomerId());
                System.out.println("  > Email: " + guest.getEmail());
                System.out.println("  > Reservations:");
                List<Reservation> reservations = guest.getReservations();
                for (Reservation reservation : reservations) {
                    System.out.println("  Reservation ID: " + reservation.getReservationId());
                    System.out.println("    > Start Date: " + reservation.getStartDate().toString());
                    System.out.println("    > End Date: " + reservation.getEndDate().toString());
                    System.out.println("    > Room Type: " + reservation.getRoomType().getRoomTypeName());
                }
            }
            
            System.out.println("== Printing out Partners");
            List<Partner> partners = partnerSessionBean.retrieveAllPartners();
            for (Partner partner : partners) {
                System.out.println("Partner ID: " + partner.getCustomerId());
                System.out.println("  > Email: " + partner.getEmail());
                System.out.println("  > Reservations:");
                List<Reservation> reservations = partner.getReservations();
                for (Reservation reservation : reservations) {
                    System.out.println("  Reservation ID: " + reservation.getReservationId());
                    System.out.println("    > Start Date: " + reservation.getStartDate().toString());
                    System.out.println("    > End Date: " + reservation.getEndDate().toString());
                    System.out.println("    > Room Type: " + reservation.getRoomType().getRoomTypeName());
                }
            }
            
            System.out.println();
            System.out.println("== Printing out Employees");
            List<Employee> employees = employeeSessionBean.retrieveAllEmployees();
            for (Employee employee : employees) {
                System.out.println("Employee ID: " + employee.getEmployeeId());
                System.out.println("  > Name: " + employee.getFirstName());
                System.out.println("  > Role: " + employee.getEmployeeRole());
            }
            
            System.out.println();
            System.out.println("== Printing out Reservations");
            List<Reservation> reservations = reservationSessionBean.retrieveAllReservations();
            for (Reservation reservation : reservations) {
                System.out.println("Reservation ID: " + reservation.getReservationId());
                System.out.println("  > Reserved By: " + reservation.getCustomer().getFirstName());
                for (RoomRate rate : reservation.getRoomRates()) {
                    System.out.println("   > Room Rate: " + rate.getRoomRateName());
                }
                System.out.println("  > Room Type used: " + reservation.getRoomType().getRoomTypeName());
                System.out.println("  > Start Date: " + reservation.getStartDate().toString());
                System.out.println("  > End Date: " + reservation.getEndDate().toString());
            } 
            
            System.out.println();
            System.out.println("== Printing out Rooms");
            List<Room> rooms = roomSessionBean.retrieveAllRooms();
            for (Room room : rooms) {
                System.out.println("Room ID: " + room.getRoomId());
                System.out.println("  > Level: " + room.getRoomLevel());
                System.out.println("  > Number: " + room.getRoomNum());
                System.out.println("  > Room Type: " + room.getRoomType().getRoomTypeName());
                System.out.println("  > Is Available: " + room.getIsAvailable());
                System.out.println("  > Is Disabled: " + room.getIsDisabled());
                System.out.println("  > Is Vacant: " + room.getIsVacant());
            }
             
            System.out.println();
            System.out.println("== Printing Room Rates");
            List<RoomRate> rates = roomRateSessionBean.retrieveAllRoomRates();
            for (RoomRate rate : rates) {
                System.out.println("Room Rate ID: " + rate.getRoomRateId());
                System.out.println("  > Rate Name: " + rate.getRoomRateName());
                System.out.println("  > Room Type: " + rate.getRoomType().getRoomTypeName());
                System.out.println("  > Rate Per Night: " + rate.getRatePerNight());
                System.out.println("  > Is Disabled: " + rate.getIsDisabled());
                if (rate.getStartDate() != null) {
                    System.out.println("  > Validity Period: " + rate.getStartDate().toString() + 
                        " -> " + rate.getEndDate().toString());
                } else {
                    System.out.println("  > Validity Period: NULL");
                }
               
            }
             
            System.out.println();
            System.out.println("== Printing Room Types");
            List<RoomType> types = roomTypeSessionBean.retrieveAllRoomTypes();
            for (RoomType type : types) {
                System.out.println("Room Type ID: " + type.getRoomTypeId());
                System.out.println("  > Name: " + type.getRoomTypeName());
                System.out.println("  > Number of Rooms: " + type.getRooms().size());
                System.out.println("  > Number of Rates: " + type.getRates().size());
                System.out.println("  > Is Disabled: " + type.getIsDisabled());
                System.out.println("  > Room Rates:");
                List<RoomRate> rates1 = type.getRates();
                for (RoomRate rate : rates1) {
                    System.out.println("    Room Rate ID: " + rate.getRoomRateId());
                    System.out.println("    > Name: " + rate.getRoomRateName());
                    System.out.println("    > Room Type: " + rate.getRoomType().getRoomTypeName());
                    System.out.println("    > Is Disabled: " + rate.getIsDisabled());
                } 
                System.out.println("  > Rooms:");
                List<Room> rooms1 = type.getRooms();
                for (Room room : rooms1) {
                    System.out.println("    Room ID: " + room.getRoomId());
                    System.out.println("    > Room Type: " + room.getRoomType().getRoomTypeName());
                    System.out.println("    > Is Disabled: " + room.getIsDisabled());
                }
                System.out.println();
            }
              
            System.out.println();
            System.out.println("== Printing Allocations");
            List<Allocation> allocations = allocationSessionBean.retrieveAllAllocations();
            for (Allocation allocation : allocations) {
                System.out.println("Allocation ID: " + allocation.getAllocationId());
                System.out.println(" > Reservation ID: " + allocation.getReservation().getReservationId());
                System.out.println(" > Date: " + allocation.getCurrentDate());
                List<Room> rooms1 = allocation.getRooms();
                System.out.println("> Rooms:");
                for (Room room : rooms1) {
                    System.out.println("    Room ID: " + room.getRoomId());
                    System.out.println("    > Room Type: " + room.getRoomType().getRoomTypeName());
                    System.out.println("    > Is Disabled: " + room.getIsDisabled());
                }
                System.out.println();
            }
            
        } catch (EmployeeQueryException e) {
            System.out.println("** postConstruct throwing error " + e.getMessage());
        } catch (ReservationQueryException | RoomQueryException | RoomRateQueryException | RoomTypeQueryException | AllocationQueryException ex) {
            Logger.getLogger(DataInitSessionBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Long[][] createRoomRates() {
        //String roomRateName, String roomRateType, Double ratePerNight, Date startDate, Date endDate
        //String roomRateName, String roomRateType, Double ratePerNight
        Long publishedRateDRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PublishedRateDR",RoomRateEnum.PublishedRate,Double.valueOf(120)));
        Long publishedRatePRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PublishedRatePR",RoomRateEnum.PublishedRate,Double.valueOf(140)));
        Long publishedRateFRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PublishedRateFR",RoomRateEnum.PublishedRate,Double.valueOf(160)));
        Long publishedRateJSId = roomRateSessionBean.createNewRoomRate(new RoomRate("PublishedRateJS",RoomRateEnum.PublishedRate,Double.valueOf(180)));
        Long publishedRateGSId = roomRateSessionBean.createNewRoomRate(new RoomRate("PublishedRateGS",RoomRateEnum.PublishedRate,Double.valueOf(200)));

        Long normalRateDRId = roomRateSessionBean.createNewRoomRate(new RoomRate("NormalRateDR",RoomRateEnum.NormalRate,Double.valueOf(100)));
        Long normalRatePRId = roomRateSessionBean.createNewRoomRate(new RoomRate("NormalRatePR",RoomRateEnum.NormalRate,Double.valueOf(120)));
        Long normalRateFRId = roomRateSessionBean.createNewRoomRate(new RoomRate("NormalRateFR",RoomRateEnum.NormalRate,Double.valueOf(140)));
        Long normalRateJSId = roomRateSessionBean.createNewRoomRate(new RoomRate("NormalRateJS",RoomRateEnum.NormalRate,Double.valueOf(160)));
        Long normalRateGSId = roomRateSessionBean.createNewRoomRate(new RoomRate("NormalRateGS",RoomRateEnum.NormalRate,Double.valueOf(180)));

        //Validity Period for Peak Rate
        LocalDateTime startLocalDateTime = LocalDateTime.of(2021,10, 10, 0, 0, 0);
        Date startDate = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
        LocalDateTime endLocalDateTime = LocalDateTime.of(2021,10, 14, 0, 0, 0);
        Date endDate = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Long peakRateDRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PeakRateDR",RoomRateEnum.PeakRate,Double.valueOf(120), startDate, endDate));
        Long peakRatePRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PeakRatePR",RoomRateEnum.PeakRate,Double.valueOf(140), startDate, endDate));
        Long peakRateFRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PeakRateFR",RoomRateEnum.PeakRate,Double.valueOf(160), startDate, endDate));
        Long peakRateJSId = roomRateSessionBean.createNewRoomRate(new RoomRate("PeakRateJS",RoomRateEnum.PeakRate,Double.valueOf(180), startDate, endDate));
        Long peakRateGSId = roomRateSessionBean.createNewRoomRate(new RoomRate("PeakRateGS",RoomRateEnum.PeakRate,Double.valueOf(200), startDate, endDate));

        //Validity Period for Promo Rate
        startLocalDateTime = LocalDateTime.of(2021,10, 14, 0, 0, 0);
        startDate = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
        endLocalDateTime = LocalDateTime.of(2021,10, 21, 0, 0, 0);
        endDate = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Long promoRateDRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PromotionRateDR",RoomRateEnum.PromotionRate,Double.valueOf(80), startDate, endDate));
        Long promoRatePRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PromotionRatePR",RoomRateEnum.PromotionRate,Double.valueOf(100), startDate, endDate));
        Long promoRateFRId = roomRateSessionBean.createNewRoomRate(new RoomRate("PromotionRateFR",RoomRateEnum.PromotionRate,Double.valueOf(120), startDate, endDate));
        Long promoRateJSId = roomRateSessionBean.createNewRoomRate(new RoomRate("PromotionRateJS",RoomRateEnum.PromotionRate,Double.valueOf(140), startDate, endDate));
        Long promoRateGSId = roomRateSessionBean.createNewRoomRate(new RoomRate("PromotionRateGS",RoomRateEnum.PromotionRate,Double.valueOf(160), startDate, endDate));

        Long[] DRroomRates = new Long[] {publishedRateDRId, normalRateDRId, peakRateDRId, promoRateDRId};
        Long[] PRroomRates = new Long[] {publishedRatePRId, normalRatePRId, peakRatePRId, promoRatePRId};
        Long[] FRroomRates = new Long[] {publishedRateFRId, normalRateFRId, peakRateFRId, promoRateFRId};
        Long[] JSroomRates = new Long[] {publishedRateJSId, normalRateJSId, peakRateJSId, promoRateJSId};
        Long[] GSroomRates = new Long[] {publishedRateGSId, normalRateGSId, peakRateGSId, promoRateGSId};

        return new Long[][]{DRroomRates, PRroomRates, FRroomRates, JSroomRates, GSroomRates};
    }

    private Long[] createRoomTypes() { 
        //String roomTypeName, String roomTypeDesc, Integer roomSize, Integer numOfBeds, Integer capacity, Integer rank, String amenities
        Long deluxeRoomId = roomTypeSessionBean.createNewRoomType(new RoomType("DeluxeRoom", "Deluxe Room", 400, 2, 4, 5, "Bed, Toilet, TV"));
        Long premierRoomId = roomTypeSessionBean.createNewRoomType(new RoomType("PremierRoom", "Premier Room", 600, 4, 6, 4, "Bed, Toilet, TV, Bathtub"));
        Long familyRoomId = roomTypeSessionBean.createNewRoomType(new RoomType("FamilyRoom", "Family Room", 800, 6, 8, 3, "Bed, Toilet, TV, Bathtub, Balcony"));
        Long juniorSuiteId = roomTypeSessionBean.createNewRoomType(new RoomType("JuniorSuite", "Junior Suite Room", 1000, 8, 10, 2,"Bed, Toilet, TV, Bathtub, Balcony, Fridge"));
        Long grandSuiteId = roomTypeSessionBean.createNewRoomType(new RoomType("GrandSuite", "Grand Suite Room", 1200, 10, 12, 1, "Bed, Toilet, TV, Bathtub, Balcony, Fridge, Safe"));

        return new Long[] {deluxeRoomId, premierRoomId, familyRoomId, juniorSuiteId, grandSuiteId};
    }
    
    private Long[][] createRooms() {
        //Integer roomLevel, Integer roomNum
        //Create 3Rooms for each RoomType
        Long room1DRId = roomSessionBean.createNewRoom(new Room(1,1));
        Long room2DRId = roomSessionBean.createNewRoom(new Room(1,2));
        Long room3DRId = roomSessionBean.createNewRoom(new Room(1,3));
        
        Long room1PRId = roomSessionBean.createNewRoom(new Room(2,1));
        Long room2PRId = roomSessionBean.createNewRoom(new Room(2,2));
        Long room3PRId = roomSessionBean.createNewRoom(new Room(2,3));
        
        Long room1FRId = roomSessionBean.createNewRoom(new Room(3,1));
        Long room2FRId = roomSessionBean.createNewRoom(new Room(3,2));
        Long room3FRId = roomSessionBean.createNewRoom(new Room(3,3));
        
        Long room1JSId = roomSessionBean.createNewRoom(new Room(4,1));
        Long room2JSId = roomSessionBean.createNewRoom(new Room(4,2));
        Long room3JSId = roomSessionBean.createNewRoom(new Room(4,3));
        
        Long room1GSId = roomSessionBean.createNewRoom(new Room(5,1));
        Long room2GSId = roomSessionBean.createNewRoom(new Room(5,2));
        Long room3GSId = roomSessionBean.createNewRoom(new Room(5,3));
        
        Long[] DRroomIds = new Long[] {room1DRId, room2DRId, room3DRId};
        Long[] PRroomIds = new Long[] {room1PRId, room2PRId, room3PRId};
        Long[] FRroomIds = new Long[] {room1FRId, room2FRId, room3FRId};
        Long[] JSroomIds = new Long[] {room1JSId, room2JSId, room3JSId};
        Long[] GSroomIds = new Long[] {room1GSId, room2GSId, room3GSId};
        
    
        return new Long[][]{DRroomIds, PRroomIds, FRroomIds, JSroomIds, GSroomIds};
    }
    
    
}
