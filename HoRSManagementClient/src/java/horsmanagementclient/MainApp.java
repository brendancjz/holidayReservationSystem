/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.RoomManagementSessionBeanRemote;
import entity.Employee;
import entity.RoomRate;
import entity.RoomType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.EmployeeEnum;
import util.enumeration.RoomRateEnum;
import util.exception.EmployeeQueryException;
import util.exception.FindEmployeeException;
import util.exception.FindRoomRateException;
import util.exception.FindRoomTypeException;
import util.exception.ReservationQueryException;
import util.exception.RoomRateQueryException;
import util.exception.RoomTypeQueryException;

/**
 *
 * @author brend
 */
public class MainApp {
    
    private RoomManagementSessionBeanRemote roomManagementSessionBean;
    private EmployeeSessionBeanRemote employeeSessionBean;
    
    MainApp(RoomManagementSessionBeanRemote roomManagementSessionBean, EmployeeSessionBeanRemote employeeSessionBean) {
        this.roomManagementSessionBean = roomManagementSessionBean;
        this.employeeSessionBean = employeeSessionBean;
    }
    
    public void run() {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("=== Welcome to HoRS Management Client. ===");
        System.out.println("Select an action:");
        System.out.println("> 1. Login");
        System.out.println("> 2. Exit");
        System.out.print("> ");
        int input = sc.nextInt();
        sc.nextLine();
        System.out.println();
        
        switch (input) {
            case 1:
                doLogin(sc);
                sc.close();
                break;
            case 2:
                doExit();
                sc.close();
                break;
            default:
                sc.close();
                run();
                break;
        }
    }

    private void doExit() {
        System.out.println("You have exited. Goodbye.");
    }

    private void doLogin(Scanner sc) {
        System.out.println("==== Login Interface ====");
            System.out.println("Enter login details:");
            System.out.print("> Employee ID: ");
            Long emId = sc.nextLong();
            sc.nextLine();
            System.out.print("> Password: ");
            String password = sc.nextLine();
            try {
                if (employeeSessionBean.verifyLoginDetails(emId, password) && 
                        employeeSessionBean.checkEmployeeExists(emId, password)) {

                    Employee currEm = employeeSessionBean.getEmployeeById(emId);
                    System.out.println("Welcome " + currEm.getEmployeeRole() + " " + currEm.getFirstName() + "\n");

                    doDashboardFeatures(sc, currEm.getEmployeeId(), currEm.getEmployeeRole());
                } else {
                    System.out.println("No account match or wrong login details. Try again.\n");
                    doLogin(sc);
                }
            } catch (FindEmployeeException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e ) {
                System.out.println("doLogin throwing error: " + e.getMessage());
            }
            
    }
    
    private void doDashboardFeatures(Scanner sc, Long emId, String emRole) {
        System.out.println("==== " + emRole + " Dashboard Interface ====");
        
        if (emRole.equals(EmployeeEnum.SYSTEMADMIN.toString())) {
            doSystemAdminDashboardFeatures(sc, emId, emRole);
        } else if (emRole.equals(EmployeeEnum.OPSMANAGER.toString())) {
            doOpsManagerDashboardFeatures(sc, emId, emRole);
        } else if (emRole.equals(EmployeeEnum.SALESMANAGER.toString())) {
            doSalesManagerDashboardFeatures(sc, emId, emRole);
        } else if (emRole.equals(EmployeeEnum.GRELMANAGER.toString())) {
            doGRelManagerDashboardFeatures(sc, emId, emRole);
        }

    }

