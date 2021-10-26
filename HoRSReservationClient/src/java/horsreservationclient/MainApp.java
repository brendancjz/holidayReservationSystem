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
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.exception.FindRoomTypeException;
import util.exception.ReservationQueryException;
import util.exception.RoomTypeQueryException;

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
            
            if (guestSessionBean.verifyLoginDetails(email) && 
                    guestSessionBean.checkGuestExists(email)) {
                
                Guest currGuest = guestSessionBean.getGuestByEmail(email);
                System.out.println("Welcome " + currGuest.getFirstName() + ", you're in!\n");
                
                doDashboardFeatures(sc, currGuest.getCustomerId());
            } else {
                System.out.println("No account match or wrong login details. Try again.\n");
                doLogin(sc);
            }
    }
    
    public void doDashboardFeatures(Scanner sc, Long guestId) {
        System.out.println("==== Dashboard Interface ====");
        System.out.println("> 1. Search Hotel Room");
        System.out.println("> 2. Reserve Hotel Room");
        System.out.println("> 3. View My Reservation Details");
        System.out.println("> 4. View All My Reservations");
        System.out.println("> 5. Logout");
        System.out.print("> ");
        int input = sc.nextInt();
        sc.nextLine();
        
        switch (input) {
            case 1:
                System.out.println("You have selected 'Search Hotel Room'\n");
                doSearchHotelRoom(sc, guestId);
                break;
            case 2:
                System.out.println("You have selected 'Reserve Hotel Room'\n");
                doReserveHotelRoom(sc, guestId);
                break;
            case 3:
                System.out.println("You have selected 'View My Reservation Details'\n");
                doViewMyReservationDetails(sc, guestId);
                break;
            case 4:
                System.out.println("You have selected 'View All My Reservations'\n");
                doViewAllMyReservations(sc, guestId);
                break;
            case 5:
                System.out.println("You have logged out.\n");
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
        System.out.printf("\n%3s%20s%20s%12s%30s", "ID", "Room Rate", "Room Type", "No. of Rooms", "Duration");
        for (Reservation reservation : reservations) {
            System.out.printf("\n%3s%20s%20s%20s%15s%30s", reservation.getReservationId(),
                    reservation.getRoomRate().getRoomRateName(), reservation.getRoomType().getRoomTypeName(),
                    reservation.getNumOfRooms(), outputFormat.format(reservation.getStartDate()) + 
                        " -> " + outputFormat.format(reservation.getEndDate()));
        
        }
        System.out.println(); System.out.println();
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
            int typeInput = sc.nextInt(); sc.nextLine();
            
            RoomType selectedType = types.get(typeInput - 1);
            System.out.println();
            Reservation reservation = reservationSessionBean.getReservationsByRoomTypeIdAndDuration(selectedType.getRoomTypeId(), checkInDate, checkOutDate);
            DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            String duration = outputFormat.format(reservation.getStartDate()) + 
                        " -> " + outputFormat.format(reservation.getEndDate());
            System.out.println("Selected Reservation details:");
            System.out.println(":: Reservation ID: " + reservation.getReservationId());
            System.out.println("   > Room Rate: " + reservation.getRoomRate().getRoomRateName());
            System.out.println("   > Room Type: " + reservation.getRoomType().getRoomTypeName());
            System.out.println("   > Num of Rooms: " + reservation.getNumOfRooms());
            System.out.println("   > Duration: " + duration);
            
            doDashboardFeatures(sc, guestId);
        } catch (RoomTypeQueryException | ReservationQueryException ex) {
            System.out.println("Error: " + ex.getMessage());
            System.out.println();
            doViewMyReservationDetails(sc, guestId);
        } catch (Exception e) {
            System.out.println("Invalid input. Please try again.\n");
            doViewMyReservationDetails(sc, guestId);
        }
    }
    
    public void doReserveHotelRoom(Scanner sc, Long guestId) {
        System.out.println("==== Reserve Hotel Room Interface ====");
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
             
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate checkInDate = LocalDate.parse(checkIn, dtFormat);
            LocalDate checkOutDate = LocalDate.parse(checkOut, dtFormat);
            long daysBetween = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            
            //Output all the room types, give guest option to select the room he wants to search
            System.out.println("Here are all available Room Types for your " + daysBetween + " night(s) stay"
                    + ". Which Hotel Room would you like to reserve?");
            List<RoomType> types = roomManagementSessionBean.getAllRoomTypes();
            int count = 1;
            for (RoomType type : types ) {
                //Check if room type is available first. If available then display
                boolean isRoomTypeAvail = true;
                try {
                    isRoomTypeAvail = reservationSessionBean.isRoomTypeAvailableForReservation(type.getRoomTypeId(), checkInDate, checkOutDate);
                } catch (ReservationQueryException e) {
                    //isRoomTypeAvail = false;
                }
                if (isRoomTypeAvail) {
                    //Derive the total reservation fee
                    double totalReservation = getTotalReservationFee(checkInDate, checkOutDate, type);
                
                    System.out.println("> " + count++ + ". " + type.getRoomTypeDesc() + 
                        "\n     ** Amenities: " + type.getAmenities() +
                        "\n     ** Total reservation fee is " + totalReservation);
                }
                
            }
            System.out.print("> ");
            int input = sc.nextInt();
            sc.nextLine();
            System.out.println(); 

            RoomType selectedRoomType = types.get(input - 1);
 
        } catch (FindRoomTypeException | RoomTypeQueryException e) {
            System.out.println("Error occured.");
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("You have made a wrong input. Try again.\n");
            doSearchHotelRoom(sc, guestId);
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
    
    private boolean isCurrentDateWithinRange(LocalDate currDate, Date startDate, Date endDate) {
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        boolean lowerBound = currDate.isAfter(start) || currDate.isEqual(start);
        boolean upperBound = currDate.isBefore(end) || currDate.isEqual(end);
        return lowerBound && upperBound;
    }
    
}
