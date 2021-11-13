/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.AllocationExceptionSessionBeanRemote;
import ejb.session.stateless.AllocationSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomManagementSessionBeanRemote;
import entity.Allocation;
import entity.AllocationException;
import entity.Employee;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import javax.ejb.EJBTransactionRolledbackException;
import util.enumeration.EmployeeEnum;
import util.exception.EmployeeExistException;
import util.exception.EmptyListException;
import util.exception.PartnerExistException;

/**
 *
 * @author brend
 */
public class SystemAdminModule {

    private PartnerSessionBeanRemote partnerSessionBean;
    private EmployeeSessionBeanRemote employeeSessionBean;
    private AllocationSessionBeanRemote allocationSessionBean;
    private ReservationSessionBeanRemote reservationSessionBean;
    private AllocationExceptionSessionBeanRemote allocationExceptionSessionBean;
    private RoomManagementSessionBeanRemote roomManagementSessionBean;

    SystemAdminModule(PartnerSessionBeanRemote partnerSessionBean, EmployeeSessionBeanRemote employeeSessionBean,
            AllocationSessionBeanRemote allocationSessionBean, AllocationExceptionSessionBeanRemote allocationExceptionSessionBean,
            ReservationSessionBeanRemote reservationSessionBean, RoomManagementSessionBeanRemote roomManagementSessionBean) {
        this.partnerSessionBean = partnerSessionBean;
        this.employeeSessionBean = employeeSessionBean;
        this.allocationExceptionSessionBean = allocationExceptionSessionBean;
        this.allocationSessionBean = allocationSessionBean;
        this.reservationSessionBean = reservationSessionBean;
        this.roomManagementSessionBean = roomManagementSessionBean;
    }

    public void doSystemAdminDashboardFeatures(Scanner sc) {

        System.out.println("==== System Admin Dashboard Interface ====");
        System.out.println("> 1. Create New Employee");
        System.out.println("> 2. View All Employees");
        System.out.println("> 3. Create New Partner");
        System.out.println("> 4. View All Partners");
        System.out.println("> 5. Allocate Rooms to Current Day");
        System.out.println("> 6. Logout");
        System.out.print("> ");
        int input = sc.nextInt();
        sc.nextLine();

        switch (input) {
            case 1:
                System.out.println("You have selected 'Create New Employee'\n");
                doCreateNewEmployee(sc);
                break;
            case 2:
                System.out.println("You have selected 'View All Employees'\n");
                doViewAllEmployees(sc);
                break;
            case 3:
                System.out.println("You have selected 'Create New Partner'\n");
                doCreateNewPartner(sc);
                break;
            case 4:
                System.out.println("You have selected 'View All Partners'\n");
                doViewAllPartners(sc);
                break;
            case 5:
                doRoomAllocation(sc);
                break;
            case 6:
                System.out.println("You have logged out.\n");
                break;
            default:
                System.out.println("Wrong input. Try again.\n");
                doSystemAdminDashboardFeatures(sc);
                break;
        }
    }

