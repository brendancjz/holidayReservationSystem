/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.AllocationExceptionSessionBeanRemote;
import ejb.session.stateless.AllocationSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import util.exception.AllocationExistException;
import util.exception.EmptyListException;
import util.exception.GuestExistException;
import util.exception.InvalidInputException;

/**
 *
 * @author brend
 */
public class GRelManagerModule {

    private AllocationSessionBeanRemote allocationSessionBean;
    private AllocationExceptionSessionBeanRemote allocationExceptionSessionBean;
    private GuestSessionBeanRemote guestSessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;
    private RoomManagementSessionBeanRemote roomManagementSessionBean;

    public GRelManagerModule(AllocationSessionBeanRemote allocationSessionBean, AllocationExceptionSessionBeanRemote allocationExceptionSessionBean, GuestSessionBeanRemote guestSessionBean, ReservationSessionBeanRemote reservationSessionBean, RoomManagementSessionBeanRemote roomManagementSessionBean) {
        this.allocationSessionBean = allocationSessionBean;
        this.allocationExceptionSessionBean = allocationExceptionSessionBean;
        this.guestSessionBean = guestSessionBean;
        this.reservationSessionBean = reservationSessionBean;
        this.roomManagementSessionBean = roomManagementSessionBean;
    }

    public void doGRelManagerDashboardFeatures(Scanner sc, Long emId) {
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

    private void doWalkInAllocation(LocalDate currDate, Long reservationId, Scanner sc, Long emId) {
        try {
            System.out.println("==== Walk In Allocating Rooms To Current Day Reservations ====");

            Date curr = Date.from(currDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            List<Reservation> reservations = reservationSessionBean.getReservationsToAllocate(currDate);
            if (reservations.isEmpty()) {
                System.out.println("No room to allocate today.\n");
                doGRelManagerDashboardFeatures(sc, emId);
                return;
            }

            for (Reservation reservation : reservations) {
                System.out.println("Allocating for Reservation ID: " + reservation.getReservationId());

                //Update reservation to the latest db
                reservation = reservationSessionBean.getReservationByReservationId(reservation.getReservationId());

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
                    System.out.println("   > Current Date:" + outputFormat.format(newAllocation.getCurrentDate()));
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

                        continue;
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

            }

        
            doGRelManagerDashboardFeatures(sc, emId);
        } catch (Exception e) {
            System.out.println("Invalid input. Try again. " + e.toString());
            doWalkInAllocation(currDate, reservationId, sc, emId);

        }
    }

    private void doCheckInGuest(Scanner sc, Long emId) {
        try {
            System.out.println("==== Check In Guest Interface ====");
            System.out.println("Please input your Guest details:");
            System.out.print("> Guest Email: ");
            String email = sc.nextLine().trim();
            System.out.print("> Current Date [DD MM YYYY]: ");
            String currDay = sc.nextLine().trim();
            System.out.println();

            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate currDate = LocalDate.parse(currDay, dtFormat);

            Guest guest = guestSessionBean.getGuestByEmail(email);
            if (guest == null) {
                throw new GuestExistException("Guest does not exist.\n");
            }

            System.out.println("Hi " + guest.getFirstName() + ", checking you in...");
            Allocation allocation = allocationSessionBean.getAllocationForGuestForCurrentDay(guest.getCustomerId(), currDate);
            if (allocation == null) {
                throw new AllocationExistException("No allocations for this customer.\n");
            }
            System.out.println("Your room(s) are:");
            for (Room room : allocation.getRooms()) {
                System.out.println(":: Room Level: " + room.getRoomLevel());
                System.out.println(":: Room Number: " + room.getRoomNum());
                System.out.println();
            }

            doGRelManagerDashboardFeatures(sc, emId);
        } catch (AllocationExistException | GuestExistException e) {
            System.out.println(e.getMessage());
            doGRelManagerDashboardFeatures(sc, emId);
        } catch (Exception e) {
            System.out.println("Something went wrong.");
            e.printStackTrace();

        }

    }

    private void doCheckOutGuest(Scanner sc, Long emId) {
        try {
            System.out.println("==== Check Out Guest Interface ====");
            System.out.println("Please input your Guest details:");
            System.out.print("> Guest Email: ");
            String email = sc.nextLine().trim();
            System.out.print("> Current Date [DD MM YYYY]: ");
            String currDay = sc.nextLine().trim();
            System.out.println();

            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate currDate = LocalDate.parse(currDay, dtFormat);

            Guest guest = guestSessionBean.getGuestByEmail(email);
            if (guest == null) {
                throw new GuestExistException("Guest does not exist.\n");
            }

            Allocation allocation = allocationSessionBean.getAllocationForGuestForCheckOutDay(guest.getCustomerId(), currDate);
            if (allocation == null) {
                throw new AllocationExistException("No allocations for this customer.\n");
            }
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
            System.out.println();
            doGRelManagerDashboardFeatures(sc, emId);
        } catch (AllocationExistException | GuestExistException e) {
            System.out.println(e.getMessage());
            doCheckOutGuest(sc, emId);
        } catch (Exception e) {
            System.out.println("Something went wrong.");
            e.printStackTrace();

        }

    }

    private void doWalkInSearchRoom(Scanner sc, Long emId) {
        try {
            System.out.println("==== Search Hotel Room Interface ====");
            System.out.println("Please input your check-in and check-out dates. Follow the format 'DD MM YYYY'.");
            System.out.print("> Check-In Date: ");
            String checkIn = sc.nextLine().trim();
            System.out.print("> Check-Out Date: ");
            String checkOut = sc.nextLine().trim();

            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate checkInDate = LocalDate.parse(checkIn, dtFormat);
            LocalDate checkOutDate = LocalDate.parse(checkOut, dtFormat);
            if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
                throw new InvalidInputException("Check out date is before or same as check in date. Try again.\n");
            }

            System.out.println();
            System.out.println("How many number rooms are you looking to reserve?");
            System.out.print("> Number of Rooms: ");
            int numOfRooms = sc.nextInt();
            sc.nextLine();

            long daysBetween = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

            //Output all the room types, give guest option to select the room he wants to search
            System.out.println("Here are all available Room Types for your " + daysBetween + " night(s) stay"
                    + ". Which Hotel Room would you like to reserve?");
            List<RoomType> types = roomManagementSessionBean.getAllRoomTypes();
            int count = 1;

            for (int i = 0; i < types.size(); i++) {
                RoomType type = types.get(i);
                //Check if room type is available first. If available then display
                boolean isRoomTypeAvail = reservationSessionBean.isRoomTypeAvailableForWalkInReservation(type.getRoomTypeId(), numOfRooms);

                if (isRoomTypeAvail) {
                    //Derive the total reservation fee
                    double totalReservation = getTotalReservationFee(checkInDate, checkOutDate, type);

                    System.out.println("> " + count++ + ". " + type.getRoomTypeDesc()
                            + "\n     ** Amenities: " + type.getAmenities()
                            + "\n     ** Total reservation fee is " + totalReservation);
                } else {
                    types.remove(type);
                    i--;
                    System.out.println("Room Type Not Available: " + type.getRoomTypeName());
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
            int reserveInput = sc.nextInt();
            sc.nextLine();
            System.out.println();
            if (reserveInput == 1) {
                Guest newGuest = doRegistration(sc);
                if (newGuest == null) {
                    return;
                }

                Long guestId = guestSessionBean.createNewGuest(newGuest);
                System.out.println("Successfully registered guest.\n");

                doWalkInReserveRoom(sc, emId, guestId, checkInDate, checkOutDate, numOfRooms, selectedRoomType);
            } else {
                System.out.println("Going back to dashboard.\n");
                doGRelManagerDashboardFeatures(sc, emId);
            }
        } catch (EmptyListException e) {
            System.out.println("You have made a wrong input. Try again.\n");
            doWalkInSearchRoom(sc, emId);
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
            doWalkInSearchRoom(sc, emId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doWalkInReserveRoom(Scanner sc, Long emId, Long guestId, LocalDate checkInDate, LocalDate checkOutDate, int numOfRooms, RoomType selectedRoomType) {
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
                doWalkInAllocation(checkInDate, reservation.getReservationId(), sc, emId);
            } else {
                System.out.println("Going back to dashboard.\n");
                doGRelManagerDashboardFeatures(sc, emId);

            }
            
        } catch (EmptyListException ex) {
            System.out.println(ex.getMessage());
            doGRelManagerDashboardFeatures(sc, emId);
        }
    }

    private double getTotalReservationFee(LocalDate checkInDate, LocalDate checkOutDate, RoomType selectedRoomType) {
        double totalReservation = 0;

        try {

            long numOfDays = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            List<RoomRate> rates = roomManagementSessionBean.getRoomRates(selectedRoomType.getRoomTypeId());

            for (int i = 0; i < numOfDays; i++) {
                //get the rate Per night for each night
                boolean foundRate = false;
                for (int j = rates.size() - 1; j >= 0; j--) {
                    RoomRate rate = rates.get(j);
                    if (((rate.getStartDate() == null) || isCurrentDateWithinRange(checkInDate, rate.getStartDate(), rate.getEndDate())) && !foundRate) {
                        totalReservation += rate.getRatePerNight();
                        checkInDate = checkInDate.plusDays(1);
                        foundRate = true;
                    }
                }
            }

        } catch (EmptyListException ex) {
            System.out.println(ex.getMessage());
        }

        return totalReservation;
    }

    public Guest doRegistration(Scanner sc) {
        try {
            System.out.println("==== Register Interface ====");
            System.out.println("Enter guest details. To cancel registration at anytime, press 'q'.");
            System.out.print("> First Name: ");
            String firstName = sc.nextLine().trim();

            System.out.print("> Last Name: ");
            String lastName = sc.nextLine().trim();

            System.out.print("> Email: ");
            String email = sc.nextLine().trim();

            Guest testExist = guestSessionBean.getGuestByEmail(email);
            if (testExist != null) {
                throw new GuestExistException("Contact number already exists. Try another.\n");
            }

            System.out.print("> Contact Number: ");
            String numberInput = sc.nextLine().trim();

            Long number = Long.parseLong(numberInput);

            testExist = guestSessionBean.getGuestByContactNum(number);
            if (testExist != null) {
                throw new GuestExistException("Contact number already exists. Try another.\n");
            }

            Guest newGuest = new Guest(firstName, lastName, number, email);
            return newGuest;

        } catch (GuestExistException e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    public void doCancelledRegistration(Scanner sc) {
        System.out.println("\nYou have cancelled registration.\n");

    }

    private boolean isCurrentDateWithinRange(LocalDate currDate, Date startDate, Date endDate) {
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        boolean lowerBound = currDate.isAfter(start) || currDate.isEqual(start);
        boolean upperBound = currDate.isBefore(end) || currDate.isEqual(end);
        return lowerBound && upperBound;
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
}
