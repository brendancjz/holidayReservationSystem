/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateless.GuestSessionBeanRemote;
import entity.Guest;
import java.util.ArrayList;
import java.util.Scanner;
import javax.ejb.EJB;

/**
 *
 * @author brend
 */
public class Main {

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
                System.out.println("Welcome, you're in!\n");
                
                doDashboardFeatures(sc, guestSessionBeanRemote.getGuestByEmail(email).getGuestId());
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
        System.out.println("==== Search Hotel Room Interface ====");
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