    private void doCreateNewEmployee(Scanner sc) {
        try {
            System.out.println("==== Create New Employee Interface ====");
            System.out.println("Please input the following details. To cancel creation at anytime, enter 'q'.");
            System.out.print("> First Name: ");
            String firstName = sc.nextLine().trim();
            if (firstName.equals("q")) {
                doCancelledEntry(sc);
                return;
            }
            System.out.print("> Last Name: ");
            String lastName = sc.nextLine().trim();
            if (lastName.equals("q")) {
                doCancelledEntry(sc);
                return;
            }
            System.out.print("> Username: ");
            String username = sc.nextLine().trim();
            if (username.equals("q")) {
                doCancelledEntry(sc);
                return;
            }

            Employee testExists = employeeSessionBean.getEmployeeByUsername(username);
            if (testExists != null) {
                throw new EmployeeExistException("Username already exist. Try another.\n");
            }

            System.out.print("> Password: ");
            String password = sc.nextLine();
            if (password.equals("q")) {
                doCancelledEntry(sc);
                return;
            }
            System.out.println("> Employee Role:\n   > 1. System Administrator"
                    + "\n   > 2. Operation Manager\n   > 3. Sales Manager"
                    + "\n   > 4. Guest Relation Manager");
            System.out.print("> ");
            String inputR = sc.next();
            sc.nextLine();
            if (inputR.equals("q")) {
                doCancelledEntry(sc);
                return;
            }
            int inputRole = Integer.parseInt(inputR);
            System.out.println();

            String role;

            switch (inputRole) {
                case 1:
                    role = EmployeeEnum.SYSTEMADMIN.toString();
                    break;
                case 2:
                    role = EmployeeEnum.OPSMANAGER.toString();
                    break;
                case 3:
                    role = EmployeeEnum.SALESMANAGER.toString();
                    break;
                case 4:
                    role = EmployeeEnum.GRELMANAGER.toString();
                    break;
                default:
                    System.out.println("Invalid role input. Try again.");
                    doCreateNewEmployee(sc);
                    return; //code ends
            }

            Employee newEmployee = new Employee(firstName, lastName, username, role, password);
            newEmployee = employeeSessionBean.getEmployeeById(employeeSessionBean.createNewEmployee(newEmployee));

            System.out.println("You have successfully created a new Employee.");
            System.out.println("Employee Details:");
            System.out.println("   > Employee ID: " + newEmployee.getEmployeeId());
            System.out.println("   > First Name: " + newEmployee.getFirstName());
            System.out.println("   > Last Name: " + newEmployee.getLastName());
            System.out.println("   > Employee Role: " + newEmployee.getEmployeeRole());
            System.out.println("   > Password: " + newEmployee.getPassword() + "\n");

            doSystemAdminDashboardFeatures(sc);

        } catch (EJBTransactionRolledbackException e) {
            System.out.println("Sorry. You have inputted values that do not match the requirements. Try again.\n");
            doSystemAdminDashboardFeatures(sc);
        } catch (EmployeeExistException e) {
            System.out.println(e.getMessage());
            doCreateNewEmployee(sc);
        } catch (Exception e) {
            System.out.println("Invalid input. Try again.");
            doCreateNewEmployee(sc);
        }
    }

    private void doViewAllEmployees(Scanner sc) {
        System.out.println("==== View Al Employees Interface ====");
        try {
            List<Employee> list = employeeSessionBean.retrieveAllEmployees();

            System.out.printf("\n%3s%15s%15s%25s", "ID", "First Name", "Last Name", "Employee Role");
            for (Employee em : list) {
                System.out.printf("\n%3s%15s%15s%25s", em.getEmployeeId(),
                        em.getFirstName(), em.getLastName(), em.getEmployeeRole());
            }
            System.out.println();
            System.out.println();

        } catch (EmptyListException e) {
            System.out.println("Error: " + e.getMessage());

        } catch (Exception e) {
            System.out.println("Uh oh.. Something went wrong.\n");
            
        }
        doSystemAdminDashboardFeatures(sc);
    }

