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
import entity.Guest;
import entity.Reservation;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import util.exception.EmptyListException;

/**
 *
 * @author brend
 */
public class MainApp {

    private RoomManagementSessionBeanRemote roomManagementSessionBean;
    private GuestSessionBeanRemote guestSessionBean;
    private PartnerSessionBeanRemote partnerSessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;

    public MainApp(RoomManagementSessionBeanRemote roomManagementSessionBean,
            GuestSessionBeanRemote guestSessionBean,
            PartnerSessionBeanRemote partnerSessionBean,
            ReservationSessionBeanRemote reservationSessionBean) {
        this.roomManagementSessionBean = roomManagementSessionBean;
        this.guestSessionBean = guestSessionBean;
        this.partnerSessionBean = partnerSessionBean;
        this.reservationSessionBean = reservationSessionBean;
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

        if (guestSessionBean.verifyLoginDetails(email)
                && guestSessionBean.checkGuestExists(email)) {

            try {
                Guest currGuest = guestSessionBean.getGuestByEmail(email);
                System.out.println("Welcome " + currGuest.getFirstName() + ", you're in!\n");

                doDashboardFeatures(sc, currGuest.getCustomerId());
            } catch (EmptyListException ex) {
                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("No account match or wrong login details. Try again.\n");
            doLogin(sc);
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
        System.out.println("==== View All My Reservations Interface ====");
        Guest guest = guestSessionBean.getGuestByGuestId(guestId);
        List<Reservation> reservations = guest.getReservations();

        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        System.out.printf("\n%3s%20s%15s%15s%30s", "ID", "Room Type", "No. of Rooms", "Total Fees", "Duration");
        for (Reservation reservation : reservations) {
            System.out.printf("\n%3s%20s%15s%15s%30s", reservation.getReservationId(),
                    reservation.getRoomType().getRoomTypeName(), reservation.getReservationFee(),
                    reservation.getNumOfRooms(), outputFormat.format(reservation.getStartDate())
                    + " -> " + outputFormat.format(reservation.getEndDate()));

        }
        System.out.println();
        System.out.println();
        doDashboardFeatures(sc, guestId);
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
            Reservation reservation = reservationSessionBean.getReservationsByRoomTypeIdAndDuration(selectedType.getRoomTypeId(), checkInDate, checkOutDate);
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
        } catch (Exception e) {
            System.out.println("Invalid input. Please try again.\n");
            System.out.println(e.toString());
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

                    System.out.println("> " + count++ + ". " + type.getRoomTypeDesc()
                            + "\n     ** Amenities: " + type.getAmenities()
                            + "\n     ** Total reservation fee is " + totalReservation);
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
            int reserveInput = sc.nextInt();
            sc.nextLine();
            System.out.println();
            if (reserveInput == 1) {
                doReserveHotelRoom(sc, guestId, checkInDate, checkOutDate, numOfRooms, selectedRoomType);
            } else {
                System.out.println("Going back to dashboard.\n");
                doDashboardFeatures(sc, guestId);
            }

        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("You have made a wrong input. Try again.\n");
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

                //ASSOCIATE THE RESERVATION WITH GUEST AND ROOM TYPE AND ROOM RATES
                reservationSessionBean.associateReservationWithGuestAndRoomTypeAndRoomRates(reservation, guestId, selectedRoomType.getRoomTypeId(), ratesUsed);

                //PERSIST RESERVATION
                Long reservationId = reservationSessionBean.createNewReservation(reservation);

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

            } else {
                System.out.println("Going back to dashboard.\n");

            }
            doDashboardFeatures(sc, guestId);
        } catch (EmptyListException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void doRegistration(Scanner sc) {
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

        if (guestSessionBean.verifyRegisterDetails(firstName, lastName, number, email)) {
            Guest newGuest = new Guest(firstName, lastName, number, email);
            Long guestId = guestSessionBean.createNewGuest(newGuest);
            System.out.println("Welcome, you're in!\n");

            doDashboardFeatures(sc, guestId);
        } else {
            System.out.println("You have inputted wrong details. Please try again.\n");

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

}
