/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateless.AllocationExceptionSessionBeanRemote;
import ejb.session.stateless.AllocationSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomManagementSessionBeanRemote;
import entity.Allocation;
import entity.AllocationException;
import entity.Guest;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import javax.ejb.EJBTransactionRolledbackException;
import util.exception.EmptyListException;
import util.exception.InvalidInputException;
import util.exception.ReservationExistException;

/**
 *
 * @author brend
 */
public class MainApp {

    private RoomManagementSessionBeanRemote roomManagementSessionBean;
    private GuestSessionBeanRemote guestSessionBean;
    private PartnerSessionBeanRemote partnerSessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;
    private AllocationSessionBeanRemote allocationSessionBean;
    private AllocationExceptionSessionBeanRemote allocationExceptionSessionBean;

    public MainApp(RoomManagementSessionBeanRemote roomManagementSessionBean,
            GuestSessionBeanRemote guestSessionBean,
            PartnerSessionBeanRemote partnerSessionBean,
            ReservationSessionBeanRemote reservationSessionBean,
            AllocationSessionBeanRemote allocationSessionBean,
            AllocationExceptionSessionBeanRemote allocationExceptionSessionBean) {
        this.roomManagementSessionBean = roomManagementSessionBean;
        this.guestSessionBean = guestSessionBean;
        this.partnerSessionBean = partnerSessionBean;
        this.reservationSessionBean = reservationSessionBean;
        this.allocationExceptionSessionBean = allocationExceptionSessionBean;
        this.allocationSessionBean = allocationSessionBean;
    }

