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
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import util.enumeration.EmployeeEnum;
import util.exception.FindEmployeeException;

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
            doGRelManagerDashboardFeatures(sc, emId, emRole);
        }
        
        
    }

 

    private void doGRelManagerDashboardFeatures(Scanner sc, Long emId, String emRole) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            
            List<Reservation> reservations = reservationSessionBean.getReservationsToAllocate(currDate);
            for(Reservation reservation: reservations) {
                System.out.println("Allocating for Reservation ID: " + reservation.getReservationId());
                RoomType typeReserved = reservation.getRoomType();
                
                int numOfRoomsToAllocate = reservation.getNumOfRooms();
                List<Room> rooms = typeReserved.getRooms();
                
                int numOfAvailRooms = 0;
                List<Room> availRooms = new ArrayList<>();
                for (Room room : rooms) {
                    if (room.getIsAvailable()) numOfAvailRooms++;
                    availRooms.add(room);
                }
                
                
                
                if (numOfAvailRooms >= numOfRoomsToAllocate) {
                    System.out.println("> Number of Rooms to allocate: " + numOfRoomsToAllocate);
                    System.out.println("> Number of Rooms available: " + numOfAvailRooms);
                
                    Allocation newAllocation = new Allocation(reservation);
                    newAllocation = allocationSessionBean.getAllocationByAllocationId(allocationSessionBean.createNewAllocation(newAllocation));
                    allocationSessionBean.associateAllocationsWithExistingRooms(newAllocation.getAllocationId(), availRooms);
                    for (int i = 0; i < numOfRoomsToAllocate; i++) {
                        Room room = availRooms.get(i);
                        if (!room.getIsAvailable()) {
                            numOfRoomsToAllocate++;
                        } else {
                            boolean isAvailable = false;
                            roomManagementSessionBean.updateRoom(room.getRoomId(), room.getRoomLevel(), room.getRoomNum(), isAvailable, room.getRoomType());
                        }
                    }
                    
                    System.out.println("Successfully allocated the reservation ID: " + reservation.getReservationId());
                } else {
                    System.out.println("\nUnable to fully allocate reservation.");
                }
                
                
            }
            
            
        } catch (Exception e) {
            System.out.println("Invalid input. Try again. " + e.toString());
            doRoomAllocation();
            
        }
        
        
    }


    

  
}
