/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.AllocationSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomManagementSessionBeanRemote;
import entity.Allocation;
import entity.Employee;
import entity.Guest;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import util.enumeration.EmployeeEnum;
import util.exception.FindEmployeeException;
import util.exception.FindRoomTypeException;
import util.exception.ReservationQueryException;
import util.exception.RoomTypeQueryException;

/**
 *
 * @author brend
 */
public class MainApp {
    
    private RoomManagementSessionBeanRemote roomManagementSessionBean;
    private EmployeeSessionBeanRemote employeeSessionBean;
    private PartnerSessionBeanRemote partnerSessionBean;
    private GuestSessionBeanRemote guestSessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;
    private AllocationSessionBeanRemote allocationSessionBean;
    
    MainApp(RoomManagementSessionBeanRemote roomManagementSessionBean, EmployeeSessionBeanRemote employeeSessionBean, 
            PartnerSessionBeanRemote partnerSessionBean, GuestSessionBeanRemote guestSessionBean, ReservationSessionBeanRemote reservationSessionBean,
            AllocationSessionBeanRemote allocationSessionBean) {
        this.roomManagementSessionBean = roomManagementSessionBean;
        this.employeeSessionBean = employeeSessionBean;
        this.partnerSessionBean = partnerSessionBean;
        this.guestSessionBean = guestSessionBean;
        this.reservationSessionBean = reservationSessionBean;
        this.allocationSessionBean = allocationSessionBean;
    }
    