    private void doSystemAdminDashboardFeatures(Scanner sc, Long emId, String emRole) {
        System.out.println("> 1. Create New Employee");
        System.out.println("> 2. View All Employees");
        System.out.println("> 3. Create New Partner");
        System.out.println("> 4. View All Partners");
        System.out.println("> 5. Logout");
        System.out.print("> ");
        int input = sc.nextInt();
        sc.nextLine();
        
        switch (input) {
            case 1:
                System.out.println("You have selected 'Create New Employee'\n");
                doCreateNewEmployee(sc, emId, emRole);
                break;
            case 2:
                System.out.println("You have selected 'View All Employees'\n");
                doViewAllEmployees(sc, emId, emRole);
                break;
            case 3:
                System.out.println("You have selected 'Create New Partner'\n");
                //doViewMyReservationDetails(sc, guestId);
                break;
            case 4:
                System.out.println("You have selected 'View All Partners'\n");
                //doViewAllMyReservations(sc, guestId);
                break;
            case 5:
                System.out.println("You have logged out.\n");
                sc.close();
                run();
                break;
            default:
                System.out.println("Wrong input. Try again.\n");
                doDashboardFeatures(sc, emId, emRole);
                break;
        }
    }

    private void doSalesManagerDashboardFeatures(Scanner sc, Long emId, String emRole) {
        System.out.println("> 1. Create New Room Rate");
        System.out.println("> 2. View Room Rate Details");
        System.out.println("> 3. View All Room Rates");
        System.out.println("> 4. Logout");
        System.out.print("> ");
        int input = sc.nextInt();
        sc.nextLine();
        
        switch (input) {
            case 1:
                System.out.println("You have selected 'Create New Room Rate'\n");
                doCreateNewRoomRate(sc, emId, emRole);
                break;
            case 2:
                System.out.println("You have selected 'View Room Rate Details'\n");
                doViewRoomRateDetails(sc, emId, emRole);
                break;
            case 3:
                System.out.println("You have selected 'View All Room Rates'\n");
                doViewAllRoomRates(sc, emId, emRole);
                break;
            case 4:
                System.out.println("You have logged out.\n");
                sc.close();
                run();
                break;
            default:
                System.out.println("Wrong input. Try again.\n");
                doDashboardFeatures(sc, emId, emRole);
                break;
        }
    }

    private void doGRelManagerDashboardFeatures(Scanner sc, Long emId, String emRole) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doOpsManagerDashboardFeatures(Scanner sc, Long emId, String emRole) {
        System.out.println("> 1. Create New Room Type");
        System.out.println("> 2. View Room Type Details");
        System.out.println("> 3. View All Room Types");
        System.out.println("> 4. Create New Room");
        System.out.println("> 5. Update Room");
        System.out.println("> 6. Delete Room");
        System.out.println("> 7. View All Rooms");
        System.out.println("> 8. View Room Allocation Exception Report");
        System.out.println("> 9. Logout");
        System.out.print("> ");
        int input = sc.nextInt();
        sc.nextLine();
        
        switch (input) {
            case 1:
                System.out.println("You have selected 'Create New Room Type'\n");
                doCreateNewRoomType(sc, emId, emRole);
                break;
            case 2:
                System.out.println("You have selected 'View Room Type Details'\n");
                doViewRoomTypeDeatails(sc, emId, emRole);
                break;
            case 3:
                System.out.println("You have selected 'View All Room Types'\n");
                doViewAllRoomTypes(sc, emId, emRole);
                break;
            case 4:
                System.out.println("You have selected 'Create New Room'\n");
                //doViewAllMyReservations(sc, guestId);
                break;
            case 5:
                System.out.println("You have selected 'Update Room'\n");
                //doViewAllMyReservations(sc, guestId);
                break;
            case 6:
                System.out.println("You have selected 'Delete Room'\n");
                //doViewAllMyReservations(sc, guestId);
                break;
            case 7:
                System.out.println("You have selected 'View All Rooms'\n");
                //doViewAllMyReservations(sc, guestId);
                break;
            case 8:
                System.out.println("You have selected 'View Room Allocation Exception Report'\n");
                //doViewAllMyReservations(sc, guestId);
                break;
            case 9:
                System.out.println("You have logged out.\n");
                sc.close();
                run();
                break;
            default:
                System.out.println("Wrong input. Try again.\n");
                doDashboardFeatures(sc, emId, emRole);
                break;
        }
    }

