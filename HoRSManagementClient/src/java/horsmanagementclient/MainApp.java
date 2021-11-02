/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.AllocationExceptionSessionBeanRemote;
import ejb.session.stateless.AllocationSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomManagementSessionBeanRemote;
import entity.Allocation;
import entity.AllocationException;
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
    private AllocationExceptionSessionBeanRemote allocationExceptionSessionBean;
    
    MainApp(RoomManagementSessionBeanRemote roomManagementSessionBean, EmployeeSessionBeanRemote employeeSessionBean, 
            PartnerSessionBeanRemote partnerSessionBean, GuestSessionBeanRemote guestSessionBean, ReservationSessionBeanRemote reservationSessionBean,
            AllocationSessionBeanRemote allocationSessionBean, AllocationExceptionSessionBeanRemote allocationExceptionSessionBean) {
        this.roomManagementSessionBean = roomManagementSessionBean;
        this.employeeSessionBean = employeeSessionBean;
        this.partnerSessionBean = partnerSessionBean;
        this.guestSessionBean = guestSessionBean;
        this.reservationSessionBean = reservationSessionBean;
        this.allocationSessionBean = allocationSessionBean;
        this.allocationExceptionSessionBean = allocationExceptionSessionBean;
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
            OpsManagerModule module = new OpsManagerModule(roomManagementSessionBean, allocationExceptionSessionBean);
            module.doOpsManagerDashboardFeatures(sc, emId);
            run();
        } else if (emRole.equals(EmployeeEnum.SALESMANAGER.toString())) {
            SalesManagerModule module = new SalesManagerModule(roomManagementSessionBean);
            module.doSalesManagerDashboardFeatures(sc, emId);
            run();
        } else if (emRole.equals(EmployeeEnum.GRELMANAGER.toString())) {
            GRelManagerModule module = new GRelManagerModule(allocationSessionBean, allocationExceptionSessionBean, guestSessionBean, reservationSessionBean, roomManagementSessionBean);
            module.doGRelManagerDashboardFeatures(sc, emId);
            run();
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
            if (reservations.size() == 0 ) {
                System.out.println("No room to allocate today.");
                run();
            }
            
            for(Reservation reservation: reservations) {
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
                    
                    //ASSOCIATE
                    for (Room room : allocatedRooms) {
                        
                        allocationSessionBean.associateAllocationWithRoom(newAllocation, room.getRoomId());
                        
                    }
                    
                    allocationSessionBean.associateAllocationWithReservation(newAllocation, reservation.getReservationId());
                    
                    //PERSIST
                    newAllocation = allocationSessionBean.getAllocationByAllocationId(allocationSessionBean.createNewAllocation(newAllocation));
                    
                    System.out.println("Successfully created an Allocation.");
                    DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                    System.out.println(":: Allocation ID: " + newAllocation.getAllocationId());
                    System.out.println("   > Reservation ID: " + newAllocation.getAllocationId());
                    System.out.println("   > Current Date:" + outputFormat.format(newAllocation.getCurrentDate()));
                    for (Room room : allocatedRooms) {
                        System.out.println("   > Room ID: " + room.getRoomId());
                    }
                    System.out.println();
                } else {
                    
                    
                    int rankOfRoomType = typeReserved.getTypeRank();
                    
                    if (rankOfRoomType == 1) { //This is the highest rank. Confirm cannot allocate a rank higher. Throw typ2 exception
                        //CREATE
                        AllocationException exception = new AllocationException(curr, 2);
                        //ASSOCIATE
                        allocationExceptionSessionBean.associateAllocationExceptionWithReservation(exception, reservation.getReservationId());
                        //PERSIST
                        allocationExceptionSessionBean.createNewAllocationException(exception);
                        System.out.println("Sorry. Type 2 Allocation Exception occurred.\n");
                        
                        continue;
                    } 
                    
                    List<Room> allocatedRooms = vacantRooms;

                    //Type 1 Exception
                    //Allocate all the rooms of the current RoomType into this allocation
                    //CREATE
                    Allocation newAllocation = new Allocation(curr);
                    
                    //ASSOCIATING
                    allocationSessionBean.associateAllocationWithReservation(newAllocation, reservation.getReservationId());
                    for (Room room : allocatedRooms) {
                        allocationSessionBean.associateAllocationWithRoom(newAllocation, room.getRoomId());
                    }
                    
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
                                allocationSessionBean.associateAllocationWithRoom(newAllocation, room.getRoomId());
                                allocatedRooms.add(room);
                            }
                            
                            numOfRoomsNeedToUpgrade = 0;
                        } else {
                            
                            if (!higherRankedVacantRooms.isEmpty()) {
                                for (Room room : higherRankedVacantRooms) {
                                    allocationSessionBean.associateAllocationWithRoom(newAllocation, room.getRoomId());
                                    allocatedRooms.add(room);
                                }
                            }
                            numOfRoomsNeedToUpgrade -= higherRankedVacantRooms.size();
                        }
                        
                    }
                    
                    //PERSIST
                    newAllocation = allocationSessionBean.getAllocationByAllocationId(allocationSessionBean.createNewAllocation(newAllocation));
                    System.out.println("Successfully created an Allocation.");
                    DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                    System.out.println(":: Allocation ID: " + newAllocation.getAllocationId());
                    System.out.println("   > Reservation ID: " + newAllocation.getAllocationId());
                    System.out.println("   > Current Date:" + outputFormat.format(newAllocation.getCurrentDate()));
                    for (Room room : allocatedRooms) {
                        System.out.println("   > Room Type: " + room.getRoomType().getRoomTypeName() + " Room ID: " + room.getRoomId());
                    }
                    System.out.println();
                    
                    //Create Type 1 Exception
                    //CREATE
                    AllocationException exception = new AllocationException(curr, 1);
                    //ASSOCIATE
                    allocationExceptionSessionBean.associateAllocationExceptionWithReservation(exception, reservation.getReservationId());
                    //PERSIST
                    allocationExceptionSessionBean.createNewAllocationException(exception);

                    System.out.println("Type 1 Allocation Exception occurred.\n");

                }
                
                
            }
            
            run();
            
        } catch (Exception e) {
            System.out.println("Invalid input. Try again. " + e.toString());
            doRoomAllocation();
            
        }
        
        
    }
    
    
    
    
}
