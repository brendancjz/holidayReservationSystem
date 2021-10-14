/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Guest;
import entity.RoomRate;
import entity.RoomType;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import javax.ejb.EJB;

/**
 *
 * @author brend
 */
public class Main {

    @EJB
    private static RoomRateSessionBeanRemote roomRateSessionBean;

    @EJB
    private static RoomTypeSessionBeanRemote roomTypeSessionBean;

    @EJB
    private static GuestSessionBeanRemote guestSessionBeanRemote;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner sc = new Scanner(System.in);
        doMainApp(sc);
        
    }
    
    public static void doMainApp(Scanner sc) {
        
        System.out.println("=== Welcome to HoRS Reservation Client. ===");
        System.out.println("Select an action:");
        System.out.println("> 1. Login");
        System.out.println("> 2. Register as Guest");
        System.out.print("> ");
        int input = sc.nextInt();
        sc.nextLine();
        System.out.println();
        
        if (input == 1) {
            doLogin(sc);
            
        } else if (input == 2) {
            doRegistration(sc);
            
        }
    }
    
    public static void doLogin(Scanner sc) {
        System.out.println("==== Login Interface ====");
            System.out.println("Enter login details:");
            System.out.print("> Email: ");
            String email = sc.nextLine();
            
            if (guestSessionBeanRemote.verifyLoginDetails(email) && 
                    guestSessionBeanRemote.checkGuestExists(email)) {
                
                Guest currGuest = guestSessionBeanRemote.getGuestByEmail(email);
                System.out.println("Welcome " + currGuest.getFirstName() + ", you're in!\n");
                
                doDashboardFeatures(sc, currGuest.getGuestId());
            } else {
                System.out.println("No account match or wrong login details. Try again.\n");
                doLogin(sc);
            }
    }
    
    public static void doDashboardFeatures(Scanner sc, Long guestId) {
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
                doMainApp(sc);
                break;
            default:
                System.out.println("Wrong input. Try again.\n");
                doDashboardFeatures(sc, guestId);
                break;
        }
    }
    
    public static void doViewAllMyReservations(Scanner sc, Long guestId) {
        System.out.println("==== View All My Reservations Interface ====");
    }
    
    public static void doViewMyReservationDetails(Scanner sc, Long guestId) {
        System.out.println("==== View My Reservation Details Interface ====");
    }
    
    public static void doReserveHotelRoom(Scanner sc, Long guestId) {
        System.out.println("==== Reserve Hotel Room Interface ====");
    }
    
    public static void doSearchHotelRoom(Scanner sc, Long guestId) {
        try {
            System.out.println("==== Search Hotel Room Interface ====");
            System.out.println("Please input your check-in and check-out dates. Follow the format 'DD MM YYYY'.");
            System.out.print("> Check-In Date: ");
            String checkIn = sc.nextLine();
            System.out.print("> Check-Out Date: ");
            String checkOut = sc.nextLine();
            System.out.println();

            //Output all the room types, give guest option to select the room he wants to search
            System.out.println("Which Hotel Room would you like to view?");
            List<RoomType> types = roomTypeSessionBean.retrieveAllRoomTypes();
            int count = 1;
            for (RoomType type : types ) {
                System.out.println("> " + count++ + ". " + type.getRoomTypeDesc() + "\n     ** Amenities: " + type.getAmenities());
            }
            System.out.print("> ");
            int input = sc.nextInt();
            sc.nextLine();
            System.out.println();

            RoomType selectedRoomType = types.get(input - 1);

            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate checkInDate = LocalDate.parse(checkIn, dtFormat);
            LocalDate checkOutDate = LocalDate.parse(checkOut, dtFormat);
            long daysBetween = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            System.out.println("You have selected " + selectedRoomType.getRoomTypeDesc() + " for " + daysBetween + " day(s).");
            
            //Search promo rate then peak rate then normal rate, which is from the back of the list
            List<RoomRate> rates = roomTypeSessionBean.getRoomRatesByRoomTypeId(selectedRoomType.getRoomTypeId());

            double totalReservation = 0;
            for (int i = 0; i < daysBetween; i++) {
                //get the rate Per night for each night
                boolean foundRate = false;
                for (int j = rates.size() - 1; j >= 0; j--) {
                    RoomRate rate = rates.get(j);
                    
                    
                    if ((rate.getStartDate() != null)) {
                        System.out.println("Rate is " + rate.getRoomRateName());
                        System.out.println("is date within range? " + isCurrentDateWithinRange(checkInDate, rate.getStartDate(), rate.getEndDate()));
                    }
                    
                    
                    if (((rate.getStartDate() == null) || isCurrentDateWithinRange(checkInDate, rate.getStartDate(), rate.getEndDate())) && 
                            !foundRate ) {
                        
                        totalReservation += rate.getRatePerNight();
                        System.out.println("Total Reservation Fee " + totalReservation);
                        System.out.println("Current date is " + checkInDate.format(dtFormat));
                        checkInDate = checkInDate.plusDays(1);
                        System.out.println("==== " + checkInDate.format(dtFormat));
                        foundRate = true;
                    }
                }
            }
            System.out.println("Total reservation fee is " + totalReservation);
            /*
            RoomRate selectedRate = null;
            for (int i = rates.size() - 1; i >= 0; i--) {
                RoomRate rate = rates.get(i);

                Date startDate = rate.getStartDate();
                Date endDate = rate.getEndDate();
                if (startDate == null && endDate == null && rate.getRoomRateName().equals("NormalRate")) {
                    selectedRate = rate;
                    break;
                } else if (startDate.compareTo(checkInDate) <= 0 && endDate.compareTo(checkOutDate) >= 0) {
                    selectedRate = rate;
                    break;
                }

            }
            

            System.out.println("Your Room Rate is " + 
                    selectedRate.getRoomRateName() + " and total reservation fee is " + selectedRate.getRatePerNight());
            */
            //Check room availability based on the existing reservations and rooms 

            //Out the room types that are available and their rates
        } catch (Exception e) {
            System.out.println("** doSearchHotelRoom throwing error " + e.getMessage());
            doSearchHotelRoom(sc, guestId);
        }
        
    }
    
    private static boolean isCurrentDateWithinRange(LocalDate currDate, Date startDate, Date endDate) {
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        boolean lowerBound = currDate.isAfter(start) || currDate.isEqual(start);
        boolean upperBound = currDate.isBefore(end) || currDate.isEqual(end);
        return lowerBound && upperBound;
    }
    
    public static void doRegistration(Scanner sc) {
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
            
            if (guestSessionBeanRemote.verifyRegisterDetails(firstName, lastName, number, email)) {
                Guest newGuest = new Guest(firstName, lastName, number, email);
                guestSessionBeanRemote.createNewGuest(newGuest);
                System.out.println("Welcome, you're in!");
            } else {
                System.out.println("You have inputted wrong details. Please try again.\n");
                
                doMainApp(sc);
            }
    }
    
    public static void doCancelledRegistration(Scanner sc) {
        System.out.println("\nYou have cancelled registration.\n");
        
        doMainApp(sc);
    }
    
}