    private void doCreateNewPartner(Scanner sc) {
        try {
            System.out.println("==== Create New Partner Interface ====");
            System.out.println("Enter partner details. To cancel creation at anytime, enter 'q'.");
            System.out.print("> First Name: ");
            String firstName = sc.nextLine().trim();
            if (firstName.equals("q")) {
                doCancelledEntry(sc);
                return;
            }
            System.out.print("> Last Name: ");
            String lastName = sc.nextLine().trim();
            if (lastName.equals("q")) {
                doCancelledEntry(sc);
                return;
            }
            System.out.print("> Email: ");
            String email = sc.nextLine().trim();
            if (email.equals("q")) {
                doCancelledEntry(sc);
                return;
            }

            Partner testExist = partnerSessionBean.getPartnerByEmail(email);
            if (testExist != null) {
                throw new PartnerExistException("Email already exists. Try another.\n");
            }

            System.out.print("> Contact Number: ");
            String numberInput = sc.nextLine().trim();
            if (numberInput.equals("q")) {
                doCancelledEntry(sc);
                return;
            }
            Long number = Long.parseLong(numberInput);
            testExist = partnerSessionBean.getPartnerByContactNum(number);
            if (testExist != null) {
                throw new PartnerExistException("Contact number already exists. Try another.\n");
            }

            if (partnerSessionBean.verifyRegisterDetails(firstName, lastName, number, email)) {
                Partner newPartner = new Partner(firstName, lastName, number, email);
                Long partnerId = partnerSessionBean.createNewPartner(newPartner);
                System.out.println("You have successfully created a new partner.\n");

                newPartner = partnerSessionBean.getPartnerByPartnerId(partnerId);
                System.out.println(":: Partner ID: " + newPartner.getCustomerId());
                System.out.println("   > Name: " + newPartner.getFirstName() + " " + newPartner.getLastName());
                System.out.println("   > Email: " + newPartner.getEmail());
                System.out.println("   > Contact Number: " + newPartner.getContactNumber());
                System.out.println();
                doSystemAdminDashboardFeatures(sc);
            } else {
                System.out.println("You have inputted wrong details. Please try again.\n");

                doCreateNewPartner(sc);
            }
        } catch (PartnerExistException e) {
            System.out.println(e.getMessage());
            doCreateNewPartner(sc);
        } catch (Exception e) {
            System.out.println("Uh oh.. Something went wrong.\n");
            doCreateNewPartner(sc);
        }

    }

    private void doCancelledEntry(Scanner sc) {
        System.out.println("\n You have cancelled entry. Taking you back to dashboard.\n");

        doSystemAdminDashboardFeatures(sc);
    }

    private void doViewAllPartners(Scanner sc) {
        try {
            System.out.println("==== View All Partners Interface ====");
            List<Partner> partners = partnerSessionBean.retrieveAllPartners();
            System.out.printf("\n%3s%15s%15s%20s%30s", "ID", "First Name", "Last Name", "Contact Number", "Email");
            for (Partner partner : partners) {
                System.out.printf("\n%3s%15s%15s%20s%30s", partner.getCustomerId(),
                        partner.getFirstName(), partner.getLastName(),
                        partner.getContactNumber(), partner.getEmail());
            }
            System.out.println();
            System.out.println();
            doSystemAdminDashboardFeatures(sc);
        } catch (EmptyListException ex) {
            System.out.println(ex.getMessage());
            doSystemAdminDashboardFeatures(sc);
        } catch (Exception e) {
            System.out.println("Uh oh.. Something went wrong.\n");
            doSystemAdminDashboardFeatures(sc);
        }
    }

    private void doRoomAllocation(Scanner sc) {
        try {
            System.out.println("==== Allocating Rooms To Current Day Reservations ====");

            System.out.println("Input Current Day [DD MM YYYY]:");
            System.out.print("> ");
            String currDay = sc.nextLine();

            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDate currDate = LocalDate.parse(currDay, dtFormat);
            Date curr = Date.from(currDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            List<Reservation> reservations = reservationSessionBean.getReservationsToAllocate(currDate);
            if (reservations.isEmpty()) {
                System.out.println("No room to allocate today.\n");
                doSystemAdminDashboardFeatures(sc);
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
                    if (room.getIsVacant() && !room.getIsDisabled() && room.getIsAvailable()) {
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
                            
                            //PERSIST
                            allocationExceptionSessionBean.createNewAllocationException(exception, reservation.getReservationId());
                            System.out.println("Sorry. Type 2 Allocation Exception occurred.\n");
                            continue;
                        }

                        RoomType higherRankedType = roomManagementSessionBean.getRoomTypeByRank(rankOfRoomType);

                        List<Room> higherRankedRooms = higherRankedType.getRooms();
                        List<Room> higherRankedVacantRooms = new ArrayList<>();
                        for (Room room : higherRankedRooms) {
                            if (room.getIsVacant() && !room.getIsDisabled() && room.getIsAvailable()) {
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

            doSystemAdminDashboardFeatures(sc);

        } catch (Exception e) {
            System.out.println("Uh oh.. Something went wrong.\n");
            doRoomAllocation(sc);

        }

    }
}
