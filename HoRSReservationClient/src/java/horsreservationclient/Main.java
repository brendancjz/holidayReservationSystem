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
        System.out.println("Welcome to HoRS Reservation Client\nSelect an action:\n> 1. Login\n> 2. Register as Guest");
        System.out.print("> Input: ");
        int input = sc.nextInt();
        sc.nextLine();
        
        if (input == 1) {
            System.out.println("==== Login Interface ====");
            System.out.println("Enter login details:");
            System.out.print("> Email: ");
            String email = sc.nextLine();
            System.out.print("> Contact Number: ");
            long number = Long.parseLong(sc.nextLine());
            
            if (guestSessionBeanRemote.verifyLoginDetails(email, number) && 
                    guestSessionBeanRemote.checkGuestExists(email, number)) {
                System.out.println("Welcome, you're in!");
            } else {
                System.out.println("No account match or wrong login details. Run again.");
            }
        } else if (input == 2) {
            System.out.println("==== Register Interface ====");
            System.out.println("Enter guest details:");
            System.out.print("> First Name: ");
            String firstName = sc.nextLine();
            System.out.print("> Last Name: ");
            String lastName = sc.nextLine();
            System.out.print("> Email: ");
            String email = sc.nextLine();
            System.out.print("> Contact Number: ");
            long number = Long.parseLong(sc.nextLine());
            
            
            if (guestSessionBeanRemote.verifyRegisterDetails(firstName, lastName, email, number)) {
                Guest newGuest = new Guest(firstName, lastName, number, email, new ArrayList<>());
                guestSessionBeanRemote.createNewGuest(newGuest);
                System.out.println("Welcome, you're in!");
            }
            
        }
    }
    
}