    private void doCreateNewEmployee(Scanner sc, Long emId, String emRole) {
        try {
            System.out.println("==== Create New Employee Interface ====");
            System.out.println("Please input the following details.");
            System.out.print("> First Name: ");
            String firstName = sc.nextLine();
            System.out.print("> Last Name: ");
            String lastName = sc.nextLine();
            System.out.print("> Password: ");
            String password = sc.nextLine();
            System.out.println("> Employee Role:\n   > 1. System Administrator"
                    + "\n   > 2. Operation Manager\n   > 3. Sales Manager"
                    + "\n   > 4. Guest Relation Manager");
            System.out.print("> ");
            int inputRole = sc.nextInt(); sc.nextLine();
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
                    System.out.println("Invalid role input.");
                    doCreateNewEmployee(sc, emId, emRole);
                    return; //code ends
            }
            
            Employee newEmployee = new Employee(firstName, lastName, role, password);
            newEmployee = employeeSessionBean.getEmployeeById(employeeSessionBean.createNewEmployee(newEmployee));
            
            System.out.println("You have successfully created a new Employee.");
            System.out.println("Employee Details:");
            System.out.println("   > Employee ID: " + newEmployee.getEmployeeId());
            System.out.println("   > First Name: " + newEmployee.getFirstName());
            System.out.println("   > Last Name: " + newEmployee.getLastName());
            System.out.println("   > Employee Role: " + newEmployee.getEmployeeRole());
            System.out.println("   > Password: " + newEmployee.getPassword() + "\n");
            
            doDashboardFeatures(sc, emId, emRole);
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void doViewAllEmployees(Scanner sc, Long emId, String emRole) {
        System.out.println("==== View Al Employees Interface ====");
        try {
            List<Employee> list = employeeSessionBean.retrieveAllEmployees();
            int count = 0;
            
            for (Employee em : list) {
                System.out.println(":: Employee ID: " + em.getEmployeeId());
                System.out.println("     > Name: " + em.getFirstName() + " " + em.getLastName());
                System.out.println("     > Role: " + em.getEmployeeRole() + "\n");
                count++;
            }
            System.out.println("Total Employees: " + count + "\n");

            doDashboardFeatures(sc, emId, emRole);
        } catch (EmployeeQueryException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
    }

    private void doCreateNewRoomRate(Scanner sc, Long emId, String emRole) {
        try {
            System.out.println("==== Create New Room Rate Interface ====");
            List<RoomType> types = roomManagementSessionBean.getAllRoomTypes();
            System.out.println("Select Room Type to have the new Room Rate:");
            int idx = 1;
            for (RoomType type : types ) {
                System.out.println("> " + idx++ + ". " + type.getRoomTypeName());
            }
            System.out.print("> ");
            int typeInput = sc.nextInt(); sc.nextLine();
            System.out.println("** You have selected: " + types.get(typeInput - 1).getRoomTypeName() + "\n");
            System.out.println("Select Room Rate Type:");
            String[] rateEnums = new String[] {RoomRateEnum.PublishedRate.toString(), 
                                                    RoomRateEnum.NormalRate.toString(), 
                                                    RoomRateEnum.PeakRate.toString(), 
                                                    RoomRateEnum.PromotionRate.toString()};
            for (int i = 0; i < rateEnums.length; i++) {
                System.out.println("> " + (i+1) + ". " + rateEnums[i]);
            }
            System.out.print("> ");
            int rateInput = sc.nextInt(); sc.nextLine();
            System.out.println("** You have selected: " + rateEnums[rateInput - 1] + "\n");
            
            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
            LocalDateTime startDate = null;
            LocalDateTime endDate = null;
            
            if (rateInput == 3 || rateInput == 4) {
                System.out.println("Input validity period of selected room rate:");
                System.out.print("> Start Date [DD MM YYYY]: ");
                String start = sc.nextLine();
                startDate = LocalDate.parse(start, dtFormat).atStartOfDay();
                System.out.print("> End Date [DD MM YYYY]: ");
                String end = sc.nextLine();
                endDate = LocalDate.parse(end, dtFormat).atStartOfDay();
                
                System.out.println("** You have selected the period of " + ChronoUnit.DAYS.between(startDate, endDate) +
                        " day(s): " + start + " -> " + end + "\n");
            }
            
            
            System.out.print("> Rate Per Night: ");
            double rateAmount = sc.nextDouble(); sc.nextLine();
            System.out.println("** You have selected: $" + rateAmount + "\n");
            
            RoomRate rate = roomManagementSessionBean.createNewRoomRate(types.get(typeInput - 1).getRoomTypeId(), rateEnums[rateInput - 1], startDate, endDate, rateAmount);
            System.out.println("You have successfully created a new Room Rate.");
            System.out.println("> Name: " + rate.getRoomRateName());
            System.out.println("> Type: " + rate.getRoomRateType());
            System.out.println("> Amount: " + rate.getRatePerNight());
            if (rate.getStartDate() != null) System.out.println("> Validity Period: " + rate.getStartDate().toString() + 
                    " -> " + rate.getEndDate().toString());
            System.out.println();
            
            doDashboardFeatures(sc, emId, emRole);
        } catch (RoomTypeQueryException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input. Try again.\n");
            doCreateNewRoomRate(sc, emId, emRole);
        }
    }

    private void doViewRoomRateDetails(Scanner sc, Long emId, String emRole) {
        try {
            System.out.println("==== View Room Rate Details Interface ====");
            System.out.print("> Room Rate Name: ");
            String rateName = sc.nextLine();
            RoomRate rate = roomManagementSessionBean.getRoomRate(rateName);
            
            System.out.println("Selected Room Rate details:");
            System.out.println("> Name: " + rate.getRoomRateName());
            System.out.println("> Type: " + rate.getRoomRateType());
            System.out.println("> Amount: " + rate.getRatePerNight());
            System.out.println("> Is Disabled: " + rate.getIsDisabled());
            if (rate.getStartDate() != null) {
                System.out.println("> Validity Period: " + rate.getStartDate().toString() + 
                    " -> " + rate.getEndDate().toString());
            } else {
                System.out.println("> Validity Period: NULL");
            }
            System.out.println();
            
            System.out.println("   Select an action:");
            System.out.println("   > 1. Update Room Rate");
            System.out.println("   > 2. Delete Room Rate");
            System.out.println("   > 3. Back to Dashboard");
            System.out.print("   > ");
            int input = sc.nextInt(); sc.nextLine(); System.out.println();
            
            switch (input) {
                case 1:
                    doUpdateRoomRate(sc, emId, emRole, rate.getRoomRateId());
                    break;
                case 2:
                    doDeleteRoomRate(sc, emId, emRole, rate.getRoomRateId());
                    break;
                case 3:
                    doDashboardFeatures(sc, emId, emRole);
                    break;
                default:
                    System.out.println("Invalid input.");
                    doDashboardFeatures(sc, emId, emRole);
                    break;
            }
        } catch (RoomRateQueryException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        
        
    }

    private void doUpdateRoomRate(Scanner sc, Long emId, String emRole, Long rateId) {
        System.out.println("==== Update Room Rate Interface ====");
        
        try {
            RoomRate rate = roomManagementSessionBean.getRoomRate(rateId);
            boolean done = false;
            String name = rate.getRoomRateName();
            Double amount = rate.getRatePerNight();
            Date startDate = rate.getStartDate();
            Date endDate = rate.getEndDate();
                        
            while (!done) {
                System.out.println("Select which detail of the rate you want to change:");
                System.out.println("> 1. Name");
                System.out.println("> 2. Amount");
                System.out.println("> 3. Validity Period");
                System.out.print("> ");
                int input = sc.nextInt(); sc.nextLine(); System.out.println();
                
                switch (input) {
                    case 1:
                        System.out.print("> Input new Name: ");
                        name = sc.nextLine();
                        break;
                    case 2:
                        System.out.print("> Input new Amount: ");
                        amount = sc.nextDouble();
                        sc.nextLine();
                        System.out.println();
                        break;
                    case 3:
                        if (rate.getRoomRateType().equals(RoomRateEnum.PeakRate.toString()) ||
                                rate.getRoomRateType().equals(RoomRateEnum.PromotionRate.toString())) {
                            
                            
                            DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd MM yyyy");
                            
                            System.out.print("> Input new Start Date [DD MM YYYY]: ");
                            String start = sc.nextLine();
                            startDate = Date.from(LocalDate.parse(start, dtFormat).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                            System.out.print("> Input new End Date [DD MM YYYY]: ");
                            String end = sc.nextLine(); System.out.println();
                            endDate = Date.from(LocalDate.parse(end, dtFormat).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                        } else {
                            System.out.println("Sorry. your Rate Type do not require a validity period.\n");
                        }   break;
                    default:
                        break;
                }
                
                System.out.println("Finalise changes?");
                System.out.println("> 1. Yes");
                System.out.println("> 2. No");
                System.out.print("> ");
                int answer = sc.nextInt(); sc.nextLine(); System.out.println();
                if (answer == 1) done = true;
            }
            
            roomManagementSessionBean.updateRoomRate(rateId, name, amount, startDate, endDate);
            System.out.println("You have successfully updated the Room Rate.\n");
            
            rate = roomManagementSessionBean.getRoomRate(rateId);
            System.out.println("Updated Room Rate details:");
            System.out.println("> Name: " + rate.getRoomRateName());
            System.out.println("> Type: " + rate.getRoomRateType());
            System.out.println("> Amount: " + rate.getRatePerNight());
            if (rate.getStartDate() != null) {
                System.out.println("> Validity Period: " + rate.getStartDate().toString() + 
                    " -> " + rate.getEndDate().toString());
            } else {
                System.out.println("> Validity Period: NULL");
            }
            System.out.println();
            
            doDashboardFeatures(sc, emId, emRole);
        } catch (FindRoomRateException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void doDeleteRoomRate(Scanner sc, Long emId, String emRole, Long roomRateId) {
        System.out.println("=== Delete Room Rate Interface ====");
        System.out.println("Confirm Deletion?");
        System.out.println("> 1. Yes");
        System.out.println("> 2. No");
        System.out.print("> ");
        try {
            int input = sc.nextInt(); sc.nextLine(); System.out.println();
            switch (input) {
                case 1:
                    roomManagementSessionBean.deleteRoomRate(roomRateId);
                    System.out.println("You have successfully deleted/disabled the Room Rate.\n");
                    break;

                case 2:
                    doDashboardFeatures(sc, emId, emRole);
                    break;
                default:
                    System.out.println("Invalid input. Try again.\n");
                    doDeleteRoomRate(sc, emId, emRole, roomRateId);
                    break;
            }
        } catch (FindRoomRateException | ReservationQueryException ex) {
                    System.out.println("Error: " + ex.getMessage());
        }
    }

    private void doViewAllRoomRates(Scanner sc, Long emId, String emRole) {
        System.out.println("==== View All Room Rates Interface ====");
        try {
            List<RoomRate> list = roomManagementSessionBean.getAllRoomRates();
            
            int count = 0;
            
            for (RoomRate rate : list) {
                System.out.println(":: Employee ID: " + rate.getRoomRateId());
                System.out.println("> Name: " + rate.getRoomRateName());
                System.out.println("> Type: " + rate.getRoomRateType());
                System.out.println("> Amount: " + rate.getRatePerNight());
                System.out.println("> Is Disabled: " + rate.getIsDisabled());
                if (rate.getStartDate() != null) {
                    System.out.println("> Validity Period: " + rate.getStartDate().toString() + 
                        " -> " + rate.getEndDate().toString());
                } else {
                    System.out.println("> Validity Period: NULL");
                }
                System.out.println();
                
                count++;
            }
            System.out.println("Total Number of Room Rates: " + count + "\n");
            doDashboardFeatures(sc, emId, emRole);
        } catch (RoomRateQueryException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void doCreateNewRoomType(Scanner sc, Long emId, String emRole) {
        try {
            System.out.println("==== Create New Room Type Interface ====");
        //String roomTypeName, String roomTypeDesc, Integer roomSize, Integer numOfBeds, Integer capacity, String amenities
        
            System.out.print("> Room Type Name [MIN 5 CHAR]: ");
            String typeName = sc.nextLine();
            System.out.print("> Room Type Description [MIN 5 CHAR]: ");
            String typeDesc = sc.nextLine();
            System.out.print("> Room Size [XXXX]: ");
            Integer roomSize = sc.nextInt(); sc.nextLine();
            System.out.print("> Number Of Beds [XX]: ");
            Integer numOfBeds = sc.nextInt(); sc.nextLine();
            System.out.print("> Room Capacity [XX]: ");
            Integer cap = sc.nextInt(); sc.nextLine();
            System.out.print("> Room Amenities [MIN 5 CHAR]: ");
            String amenities = sc.nextLine();
            System.out.println();
            RoomType newRoomType = new RoomType(typeName, typeDesc, roomSize, numOfBeds, cap, amenities);
            
            Long newRoomTypeId = roomManagementSessionBean.createNewRoomType(newRoomType);

            RoomType type = roomManagementSessionBean.getRoomType(newRoomTypeId);
            
            System.out.println("You have successfully created a new Room Type.");
            System.out.println("> Name: " + type.getRoomTypeName());
            System.out.println("> Description: " + type.getRoomTypeDesc());
            System.out.println("> Size: " + type.getRoomSize());
            System.out.println("> Number Of Beds: " + type.getNumOfBeds());
            System.out.println("> Capacity: " + type.getCapacity());
            System.out.println("> Amenities: " + type.getAmenities());
            System.out.println();
            
            doDashboardFeatures(sc, emId, emRole);
        } catch (FindRoomTypeException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid input. Try again.\n");
            doCreateNewRoomType(sc, emId, emRole);
        }
    }

    private void doViewRoomTypeDeatails(Scanner sc, Long emId, String emRole) {
        System.out.println("==== View Room Type Details Interface ====");
        try {
            
            System.out.print("> Room Type Name: ");
            String typeName = sc.nextLine();System.out.println();
            RoomType type = roomManagementSessionBean.getRoomType(typeName);
            
            System.out.println("Selected Room Type details:");
            System.out.println("> Name: " + type.getRoomTypeName());
            System.out.println("> Description: " + type.getRoomTypeDesc());
            System.out.println("> Size: " + type.getRoomSize());
            System.out.println("> Number Of Beds: " + type.getNumOfBeds());
            System.out.println("> Capacity: " + type.getCapacity());
            System.out.println("> Amenities: " + type.getAmenities());
            System.out.println("> Number of Rooms: " + type.getRooms().size());
            System.out.println("> Room Rates:");
            List<RoomRate> rates = roomManagementSessionBean.getRoomRates(type.getRoomTypeId());
            
            for (RoomRate rate : rates) {
                System.out.println("  > " + rate.getRoomRateName());
            }
            System.out.println();
            
            
            System.out.println("   Select an action:");
            System.out.println("   > 1. Update Room Type");
            System.out.println("   > 2. Delete Room Type");
            System.out.println("   > 3. Back to Dashboard");
            System.out.print("   > ");
            int input = sc.nextInt(); sc.nextLine(); System.out.println();
            
            switch (input) {
                case 1:
                    doUpdateRoomType(sc, emId, emRole, type.getRoomTypeId());
                    break;
                case 2:
                    doDeleteRoomType(sc, emId, emRole, type.getRoomTypeId());
                    break;
                case 3:
                    doDashboardFeatures(sc, emId, emRole);
                    break;
                default:
                    System.out.println("Invalid input.");
                    doDashboardFeatures(sc, emId, emRole);
                    break;
            }
        } catch (RoomTypeQueryException | FindRoomTypeException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void doViewAllRoomTypes(Scanner sc, Long emId, String emRole) {
        
    }

    private void doUpdateRoomType(Scanner sc, Long emId, String emRole, Long roomTypeId) {
        System.out.println("==== Update Room Type Interface ====");
        
        try {
            RoomType type = roomManagementSessionBean.getRoomType(roomTypeId);
            boolean done = false;
            String name = type.getRoomTypeName();
            String desc = type.getRoomTypeDesc();
            Integer size = type.getRoomSize();
            Integer beds = type.getNumOfBeds();
            Integer cap = type.getCapacity();
            String amenities = type.getAmenities();
                        
            while (!done) {
                System.out.println("Select which detail of the type you want to change:");
                System.out.println("> 1. Name");
                System.out.println("> 2. Description");
                System.out.println("> 3. Room Size");
                System.out.println("> 4. Number Of Beds");
                System.out.println("> 5. Room Capacity");
                System.out.println("> 6. Amenities");
                System.out.print("> ");
                int input = sc.nextInt(); sc.nextLine(); System.out.println();
                
                switch (input) {
                    case 1:
                        System.out.print("> Input new Name: ");
                        name = sc.nextLine();
                        break;
                    case 2:
                        System.out.print("> Input new Description: ");
                        desc = sc.nextLine();
                        break;
                    case 3:
                        System.out.print("> Input new Room Size: ");
                        size = sc.nextInt(); sc.nextLine();
                        break;
                    case 4:
                        System.out.print("> Input new Number Of Beds: ");
                        beds = sc.nextInt(); sc.nextLine();
                        break;
                    case 5:
                        System.out.print("> Input new Room Capacity: ");
                        cap = sc.nextInt(); sc.nextLine();
                        break;
                    case 6:
                        System.out.print("> Input new Amenities: ");
                        amenities = sc.nextLine();
                        break;
                    default:
                        break;
                }
                
                System.out.println("Finalise changes?");
                System.out.println("> 1. Yes");
                System.out.println("> 2. No");
                System.out.print("> ");
                int answer = sc.nextInt(); sc.nextLine(); System.out.println();
                if (answer == 1) done = true;
            }
            
            roomManagementSessionBean.updateRoomType(roomTypeId, name, desc, size, beds, cap, amenities);
            System.out.println("You have successfully updated the Room Type.\n");
            
            type = roomManagementSessionBean.getRoomType(roomTypeId);
            System.out.println("Updated Room Rate details:");
            System.out.println("> Name: " + type.getRoomTypeName());
            System.out.println("> Description: " + type.getRoomTypeDesc());
            System.out.println("> Size: " + type.getRoomSize());
            System.out.println("> Number Of Beds: " + type.getNumOfBeds());
            System.out.println("> Capacity: " + type.getCapacity());
            System.out.println("> Amenities: " + type.getAmenities());
            System.out.println();
            
            doDashboardFeatures(sc, emId, emRole);
        } catch (FindRoomTypeException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void doDeleteRoomType(Scanner sc, Long emId, String emRole, Long roomTypeId) {
        System.out.println("=== Delete Room Type Interface ====");
        System.out.println("Confirm Deletion?");
        System.out.println("> 1. Yes");
        System.out.println("> 2. No");
        System.out.print("> ");
        try { 
            int input = sc.nextInt(); sc.nextLine(); System.out.println();
            switch (input) {
                case 1:
                    roomManagementSessionBean.deleteRoomType(roomTypeId);
                    System.out.println("You have successfully deleted/disabled the Room Type.\n");
                    break;

                case 2:
                    doDashboardFeatures(sc, emId, emRole);
                    break;
                default:
                    System.out.println("Invalid input. Try again.\n");
                    doDeleteRoomType(sc, emId, emRole, roomTypeId);
                    break;
            }
        } catch (FindRoomRateException | ReservationQueryException | FindRoomTypeException ex) {
            System.out.println("Error: " + ex.getMessage());
        } catch (Exception e) {
            System.out.println("Unspecified Error " + e.getMessage());
        }
    }
}
