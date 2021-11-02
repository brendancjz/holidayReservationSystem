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
                //Linking them alr
                createRoomRates(roomTypeIds);
                System.out.println("created all room rates and linked");

                //Create some rooms 
                createRooms(roomTypeIds);
                System.out.println("created all rooms and linked");

            } 
              
            if (em.find(Reservation.class, 1L) == null) {
                LocalDateTime startLocalDateTime = LocalDateTime.of(2021,10, 10, 0, 0, 0);
                Date startDate = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
                LocalDateTime endLocalDateTime = LocalDateTime.of(2021,10, 14, 0, 0, 0);
                Date endDate = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
                
                Reservation reservation = new Reservation(startDate, endDate, 1, Double.valueOf(480));
                reservationSessionBean.associateReservationWithGuestAndRoomTypeAndRoomRate(reservation, 1L, 1L, 1L);
                Long reservationId = reservationSessionBean.createNewReservation(reservation);
                guestSessionBean.associateGuestWithReservation(1L, reservationId);
                
                startLocalDateTime = LocalDateTime.of(2021,10, 12, 0, 0, 0);
                startDate = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
                endLocalDateTime = LocalDateTime.of(2021,10, 16, 0, 0, 0);
                endDate = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
                
                Reservation reservation2 = new Reservation(startDate, endDate, 1, Double.valueOf(480));
                reservationSessionBean.associateReservationWithGuestAndRoomTypeAndRoomRate(reservation2, 1L, 2L, 2L);
                reservationId = reservationSessionBean.createNewReservation(reservation2);
                guestSessionBean.associateGuestWithReservation(1L, reservationId);
                
                startLocalDateTime = LocalDateTime.of(2021,10, 15, 0, 0, 0);
                startDate = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
                endLocalDateTime = LocalDateTime.of(2021,10, 18, 0, 0, 0);
                endDate = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
                
                Reservation reservation3 = new Reservation(startDate, endDate, 1, Double.valueOf(420));
                reservationSessionBean.associateReservationWithGuestAndRoomTypeAndRoomRate(reservation3, 1L, 4L, 4L);
                reservationId = reservationSessionBean.createNewReservation(reservation3);
                guestSessionBean.associateGuestWithReservation(1L, reservationId);
                
                
                System.out.println("created all reservations");
            }
             
            if (em.find(Allocation.class, 1L) == null) {
                LocalDateTime startLocalDateTime = LocalDateTime.of(2021,10, 10, 0, 0, 0);
                Date currDate = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
                
                //CREATE ALLOCATION
                Allocation allocation = new Allocation(currDate);
                //ASSOCIATE
                allocationSessionBean.associateAllocationWithReservation(allocation, 1L);
                allocationSessionBean.associateAllocationWithRoom(allocation, 1L);
                //PERSIST
                Long allocationId = allocationSessionBean.createNewAllocation(allocation);
                
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
                if (allocation.getReservation() != null) System.out.println(" > Reservation ID: " + allocation.getReservation().getReservationId());
                
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
    
    private void createRoomRates(Long[] roomTypeIds) {
        
        //String roomRateName, String roomRateType, Double ratePerNight, Date startDate, Date endDate
        //String roomRateName, String roomRateType, Double ratePerNight
        
        //CREATE ROOM RATE
        RoomRate rate1 = new RoomRate("PublishedRateDR",RoomRateEnum.PublishedRate,Double.valueOf(120));
        //ASSOCIATE ROOM RATE WITH ROOM TYPE
        roomRateSessionBean.associateRoomRateWithRoomType(rate1, roomTypeIds[0]);
        //PERSIST ROOM RATE
        Long publishedRateDRId = roomRateSessionBean.createNewRoomRate(rate1);
        //ASSOCIATE ROOM TYPE WITH ROOM RATE
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[0], publishedRateDRId);
        
        RoomRate rate2 = new RoomRate("PublishedRatePR",RoomRateEnum.PublishedRate,Double.valueOf(140));
        roomRateSessionBean.associateRoomRateWithRoomType(rate2, roomTypeIds[0]);
        Long publishedRatePRId = roomRateSessionBean.createNewRoomRate(rate2);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[0], publishedRatePRId);
        
        RoomRate rate3 = new RoomRate("PublishedRateFR",RoomRateEnum.PublishedRate,Double.valueOf(160));
        roomRateSessionBean.associateRoomRateWithRoomType(rate3, roomTypeIds[0]);
        Long publishedRateFRId = roomRateSessionBean.createNewRoomRate(rate3);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[0], publishedRateFRId);
        
        RoomRate rate4 = new RoomRate("PublishedRateJS",RoomRateEnum.PublishedRate,Double.valueOf(180)); 
        roomRateSessionBean.associateRoomRateWithRoomType(rate4, roomTypeIds[0]);
        Long publishedRateJSId = roomRateSessionBean.createNewRoomRate(rate4);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[0], publishedRateJSId);
        
        RoomRate rate5 = new RoomRate("PublishedRateGS",RoomRateEnum.PublishedRate,Double.valueOf(200));
        roomRateSessionBean.associateRoomRateWithRoomType(rate5, roomTypeIds[0]);
        Long publishedRateGSId = roomRateSessionBean.createNewRoomRate(rate5);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[0], publishedRateGSId);

        RoomRate rate6 = new RoomRate("NormalRateDR",RoomRateEnum.NormalRate,Double.valueOf(100));
        roomRateSessionBean.associateRoomRateWithRoomType(rate6, roomTypeIds[1]);
        Long normalRateDRId = roomRateSessionBean.createNewRoomRate(rate6);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[1], normalRateDRId);
        
        RoomRate rate7 = new RoomRate("NormalRatePR",RoomRateEnum.NormalRate,Double.valueOf(120));
        roomRateSessionBean.associateRoomRateWithRoomType(rate7, roomTypeIds[1]);
        Long normalRatePRId = roomRateSessionBean.createNewRoomRate(rate7);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[1], normalRatePRId);
        
        RoomRate rate8 = new RoomRate("NormalRateFR",RoomRateEnum.NormalRate,Double.valueOf(140));
        roomRateSessionBean.associateRoomRateWithRoomType(rate8, roomTypeIds[1]);
        Long normalRateFRId = roomRateSessionBean.createNewRoomRate(rate8);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[1], normalRateFRId);
        
        RoomRate rate9 = new RoomRate("NormalRateJS",RoomRateEnum.NormalRate,Double.valueOf(160));
        roomRateSessionBean.associateRoomRateWithRoomType(rate9, roomTypeIds[1]);
        Long normalRateJSId = roomRateSessionBean.createNewRoomRate(rate9);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[1], normalRateJSId);
        
        RoomRate rate10 = new RoomRate("NormalRateGS",RoomRateEnum.NormalRate,Double.valueOf(180));
        roomRateSessionBean.associateRoomRateWithRoomType(rate10, roomTypeIds[1]);
        Long normalRateGSId = roomRateSessionBean.createNewRoomRate(rate10);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[1], normalRateGSId);
        

        //Validity Period for Peak Rate
        LocalDateTime startLocalDateTime = LocalDateTime.of(2021,10, 10, 0, 0, 0);
        Date startDate = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
        LocalDateTime endLocalDateTime = LocalDateTime.of(2021,10, 14, 0, 0, 0);
        Date endDate = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

        RoomRate rate11 = new RoomRate("PeakRateDR",RoomRateEnum.PeakRate,Double.valueOf(120), startDate, endDate);
        roomRateSessionBean.associateRoomRateWithRoomType(rate11, roomTypeIds[2]);
        Long peakRateDRId = roomRateSessionBean.createNewRoomRate(rate11);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[2], peakRateDRId);
        
        RoomRate rate12 = new RoomRate("PeakRatePR",RoomRateEnum.PeakRate,Double.valueOf(140), startDate, endDate);
        roomRateSessionBean.associateRoomRateWithRoomType(rate12, roomTypeIds[2]);
        Long peakRatePRId = roomRateSessionBean.createNewRoomRate(rate12);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[2], peakRatePRId);
        
        RoomRate rate13 = new RoomRate("PeakRateFR",RoomRateEnum.PeakRate,Double.valueOf(160), startDate, endDate);
        roomRateSessionBean.associateRoomRateWithRoomType(rate13, roomTypeIds[2]);
        Long peakRateFRId = roomRateSessionBean.createNewRoomRate(rate13);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[2], peakRateFRId);
        
        RoomRate rate14 = new RoomRate("PeakRateJS",RoomRateEnum.PeakRate,Double.valueOf(180), startDate, endDate);
        roomRateSessionBean.associateRoomRateWithRoomType(rate14, roomTypeIds[2]);
        Long peakRateJSId = roomRateSessionBean.createNewRoomRate(rate14);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[2], peakRateJSId);
        
        RoomRate rate15 = new RoomRate("PeakRateGS",RoomRateEnum.PeakRate,Double.valueOf(200), startDate, endDate);
        roomRateSessionBean.associateRoomRateWithRoomType(rate15, roomTypeIds[2]);
        Long peakRateGSId = roomRateSessionBean.createNewRoomRate(rate15);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[2], peakRateGSId);

        //Validity Period for Promo Rate
        startLocalDateTime = LocalDateTime.of(2021,10, 14, 0, 0, 0);
        startDate = Date.from(startLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
        endLocalDateTime = LocalDateTime.of(2021,10, 21, 0, 0, 0);
        endDate = Date.from(endLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
        
        RoomRate rate16 = new RoomRate("PromotionRateDR",RoomRateEnum.PromotionRate,Double.valueOf(80), startDate, endDate);
        roomRateSessionBean.associateRoomRateWithRoomType(rate16, roomTypeIds[3]);
        Long promoRateDRId = roomRateSessionBean.createNewRoomRate(rate16);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[3], promoRateDRId);
        
        RoomRate rate17 = new RoomRate("PromotionRatePR",RoomRateEnum.PromotionRate,Double.valueOf(100), startDate, endDate);
        roomRateSessionBean.associateRoomRateWithRoomType(rate17, roomTypeIds[3]);
        Long promoRatePRId = roomRateSessionBean.createNewRoomRate(rate17);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[3], promoRatePRId);
        
        RoomRate rate18 = new RoomRate("PromotionRateFR",RoomRateEnum.PromotionRate,Double.valueOf(120), startDate, endDate);
        roomRateSessionBean.associateRoomRateWithRoomType(rate18, roomTypeIds[3]);
        Long promoRateFRId = roomRateSessionBean.createNewRoomRate(rate18);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[3], promoRateFRId);
        
        RoomRate rate19 = new RoomRate("PromotionRateJS",RoomRateEnum.PromotionRate,Double.valueOf(140), startDate, endDate);
        roomRateSessionBean.associateRoomRateWithRoomType(rate19, roomTypeIds[3]);
        Long promoRateJSId = roomRateSessionBean.createNewRoomRate(rate19);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[3], promoRateJSId);
        
        RoomRate rate20 = new RoomRate("PromotionRateGS",RoomRateEnum.PromotionRate,Double.valueOf(160), startDate, endDate);
        roomRateSessionBean.associateRoomRateWithRoomType(rate20, roomTypeIds[3]);
        Long promoRateGSId = roomRateSessionBean.createNewRoomRate(rate20);
        roomTypeSessionBean.associateRoomTypeWithRoomRate(roomTypeIds[3], promoRateGSId);

        
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
    
    private void createRooms(Long[] roomTypeIds) {
        
        //Integer roomLevel, Integer roomNum
        //Create 3Rooms for each RoomType
        
        //CREATE ROOM
        Room room1 = new Room(1,1);
        //ASSOCIATE ROOM WITH ROOM TYPE
        roomSessionBean.associateRoomWithRoomType(room1, roomTypeIds[0]);
        //PERSIST ROOM
        Long room1DRId = roomSessionBean.createNewRoom(room1);
        //ASSOCIATE ROOM TYPE WITH ROOM
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[0], room1DRId);
        
        Room room2 = new Room(1,2);
        roomSessionBean.associateRoomWithRoomType(room2, roomTypeIds[0]);
        Long room2DRId = roomSessionBean.createNewRoom(room2);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[0], room2DRId);
        
        Room room3 = new Room(1,3);
        roomSessionBean.associateRoomWithRoomType(room3, roomTypeIds[0]);
        Long room3DRId = roomSessionBean.createNewRoom(room3);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[0], room3DRId);
        
        Room room4 = new Room(2,1);
        roomSessionBean.associateRoomWithRoomType(room4, roomTypeIds[1]);
        Long room1PRId = roomSessionBean.createNewRoom(room4);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[1], room1PRId);
        
        Room room5 = new Room(2,2);
        roomSessionBean.associateRoomWithRoomType(room5, roomTypeIds[1]);
        Long room2PRId = roomSessionBean.createNewRoom(room5);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[1], room2PRId);
        
        Room room6 = new Room(2,3);
        roomSessionBean.associateRoomWithRoomType(room6, roomTypeIds[1]);
        Long room3PRId = roomSessionBean.createNewRoom(room6);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[1], room3PRId);
        
        Room room7 = new Room(3,1);
        roomSessionBean.associateRoomWithRoomType(room7, roomTypeIds[2]);
        Long room1FRId = roomSessionBean.createNewRoom(room7);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[2], room1FRId);
        
        Room room8 = new Room(3,2);
        roomSessionBean.associateRoomWithRoomType(room8, roomTypeIds[2]);
        Long room2FRId = roomSessionBean.createNewRoom(room8);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[2], room2FRId);
        
        Room room9 = new Room(3,3);
        roomSessionBean.associateRoomWithRoomType(room9, roomTypeIds[2]);
        Long room3FRId = roomSessionBean.createNewRoom(room9);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[2], room3FRId);
        
        Room room10 = new Room(4,1);
        roomSessionBean.associateRoomWithRoomType(room10, roomTypeIds[3]);
        Long room1JSId = roomSessionBean.createNewRoom(room10);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[3], room1JSId);
        
        Room room11 = new Room(4,2);
        roomSessionBean.associateRoomWithRoomType(room11, roomTypeIds[3]);
        Long room2JSId = roomSessionBean.createNewRoom(room11);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[3], room2JSId);
        
        Room room12 = new Room(4,3);
        roomSessionBean.associateRoomWithRoomType(room12, roomTypeIds[3]);
        Long room3JSId = roomSessionBean.createNewRoom(room12);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[3], room3JSId);
        
        Room room13 = new Room(5,1);
        roomSessionBean.associateRoomWithRoomType(room13, roomTypeIds[4]);
        Long room1GSId = roomSessionBean.createNewRoom(room13);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[4], room1GSId);
        
        Room room14 = new Room(5,2);
        roomSessionBean.associateRoomWithRoomType(room14, roomTypeIds[4]);
        Long room2GSId = roomSessionBean.createNewRoom(room14);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[4], room2GSId);
        
        Room room15 = new Room(5,3);
        roomSessionBean.associateRoomWithRoomType(room15, roomTypeIds[4]);
        Long room3GSId = roomSessionBean.createNewRoom(room15);
        roomTypeSessionBean.associateRoomTypeWithRoom(roomTypeIds[4], room3GSId);
        
        
    }
    
    
}