    public void run() {
        try {
            Scanner sc = new Scanner(System.in);

            System.out.println("=== Welcome to HoRS Reservation Client. ===");
            System.out.println("Select an action:");
            System.out.println("> 1. Login");
            System.out.println("> 2. Register as Guest");
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
                    doRegistration(sc);
                    break;
                case 3:
                    System.out.println("You have exited. Goodbye.");
                    break;
                default:
                    System.out.println("Invalid input. Try again.\n");
                    run();
                    break;
            }
        } catch (Exception e) {
            System.out.println("Invalid Input. Please try again.\n");
            run();
        }

    }

    public void doLogin(Scanner sc) {
        System.out.println("==== Login Interface ====");
        System.out.println("Enter login details:");
        System.out.print("> Email: ");
        String email = sc.nextLine();
        try {
            if (guestSessionBean.checkGuestExists(email)) {

                Guest currGuest = guestSessionBean.getGuestByEmail(email);
                System.out.println("Welcome " + currGuest.getFirstName() + ", you're in!\n");

                doDashboardFeatures(sc, currGuest.getCustomerId());

            } else {
                System.out.println("No account match or wrong login details. Try again.\n");
                doLogin(sc);
            }
        } catch (Exception e) {
            System.out.println("Uh oh.. Something went wrong.\n");
            run();
        }

    }

    public void doDashboardFeatures(Scanner sc, Long guestId) {
        System.out.println("==== Dashboard Interface ====");
        System.out.println("> 1. Search Hotel Room");
        System.out.println("> 2. View My Reservation Details");
        System.out.println("> 3. View All My Reservations");
        System.out.println("> 4. Logout");
        System.out.print("> ");
        int input = sc.nextInt();
        sc.nextLine();

        switch (input) {
            case 1:
                System.out.println("You have selected 'Search Hotel Room'\n");
                doSearchHotelRoom(sc, guestId);
                break;
            case 2:
                System.out.println("You have selected 'View My Reservation Details'\n");
                doViewMyReservationDetails(sc, guestId);
                break;
            case 3:
                System.out.println("You have selected 'View All My Reservations'\n");
                doViewAllMyReservations(sc, guestId);
                break;
            case 4:
                System.out.println("You have logged out.\n");
                run();
                break;
            default:
                System.out.println("Wrong input. Try again.\n");
                doDashboardFeatures(sc, guestId);
                break;
        }
    }

    public void doViewAllMyReservations(Scanner sc, Long guestId) {
        try {
            System.out.println("==== View All My Reservations Interface ====");
            Guest guest = guestSessionBean.getGuestByGuestId(guestId);
            List<Reservation> reservations = guest.getReservations();

            DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            System.out.printf("\n%3s%30s%15s%15s%30s", "ID", "Room Type", "No. of Rooms", "Total Fees", "Duration");
            for (Reservation reservation : reservations) {
                System.out.printf("\n%3s%30s%15s%15s%30s", reservation.getReservationId(),
                        reservation.getRoomType().getRoomTypeName(), reservation.getReservationFee(),
                        reservation.getNumOfRooms(), outputFormat.format(reservation.getStartDate())
                        + " -> " + outputFormat.format(reservation.getEndDate()));

            }
            System.out.println();
            System.out.println();
            doDashboardFeatures(sc, guestId);
        } catch (Exception e) {
            System.out.println("Uh oh.. Something went wrong.\n");
            doDashboardFeatures(sc, guestId);
        }
        
    }

    public void doViewMyReservationDetails(Scanner sc, Long guestId) {
        try {
            System.out.println("==== View My Reservation Details Interface ====");
            System.out.println("Enter your reservation's details:");
            System.out.print("> Check-In Date [DD MM YYYY]: ");
            String checkIn = sc.nextLine();
            System.out.print("> Check-Out Date [DD MM YYYY]: ");
            String checkOut = sc.nextLine();
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate checkInDate = LocalDate.parse(checkIn, dtFormat);
            LocalDate checkOutDate = LocalDate.parse(checkOut, dtFormat);
            if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
                throw new InvalidInputException("Invalid dates input.\n");
            }
            System.out.println("Which Room Type did you reserve?");
            List<RoomType> types = roomManagementSessionBean.getAllRoomTypes();
            int idx = 1;
            for (RoomType type : types) {
                System.out.println("> " + idx++ + ". " + type.getRoomTypeName());
            }
            System.out.print("> ");
            int typeInput = sc.nextInt();
            sc.nextLine();

            RoomType selectedType = types.get(typeInput - 1);
            System.out.println();
            Reservation reservation = reservationSessionBean.getReservationsByRoomTypeIdAndDuration(selectedType.getRoomTypeId(), checkInDate, checkOutDate, guestId);
            if (reservation == null) {
                throw new ReservationExistException("Reservation does not exist.\n");
            }
            DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            String duration = outputFormat.format(reservation.getStartDate())
                    + " -> " + outputFormat.format(reservation.getEndDate());
            System.out.println("Selected Reservation details:");
            System.out.println(":: Reservation ID: " + reservation.getReservationId());
            System.out.println("   > Reservation Fee: " + reservation.getReservationFee());

            System.out.println("   > Room Type: " + reservation.getRoomType().getRoomTypeName());
            System.out.println("   > Num of Rooms: " + reservation.getNumOfRooms());
            System.out.println("   > Duration: " + duration);
            System.out.println();
            doDashboardFeatures(sc, guestId);
        } catch (InvalidInputException | ReservationExistException | EmptyListException e) {
            System.out.println(e.getMessage());
            doDashboardFeatures(sc, guestId);
        } catch (Exception e) {
            System.out.println("Uh oh.. Something went wrong.\n");
            doViewMyReservationDetails(sc, guestId);
        }
    }

    public void doSearchHotelRoom(Scanner sc, Long guestId) {
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
            sc.nextLine();
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate checkInDate = LocalDate.parse(checkIn, dtFormat);
            LocalDate checkOutDate = LocalDate.parse(checkOut, dtFormat);
            if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
                throw new InvalidInputException("Invalid dates input.\n");
            }
            long daysBetween = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

            //Output all the room types, give guest option to select the room he wants to search
            System.out.println("Here are all available Room Types for your " + daysBetween + " night(s) stay.");
            List<RoomType> types = roomManagementSessionBean.getAllRoomTypes();
            int count = 1;

            for (int i = 0; i < types.size(); i++) {
                RoomType type = types.get(i);
                //Check if room type is available first. If available then display
                boolean isRoomTypeAvail = reservationSessionBean.isRoomTypeAvailableForReservation(type.getRoomTypeId(), checkInDate, checkOutDate, numOfRooms);

                if (isRoomTypeAvail) {
                    //Derive the total reservation fee
                    double totalReservation = getTotalReservationFee(checkInDate, checkOutDate, type);

                    System.out.println("> " + count++ + ". " + type.getRoomTypeDesc()
                            + "\n     ** Amenities: " + type.getAmenities()
                            + "\n     ** Total reservation fee is " + totalReservation * numOfRooms);
                } else {
                    types.remove(type);
                    i--;

                }
            }

            System.out.println();
            System.out.println("Do you want to reserve a room?");
            System.out.println("> 1. Yes");
            System.out.println("> 2. No");
            System.out.print("> ");
            int reserveInput = sc.nextInt();
            sc.nextLine();
            System.out.println();
            if (reserveInput == 1) {
                System.out.println("Which Hotel Room would you like to reserve?\n");
                count = 1;
                for (int i = 0; i < types.size(); i++) {
                    RoomType type = types.get(i);
                    //Check if room type is available first. If available then display
                    boolean isRoomTypeAvail = reservationSessionBean.isRoomTypeAvailableForReservation(type.getRoomTypeId(), checkInDate, checkOutDate, numOfRooms);

                    if (isRoomTypeAvail) {
                        //Derive the total reservation fee
                        double totalReservation = getTotalReservationFee(checkInDate, checkOutDate, type);

                        System.out.println("> " + count++ + ". " + type.getRoomTypeDesc()
                                + "\n     ** Amenities: " + type.getAmenities()
                                + "\n     ** Total reservation fee is " + totalReservation * numOfRooms);
                    } else {
                        types.remove(type);
                        i--;

                    }
                }
                System.out.print("> ");
                int input = sc.nextInt();
                sc.nextLine();
                System.out.println();
                RoomType selectedRoomType = types.get(input - 1);
                System.out.println("** You have selected " + selectedRoomType.getRoomTypeName() + "\n");

                doReserveHotelRoom(sc, guestId, checkInDate, checkOutDate, numOfRooms, selectedRoomType);
            } else {
                System.out.println("Going back to dashboard.\n");
                doDashboardFeatures(sc, guestId);
            }

        } catch (EmptyListException | InvalidInputException e) {
            System.out.println(e.toString());
            System.out.println("You have made a wrong input. Try again.\n");
            doDashboardFeatures(sc, guestId);
        } catch (Exception e) {
            System.out.println("Uh oh.. Something went wrong.\n");
            doSearchHotelRoom(sc, guestId);
        }

    }

    private void doReserveHotelRoom(Scanner sc, Long guestId, LocalDate checkInDate, LocalDate checkOutDate, int numOfRooms, RoomType selectedRoomType) {

        try {
            List<RoomRate> ratesUsed = getRoomRateUsed(checkInDate, checkOutDate, selectedRoomType);

            System.out.println("Confirm reservation?");
            System.out.println("> 1. Yes");
            System.out.println("> 2. No");
            System.out.print("> ");
            int confirmationInput = sc.nextInt();
            sc.nextLine();
            System.out.println();

            if (confirmationInput == 1) {

                Date startDate = Date.from(checkInDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                Date endDate = Date.from(checkOutDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

                //CREATE RESERVATION OBJECT
                Reservation reservation = new Reservation(startDate, endDate, numOfRooms, getTotalReservationFee(checkInDate, checkOutDate, selectedRoomType) * numOfRooms);

                DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

                //PERSIST RESERVATION
                Long reservationId = reservationSessionBean.createNewReservation(reservation, guestId, selectedRoomType.getRoomTypeId(), ratesUsed);

                //ASSOCIATE RESERVATION TO GUEST
                guestSessionBean.associateGuestWithReservation(guestId, reservationId);

                reservation = reservationSessionBean.getReservationByReservationId(reservationId);

                System.out.println("You have made a reservation:");
                System.out.println(":: Reservation ID: " + reservation.getReservationId());
                System.out.println("> Number Of Rooms: " + reservation.getNumOfRooms());
                System.out.println("> Reservation Fee: " + reservation.getReservationFee());
                System.out.println("> Start Date: " + outputFormat.format(reservation.getStartDate()));
                System.out.println("> End Date: " + outputFormat.format(reservation.getEndDate()));
                System.out.println();

                //Check if the checkin date is equals to today and if it is past 2am, start allocation.
                Integer timeCheck = LocalDateTime.now().getHour();
                
                
                if (checkInDate.isEqual(LocalDate.now()) && timeCheck >= 2) {
                    //do allocation
                    System.out.println("Auto allocation of room because reservation is for today and after 2pm.\n");
                    
                    doWalkInAllocation(checkInDate, reservation.getReservationId(), sc, guestId);
                }

            } else {
                System.out.println("Going back to dashboard.\n");
            }
            doDashboardFeatures(sc, guestId);
        } catch (EmptyListException ex) {
            System.out.println(ex.getMessage());
            doDashboardFeatures(sc, guestId);
        } catch (Exception e ) {
            System.out.println("Uh oh.. Something went wrong.\n");
            doDashboardFeatures(sc, guestId);
        }

    }

    public void doRegistration(Scanner sc) {
        try {
            System.out.println("==== Register Interface ====");
            System.out.println("Enter guest details. To cancel registration at anytime, press 'q'.");
            System.out.print("> First Name: ");
            String firstName = sc.nextLine();
            if (firstName.equals("q")) {
                doCancelledRegistration(sc);
                return;
            }
            System.out.print("> Last Name: ");
            String lastName = sc.nextLine();
            if (lastName.equals("q")) {
                doCancelledRegistration(sc);
                return;
            }
            System.out.print("> Email: ");
            String email = sc.nextLine();
            if (email.equals("q")) {
                doCancelledRegistration(sc);
                return;
            }
            System.out.print("> Contact Number: ");
            String numberInput = sc.nextLine();
            if (numberInput.equals("q")) {
                doCancelledRegistration(sc);
                return;
            }
            Long number = Long.parseLong(numberInput);

            Guest newGuest = new Guest(firstName, lastName, number, email);
            Long guestId = guestSessionBean.createNewGuest(newGuest);
            System.out.println("Welcome, you're in!\n");

            doDashboardFeatures(sc, guestId);
            
        } catch (NumberFormatException | EJBTransactionRolledbackException e) {
            System.out.println("Sorry. You have inputted invalid values. Try again.\n");
            run();
        } catch (Exception e) {
            System.out.println("Uh oh.. Something went wrong.\n");
            run();
        }
        

    }

    public void doCancelledRegistration(Scanner sc) {
        System.out.println("\nYou have cancelled registration.\n");

        run();
    }

    private double getTotalReservationFee(LocalDate checkInDate, LocalDate checkOutDate, RoomType selectedRoomType) throws EmptyListException {
        double totalReservation = 0;
        long numOfDays = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        List<RoomRate> rates = roomManagementSessionBean.getRoomRates(selectedRoomType.getRoomTypeId());
        
        //Sort the rates
        RoomRate[] orderedRates = new RoomRate[4];
        for (RoomRate rate : rates) {
            if (null != rate.getRoomRateType()) switch (rate.getRoomRateType()) {
                case PublishedRate:
                    orderedRates[3] = rate;
                    break;
                case NormalRate:
                    orderedRates[2] = rate;
                    break;
                case PeakRate:
                    orderedRates[1] = rate;
                    break;
                case PromotionRate:
                    orderedRates[0] = rate;
                    break;
                default:
                    break;
            }
        }

        for (int i = 0; i < numOfDays; i++) {
            //get the rate Per night for each night
            boolean foundRate = false;
            for (int j = 0; j < orderedRates.length; j++) {
                RoomRate rate = orderedRates[j];
                if (rate != null && ((rate.getStartDate() == null) || isCurrentDateWithinRange(checkInDate, rate.getStartDate(), rate.getEndDate())) && !foundRate) {
                    totalReservation += rate.getRatePerNight();
                    checkInDate = checkInDate.plusDays(1);
                    foundRate = true;
                }
            }
        }
        return totalReservation;
    }

    private List<RoomRate> getRoomRateUsed(LocalDate checkInDate, LocalDate checkOutDate, RoomType selectedRoomType) throws EmptyListException {
        long numOfDays = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        List<RoomRate> rates = roomManagementSessionBean.getRoomRates(selectedRoomType.getRoomTypeId());
        List<RoomRate> ratesUsed = new ArrayList<>();
        for (int i = 0; i < numOfDays; i++) {
            //get the rate Per night for each night
            boolean foundRate = false;
            for (int j = rates.size() - 1; j >= 0; j--) {
                RoomRate rate = rates.get(j);
                if (((rate.getStartDate() == null) || isCurrentDateWithinRange(checkInDate, rate.getStartDate(), rate.getEndDate())) && !foundRate) {
                    if (!ratesUsed.contains(rate)) {
                        ratesUsed.add(rate); //Adding unique room rates
                    }
                    checkInDate = checkInDate.plusDays(1);
                    foundRate = true;
                }
            }
        }
        return ratesUsed;
    }

    private boolean isCurrentDateWithinRange(LocalDate currDate, Date startDate, Date endDate) {
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        boolean lowerBound = currDate.isAfter(start) || currDate.isEqual(start);
        boolean upperBound = currDate.isBefore(end) || currDate.isEqual(end);
        return lowerBound && upperBound;
    }

    private void doWalkInAllocation(LocalDate currDate, Long reservationId, Scanner sc, Long guestId) {
        try {
            System.out.println("==== Walk In Allocating Rooms To Current Day Reservation ====");

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

                //CREATE
                Allocation newAllocation = new Allocation(curr);

                //PERSIST
                Long newAllocationId = allocationSessionBean.createNewAllocation(newAllocation, reservation.getReservationId());

                List<Long> roomList = new ArrayList<>();
                for (Room room : allocatedRooms) {
                    roomList.add(room.getRoomId());
                }
                allocationSessionBean.associateAllocationWithRooms(newAllocationId, roomList);

                newAllocation = allocationSessionBean.getAllocationByAllocationId(newAllocationId);
                System.out.println("Successfully created an Allocation.");
                DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                System.out.println(":: Allocation ID: " + newAllocation.getAllocationId());
                System.out.println("   > Reservation ID: " + newAllocation.getAllocationId());
                System.out.println("   > Current Date: " + outputFormat.format(newAllocation.getCurrentDate()));
                for (Room room : newAllocation.getRooms()) {
                    System.out.println("   > Room ID: " + room.getRoomId());
                }
                System.out.println();
            } else {

                int rankOfRoomType = typeReserved.getTypeRank();

                if (rankOfRoomType == 1) { //This is the highest rank. Confirm cannot allocate a rank higher. Throw typ2 exception
                    //CREATE
                    AllocationException exception = new AllocationException(curr, 2);
                    //ASSOCIATE

                    //PERSIST
                    allocationExceptionSessionBean.createNewAllocationException(exception, reservation.getReservationId());
                    System.out.println("Sorry. Type 2 Allocation Exception occurred.\n");
                    doDashboardFeatures(sc, guestId);
                    return;
                }

                List<Room> allocatedRooms = vacantRooms;

                //Type 1 Exception
                //Allocate all the rooms of the current RoomType into this allocation
                //CREATE
                Allocation newAllocation = new Allocation(curr);

                //Get the remaining rooms from other RoomTypes, while loop
                int numOfRoomsNeedToUpgrade = numOfRoomsToAllocate - vacantRooms.size();
                while (numOfRoomsNeedToUpgrade > 0) {
                    //get a higher rank RoomType
                    rankOfRoomType = rankOfRoomType - 1;

                    if (rankOfRoomType <= 0) {
                        //CREATE
                        AllocationException exception = new AllocationException(curr, 2);
                        //ASSOCIATE
                        allocationExceptionSessionBean.associateAllocationExceptionWithReservation(exception, reservation.getReservationId());
                        //PERSIST
                        allocationExceptionSessionBean.createNewAllocationException(exception);
                        System.out.println("Sorry. Type 2 Allocation Exception occurred.\n");

                        break;
                    }

                    RoomType higherRankedType = roomManagementSessionBean.getRoomTypeByRank(rankOfRoomType);

                    List<Room> higherRankedRooms = higherRankedType.getRooms();
                    List<Room> higherRankedVacantRooms = new ArrayList<>();
                    for (Room room : higherRankedRooms) {
                        if (room.getIsVacant()) {
                            higherRankedVacantRooms.add(room);
                        }
                    }

                    if (higherRankedVacantRooms.size() >= numOfRoomsNeedToUpgrade) {

                        List<Room> higherRankedAllocatedRooms = higherRankedVacantRooms.subList(0, numOfRoomsNeedToUpgrade);

                        for (Room room : higherRankedAllocatedRooms) {

                            allocatedRooms.add(room);
                        }

                        numOfRoomsNeedToUpgrade = 0;
                    } else {

                        if (!higherRankedVacantRooms.isEmpty()) {
                            for (Room room : higherRankedVacantRooms) {

                                allocatedRooms.add(room);
                            }
                        }
                        numOfRoomsNeedToUpgrade -= higherRankedVacantRooms.size();
                    }

                }

                //PERSIST
                Long newAllocationId = allocationSessionBean.createNewAllocation(newAllocation, reservation.getReservationId());

                //ASSOCIATING 
                List<Long> roomList = new ArrayList<>();
                for (Room room : allocatedRooms) {
                    roomList.add(room.getRoomId());
                }
                allocationSessionBean.associateAllocationWithRooms(newAllocationId, roomList);
                newAllocation = allocationSessionBean.getAllocationByAllocationId(newAllocationId);
                System.out.println("Successfully created an Allocation.");
                DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                System.out.println(":: Allocation ID: " + newAllocation.getAllocationId());
                System.out.println("   > Reservation ID: " + newAllocation.getAllocationId());
                System.out.println("   > Current Date:" + outputFormat.format(newAllocation.getCurrentDate()));
                for (Room room : newAllocation.getRooms()) {
                    System.out.println("   > Room Type: " + room.getRoomType().getRoomTypeName() + " Room ID: " + room.getRoomId());
                }
                System.out.println();

                //Create Type 1 Exception
                //CREATE
                AllocationException exception = new AllocationException(curr, 1);
                //PERSIST
                allocationExceptionSessionBean.createNewAllocationException(exception, reservation.getReservationId());

                System.out.println("Type 1 Allocation Exception occurred.\n");

            }

            doDashboardFeatures(sc, guestId);
        } catch (Exception e) {
            System.out.println("Uh oh.. Something went wrong.\n");
            doDashboardFeatures(sc, guestId);
        }
    }

}