    public void run() {
        try {
            Scanner sc = new Scanner(System.in);
        
            System.out.println("=== Welcome to HoRS Management Client. ===");
            System.out.println("Select an action:");
            System.out.println("> 1. Login");
            System.out.println("> 2. Allocate Rooms to Current Day Reservations");
            System.out.println("> 3. Exit");
            System.out.print("> ");
            int input = sc.nextInt();
            sc.nextLine();
            System.out.println();

            switch (input) {
                case 1:
                    doLogin(sc);
                    break;
                case 2:
                    doRoomAllocation();
                case 3:
                    doExit();
                    break;
                default:
                    run();
                    break;
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Try again.\n");
            run();
        }
        
    }

    private void doExit() {
        System.out.println("You have exited. Goodbye.");
    }

    private void doLogin(Scanner sc) {
        try {
            System.out.println("==== Login Interface ====");
            System.out.println("Enter login details:");
            System.out.print("> Employee ID: ");
            Long emId = sc.nextLong();
            sc.nextLine();
            System.out.print("> Password: ");
            String password = sc.nextLine();
            
            if (employeeSessionBean.verifyLoginDetails(emId, password) && 
                    employeeSessionBean.checkEmployeeExists(emId, password)) {

                Employee currEm = employeeSessionBean.getEmployeeById(emId);
                System.out.println("Welcome " + currEm.getEmployeeRole() + " " + currEm.getFirstName() + "\n");

                doDashboardFeatures(sc, currEm);
            } else {
                System.out.println("No account match or wrong login details. Try again.\n");
                doLogin(sc);
            }
        } catch (FindEmployeeException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e ) {
            System.out.println("Login Error. Try again.\n");
            run();
        }
            
    }
    
    private void doDashboardFeatures(Scanner sc, Employee em) {
        
        String emRole = em.getEmployeeRole();
        Long emId = em.getEmployeeId();
        if (emRole.equals(EmployeeEnum.SYSTEMADMIN.toString())) {
            SystemAdminModule module = new SystemAdminModule(partnerSessionBean, employeeSessionBean);
            module.doSystemAdminDashboardFeatures(sc, emId);
            run();
        } else if (emRole.equals(EmployeeEnum.OPSMANAGER.toString())) {
            OpsManagerModule module = new OpsManagerModule(roomManagementSessionBean);
            module.doOpsManagerDashboardFeatures(sc, emId);
            run();
        } else if (emRole.equals(EmployeeEnum.SALESMANAGER.toString())) {
            SalesManagerModule module = new SalesManagerModule(roomManagementSessionBean);
            module.doSalesManagerDashboardFeatures(sc, emId);
            run();
        } else if (emRole.equals(EmployeeEnum.GRELMANAGER.toString())) {
            doGRelManagerDashboardFeatures(sc, emId);
        }
        
        
    }

 

    private void doGRelManagerDashboardFeatures(Scanner sc, Long emId) {
        System.out.println("==== Guest Relations Manager Dashboard Interface ====");
        System.out.println("> 1. Walk in Search Room");
        System.out.println("> 2. Check-in Guest");
        System.out.println("> 3. Check-out Guest");
        System.out.println("> 4. Logout");
        System.out.print("> ");
        int input = sc.nextInt();
        sc.nextLine();
        
        switch (input) {
            case 1:
                System.out.println("You have selected 'Walk in Search Room'\n");
                doWalkInSearchRoom(sc, emId);
                break;
            case 2:
                System.out.println("You have selected 'Check-in Guest'\n");
                doCheckInGuest(sc, emId);
                break;
            case 3:
                System.out.println("You have selected 'Check-out Guest'\n");
                doCheckOutGuest(sc, emId);
                break;
            case 4:
                System.out.println("You have logged out.\n");
         
                break;
            default:
                System.out.println("Wrong input. Try again.\n");
                doGRelManagerDashboardFeatures(sc, emId);
                break;
        }
    }

    private void doRoomAllocation() {
        try {
            System.out.println("==== Allocating Rooms To Current Day Reservations ====");
            Scanner sc = new Scanner(System.in);
            System.out.println("Input Current Day [DD MM YYYY]:");
            System.out.print("> ");
            String currDay = sc.nextLine();

            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate currDate = LocalDate.parse(currDay, dtFormat);
            Date curr = Date.from(currDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            List<Reservation> reservations = reservationSessionBean.getReservationsToAllocate(currDate);
            for(Reservation reservation: reservations) {
                System.out.println("Allocating for Reservation ID: " + reservation.getReservationId());
                RoomType typeReserved = reservation.getRoomType();
                
                int numOfRoomsToAllocate = reservation.getNumOfRooms();
                List<Room> rooms = typeReserved.getRooms();

                List<Room> vacantRooms = new ArrayList<>();
                for (Room room : rooms) {
                    if (room.getIsVacant()) {
                        vacantRooms.add(room);
                    }
                    
                }

                if (vacantRooms.size() >= numOfRoomsToAllocate) {
                    System.out.println("> Number of Rooms to allocate: " + numOfRoomsToAllocate);
                    System.out.println("> Number of Vacant Rooms: " + vacantRooms.size());
                    
                    List<Room> allocatedRooms = vacantRooms.subList(0, numOfRoomsToAllocate);
                    
                    Allocation newAllocation = new Allocation(curr);
                    newAllocation = allocationSessionBean.getAllocationByAllocationId(allocationSessionBean.createNewAllocation(newAllocation));
                    
                    for (Room room : allocatedRooms) {
                        
                        allocationSessionBean.associateAllocationWithRoom(newAllocation.getAllocationId(), room.getRoomId());
                    }
                    
                    allocationSessionBean.associateAllocationWithReservation(newAllocation.getAllocationId(), reservation.getReservationId());
                    System.out.println("Successfully created an Allocation.");
                    DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                    System.out.println(":: Allocation ID: " + newAllocation.getAllocationId());
                    System.out.println("   > Reservation ID: " + newAllocation.getAllocationId());
                    System.out.println("   > Current Date:" + outputFormat.format(newAllocation.getCurrentDate()));
                    for (Room room : allocatedRooms) {
                        System.out.println("   > Room ID: " + room.getRoomId());
                    }
                    System.out.println();
                    run();
                } else {
                    System.out.println("\nUnable to fully allocate reservation.");
                }
                
                
            }
            
            
        } catch (Exception e) {
            System.out.println("Invalid input. Try again. " + e.toString());
            doRoomAllocation();
            
        }
        
        
    }
    
    private void doWalkInAllocation(LocalDate currDate, Long reservationId) {
        try {
            System.out.println("==== Walk In Allocating Rooms To Current Day Reservations ====");
     
            Date curr = Date.from(currDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

            Reservation reservation = reservationSessionBean.getReservationByReservationId(reservationId);
            
            System.out.println("Allocating for Reservation ID: " + reservation.getReservationId());
            RoomType typeReserved = reservation.getRoomType();

            int numOfRoomsToAllocate = reservation.getNumOfRooms();
            List<Room> rooms = typeReserved.getRooms();

            List<Room> vacantRooms = new ArrayList<>();
            for (Room room : rooms) {
                if (room.getIsVacant()) {
                    vacantRooms.add(room);
                }

            }

            if (vacantRooms.size() >= numOfRoomsToAllocate) {
                System.out.println("> Number of Rooms to allocate: " + numOfRoomsToAllocate);
                System.out.println("> Number of Vacant Rooms: " + vacantRooms.size());

                List<Room> allocatedRooms = vacantRooms.subList(0, numOfRoomsToAllocate);

                Allocation newAllocation = new Allocation(curr);
                newAllocation = allocationSessionBean.getAllocationByAllocationId(allocationSessionBean.createNewAllocation(newAllocation));

                for (Room room : allocatedRooms) {

                    allocationSessionBean.associateAllocationWithRoom(newAllocation.getAllocationId(), room.getRoomId());
                }

                allocationSessionBean.associateAllocationWithReservation(newAllocation.getAllocationId(), reservation.getReservationId());
                System.out.println("Successfully created an Allocation.");
                DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                System.out.println(":: Allocation ID: " + newAllocation.getAllocationId());
                System.out.println("   > Reservation ID: " + newAllocation.getAllocationId());
                System.out.println("   > Current Date:" + outputFormat.format(newAllocation.getCurrentDate()));
                for (Room room : allocatedRooms) {
                    System.out.println("   > Room ID: " + room.getRoomId());
                }
                System.out.println();
                run();
            } else {
                System.out.println("\nUnable to fully allocate reservation.");
            }
                  
            
        } catch (Exception e) {
            System.out.println("Invalid input. Try again. " + e.toString());
            doWalkInAllocation(currDate, reservationId);
            
        }
    }

    private void doCheckInGuest(Scanner sc, Long emId) {
        try {
            System.out.println("==== Check In Guest Interface ====");
            System.out.println("Please input your Guest details:");
            System.out.print("> Guest Email: ");
            String email = sc.nextLine();
            System.out.print("> Current Date [DD MM YYYY]: ");
            String currDay = sc.nextLine();
            System.out.println();
            
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate currDate = LocalDate.parse(currDay, dtFormat);
            Guest guest = guestSessionBean.getGuestByEmail(email);
            Allocation allocation = allocationSessionBean.getAllocationForGuestForCurrentDay(guest.getCustomerId(), currDate);
            
            System.out.println("Your room(s) are:");
            for (Room room : allocation.getRooms()) {
                System.out.println(":: Room Level: " + room.getRoomLevel());
                System.out.println(":: Room Number: " + room.getRoomNum());
                System.out.println();
            }
            
            doGRelManagerDashboardFeatures(sc, emId);
        } catch (Exception e) {
            System.out.println("Invalid input. Try again.\n" + e.toString());
            doCheckInGuest(sc, emId);
        }
        
    }
    
    private void doCheckOutGuest(Scanner sc, Long emId) {
        try {
            System.out.println("==== Check Out Guest Interface ====");
            System.out.println("Please input your Guest details:");
            System.out.print("> Guest Email: ");
            String email = sc.nextLine();
            System.out.print("> Current Date [DD MM YYYY]: ");
            String currDay = sc.nextLine();
            System.out.println();
            
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate currDate = LocalDate.parse(currDay, dtFormat);
            Guest guest = guestSessionBean.getGuestByEmail(email);
            Allocation allocation = allocationSessionBean.getAllocationForGuestForCheckOutDay(guest.getCustomerId(), currDate);
            
            for (Room room : allocation.getRooms()) {
                roomManagementSessionBean.updateRoomVacancy(room.getRoomId(), Boolean.TRUE);
            }
            
            System.out.println("Successfully checked out guest.");
            System.out.println("These room(s) are now vacant.");
            for (Room room : allocation.getRooms()) {
                room = roomManagementSessionBean.getRoom(room.getRoomId());
                System.out.println(" > Room ID: " + room.getRoomId());
                System.out.println("   > IsVacant: " + room.getIsVacant());
                
            }
            doGRelManagerDashboardFeatures(sc, emId);
        } catch (Exception e) {
            System.out.println("Invalid guest details. Try again.\n" + e.toString());
            doCheckInGuest(sc, emId);
        }
        
    }

    private void doWalkInSearchRoom(Scanner sc, Long emId) {
        try {
            System.out.println("==== Search Hotel Room Interface ====");
            System.out.println("Please input your check-in and check-out dates. Follow the format 'DD MM YYYY'.");
            System.out.print("> Check-In Date: ");
            String checkIn = sc.nextLine();
            System.out.print("> Check-Out Date: ");
            String checkOut = sc.nextLine();
            System.out.println();
            System.out.println("How many number rooms are you looking to reserve?");
            System.out.print("> Number of Rooms: ");
            int numOfRooms = sc.nextInt();
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate checkInDate = LocalDate.parse(checkIn, dtFormat);
            LocalDate checkOutDate = LocalDate.parse(checkOut, dtFormat);
            long daysBetween = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            
            //Output all the room types, give guest option to select the room he wants to search
            System.out.println("Here are all available Room Types for your " + daysBetween + " night(s) stay"
                    + ". Which Hotel Room would you like to reserve?");
            List<RoomType> types = roomManagementSessionBean.getAllRoomTypes();
            int count = 1;
            
            for (int i = 0; i < types.size(); i++) {
                RoomType type = types.get(i);
                //Check if room type is available first. If available then display
                boolean isRoomTypeAvail = reservationSessionBean.isRoomTypeAvailableForReservation(type.getRoomTypeId(), checkInDate, checkOutDate, numOfRooms);
                
                
                if (isRoomTypeAvail) {
                    //Derive the total reservation fee
                    double totalReservation = getTotalReservationFee(checkInDate, checkOutDate, type);
                
                    System.out.println("> " + count++ + ". " + type.getRoomTypeDesc() + 
                        "\n     ** Amenities: " + type.getAmenities() +
                        "\n     ** Total reservation fee is " + totalReservation);
                } else {
                    types.remove(type);
                    i--;
                    System.out.println("Room Type delete " + type.getRoomTypeName());
                }
            }
            System.out.print("> ");
            int input = sc.nextInt();
            sc.nextLine();
            System.out.println(); 

            RoomType selectedRoomType = types.get(input - 1);
            System.out.println("** You have selected " + selectedRoomType.getRoomTypeName() + "\n");
            System.out.println("Do you want to reserve the room?");
            System.out.println("> 1. Yes");
            System.out.println("> 2. No");
            System.out.print("> ");
            int reserveInput = sc.nextInt(); sc.nextLine(); System.out.println();
            if (reserveInput == 1) {
                Guest newGuest = doRegistration(sc);
                if (newGuest == null) return;
                
                Long guestId = guestSessionBean.createNewGuest(newGuest);
                System.out.println("Successfully registered guest.\n");
                
                doWalkInReserveRoom(sc, emId, guestId, checkInDate, checkOutDate, numOfRooms, selectedRoomType);
            } else {
                System.out.println("Going back to dashboard.\n");
                doGRelManagerDashboardFeatures(sc, emId);
            }
        } catch (FindRoomTypeException | RoomTypeQueryException | ReservationQueryException e) {
            System.out.println("Error occured.");
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("You have made a wrong input. Try again.\n");
            doWalkInSearchRoom(sc, emId);
        }
    }
    
    private void doWalkInReserveRoom(Scanner sc, Long emId, Long guestId, LocalDate checkInDate, LocalDate checkOutDate, int numOfRooms, RoomType selectedRoomType) {
        try {
            
            List<RoomRate> ratesUsed = getRoomRateUsed(checkInDate, checkOutDate, selectedRoomType);
            
            System.out.println("Confirm reservation?");
            System.out.println("> 1. Yes");
            System.out.println("> 2. No");
            System.out.print("> ");
            int confirmationInput = sc.nextInt(); sc.nextLine();
            System.out.println();
            
            if (confirmationInput == 1) {
                Date startDate = Date.from(checkInDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                Date endDate = Date.from(checkOutDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                Reservation reservation = new Reservation(startDate, endDate, numOfRooms, getTotalReservationFee(checkInDate, checkOutDate, selectedRoomType) * numOfRooms);
                Long reservationId = reservationSessionBean.createNewReservation(reservation);
                
                DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                reservation = reservationSessionBean.getReservationByReservationId(reservationId);
                
                reservationSessionBean.associateExistingReservationWithGuestAndRoomTypeAndRoomRates(reservationId, guestId, selectedRoomType.getRoomTypeId(), ratesUsed);
                System.out.println("You have made a reservation:");
                System.out.println(":: Reservation ID: " + reservation.getReservationId());
                System.out.println("> Number Of Rooms: " + reservation.getNumOfRooms());
                System.out.println("> Reservation Fee: " + reservation.getReservationFee());
                System.out.println("> Start Date: " + outputFormat.format(reservation.getStartDate()));
                System.out.println("> End Date: " + outputFormat.format(reservation.getEndDate()));
                System.out.println();
                
                doWalkInAllocation(checkInDate, reservation.getReservationId());
            } else {
                System.out.println("Going back to dashboard.\n");
                doGRelManagerDashboardFeatures(sc, emId);
            }
            
        } catch (FindRoomTypeException ex) {
            System.out.println("Error: " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("Something went wrong. " + e.toString());
            doGRelManagerDashboardFeatures(sc, emId);
        }
    }
    
    private double getTotalReservationFee(LocalDate checkInDate, LocalDate checkOutDate, RoomType selectedRoomType) throws FindRoomTypeException {
        double totalReservation = 0;
        long numOfDays = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        List<RoomRate> rates = roomManagementSessionBean.getRoomRates(selectedRoomType.getRoomTypeId());
            
        for (int i = 0; i < numOfDays; i++) {
                //get the rate Per night for each night
                boolean foundRate = false;
                for (int j = rates.size() - 1; j >= 0; j--) {
                    RoomRate rate = rates.get(j);
                    if (((rate.getStartDate() == null) || isCurrentDateWithinRange(checkInDate, rate.getStartDate(), rate.getEndDate())) && !foundRate ) {
                        totalReservation += rate.getRatePerNight();
                        checkInDate = checkInDate.plusDays(1);
                        foundRate = true;
                    }
                }
            }
        return totalReservation;
    }

    public Guest doRegistration(Scanner sc) {
        System.out.println("==== Register Interface ====");
        System.out.println("Enter guest details. To cancel registration at anytime, press 'q'.");
        System.out.print("> First Name: ");
        String firstName = sc.nextLine();
        
        System.out.print("> Last Name: ");
        String lastName = sc.nextLine();
        
        System.out.print("> Email: ");
        String email = sc.nextLine();
        
        System.out.print("> Contact Number: ");
        String numberInput = sc.nextLine();
        
        Long number = Long.parseLong(numberInput);
        
        Guest newGuest;
        if (guestSessionBean.verifyRegisterDetails(firstName, lastName, number, email)) {
            newGuest = new Guest(firstName, lastName, number, email);
            return newGuest;
        } else {
            System.out.println("You have inputted wrong details. Please try again.\n");

            return null;
        }
    }
    
    public void doCancelledRegistration(Scanner sc) {
        System.out.println("\nYou have cancelled registration.\n");
        
        run();
    }
    
    private boolean isCurrentDateWithinRange(LocalDate currDate, Date startDate, Date endDate) {
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        boolean lowerBound = currDate.isAfter(start) || currDate.isEqual(start);
        boolean upperBound = currDate.isBefore(end) || currDate.isEqual(end);
        return lowerBound && upperBound;
    }

    private List<RoomRate> getRoomRateUsed(LocalDate checkInDate, LocalDate checkOutDate, RoomType selectedRoomType) throws FindRoomTypeException {
        long numOfDays = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        List<RoomRate> rates = roomManagementSessionBean.getRoomRates(selectedRoomType.getRoomTypeId());
        List<RoomRate> ratesUsed = new ArrayList<>();
        for (int i = 0; i < numOfDays; i++) {
            //get the rate Per night for each night
            boolean foundRate = false;
            for (int j = rates.size() - 1; j >= 0; j--) {
                RoomRate rate = rates.get(j);
                if (((rate.getStartDate() == null) || isCurrentDateWithinRange(checkInDate, rate.getStartDate(), rate.getEndDate())) && !foundRate ) {
                    if (!ratesUsed.contains(rate)) ratesUsed.add(rate); //Adding unique room rates
                    checkInDate = checkInDate.plusDays(1);
                    foundRate = true;
                }
            }
        }
        return ratesUsed;
    }
}
